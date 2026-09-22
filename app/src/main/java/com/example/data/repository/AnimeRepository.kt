package com.example.data.repository

import android.content.Context
import com.example.data.firebase.FirestoreManager
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class AnimeRepository(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    val firestoreManager = FirestoreManager()

    // Current User Session (Managed by FirebaseAuth)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Comments per post
    private val _postComments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val postComments: StateFlow<Map<String, List<Comment>>> = _postComments.asStateFlow()

    // Feed Posts
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Stories
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // Reels
    private val _reels = MutableStateFlow<List<Reel>>(emptyList())
    val reels: StateFlow<List<Reel>> = _reels.asStateFlow()

    // Chats & Conversations
    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    // Anime Encyclopedia
    private val _animeList = MutableStateFlow<List<AnimeItem>>(emptyList())
    val animeList: StateFlow<List<AnimeItem>> = _animeList.asStateFlow()

    // Characters
    private val _characters = MutableStateFlow<List<CharacterItem>>(emptyList())
    val characters: StateFlow<List<CharacterItem>> = _characters.asStateFlow()

    // Quotes
    private val _quotes = MutableStateFlow<List<QuoteItem>>(emptyList())
    val quotes: StateFlow<List<QuoteItem>> = _quotes.asStateFlow()

    // Store & Economy
    private val _storeItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val storeItems: StateFlow<List<StoreItem>> = _storeItems.asStateFlow()

    // Anime Worlds
    private val _worlds = MutableStateFlow<List<WorldItem>>(emptyList())
    val worlds: StateFlow<List<WorldItem>> = _worlds.asStateFlow()

    // Groups
    private val _groups = MutableStateFlow<List<GroupItem>>(emptyList())
    val groups: StateFlow<List<GroupItem>> = _groups.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Subscribed Anime for Push Notifications
    private val _subscribedAnimeIds = MutableStateFlow<Set<String>>(setOf("a1", "a3"))
    val subscribedAnimeIds: StateFlow<Set<String>> = _subscribedAnimeIds.asStateFlow()

    // Favorites & Watchlist (Synced with Firestore)
    private val _favorites = MutableStateFlow<List<FavoriteItem>>(emptyList())
    val favorites: StateFlow<List<FavoriteItem>> = _favorites.asStateFlow()

    // Games Suite
    private val _gameCharacters = MutableStateFlow<List<GameCharacter>>(emptyList())
    val gameCharacters: StateFlow<List<GameCharacter>> = _gameCharacters.asStateFlow()

    private val _gameMissions = MutableStateFlow<List<GameMission>>(emptyList())
    val gameMissions: StateFlow<List<GameMission>> = _gameMissions.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        scope.launch {
            loadSeedDataFromAssets()
            initDefaultPosts()
            initDefaultStories()
            initDefaultReels()
            initDefaultChats()
            initDefaultWorldsAndGroups()
            initDefaultNotifications()
            initDefaultGameData()

            // Synchronize with live Firestore collection "posts"
            firestoreManager.startPostsListener { remotePosts ->
                if (remotePosts.isNotEmpty()) {
                    val remoteIds = remotePosts.map { it.id }.toSet()
                    val filteredLocal = _posts.value.filterNot { remoteIds.contains(it.id) }
                    _posts.value = remotePosts + filteredLocal
                }
            }

            // Synchronize with live Firestore collection "notifications"
            firestoreManager.startNotificationsListener { remoteNotifs ->
                if (remoteNotifs.isNotEmpty()) {
                    val remoteIds = remoteNotifs.map { it.id }.toSet()
                    val filteredLocal = _notifications.value.filterNot { remoteIds.contains(it.id) }
                    _notifications.value = remoteNotifs + filteredLocal
                }
            }
        }
    }

    private fun loadSeedDataFromAssets() {
        try {
            val inputStream = context.assets.open("seed_data.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // Parse Anime
            val animeArr = root.optJSONArray("anime")
            val parsedAnime = mutableListOf<AnimeItem>()
            if (animeArr != null) {
                for (i in 0 until animeArr.length()) {
                    val obj = animeArr.getJSONObject(i)
                    val genresList = mutableListOf<String>()
                    val gArr = obj.optJSONArray("g")
                    if (gArr != null) {
                        for (g in 0 until gArr.length()) genresList.add(gArr.getString(g))
                    }
                    parsedAnime.add(
                        AnimeItem(
                            id = obj.optString("id", "a_$i"),
                            titleAr = obj.optString("n", "أنمي"),
                            titleEn = obj.optString("en", ""),
                            year = obj.optString("y", "2024"),
                            episodes = obj.optString("ep", "12 حلقة"),
                            rating = obj.optDouble("r", 8.8),
                            tier = obj.optString("tier", "S"),
                            genres = genresList,
                            studio = obj.optString("st", "استوديو رائد"),
                            status = obj.optString("stt", "مكتمل"),
                            color1 = obj.optString("c1", "#00F0FF"),
                            color2 = obj.optString("c2", "#8B5CF6"),
                            bannerUrl = obj.optString("banner", "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600"),
                            fanScore = obj.optDouble("fanScore", 9.2),
                            fanReviewsCount = obj.optInt("fanReviewsCount", 850),
                            fanConsensus = obj.optString("fanConsensus", "تحفة فنية أسطورية لا تفوت"),
                            quote = obj.optString("quote", "عالم بلا قواعد... يحكمه الأقوى."),
                            synopsis = obj.optString("synopsis", "قصة ملحمية مليئة بالإثارة والغموض والصراعات المصيرية.")
                        )
                    )
                }
            }
            if (parsedAnime.isNotEmpty()) {
                _animeList.value = parsedAnime
            } else {
                _animeList.value = getFallbackAnimeList()
            }

            // Parse Characters
            val charsArr = root.optJSONArray("chars")
            val parsedChars = mutableListOf<CharacterItem>()
            if (charsArr != null) {
                for (i in 0 until charsArr.length()) {
                    val obj = charsArr.getJSONObject(i)
                    parsedChars.add(
                        CharacterItem(
                            id = obj.optString("id", "c_$i"),
                            nameAr = obj.optString("n", "شخصية"),
                            nameEn = obj.optString("en", ""),
                            animeName = obj.optString("a", "أنمي"),
                            role = obj.optString("r", "بطل"),
                            quote = obj.optString("q", ""),
                            votes = obj.optInt("v", 100),
                            avatarUrl = obj.optString("img", "https://images.unsplash.com/photo-1563089145-599997674d42?w=150")
                        )
                    )
                }
            }
            if (parsedChars.isNotEmpty()) {
                _characters.value = parsedChars
            } else {
                _characters.value = getFallbackCharacters()
            }

            // Parse Quotes
            val quotesArr = root.optJSONArray("quotes")
            val parsedQuotes = mutableListOf<QuoteItem>()
            if (quotesArr != null) {
                for (i in 0 until quotesArr.length()) {
                    val obj = quotesArr.getJSONObject(i)
                    parsedQuotes.add(
                        QuoteItem(
                            id = "q_$i",
                            quote = obj.optString("q", ""),
                            anime = obj.optString("a", ""),
                            character = obj.optString("c", ""),
                            color = obj.optString("color", "#00F0FF")
                        )
                    )
                }
            }
            _quotes.value = if (parsedQuotes.isNotEmpty()) parsedQuotes else getFallbackQuotes()

            // Parse Store Items
            val storeArr = root.optJSONArray("storeItems")
            val parsedStore = mutableListOf<StoreItem>()
            if (storeArr != null) {
                for (i in 0 until storeArr.length()) {
                    val obj = storeArr.getJSONObject(i)
                    parsedStore.add(
                        StoreItem(
                            id = obj.optString("id", "item_$i"),
                            name = obj.optString("n", "عنصر تجميلي"),
                            type = obj.optString("t", "frame"),
                            priceCoins = obj.optInt("p", 150),
                            priceStars = obj.optInt("s", 0),
                            description = obj.optString("d", "عنصر حصري لتمييز ملفك الشخصي"),
                            previewColor = obj.optString("c", "#00F0FF")
                        )
                    )
                }
            }
            _storeItems.value = if (parsedStore.isNotEmpty()) parsedStore else getFallbackStoreItems()

        } catch (e: Exception) {
            _animeList.value = getFallbackAnimeList()
            _characters.value = getFallbackCharacters()
            _quotes.value = getFallbackQuotes()
            _storeItems.value = getFallbackStoreItems()
        }
    }

    private fun initDefaultPosts() {
        _posts.value = listOf(
            Post(
                id = "p1",
                authorId = "u_luffy",
                authorName = "لوفي القبعة القشية",
                authorAvatar = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150",
                authorRole = "إمبراطور البحر",
                isVerified = true,
                text = "سأصبح ملك القراصنة القادم! ما هي توقعاتكم لجزيرة إلباف والتحالفات القادمة في ون بيس؟ هل سنرى المعركة الكبرى قريباً؟ 🔥🏴‍☠️",
                mediaUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600",
                tags = listOf("ون_بيس", "لوفي", "نظريات"),
                likesCount = 428,
                commentsCount = 64,
                sharesCount = 18,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 35,
                isLiked = true,
                animeRef = "ون بيس"
            ),
            Post(
                id = "p2",
                authorId = "u_levi",
                authorName = "ليفاي أكرمان",
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                authorRole = "فيلق الاستطلاع",
                isVerified = true,
                text = "الندم خيار الضعفاء. اتخذ قرارك وتحمل عواقبه حتى النهاية. ما هو أكثر مشهد قتال في هجوم العمالقة لا يمكنكم نسيانه أبداً؟ ⚔️",
                mediaUrl = null,
                tags = listOf("هجوم_العمالقة", "ليفاي", "اقتباسات"),
                likesCount = 890,
                commentsCount = 112,
                sharesCount = 45,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 120,
                isLiked = false,
                animeRef = "هجوم العمالقة"
            ),
            Post(
                id = "p3",
                authorId = "u_gojo",
                authorName = "ساتورو غوجو",
                authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                authorRole = "المعوذ الأقوى",
                isVerified = true,
                text = "توسيع النطاق: الفراغ اللانهائي! 🤞✨ تقييمكم لآرك شيبويا في جوجوتسو كايسن؟ هل تفوق على جميع آركات الشونين الحديثة؟",
                mediaUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600",
                tags = listOf("جوجوتسو_كايسن", "غوجو", "مقارنات"),
                likesCount = 1350,
                commentsCount = 240,
                sharesCount = 92,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 240,
                isLiked = true,
                animeRef = "جوجوتسو كايسن"
            ),
            Post(
                id = "p4",
                authorId = "u_zoro",
                authorName = "رورونوا زورو",
                authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                authorRole = "سياف الأسطورة",
                isVerified = true,
                text = "لا يوجد طريق مختصر لتصبح الأقوى. فقط التدريب المستمر والإصرار.",
                mediaUrl = null,
                tags = listOf("ون_بيس", "زورو", "حكمة"),
                likesCount = 512,
                commentsCount = 38,
                sharesCount = 12,
                createdAt = System.currentTimeMillis() - 1000 * 60 * 360,
                isLiked = false
            )
        )
    }

    private fun initDefaultStories() {
        _stories.value = listOf(
            Story(
                id = "s1",
                userId = "u_luffy",
                userName = "لوفي",
                userAvatar = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600",
                caption = "وليمة القراصنة اليوم! 🍖",
                isSeen = false
            ),
            Story(
                id = "s2",
                userId = "u_gojo",
                userName = "غوجو",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600",
                caption = "أنا الأفضل بدون منازع 😎",
                isSeen = false
            ),
            Story(
                id = "s3",
                userId = "u_tanjiro",
                userName = "تانجيرو",
                userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=600",
                caption = "تنفس الشمس — الضربة الحاسمة ☀️",
                isSeen = true
            ),
            Story(
                id = "s4",
                userId = "u_naruto",
                userName = "ناروتو",
                userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                mediaUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600",
                caption = "أفضل رامين في كونوها 🍜",
                isSeen = true
            )
        )
    }

    private fun initDefaultReels() {
        _reels.value = listOf(
            Reel(
                id = "r1",
                authorId = "u_tanjiro",
                authorName = "تانجيرو كامادو",
                authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                caption = "تحريك أسطوري لقتال أكازا ورينغوكو! لا زلت أشعر بالقشعريرة مع كل مشاهدة 🔥⚔️",
                soundTitle = "Gurenge - Demon Slayer Opening",
                likesCount = 1420,
                commentsCount = 89,
                isLiked = true
            ),
            Reel(
                id = "r2",
                authorId = "u_luffy",
                authorName = "لوفي القبعة القشية",
                authorAvatar = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                caption = "ظهور الغيار 5 لأول مرة في الأنمي! صوت طبول الحرية يهز الوجدان 🥁✨",
                soundTitle = "Drums of Liberation - One Piece OST",
                likesCount = 3890,
                commentsCount = 310,
                isLiked = false
            ),
            Reel(
                id = "r3",
                authorId = "u_gojo",
                authorName = "ساتورو غوجو",
                authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                caption = "أجمل لقطات المجال اللانهائي بدقة 4K الخارقة 🌌⚡",
                soundTitle = "SpecialZ - King Gnu",
                likesCount = 2540,
                commentsCount = 178,
                isLiked = true
            )
        )
    }

    private fun initDefaultChats() {
        val convos = listOf(
            ChatConversation(
                id = "c_luffy",
                name = "لوفي القبعة القشية",
                avatar = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150",
                lastMessage = "هل تريد الانضمام إلى طاقمنا في الرحلة القادمة؟",
                lastMessageTime = "منذ 10 د",
                unreadCount = 2,
                isOnline = true
            ),
            ChatConversation(
                id = "c_gojo",
                name = "ساتورو غوجو",
                avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                lastMessage = "لا تقلق، فأنتم معي الأقوياء دائماً 🤞",
                lastMessageTime = "منذ ساعتين",
                unreadCount = 0,
                isOnline = true
            ),
            ChatConversation(
                id = "c_otaku_club",
                name = "نقابة أوتاكو العرب ⚔️",
                avatar = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=150",
                lastMessage = "أحمد: ما هو موعد الحلقة القادمة من بليتش؟",
                lastMessageTime = "منذ 45 د",
                unreadCount = 5,
                isGroup = true
            ),
            ChatConversation(
                id = "c_world_op",
                name = "عالم ون بيس (الجراند لاين)",
                avatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                lastMessage = "مناقشة عامة مفتوحة لأعضاء العالم",
                lastMessageTime = "منذ ساعة",
                unreadCount = 0,
                isWorld = true
            )
        )
        _conversations.value = convos

        _messages.value = mapOf(
            "c_luffy" to listOf(
                ChatMessage(
                    id = "m1",
                    senderId = "u_luffy",
                    senderName = "لوفي القبعة القشية",
                    senderAvatar = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150",
                    text = "مرحباً يا صديقي! سمعت أنك تبحث عن مغامرة حقيقية؟",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 30,
                    isMe = false
                ),
                ChatMessage(
                    id = "m2",
                    senderId = "u_demo_1",
                    senderName = "مستخدم أنمي بلاك",
                    senderAvatar = "",
                    text = "أهلاً لوفي! بكل تأكيد، أنا جاهز لأي إبحار في الجراند لاين!",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 25,
                    isMe = true
                ),
                ChatMessage(
                    id = "m3",
                    senderId = "u_luffy",
                    senderName = "لوفي القبعة القشية",
                    senderAvatar = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150",
                    text = "هل تريد الانضمام إلى طاقمنا في الرحلة القادمة؟",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 10,
                    isMe = false
                )
            )
        )
    }

    private fun initDefaultWorldsAndGroups() {
        _worlds.value = listOf(
            WorldItem(
                id = "w1",
                name = "عالم ون بيس — الجراند لاين",
                membersCount = 1420,
                bannerUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=600",
                description = "العالم الرسمي لعشاق ون بيس لمناقشة الحلقات، الفصول، النظريات، وصراعات الأباطرة.",
                tag = "شونين",
                isJoined = true
            ),
            WorldItem(
                id = "w2",
                name = "عالم هجوم العمالقة — جزيرة باراديس",
                membersCount = 980,
                bannerUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600",
                description = "مساحة مخصصة لتحليل قرارات إيرين، معارك فيلق الاستطلاع، وفلسفة الحرية.",
                tag = "سينين / دراما",
                isJoined = false
            ),
            WorldItem(
                id = "w3",
                name = "عالم كيميتسو — فيلق قتلة الشياطين",
                membersCount = 1150,
                bannerUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=600",
                description = "مجتمع الهاشيرا والمقاتلين، تقييم المعارك، ومتابعة أفلام قلعة اللانهاية.",
                tag = "خيال / قتال",
                isJoined = true
            ),
            WorldItem(
                id = "w4",
                name = "عالم جوجوتسو — مدرسة طوكيو للتعويذ",
                membersCount = 1680,
                bannerUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600",
                description = "أقوى نقاشات توسيع النطاق، تقنيات الطاقة الملعونة، ومواجهات سوكونا.",
                tag = "شونين خارق",
                isJoined = false
            )
        )

        _groups.value = listOf(
            GroupItem(
                id = "g1",
                name = "نقابة أوتاكو النخبة",
                membersCount = 120,
                avatarUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=150",
                description = "مجموعة حصرية لأصحاب المستويات العالية وعشاق التحليلات العميقة.",
                leaderName = "مدير أنمي بلاك"
            ),
            GroupItem(
                id = "g2",
                name = "رابطة الرسامين والمصممين",
                membersCount = 85,
                avatarUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150",
                description = "شارك رسومات الأنمي، تصميم الفان آرت، وتصاميم الأغلفة الأسبوعية.",
                leaderName = "سورا الفنان"
            ),
            GroupItem(
                id = "g3",
                name = "نادي مانجا الأسبوع",
                membersCount = 210,
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                description = "مراجعة فصول المانجا فور صدورها مع تحذير شديد من الحرق!",
                leaderName = "قارئ المانجا"
            )
        )
    }

    private fun initDefaultNotifications() {
        _notifications.value = listOf(
            NotificationItem(
                id = "n1",
                title = "إعجاب جديد",
                body = "أعجب لوفي بمنشورك الأخير حول جزيرة إلباف",
                type = "like",
                timeAgo = "منذ 15 د",
                isRead = false,
                iconName = "heart"
            ),
            NotificationItem(
                id = "n2",
                title = "مكافأة يومية",
                body = "تمت إضافة 50 عملة كوينز و2 نجمة إلى حسابك لتسجيل الدخول اليومي",
                type = "reward",
                timeAgo = "منذ ساعتين",
                isRead = false,
                iconName = "gift"
            ),
            NotificationItem(
                id = "n3",
                title = "إعلان رسمي من الإدارة",
                body = "تم إطلاق نظام الألعاب الجديد وبطولات الساحة الأسبوعية!",
                type = "system",
                timeAgo = "منذ 5 ساعات",
                isRead = true,
                iconName = "bell"
            )
        )
    }

    private fun initDefaultGameData() {
        _gameCharacters.value = listOf(
            GameCharacter(
                id = "gc_luffy",
                name = "لوفي — الغيار الخامس",
                anime = "ون بيس",
                role = "مهاجم أسطوري",
                element = "حرية / برق",
                rarity = "UR",
                baseAtk = 280,
                baseDef = 190,
                baseHp = 2400,
                currentLevel = 25,
                isUnlocked = true,
                skillName = "مسدس باجرانج العملاق",
                skillDescription = "لكمة عملاقة تسبب 450% ضرر وتخترق دفاعات الخصم بالكامل",
                avatarUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=150"
            ),
            GameCharacter(
                id = "gc_zoro",
                name = "زورو — ملك الجحيم",
                anime = "ون بيس",
                role = "سياف فتاك",
                element = "نار وظلال",
                rarity = "SSR",
                baseAtk = 250,
                baseDef = 160,
                baseHp = 2100,
                currentLevel = 18,
                isUnlocked = true,
                skillName = "إيشيداي سانزن دايسن سيكاي",
                skillDescription = "تقطيع فراغي فائق السرعة يسبب 380% ضرر جماعي",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"
            ),
            GameCharacter(
                id = "gc_gojo",
                name = "ساتورو غوجو — الأقوى",
                anime = "جوجوتسو كايسن",
                role = "معوذ كوني",
                element = "طاقة ملعونة",
                rarity = "UR",
                baseAtk = 320,
                baseDef = 280,
                baseHp = 2800,
                currentLevel = 30,
                isUnlocked = false,
                unlockCostCoins = 1500,
                skillName = "الأرجواني المجوف (Hollow Purple)",
                skillDescription = "محو الوجود في المسار المستهدف مسبباً 600% ضرر ساحق",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150"
            ),
            GameCharacter(
                id = "gc_tanjiro",
                name = "تانجيرو — تنفس الشمس",
                anime = "كيميتسو",
                role = "مقاتل توازن",
                element = "شمس نقية",
                rarity = "SSR",
                baseAtk = 220,
                baseDef = 175,
                baseHp = 2000,
                currentLevel = 15,
                isUnlocked = false,
                unlockCostCoins = 800,
                skillName = "رقصة إله النار المتعاقبة",
                skillDescription = "12 حركة هجومية متتالية مع زيادة سرعة الهجوم بنسبة 50%",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
            )
        )

        _gameMissions.value = listOf(
            GameMission(
                id = "m_1",
                title = "أول نصر في الساحة",
                description = "اهزم 5 شياطين في وضع القتال السريع",
                rewardCoins = 100,
                rewardStars = 2,
                currentProgress = 5,
                totalProgress = 5,
                isClaimed = false
            ),
            GameMission(
                id = "m_2",
                title = "ترقية البطل",
                description = "قم بترقية أي بطل إلى المستوى 10 أو أعلى",
                rewardCoins = 250,
                rewardStars = 5,
                currentProgress = 1,
                totalProgress = 1,
                isClaimed = true
            ),
            GameMission(
                id = "m_3",
                title = "سيد المهارات القاضية",
                description = "استخدم المهارة الخارقة 10 مرات في المعارك",
                rewardCoins = 300,
                rewardStars = 6,
                currentProgress = 7,
                totalProgress = 10,
                isClaimed = false
            )
        )
    }

    // Interactive Actions
    fun addPost(text: String, tags: List<String> = emptyList(), animeRef: String? = null) {
        createPost(text, tags, null, animeRef)
    }

    fun createPost(text: String, tags: List<String>, mediaUrl: String? = null, animeRef: String? = null) {
        val user = _currentUser.value ?: return
        val newPost = Post(
            id = "p_${System.currentTimeMillis()}",
            authorId = user.id,
            authorName = user.name,
            authorAvatar = user.avatar,
            authorRole = user.role,
            isVerified = user.isVerified,
            text = text,
            mediaUrl = mediaUrl,
            tags = tags,
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            createdAt = System.currentTimeMillis(),
            isLiked = false,
            animeRef = animeRef
        )
        _posts.value = listOf(newPost) + _posts.value
        // Sync to live Firestore
        firestoreManager.publishPost(newPost)
        // Award XP for posting
        addXP(25)
    }

    fun toggleLikePost(postId: String) {
        val user = _currentUser.value
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                val newLiked = !post.isLiked
                val newCount = if (newLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
                if (user != null) {
                    firestoreManager.togglePostLike(postId, user.id, post.isLiked, post.likesCount)
                }
                post.copy(isLiked = newLiked, likesCount = newCount)
            } else post
        }
    }

    fun addComment(postId: String, text: String) {
        val user = _currentUser.value ?: return
        val comment = Comment(
            id = "c_${System.currentTimeMillis()}",
            postId = postId,
            authorId = user.id,
            authorName = user.name,
            authorAvatar = user.avatar,
            text = text,
            createdAt = System.currentTimeMillis()
        )
        val currentComments = _postComments.value[postId] ?: emptyList()
        val updated = _postComments.value.toMutableMap()
        updated[postId] = currentComments + comment
        _postComments.value = updated

        firestoreManager.publishComment(postId, comment)

        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(commentsCount = post.commentsCount + 1)
            } else post
        }
        addXP(10)
    }

    fun listenToPostComments(postId: String) {
        firestoreManager.startCommentsListener(postId) { comments ->
            if (comments.isNotEmpty()) {
                val updated = _postComments.value.toMutableMap()
                updated[postId] = comments
                _postComments.value = updated
            }
        }
    }

    fun listenToChat(chatId: String) {
        val currentUid = _currentUser.value?.id
        firestoreManager.startChatListener(chatId, currentUid) { remoteMessages ->
            if (remoteMessages.isNotEmpty()) {
                val updatedMap = _messages.value.toMutableMap()
                val existing = updatedMap[chatId] ?: emptyList()
                val remoteIds = remoteMessages.map { it.id }.toSet()
                val merged = remoteMessages + existing.filterNot { remoteIds.contains(it.id) }
                updatedMap[chatId] = merged.sortedBy { it.createdAt }
                _messages.value = updatedMap
            }
        }
    }

    fun sendMessage(chatId: String, text: String) {
        val user = _currentUser.value ?: return
        val newMsg = ChatMessage(
            id = "m_${System.currentTimeMillis()}",
            senderId = user.id,
            senderName = user.name,
            senderAvatar = user.avatar,
            text = text,
            createdAt = System.currentTimeMillis(),
            isMe = true
        )
        val currentList = _messages.value[chatId] ?: emptyList()
        val updatedMap = _messages.value.toMutableMap()
        updatedMap[chatId] = currentList + newMsg
        _messages.value = updatedMap

        // Sync to live Firestore
        firestoreManager.sendChatMessage(chatId, newMsg)

        // Update conversation last message
        _conversations.value = _conversations.value.map { convo ->
            if (convo.id == chatId) {
                convo.copy(lastMessage = text, lastMessageTime = "الآن")
            } else convo
        }
    }

    fun buyStoreItem(item: StoreItem): Boolean {
        val user = _currentUser.value ?: return false
        if (user.coins >= item.priceCoins && user.stars >= item.priceStars) {
            val updatedUser = user.copy(
                coins = user.coins - item.priceCoins,
                stars = user.stars - item.priceStars,
                badges = if (item.type == "badge" && !user.badges.contains(item.name)) user.badges + item.name else user.badges,
                frame = if (item.type == "frame") item.id else user.frame,
                title = if (item.type == "title") item.name else user.title
            )
            _currentUser.value = updatedUser
            firestoreManager.saveUserProfile(updatedUser)

            _storeItems.value = _storeItems.value.map {
                if (it.id == item.id) it.copy(isPurchased = true) else it
            }
            return true
        }
        return false
    }

    fun upgradeGameCharacter(charId: String): Boolean {
        val user = _currentUser.value ?: return false
        val cost = 100
        if (user.coins >= cost) {
            val updatedUser = user.copy(coins = user.coins - cost)
            _currentUser.value = updatedUser
            firestoreManager.saveUserProfile(updatedUser)
            _gameCharacters.value = _gameCharacters.value.map { c ->
                if (c.id == charId) {
                    c.copy(
                        currentLevel = c.currentLevel + 1,
                        baseAtk = c.baseAtk + 15,
                        baseDef = c.baseDef + 10,
                        baseHp = c.baseHp + 100
                    )
                } else c
            }
            return true
        }
        return false
    }

    fun unlockGameCharacter(charId: String): Boolean {
        val user = _currentUser.value ?: return false
        val targetChar = _gameCharacters.value.find { it.id == charId } ?: return false
        if (user.coins >= targetChar.unlockCostCoins) {
            val updatedUser = user.copy(coins = user.coins - targetChar.unlockCostCoins)
            _currentUser.value = updatedUser
            firestoreManager.saveUserProfile(updatedUser)
            _gameCharacters.value = _gameCharacters.value.map { c ->
                if (c.id == charId) c.copy(isUnlocked = true) else c
            }
            return true
        }
        return false
    }

    fun claimMission(missionId: String) {
        val user = _currentUser.value ?: return
        val mission = _gameMissions.value.find { it.id == missionId } ?: return
        if (!mission.isClaimed && mission.currentProgress >= mission.totalProgress) {
            val updatedUser = user.copy(
                coins = user.coins + mission.rewardCoins,
                stars = user.stars + mission.rewardStars
            )
            _currentUser.value = updatedUser
            firestoreManager.saveUserProfile(updatedUser)
            _gameMissions.value = _gameMissions.value.map {
                if (it.id == missionId) it.copy(isClaimed = true) else it
            }
        }
    }

    fun joinWorld(worldId: String) {
        _worlds.value = _worlds.value.map { w ->
            if (w.id == worldId) {
                val newJoined = !w.isJoined
                val count = if (newJoined) w.membersCount + 1 else w.membersCount - 1
                w.copy(isJoined = newJoined, membersCount = count)
            } else w
        }
    }

    fun addXP(amount: Int) {
        val user = _currentUser.value ?: return
        val newXp = user.xp + amount
        val newLevel = 1 + (newXp / 300)
        val updated = user.copy(xp = newXp, level = newLevel)
        _currentUser.value = updated
        firestoreManager.saveUserProfile(updated)
    }

    fun updateUserProfile(name: String, username: String, bio: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(name = name, username = username, bio = bio)
        _currentUser.value = updated
        firestoreManager.saveUserProfile(updated)
    }

    fun updateProfile(name: String, username: String, bio: String) {
        updateUserProfile(name, username, bio)
    }

    fun toggleLikeReel(reelId: String) {
        _reels.value = _reels.value.map { r ->
            if (r.id == reelId) {
                val newLiked = !r.isLiked
                val count = if (newLiked) r.likesCount + 1 else maxOf(0, r.likesCount - 1)
                r.copy(isLiked = newLiked, likesCount = count)
            } else r
        }
    }

    fun rewardCombat(coins: Int, stars: Int, xp: Int) {
        val user = _currentUser.value ?: return
        val newCoins = user.coins + coins
        val newStars = user.stars + stars
        val newXp = user.xp + xp
        val newLevel = 1 + (newXp / 300)
        val updated = user.copy(coins = newCoins, stars = newStars, xp = newXp, level = newLevel)
        _currentUser.value = updated
        firestoreManager.saveUserProfile(updated)
    }

    fun markNotificationAsRead(notifId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notifId) it.copy(isRead = true) else it
        }
    }

    fun toggleAnimeSubscription(animeId: String): Boolean {
        val current = _subscribedAnimeIds.value.toMutableSet()
        val willSubscribe = !current.contains(animeId)
        if (willSubscribe) {
            current.add(animeId)
            com.example.service.FcmNotificationHelper.subscribeToAnimeEpisodes(animeId)
        } else {
            current.remove(animeId)
            com.example.service.FcmNotificationHelper.unsubscribeFromAnimeEpisodes(animeId)
        }
        _subscribedAnimeIds.value = current
        return willSubscribe
    }

    fun isAnimeSubscribed(animeId: String): Boolean {
        return _subscribedAnimeIds.value.contains(animeId)
    }

    fun broadcastEpisodeRelease(
        animeTitle: String,
        episodeNumber: String,
        bannerUrl: String? = null,
        synopsis: String? = null
    ) {
        // Show native FCM-styled push notification on the device
        com.example.service.FcmNotificationHelper.showEpisodeNotification(
            context = context,
            animeTitle = animeTitle,
            episodeNumber = episodeNumber,
            bannerUrl = bannerUrl,
            synopsis = synopsis
        )

        // Also add to in-app notification center
        val newNotif = NotificationItem(
            id = "ep_${System.currentTimeMillis()}",
            title = "🎬 حلقة جديدة: $animeTitle",
            body = "صدرت الآن الحلقة رقم $episodeNumber! متوفرة للمشاهدة والتفاعل على أنمي بلاك.",
            type = "episode",
            timeAgo = "الآن",
            isRead = false,
            iconName = "play"
        )
        _notifications.value = listOf(newNotif) + _notifications.value
        // Publish to live Firestore
        firestoreManager.publishNotification(newNotif)
    }

    fun setUser(user: User?) {
        _currentUser.value = user
        if (user != null) {
            // Stop previous user-scoped listeners
            firestoreManager.stopAllChatListeners()
            firestoreManager.stopFavoritesListener()
            firestoreManager.stopNotificationsListener()

            // Start new user-scoped listeners
            firestoreManager.startFavoritesListener(user.id) { remoteFavs ->
                _favorites.value = remoteFavs
            }
            firestoreManager.startNotificationsListener(user.id) { remoteNotifs ->
                if (remoteNotifs.isNotEmpty()) {
                    val remoteIds = remoteNotifs.map { it.id }.toSet()
                    val filteredLocal = _notifications.value.filterNot { remoteIds.contains(it.id) }
                    _notifications.value = remoteNotifs + filteredLocal
                }
            }
        } else {
            // COMPLETE LOGOUT PURGE: Stop all listeners and wipe sensitive user state
            firestoreManager.stopAllChatListeners()
            firestoreManager.stopFavoritesListener()
            firestoreManager.stopNotificationsListener()

            _favorites.value = emptyList()
            _messages.value = emptyMap()
            _notifications.value = emptyList()

            // Reset conversation messages & reset default chats to prevent cross-account chat leaks
            initDefaultChats()

            // Restart public notifications listener
            firestoreManager.startNotificationsListener(null) { remoteNotifs ->
                if (remoteNotifs.isNotEmpty()) {
                    _notifications.value = remoteNotifs
                }
            }
        }
    }

    fun isFavorite(targetId: String): Boolean {
        return _favorites.value.any { it.targetId == targetId }
    }

    fun toggleFavoriteAnime(anime: AnimeItem): Boolean {
        val user = _currentUser.value
        val userId = user?.id ?: "guest_user"
        val existing = _favorites.value.find { it.targetId == anime.id }
        return if (existing != null) {
            // Remove from local and Firestore
            _favorites.value = _favorites.value.filterNot { it.targetId == anime.id }
            firestoreManager.deleteFavorite(userId, anime.id)
            false
        } else {
            // Add to local and Firestore
            val newFav = FavoriteItem(
                id = "fav_${anime.id}",
                userId = userId,
                targetId = anime.id,
                titleAr = anime.titleAr,
                titleEn = anime.titleEn,
                type = "anime",
                bannerUrl = anime.bannerUrl,
                rating = anime.rating,
                genre = anime.genres.firstOrNull() ?: "أنمي",
                status = anime.status,
                addedAt = System.currentTimeMillis()
            )
            _favorites.value = listOf(newFav) + _favorites.value
            firestoreManager.saveFavorite(userId, newFav)
            addXP(15) // Reward XP for organizing watchlist
            true
        }
    }

    fun toggleFavoriteManga(mangaId: String, titleAr: String, titleEn: String, bannerUrl: String, rating: Double, genre: String, status: String): Boolean {
        val user = _currentUser.value
        val userId = user?.id ?: "guest_user"
        val existing = _favorites.value.find { it.targetId == mangaId }
        return if (existing != null) {
            _favorites.value = _favorites.value.filterNot { it.targetId == mangaId }
            firestoreManager.deleteFavorite(userId, mangaId)
            false
        } else {
            val newFav = FavoriteItem(
                id = "fav_$mangaId",
                userId = userId,
                targetId = mangaId,
                titleAr = titleAr,
                titleEn = titleEn,
                type = "manga",
                bannerUrl = bannerUrl,
                rating = rating,
                genre = genre,
                status = status,
                addedAt = System.currentTimeMillis()
            )
            _favorites.value = listOf(newFav) + _favorites.value
            firestoreManager.saveFavorite(userId, newFav)
            addXP(15)
            true
        }
    }

    fun removeFavorite(targetId: String) {
        val user = _currentUser.value
        val userId = user?.id ?: "guest_user"
        _favorites.value = _favorites.value.filterNot { it.targetId == targetId }
        firestoreManager.deleteFavorite(userId, targetId)
    }

    // Fallbacks
    private fun getFallbackAnimeList(): List<AnimeItem> = listOf(
        AnimeItem(
            id = "a1",
            titleAr = "ون بيس",
            titleEn = "One Piece",
            year = "1999",
            episodes = "1100+ حلقة",
            rating = 9.8,
            tier = "S+",
            genres = listOf("أكشن", "مغامرات", "كوميديا", "فانتازيا"),
            studio = "توي أنيميشن",
            status = "مستمر",
            color1 = "#00F0FF",
            color2 = "#3B82F6",
            bannerUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600",
            fanScore = 9.9,
            fanReviewsCount = 14200,
            fanConsensus = "أعظم رحلة ملحمية في تاريخ الأنمي دون منازع.",
            quote = "العالم ينتظر إجابتنا... القراصنة هم الحرية!",
            synopsis = "يبحر مونكي دي لوفي مع طاقمه بحثاً عن الكنز الأسطوري ون بيس ليصبح ملك القراصنة في عالم تملؤه الأسرار والغموض."
        ),
        AnimeItem(
            id = "a2",
            titleAr = "هجوم العمالقة",
            titleEn = "Attack on Titan",
            year = "2013",
            episodes = "89 حلقة",
            rating = 9.9,
            tier = "S+",
            genres = listOf("أكشن", "دراما", "غموض", "نفسي"),
            studio = "ويت / مابا",
            status = "مكتمل",
            color1 = "#8B5CF6",
            color2 = "#EC4899",
            bannerUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600",
            fanScore = 9.9,
            fanReviewsCount = 18400,
            fanConsensus = "تحفة سردية غير مسبوقة صدمت العالم بنهايتها وفلسفتها العميقة.",
            quote = "إذا انتصرت فستعيش، وإذا خسرت فستموت. إذا لم تقاتل فلن تنتصر أبداً.",
            synopsis = "يعيش البشر خلف جدران ضخمة تحميهم من عمالقة آكلي لحوم البشر، حتى يتحطم الجدار وتبدأ معركة وجودية من أجل الحرية."
        ),
        AnimeItem(
            id = "a3",
            titleAr = "جوجوتسو كايسن",
            titleEn = "Jujutsu Kaisen",
            year = "2020",
            episodes = "47 حلقة",
            rating = 9.4,
            tier = "S",
            genres = listOf("أكشن", "خوارق", "شونين"),
            studio = "مابا",
            status = "مستمر",
            color1 = "#00F0FF",
            color2 = "#8B5CF6",
            bannerUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600",
            fanScore = 9.5,
            fanReviewsCount = 9200,
            fanConsensus = "قتالات مذهلة وإخراج سينمائي يرفع معايير الشونين الحديث.",
            quote = "لا تقلق... فأنا الأقوى.",
            synopsis = "يجد إيتادوري يوجي نفسه في عالم اللعنات بعد أن ابتلع إصبع سوكونا ملك اللعنات، ليتعلم فنون التعويذ في ثانوية طوكيو."
        )
    )

    private fun getFallbackCharacters(): List<CharacterItem> = listOf(
        CharacterItem("c1", "مونكي دي لوفي", "Monkey D. Luffy", "ون بيس", "قائد الطاقم", "سأصبح ملك القراصنة!", 4250, "https://images.unsplash.com/photo-1563089145-599997674d42?w=150"),
        CharacterItem("c2", "ليفاي أكرمان", "Levi Ackerman", "هجوم العمالقة", "كابتن فيلق الاستطلاع", "كرسوا قلوبكم!", 5120, "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"),
        CharacterItem("c3", "ساتورو غوجو", "Satoru Gojo", "جوجوتسو كايسن", "المعوذ الأقوى", "توسيع النطاق: الفراغ اللانهائي", 6300, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150")
    )

    private fun getFallbackQuotes(): List<QuoteItem> = listOf(
        QuoteItem("q1", "الناس يعيشون حياتهم مقيدين بما يعرفونه ويفهمونه، ويسمون ذلك بالواقع.", "ناروتو", "إيتاتشي أوتشيها", "#FB7185"),
        QuoteItem("q2", "الخوف ليس شراً، إنه يخبرك ما هي نقاط ضعفك، وبمجرد أن تعرفها يمكنك أن تصبح أقوى وأكثر لطفاً.", "فيري تيل", "غيلدارتس كليف", "#00F0FF"),
        QuoteItem("q3", "إذا كنت لا تشارك شخصاً ألمه، فلن تستطيع أبداً أن تفهمه.", "ناروتو", "ناغاتو (باين)", "#8B5CF6")
    )

    private fun getFallbackStoreItems(): List<StoreItem> = listOf(
        StoreItem("f_fire", "إطار هالة النار", "frame", 150, 0, "flame", "إطار مشتعل باللون البرتقالي والأحمر", "#F97316"),
        StoreItem("f_neon", "إطار النيون السيبراني", "frame", 250, 5, "sparkles", "إطار نيون بأضواء كهرومغناطيسية متحركة", "#00F0FF"),
        StoreItem("b_founder", "شارة المؤسس الأسطوري", "badge", 500, 10, "award", "شارة ذهبية خاصة للرواد الأوائل", "#FBBF24"),
        StoreItem("t_emperor", "لقب إمبراطور الأنمي", "title", 800, 15, "crown", "لقب يوضع بجوار اسمك في كل مكان", "#EC4899")
    )

    companion object {
        @Volatile
        private var instance: AnimeRepository? = null

        fun getInstance(context: Context): AnimeRepository {
            return instance ?: synchronized(this) {
                instance ?: AnimeRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
