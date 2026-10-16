package com.example.model

enum class UserRole(val labelEn: String, val labelTa: String) {
    CITIZEN("Citizen / Disaster Victim", "பாதிக்கப்பட்ட பொதுமக்கள்"),
    RESCUE_TEAM("Rescue Team Member", "மீட்புக் குழு உறுப்பினர்"),
    MEDICAL_TEAM("Medical Team Member", "மருத்துவக் குழு உறுப்பினர்"),
    VOLUNTEER("Volunteer", "தன்னார்வலர்"),
    RELIEF_MANAGER("Relief Camp Manager", "நிவாரண முகாம் மேலாளர்"),
    DISTRICT_OFFICER("District Disaster Officer", "மாவட்ட பேரிடர் மேலாண்மை அலுவலர்"),
    SUPER_ADMIN("State Administrator", "மாநில தலைமை நிர்வாகி")
}

enum class PriorityLevel(val labelEn: String, val labelTa: String) {
    CRITICAL("Critical", "மிக அவசரம்"),
    HIGH("High", "அதி முக்கியம்"),
    MEDIUM("Medium", "நடுத்தரம்"),
    LOW("Low", "குறைந்த முன்னுரிமை"),
    PENDING("Pending Review", "சரிபார்க்கப்படுகிறது")
}

enum class RequestStatus(val labelEn: String, val labelTa: String) {
    SUBMITTED("Submitted", "சமர்ப்பிக்கப்பட்டது"),
    UNDER_REVIEW("Under Review", "ஆய்வில் உள்ளது"),
    ASSIGNED("Assigned", "குழு ஒதுக்கப்பட்டது"),
    TEAM_DISPATCHED("Team Dispatched", "குழு புறப்பட்டது"),
    ASSISTANCE_PROVIDED("Assistance Provided", "உதவி வழங்கப்பட்டது"),
    RESOLVED("Resolved", "தீர்க்கப்பட்டது"),
    CANCELLED("Cancelled", "ரத்து செய்யப்பட்டது"),
    DUPLICATE("Duplicate", "நகல் பதிவு")
}

enum class DisasterCategory(val labelEn: String, val labelTa: String) {
    FLOODING("Flooding & Waterlogging", "வெள்ளப்பெருக்கு & நீர் தேக்கம்"),
    CYCLONE("Cyclone & Gale Storms", "புயல் & பலத்த காற்று"),
    LANDSLIDE("Landslide & Mudflow", "நிலச்சரிவு"),
    FIRE("Fire Incident", "தீ விபத்து"),
    COLLAPSED_BUILDING("Building / Wall Collapse", "கட்டட இடிவு"),
    BLOCKED_ROAD("Blocked Road & Bridges", "துண்டிக்கப்பட்ட சாலை / பாலம்"),
    POWER_OUTAGE("Major Power Outage", "மின்சாரம் துண்டிப்பு"),
    MEDICAL_EMERGENCY("Critical Medical Emergency", "அவசர மருத்துவ உதவி"),
    MISSING_PERSON("Missing Person", "காணாமல் போனவர்"),
    OTHER("Other Hazard", "பிற ஆபத்துகள்")
}

enum class VerificationStatus(val labelEn: String, val labelTa: String) {
    PENDING("Pending Verification", "சரிபார்ப்பு நிலுவை"),
    VERIFIED("Verified by Officials", "அதிகாரப்பூர்வமாக உறுதிசெய்யப்பட்டது"),
    REJECTED("Rejected", "நிராகரிக்கப்பட்டது"),
    DUPLICATE("Duplicate Report", "நகல் பதிவு")
}

enum class CampStatus(val labelEn: String, val labelTa: String) {
    ACTIVE("Operational & Open", "செயல்பாட்டில் உள்ளது"),
    AT_CAPACITY("Full (At Capacity)", "முழு கொள்ளளவு"),
    STANDBY("Standby Readiness", "தயார் நிலையில் உள்ளது"),
    CLOSED("Closed", "மூடப்பட்டது")
}

