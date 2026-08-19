package com.example.scanrise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SoftBrass,
    onPrimary = Night,
    primaryContainer = EmeraldDeep,
    onPrimaryContainer = EmeraldGlow,
    secondary = EmeraldGlow,
    onSecondary = Night,
    secondaryContainer = EmeraldDeep,
    onSecondaryContainer = Parchment,
    background = Night,
    surface = NightRaised,
    surfaceVariant = NightRaised,
    onBackground = Parchment,
    onSurface = Parchment,
    onSurfaceVariant = Slate,
    outline = Slate,
    outlineVariant = Slate.copy(alpha = 0.45f),
    error = ErrorRed,
    surfaceTint = SoftBrass
)

@Composable
fun ScanRiseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
