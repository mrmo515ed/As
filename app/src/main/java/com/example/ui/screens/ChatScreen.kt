package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    conversations: List<ChatConversation>,
    messages: Map<String, List<ChatMessage>>,
    onSendMessage: (chatId: String, text: String) -> Unit,
    onOpenChat: (chatId: String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("محادثات خاصة", "النقابات والعوالم", "طلبات المراسلة")
    var activeChat by remember { mutableStateOf<ChatConversation?>(null) }

    if (activeChat != null) {
        val chat = activeChat!!
        val currentMessages = messages[chat.id] ?: emptyList()

        LaunchedEffect(chat.id) {
            onOpenChat(chat.id)
        }

        ChatRoomView(
            chat = chat,
            messages = currentMessages,
            onBack = { activeChat = null },
            onSendMessage = { text -> onSendMessage(chat.id, text) }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDeepVoid)
                .testTag("chat_screen")
        ) {
            // Header & Tabs
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

            // Conversations List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                val filteredList = when (selectedTab) {
                    0 -> conversations.filter { !it.isGroup && !it.isWorld }
                    1 -> conversations.filter { it.isGroup || it.isWorld }
                    else -> emptyList()
                }

                if (selectedTab == 2) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "لا توجد طلبات مراسلة معلقة حالياً", color = TextMuted)
                        }
                    }
                } else if (filteredList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "لا توجد محادثات نشطة هنا", color = TextMuted)
                        }
                    }
                } else {
                    items(filteredList) { convo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeChat = convo
                                    onOpenChat(convo.id)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("chat_item_${convo.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Avatar with Online dot
                            Box(
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(BgCardElevated)
                                        .border(1.dp, BorderSubtle, CircleShape)
                                ) {
                                    AsyncImage(
                                        model = convo.avatar,
                                        contentDescription = convo.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                if (convo.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen)
                                            .border(2.dp, BgDeepVoid, CircleShape)
                                    )
                                }
                            }

                            // Info
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = convo.name,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = convo.lastMessageTime,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = convo.lastMessage.ifEmpty { "انقر لبدء الدردشة..." },
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (convo.unreadCount > 0) {
                                        Badge(
                                            containerColor = NeonCyan,
                                            contentColor = BgDeepVoid
                                        ) {
                                            Text(
                                                text = "${convo.unreadCount}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(
                            color = BorderSubtle,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 74.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRoomView(
    chat: ChatConversation,
    messages: List<ChatMessage>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("chat_room_view")
    ) {
        // Room Top Bar
        Surface(
            color = BgSurfaceDark,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "رجوع",
                        tint = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    AsyncImage(
                        model = chat.avatar,
                        contentDescription = chat.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chat.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (chat.isOnline) "متصل الآن 🟢" else "غير متصل",
                        color = if (chat.isOnline) EmeraldGreen else TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.isMe
                val timeStr = remember(msg.createdAt) {
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.createdAt))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isMe) 14.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 14.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) Color(0xFF1E2A4A) else BgCard
                        ),
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .border(
                                width = 1.dp,
                                color = if (isMe) NeonCyan.copy(alpha = 0.5f) else BorderSubtle,
                                shape = RoundedCornerShape(14.dp)
                            )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (!isMe && msg.senderName.isNotBlank()) {
                                Text(
                                    text = msg.senderName,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                            Text(
                                text = msg.text,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timeStr,
                                color = TextMuted,
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Input Bar
        Surface(
            color = BgSurfaceDark,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("اكتب رسالتك للأوتاكو...", color = TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = BgCard,
                        unfocusedContainerColor = BgCard
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = NeonCyan),
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .testTag("send_msg_btn")
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
