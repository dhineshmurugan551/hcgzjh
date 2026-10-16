package com.example.ai

import com.example.model.DisasterCategory
import com.example.model.PriorityLevel
import com.example.model.SOSRequest
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class AiAssessmentResult(
    val priority: PriorityLevel,
    val summary: String,
    val requiresUrgentReview: Boolean,
    val vulnerabilityFlags: List<String>,
    val recommendedTeamType: String,
    val isRuleBased: Boolean = true,
    val engineName: String = "SDR AI-Triage Engine v2.4 (Active)"
)

object EmergencyAiAssistant {

    /**
     * Triage an SOS request based on objective danger signals, water depth, medical urgency,
     * and vulnerable persons while strictly ensuring no downgrading.
     */
    fun analyzeSosRequest(req: SOSRequest): AiAssessmentResult {
        val flags = mutableListOf<String>()
        var score = 0

        // Trapped count impact
        if (req.peopleCount >= 10) {
            score += 4
            flags.add("Large group trapped (${req.peopleCount} persons)")
        } else if (req.peopleCount >= 4) {
            score += 2
            flags.add("Family / Group trapped (${req.peopleCount} persons)")
        }

        // Urgent medical check
        if (req.hasUrgentMedical) {
            score += 5
            flags.add("Urgent Medical Emergency declared")
        }

        // Special care requirements
        if (req.hasChildren) {
            score += 2
            flags.add("Infants / Children present")
        }
        if (req.hasElderly) {
            score += 2
            flags.add("Elderly persons present")
        }
        if (req.hasDisability) {
            score += 2
            flags.add("Mobility / Accessibility assistance required")
        }

        // Keyword analysis in user notes
        val notesLower = req.notes.lowercase()
        val criticalKeywords = listOf("chest pain", "bleeding", "roof", "water rising", "drowning", "unconscious", "fire", "smoke", "electric", "short circuit", "collapsed")
        val matchedCritical = criticalKeywords.filter { notesLower.contains(it) }
        if (matchedCritical.isNotEmpty()) {
            score += 4
            flags.add("Critical keywords detected: ${matchedCritical.joinToString(", ")}")
        }

        // Category baseline
        when (req.category) {
            DisasterCategory.FIRE -> score += 5
            DisasterCategory.COLLAPSED_BUILDING -> score += 5
            DisasterCategory.MEDICAL_EMERGENCY -> score += 4
            DisasterCategory.FLOODING -> score += 3
            DisasterCategory.LANDSLIDE -> score += 4
            DisasterCategory.CYCLONE -> score += 3
            DisasterCategory.BLOCKED_ROAD -> score += 1
            DisasterCategory.POWER_OUTAGE -> score += 1
            DisasterCategory.MISSING_PERSON -> score += 2
            DisasterCategory.OTHER -> score += 1
        }

        val priority = when {
            score >= 9 || req.hasUrgentMedical -> PriorityLevel.CRITICAL
            score >= 6 -> PriorityLevel.HIGH
            score >= 3 -> PriorityLevel.MEDIUM
            else -> PriorityLevel.LOW
        }

        val recommendedTeam = when {
            req.hasUrgentMedical || req.category == DisasterCategory.MEDICAL_EMERGENCY -> "Medical Response Unit (108 Ambulance + Paramedic)"
            req.category == DisasterCategory.FIRE -> "Fire & Rescue Services"
            req.category == DisasterCategory.FLOODING && (req.hasElderly || req.hasChildren) -> "NDRF / SDRF Inflatable Boat Team"
            req.category == DisasterCategory.COLLAPSED_BUILDING -> "Urban Search & Rescue (USAR) Specialist"
            else -> "Disaster Quick Response Team (QRT)"
        }

        val summary = buildString {
            append("Automated Priority: ${priority.labelEn}. ")
            if (flags.isNotEmpty()) {
                append("Key factors: ${flags.take(2).joinToString("; ")}. ")
            }
            append("Dispatch recommendation: $recommendedTeam. Note: Human responders make final dispatch decisions.")
        }

        return AiAssessmentResult(
            priority = priority,
            summary = summary,
            requiresUrgentReview = priority == PriorityLevel.CRITICAL,
            vulnerabilityFlags = flags,
            recommendedTeamType = recommendedTeam,
            isRuleBased = true
        )
    }

    /**
     * Categorizes free-form text reports into standard disaster categories.
     */
    fun categorizeReport(text: String): Pair<DisasterCategory, PriorityLevel> {
        val lower = text.lowercase()
        return when {
            lower.contains("fire") || lower.contains("smoke") || lower.contains("burning") || lower.contains("தீ") ->
                DisasterCategory.FIRE to PriorityLevel.CRITICAL

            lower.contains("collapsed") || lower.contains("building fall") || lower.contains("wall collapse") || lower.contains("debris") || lower.contains("இடிவு") ->
                DisasterCategory.COLLAPSED_BUILDING to PriorityLevel.CRITICAL

            lower.contains("flood") || lower.contains("submerged") || lower.contains("waterlevel") || lower.contains("வெள்ளம்") || lower.contains("நீர்") ->
                DisasterCategory.FLOODING to PriorityLevel.HIGH

            lower.contains("cyclone") || lower.contains("gale") || lower.contains("wind") || lower.contains("storm") || lower.contains("புயல்") ->
                DisasterCategory.CYCLONE to PriorityLevel.HIGH

            lower.contains("landslide") || lower.contains("mudslide") || lower.contains("நிலச்சரிவு") ->
                DisasterCategory.LANDSLIDE to PriorityLevel.HIGH

            lower.contains("road") || lower.contains("tree fell") || lower.contains("bridge") || lower.contains("block") || lower.contains("சாலை") ->
                DisasterCategory.BLOCKED_ROAD to PriorityLevel.MEDIUM

            lower.contains("current") || lower.contains("power") || lower.contains("transformer") || lower.contains("blackout") || lower.contains("மின்சாரம்") ->
                DisasterCategory.POWER_OUTAGE to PriorityLevel.MEDIUM

            lower.contains("missing") || lower.contains("lost") || lower.contains("காணவில்லை") ->
                DisasterCategory.MISSING_PERSON to PriorityLevel.HIGH

            lower.contains("heart") || lower.contains("stroke") || lower.contains("doctor") || lower.contains("injury") || lower.contains("மருத்துவம்") ->
                DisasterCategory.MEDICAL_EMERGENCY to PriorityLevel.CRITICAL

            else -> DisasterCategory.OTHER to PriorityLevel.MEDIUM
        }
    }

    /**
     * Check if a new SOS request may be a duplicate of an existing active request
     * (e.g. within 300 meters and within 30 minutes).
     */
    fun findPotentialDuplicates(newReq: SOSRequest, allRequests: List<SOSRequest>): List<SOSRequest> {
        return allRequests.filter { existing ->
            if (existing.id == newReq.id) return@filter false
            // Same citizen phone or very close GPS coordinates
            val samePhone = existing.citizenPhone.isNotEmpty() && existing.citizenPhone == newReq.citizenPhone
            val distanceKm = calculateDistanceKm(existing.latitude, existing.longitude, newReq.latitude, newReq.longitude)
            val timeDiffMinutes = kotlin.math.abs(existing.timestamp - newReq.timestamp) / (1000 * 60)

            (samePhone && timeDiffMinutes < 60) || (distanceKm < 0.25 && timeDiffMinutes < 30)
        }
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
