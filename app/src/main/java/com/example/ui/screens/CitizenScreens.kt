package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CitizenHomeScreen(
    user: UserAccount,
    sosRequests: List<SOSRequest>,
    alerts: List<DisasterAlert>,
    lang: String,
    onSosClick: () -> Unit,
    onReportClick: () -> Unit,
    onMapClick: () -> Unit,
    onReliefCampsClick: () -> Unit,
    onMedicalHelpClick: () -> Unit,
    onTrackRequestsClick: () -> Unit,
    onSafetyGuidesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeSosCount = sosRequests.count {
        it.status != RequestStatus.RESOLVED && it.status != RequestStatus.CANCELLED
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Active Emergency Broadcast Banner if severe alert exists
            val topAlert = alerts.firstOrNull { it.severity == AlertSeverity.EMERGENCY }
            if (topAlert != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_alert_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Alert",
                            tint = EmergencyRedDark,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == "ta") topAlert.titleTa else topAlert.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmergencyRedDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (lang == "ta") topAlert.descriptionTa else topAlert.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = EmergencyRedDark.copy(alpha = 0.85f),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // Giant Prominent SOS Button
        item {
            SosEmergencyButton(
                currentLanguage = lang,
                onClick = onSosClick
            )
        }

        // 112 Direct Emergency Call Bar with Disclaimer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                        context.startActivity(dialIntent)
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EmergencyRed.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhoneInTalk, contentDescription = "112", tint = EmergencyRed)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == "ta") "அவசர எண் 112 அழைக்கவும்" else "Call Official Emergency 112",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == "ta") "காவல்துறை, தீயணைப்பு, ஆம்புலன்ஸ் நேரடி அழைப்பு" else "Direct Police, Fire, Ambulance Helpline",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    FilledTonalButton(
                        onClick = {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                            context.startActivity(dialIntent)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = EmergencyRed, contentColor = Color.White)
                    ) {
                        Text("112", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Action Grid (2 columns)
        item {
            Text(
                text = if (lang == "ta") "அவசர சேவைகள்" else "Emergency Services",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = if (lang == "ta") "பேரிடர் அறிக்கை" else "Report Incident",
                    subtitle = if (lang == "ta") "வெள்ளம்/புயல் பாதிப்பு" else "Floods, fires, blockages",
                    icon = Icons.Default.ReportProblem,
                    color = WarningOrange,
                    modifier = Modifier.weight(1f),
                    testTag = "report_disaster_button",
                    onClick = onReportClick
                )
                QuickActionCard(
                    title = if (lang == "ta") "நேரலை வரைபடம்" else "Live Disaster Map",
                    subtitle = if (lang == "ta") "முகாம்கள், ஆபத்து மண்டலம்" else "Camps & hazards",
                    icon = Icons.Default.Map,
                    color = RescueBlue,
                    modifier = Modifier.weight(1f),
                    testTag = "live_map_button",
                    onClick = onMapClick
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = if (lang == "ta") "நிவாரண முகாம்கள்" else "Relief Camps",
                    subtitle = if (lang == "ta") "உணவு, குடிநீர், தங்குமிடம்" else "Food, shelter, beds",
                    icon = Icons.Default.NightShelter,
                    color = SafetyGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "relief_camps_button",
                    onClick = onReliefCampsClick
                )
                QuickActionCard(
                    title = if (lang == "ta") "மருத்துவ உதவி" else "Medical Aid",
                    subtitle = if (lang == "ta") "முதலுதவி & மருந்துகள்" else "First aid & ambulance",
                    icon = Icons.Default.LocalHospital,
                    color = MedicalCyan,
                    modifier = Modifier.weight(1f),
                    testTag = "medical_aid_button",
                    onClick = onMedicalHelpClick
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = if (lang == "ta") "கோரிக்கைகள் ($activeSosCount)" else "Track Requests ($activeSosCount)",
                    subtitle = if (lang == "ta") "மீட்புக் குழு நிலை" else "Live status pipeline",
                    icon = Icons.Default.Assignment,
                    color = Color(0xFF6A1B9A),
                    modifier = Modifier.weight(1f),
                    testTag = "track_requests_button",
                    onClick = onTrackRequestsClick
                )
                QuickActionCard(
                    title = if (lang == "ta") "பாதுகாப்பு & ஆடியோ" else "Safety & Voice",
                    subtitle = if (lang == "ta") "குரல் வழிமுறைகள்" else "Audio guidance",
                    icon = Icons.Default.VolumeUp,
                    color = Color(0xFF00897B),
                    modifier = Modifier.weight(1f),
                    testTag = "safety_guides_button",
                    onClick = onSafetyGuidesClick
                )
            }
        }

        // Emergency Helplines quick contacts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (lang == "ta") "அவசர தொடர்பு எண்கள்" else "Emergency Helplines (Tamil Nadu)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HelplinePill(number = "112", label = "All Emergency", onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")))
                        })
                        HelplinePill(number = "108", label = "Ambulance", onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108")))
                        })
                        HelplinePill(number = "101", label = "Fire Force", onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:101")))
                        })
                        HelplinePill(number = "1070", label = "State Disaster", onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:1070")))
                        })
                    }
                }
            }
        }

        item {
            // Legal Disclaimer
            Text(
                text = if (lang == "ta")
                    "முக்கிய குறிப்பு: இச்செயலி அவசர காலங்களில் உதவுவதற்காக உருவாக்கப்பட்டது. அதிகாரப்பூர்வ மீட்புப் பணிகளுக்கு 112 அல்லது 108 எண்களை அழைக்கவும்."
                else
                    "Notice: Smart Disaster Rescue complements civil protection efforts. For life-threatening emergencies, always dial 112 or 108 directly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HelplinePill(number: String, label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(number, fontWeight = FontWeight.Bold, color = EmergencyRed, style = MaterialTheme.typography.titleSmall)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SosRequestScreen(
    user: UserAccount,
    lang: String,
    onSubmit: (category: DisasterCategory, peopleCount: Int, address: String, landmark: String, hasMedical: Boolean, hasChildren: Boolean, hasElderly: Boolean, hasDisability: Boolean, notes: String, lat: Double, lon: Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(DisasterCategory.FLOODING) }
    var peopleCount by remember { mutableIntStateOf(2) }
    var address by remember { mutableStateOf("Velachery West, Chennai") }
    var landmark by remember { mutableStateOf("Near Phoenix Mall / Bus Stop") }
    var hasMedical by remember { mutableStateOf(false) }
    var hasChildren by remember { mutableStateOf(false) }
    var hasElderly by remember { mutableStateOf(false) }
    var hasDisability by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    // GPS coordinates simulation
    var latitude by remember { mutableDoubleStateOf(12.9815) }
    var longitude by remember { mutableDoubleStateOf(80.2180) }
    var isGpsActive by remember { mutableStateOf(true) }

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
                    text = if (lang == "ta") "அவசர SOS உதவி கோரிக்கை" else "Submit Emergency SOS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = EmergencyRed
                )
            }
        }

        // Location Box with GPS toggle & manual correction
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, tint = RescueBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == "ta") "இருப்பிடம் (GPS)" else "Incident Location (GPS)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = isGpsActive,
                            onCheckedChange = { isGpsActive = it }
                        )
                    }

                    if (isGpsActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SafetyGreenLight.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "GPS Coords: ${"%.4f".format(latitude)}° N, ${"%.4f".format(longitude)}° E (Accuracy: ±5m)",
                                style = MaterialTheme.typography.labelSmall,
                                color = SafetyGreen,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (lang == "ta") "முகவரி / இடம்" else "Address / Street Name") },
                        modifier = Modifier.fillMaxWidth().testTag("sos_address_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text(if (lang == "ta") "அடையாளக் குறி (Landmark)" else "Prominent Landmark") },
                        modifier = Modifier.fillMaxWidth().testTag("sos_landmark_input")
                    )
                }
            }
        }

        // Disaster Category Selection
        item {
            Text(
                text = if (lang == "ta") "அவசர வகை" else "Emergency Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip(
                    label = if (lang == "ta") "வெள்ளம்" else "Flooding",
                    selected = selectedCategory == DisasterCategory.FLOODING,
                    onClick = { selectedCategory = DisasterCategory.FLOODING },
                    modifier = Modifier.weight(1f)
                )
                CategoryChip(
                    label = if (lang == "ta") "புயல்" else "Cyclone",
                    selected = selectedCategory == DisasterCategory.CYCLONE,
                    onClick = { selectedCategory = DisasterCategory.CYCLONE },
                    modifier = Modifier.weight(1f)
                )
                CategoryChip(
                    label = if (lang == "ta") "தீ விபத்து" else "Fire",
                    selected = selectedCategory == DisasterCategory.FIRE,
                    onClick = { selectedCategory = DisasterCategory.FIRE },
                    modifier = Modifier.weight(1f)
                )
                CategoryChip(
                    label = if (lang == "ta") "இடிவு" else "Collapse",
                    selected = selectedCategory == DisasterCategory.COLLAPSED_BUILDING,
                    onClick = { selectedCategory = DisasterCategory.COLLAPSED_BUILDING },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Number of people needing assistance
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (lang == "ta") "பாதிக்கப்பட்ட நபர்கள்" else "Persons Needing Rescue",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == "ta") "சிக்கியுள்ளவர்களின் எண்ணிக்கை" else "Number of stranded individuals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (peopleCount > 1) peopleCount-- },
                            enabled = peopleCount > 1
                        ) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
                        }
                        Text(
                            text = "$peopleCount",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { peopleCount++ }) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
                        }
                    }
                }
            }
        }

        // Vulnerability & Urgent Needs Toggles
        item {
            Text(
                text = if (lang == "ta") "சிறப்பு தேவைகள் & உடனடி உதவிகள்" else "Special Assistance & Urgent Needs",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    CheckboxRow(
                        label = if (lang == "ta") "அவசர மருத்துவ உதவி தேவை (காயம்/நோய்)" else "Urgent Medical Assistance Required (Injury/Illness)",
                        checked = hasMedical,
                        onCheckedChange = { hasMedical = it }
                    )
                    CheckboxRow(
                        label = if (lang == "ta") "குழந்தைகள் / கைக்குழந்தைகள் உள்ளனர்" else "Children / Infants Present",
                        checked = hasChildren,
                        onCheckedChange = { hasChildren = it }
                    )
                    CheckboxRow(
                        label = if (lang == "ta") "முதியவர்கள் உள்ளனர் (60+ வயது)" else "Elderly Persons Present (Age 60+)",
                        checked = hasElderly,
                        onCheckedChange = { hasElderly = it }
                    )
                    CheckboxRow(
                        label = if (lang == "ta") "மாற்றுத்திறனாளிகள் / நடமாட இயலாதவர்" else "Persons with Disabilities / Wheelchair Required",
                        checked = hasDisability,
                        onCheckedChange = { hasDisability = it }
                    )
                }
            }
        }

        // Additional Notes
        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(if (lang == "ta") "கூடுதல் விவரங்கள் (நீர் மட்டம், மாடி நிலை, முதலியன)" else "Additional Notes (e.g. water level, floor, terrace)") },
                modifier = Modifier.fillMaxWidth().testTag("sos_notes_input"),
                minLines = 2,
                maxLines = 4
            )
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    onSubmit(
                        selectedCategory,
                        peopleCount,
                        address,
                        landmark,
                        hasMedical,
                        hasChildren,
                        hasElderly,
                        hasDisability,
                        notes,
                        latitude,
                        longitude
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_sos_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == "ta") "அவசர SOS கோரிக்கையை அனுப்புக" else "TRANSMIT SOS SIGNAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) EmergencyRed else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        )
    }
}

