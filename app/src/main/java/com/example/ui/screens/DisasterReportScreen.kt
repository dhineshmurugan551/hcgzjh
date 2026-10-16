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
import com.example.ui.theme.*

@Composable
fun DisasterReportScreen(
    reports: List<DisasterReport>,
    lang: String,
    onSubmitReport: (category: DisasterCategory, description: String, district: String, location: String, affected: Int, lat: Double, lon: Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showForm by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf(DisasterCategory.FLOODING) }
    var description by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("Chennai") }
    var location by remember { mutableStateOf("") }
    var affectedCount by remember { mutableIntStateOf(10) }

    val districts = listOf("Chennai", "Chengalpattu", "Kanchipuram", "Cuddalore", "Tiruvallur", "Madurai", "Coimbatore", "Kanyakumari")

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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = if (lang == "ta") "பேரிடர் பாதிப்பு அறிக்கைகள்" else "Disaster Incidents",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                FilledTonalButton(
                    onClick = { showForm = !showForm },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = WarningOrange, contentColor = Color.White),
                    modifier = Modifier.testTag("toggle_report_form_button")
                ) {
                    Icon(if (showForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showForm) (if (lang == "ta") "மூடு" else "Close") else (if (lang == "ta") "புதிய அறிக்கை" else "New Report"))
                }
            }
        }

        if (showForm) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("disaster_report_form_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (lang == "ta") "புதிய பேரிடர் சம்பவத்தை பதிவு செய்யவும்" else "Report New Incident / Hazard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WarningOrange
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Dropdown
                        Text(
                            text = if (lang == "ta") "சம்பவ வகை" else "Incident Category",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CategorySmallChip(
                                label = if (lang == "ta") "வெள்ளம்" else "Flood",
                                selected = selectedCategory == DisasterCategory.FLOODING,
                                onClick = { selectedCategory = DisasterCategory.FLOODING },
                                modifier = Modifier.weight(1f)
                            )
                            CategorySmallChip(
                                label = if (lang == "ta") "சாலை துண்டிப்பு" else "Road Block",
                                selected = selectedCategory == DisasterCategory.BLOCKED_ROAD,
                                onClick = { selectedCategory = DisasterCategory.BLOCKED_ROAD },
                                modifier = Modifier.weight(1f)
                            )
                            CategorySmallChip(
                                label = if (lang == "ta") "மின்சாரம்" else "Power Out",
                                selected = selectedCategory == DisasterCategory.POWER_OUTAGE,
                                onClick = { selectedCategory = DisasterCategory.POWER_OUTAGE },
                                modifier = Modifier.weight(1f)
                            )
                            CategorySmallChip(
                                label = if (lang == "ta") "தீ" else "Fire",
                                selected = selectedCategory == DisasterCategory.FIRE,
                                onClick = { selectedCategory = DisasterCategory.FIRE },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text(if (lang == "ta") "சம்பவம் நடந்த இடம் / பாலம் / சாலை" else "Incident Location / Street / Bridge") },
                            modifier = Modifier.fillMaxWidth().testTag("report_location_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(if (lang == "ta") "விவரங்கள் (AI வகைப்படுத்த உதவும்)" else "Description (AI will triage priority)") },
                            modifier = Modifier.fillMaxWidth().testTag("report_description_input"),
                            minLines = 2,
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == "ta") "பாதிக்கப்பட்ட உத்தேச நபர்கள்: $affectedCount" else "Approx. Affected Persons: $affectedCount",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row {
                                IconButton(onClick = { if (affectedCount > 5) affectedCount -= 5 }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                IconButton(onClick = { affectedCount += 5 }) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (location.isNotBlank()) {
                                    onSubmitReport(selectedCategory, description, district, location, affectedCount, 13.00, 80.22)
                                    location = ""
                                    description = ""
                                    showForm = false
                                }
                            },
                            enabled = location.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_report_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = WarningOrange)
                        ) {
                            Icon(Icons.Default.Publish, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (lang == "ta") "அறிக்கையை சமர்ப்பிக்கவும்" else "Submit Report")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = if (lang == "ta") "சமீபத்திய சம்பவ அறிக்கைகள் (${reports.size})" else "Recent Community Reports (${reports.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(reports) { rpt ->
            DisasterReportCard(report = rpt, lang = lang)
        }
    }
}

@Composable
fun CategorySmallChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (selected) WarningOrange else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            maxLines = 1
        )
    }
}

@Composable
fun DisasterReportCard(report: DisasterReport, lang: String) {
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
                Text(
                    text = report.id,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                PriorityBadge(report.priority, lang)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (lang == "ta") report.category.labelTa else report.category.labelEn,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${report.location}, ${report.district}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (report.verificationStatus == VerificationStatus.VERIFIED) SafetyGreenLight else WarningOrangeLight
                ) {
                    Text(
                        text = if (lang == "ta") report.verificationStatus.labelTa else report.verificationStatus.labelEn,
                        color = if (report.verificationStatus == VerificationStatus.VERIFIED) SafetyGreen else WarningOrange,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = "Affected: ~${report.affectedPeople} persons",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
