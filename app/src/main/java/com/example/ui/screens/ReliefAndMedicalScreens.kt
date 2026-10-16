package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.*

@Composable
fun ReliefCampsScreen(
    camps: List<ReliefCamp>,
    lang: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyOpen by remember { mutableStateOf(false) }

    val filteredCamps = camps.filter {
        (it.name.contains(searchQuery, ignoreCase = true) || it.district.contains(searchQuery, ignoreCase = true) || it.address.contains(searchQuery, ignoreCase = true)) &&
                (!filterOnlyOpen || it.operatingStatus == CampStatus.ACTIVE)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (lang == "ta") "அருகிலுள்ள நிவாரண முகாம்கள்" else "Nearby Relief Camps & Shelters",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SafetyGreen
                )
            }
        }

        // Search Bar & Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(if (lang == "ta") "மாவட்டம் அல்லது முகாம் பெயர் தேடவும்" else "Search camp by area or district...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("camp_search_input")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = filterOnlyOpen,
                    onClick = { filterOnlyOpen = !filterOnlyOpen },
                    label = { Text(if (lang == "ta") "செயல்பாட்டில் உள்ளவை மட்டும்" else "Only Open & Available Camps") }
                )
            }
        }

        items(filteredCamps) { camp ->
            ReliefCampCard(camp = camp, lang = lang, onCall = {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${camp.contactPhone}")))
            })
        }
    }
}

@Composable
fun ReliefCampCard(camp: ReliefCamp, lang: String, onCall: () -> Unit) {
    val occupancyPercent = (camp.currentOccupants.toFloat() / camp.totalCapacity.coerceAtLeast(1))
    val isFull = occupancyPercent >= 0.95f

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
                    text = camp.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isFull) WarningOrangeLight else SafetyGreenLight
                ) {
                    Text(
                        text = if (lang == "ta") camp.operatingStatus.labelTa else camp.operatingStatus.labelEn,
                        color = if (isFull) WarningOrange else SafetyGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${camp.address} (${camp.district})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Occupancy Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == "ta") "தங்கும் கொள்ளளவு: ${camp.currentOccupants} / ${camp.totalCapacity}" else "Capacity: ${camp.currentOccupants} / ${camp.totalCapacity} Beds",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(occupancyPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isFull) WarningOrange else SafetyGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { occupancyPercent.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (isFull) WarningOrange else SafetyGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Supplies pill indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SupplyBadge(label = "${camp.foodStockPacks} Meals", icon = Icons.Default.Restaurant, active = camp.foodAvailable)
                SupplyBadge(label = "${camp.waterLitres}L Water", icon = Icons.Default.WaterDrop, active = camp.waterAvailable)
                SupplyBadge(label = "${camp.medicalKits} Medkits", icon = Icons.Default.MedicalServices, active = camp.medicalAvailable)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Manager: ${camp.managerName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = camp.openingHours,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledTonalButton(
                    onClick = onCall,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SafetyGreen, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(camp.contactPhone, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SupplyBadge(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) MaterialTheme.colorScheme.surfaceVariant else Color.LightGray.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (active) MaterialTheme.colorScheme.primary else Color.Gray)
            Spacer(modifier = Modifier.width(3.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun MedicalAssistanceScreen(
    medicalRequests: List<MedicalRequest>,
    lang: String,
    onSubmitMedical: (name: String, phone: String, count: Int, symptoms: String, location: String, transport: Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var patientCount by remember { mutableIntStateOf(1) }
    var symptoms by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var transportRequired by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (lang == "ta") "அவசர மருத்துவ உதவி கோரிக்கை" else "Emergency Medical Assistance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyan
                )
            }
        }

        // Direct 108 Ambulance Call
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalCyanLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == "ta") "உடனடி 108 ஆம்புலன்ஸ் உதவி" else "Call 108 Free Emergency Ambulance",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MedicalCyan
                        )
                        Text(
                            text = if (lang == "ta") "உயிருக்கு ஆபத்தான நிலைகளில் உடனே அழைக்கவும்" else "Government Emergency Ambulance Service",
                            style = MaterialTheme.typography.bodySmall,
                            color = MedicalCyan.copy(alpha = 0.8f)
                        )
                    }
                    Button(
                        onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))) },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("108", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Medical Assistance Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (lang == "ta") "மருத்துவ உதவி படிவம்" else "Request Paramedic / Doctor Triage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (lang == "ta") "நோயாளி / கோருபவர் பெயர்" else "Patient / Requester Name") },
                        modifier = Modifier.fillMaxWidth().testTag("medical_name_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (lang == "ta") "தொடர்பு தொலைபேசி எண்" else "Contact Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("medical_phone_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (lang == "ta") "தற்போது இருக்கும் இடம் / முகவரி" else "Current Location / Address") },
                        modifier = Modifier.fillMaxWidth().testTag("medical_location_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = symptoms,
                        onValueChange = { symptoms = it },
                        label = { Text(if (lang == "ta") "மருத்துவ அறிகுறிகள் (காயம், காய்ச்சல், பிரசவ வலி, மருந்து தேவை)" else "Symptoms / Condition (e.g. trauma bleeding, insulin needed, delivery)") },
                        modifier = Modifier.fillMaxWidth().testTag("medical_symptoms_input"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (lang == "ta") "ஆம்புலன்ஸ் வாகனம் தேவையா?" else "Ambulance Transport Required?")
                        Switch(
                            checked = transportRequired,
                            onCheckedChange = { transportRequired = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                onSubmitMedical(name, phone, patientCount, symptoms, location, transportRequired)
                                name = ""
                                phone = ""
                                symptoms = ""
                                location = ""
                            }
                        },
                        enabled = name.isNotBlank() && phone.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("submit_medical_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (lang == "ta") "மருத்துவ கோரிக்கையை அனுப்பவும்" else "Submit Medical Request", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Medical Requests
        item {
            Text(
                text = if (lang == "ta") "செயலில் உள்ள மருத்துவ கோரிக்கைகள்" else "Active Medical Aid Cases (${medicalRequests.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(medicalRequests) { med ->
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
                        Text(med.requesterName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        PriorityBadge(med.priority, lang)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Symptoms: ${med.symptoms}", style = MaterialTheme.typography.bodyMedium)
                    Text("Location: ${med.location} • Phone: ${med.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (med.transportRequired) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(shape = RoundedCornerShape(6.dp), color = MedicalCyanLight) {
                            Text("Ambulance Dispatched", color = MedicalCyan, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}