enum class AlertSeverity(val labelEn: String, val labelTa: String) {
    EMERGENCY("Red Alert - Severe", "சிவப்பு எச்சரிக்கை"),
    WARNING("Orange Alert - Warning", "ஆரஞ்சு எச்சரிக்கை"),
    ADVISORY("Yellow Alert - Watch", "மஞ்சள் எச்சரிக்கை")
}

data class UserAccount(
    val id: String,
    val name: String,
    val phone: String,
    val role: UserRole,
    val district: String,
    val isVerified: Boolean = true,
    val organization: String = "",
    val accessibilityNeeds: String = "",
    val preferredLanguage: String = "en"
)

data class SOSRequest(
    val id: String,
    val citizenId: String,
    val citizenName: String,
    val citizenPhone: String,
    val category: DisasterCategory,
    val peopleCount: Int,
    val latitude: Double,
    val longitude: Double,
    val locationAddress: String,
    val landmark: String,
    val hasUrgentMedical: Boolean,
    val hasChildren: Boolean,
    val hasElderly: Boolean,
    val hasDisability: Boolean,
    val notes: String,
    val status: RequestStatus = RequestStatus.SUBMITTED,
    val assignedTeamId: String? = null,
    val assignedTeamName: String? = null,
    val aiPriority: PriorityLevel = PriorityLevel.HIGH,
    val aiSummary: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis(),
    val rejectionReason: String? = null
)

data class DisasterReport(
    val id: String,
    val reporterName: String,
    val reporterPhone: String,
    val category: DisasterCategory,
    val description: String,
    val district: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val affectedPeople: Int,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val priority: PriorityLevel = PriorityLevel.MEDIUM,
    val assignedOfficer: String? = null,
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ReliefCamp(
    val id: String,
    val name: String,
    val address: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val contactPhone: String,
    val managerName: String,
    val totalCapacity: Int,
    val currentOccupants: Int,
    val foodAvailable: Boolean,
    val waterAvailable: Boolean,
    val medicalAvailable: Boolean,
    val wheelchairAccessible: Boolean,
    val foodStockPacks: Int,
    val waterLitres: Int,
    val medicalKits: Int,
    val blankets: Int,
    val operatingStatus: CampStatus = CampStatus.ACTIVE,
    val lastInventoryUpdate: Long = System.currentTimeMillis(),
    val openingHours: String = "24x7 Emergency Centre",
    val verifiedByAdmin: Boolean = true
)

data class MedicalRequest(
    val id: String,
    val requesterName: String,
    val phone: String,
    val patientCount: Int,
    val symptoms: String,
    val location: String,
    val transportRequired: Boolean,
    val priority: PriorityLevel = PriorityLevel.HIGH,
    val status: RequestStatus = RequestStatus.SUBMITTED,
    val timestamp: Long = System.currentTimeMillis()
)

data class VolunteerTask(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val district: String,
    val location: String,
    val volunteersNeeded: Int,
    val assignedVolunteers: List<String> = emptyList(),
    val isCompleted: Boolean = false,
    val priority: PriorityLevel = PriorityLevel.MEDIUM
)

data class DisasterAlert(
    val id: String,
    val title: String,
    val titleTa: String,
    val severity: AlertSeverity,
    val description: String,
    val descriptionTa: String,
    val issuingAuthority: String,
    val affectedDistricts: List<String>,
    val publishedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class AuditLog(
    val id: String,
    val adminName: String,
    val action: String,
    val target: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SafetyGuide(
    val id: String,
    val titleEn: String,
    val titleTa: String,
    val category: DisasterCategory,
    val doListEn: List<String>,
    val doListTa: List<String>,
    val dontListEn: List<String>,
    val dontListTa: List<String>,
    val speechScriptEn: String,
    val speechScriptTa: String
)
