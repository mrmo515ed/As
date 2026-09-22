package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AnimeBlackDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF06070B),
    primaryContainer = Color(0xFF003840),
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2C1654),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = OtakuGold,
    onTertiary = Color(0xFF332000),
    tertiaryContainer = Color(0xFF4D3300),
    onTertiaryContainer = OtakuGold,
    background = BgDeepVoid,
    onBackground = TextPrimary,
    surface = BgSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = CrimsonRed
)

@Composable
fun AnimeBlackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AnimeBlackDarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AnimeBlackTheme(darkTheme = true, content = content)
}
