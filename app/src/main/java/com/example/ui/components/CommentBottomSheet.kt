package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.ui.theme.*

@Composable
fun CommentBottomSheet(
    post: Post,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val sampleComments = remember {
        mutableStateListOf(
            Comment("c1", post.id, "u1", "ساسكي أوتشيها", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150", "تحليل منطقي جداً، أتفق معك تماماً في هذه النقطة.", System.currentTimeMillis() - 1000 * 60 * 15, 8),
            Comment("c2", post.id, "u2", "روبين نيكو", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150", "ربما هناك معنى تاريخي مخفي في البونيغليف لم نكتشفه بعد.", System.currentTimeMillis() - 1000 * 60 * 5, 14)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                .testTag("comments_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "التعليقات والمناقشات (${sampleComments.size})",
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

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Comments List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sampleComments) { comment ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = comment.authorAvatar,
                                contentDescription = comment.authorName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, BorderSubtle, CircleShape)
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BgCardElevated)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = comment.authorName,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "منذ قليل",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment.text,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("اكتب تعليقك...", color = TextMuted, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = BgCardElevated,
                            unfocusedContainerColor = BgCardElevated
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("comment_input")
                    )

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                sampleComments.add(
                                    Comment(
                                        id = "c_${System.currentTimeMillis()}",
                                        postId = post.id,
                                        authorId = "u_me",
                                        authorName = "أنت",
                                        authorAvatar = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=150",
                                        text = commentText
                                    )
                                )
                                onAddComment(commentText)
                                commentText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = NeonCyan),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .testTag("send_comment_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "إرسال",
                            tint = BgDeepVoid
                        )
                    }
                }
            }
        }
    }
}
