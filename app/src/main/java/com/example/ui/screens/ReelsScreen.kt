package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Reel
import com.example.ui.theme.*

@Composable
fun ReelsScreen(
    reels: List<Reel>,
    onLikeReel: (String) -> Unit
) {
    if (reels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeepVoid),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "لا توجد مقاطع ريلز حالياً", color = TextMuted)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = reels[page]
            var isLiked by remember { mutableStateOf(reel.isLiked) }
            var likesCount by remember { mutableStateOf(reel.likesCount) }

            Box(modifier = Modifier.fillMaxSize()) {
                // Background visual backdrop simulating video feed
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1000",
                    contentDescription = reel.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark vignette overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x33000000),
                                    Color.Transparent,
                                    Color(0xCC000000)
                                )
                            )
                        )
                )

                // Bottom Content & Author info
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 80.dp, bottom = 90.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = reel.authorAvatar,
                            contentDescription = reel.authorName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, NeonCyan, CircleShape)
                        )

                        Text(
                            text = reel.authorName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "متابعة",
                                color = BgDeepVoid,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = reel.caption,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Sound",
                            tint = NeonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = reel.soundTitle,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Right Floating Actions (Like, Comment, Share, Sound Disc)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Like Action
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                isLiked = !isLiked
                                likesCount = if (isLiked) likesCount + 1 else likesCount - 1
                                onLikeReel(reel.id)
                            }
                            .testTag("reel_like_btn_${reel.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0x551A1F33)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) CrimsonRed else Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = "$likesCount",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Comment Action
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0x551A1F33)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ModeComment,
                                contentDescription = "Comment",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "${reel.commentsCount}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share Action
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0x551A1F33)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "مشاركة",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }

                    // Spinning Music Disc
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1F2538))
                            .border(2.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = "Disc",
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
