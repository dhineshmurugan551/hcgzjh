package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyTopBar(
    currentRole: UserRole,
    currentLanguage: String,
    isHighContrast: Boolean,
    onLanguageToggle: () -> Unit,
    onHighContrastToggle: () -> Unit,
    onRoleSwitchClick: () -> Unit,
    onEmergencyCallClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.testTag("emergency_top_bar"),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Disaster App Logo",
                        tint = EmergencyRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == "ta") "ஸ்மார்ட் பேரிடர் மீட்பு" else "Smart Disaster Rescue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(top = 2.dp).clickable { onRoleSwitchClick() }
                ) {
                    Text(
                        text = if (currentLanguage == "ta") "பங்கு: ${currentRole.labelTa} ▼" else "Role: ${currentRole.labelEn} ▼",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        actions = {
            // Language selector button
            TextButton(
                onClick = onLanguageToggle,
                modifier = Modifier.testTag("language_toggle_button")
            ) {
                Text(
                    text = if (currentLanguage == "ta") "ENG" else "தமிழ்",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // High Contrast mode toggle button
            IconButton(
                onClick = onHighContrastToggle,
                modifier = Modifier.testTag("high_contrast_toggle")
            ) {
                Icon(
                    imageVector = if (isHighContrast) Icons.Default.Contrast else Icons.Default.BrightnessMedium,
                    contentDescription = "Toggle High Contrast Accessibility",
                    tint = if (isHighContrast) WarningOrange else MaterialTheme.colorScheme.onSurface
                )
            }

            // Direct Emergency 112 Call Button
            FilledIconButton(
                onClick = onEmergencyCallClick,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = EmergencyRed),
                modifier = Modifier.testTag("call_112_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Direct Call 112",
                    tint = Color.White
                )
            }

            // User Profile
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun SosEmergencyButton(
    currentLanguage: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .testTag("sos_emergency_button")
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = EmergencyRed),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "SOS Warning",
                    tint = EmergencyRed,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (currentLanguage == "ta") "அவசர உதவி SOS" else "EMERGENCY SOS",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentLanguage == "ta")
                    "உடனடி மீட்பு & மருத்துவ உதவிக்கு அழுத்தவும்"
                else
                    "Press to Send Distress Signal & GPS Location",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: PriorityLevel, lang: String = "en") {
    val (bgColor, textColor) = when (priority) {
        PriorityLevel.CRITICAL -> EmergencyRed to Color.White
        PriorityLevel.HIGH -> WarningOrange to Color.White
        PriorityLevel.MEDIUM -> Color(0xFF0288D1) to Color.White
        PriorityLevel.LOW -> SafetyGreen to Color.White
        PriorityLevel.PENDING -> Color.Gray to Color.White
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = if (lang == "ta") priority.labelTa else priority.labelEn,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatusBadge(status: RequestStatus, lang: String = "en") {
    val (bgColor, textColor) = when (status) {
        RequestStatus.SUBMITTED -> Color(0xFF607D8B) to Color.White
        RequestStatus.UNDER_REVIEW -> WarningOrange to Color.White
        RequestStatus.ASSIGNED -> RescueBlue to Color.White
        RequestStatus.TEAM_DISPATCHED -> Color(0xFF6A1B9A) to Color.White
        RequestStatus.ASSISTANCE_PROVIDED -> SafetyGreen to Color.White
        RequestStatus.RESOLVED -> Color(0xFF1B5E20) to Color.White
        RequestStatus.CANCELLED -> Color.Gray to Color.White
        RequestStatus.DUPLICATE -> Color(0xFFBF360C) to Color.White
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = if (lang == "ta") status.labelTa else status.labelEn,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    count: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = count,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
