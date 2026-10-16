package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val roleName: String,
    val district: String,
    val isVerified: Boolean = true,
    val organization: String = "",
    val accessibilityNeeds: String = "",
    val preferredLanguage: String = "en"
) {
    fun toUserAccount(): UserAccount {
        val role = try { UserRole.valueOf(roleName) } catch (_: Exception) { UserRole.CITIZEN }
        return UserAccount(
            id = id,
            name = name,
            phone = phone,
            role = role,
            district = district,
            isVerified = isVerified,
            organization = organization,
            accessibilityNeeds = accessibilityNeeds,
            preferredLanguage = preferredLanguage
        )
    }

    companion object {
        fun from(user: UserAccount): UserEntity = UserEntity(
            id = user.id,
            name = user.name,
            phone = user.phone,
            roleName = user.role.name,
            district = user.district,
            isVerified = user.isVerified,
            organization = user.organization,
            accessibilityNeeds = user.accessibilityNeeds,
            preferredLanguage = user.preferredLanguage
        )
    }
}

@Entity(tableName = "sos_requests")
data class SosEntity(
    @PrimaryKey val id: String,
    val citizenId: String,
    val citizenName: String,
    val citizenPhone: String,
    val categoryName: String,
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
    val statusName: String,
    val assignedTeamId: String?,
    val assignedTeamName: String?,
    val aiPriorityName: String,
    val aiSummary: String,
    val timestamp: Long,
    val updatedTimestamp: Long,
    val rejectionReason: String?
) {
    fun toModel(): SOSRequest {
        val cat = try { DisasterCategory.valueOf(categoryName) } catch (_: Exception) { DisasterCategory.OTHER }
        val stat = try { RequestStatus.valueOf(statusName) } catch (_: Exception) { RequestStatus.SUBMITTED }
        val prio = try { PriorityLevel.valueOf(aiPriorityName) } catch (_: Exception) { PriorityLevel.HIGH }
        return SOSRequest(
            id = id,
            citizenId = citizenId,
            citizenName = citizenName,
            citizenPhone = citizenPhone,
            category = cat,
            peopleCount = peopleCount,
            latitude = latitude,
            longitude = longitude,
            locationAddress = locationAddress,
            landmark = landmark,
            hasUrgentMedical = hasUrgentMedical,
            hasChildren = hasChildren,
            hasElderly = hasElderly,
            hasDisability = hasDisability,
            notes = notes,
            status = stat,
            assignedTeamId = assignedTeamId,
            assignedTeamName = assignedTeamName,
            aiPriority = prio,
            aiSummary = aiSummary,
            timestamp = timestamp,
            updatedTimestamp = updatedTimestamp,
            rejectionReason = rejectionReason
        )
    }

    companion object {
        fun from(model: SOSRequest): SosEntity = SosEntity(
            id = model.id,
            citizenId = model.citizenId,
            citizenName = model.citizenName,
            citizenPhone = model.citizenPhone,
            categoryName = model.category.name,
            peopleCount = model.peopleCount,
            latitude = model.latitude,
            longitude = model.longitude,
            locationAddress = model.locationAddress,
            landmark = model.landmark,
            hasUrgentMedical = model.hasUrgentMedical,
            hasChildren = model.hasChildren,
            hasElderly = model.hasElderly,
            hasDisability = model.hasDisability,
            notes = model.notes,
            statusName = model.status.name,
            assignedTeamId = model.assignedTeamId,
            assignedTeamName = model.assignedTeamName,
            aiPriorityName = model.aiPriority.name,
            aiSummary = model.aiSummary,
            timestamp = model.timestamp,
            updatedTimestamp = model.updatedTimestamp,
            rejectionReason = model.rejectionReason
        )
    }
}

