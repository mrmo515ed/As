package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Anime Black Design System Colors
val BgDeepVoid = Color(0xFF06070B)
val BgSurfaceDark = Color(0xFF0A0C14)
val BgCard = Color(0xFF121624)
val BgCardElevated = Color(0xFF1A1F33)
val BorderSubtle = Color(0xFF222942)
val BorderGlow = Color(0xFF00F0FF)

// Accents
val NeonCyan = Color(0xFF00F0FF)
val NeonPurple = Color(0xFF8B5CF6)
val NeonPink = Color(0xFFEC4899)
val OtakuGold = Color(0xFFFBBF24)
val FlameOrange = Color(0xFFF97316)
val CrimsonRed = Color(0xFFFB7185)
val EmeraldGreen = Color(0xFF10B981)

// Typography
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Gradients
val AnimeGradient = Brush.horizontalGradient(
    colors = listOf(NeonCyan, NeonPurple, NeonPink)
)

val FireGradient = Brush.horizontalGradient(
    colors = listOf(FlameOrange, CrimsonRed, NeonPink)
)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(OtakuGold, Color(0xFFF59E0B), Color(0xFFD97706))
)

val CardGlowBrush = Brush.linearGradient(
    colors = listOf(Color(0x3300F0FF), Color(0x118B5CF6))
)
