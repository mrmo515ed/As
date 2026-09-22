package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import coil.compose.AsyncImage
import com.example.data.model.AnimeItem
import com.example.data.model.CharacterItem
import com.example.data.model.WorldItem
import com.example.ui.theme.*

@Composable
fun ExploreScreen(
    animeList: List<AnimeItem>,
    characters: List<CharacterItem>,
    worlds: List<WorldItem>,
    onAnimeClick: (AnimeItem) -> Unit,
    onWorldClick: (WorldItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("الكل") }
    val moods = listOf("الكل", "حماسي ودموي", "نفسي وغامض", "مغامرات ملحمية", "كوميدي ساخر")

    val filteredAnime = remember(searchQuery, selectedMood, animeList) {
        animeList.filter { anime ->
            val matchesQuery = searchQuery.isBlank() ||
                    anime.titleAr.contains(searchQuery, ignoreCase = true) ||
                    anime.titleEn.contains(searchQuery, ignoreCase = true) ||
                    anime.genres.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesMood = when (selectedMood) {
                "حماسي ودموي" -> anime.genres.any { it in listOf("أكشن", "شونين", "خوارق") }
                "نفسي وغامض" -> anime.genres.any { it in listOf("غموض", "نفسي", "دراما", "سينين") }
                "مغامرات ملحمية" -> anime.genres.any { it in listOf("مغامرات", "فانتازيا") }
                "كوميدي ساخر" -> anime.genres.any { it in listOf("كوميديا") }
                else -> true
            }

            matchesQuery && matchesMood
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("explore_screen")
    ) {
        // 1. Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث عن أنمي، شخصية، استوديو...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = NeonCyan
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Text("مسح", color = CrimsonRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
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
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_bar")
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 2. AI Mood Recommendation Chips
            item {
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "اقتراحات الذكاء الاصطناعي حسب مزاجك:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(moods) { mood ->
                            val isChosen = selectedMood == mood
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isChosen) NeonPurple else BgCard)
                                    .border(1.dp, if (isChosen) NeonPurple else BorderSubtle, RoundedCornerShape(10.dp))
                                    .clickable { selectedMood = mood }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = mood,
                                    color = if (isChosen) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 3. Characters Spotlight
            if (characters.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "شخصيات الأسبوع الأكثر شعبية ⭐",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(characters.take(10)) { char ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = BgCard),
                                    modifier = Modifier
                                        .width(130.dp)
                                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .background(BgCardElevated)
                                                .border(1.dp, NeonCyan, CircleShape)
                                        ) {
                                            AsyncImage(
                                                model = char.avatarUrl,
                                                contentDescription = char.nameAr,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = char.nameAr,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = char.animeName,
                                            color = TextMuted,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Votes",
                                                tint = OtakuGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "${char.votes}",
                                                color = OtakuGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Top Anime Catalog Grid Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "موسوعة الأنمي المختارة (${filteredAnime.size})",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "مرتبة حسب التقييم العالمي",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // 5. Anime Grid Cards
            items(filteredAnime.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { anime ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BgCard),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                                .clickable { onAnimeClick(anime) }
                                .testTag("anime_card_${anime.id}")
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                ) {
                                    AsyncImage(
                                        model = anime.bannerUrl,
                                        contentDescription = anime.titleAr,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Tier / Rating Badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xCC06070B))
                                            .border(0.5.dp, OtakuGold, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Rating",
                                                tint = OtakuGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "${anime.rating}",
                                                color = OtakuGold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // Details
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = anime.titleAr,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = anime.titleEn,
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        anime.genres.take(2).forEach { g ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(BgCardElevated)
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = g,
                                                    color = TextSecondary,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Spacer if odd item count
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
