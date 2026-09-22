package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.ui.theme.*

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSubmit: (text: String, tags: List<String>, animeRef: String?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedAnime by remember { mutableStateOf("") }
    val availableTags = listOf("نقاش", "نظريات", "مراجعة", "فان_آرت", "أخبار", "اقتباسات", "حرق_محتمل")
    val selectedTags = remember { mutableStateListOf<String>() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                .testTag("create_post_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إنشاء منشور جديد",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Post Text Input
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("ماذا يدور في ذهنك حول عالم الأنمي اليوم؟", color = TextMuted) },
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
                        .height(130.dp)
                        .testTag("create_post_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Anime Reference Input
                OutlinedTextField(
                    value = selectedAnime,
                    onValueChange = { selectedAnime = it },
                    placeholder = { Text("اسم الأنمي المرتبط (اختياري)", color = TextMuted) },
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

                Spacer(modifier = Modifier.height(12.dp))

                // Tags selector
                Text(
                    text = "الوسوم والتصنيفات:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableTags) { tag ->
                        val isChosen = selectedTags.contains(tag)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isChosen) NeonCyan else BgCardElevated)
                                .clickable {
                                    if (isChosen) selectedTags.remove(tag) else selectedTags.add(tag)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                color = if (isChosen) BgDeepVoid else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onSubmit(text, selectedTags.toList(), selectedAnime.ifBlank { null })
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_post_btn")
                ) {
                    Text(
                        text = "نشر الآن (+25 XP)",
                        color = BgDeepVoid,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