@Entity(tableName = "disaster_reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterName: String,
    val reporterPhone: String,
    val categoryName: String,
    val description: String,
    val district: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val affectedPeople: Int,
    val verificationStatusName: String,
    val priorityName: String,
    val assignedOfficer: String?,
    val rejectionReason: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toModel(): DisasterReport {
        val cat = try { DisasterCategory.valueOf(categoryName) } catch (_: Exception) { DisasterCategory.OTHER }
        val vStat = try { VerificationStatus.valueOf(verificationStatusName) } catch (_: Exception) { VerificationStatus.PENDING }
        val prio = try { PriorityLevel.valueOf(priorityName) } catch (_: Exception) { PriorityLevel.MEDIUM }
        return DisasterReport(
            id = id,
            reporterName = reporterName,
            reporterPhone = reporterPhone,
            category = cat,
            description = description,
            district = district,
            location = location,
            latitude = latitude,
            longitude = longitude,
            affectedPeople = affectedPeople,
            verificationStatus = vStat,
            priority = prio,
            assignedOfficer = assignedOfficer,
            rejectionReason = rejectionReason,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun from(m: DisasterReport): ReportEntity = ReportEntity(
            id = m.id,
            reporterName = m.reporterName,
            reporterPhone = m.reporterPhone,
            categoryName = m.category.name,
            description = m.description,
            district = m.district,
            location = m.location,
            latitude = m.latitude,
            longitude = m.longitude,
            affectedPeople = m.affectedPeople,
            verificationStatusName = m.verificationStatus.name,
            priorityName = m.priority.name,
            assignedOfficer = m.assignedOfficer,
            rejectionReason = m.rejectionReason,
            createdAt = m.createdAt,
            updatedAt = m.updatedAt
        )
    }
}

@Entity(tableName = "relief_camps")
data class ReliefCampEntity(
    @PrimaryKey val id: String,
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
    val operatingStatusName: String,
    val lastInventoryUpdate: Long,
    val openingHours: String,
    val verifiedByAdmin: Boolean
) {
    fun toModel(): ReliefCamp {
        val status = try { CampStatus.valueOf(operatingStatusName) } catch (_: Exception) { CampStatus.ACTIVE }
        return ReliefCamp(
            id = id,
            name = name,
            address = address,
            district = district,
            latitude = latitude,
            longitude = longitude,
            contactPhone = contactPhone,
            managerName = managerName,
            totalCapacity = totalCapacity,
            currentOccupants = currentOccupants,
            foodAvailable = foodAvailable,
            waterAvailable = waterAvailable,
            medicalAvailable = medicalAvailable,
            wheelchairAccessible = wheelchairAccessible,
            foodStockPacks = foodStockPacks,
            waterLitres = waterLitres,
            medicalKits = medicalKits,
            blankets = blankets,
            operatingStatus = status,
            lastInventoryUpdate = lastInventoryUpdate,
            openingHours = openingHours,
            verifiedByAdmin = verifiedByAdmin
        )
    }

    companion object {
        fun from(m: ReliefCamp): ReliefCampEntity = ReliefCampEntity(
            id = m.id,
            name = m.name,
            address = m.address,
            district = m.district,
            latitude = m.latitude,
            longitude = m.longitude,
            contactPhone = m.contactPhone,
            managerName = m.managerName,
            totalCapacity = m.totalCapacity,
            currentOccupants = m.currentOccupants,
            foodAvailable = m.foodAvailable,
            waterAvailable = m.waterAvailable,
            medicalAvailable = m.medicalAvailable,
            wheelchairAccessible = m.wheelchairAccessible,
            foodStockPacks = m.foodStockPacks,
            waterLitres = m.waterLitres,
            medicalKits = m.medicalKits,
            blankets = m.blankets,
            operatingStatusName = m.operatingStatus.name,
            lastInventoryUpdate = m.lastInventoryUpdate,
            openingHours = m.openingHours,
            verifiedByAdmin = m.verifiedByAdmin
        )
    }
}

@Entity(tableName = "disaster_alerts")
data class AlertEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleTa: String,
    val severityName: String,
    val description: String,
    val descriptionTa: String,
    val issuingAuthority: String,
    val affectedDistrictsString: String,
    val publishedAt: Long,
    val isActive: Boolean
) {
    fun toModel(): DisasterAlert {
        val sev = try { AlertSeverity.valueOf(severityName) } catch (_: Exception) { AlertSeverity.WARNING }
        return DisasterAlert(
            id = id,
            title = title,
            titleTa = titleTa,
            severity = sev,
            description = description,
            descriptionTa = descriptionTa,
            issuingAuthority = issuingAuthority,
            affectedDistricts = affectedDistrictsString.split(",").filter { it.isNotBlank() },
            publishedAt = publishedAt,
            isActive = isActive
        )
    }

    companion object {
        fun from(m: DisasterAlert): AlertEntity = AlertEntity(
            id = m.id,
            title = m.title,
            titleTa = m.titleTa,
            severityName = m.severity.name,
            description = m.description,
            descriptionTa = m.descriptionTa,
            issuingAuthority = m.issuingAuthority,
            affectedDistrictsString = m.affectedDistricts.joinToString(","),
            publishedAt = m.publishedAt,
            isActive = m.isActive
        )
    }
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val adminName: String,
    val action: String,
    val target: String,
    val details: String,
    val timestamp: Long
) {
    fun toModel(): AuditLog = AuditLog(id, adminName, action, target, details, timestamp)

    companion object {
        fun from(m: AuditLog): AuditLogEntity = AuditLogEntity(
            m.id, m.adminName, m.action, m.target, m.details, m.timestamp
        )
    }
}
