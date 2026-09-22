package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.AnimeRepository
import com.example.data.repository.AuthRepository
import com.example.service.FcmNotificationHelper
import com.example.ui.components.AnimeBottomNav
import com.example.ui.components.AnimeTopBar
import com.example.ui.components.CreatePostDialog
import com.example.ui.screens.*
import com.example.ui.theme.AnimeBlackTheme
import com.example.ui.theme.BgDeepVoid
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var animeRepository: AnimeRepository
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        animeRepository = AnimeRepository(applicationContext)
        authRepository = AuthRepository(animeRepository)

        // Initialize notification channels for Android 8.0+
        FcmNotificationHelper.createNotificationChannels(applicationContext)

        // Auto-subscribe to default new episodes topic
        FcmNotificationHelper.subscribeToNewEpisodesTopic()

        // Handle initial notification intent route
        val initialRoute = intent?.getStringExtra("route") ?: "splash"

        setContent {
            AnimeBlackTheme {
                AnimeBlackApp(
                    animeRepo = animeRepository,
                    authRepo = authRepository,
                    initialRoute = initialRoute
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::animeRepository.isInitialized) {
            animeRepository.firestoreManager.cleanup()
        }
    }
}

@Composable
fun AnimeBlackApp(
    animeRepo: AnimeRepository,
    authRepo: AuthRepository,
    initialRoute: String = "splash"
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepo, animeRepo)
    )

    // Request Android 13+ POST_NOTIFICATIONS permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            FcmNotificationHelper.subscribeToNewEpisodesTopic()
            Toast.makeText(context, "تم تفعيل إشعارات الحلقات الجديدة بنجاح 🔔", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // State collections
    val currentUser by animeRepo.currentUser.collectAsStateWithLifecycle()
    val posts by animeRepo.posts.collectAsStateWithLifecycle()
    val stories by animeRepo.stories.collectAsStateWithLifecycle()
    val animeList by animeRepo.animeList.collectAsStateWithLifecycle()
    val characters by animeRepo.characters.collectAsStateWithLifecycle()
    val worlds by animeRepo.worlds.collectAsStateWithLifecycle()
    val reels by animeRepo.reels.collectAsStateWithLifecycle()
    val storeItems by animeRepo.storeItems.collectAsStateWithLifecycle()
    val conversations by animeRepo.conversations.collectAsStateWithLifecycle()
    val messages by animeRepo.messages.collectAsStateWithLifecycle()
    val notifications by animeRepo.notifications.collectAsStateWithLifecycle()
    val subscribedAnimeIds by animeRepo.subscribedAnimeIds.collectAsStateWithLifecycle()
    val favorites by animeRepo.favorites.collectAsStateWithLifecycle()

    var currentRoute by remember { mutableStateOf(initialRoute) }
    val routeHistory = remember { mutableStateListOf(initialRoute) }

    var showAuthDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    fun navigateTo(route: String) {
        if (currentRoute != route) {
            routeHistory.add(route)
            currentRoute = route
        }
    }

    fun navigateBack() {
        if (routeHistory.size > 1) {
            routeHistory.removeAt(routeHistory.size - 1)
            currentRoute = routeHistory.last()
        } else {
            currentRoute = "home"
        }
    }

    BackHandler(enabled = currentRoute !in listOf("home", "splash", "auth")) {
        navigateBack()
    }

    val unreadNotifs = notifications.count { !it.isRead }
    val isFullScreenRoute = currentRoute in listOf("splash", "auth", "login", "register", "admin", "notifications")

    Scaffold(
        topBar = {
            if (!isFullScreenRoute) {
                AnimeTopBar(
                    user = currentUser,
                    unreadNotificationsCount = unreadNotifs,
                    onSearchClick = { navigateTo("explore") },
                    onFavoritesClick = { navigateTo("favorites") },
                    onNotificationsClick = { navigateTo("notifications") },
                    onAdminClick = { navigateTo("admin") }
                )
            }
        },
        bottomBar = {
            if (!isFullScreenRoute) {
                AnimeBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { target ->
                        if (target == "create") {
                            showCreatePostDialog = true
                        } else {
                            navigateTo(target)
                        }
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullScreenRoute) PaddingValues(0.dp) else innerPadding)
                .background(BgDeepVoid)
        ) {
            when (currentRoute) {
                "splash" -> SplashScreen(
                    isLoggedIn = authViewModel.isUserLoggedIn || currentUser != null,
                    onNavigateToHome = {
                        routeHistory.clear()
                        routeHistory.add("home")
                        currentRoute = "home"
                    },
                    onNavigateToAuth = {
                        routeHistory.clear()
                        routeHistory.add("auth")
                        currentRoute = "auth"
                    }
                )

                "auth" -> AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        routeHistory.clear()
                        routeHistory.add("home")
                        currentRoute = "home"
                    },
                    onContinueAsGuest = {
                        routeHistory.clear()
                        routeHistory.add("home")
                        currentRoute = "home"
                    }
                )

                "home" -> HomeScreen(
                    user = currentUser,
                    posts = posts,
                    stories = stories,
                    onLikePost = { postId -> animeRepo.toggleLikePost(postId) },
                    onAddComment = { postId, text ->
                        animeRepo.addComment(postId, text)
                        Toast.makeText(context, "تم نشر تعليقك! +10 XP", Toast.LENGTH_SHORT).show()
                    },
                    onCreatePost = { text, tags, animeRef ->
                        animeRepo.addPost(text, tags, animeRef)
                        Toast.makeText(context, "تم نشر منشورك بنجاح! +25 XP", Toast.LENGTH_SHORT).show()
                    },
                    onNavigateToCreate = { showCreatePostDialog = true }
                )

                "explore" -> ExploreScreen(
                    animeList = animeList,
                    characters = characters,
                    worlds = worlds,
                    onAnimeClick = { navigateTo("wiki") },
                    onWorldClick = { navigateTo("chat") }
                )

                "reels" -> ReelsScreen(
                    reels = reels,
                    onLikeReel = { reelId -> animeRepo.toggleLikeReel(reelId) }
                )

                "chat" -> ChatScreen(
                    conversations = conversations,
                    messages = messages,
                    onSendMessage = { chatId, text -> animeRepo.sendMessage(chatId, text) },
                    onOpenChat = { chatId -> animeRepo.listenToChat(chatId) }
                )

                "profile" -> ProfileScreen(
                    user = currentUser,
                    myPosts = posts.filter { it.authorName == currentUser?.name || it.authorId == currentUser?.id },
                    favorites = favorites,
                    onRemoveFavorite = { targetId ->
                        animeRepo.removeFavorite(targetId)
                        Toast.makeText(context, "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show()
                    },
                    onOpenAnimeWiki = { animeId ->
                        navigateTo("wiki")
                    },
                    onUpdateProfile = { name, username, bio ->
                        animeRepo.updateProfile(name, username, bio)
                        Toast.makeText(context, "تم تحديث الملف الشخصي", Toast.LENGTH_SHORT).show()
                    },
                    onLogout = {
                        authViewModel.logout()
                        routeHistory.clear()
                        routeHistory.add("auth")
                        currentRoute = "auth"
                    },
                    onLikePost = { postId -> animeRepo.toggleLikePost(postId) }
                )

                "favorites" -> FavoritesScreen(
                    favorites = favorites,
                    onRemoveFavorite = { targetId ->
                        animeRepo.removeFavorite(targetId)
                        Toast.makeText(context, "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show()
                    },
                    onOpenAnimeWiki = { animeId ->
                        navigateTo("wiki")
                    },
                    onBack = { navigateBack() }
                )

                "more" -> MoreHubScreen(
                    currentUser = currentUser,
                    onNavigate = { target -> navigateTo(target) }
                )

                "wiki" -> AnimeWikiScreen(
                    animeList = animeList,
                    characters = characters,
                    isAnimeSubscribed = { animeId -> subscribedAnimeIds.contains(animeId) },
                    onToggleSubscription = { animeId ->
                        val nowSubscribed = animeRepo.toggleAnimeSubscription(animeId)
                        val msg = if (nowSubscribed) "تم تفعيل إشعارات الحلقات لهذا الأنمي 🔔" else "تم إيقاف التنبيهات"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        nowSubscribed
                    },
                    isAnimeFavorite = { animeId -> animeRepo.isFavorite(animeId) },
                    onToggleFavorite = { anime ->
                        val isNowFav = animeRepo.toggleFavoriteAnime(anime)
                        val msg = if (isNowFav) "تمت إضافة ${anime.titleAr} إلى المفضلة ❤️ (+15 XP)" else "تمت إزالة ${anime.titleAr} من المفضلة"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        isNowFav
                    },
                    onSimulateEpisodePush = { anime ->
                        animeRepo.broadcastEpisodeRelease(
                            animeTitle = anime.titleAr,
                            episodeNumber = "الحلقة الجديدة",
                            bannerUrl = anime.bannerUrl,
                            synopsis = anime.synopsis
                        )
                        Toast.makeText(context, "تم إرسال إشعار الدفع التجريبي للحلقة! اسحب شريط الإشعارات 📲", Toast.LENGTH_LONG).show()
                    }
                )

                "games" -> GamesHubScreen(
                    user = currentUser,
                    onRewardEarned = { coins, stars, xp ->
                        animeRepo.rewardCombat(coins, stars, xp)
                        Toast.makeText(context, "حصلت على: +$coins عملة | +$stars نجوم | +$xp XP!", Toast.LENGTH_SHORT).show()
                    }
                )

                "store" -> StoreScreen(
                    user = currentUser,
                    storeItems = storeItems,
                    onBuyItem = { item ->
                        val success = animeRepo.buyStoreItem(item)
                        if (success) {
                            Toast.makeText(context, "مبروك! تم شراء ${item.name} وتجهيزه 🌟", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "رصيدك من العملات غير كافٍ!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                "admin" -> AdminPanelScreen(
                    currentUser = currentUser,
                    onBack = { navigateBack() },
                    onBroadcastEpisodePush = { title, ep, banner, syn ->
                        animeRepo.broadcastEpisodeRelease(title, ep, banner, syn)
                        Toast.makeText(context, "تم بث إشعار الحلقة $ep عبر FCM لجميع الأجهزة!", Toast.LENGTH_LONG).show()
                    }
                )

                "notifications" -> NotificationsScreen(
                    notifications = notifications,
                    onBack = { navigateBack() },
                    onNotificationClick = { notifId -> animeRepo.markNotificationAsRead(notifId) }
                )
            }
        }

        // Global Create Post Dialog
        if (showCreatePostDialog) {
            CreatePostDialog(
                onDismiss = { showCreatePostDialog = false },
                onSubmit = { text, tags, animeRef ->
                    animeRepo.addPost(text, tags, animeRef)
                    Toast.makeText(context, "تم نشر منشورك بنجاح! +25 XP", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Global Auth Dialog
        if (showAuthDialog) {
            AuthDialog(
                onDismiss = { showAuthDialog = false },
                onLogin = { email, pass ->
                    authViewModel.login(email, pass) { user ->
                        Toast.makeText(context, "أهلاً بك يا ${user.name}!", Toast.LENGTH_SHORT).show()
                    }
                },
                onRegister = { name, email, pass ->
                    authViewModel.register(name, email, pass, pass) { user ->
                        Toast.makeText(context, "مرحباً بك في أنمي بلاك، ${user.name}!", Toast.LENGTH_SHORT).show()
                    }
                },
                onGuestLogin = {
                    Toast.makeText(context, "تصفح كزائر نشط", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