@Composable
fun CheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TrackRequestsScreen(
    requests: List<SOSRequest>,
    lang: String,
    onBack: () -> Unit,
    onCancelRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                    text = if (lang == "ta") "எனது அவசர கோரிக்கைகள்" else "Track My SOS Requests",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (requests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (lang == "ta") "செயலில் உள்ள அவசர கோரிக்கைகள் இல்லை" else "No Active Emergency Requests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == "ta") "நீங்கள் பாதுகாப்பாக உள்ளீர்கள்" else "You are currently marked safe.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(requests) { req ->
                SosStatusCard(request = req, lang = lang, onCancel = { onCancelRequest(req.id) })
            }
        }
    }
}

@Composable
fun SosStatusCard(
    request: SOSRequest,
    lang: String,
    onCancel: () -> Unit
) {
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
                    text = request.id,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusBadge(status = request.status, lang = lang)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${if (lang == "ta") request.category.labelTa else request.category.labelEn} • ${request.peopleCount} ${if (lang == "ta") "நபர்கள்" else "people"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = request.locationAddress,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (request.assignedTeamName != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RescueBlueLight.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = RescueBlueDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Assigned: ${request.assignedTeamName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RescueBlueDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (request.aiSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "AI Triaged: ${request.aiSummary}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Workflow Progress Bar
            val stepIndex = when (request.status) {
                RequestStatus.SUBMITTED -> 1
                RequestStatus.UNDER_REVIEW -> 2
                RequestStatus.ASSIGNED -> 3
                RequestStatus.TEAM_DISPATCHED -> 4
                RequestStatus.ASSISTANCE_PROVIDED -> 5
                RequestStatus.RESOLVED -> 6
                else -> 0
            }

            if (stepIndex > 0) {
                LinearProgressIndicator(
                    progress = { stepIndex / 6f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = EmergencyRed,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Step $stepIndex of 6: ${if (lang == "ta") request.status.labelTa else request.status.labelEn}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (request.status != RequestStatus.RESOLVED && request.status != RequestStatus.CANCELLED) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (lang == "ta") "கோரிக்கையை ரத்து செய்" else "Cancel Request", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
