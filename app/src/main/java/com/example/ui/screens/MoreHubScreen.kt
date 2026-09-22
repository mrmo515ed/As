package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.User
import com.example.ui.theme.*

data class HubMenuItem(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isOwnerOnly: Boolean = false
)

@Composable
fun MoreHubScreen(
    currentUser: User?,
    onNavigate: (String) -> Unit
) {
    val isOwner = currentUser?.email?.equals("m774545471@gmail.com", ignoreCase = true) == true ||
            currentUser?.role == "Owner" || currentUser?.role == "Admin"

    val menuItems = listOf(
        HubMenuItem("favorites", "المفضلة والمتابعة", "قائمتك الخاصة بالأنمي والمانجا المحفوظة", Icons.Default.Favorite, CrimsonRed),
        HubMenuItem("wiki", "موسوعة الأنمي", "48+ أنمي مع إجماع المراجعين", Icons.Default.MenuBook, NeonCyan),
        HubMenuItem("games", "ساحة المعارك والألعاب", "قتال الأبطال ضد الشياطين وجوائز حقيقية", Icons.Default.SportsEsports, FlameOrange),
        HubMenuItem("store", "متجر الأوتاكو", "إطارات، شارات، وألقاب مميزة", Icons.Default.ShoppingBag, OtakuGold),
        HubMenuItem("notifications", "مركز الإشعارات", "تنبيهات المنشورات والردود والجوائز", Icons.Default.Notifications, NeonPurple),
        HubMenuItem("admin", "لوحة الإدارة العليا", "تحكم شامل بالسيرفر والحظر والتعميمات", Icons.Default.AdminPanelSettings, CrimsonRed, isOwnerOnly = true),
        HubMenuItem("about", "عن أنمي بلاك", "منصة مجتمع الأوتاكو الأولى عربياً", Icons.Default.Info, TextSecondary)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .padding(14.dp)
            .testTag("more_hub_screen")
    ) {
        Text(
            text = "المزيد من أقسام أنمي بلاك",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
        Text(
            text = "استكشف كافة الخدمات والألعاب والموسوعات المتاحة",
            color = TextMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            items(menuItems) { item ->
                if (!item.isOwnerOnly || isOwner) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .border(1.dp, item.accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .clickable { onNavigate(item.id) }
                            .testTag("hub_btn_${item.id}")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(item.accentColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.titleAr,
                                    tint = item.accentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = item.titleAr,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = item.subtitleAr,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
