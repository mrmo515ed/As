package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.Post
import com.example.data.model.User
import com.example.ui.components.OtakuCardView
import com.example.ui.components.PostCard
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: User?,
    myPosts: List<Post>,
    favorites: List<com.example.data.model.FavoriteItem> = emptyList(),
    onRemoveFavorite: (String) -> Unit = {},
    onOpenAnimeWiki: (String) -> Unit = {},
    onUpdateProfile: (name: String, username: String, bio: String) -> Unit,
    onLogout: () -> Unit,
    onLikePost: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("منشوراتي", "المفضلة (${favorites.size})", "الإنجازات", "الشارات")
    var showEditDialog by remember { mutableStateOf(false) }

    if (user == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeepVoid)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "أنت تتصفح كزائر حالياً",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "سجل دخولك أو أنشئ حسابك لحفظ مستواك، شاراتك، وبطاقتك الرقمية الأسطورية.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "تسجيل الدخول / إنشاء حساب",
                            color = BgDeepVoid,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("profile_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Digital Otaku Card
            item {
                Box(modifier = Modifier.padding(14.dp)) {
                    OtakuCardView(user = user)
                }
            }

            // 2. Action Buttons: Edit Profile & Logout
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showEditDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .testTag("edit_profile_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Text(text = "تعديل الملف الشخصي", color = TextPrimary, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FB7185)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .border(1.dp, CrimsonRed, RoundedCornerShape(12.dp))
                            .testTag("logout_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout", tint = CrimsonRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // 3. User Bio Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "نبذة أوتاكو:",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.bio.ifBlank { "لا توجد نبذة شخصية حتى الآن." },
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${user.followersCount}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "المتابعون", color = TextMuted, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${user.followingCount}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "يتابع", color = TextMuted, fontSize = 11.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${myPosts.size}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(text = "المنشورات", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // 4. Content Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = BgSurfaceDark,
                    contentColor = NeonCyan,
                    divider = { HorizontalDivider(color = BorderSubtle) }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTab == index) NeonCyan else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // 5. Tab Content
            when (selectedTab) {
                0 -> {
                    if (myPosts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "لم تنشر أي منشورات بعد. شارك رأيك الآن!", color = TextMuted)
                            }
                        }
                    } else {
                        items(myPosts) { post ->
                            PostCard(
                                post = post,
                                onLikeClick = { onLikePost(post.id) },
                                onCommentClick = {},
                                onShareClick = {},
                                onAuthorClick = {}
                            )
                        }
                    }
                }
                1 -> {
                    if (favorites.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "قائمتك المفضلة فارغة حالياً.", color = TextMuted)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = "يمكنك إضافة الأنمي والمانجا من شاشة الموسوعة بنقرة واحدة.", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        items(favorites, key = { it.id }) { favItem ->
                            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                                FavoriteItemCard(
                                    item = favItem,
                                    onRemove = { onRemoveFavorite(favItem.targetId) },
                                    onClick = { onOpenAnimeWiki(favItem.targetId) }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                "أول منشور في المنصة" to "مكتمل (+50 XP)",
                                "الوصول للمستوى 10" to "مكتمل (+100 XP)",
                                "الفوز بأول معركة في الساحة" to "مكتمل (+250 XP)",
                                "امتلاك 3 إطارات نادرة" to "قيد التقدم (1/3)"
                            ).forEach { (title, status) ->
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
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = status, color = if (status.contains("مكتمل")) EmeraldGreen else OtakuGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(text = "شارات الشرف المعلقة:", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                user.badges.forEach { b ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0x3300F0FF))
                                            .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = "🎖️ $b", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit Profile Dialog
        if (showEditDialog) {
            EditProfileDialog(
                currentUser = user,
                onDismiss = { showEditDialog = false },
                onSave = { name, username, bio ->
                    onUpdateProfile(name, username, bio)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onSave: (name: String, username: String, bio: String) -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var username by remember { mutableStateOf(currentUser.username) }
    var bio by remember { mutableStateOf(currentUser.bio) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "تعديل الملف الشخصي",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("الاسم الكامل", color = TextMuted) },
                    singleLine = true,
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

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("اسم المستخدم (@)", color = TextMuted) },
                    singleLine = true,
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

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("النبذة التعريفية", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = BgCardElevated,
                        unfocusedContainerColor = BgCardElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(name, username, bio) },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("حفظ التغييرات", color = BgDeepVoid, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
