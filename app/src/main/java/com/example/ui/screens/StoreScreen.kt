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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.StoreItem
import com.example.data.model.User
import com.example.ui.theme.*

@Composable
fun StoreScreen(
    user: User?,
    storeItems: List<StoreItem>,
    onBuyItem: (StoreItem) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }
    val categories = listOf("الكل", "frame", "badge", "title", "bubble")

    val filteredItems = remember(selectedCategory, storeItems) {
        if (selectedCategory == "الكل") storeItems
        else storeItems.filter { it.type == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("store_screen")
    ) {
        // Balance Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .border(1.dp, OtakuGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "متجر كنز الأوتاكو 💎", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "خصّص ملفك الشخصي بأندر الإطارات والألقاب والشارات", color = TextMuted, fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FBBF24))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Coins", tint = OtakuGold, modifier = Modifier.size(16.dp))
                        Text(text = "${user?.coins ?: 0}", color = OtakuGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x3300F0FF))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Stars", tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Text(text = "${user?.stars ?: 0}", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Category Filter Tabs
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val label = when (cat) {
                    "الكل" -> "الكل"
                    "frame" -> "إطارات الرمزية 🖼️"
                    "badge" -> "شارات الشرف 🎖️"
                    "title" -> "الألقاب الأسطورية 👑"
                    "bubble" -> "فقاعات المحادثة 💬"
                    else -> cat
                }
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonCyan else BgCard)
                        .border(1.dp, if (isSelected) NeonCyan else BorderSubtle, RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) BgDeepVoid else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Store Items List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredItems) { item ->
                val canAfford = (user?.coins ?: 0) >= item.priceCoins && (user?.stars ?: 0) >= item.priceStars
                val isLegendary = item.priceCoins >= 500

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .testTag("store_item_${item.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BgCardElevated)
                                    .border(1.dp, if (isLegendary) OtakuGold else NeonCyan, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (item.type) {
                                        "frame" -> "🖼️"
                                        "badge" -> "🎖️"
                                        "title" -> "👑"
                                        "bubble" -> "💬"
                                        else -> "✨"
                                    },
                                    fontSize = 22.sp
                                )
                            }

                            Column {
                                Text(text = item.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = if (isLegendary) "أسطوري 🌟" else "مميز ✨",
                                    color = if (isLegendary) OtakuGold else NeonPurple,
                                    fontSize = 11.sp
                                )
                                if (item.description.isNotBlank()) {
                                    Text(
                                        text = item.description,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        if (item.isPurchased) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x2210B981))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Purchased", tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                    Text(text = "مملوك", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Button(
                                onClick = { onBuyItem(item) },
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isLegendary) OtakuGold else NeonCyan,
                                    disabledContainerColor = BgCardElevated
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Price", tint = BgDeepVoid, modifier = Modifier.size(14.dp))
                                    Text(text = "${item.priceCoins}", color = BgDeepVoid, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
