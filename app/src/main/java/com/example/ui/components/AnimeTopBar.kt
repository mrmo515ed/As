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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.theme.*

@Composable
fun AnimeTopBar(
    user: User?,
    unreadNotificationsCount: Int,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Surface(
        color = BgSurfaceDark,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderSubtle)
            .testTag("anime_top_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AB",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
                Column {
                    Text(
                        text = "ANIME BLACK",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "أنمي بلاك",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right side: Economy chips, Admin Badge & Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Admin Badge (if owner/admin)
                if (user?.role == "Owner" || user?.role == "Admin") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FB7185))
                            .border(1.dp, CrimsonRed, RoundedCornerShape(6.dp))
                            .clickable { onAdminClick() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("admin_badge_btn")
                    ) {
                        Text(
                            text = "الإدارة",
                            color = CrimsonRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Coins Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgCardElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = OtakuGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${user?.coins ?: 0}",
                        color = OtakuGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Stars Chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgCardElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = NeonPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${user?.stars ?: 0}",
                        color = NeonPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Search Icon
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = TextSecondary
                    )
                }

                // Notifications Icon with Badge
                Box(
                    modifier = Modifier.testTag("notifications_btn")
                ) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "الإشعارات",
                            tint = if (unreadNotificationsCount > 0) NeonCyan else TextSecondary
                        )
                    }
                    if (unreadNotificationsCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(CrimsonRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadNotificationsCount",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
