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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.AnimeItem
import com.example.data.model.CharacterItem
import com.example.ui.theme.*

@Composable
fun AnimeWikiScreen(
    animeList: List<AnimeItem>,
    characters: List<CharacterItem>,
    isAnimeSubscribed: (String) -> Boolean = { false },
    onToggleSubscription: (String) -> Boolean = { false },
    onSimulateEpisodePush: (AnimeItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("الكل") }
    val genres = listOf("الكل", "أكشن", "مغامرات", "دراما", "غموض", "فانتازيا", "خوارق", "شونين", "كوميديا")
    var selectedAnimeDetail by remember { mutableStateOf<AnimeItem?>(null) }

    val filteredList = remember(searchQuery, selectedGenre, animeList) {
        animeList.filter { anime ->
            val matchQuery = searchQuery.isBlank() ||
                    anime.titleAr.contains(searchQuery, ignoreCase = true) ||
                    anime.titleEn.contains(searchQuery, ignoreCase = true)
            val matchGenre = selectedGenre == "الكل" || anime.genres.contains(selectedGenre)
            matchQuery && matchGenre
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("anime_wiki_screen")
    ) {
        // Search
        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في 48+ موسوعة أنمي كاملة...", color = TextMuted, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "بحث", tint = NeonCyan)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = BgCard,
                    unfocusedContainerColor = BgCard
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Genre filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { g ->
                val isChosen = selectedGenre == g
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isChosen) NeonCyan else BgCard)
                        .border(1.dp, if (isChosen) NeonCyan else BorderSubtle, RoundedCornerShape(8.dp))
                        .clickable { selectedGenre = g }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = g,
                        color = if (isChosen) BgDeepVoid else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Anime list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { anime ->
                val isSubscribed = isAnimeSubscribed(anime.id)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { selectedAnimeDetail = anime }
                        .testTag("wiki_item_${anime.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Poster Banner
                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 110.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BgCardElevated)
                        ) {
                            AsyncImage(
                                model = anime.bannerUrl,
                                contentDescription = anime.titleAr,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = anime.titleAr,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isSubscribed) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "تنبيهات مفعّلة",
                                            tint = NeonCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Rating", tint = OtakuGold, modifier = Modifier.size(14.dp))
                                    Text(text = "${anime.rating}", color = OtakuGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = anime.titleEn,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${anime.studio} • ${anime.year} • ${anime.episodes}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = anime.synopsis,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Anime Detail Modal
        selectedAnimeDetail?.let { anime ->
            AnimeDetailDialog(
                anime = anime,
                isSubscribed = isAnimeSubscribed(anime.id),
                onToggleSubscription = { onToggleSubscription(anime.id) },
                onSimulateEpisodePush = { onSimulateEpisodePush(anime) },
                onDismiss = { selectedAnimeDetail = null }
            )
        }
    }
}

@Composable
fun AnimeDetailDialog(
    anime: AnimeItem,
    isSubscribed: Boolean,
    onToggleSubscription: () -> Boolean,
    onSimulateEpisodePush: () -> Unit,
    onDismiss: () -> Unit
) {
    var subscribedState by remember { mutableStateOf(isSubscribed) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                .testTag("anime_detail_dialog")
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = anime.titleAr, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }
                }

                // Banner
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        AsyncImage(
                            model = anime.bannerUrl,
                            contentDescription = anime.titleAr,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Push Notifications Subscription Bar (FCM)
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (subscribedState) Color(0x2200F0FF) else BgCardElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (subscribedState) NeonCyan else BorderSubtle,
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (subscribedState) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                        contentDescription = "FCM Notifications",
                                        tint = if (subscribedState) NeonCyan else TextSecondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (subscribedState) "تنبيهات الحلقات مفعّلة ✅" else "تنبيهات الحلقات الجديدة (FCM)",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "إشعار دفع فوري عند توفر أي حلقة جديدة",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        subscribedState = onToggleSubscription()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (subscribedState) Color(0x33FB7185) else NeonCyan
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (subscribedState) "إلغاء التنبيه" else "تفعيل 🔔",
                                        color = if (subscribedState) CrimsonRed else BgDeepVoid,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Quick test button
                            Spacer(modifier = Modifier.height(8.dp))
                            FilledTonalButton(
                                onClick = onSimulateEpisodePush,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0x338B5CF6)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Test Push",
                                        tint = NeonPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "اختبار وصول إشعار Push Notification للحلقة الآن 📲",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Stats Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BgCardElevated)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "التقييم", color = TextMuted, fontSize = 10.sp)
                            Text(text = "${anime.rating} / 10", color = OtakuGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "إجماع الأوتاكو", color = TextMuted, fontSize = 10.sp)
                            Text(text = "${anime.fanScore} ⭐", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "الحلقات", color = TextMuted, fontSize = 10.sp)
                            Text(text = anime.episodes, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Synopsis
                item {
                    Text(text = "نبذة القصة:", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = anime.synopsis, color = TextSecondary, fontSize = 12.sp, lineHeight = 20.sp)
                }

                if (anime.fanConsensus.isNotBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x228B5CF6)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NeonPurple, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "رأي الجمهور وخلاصة المراجعات:", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = anime.fanConsensus, color = TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (anime.quote.isNotBlank()) {
                    item {
                        Text(text = "اقتباس شهير:", color = OtakuGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "\"${anime.quote}\"", color = TextPrimary, fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }
        }
    }
}
