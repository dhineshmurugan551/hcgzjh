package com.example.data

import android.content.Context
import com.example.ai.EmergencyAiAssistant
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class DisasterRepository(context: Context) {
    private val database = DisasterDatabase.getDatabase(context)
    private val dao = database.disasterDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current Session State
    private val _currentUser = MutableStateFlow<UserAccount>(DefaultUsers.citizenUser)
    val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

    // High Contrast Accessibility Mode State
    private val _isHighContrast = MutableStateFlow(false)
    val isHighContrast: StateFlow<Boolean> = _isHighContrast.asStateFlow()

    // App Language State ("en" or "ta")
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Volunteer Tasks In-Memory + Persistent
    private val _volunteerTasks = MutableStateFlow<List<VolunteerTask>>(DefaultSeedData.initialVolunteerTasks)
    val volunteerTasks: StateFlow<List<VolunteerTask>> = _volunteerTasks.asStateFlow()

    // Medical Requests
    private val _medicalRequests = MutableStateFlow<List<MedicalRequest>>(DefaultSeedData.initialMedicalRequests)
    val medicalRequests: StateFlow<List<MedicalRequest>> = _medicalRequests.asStateFlow()

    // Flows from Room Database
    val allSosRequests: Flow<List<SOSRequest>> = dao.getAllSosRequests().map { list ->
        list.map { it.toModel() }
    }

    val allReports: Flow<List<DisasterReport>> = dao.getAllReports().map { list ->
        list.map { it.toModel() }
    }

    val reliefCamps: Flow<List<ReliefCamp>> = dao.getAllReliefCamps().map { list ->
        list.map { it.toModel() }
    }

    val activeAlerts: Flow<List<DisasterAlert>> = dao.getActiveAlerts().map { list ->
        list.map { it.toModel() }
    }

    val auditLogs: Flow<List<AuditLog>> = dao.getAuditLogs().map { list ->
        list.map { it.toModel() }
    }

    init {
        // Pre-populate seed data if database is empty
        scope.launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        // Users
        DefaultUsers.allPresetUsers.forEach { user ->
            dao.insertUser(UserEntity.from(user))
        }

        // Check if camps exist
        val existingCamps = dao.getAllReliefCamps().first()
        if (existingCamps.isEmpty()) {
            dao.insertAllReliefCamps(DefaultSeedData.initialReliefCamps.map { ReliefCampEntity.from(it) })
            dao.insertAllAlerts(DefaultSeedData.initialAlerts.map { AlertEntity.from(it) })
            DefaultSeedData.initialSosRequests.forEach { dao.insertSosRequest(SosEntity.from(it)) }
            DefaultSeedData.initialReports.forEach { dao.insertReport(ReportEntity.from(it)) }
            dao.insertAuditLog(
                AuditLogEntity(
                    id = "LOG-INIT",
                    adminName = "System",
                    action = "SYSTEM_INITIALIZED",
                    target = "Database",
                    details = "Disaster rescue database initialized with emergency responders and shelters.",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun toggleHighContrast() {
        _isHighContrast.value = !_isHighContrast.value
    }

    fun switchUser(user: UserAccount) {
        _currentUser.value = user
    }

    suspend fun registerUser(name: String, phone: String, role: UserRole, district: String): UserAccount {
        val isOfficial = role != UserRole.CITIZEN && role != UserRole.VOLUNTEER
        val newUser = UserAccount(
            id = "USR-${UUID.randomUUID().toString().take(6).uppercase()}",
            name = name,
            phone = phone,
            role = role,
            district = district,
            isVerified = !isOfficial // Officials need admin approval per prompt requirements
        )
        dao.insertUser(UserEntity.from(newUser))
        _currentUser.value = newUser
        return newUser
    }

    suspend fun loginWithPhone(phone: String): UserAccount? {
        val userEntity = dao.getUserByPhone(phone)
        return userEntity?.toUserAccount()?.also {
            _currentUser.value = it
        }
    }

    // SOS Operations
    suspend fun submitSosRequest(
        citizenName: String,
        citizenPhone: String,
        category: DisasterCategory,
        peopleCount: Int,
        latitude: Double,
        longitude: Double,
        locationAddress: String,
        landmark: String,
        hasUrgentMedical: Boolean,
        hasChildren: Boolean,
        hasElderly: Boolean,
        hasDisability: Boolean,
        notes: String
    ): Pair<SOSRequest, List<SOSRequest>> {
        val newId = "SOS-${(1000..9999).random()}"
        val initialRequest = SOSRequest(
            id = newId,
            citizenId = _currentUser.value.id,
            citizenName = citizenName.ifBlank { _currentUser.value.name },
            citizenPhone = citizenPhone.ifBlank { _currentUser.value.phone },
            category = category,
            peopleCount = peopleCount,
            latitude = latitude,
            longitude = longitude,
            locationAddress = locationAddress,
            landmark = landmark,
            hasUrgentMedical = hasUrgentMedical,
            hasChildren = hasChildren,
            hasElderly = hasElderly,
            hasDisability = hasDisability,
            notes = notes
        )

        // Run AI Triage Assessment
        val aiAssessment = EmergencyAiAssistant.analyzeSosRequest(initialRequest)
        val finalRequest = initialRequest.copy(
            aiPriority = aiAssessment.priority,
            aiSummary = aiAssessment.summary
        )

        // Duplicate Detection check against existing requests
        val existingRequests = dao.getAllSosRequests().first().map { it.toModel() }
        val duplicates = EmergencyAiAssistant.findPotentialDuplicates(finalRequest, existingRequests)

        dao.insertSosRequest(SosEntity.from(finalRequest))

        logAction(
            adminName = _currentUser.value.name,
            action = "SOS_SUBMITTED",
            target = finalRequest.id,
            details = "Emergency SOS created at $locationAddress for $peopleCount people. Priority: ${aiAssessment.priority.name}"
        )

        return Pair(finalRequest, duplicates)
    }

    suspend fun updateSosStatus(
        requestId: String,
        newStatus: RequestStatus,
        responderName: String? = null,
        rejectionReason: String? = null
    ) {
        val requests = dao.getAllSosRequests().first().map { it.toModel() }
        val target = requests.find { it.id == requestId } ?: return

        val updated = target.copy(
            status = newStatus,
            assignedTeamName = responderName ?: target.assignedTeamName,
            rejectionReason = rejectionReason ?: target.rejectionReason,
            updatedTimestamp = System.currentTimeMillis()
        )
        dao.updateSosRequest(SosEntity.from(updated))

        logAction(
            adminName = _currentUser.value.name,
            action = "SOS_STATUS_UPDATED",
            target = requestId,
            details = "Status changed to ${newStatus.labelEn} by ${_currentUser.value.name}. Reason: ${rejectionReason ?: "N/A"}"
        )
    }

    // Disaster Reports Operations
    suspend fun submitDisasterReport(
        category: DisasterCategory,
        description: String,
        district: String,
        location: String,
        latitude: Double,
        longitude: Double,
        affectedPeople: Int
    ): DisasterReport {
        val reportId = "RPT-${(100..999).random()}"
        val (detectedCategory, priority) = EmergencyAiAssistant.categorizeReport(description)
        val finalCategory = if (category == DisasterCategory.OTHER) detectedCategory else category

        val report = DisasterReport(
            id = reportId,
            reporterName = _currentUser.value.name,
            reporterPhone = _currentUser.value.phone,
            category = finalCategory,
            description = description,
            district = district,
            location = location,
            latitude = latitude,
            longitude = longitude,
            affectedPeople = affectedPeople,
            verificationStatus = VerificationStatus.PENDING,
            priority = priority
        )
        dao.insertReport(ReportEntity.from(report))

        logAction(
            adminName = _currentUser.value.name,
            action = "DISASTER_REPORT_CREATED",
            target = report.id,
            details = "Reported ${finalCategory.labelEn} at $location. Priority: ${priority.labelEn}"
        )

        return report
    }

    suspend fun verifyReport(
        reportId: String,
        status: VerificationStatus,
        reason: String? = null,
        assignedOfficer: String? = null
    ) {
        val reports = dao.getAllReports().first().map { it.toModel() }
        val target = reports.find { it.id == reportId } ?: return
        val updated = target.copy(
            verificationStatus = status,
            rejectionReason = reason,
            assignedOfficer = assignedOfficer ?: target.assignedOfficer,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateReport(ReportEntity.from(updated))

        logAction(
            adminName = _currentUser.value.name,
            action = "REPORT_VERIFIED",
            target = reportId,
            details = "Verification status set to ${status.labelEn}. Officer: ${assignedOfficer ?: _currentUser.value.name}"
        )
    }

    // Relief Camp Operations
    suspend fun updateCampInventory(
        campId: String,
        occupants: Int,
        foodPacks: Int,
        waterL: Int,
        medKits: Int,
        blankets: Int,
        status: CampStatus
    ) {
        val camps = dao.getAllReliefCamps().first().map { it.toModel() }
        val target = camps.find { it.id == campId } ?: return
        val updated = target.copy(
            currentOccupants = occupants,
            foodStockPacks = foodPacks,
            waterLitres = waterL,
            medicalKits = medKits,
            blankets = blankets,
            operatingStatus = status,
            foodAvailable = foodPacks > 50,
            waterAvailable = waterL > 100,
            medicalAvailable = medKits > 10,
            lastInventoryUpdate = System.currentTimeMillis()
        )
        dao.updateReliefCamp(ReliefCampEntity.from(updated))

        logAction(
            adminName = _currentUser.value.name,
            action = "INVENTORY_UPDATED",
            target = target.name,
            details = "Updated occupants: $occupants, Food: $foodPacks, Water: ${waterL}L, Status: ${status.name}"
        )
    }

    // Volunteer Task Operations
    fun toggleVolunteerTaskAssignment(taskId: String, volunteerName: String) {
        val current = _volunteerTasks.value.toMutableList()
        val index = current.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = current[index]
            val volunteers = task.assignedVolunteers.toMutableList()
            if (volunteers.contains(volunteerName)) {
                volunteers.remove(volunteerName)
            } else {
                volunteers.add(volunteerName)
            }
            val updated = task.copy(assignedVolunteers = volunteers)
            current[index] = updated
            _volunteerTasks.value = current
        }
    }

    fun completeVolunteerTask(taskId: String) {
        val current = _volunteerTasks.value.toMutableList()
        val index = current.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val updated = current[index].copy(isCompleted = true)
            current[index] = updated
            _volunteerTasks.value = current
        }
    }

    // Medical Assistance Requests
    fun submitMedicalRequest(req: MedicalRequest) {
        val current = _medicalRequests.value.toMutableList()
        current.add(0, req)
        _medicalRequests.value = current
    }

    // Public Alerts Operations
    suspend fun publishAlert(
        title: String,
        titleTa: String,
        severity: AlertSeverity,
        description: String,
        descriptionTa: String,
        issuingAuthority: String,
        districts: List<String>
    ) {
        val alert = DisasterAlert(
            id = "ALT-${System.currentTimeMillis()}",
            title = title,
            titleTa = titleTa,
            severity = severity,
            description = description,
            descriptionTa = descriptionTa,
            issuingAuthority = issuingAuthority,
            affectedDistricts = districts
        )
        dao.insertAlert(AlertEntity.from(alert))

        logAction(
            adminName = _currentUser.value.name,
            action = "DISASTER_ALERT_ISSUED",
            target = title,
            details = "Severity: ${severity.name}, Districts: ${districts.joinToString(", ")}"
        )
    }

    private suspend fun logAction(adminName: String, action: String, target: String, details: String) {
        val log = AuditLogEntity(
            id = "LOG-${UUID.randomUUID().toString().take(8)}",
            adminName = adminName,
            action = action,
            target = target,
            details = details,
            timestamp = System.currentTimeMillis()
        )
        dao.insertAuditLog(log)
    }
}
