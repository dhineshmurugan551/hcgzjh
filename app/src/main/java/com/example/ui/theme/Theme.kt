package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val EmergencyLightColorScheme = lightColorScheme(
    primary = EmergencyRed,
    onPrimary = Color.White,
    primaryContainer = EmergencyRedContainer,
    onPrimaryContainer = EmergencyRedDark,
    secondary = RescueBlue,
    onSecondary = Color.White,
    secondaryContainer = RescueBlueLight,
    onSecondaryContainer = RescueBlueDark,
    tertiary = WarningOrange,
    onTertiary = Color.White,
    background = Gray50,
    surface = Color.White,
    onBackground = Gray900,
    onSurface = Gray900,
    error = EmergencyRedDark,
    onError = Color.White
)

private val EmergencyDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A80),
    onPrimary = EmergencyRedDark,
    primaryContainer = EmergencyRedDark,
    onPrimaryContainer = EmergencyRedLight,
    secondary = Color(0xFF90CAF9),
    onSecondary = RescueBlueDark,
    secondaryContainer = RescueBlueDark,
    onSecondaryContainer = RescueBlueLight,
    tertiary = Color(0xFFFFB74D),
    onTertiary = WarningOrange,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

private val HighContrastColorScheme = lightColorScheme(
    primary = Color(0xFFB71C1C),
    onPrimary = Color.White,
    primaryContainer = HighContrastYellow,
    onPrimaryContainer = HighContrastBlack,
    secondary = Color(0xFF0D47A1),
    onSecondary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Color(0xFFB71C1C),
    onError = Color.White
)

@Composable
fun DisasterRescueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast -> HighContrastColorScheme
        darkTheme -> EmergencyDarkColorScheme
        else -> EmergencyLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
