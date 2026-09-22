package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavItem(
    val route: String,
    val labelAr: String,
    val icon: ImageVector,
    val isCenterAction: Boolean = false
)

@Composable
fun AnimeBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem("home", "الرئيسية", Icons.Default.Home),
        NavItem("explore", "استكشاف", Icons.Default.Explore),
        NavItem("reels", "ريلز", Icons.Default.PlayCircle),
        NavItem("create", "نشر", Icons.Default.AddCircle, isCenterAction = true),
        NavItem("chat", "محادثات", Icons.Default.ChatBubble),
        NavItem("profile", "حسابي", Icons.Default.Person),
        NavItem("more", "المزيد", Icons.Default.Apps)
    )

    Surface(
        color = BgSurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderSubtle)
            .testTag("anime_bottom_nav")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                if (item.isCenterAction) {
                    // Elevated Center Action Button for Create Post/Reel
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AnimeGradient)
                            .clickable { onNavigate(item.route) }
                            .testTag("nav_btn_${item.route}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = item.labelAr,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onNavigate(item.route) }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("nav_btn_${item.route}")
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0x2200F0FF) else Color.Transparent)
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.labelAr,
                                tint = if (isSelected) NeonCyan else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = item.labelAr,
                            color = if (isSelected) NeonCyan else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
