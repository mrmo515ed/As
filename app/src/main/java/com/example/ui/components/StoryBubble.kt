package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Story
import com.example.ui.theme.*

@Composable
fun StoryBubble(
    story: Story?,
    isAddStory: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(if (isAddStory) BgCardElevated else Color.Transparent)
                .border(
                    width = 2.dp,
                    brush = if (story?.isSeen == false) AnimeGradient else FireGradient,
                    shape = CircleShape
                )
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isAddStory) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(BgCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة قصة",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                AsyncImage(
                    model = story?.userAvatar,
                    contentDescription = story?.userName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isAddStory) "قصتي" else story?.userName ?: "",
            color = if (story?.isSeen == false) TextPrimary else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (story?.isSeen == false) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
