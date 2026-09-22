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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
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
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.model.User
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.PostCard
import com.example.ui.components.StoryBubble
import com.example.ui.components.StoryViewerDialog
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    user: User?,
    posts: List<Post>,
    stories: List<Story>,
    onLikePost: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onCreatePost: (String, List<String>, String?) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("الكل") }
    val filters = listOf("الكل", "متابعة", "أنمي", "مانجا", "نظريات", "أخبار")
    var showCreateDialog by remember { mutableStateOf(false) }
    var activeCommentPost by remember { mutableStateOf<Post?>(null) }
    var activeStoryView by remember { mutableStateOf<Story?>(null) }

    val filteredPosts = remember(selectedFilter, posts) {
        if (selectedFilter == "الكل") posts
        else posts.filter { it.tags.contains(selectedFilter) || it.animeRef == selectedFilter }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("home_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Stories Carousel
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // My Story / Add Story button
                        item {
                            StoryBubble(
                                story = null,
                                isAddStory = true,
                                onClick = onNavigateToCreate
                            )
                        }

                        // Friends & Otaku Stories
                        items(stories) { story ->
                            StoryBubble(
                                story = story,
                                isAddStory = false,
                                onClick = { activeStoryView = story }
                            )
                        }
                    }
                }
            }

            // 2. Quick Post Trigger Box
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { showCreateDialog = true }
                        .testTag("quick_post_box")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "اكتب",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "شارك رأيك أو نظريتك مع الأوتاكو...",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2200F0FF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "نشر +25 XP",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Feed Category Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filters) { f ->
                        val isSelected = selectedFilter == f
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NeonCyan else BgCard)
                                .border(1.dp, if (isSelected) NeonCyan else BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable { selectedFilter = f }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = f,
                                color = if (isSelected) BgDeepVoid else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 4. Posts Stream
            if (filteredPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد منشورات في هذا التصنيف حالياً.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post.id) },
                        onCommentClick = { activeCommentPost = post },
                        onShareClick = { /* Share handled */ },
                        onAuthorClick = { /* Profile view */ }
                    )
                }
            }
        }

        // Create Post Dialog
        if (showCreateDialog) {
            CreatePostDialog(
                onDismiss = { showCreateDialog = false },
                onSubmit = { text, tags, animeRef ->
                    onCreatePost(text, tags, animeRef)
                }
            )
        }

        // Comments Bottom Sheet
        activeCommentPost?.let { p ->
            CommentBottomSheet(
                post = p,
                onDismiss = { activeCommentPost = null },
                onAddComment = { text ->
                    onAddComment(p.id, text)
                }
            )
        }

        // Story Preview Modal
        activeStoryView?.let { story ->
            StoryViewerDialog(
                story = story,
                onDismiss = { activeStoryView = null },
                onLikeStory = {
                    // Like story interaction
                }
            )
        }
    }
}
