package com.example

import com.example.ai.EmergencyAiAssistant
import com.example.model.DisasterCategory
import com.example.model.PriorityLevel
import com.example.model.SOSRequest
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testAiTriageCriticalForUrgentMedical() {
        val sos = SOSRequest(
            id = "SOS-TEST-01",
            citizenId = "USR-1",
            citizenName = "John",
            citizenPhone = "9998887770",
            category = DisasterCategory.FLOODING,
            peopleCount = 4,
            latitude = 13.0,
            longitude = 80.2,
            locationAddress = "Test Address",
            landmark = "Near School",
            hasUrgentMedical = true,
            hasChildren = true,
            hasElderly = true,
            hasDisability = false,
            notes = "Water level rising rapidly"
        )
        val result = EmergencyAiAssistant.analyzeSosRequest(sos)
        assertEquals(PriorityLevel.CRITICAL, result.priority)
        assertTrue(result.requiresUrgentReview)
        assertTrue(result.vulnerabilityFlags.isNotEmpty())
    }

    @Test
    fun testAiCategorizeFireFromText() {
        val (cat, priority) = EmergencyAiAssistant.categorizeReport("Heavy black smoke and fire breaking out on 3rd floor")
        assertEquals(DisasterCategory.FIRE, cat)
        assertEquals(PriorityLevel.CRITICAL, priority)
    }

    @Test
    fun testDuplicateDetection() {
        val req1 = SOSRequest(
            id = "SOS-001",
            citizenId = "U1",
            citizenName = "Citizen 1",
            citizenPhone = "9876543210",
            category = DisasterCategory.FLOODING,
            peopleCount = 2,
            latitude = 12.9815,
            longitude = 80.2180,
            locationAddress = "Velachery",
            landmark = "Phoenix",
            hasUrgentMedical = false,
            hasChildren = false,
            hasElderly = false,
            hasDisability = false,
            notes = "Trapped"
        )

        // Close request with same phone
        val req2 = req1.copy(id = "SOS-002", peopleCount = 3)
        val duplicates = EmergencyAiAssistant.findPotentialDuplicates(req2, listOf(req1))
        assertEquals(1, duplicates.size)
        assertEquals("SOS-001", duplicates[0].id)
    }
}
