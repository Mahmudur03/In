package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FinanceDarkColorScheme = darkColorScheme(
    primary = BrightBluePrimary,
    onPrimary = Color.White,
    primaryContainer = CardNavySurface,
    onPrimaryContainer = TextPrimaryWhite,
    
    background = DarkNavyBackground,
    onBackground = TextPrimaryWhite,
    
    surface = CardNavySurface,
    onSurface = TextPrimaryWhite,
    surfaceVariant = DarkNavySurfaceLight,
    onSurfaceVariant = TextSecondarySlate,
    
    outline = BorderSlate,
    
    secondary = AccentTeal,
    onSecondary = DarkNavyBackground,
    
    tertiary = IncomeGreen,
    onTertiary = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme to match "Dark Navy (#0A192F)" design specs perfectly
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our intentional color scheme branding
    content: @Composable () -> Unit
) {
    // We intentionally ignore system theme & dynamicColor configurations here to strictly honor
    // the user's specific request for a Dominant Blue Navy Slate visual mode.
    MaterialTheme(
        colorScheme = FinanceDarkColorScheme,
        typography = Typography,
        content = content
    )
}
