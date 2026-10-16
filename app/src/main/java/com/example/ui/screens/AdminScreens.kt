package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    user: UserAccount,
    sosRequests: List<SOSRequest>,
    reports: List<DisasterReport>,
    camps: List<ReliefCamp>,
    alerts: List<DisasterAlert>,
    auditLogs: List<AuditLog>,
    lang: String,
    onVerifyReport: (reportId: String, status: VerificationStatus, reason: String?) -> Unit,
    onPublishAlert: (title: String, titleTa: String, severity: AlertSeverity, desc: String, descTa: String, authority: String, districts: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Analytics, 1: Verify Reports, 2: Broadcast Alert, 3: Audit Logs

    val criticalCount = sosRequests.count { it.aiPriority == PriorityLevel.CRITICAL && it.status != RequestStatus.RESOLVED }
    val unverifiedCount = reports.count { it.verificationStatus == VerificationStatus.PENDING }
    val dispatchedCount = sosRequests.count { it.status == RequestStatus.TEAM_DISPATCHED }
    val resolvedCount = sosRequests.count { it.status == RequestStatus.RESOLVED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().testTag("admin_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == "ta") "பேரிடர் கட்டுப்பாட்டு அறை (EOC)" else "State Emergency Operations Centre",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.25f)) {
                            Text(
                                text = user.role.labelEn.uppercase(),
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
                        text = "${user.organization} • ${user.district}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Navigation Tabs
        item {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Analytics") },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Verify ($unverifiedCount)") },
                    icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Alerts") },
                    icon = { Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Audit") },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Key Metrics 2x2 Grid
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard(
                            title = "Critical Emergencies",
                            count = "$criticalCount",
                            icon = Icons.Default.Warning,
                            iconTint = EmergencyRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Unverified Reports",
                            count = "$unverifiedCount",
                            icon = Icons.Default.HelpOutline,
                            iconTint = WarningOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatCard(
                            title = "Teams Dispatched",
                            count = "$dispatchedCount",
                            icon = Icons.Default.DirectionsBoat,
                            iconTint = RescueBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Relief Camps Open",
                            count = "${camps.size}",
                            icon = Icons.Default.NightShelter,
                            iconTint = SafetyGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Incident Category Breakdown
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Incidents by Hazard Category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val floodCount = sosRequests.count { it.category == DisasterCategory.FLOODING } + reports.count { it.category == DisasterCategory.FLOODING }
                            val cycloneCount = sosRequests.count { it.category == DisasterCategory.CYCLONE } + reports.count { it.category == DisasterCategory.CYCLONE }
                            val fireCount = sosRequests.count { it.category == DisasterCategory.FIRE } + reports.count { it.category == DisasterCategory.FIRE }
                            val roadCount = reports.count { it.category == DisasterCategory.BLOCKED_ROAD }

                            CategoryBar("Flooding & Waterlogging", floodCount, 10, RescueBlue)
                            CategoryBar("Cyclone & Gale Storms", cycloneCount, 10, Color(0xFF00ACC1))
                            CategoryBar("Fire Incidents", fireCount, 10, EmergencyRed)
                            CategoryBar("Road Blockages & Bridges", roadCount, 10, WarningOrange)
                        }
                    }
                }
            }

            1 -> {
                // Report Verification list
                val pending = reports.filter { it.verificationStatus == VerificationStatus.PENDING }
                if (pending.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("All submitted reports have been reviewed!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    items(pending) { rpt ->
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
                                    Text(rpt.id, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    PriorityBadge(rpt.priority, lang)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(rpt.category.labelEn, fontWeight = FontWeight.Bold)
                                Text("${rpt.location} (${rpt.district})", style = MaterialTheme.typography.bodySmall)
                                Text(rpt.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                                Text("Reporter: ${rpt.reporterName} (${rpt.reporterPhone})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { onVerifyReport(rpt.id, VerificationStatus.REJECTED, "Unverified / False report") },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed)
                                    ) {
                                        Text("Reject")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { onVerifyReport(rpt.id, VerificationStatus.VERIFIED, null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen)
                                    ) {
                                        Text("Verify & Publish")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Broadcast Alert Creator
                item {
                    AlertPublisherCard(lang = lang, onPublish = onPublishAlert)
                }

                item {
                    Text("Active Disaster Broadcasts (${alerts.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(alerts) { alert ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (alert.severity == AlertSeverity.EMERGENCY) EmergencyRedContainer else WarningOrangeLight
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(alert.title, fontWeight = FontWeight.Bold, color = EmergencyRedDark)
                                Surface(shape = RoundedCornerShape(6.dp), color = EmergencyRed) {
                                    Text(alert.severity.name, color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(4.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(alert.description, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Authority: ${alert.issuingAuthority}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            3 -> {
                // Administrative Audit Log table
                item {
                    Text("Administrative Audit Trail (${auditLogs.size} Events)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(auditLogs) { log ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = RescueBlue)
                                Text(log.adminName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(log.details, style = MaterialTheme.typography.bodySmall)
                            Text("Target: ${log.target}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBar(label: String, count: Int, max: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text("$count", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        val fraction = (count.toFloat() / max.coerceAtLeast(1)).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun AlertPublisherCard(
    lang: String,
    onPublish: (title: String, titleTa: String, severity: AlertSeverity, desc: String, descTa: String, authority: String, districts: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var titleTa by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var descTa by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf(AlertSeverity.EMERGENCY) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Broadcast Official Emergency Alert", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EmergencyRed)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Alert Title (English)") },
                modifier = Modifier.fillMaxWidth().testTag("alert_title_en_input")
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = titleTa,
                onValueChange = { titleTa = it },
                label = { Text("எச்சரிக்கை தலைப்பு (தமிழ்)") },
                modifier = Modifier.fillMaxWidth().testTag("alert_title_ta_input")
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Detailed Public Instructions (English)") },
                modifier = Modifier.fillMaxWidth().testTag("alert_desc_en_input"),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = descTa,
                onValueChange = { descTa = it },
                label = { Text("விரிவான பொது வழிமுறைகள் (தமிழ்)") },
                modifier = Modifier.fillMaxWidth().testTag("alert_desc_ta_input"),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onPublish(
                            title,
                            titleTa.ifBlank { title },
                            severity,
                            desc,
                            descTa.ifBlank { desc },
                            "State Disaster Management Authority (SDMA)",
                            listOf("Chennai", "Cuddalore", "Chengalpattu")
                        )
                        title = ""
                        titleTa = ""
                        desc = ""
                        descTa = ""
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("publish_alert_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Issue Public Alert", fontWeight = FontWeight.Bold)
            }
        }
    }
}
