package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*

data class MapPinItem(
    val id: String,
    val title: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val type: PinType,
    val details: String,
    val isVerified: Boolean = true,
    val priority: PriorityLevel = PriorityLevel.HIGH
)

enum class PinType {
    SOS_INCIDENT,
    DISASTER_REPORT,
    RELIEF_CAMP,
    MEDICAL_HOSPITAL,
    BLOCKED_ROAD
}

@Composable
fun InteractiveDisasterMap(
    pins: List<MapPinItem>,
    selectedPin: MapPinItem?,
    onPinSelected: (MapPinItem) -> Unit,
    lang: String = "en",
    modifier: Modifier = Modifier
) {
    // Map pan & zoom state
    var scale by remember { mutableStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Bounding coordinates around Tamil Nadu / Chennai area
    // Center: ~13.00 N, 80.20 E
    val centerLat = 13.00
    val centerLon = 80.22
    val latSpan = 0.50
    val lonSpan = 0.50

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8ECEF))
            .testTag("interactive_disaster_map")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 4.0f)
                        offset += pan
                    }
                }
                .pointerInput(pins, scale, offset) {
                    detectTapGestures { tapOffset ->
                        // Hit-test pins
                        val width = size.width
                        val height = size.height

                        var closestPin: MapPinItem? = null
                        var minDistance = Float.MAX_VALUE

                        for (pin in pins) {
                            val xNorm = ((pin.longitude - (centerLon - lonSpan / 2)) / lonSpan).toFloat()
                            val yNorm = (1f - ((pin.latitude - (centerLat - latSpan / 2)) / latSpan)).toFloat()

                            val screenX = (xNorm * width * scale) + offset.x
                            val screenY = (yNorm * height * scale) + offset.y

                            val dx = tapOffset.x - screenX
                            val dy = tapOffset.y - screenY
                            val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                            if (distance < 60f && distance < minDistance) {
                                minDistance = distance
                                closestPin = pin
                            }
                        }

                        if (closestPin != null) {
                            onPinSelected(closestPin)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw River & Water bodies (Adyar & Cooum Rivers approximation)
            val riverPath = Path().apply {
                val startX = (0.05f * width * scale) + offset.x
                val startY = (0.45f * height * scale) + offset.y
                moveTo(startX, startY)
                cubicTo(
                    (0.35f * width * scale) + offset.x, (0.50f * height * scale) + offset.y,
                    (0.65f * width * scale) + offset.x, (0.42f * height * scale) + offset.y,
                    (0.95f * width * scale) + offset.x, (0.48f * height * scale) + offset.y
                )
            }
            drawPath(
                path = riverPath,
                color = Color(0xFF90CAF9).copy(alpha = 0.6f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 24f * scale)
            )

            // Coastline (Bay of Bengal on East)
            val coastPath = Path().apply {
                moveTo((0.92f * width * scale) + offset.x, 0f)
                lineTo((0.92f * width * scale) + offset.x, height)
                lineTo(width, height)
                lineTo(width, 0f)
                close()
            }
            drawPath(path = coastPath, color = Color(0xFFBBDEFB).copy(alpha = 0.4f))

            // 2. Draw Major Roads grid lines
            val roadColor = Color(0xFFCFD8DC)
            for (i in 1..8) {
                val gridY = (i * 0.12f * height * scale) + offset.y
                drawLine(
                    color = roadColor,
                    start = Offset(0f, gridY),
                    end = Offset(width, gridY),
                    strokeWidth = 3f * scale
                )
                val gridX = (i * 0.12f * width * scale) + offset.x
                drawLine(
                    color = roadColor,
                    start = Offset(gridX, 0f),
                    end = Offset(gridX, height),
                    strokeWidth = 3f * scale
                )
            }

            // 3. Draw Map Pins
            for (pin in pins) {
                val xNorm = ((pin.longitude - (centerLon - lonSpan / 2)) / lonSpan).toFloat()
                val yNorm = (1f - ((pin.latitude - (centerLat - latSpan / 2)) / latSpan)).toFloat()

                val screenX = (xNorm * width * scale) + offset.x
                val screenY = (yNorm * height * scale) + offset.y

                // Only render if visible on screen
                if (screenX in -50f..(width + 50f) && screenY in -50f..(height + 50f)) {
                    drawMapMarker(
                        x = screenX,
                        y = screenY,
                        type = pin.type,
                        isSelected = selectedPin?.id == pin.id,
                        scale = scale
                    )
                }
            }
        }

        // Map Control Overlays: Zoom in / Zoom out / Reset
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            FilledIconButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(4f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(6.dp))
            FilledIconButton(
                onClick = { scale = (scale * 0.8f).coerceAtLeast(0.6f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(6.dp))
            FilledIconButton(
                onClick = {
                    scale = 1.0f
                    offset = Offset.Zero
                },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Reset Map View", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Map Legend at Top Left
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shadowElevation = 2.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EmergencyRed))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SOS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SafetyGreen))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (lang == "ta") "முகாம்" else "Camp", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(WarningOrange))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (lang == "ta") "அபாயம்" else "Hazard", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }

        // Bottom Selected Pin Info Banner
        if (selectedPin != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("map_marker_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedPin.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        PriorityBadge(selectedPin.priority, lang)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedPin.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedPin.isVerified) Icons.Default.Verified else Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = if (selectedPin.isVerified) SafetyGreen else WarningOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedPin.isVerified)
                                (if (lang == "ta") "அதிகாரப்பூர்வமாக உறுதிசெய்யப்பட்டது" else "Official & Verified Incident")
                            else
                                (if (lang == "ta") "சரிபார்ப்பு நிலுவை" else "Pending Verification"),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selectedPin.isVerified) SafetyGreen else WarningOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawMapMarker(
    x: Float,
    y: Float,
    type: PinType,
    isSelected: Boolean,
    scale: Float
) {
    val baseRadius = if (isSelected) 22f else 16f

    val color = when (type) {
        PinType.SOS_INCIDENT -> EmergencyRed
        PinType.RELIEF_CAMP -> SafetyGreen
        PinType.MEDICAL_HOSPITAL -> MedicalCyan
        PinType.BLOCKED_ROAD -> Color(0xFFD84315)
        PinType.DISASTER_REPORT -> WarningOrange
    }

    // Outer ripple if selected
    if (isSelected) {
        drawCircle(
            color = color.copy(alpha = 0.35f),
            radius = baseRadius + 12f,
            center = Offset(x, y)
        )
    }

    // Shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.25f),
        radius = baseRadius,
        center = Offset(x, y + 3f)
    )

    // Main Pin Body
    drawCircle(
        color = color,
        radius = baseRadius,
        center = Offset(x, y)
    )

    // Inner White Accent
    drawCircle(
        color = Color.White,
        radius = baseRadius * 0.45f,
        center = Offset(x, y)
    )
}
