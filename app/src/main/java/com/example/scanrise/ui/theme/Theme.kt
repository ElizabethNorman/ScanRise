package com.example.scanrise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SoftBrass,
    onPrimary = Night,
    secondary = SoftBrass,
    background = Night,
    surface = NightRaised,
    surfaceVariant = NightRaised,
    onBackground = Parchment,
    onSurface = Parchment,
    outline = Slate
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
