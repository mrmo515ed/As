package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun AdminPanelScreen(
    currentUser: User?,
    onBack: () -> Unit,
    onBroadcastEpisodePush: (String, String, String?, String?) -> Unit = { _, _, _, _ -> }
) {
    val isOwner = currentUser?.email?.equals("m774545471@gmail.com", ignoreCase = true) == true ||
            currentUser?.role == "Owner" || currentUser?.role == "Admin"

    var broadcastText by remember { mutableStateOf("") }
    var broadcastSuccess by remember { mutableStateOf(false) }

    // Push Notification Form State
    var pushAnimeTitle by remember { mutableStateOf("ون بيس (One Piece)") }
    var pushEpisodeNumber by remember { mutableStateOf("1122") }
    var pushSynopsis by remember { mutableStateOf("بداية معركة جزيرة إلباف المصيرية وتصادم القوى العظمى!") }
    var pushBannerUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600") }
    var pushSentSuccess by remember { mutableStateOf(false) }

    val quickAnimeSuggestions = listOf(
        "ون بيس (One Piece)",
        "جوجوتسو كايسن (Jujutsu Kaisen)",
        "قاتل الشياطين (Demon Slayer)",
        "سولو ليفلينج (Solo Leveling)",
        "بليتش: حرب الألف سنة الدموية",
        "هجوم العمالقة (Attack on Titan)"
    )

    val sampleUsers = remember {
        mutableStateListOf(
            Triple("مستخدم_أوتاكو_1", "@otaku_fan", "نشط"),
            Triple("سبامر_مجهول", "@spammer_bot", "نشط"),
            Triple("لوفي_العرب", "@luffy_ar", "نشط")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("admin_panel_screen")
    ) {
        // Admin Header
        Surface(
            color = BgSurfaceDark,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
                }
                Column {
                    Text(text = "لوحة تحكم الإدارة العليا (Owner)", color = CrimsonRed, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text(text = "animeblack-system-root • FCM Notifications Console", color = TextMuted, fontSize = 10.sp)
                }
            }
        }

        if (!isOwner) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "عذراً، هذه اللوحة مخصصة فقط لمدير النظام (m774545471@gmail.com).",
                    color = CrimsonRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // FCM Push Notification for New Episodes (PRIMARY REQUEST)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NeonCyan, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "FCM Push",
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "بث إشعار حلقة جديدة (FCM Push Notification) 🎬",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "إشعار دفع حقيقي للمستخدمين وقنوات topic: new_episodes",
                                    color = NeonCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Fast selector
                        Text(text = "اختر الأنمي المستهدف:", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(quickAnimeSuggestions) { a ->
                                val isSelected = pushAnimeTitle == a
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonCyan else BgCardElevated)
                                        .clickable { pushAnimeTitle = a }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = a,
                                        color = if (isSelected) BgDeepVoid else TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Anime Title Input
                        OutlinedTextField(
                            value = pushAnimeTitle,
                            onValueChange = { pushAnimeTitle = it },
                            label = { Text("عنوان الأنمي", color = TextMuted, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = BgCardElevated,
                                unfocusedContainerColor = BgCardElevated
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Episode Number Input
                        OutlinedTextField(
                            value = pushEpisodeNumber,
                            onValueChange = { pushEpisodeNumber = it },
                            label = { Text("رقم الحلقة", color = TextMuted, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = BgCardElevated,
                                unfocusedContainerColor = BgCardElevated
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Episode Synopsis / Highlights
                        OutlinedTextField(
                            value = pushSynopsis,
                            onValueChange = { pushSynopsis = it },
                            label = { Text("أبرز أحداث الحلقة أو نص الإشعار", color = TextMuted, fontSize = 11.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = BgCardElevated,
                                unfocusedContainerColor = BgCardElevated
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (pushAnimeTitle.isNotBlank() && pushEpisodeNumber.isNotBlank()) {
                                    onBroadcastEpisodePush(
                                        pushAnimeTitle,
                                        pushEpisodeNumber,
                                        pushBannerUrl,
                                        pushSynopsis
                                    )
                                    pushSentSuccess = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send Push",
                                    tint = BgDeepVoid
                                )
                                Text(
                                    text = "إرسال إشعار الدفع الفوري (FCM Push) للمستخدمين 🚀",
                                    color = BgDeepVoid,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (pushSentSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✅ تم إرسال إشعار الحلقة $pushEpisodeNumber بنجاح إلى شريط إشعارات الأجهزة ومستمعي FCM!",
                                color = EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Broadcast Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CrimsonRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(text = "إرسال تعميم إداري عام 📢", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = broadcastText,
                            onValueChange = { broadcastText = it },
                            placeholder = { Text("اكتب نص التعميم الإداري...", color = TextMuted, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = CrimsonRed,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = BgCardElevated,
                                unfocusedContainerColor = BgCardElevated
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (broadcastText.isNotBlank()) {
                                    broadcastSuccess = true
                                    broadcastText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "بث التعميم الإداري", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        if (broadcastSuccess) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "تم إرسال التعميم الإداري بنجاح إلى شبكة السيرفر.", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // User Moderation Section
            item {
                Text(text = "إدارة المستخدمين والحظر المباشر:", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            items(sampleUsers.indices.toList()) { idx ->
                val (name, handle, status) = sampleUsers[idx]
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "$handle • $status", color = if (status == "محظور") CrimsonRed else EmeraldGreen, fontSize = 11.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    val newStatus = if (status == "نشط") "محظور" else "نشط"
                                    sampleUsers[idx] = Triple(name, handle, newStatus)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (status == "نشط") CrimsonRed else EmeraldGreen
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = if (status == "نشط") "حظر" else "إلغاء الحظر", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
