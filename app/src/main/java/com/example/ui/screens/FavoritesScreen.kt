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
import coil.compose.AsyncImage
import com.example.data.model.FavoriteItem
import com.example.ui.theme.*

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteItem>,
    onRemoveFavorite: (String) -> Unit,
    onOpenAnimeWiki: (String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    var selectedTypeFilter by remember { mutableStateOf("الكل") }
    val filters = listOf("الكل", "أنمي", "مانجا")

    val filteredList = remember(favorites, selectedTypeFilter) {
        when (selectedTypeFilter) {
            "أنمي" -> favorites.filter { it.type == "anime" }
            "مانجا" -> favorites.filter { it.type == "manga" }
            else -> favorites
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("favorites_screen")
    ) {
        // Header
        Surface(
            color = BgSurfaceDark,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "رجوع",
                                tint = TextPrimary
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33FB7185)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "المفضلة",
                            tint = CrimsonRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "المفضلة وقائمة المتابعة",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "تخزين سحابي فوري ومباشر على Firestore",
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                // Count Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgCardElevated)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${favorites.size} عنصر",
                        color = OtakuGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { f ->
                val isSelected = selectedTypeFilter == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) CrimsonRed else BgCard)
                        .border(1.dp, if (isSelected) CrimsonRed else BorderSubtle, RoundedCornerShape(10.dp))
                        .clickable { selectedTypeFilter = f }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("filter_fav_$f")
                ) {
                    Text(
                        text = f,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Content
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x1AFB7185)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "قائمة فارغة",
                            tint = CrimsonRed.copy(alpha = 0.6f),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Text(
                        text = "قائمتك المفضلة فارغة حالياً!",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "اضغط على رمز القلب في موسوعة الأنمي أو المانجا لحفظ أعمالك المفضلة والمزامنة مع حسابك.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp, top = 4.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    FavoriteItemCard(
                        item = item,
                        onRemove = { onRemoveFavorite(item.targetId) },
                        onClick = { onOpenAnimeWiki(item.targetId) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: FavoriteItem,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("fav_card_${item.targetId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster Image
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 95.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BgCardElevated)
            ) {
                AsyncImage(
                    model = item.bannerUrl.ifBlank { "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=300" },
                    contentDescription = item.titleAr,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Type Badge (anime or manga)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (item.type == "manga") FlameOrange else NeonCyan)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (item.type == "manga") "مانجا" else "أنمي",
                        color = BgDeepVoid,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.titleAr,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = OtakuGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${item.rating}",
                            color = OtakuGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (item.titleEn.isNotBlank()) {
                    Text(
                        text = item.titleEn,
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x2200F0FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.genre.ifBlank { "شونين" },
                            color = NeonCyan,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "• ${item.status}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Remove Button
            IconButton(
                onClick = onRemove,
                modifier = Modifier.testTag("remove_fav_${item.targetId}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف من المفضلة",
                    tint = CrimsonRed
                )
            }
        }
    }
}
