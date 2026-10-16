package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.tts.DisasterVoiceAssistant
import com.example.ui.components.InteractiveDisasterMap
import com.example.ui.components.MapPinItem
import com.example.ui.components.PinType
import com.example.ui.theme.*

@Composable
fun LiveDisasterMapScreen(
    sosRequests: List<SOSRequest>,
    reports: List<DisasterReport>,
    camps: List<ReliefCamp>,
    lang: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedPin by remember { mutableStateOf<MapPinItem?>(null) }

    // Map pins generation from real database entities
    val allPins = remember(sosRequests, reports, camps) {
        val list = mutableListOf<MapPinItem>()

        sosRequests.forEach { sos ->
            list.add(
                MapPinItem(
                    id = sos.id,
                    title = "SOS: ${sos.category.labelEn} (${sos.peopleCount} trapped)",
                    category = sos.category.name,
                    latitude = sos.latitude,
                    longitude = sos.longitude,
                    type = PinType.SOS_INCIDENT,
                    details = "${sos.locationAddress} • Landmark: ${sos.landmark}. Status: ${sos.status.labelEn}",
                    isVerified = true,
                    priority = sos.aiPriority
                )
            )
        }

        reports.forEach { rpt ->
            list.add(
                MapPinItem(
                    id = rpt.id,
                    title = "Hazard: ${rpt.category.labelEn}",
                    category = rpt.category.name,
                    latitude = rpt.latitude,
                    longitude = rpt.longitude,
                    type = if (rpt.category == DisasterCategory.BLOCKED_ROAD) PinType.BLOCKED_ROAD else PinType.DISASTER_REPORT,
                    details = "${rpt.location} • ${rpt.description}",
                    isVerified = rpt.verificationStatus == VerificationStatus.VERIFIED,
                    priority = rpt.priority
                )
            )
        }

        camps.forEach { camp ->
            list.add(
                MapPinItem(
                    id = camp.id,
                    title = "Camp: ${camp.name}",
                    category = "RELIEF_CAMP",
                    latitude = camp.latitude,
                    longitude = camp.longitude,
                    type = PinType.RELIEF_CAMP,
                    details = "Capacity: ${camp.currentOccupants}/${camp.totalCapacity} beds. Food: ${if (camp.foodAvailable) "Available" else "Low"}. Phone: ${camp.contactPhone}",
                    isVerified = true,
                    priority = PriorityLevel.LOW
                )
            )
        }

        list
    }

    val filteredPins = allPins.filter { pin ->
        when (selectedFilter) {
            "SOS" -> pin.type == PinType.SOS_INCIDENT
            "CAMPS" -> pin.type == PinType.RELIEF_CAMP
            "HAZARDS" -> pin.type == PinType.DISASTER_REPORT || pin.type == PinType.BLOCKED_ROAD
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (lang == "ta") "நேரலை பேரிடர் வரைபடம்" else "Live Disaster Response Map",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = RescueBlue
            )
        }

        // Filter chips row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${allPins.size})") }
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "SOS",
                    onClick = { selectedFilter = "SOS" },
                    label = { Text("SOS (${sosRequests.size})") },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(16.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "CAMPS",
                    onClick = { selectedFilter = "CAMPS" },
                    label = { Text("Camps (${camps.size})") },
                    leadingIcon = { Icon(Icons.Default.NightShelter, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(16.dp)) }
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "HAZARDS",
                    onClick = { selectedFilter = "HAZARDS" },
                    label = { Text("Hazards (${reports.size})") },
                    leadingIcon = { Icon(Icons.Default.ReportProblem, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(16.dp)) }
                )
            }
        }

        // Interactive Canvas Map
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            InteractiveDisasterMap(
                pins = filteredPins,
                selectedPin = selectedPin,
                onPinSelected = { selectedPin = it },
                lang = lang
            )
        }
    }
}

@Composable
fun SafetyGuidelinesScreen(
    guides: List<SafetyGuide>,
    voiceAssistant: DisasterVoiceAssistant,
    lang: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGuideId by remember { mutableStateOf(guides.firstOrNull()?.id ?: "") }
    val currentGuide = guides.find { it.id == selectedGuideId } ?: guides.firstOrNull()
    var isSpeaking by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        voiceAssistant.stop()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = if (lang == "ta") "பேரிடர் பாதுகாப்பு வழிமுறைகள்" else "Disaster Safety Instructions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Guide Selector Tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(guides) { guide ->
                    FilterChip(
                        selected = guide.id == selectedGuideId,
                        onClick = {
                            selectedGuideId = guide.id
                            voiceAssistant.stop()
                            isSpeaking = false
                        },
                        label = {
                            Text(
                                text = if (lang == "ta") guide.titleTa else guide.titleEn,
                                fontWeight = if (guide.id == selectedGuideId) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        if (currentGuide != null) {
            // TTS Voice Narration Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("tts_voice_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = "TTS Voice", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == "ta") "குரல் வழி காட்டி (Tamil Voice)" else "Voice Assistant Audio (English / Tamil)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (lang == "ta") "அவசர வழிமுறைகளை தமிழில் கேட்கவும்" else "Listen to emergency audio instructions aloud",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        FilledIconButton(
                            onClick = {
                                if (isSpeaking) {
                                    voiceAssistant.stop()
                                    isSpeaking = false
                                } else {
                                    val script = if (lang == "ta") currentGuide.speechScriptTa else currentGuide.speechScriptEn
                                    voiceAssistant.speak(script, lang)
                                    isSpeaking = true
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Play Audio",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Dos section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SafetyGreenLight.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafetyGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == "ta") "செய்ய வேண்டியவை (DOs)" else "Actions to Take (DOs)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SafetyGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val dos = if (lang == "ta") currentGuide.doListTa else currentGuide.doListEn
                        dos.forEach { itemText ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("✓ ", color = SafetyGreen, fontWeight = FontWeight.Bold)
                                Text(itemText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Don'ts section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cancel, contentDescription = null, tint = EmergencyRedDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == "ta") "செய்யக் கூடாதவை (DON'Ts)" else "Things to Avoid (DON'Ts)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmergencyRedDark
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val donts = if (lang == "ta") currentGuide.dontListTa else currentGuide.dontListEn
                        donts.forEach { itemText ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("✗ ", color = EmergencyRedDark, fontWeight = FontWeight.Bold)
                                Text(itemText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
