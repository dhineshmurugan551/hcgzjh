package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun RescueTeamScreen(
    user: UserAccount,
    sosRequests: List<SOSRequest>,
    lang: String,
    onUpdateStatus: (requestId: String, newStatus: RequestStatus, note: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val assignedIncidents = sosRequests.filter {
        it.status != RequestStatus.RESOLVED && it.status != RequestStatus.CANCELLED
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RescueBlue),
                modifier = Modifier.fillMaxWidth().testTag("rescue_responder_header")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == "ta") "மீட்புக் குழு செயல்பாட்டு பலகை" else "Rescue Team Operations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
                            Text(
                                text = "ACTIVE UNIT",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = user.organization.ifBlank { "National Disaster Response Force (NDRF)" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            Text(
                text = if (lang == "ta") "ஒதுக்கப்பட்ட அவசர சம்பவங்கள் (${assignedIncidents.size})" else "Assigned Emergency Incidents (${assignedIncidents.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (assignedIncidents.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == "ta") "நிலுவையில் உள்ள சம்பவங்கள் இல்லை" else "No Active Incidents Assigned",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            items(assignedIncidents) { req ->
                RescueIncidentCard(req = req, lang = lang, onUpdateStatus = onUpdateStatus)
            }
        }
    }
}

@Composable
fun RescueIncidentCard(
    req: SOSRequest,
    lang: String,
    onUpdateStatus: (String, RequestStatus, String?) -> Unit
) {
    var showDeclineDialog by remember { mutableStateOf(false) }
    var declineReason by remember { mutableStateOf("") }

    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text(if (lang == "ta") "காரணத்தைக் குறிப்பிடவும்" else "Decline Assignment Reason") },
            text = {
                OutlinedTextField(
                    value = declineReason,
                    onValueChange = { declineReason = it },
                    label = { Text("e.g. Inaccessible terrain, boat needed, deployed elsewhere") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStatus(req.id, RequestStatus.UNDER_REVIEW, declineReason)
                        showDeclineDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text(if (lang == "ta") "நிராகரி" else "Decline")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${req.id} • ${if (lang == "ta") req.category.labelTa else req.category.labelEn}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                PriorityBadge(priority = req.aiPriority, lang = lang)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${req.peopleCount} ${if (lang == "ta") "நபர்கள் சிக்கியுள்ளனர்" else "Persons Stranded"} • ${req.citizenName} (${req.citizenPhone})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Location: ${req.locationAddress}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Landmark: ${req.landmark}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Special flags
            val flags = mutableListOf<String>()
            if (req.hasUrgentMedical) flags.add("Urgent Medical")
            if (req.hasChildren) flags.add("Children")
            if (req.hasElderly) flags.add("Elderly")
            if (req.hasDisability) flags.add("Disability")

            if (flags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    flags.forEach { f ->
                        Surface(shape = RoundedCornerShape(6.dp), color = WarningOrangeLight) {
                            Text(f, color = WarningOrange, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }

            if (req.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Victim notes: \"${req.notes}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = req.status, lang = lang)

                // Action state machine
                when (req.status) {
                    RequestStatus.SUBMITTED, RequestStatus.UNDER_REVIEW -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { showDeclineDialog = true }) {
                                Text("Decline", color = EmergencyRed)
                            }
                            Button(
                                onClick = { onUpdateStatus(req.id, RequestStatus.TEAM_DISPATCHED, null) },
                                colors = ButtonDefaults.buttonColors(containerColor = RescueBlue)
                            ) {
                                Text("Accept & Dispatch")
                            }
                        }
                    }
                    RequestStatus.ASSIGNED -> {
                        Button(
                            onClick = { onUpdateStatus(req.id, RequestStatus.TEAM_DISPATCHED, null) },
                            colors = ButtonDefaults.buttonColors(containerColor = RescueBlue)
                        ) {
                            Text("Dispatch Team")
                        }
                    }
                    RequestStatus.TEAM_DISPATCHED -> {
                        Button(
                            onClick = { onUpdateStatus(req.id, RequestStatus.ASSISTANCE_PROVIDED, null) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)
                        ) {
                            Text("Arrived & Providing Aid")
                        }
                    }
                    RequestStatus.ASSISTANCE_PROVIDED -> {
                        Button(
                            onClick = { onUpdateStatus(req.id, RequestStatus.RESOLVED, "Rescue successfully concluded") },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen)
                        ) {
                            Text("Mark Resolved")
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun ReliefManagerScreen(
    user: UserAccount,
    camps: List<ReliefCamp>,
    lang: String,
    onUpdateInventory: (campId: String, occupants: Int, food: Int, water: Int, meds: Int, blankets: Int, status: CampStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val myCamp = camps.firstOrNull() ?: return
    var occupants by remember(myCamp) { mutableIntStateOf(myCamp.currentOccupants) }
    var foodPacks by remember(myCamp) { mutableIntStateOf(myCamp.foodStockPacks) }
    var waterLitres by remember(myCamp) { mutableIntStateOf(myCamp.waterLitres) }
    var medKits by remember(myCamp) { mutableIntStateOf(myCamp.medicalKits) }
    var blankets by remember(myCamp) { mutableIntStateOf(myCamp.blankets) }
    var operatingStatus by remember(myCamp) { mutableStateOf(myCamp.operatingStatus) }

    val hasLowStock = foodPacks < 100 || waterLitres < 500 || medKits < 10

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SafetyGreen),
                modifier = Modifier.fillMaxWidth().testTag("relief_manager_header")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (lang == "ta") "நிவாரண முகாம் மேலாளர் பலகை" else "Relief Camp Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = myCamp.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Manager: ${user.name} • ${myCamp.district}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        if (hasLowStock) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WarningOrangeLight)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lang == "ta") "குறைந்த கையிருப்பு எச்சரிக்கை! மாவட்ட அலுவலகத்திற்கு கோரிக்கை அனுப்பப்பட்டது." else "Low Stock Alert! Auto-forwarded to District Disaster Officer for resupply.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                    }
                }
            }
        }

        // Inventory Editor
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (lang == "ta") "முகாம் இருப்பு & கொள்ளளவு திருத்தம்" else "Update Camp Inventory & Headcount",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    InventoryCounterRow(
                        label = if (lang == "ta") "தங்கியுள்ள நபர்கள் (Occupants)" else "Current Occupants (Max ${myCamp.totalCapacity})",
                        value = occupants,
                        step = 10,
                        onValueChange = { occupants = it }
                    )

                    InventoryCounterRow(
                        label = if (lang == "ta") "உணவுப் பொட்டலங்கள் (Meals)" else "Prepared Food Packs",
                        value = foodPacks,
                        step = 50,
                        onValueChange = { foodPacks = it }
                    )

                    InventoryCounterRow(
                        label = if (lang == "ta") "குடிநீர் (லிட்டர்)" else "Drinking Water (Litres)",
                        value = waterLitres,
                        step = 100,
                        onValueChange = { waterLitres = it }
                    )

                    InventoryCounterRow(
                        label = if (lang == "ta") "முதலுதவி பெட்டிகள் (Med Kits)" else "First Aid / Medical Kits",
                        value = medKits,
                        step = 5,
                        onValueChange = { medKits = it }
                    )

                    InventoryCounterRow(
                        label = if (lang == "ta") "போர்வைகள் (Blankets)" else "Blankets & Tarpaulins",
                        value = blankets,
                        step = 25,
                        onValueChange = { blankets = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val newStatus = if (occupants >= myCamp.totalCapacity) CampStatus.AT_CAPACITY else CampStatus.ACTIVE
                            onUpdateInventory(myCamp.id, occupants, foodPacks, waterLitres, medKits, blankets, newStatus)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_inventory_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (lang == "ta") "இருப்பு விவரங்களைச் சேமி" else "Save & Sync Inventory", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryCounterRow(label: String, value: Int, step: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (value >= step) onValueChange(value - step) }) {
                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
            }
            Text(
                text = "$value",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            IconButton(onClick = { onValueChange(value + step) }) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
            }
        }
    }
}

@Composable
fun VolunteerScreen(
    user: UserAccount,
    tasks: List<VolunteerTask>,
    lang: String,
    onToggleTask: (taskId: String) -> Unit,
    onCompleteTask: (taskId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isOnDuty by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A148C)),
                modifier = Modifier.fillMaxWidth().testTag("volunteer_header")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == "ta") "தன்னார்வலர் போர்டல்" else "Disaster Volunteer Portal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isOnDuty) "ON DUTY" else "OFF DUTY",
                                color = if (isOnDuty) Color(0xFF69F0AE) else Color.LightGray,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = isOnDuty,
                                onCheckedChange = { isOnDuty = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF00E676))
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Skills: First Aid certified, Inflatable Boat, Tamil & English Translator",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            Text(
                text = if (lang == "ta") "களப் பணிகள் (${tasks.size})" else "Active Field Tasks (${tasks.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(tasks) { task ->
            val isAssigned = task.assignedVolunteers.contains(user.name)

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(task.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        PriorityBadge(task.priority, lang)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(task.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Location: ${task.location} (${task.district})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Volunteers: ${task.assignedVolunteers.size} / ${task.volunteersNeeded} enrolled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (task.isCompleted) {
                            Surface(shape = RoundedCornerShape(6.dp), color = SafetyGreenLight) {
                                Text("COMPLETED", color = SafetyGreen, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        } else {
                            Button(
                                onClick = { onToggleTask(task.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAssigned) Color.Gray else RescueBlue
                                )
                            ) {
                                Text(if (isAssigned) "Leave Task" else "Sign Up For Task")
                            }

                            if (isAssigned) {
                                OutlinedButton(
                                    onClick = { onCompleteTask(task.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SafetyGreen)
                                ) {
                                    Text("Mark Done")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
