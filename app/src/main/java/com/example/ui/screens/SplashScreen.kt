package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isLoggedIn: Boolean,
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    // Pulse animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "splash_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    LaunchedEffect(Unit) {
        // Smooth delay to present branding and allow Firebase Auth state check
        delay(1800)
        if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background glow circles
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.15f * glowAlpha),
                            NeonPurple.copy(alpha = 0.08f * glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // App Logo Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(BgCardElevated, BgSurfaceDark)
                        )
                    )
                    .border(2.dp, AnimeGradient, CircleShape)
                    .testTag("splash_logo")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "شعار أنمي بلاك",
                        tint = NeonCyan,
                        modifier = Modifier.size(46.dp)
                    )
                    Text(
                        text = "AB",
                        color = OtakuGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name with Otaku Kanji
            Text(
                text = "アニメブラック",
                color = NeonCyan.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "أنمي بلاك",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Anime Black • عالم الأوتاكو الأسطوري",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Badges row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SplashBadge(text = "مجتمع عربي", color = NeonCyan)
                SplashBadge(text = "مبارزات ونقابات", color = NeonPurple)
                SplashBadge(text = "موسوعة الأنمي", color = FlameOrange)
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Clean modern loading indicator
            CircularProgressIndicator(
                color = NeonCyan,
                strokeWidth = 2.5.dp,
                trackColor = BorderSubtle,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("splash_loading_indicator")
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "جاري الاتصال والتحميل...",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Bottom version note
        Text(
            text = "Anime Black v1.0.0 • Powered by Firebase",
            color = TextMuted.copy(alpha = 0.5f),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun SplashBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
