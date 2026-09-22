package com.example.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val avatar: String = "",
    val bio: String = "",
    val role: String = "عضو", // "Owner", "Admin", "عضو", "VIP"
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 380,
    val stars: Int = 16,
    val reputation: Int = 100,
    val isVerified: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val frame: String = "fire_aura",
    val title: String = "أوتاكو مبتدئ",
    val badges: List<String> = listOf("مؤسس", "محب الأنمي")
)

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val authorRole: String = "عضو",
    val isVerified: Boolean = false,
    val text: String = "",
    val mediaUrl: String? = null,
    val tags: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val animeRef: String? = null
)

data class Comment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val likesCount: Int = 0
)

data class Story(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val mediaUrl: String = "",
    val caption: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isSeen: Boolean = false
)

data class Reel(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorAvatar: String = "",
    val videoUrl: String = "",
    val caption: String = "",
    val soundTitle: String = "Anime Original Soundtrack - Theme",
    val likesCount: Int = 120,
    val commentsCount: Int = 15,
    val isLiked: Boolean = false
)

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val text: String = "",
    val mediaUrl: String? = null,
    val type: String = "text", // "text", "image", "voice", "sticker"
    val createdAt: Long = System.currentTimeMillis(),
    val isMe: Boolean = false,
    val isRead: Boolean = true
)

data class ChatConversation(
    val id: String = "",
    val name: String = "",
    val avatar: String = "",
    val lastMessage: String = "",
    val lastMessageTime: String = "الآن",
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val isWorld: Boolean = false,
    val isOnline: Boolean = true
)

data class ChatRequest(
    val id: String = "",
    val fromUserId: String = "",
    val fromUserName: String = "",
    val fromUserAvatar: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "pending"
)

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "system", // "like", "comment", "follow", "system", "reward"
    val timeAgo: String = "الآن",
    val isRead: Boolean = false,
    val iconName: String = "bell"
)

data class AnimeItem(
    val id: String = "",
    val titleAr: String = "",
    val titleEn: String = "",
    val year: String = "",
    val episodes: String = "",
    val rating: Double = 9.0,
    val tier: String = "S",
    val genres: List<String> = emptyList(),
    val studio: String = "",
    val status: String = "مكتمل",
    val color1: String = "#00F0FF",
    val color2: String = "#8B5CF6",
    val bannerUrl: String = "",
    val fanScore: Double = 9.5,
    val fanReviewsCount: Int = 1200,
    val fanConsensus: String = "",
    val quote: String = "",
    val synopsis: String = ""
)

data class CharacterItem(
    val id: String = "",
    val nameAr: String = "",
    val nameEn: String = "",
    val animeName: String = "",
    val role: String = "بطل القصة",
    val quote: String = "",
    val votes: Int = 0,
    val avatarUrl: String = ""
)

data class QuoteItem(
    val id: String = "",
    val quote: String = "",
    val anime: String = "",
    val character: String = "",
    val color: String = "#00F0FF"
)

data class StoreItem(
    val id: String = "",
    val name: String = "",
    val type: String = "frame", // "frame", "badge", "title", "bubble"
    val priceCoins: Int = 100,
    val priceStars: Int = 0,
    val icon: String = "sparkles",
    val description: String = "",
    val previewColor: String = "#00F0FF",
    val isPurchased: Boolean = false
)

data class WorldItem(
    val id: String = "",
    val name: String = "",
    val membersCount: Int = 150,
    val bannerUrl: String = "",
    val description: String = "",
    val tag: String = "عام",
    val isJoined: Boolean = false
)

data class GroupItem(
    val id: String = "",
    val name: String = "",
    val membersCount: Int = 42,
    val avatarUrl: String = "",
    val description: String = "",
    val leaderName: String = ""
)

data class GameCharacter(
    val id: String = "",
    val name: String = "",
    val anime: String = "",
    val role: String = "مهاجم",
    val element: String = "نار",
    val rarity: String = "SSR",
    val baseAtk: Int = 120,
    val baseDef: Int = 80,
    val baseHp: Int = 1000,
    val currentLevel: Int = 1,
    val isUnlocked: Boolean = false,
    val unlockCostCoins: Int = 500,
    val skillName: String = "الهجوم الخارق",
    val skillDescription: String = "ضربة قاضية تسبب 250% ضرر",
    val avatarUrl: String = ""
)

data class GameMission(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val rewardCoins: Int = 50,
    val rewardStars: Int = 1,
    val currentProgress: Int = 0,
    val totalProgress: Int = 1,
    val isClaimed: Boolean = false
)
