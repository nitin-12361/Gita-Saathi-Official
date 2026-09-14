package com.nkapps.gitasaathi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.GitaData
import com.nkapps.gitasaathi.data.Chapter
import com.nkapps.gitasaathi.data.Verse
import com.nkapps.gitasaathi.data.AudioMode
import com.nkapps.gitasaathi.data.VoiceStyle
import com.nkapps.gitasaathi.data.BookmarkEntity
import com.nkapps.gitasaathi.data.RecentPositionEntity
import com.nkapps.gitasaathi.data.SearchHistoryEntity
import com.nkapps.gitasaathi.data.UserProfile
import com.nkapps.gitasaathi.ui.GitaViewModel
import com.nkapps.gitasaathi.ui.Screen
import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.nkapps.gitasaathi.notifications.DailyShlokaReceiver
import com.nkapps.gitasaathi.notifications.DailyShlokaScheduler
import com.nkapps.gitasaathi.ui.components.DailyNotificationDialog
import com.nkapps.gitasaathi.ui.components.AboutGitaSaathiDialog
import com.nkapps.gitasaathi.ui.components.AudioPlayerBar
import com.nkapps.gitasaathi.ui.components.GeminiApiKeyDialog
import com.nkapps.gitasaathi.ui.components.GitaAiChatDialog
import com.nkapps.gitasaathi.ui.components.GitaNavigationDrawerContent
import com.nkapps.gitasaathi.ui.components.GitaTopBar
import com.nkapps.gitasaathi.ui.components.ReadingHistoryDialog
import com.nkapps.gitasaathi.ui.components.UserProfileDialog
import com.nkapps.gitasaathi.ui.components.GitaQuizDialog
import com.nkapps.gitasaathi.data.DailyQuizState
import com.nkapps.gitasaathi.data.SubmitQuizResult
import com.nkapps.gitasaathi.data.GitaWallpaper
import com.nkapps.gitasaathi.data.WallpaperCategory
import com.nkapps.gitasaathi.ui.components.WallpaperDetailDialog
import com.nkapps.gitasaathi.ui.screens.WallpaperGalleryScreen
import com.nkapps.gitasaathi.data.GitaMood
import com.nkapps.gitasaathi.data.StreakData
import com.nkapps.gitasaathi.ui.SleepTimerOption
import com.nkapps.gitasaathi.ui.components.VoiceSettingsDialog
import com.nkapps.gitasaathi.data.JapaMalaState
import com.nkapps.gitasaathi.ui.screens.JapaMalaScreen
import com.nkapps.gitasaathi.ui.components.GitaMoodDialog
import com.nkapps.gitasaathi.ui.components.SleepTimerDialog
import com.nkapps.gitasaathi.ui.components.BottomNavTab
import com.nkapps.gitasaathi.ui.components.GitaBottomNavigationBar
import com.nkapps.gitasaathi.ui.screens.BookmarksScreen
import com.nkapps.gitasaathi.ui.screens.ChapterScreen
import com.nkapps.gitasaathi.ui.screens.ChaptersListScreen
import com.nkapps.gitasaathi.ui.screens.FullScreenPlayerScreen
import com.nkapps.gitasaathi.ui.screens.GitaShortsFeedScreen
import com.nkapps.gitasaathi.ui.screens.HomeScreen
import com.nkapps.gitasaathi.ui.screens.SearchScreen
import com.nkapps.gitasaathi.ui.screens.ShortsChaptersScreen
import com.nkapps.gitasaathi.ui.screens.ShortsChapterVersesScreen
import com.nkapps.gitasaathi.ui.screens.GitaGoldScreen
import com.nkapps.gitasaathi.ads.GitaAdManager
import com.nkapps.gitasaathi.ui.theme.GitaSaathiTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val incomingIntent = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        incomingIntent.value = intent
        setContent {
            GitaSaathiApp(incomingIntentState = incomingIntent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingIntent.value = intent
    }
}

@Composable
fun GitaSaathiApp(
    viewModel: GitaViewModel = viewModel(),
    incomingIntentState: State<Intent?>? = null
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val selectedChapter by viewModel.selectedChapter.collectAsStateWithLifecycle()
    val currentVerseList by viewModel.currentVerseList.collectAsStateWithLifecycle()
    val selectedVerse by viewModel.selectedVerse.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsStateWithLifecycle()
    val activePlayingVerse by viewModel.activePlayingVerse.collectAsStateWithLifecycle()
    val audioMode by viewModel.audioMode.collectAsStateWithLifecycle()
    val voiceStyle by viewModel.voiceStyle.collectAsStateWithLifecycle()
    val audioSpeed by viewModel.audioSpeed.collectAsStateWithLifecycle()
    val narrationLanguage by viewModel.narrationLanguage.collectAsStateWithLifecycle()
    val autoContinueAudio by viewModel.autoContinueAudio.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val recentPosition by viewModel.recentPosition.collectAsStateWithLifecycle()
    val aiInsightText by viewModel.aiInsightText.collectAsStateWithLifecycle()
    val isInsightLoading by viewModel.isInsightLoading.collectAsStateWithLifecycle()
    val selectedVerseForInsight by viewModel.selectedVerseForInsight.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val showHistoryDialog by viewModel.showHistoryDialog.collectAsStateWithLifecycle()
    val showAboutDialog by viewModel.showAboutDialog.collectAsStateWithLifecycle()
    val showNotifDialog by viewModel.showNotifDialog.collectAsStateWithLifecycle()
    val showProfileDialog by viewModel.showProfileDialog.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val readingHistory by viewModel.readingHistory.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()

    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val aiChatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
    val isAiChatLoading by viewModel.isAiChatLoading.collectAsStateWithLifecycle()
    val showAiDialog by viewModel.showAiDialog.collectAsStateWithLifecycle()
    val showApiKeyDialog by viewModel.showApiKeyDialog.collectAsStateWithLifecycle()
    val showVoiceSettingsDialog by viewModel.showVoiceSettingsDialog.collectAsStateWithLifecycle()
    val showFullScreenPlayer by viewModel.showFullScreenPlayer.collectAsStateWithLifecycle()
    val isAudioLoading by viewModel.isAudioLoading.collectAsStateWithLifecycle()
    val audioErrorMessage by viewModel.audioErrorMessage.collectAsStateWithLifecycle()
    val audioProgress by viewModel.audioProgress.collectAsStateWithLifecycle()
    val audioCurrentTime by viewModel.audioCurrentTime.collectAsStateWithLifecycle()
    val audioTotalTime by viewModel.audioTotalTime.collectAsStateWithLifecycle()

    val sadhanaStreak by viewModel.sadhanaStreak.collectAsStateWithLifecycle()
    val showMoodDialog by viewModel.showMoodDialog.collectAsStateWithLifecycle()
    val selectedMood by viewModel.selectedMood.collectAsStateWithLifecycle()
    val japaMalaState by viewModel.japaMalaState.collectAsStateWithLifecycle()
    val showSleepTimerDialog by viewModel.showSleepTimerDialog.collectAsStateWithLifecycle()
    val sleepTimerOption by viewModel.sleepTimerOption.collectAsStateWithLifecycle()
    val sleepTimerSecondsRemaining by viewModel.sleepTimerSecondsRemaining.collectAsStateWithLifecycle()
    val shortsTargetVerse by viewModel.shortsTargetVerse.collectAsStateWithLifecycle()
    val shortsLanguage by viewModel.shortsLanguage.collectAsStateWithLifecycle()
    val selectedShortsChapter by viewModel.selectedShortsChapter.collectAsStateWithLifecycle()
    val selectedShortsVerses by viewModel.selectedShortsVerses.collectAsStateWithLifecycle()

    val dailyQuizState by viewModel.dailyQuizState.collectAsStateWithLifecycle()
    val showQuizDialog by viewModel.showQuizDialog.collectAsStateWithLifecycle()

    val selectedWallpaper by viewModel.selectedWallpaper.collectAsStateWithLifecycle()
    val wallpaperCategory by viewModel.wallpaperCategory.collectAsStateWithLifecycle()

    val shlokaOfTheDay = GitaData.getShlokaOfTheDay()
    val isShlokaOfDayBookmarked = viewModel.isVerseBookmarked(shlokaOfTheDay.verseKey)

    // Notification Permission Request (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            DailyShlokaScheduler.scheduleDailyNotification(context)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                DailyShlokaScheduler.scheduleDailyNotification(context)
            }
        } else {
            DailyShlokaScheduler.scheduleDailyNotification(context)
        }
    }

    // Handle incoming intent when user taps Daily Shloka push notification, FCM, or Widget
    val activityIntent = incomingIntentState?.value ?: (context as? Activity)?.intent
    LaunchedEffect(activityIntent) {
        val targetIntent = activityIntent ?: return@LaunchedEffect
        val shouldOpen = targetIntent.getBooleanExtra(DailyShlokaReceiver.EXTRA_OPEN_SHLOKA, false) ||
                targetIntent.hasExtra(DailyShlokaReceiver.EXTRA_CHAPTER_ID) ||
                targetIntent.hasExtra("target_chapter")

        if (shouldOpen) {
            val ch = when {
                targetIntent.hasExtra(DailyShlokaReceiver.EXTRA_CHAPTER_ID) ->
                    targetIntent.getIntExtra(DailyShlokaReceiver.EXTRA_CHAPTER_ID, 1)
                targetIntent.hasExtra("target_chapter") ->
                    targetIntent.getIntExtra("target_chapter", 1)
                else -> 1
            }
            val v = when {
                targetIntent.hasExtra(DailyShlokaReceiver.EXTRA_VERSE_ID) ->
                    targetIntent.getIntExtra(DailyShlokaReceiver.EXTRA_VERSE_ID, 1)
                targetIntent.hasExtra("target_verse") ->
                    targetIntent.getIntExtra("target_verse", 1)
                else -> 1
            }
            val verse = GitaData.getVerse(ch, v) ?: GitaData.getShlokaOfTheDay()
            viewModel.selectVerse(verse)

            // Clear intent flags and extras so it does not trigger again on recomposition or configuration change
            targetIntent.removeExtra(DailyShlokaReceiver.EXTRA_OPEN_SHLOKA)
            targetIntent.removeExtra(DailyShlokaReceiver.EXTRA_CHAPTER_ID)
            targetIntent.removeExtra(DailyShlokaReceiver.EXTRA_VERSE_ID)
            targetIntent.removeExtra("target_chapter")
            targetIntent.removeExtra("target_verse")
        }
    }

    GitaSaathiAppContent(
        currentScreen = currentScreen,
        appLanguage = appLanguage,
        isDarkMode = isDarkMode,
        selectedChapter = selectedChapter,
        currentVerseList = currentVerseList,
        selectedVerse = selectedVerse,
        searchQuery = searchQuery,
        searchResults = searchResults,
        isPlayingAudio = isPlayingAudio,
        activePlayingVerse = activePlayingVerse,
        audioMode = audioMode,
        voiceStyle = voiceStyle,
        audioSpeed = audioSpeed,
        narrationLanguage = narrationLanguage,
        autoContinueAudio = autoContinueAudio,
        bookmarks = bookmarks,
        recentPosition = recentPosition,
        aiInsightText = aiInsightText,
        isInsightLoading = isInsightLoading,
        selectedVerseForInsight = selectedVerseForInsight,
        showHistoryDialog = showHistoryDialog,
        showAboutDialog = showAboutDialog,
        showNotifDialog = showNotifDialog,
        showProfileDialog = showProfileDialog,
        userProfile = userProfile,
        readingHistory = readingHistory,
        searchHistory = searchHistory,
        customApiKey = customApiKey,
        aiChatMessages = aiChatMessages,
        isAiChatLoading = isAiChatLoading,
        showAiDialog = showAiDialog,
        showApiKeyDialog = showApiKeyDialog,
        showVoiceSettingsDialog = showVoiceSettingsDialog,
        showFullScreenPlayer = showFullScreenPlayer,
        isAudioLoading = isAudioLoading,
        audioErrorMessage = audioErrorMessage,
        audioProgress = audioProgress,
        audioCurrentTime = audioCurrentTime,
        audioTotalTime = audioTotalTime,
        shlokaOfTheDay = shlokaOfTheDay,
        isShlokaOfDayBookmarked = isShlokaOfDayBookmarked,
        sadhanaStreak = sadhanaStreak,
        showMoodDialog = showMoodDialog,
        selectedMood = selectedMood,
        japaMalaState = japaMalaState,
        showSleepTimerDialog = showSleepTimerDialog,
        sleepTimerOption = sleepTimerOption,
        sleepTimerSecondsRemaining = sleepTimerSecondsRemaining,
        dailyQuizState = dailyQuizState,
        showQuizDialog = showQuizDialog,
        onOpenQuiz = { viewModel.openQuizDialog() },
        onCloseQuiz = { viewModel.closeQuizDialog() },
        onSubmitQuiz = { viewModel.submitQuizScore(it) },
        selectedWallpaper = selectedWallpaper,
        wallpaperCategory = wallpaperCategory,
        onSelectWallpaper = { viewModel.selectWallpaper(it) },
        onSelectWallpaperCategory = { viewModel.setWallpaperCategory(it) },
        onOpenWallpapers = { viewModel.navigateTo(Screen.WALLPAPERS) },
        onProfileClick = { viewModel.openProfileDialog() },
        onChaptersClick = {
            viewModel.resetNavigationAndCloseDialogs(Screen.CHAPTERS_LIST)
        },
        onBookmarksClick = {
            viewModel.resetNavigationAndCloseDialogs(Screen.BOOKMARKS)
        },
        onHistoryClick = { viewModel.openHistoryDialog() },
        onDailyShlokaNotifClick = { viewModel.openNotifDialog() },
        onVoiceSettingsClick = { viewModel.openVoiceSettingsDialog() },
        onShareClick = {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    if (appLanguage == AppLanguage.HINDI)
                        "गीता साथी - श्रीमद्भगवद्गीता का अध्ययन, स्वर वाचन एवं AI श्लोक दर्शन हेतु ऐप! Built by Nitin."
                    else
                        "Experience Srimad Bhagavad Gita with audio narration and AI companion on Gita Saathi! Built by Nitin."
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Gita Saathi")
            context.startActivity(shareIntent)
        },
        onAboutClick = { viewModel.openAboutDialog() },
        onBackClick = {
            if (viewModel.currentScreen.value == Screen.SHORTS) {
                viewModel.navigateTo(Screen.SHORTS_CHAPTERS)
            } else if (viewModel.currentScreen.value == Screen.SHORTS_VERSES_GRID) {
                viewModel.navigateTo(Screen.SHORTS_CHAPTERS)
            } else if (viewModel.currentScreen.value == Screen.CHAPTER_DETAIL) {
                (context as? Activity)?.let { activity ->
                    GitaAdManager.showInterstitialWithCooldown(activity) {
                        viewModel.navigateTo(Screen.CHAPTERS_LIST)
                    }
                } ?: viewModel.navigateTo(Screen.CHAPTERS_LIST)
            } else {
                viewModel.navigateTo(Screen.HOME)
            }
        },
        onLanguageToggle = { viewModel.toggleAppLanguage() },
        onDarkModeToggle = { viewModel.toggleDarkMode() },
        onAiChatClick = { viewModel.openAiDialog() },
        onApiKeyClick = { viewModel.openApiKeyDialog() },
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onChapterClick = { viewModel.selectChapter(it) },
        onVerseClick = { viewModel.selectVerse(it) },
        onPlayVerseAudio = { verse ->
            if (activePlayingVerse?.verseKey == verse.verseKey) {
                viewModel.togglePlayPause()
            } else {
                viewModel.playVerseAudio(verse, forceRestart = true)
            }
        },
        onToggleBookmark = { viewModel.toggleBookmark(it) },
        onBookmarkPageClick = { viewModel.navigateTo(Screen.BOOKMARKS) },
        onOpenAiChat = { viewModel.openAiDialog() },
        onFetchAiInsight = { viewModel.fetchAiInsight(it) },
        onSaveQuery = { viewModel.saveSearchQuery(it) },
        onDeleteHistoryQuery = { viewModel.deleteSearchQuery(it) },
        onClearHistory = { viewModel.clearSearchHistory() },
        onPlayPauseToggle = { viewModel.togglePlayPause() },
        onNextClick = { viewModel.playNextVerse() },
        onPrevClick = { viewModel.playPreviousVerse() },
        onSpeedChange = { viewModel.setAudioSpeed(it) },
        onAudioModeChange = { viewModel.setAudioMode(it) },
        onNarrationLangChange = { viewModel.setNarrationLanguage(it) },
        onAutoContinueToggle = { viewModel.toggleAutoContinue() },
        onVoiceStyleSelected = { viewModel.setVoiceStyle(it) },
        onSeek = { viewModel.seekTo(it) },
        onSeekStarted = { viewModel.onSeekStarted() },
        onTestSample = { lang, style, speed -> viewModel.testVoiceSample(lang, style, speed) },
        onSendMessage = { viewModel.sendAiChatMessage(it) },
        onSaveApiKey = { viewModel.setCustomApiKey(it) },
        onRegister = { name, email -> viewModel.registerUser(name, email) },
        onLogout = { viewModel.logoutUser() },
        onCloseProfileDialog = { viewModel.closeProfileDialog() },
        onCloseNotifDialog = { viewModel.closeNotifDialog() },
        onCloseAboutDialog = { viewModel.closeAboutDialog() },
        onCloseHistoryDialog = { viewModel.closeHistoryDialog() },
        onCloseVoiceSettingsDialog = { viewModel.closeVoiceSettingsDialog() },
        onCloseAiDialog = { viewModel.closeAiDialog() },
        onCloseApiKeyDialog = { viewModel.closeApiKeyDialog() },
        onOpenFullScreenPlayer = { viewModel.openFullScreenPlayer() },
        onCloseFullScreenPlayer = { viewModel.closeFullScreenPlayer() },
        onDismissVerseDetail = { viewModel.dismissVerseDetail() },
        onClearAudioError = { viewModel.clearAudioError() },
        isVerseBookmarked = { viewModel.isVerseBookmarked(it) },
        navigateTo = { viewModel.navigateTo(it) },
        onMoodClick = { viewModel.openMoodDialog(it) },
        onCloseMoodDialog = { viewModel.closeMoodDialog() },
        onOpenJapaMala = { viewModel.openJapaMala() },
        onIncrementJapaBead = { viewModel.incrementJapaBead() },
        onResetJapaMala = { viewModel.resetJapaMala() },
        onSelectJapaMantra = { viewModel.selectJapaMantra(it) },
        onSetTargetJapaMalas = { viewModel.setTargetJapaMalas(it) },
        onToggleJapaHaptic = { viewModel.toggleJapaHaptic() },
        onToggleJapaSound = { viewModel.toggleJapaSound() },
        onDismissJapaCelebration = { viewModel.dismissJapaCelebration() },
        onOpenSleepTimerDialog = { viewModel.openSleepTimerDialog() },
        onCloseSleepTimerDialog = { viewModel.closeSleepTimerDialog() },
        onSelectSleepTimer = { viewModel.setSleepTimer(it) },
        onShareStory = { viewModel.shareVerseAsStory(context, it) },
        shortsTargetVerse = shortsTargetVerse,
        shortsLanguage = shortsLanguage,
        selectedShortsChapter = selectedShortsChapter,
        selectedShortsVerses = selectedShortsVerses,
        onSelectShortsChapter = { viewModel.openShortsVersesGrid(it) },
        onOpenShorts = { verse ->
            if (verse != null) {
                viewModel.openShortsFeed(verse)
            } else {
                viewModel.openShortsChapters()
            }
        },
        onShortsLanguageChange = { viewModel.setShortsLanguage(it) }
    )
}

@Composable
fun GitaSaathiAppContent(
    currentScreen: Screen,
    appLanguage: AppLanguage,
    isDarkMode: Boolean,
    selectedChapter: Chapter?,
    currentVerseList: List<Verse>,
    selectedVerse: Verse?,
    searchQuery: String,
    searchResults: List<Verse>,
    isPlayingAudio: Boolean,
    activePlayingVerse: Verse?,
    audioMode: AudioMode,
    voiceStyle: VoiceStyle,
    audioSpeed: Float,
    narrationLanguage: AppLanguage,
    autoContinueAudio: Boolean,
    bookmarks: List<BookmarkEntity>,
    recentPosition: RecentPositionEntity?,
    aiInsightText: String?,
    isInsightLoading: Boolean,
    selectedVerseForInsight: Verse?,
    showHistoryDialog: Boolean,
    showAboutDialog: Boolean,
    showNotifDialog: Boolean,
    showProfileDialog: Boolean,
    userProfile: UserProfile,
    readingHistory: List<Verse>,
    searchHistory: List<SearchHistoryEntity>,
    customApiKey: String,
    aiChatMessages: List<GitaViewModel.AiChatMessage>,
    isAiChatLoading: Boolean,
    showAiDialog: Boolean,
    showApiKeyDialog: Boolean,
    showVoiceSettingsDialog: Boolean,
    showFullScreenPlayer: Boolean,
    isAudioLoading: Boolean,
    audioErrorMessage: String?,
    audioProgress: Float,
    audioCurrentTime: Long,
    audioTotalTime: Long,
    shlokaOfTheDay: Verse,
    isShlokaOfDayBookmarked: Boolean,
    sadhanaStreak: StreakData = StreakData(),
    showMoodDialog: Boolean = false,
    selectedMood: GitaMood? = null,
    japaMalaState: JapaMalaState = JapaMalaState(),
    showSleepTimerDialog: Boolean = false,
    sleepTimerOption: SleepTimerOption = SleepTimerOption.OFF,
    sleepTimerSecondsRemaining: Long = 0L,
    dailyQuizState: DailyQuizState? = null,
    showQuizDialog: Boolean = false,
    onOpenQuiz: () -> Unit = {},
    onCloseQuiz: () -> Unit = {},
    onSubmitQuiz: (Int) -> SubmitQuizResult = { SubmitQuizResult(0, 0, 0, emptyList()) },
    selectedWallpaper: GitaWallpaper? = null,
    wallpaperCategory: WallpaperCategory = WallpaperCategory.ALL,
    onSelectWallpaper: (GitaWallpaper?) -> Unit = {},
    onSelectWallpaperCategory: (WallpaperCategory) -> Unit = {},
    onOpenWallpapers: () -> Unit = {},
    onProfileClick: () -> Unit,
    onChaptersClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDailyShlokaNotifClick: () -> Unit,
    onVoiceSettingsClick: () -> Unit,
    onShareClick: () -> Unit,
    onAboutClick: () -> Unit,
    onBackClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onDarkModeToggle: () -> Unit,
    onAiChatClick: () -> Unit,
    onApiKeyClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onChapterClick: (Int) -> Unit,
    onVerseClick: (Verse?) -> Unit,
    onPlayVerseAudio: (Verse) -> Unit,
    onToggleBookmark: (Verse) -> Unit,
    onBookmarkPageClick: () -> Unit,
    onOpenAiChat: () -> Unit,
    onFetchAiInsight: (Verse) -> Unit,
    onSaveQuery: (String) -> Unit,
    onDeleteHistoryQuery: (String) -> Unit,
    onClearHistory: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onAudioModeChange: (AudioMode) -> Unit,
    onNarrationLangChange: (AppLanguage) -> Unit,
    onAutoContinueToggle: () -> Unit,
    onVoiceStyleSelected: (VoiceStyle) -> Unit,
    onSeek: (Float) -> Unit,
    onSeekStarted: () -> Unit,
    onTestSample: (AppLanguage, VoiceStyle, Float) -> Unit,
    onSendMessage: (String) -> Unit,
    onSaveApiKey: (String) -> Unit,
    onRegister: (String, String) -> Unit,
    onLogout: () -> Unit,
    onCloseProfileDialog: () -> Unit,
    onCloseNotifDialog: () -> Unit,
    onCloseAboutDialog: () -> Unit,
    onCloseHistoryDialog: () -> Unit,
    onCloseVoiceSettingsDialog: () -> Unit,
    onCloseAiDialog: () -> Unit,
    onCloseApiKeyDialog: () -> Unit,
    onOpenFullScreenPlayer: () -> Unit,
    onCloseFullScreenPlayer: () -> Unit,
    onDismissVerseDetail: () -> Unit,
    onClearAudioError: () -> Unit,
    isVerseBookmarked: (String) -> Boolean,
    navigateTo: (Screen) -> Unit,
    onMoodClick: (GitaMood) -> Unit = {},
    onCloseMoodDialog: () -> Unit = {},
    onOpenJapaMala: () -> Unit = {},
    onIncrementJapaBead: () -> Unit = {},
    onResetJapaMala: () -> Unit = {},
    onSelectJapaMantra: (Int) -> Unit = {},
    onSetTargetJapaMalas: (Int) -> Unit = {},
    onToggleJapaHaptic: () -> Unit = {},
    onToggleJapaSound: () -> Unit = {},
    onDismissJapaCelebration: () -> Unit = {},
    onOpenSleepTimerDialog: () -> Unit = {},
    onCloseSleepTimerDialog: () -> Unit = {},
    onSelectSleepTimer: (SleepTimerOption) -> Unit = {},
    onShareStory: (Verse) -> Unit = {},
    shortsTargetVerse: Verse? = null,
    shortsLanguage: AppLanguage = AppLanguage.HINDI,
    selectedShortsChapter: Chapter? = null,
    selectedShortsVerses: List<Verse> = emptyList(),
    onSelectShortsChapter: (Int) -> Unit = {},
    onOpenShorts: (Verse?) -> Unit = {},
    onShortsLanguageChange: (AppLanguage) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Handle back button behavior for sub-screens and open drawers/dialogs/player
    BackHandler(enabled = drawerState.isOpen || showFullScreenPlayer || currentScreen != Screen.HOME || selectedVerse != null || selectedWallpaper != null || showQuizDialog || showAiDialog || showApiKeyDialog || showVoiceSettingsDialog || showHistoryDialog || showAboutDialog || showNotifDialog || showProfileDialog || showMoodDialog || showSleepTimerDialog) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (showFullScreenPlayer) {
            onCloseFullScreenPlayer()
            onDismissVerseDetail()
        } else if (selectedWallpaper != null) {
            onSelectWallpaper(null)
        } else if (showQuizDialog) {
            onCloseQuiz()
        } else if (showMoodDialog) {
            onCloseMoodDialog()
        } else if (showSleepTimerDialog) {
            onCloseSleepTimerDialog()
        } else if (showProfileDialog) {
            onCloseProfileDialog()
        } else if (showNotifDialog) {
            onCloseNotifDialog()
        } else if (showAboutDialog) {
            onCloseAboutDialog()
        } else if (showHistoryDialog) {
            onCloseHistoryDialog()
        } else if (showVoiceSettingsDialog) {
            onCloseVoiceSettingsDialog()
        } else if (showApiKeyDialog) {
            onCloseApiKeyDialog()
        } else if (showAiDialog) {
            onCloseAiDialog()
        } else if (selectedVerse != null) {
            onDismissVerseDetail()
        } else if (currentScreen == Screen.SHORTS) {
            if (selectedShortsChapter != null) {
                navigateTo(Screen.SHORTS_VERSES_GRID)
            } else {
                navigateTo(Screen.SHORTS_CHAPTERS)
            }
        } else if (currentScreen == Screen.SHORTS_VERSES_GRID) {
            navigateTo(Screen.SHORTS_CHAPTERS)
        } else if (currentScreen == Screen.CHAPTER_DETAIL) {
            (context as? Activity)?.let { activity ->
                GitaAdManager.showInterstitialWithCooldown(activity) {
                    navigateTo(Screen.CHAPTERS_LIST)
                }
            } ?: navigateTo(Screen.CHAPTERS_LIST)
        } else {
            navigateTo(Screen.HOME)
        }
    }

    GitaSaathiTheme(darkTheme = isDarkMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                GitaNavigationDrawerContent(
                    appLanguage = appLanguage,
                    userProfile = userProfile,
                    onProfileClick = {
                        coroutineScope.launch { drawerState.close() }
                        onProfileClick()
                    },
                    onChaptersClick = {
                        coroutineScope.launch { drawerState.close() }
                        onChaptersClick()
                    },
                    onShortsClick = {
                        coroutineScope.launch { drawerState.close() }
                        onOpenShorts(null)
                    },
                    onQuizClick = {
                        coroutineScope.launch { drawerState.close() }
                        onOpenQuiz()
                    },
                    onWallpapersClick = {
                        coroutineScope.launch { drawerState.close() }
                        onOpenWallpapers()
                    },
                    onJapaClick = {
                        coroutineScope.launch { drawerState.close() }
                        onOpenJapaMala()
                    },
                    onBookmarksClick = {
                        coroutineScope.launch { drawerState.close() }
                        onBookmarksClick()
                    },
                    onHistoryClick = {
                        coroutineScope.launch { drawerState.close() }
                        onHistoryClick()
                    },
                    onDailyShlokaNotifClick = {
                        coroutineScope.launch { drawerState.close() }
                        onDailyShlokaNotifClick()
                    },
                    onVoiceSettingsClick = {
                        coroutineScope.launch { drawerState.close() }
                        onVoiceSettingsClick()
                    },
                    onShareClick = {
                        coroutineScope.launch { drawerState.close() }
                        onShareClick()
                    },
                    onAboutClick = {
                        coroutineScope.launch { drawerState.close() }
                        onAboutClick()
                    },
                    onGoldClick = {
                        coroutineScope.launch { drawerState.close() }
                        navigateTo(Screen.GOLD)
                    }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = if (currentScreen == Screen.SHORTS) WindowInsets(0, 0, 0, 0) else WindowInsets.safeDrawing,
                topBar = {
                    if (currentScreen != Screen.SHORTS) {
                        GitaTopBar(
                            currentScreen = currentScreen,
                            appLanguage = appLanguage,
                            isDarkMode = isDarkMode,
                            onMenuClick = { coroutineScope.launch { drawerState.open() } },
                            onBackClick = onBackClick,
                            onLanguageToggle = onLanguageToggle,
                            onDarkModeToggle = onDarkModeToggle,
                            onAiChatClick = onAiChatClick,
                            onApiKeyClick = onApiKeyClick,
                            onGoldClick = { navigateTo(Screen.GOLD) }
                        )
                    }
                },
                bottomBar = {
                    if (currentScreen != Screen.SHORTS && currentScreen != Screen.JAPA_MALA && currentScreen != Screen.GOLD) {
                        GitaBottomNavigationBar(
                            currentScreen = currentScreen,
                            appLanguage = appLanguage,
                            onTabSelected = { tab ->
                                when (tab) {
                                    is BottomNavTab.Home -> navigateTo(Screen.HOME)
                                    is BottomNavTab.Shorts -> onOpenShorts(null)
                                    is BottomNavTab.Chapters -> navigateTo(Screen.CHAPTERS_LIST)
                                    is BottomNavTab.AiGuide -> onOpenAiChat()
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (currentScreen == Screen.SHORTS || currentScreen == Screen.JAPA_MALA) Modifier else Modifier.padding(innerPadding))
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        if (targetState == Screen.JAPA_MALA || initialState == Screen.JAPA_MALA) {
                            (fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                             scaleIn(initialScale = 0.95f, animationSpec = tween(280, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                                    scaleOut(targetScale = 0.98f, animationSpec = tween(200, easing = FastOutSlowInEasing))
                                )
                        } else if (targetState == Screen.WALLPAPERS || initialState == Screen.WALLPAPERS) {
                            (fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)) +
                             scaleIn(initialScale = 0.95f, animationSpec = tween(280, easing = FastOutSlowInEasing)))
                                .togetherWith(fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)))
                        } else if (targetState == Screen.SEARCH || initialState == Screen.SEARCH) {
                            (fadeIn(animationSpec = tween(240, easing = FastOutSlowInEasing)) +
                             slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(240, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing)) +
                                    slideOutVertically(targetOffsetY = { 50 }, animationSpec = tween(180, easing = FastOutSlowInEasing))
                                )
                        } else if (targetState == Screen.SHORTS || initialState == Screen.SHORTS) {
                            fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing))
                                .togetherWith(fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)))
                        } else if (targetState.ordinal > initialState.ordinal) {
                            // Forward navigation: Slide in from right (22%), previous slides slightly left (-12%)
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> (fullWidth * 0.22f).toInt() },
                                animationSpec = tween(280, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> -(fullWidth * 0.12f).toInt() },
                                        animationSpec = tween(240, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
                                )
                        } else {
                            // Backward navigation: Slide in from left (-12%), current slides out to right (+22%)
                            (slideInHorizontally(
                                initialOffsetX = { fullWidth -> -(fullWidth * 0.12f).toInt() },
                                animationSpec = tween(280, easing = FastOutSlowInEasing)
                            ) + fadeIn(animationSpec = tween(280, easing = FastOutSlowInEasing)))
                                .togetherWith(
                                    slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> (fullWidth * 0.22f).toInt() },
                                        animationSpec = tween(240, easing = FastOutSlowInEasing)
                                    ) + fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
                                )
                        }
                    },
                    label = "screen_navigation_animated_content",
                    modifier = Modifier.fillMaxSize()
                ) { targetScreen ->
                    when (targetScreen) {
                        Screen.HOME -> {
                            HomeScreen(
                                appLanguage = appLanguage,
                                searchQuery = searchQuery,
                                recentPosition = recentPosition,
                                chapters = GitaData.CHAPTERS,
                                shlokaOfTheDay = shlokaOfTheDay,
                                isBookmarked = isShlokaOfDayBookmarked,
                                sadhanaStreak = sadhanaStreak,
                                quizState = dailyQuizState,
                                onSearchQueryChange = onSearchQueryChange,
                                onChapterClick = onChapterClick,
                                onVerseClick = onVerseClick,
                                onPlayVerseAudio = onPlayVerseAudio,
                                onToggleBookmark = onToggleBookmark,
                                onBookmarkPageClick = onBookmarkPageClick,
                                onOpenAiChat = onOpenAiChat,
                                onMoodClick = onMoodClick,
                                onShareStory = onShareStory,
                                onOpenShorts = onOpenShorts,
                                onViewAllChaptersClick = { navigateTo(Screen.CHAPTERS_LIST) },
                                onOpenQuiz = onOpenQuiz,
                                onOpenWallpapers = onOpenWallpapers,
                                onOpenJapaMala = onOpenJapaMala,
                                onSearchClick = { navigateTo(Screen.SEARCH) }
                            )
                        }

                        Screen.CHAPTERS_LIST -> {
                            ChaptersListScreen(
                                appLanguage = appLanguage,
                                chapters = GitaData.CHAPTERS,
                                onChapterClick = onChapterClick
                            )
                        }

                        Screen.CHAPTER_DETAIL -> {
                            selectedChapter?.let { chapter ->
                                ChapterScreen(
                                    chapter = chapter,
                                    verses = currentVerseList,
                                    appLanguage = appLanguage,
                                    activePlayingVerse = activePlayingVerse,
                                    isPlayingAudio = isPlayingAudio,
                                    isBookmarked = isVerseBookmarked,
                                    aiInsightText = aiInsightText,
                                    isInsightLoading = isInsightLoading,
                                    selectedVerseForInsight = selectedVerseForInsight,
                                    onVerseClick = onVerseClick,
                                    onPlayVerseAudio = onPlayVerseAudio,
                                    onToggleBookmark = onToggleBookmark,
                                    onFetchAiInsight = onFetchAiInsight,
                                    onShareStory = onShareStory,
                                    onOpenShorts = { onOpenShorts(it) }
                                )
                            } ?: run {
                                navigateTo(Screen.HOME)
                            }
                        }

                        Screen.SHORTS_CHAPTERS -> {
                            ShortsChaptersScreen(
                                appLanguage = appLanguage,
                                chapters = GitaData.CHAPTERS,
                                onChapterVideoClick = { chapterId ->
                                    onSelectShortsChapter(chapterId)
                                },
                                onPlayAllVideosClick = {
                                    onOpenShorts(GitaData.getVerse(1, 1))
                                }
                            )
                        }

                        Screen.SHORTS_VERSES_GRID -> {
                            selectedShortsChapter?.let { chapter ->
                                ShortsChapterVersesScreen(
                                    chapter = chapter,
                                    verses = selectedShortsVerses,
                                    appLanguage = appLanguage,
                                    onVerseClick = { verse ->
                                        onOpenShorts(verse)
                                    },
                                    onPlayAllClick = {
                                        val firstVerse = selectedShortsVerses.firstOrNull() ?: GitaData.getVerse(chapter.id, 1)
                                        onOpenShorts(firstVerse)
                                    },
                                    onBackClick = {
                                        navigateTo(Screen.SHORTS_CHAPTERS)
                                    }
                                )
                            } ?: run {
                                navigateTo(Screen.SHORTS_CHAPTERS)
                            }
                        }

                        Screen.SHORTS -> {
                            GitaShortsFeedScreen(
                                initialVerse = shortsTargetVerse,
                                appLanguage = appLanguage,
                                shortsLanguage = shortsLanguage,
                                bookmarks = bookmarks,
                                onToggleBookmark = onToggleBookmark,
                                onLanguageChange = onShortsLanguageChange,
                                onBackClick = onBackClick,
                                onVerseSelectedForAudio = { verse ->
                                    onPlayVerseAudio(verse)
                                    onOpenFullScreenPlayer()
                                },
                                onGoldClick = { navigateTo(Screen.GOLD) }
                            )
                        }

                        Screen.BOOKMARKS -> {
                            BookmarksScreen(
                                bookmarks = bookmarks,
                                appLanguage = appLanguage,
                                onVerseClick = onVerseClick,
                                onPlayAudio = onPlayVerseAudio,
                                onRemoveBookmark = onToggleBookmark
                            )
                        }

                        Screen.SEARCH -> {
                            SearchScreen(
                                searchQuery = searchQuery,
                                searchResults = searchResults,
                                searchHistory = searchHistory,
                                appLanguage = appLanguage,
                                onSearchQueryChange = onSearchQueryChange,
                                onSaveQuery = onSaveQuery,
                                onDeleteHistoryQuery = onDeleteHistoryQuery,
                                onClearHistory = onClearHistory,
                                onVerseClick = onVerseClick,
                                onPlayAudio = onPlayVerseAudio,
                                onBackClick = { navigateTo(Screen.HOME) }
                            )
                        }

                        Screen.WALLPAPERS -> {
                            WallpaperGalleryScreen(
                                appLanguage = appLanguage,
                                selectedCategory = wallpaperCategory,
                                onSelectCategory = onSelectWallpaperCategory,
                                onWallpaperClick = { onSelectWallpaper(it) },
                                onBackClick = { navigateTo(Screen.HOME) }
                            )
                        }

                        Screen.JAPA_MALA -> {
                            JapaMalaScreen(
                                state = japaMalaState,
                                appLanguage = appLanguage,
                                onIncrementBead = onIncrementJapaBead,
                                onResetMala = onResetJapaMala,
                                onSelectMantra = onSelectJapaMantra,
                                onSetTargetMalas = onSetTargetJapaMalas,
                                onToggleHaptic = onToggleJapaHaptic,
                                onToggleSound = onToggleJapaSound,
                                onDismissCelebration = onDismissJapaCelebration,
                                onBack = { navigateTo(Screen.HOME) }
                            )
                        }

                        Screen.GOLD -> {
                            GitaGoldScreen(
                                appLanguage = appLanguage,
                                onBackClick = { navigateTo(Screen.HOME) }
                            )
                        }

                        else -> {
                            navigateTo(Screen.HOME)
                        }
                    }
                }

                // Floating Audio Player Bar at bottom (Hidden when watching Shorts/Reels, Japa Mala, or Gold Screen)
                if (activePlayingVerse != null && currentScreen != Screen.SHORTS && currentScreen != Screen.JAPA_MALA && currentScreen != Screen.GOLD) {
                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        AudioPlayerBar(
                            activeVerse = activePlayingVerse,
                            isPlaying = isPlayingAudio,
                            audioMode = audioMode,
                            narrationLanguage = narrationLanguage,
                            speed = audioSpeed,
                            autoContinue = autoContinueAudio,
                            appLanguage = appLanguage,
                            isLoading = isAudioLoading,
                            onPlayPauseToggle = onPlayPauseToggle,
                            onNextClick = onNextClick,
                            onPrevClick = onPrevClick,
                            onSpeedChange = onSpeedChange,
                            onAudioModeChange = onAudioModeChange,
                            onNarrationLangChange = onNarrationLangChange,
                            onAutoContinueToggle = onAutoContinueToggle,
                            onVerseCardClick = onOpenFullScreenPlayer,
                            onVoiceSettingsClick = onVoiceSettingsClick
                        )
                    }
                }

                // Full Screen Spotify-Style Audio Player Screen
                if (showFullScreenPlayer) {
                    (activePlayingVerse ?: selectedVerse)?.let { verse ->
                        BackHandler {
                            onCloseFullScreenPlayer()
                            onDismissVerseDetail()
                        }
                        FullScreenPlayerScreen(
                            verse = verse,
                            isPlaying = isPlayingAudio,
                            audioMode = audioMode,
                            appLanguage = appLanguage,
                            speed = audioSpeed,
                            autoContinue = autoContinueAudio,
                            isLoading = isAudioLoading,
                            isBookmarked = isVerseBookmarked(verse.verseKey),
                            audioProgress = audioProgress,
                            audioCurrentTime = audioCurrentTime,
                            audioTotalTime = audioTotalTime,
                            sleepTimerOption = sleepTimerOption,
                            sleepTimerSecondsRemaining = sleepTimerSecondsRemaining,
                            aiInsightText = aiInsightText,
                            isInsightLoading = isInsightLoading,
                            onClose = {
                                onCloseFullScreenPlayer()
                                onDismissVerseDetail()
                            },
                            onPlayPauseToggle = onPlayPauseToggle,
                            onNextClick = onNextClick,
                            onPrevClick = onPrevClick,
                            onSpeedChange = onSpeedChange,
                            onAudioModeChange = onAudioModeChange,
                            onAutoContinueToggle = onAutoContinueToggle,
                            onToggleBookmark = { onToggleBookmark(verse) },
                            onVoiceSettingsClick = onVoiceSettingsClick,
                            onSeek = onSeek,
                            onSeekStarted = onSeekStarted,
                            onFetchAiInsight = { onFetchAiInsight(verse) },
                            onJapaClick = {
                                onCloseFullScreenPlayer()
                                onOpenJapaMala()
                            },
                            onSleepTimerClick = onOpenSleepTimerDialog,
                            onShareStory = { onShareStory(verse) },
                            onOpenShorts = { onOpenShorts(verse) }
                        )
                    }
                }

                // Gita Mood / Problem Solver Dialog
                if (showMoodDialog && selectedMood != null) {
                    GitaMoodDialog(
                        mood = selectedMood,
                        appLanguage = appLanguage,
                        activePlayingVerse = activePlayingVerse,
                        isPlayingAudio = isPlayingAudio,
                        onDismiss = onCloseMoodDialog,
                        onVerseClick = { onVerseClick(it) },
                        onPlayVerseAudio = { onPlayVerseAudio(it) }
                    )
                }

                // Sleep Timer Dialog
                if (showSleepTimerDialog) {
                    SleepTimerDialog(
                        appLanguage = appLanguage,
                        currentOption = sleepTimerOption,
                        secondsRemaining = sleepTimerSecondsRemaining,
                        onDismiss = onCloseSleepTimerDialog,
                        onSelectOption = onSelectSleepTimer
                    )
                }

                // Voice Settings Dialog
                if (showVoiceSettingsDialog) {
                    VoiceSettingsDialog(
                        appLanguage = appLanguage,
                        currentAudioMode = audioMode,
                        currentVoiceStyle = voiceStyle,
                        currentSpeed = audioSpeed,
                        currentNarrationLang = narrationLanguage,
                        onAudioModeSelected = onAudioModeChange,
                        onVoiceStyleSelected = onVoiceStyleSelected,
                        onSpeedSelected = onSpeedChange,
                        onNarrationLangSelected = onNarrationLangChange,
                        onTestSample = onTestSample,
                        onDismiss = onCloseVoiceSettingsDialog
                    )
                }

                // Gita AI Interactive Chat Dialog
                if (showAiDialog) {
                    GitaAiChatDialog(
                        appLanguage = appLanguage,
                        messages = aiChatMessages,
                        isLoading = isAiChatLoading,
                        customApiKey = customApiKey,
                        onSendMessage = onSendMessage,
                        onOpenApiKeySetup = onApiKeyClick,
                        onDismiss = onCloseAiDialog
                    )
                }

                // Gemini API Key Settings Dialog
                if (showApiKeyDialog) {
                    GeminiApiKeyDialog(
                        appLanguage = appLanguage,
                        currentApiKey = customApiKey,
                        onSaveApiKey = onSaveApiKey,
                        onDismiss = onCloseApiKeyDialog
                    )
                }

                // Reading History Dialog
                if (showHistoryDialog) {
                    ReadingHistoryDialog(
                        appLanguage = appLanguage,
                        recentPosition = recentPosition,
                        readingHistory = readingHistory,
                        onVerseClick = onVerseClick,
                        onDismiss = onCloseHistoryDialog
                    )
                }

                // User Profile & Account Dialog
                if (showProfileDialog) {
                    UserProfileDialog(
                        userProfile = userProfile,
                        appLanguage = appLanguage,
                        sadhanaStreak = sadhanaStreak,
                        onDismiss = onCloseProfileDialog,
                        onRegister = onRegister,
                        onLogout = onLogout
                    )
                }

                // About Gita Saathi Dialog
                if (showAboutDialog) {
                    AboutGitaSaathiDialog(
                        appLanguage = appLanguage,
                        onDismiss = onCloseAboutDialog
                    )
                }

                // Daily Notification Dialog
                if (showNotifDialog) {
                    DailyNotificationDialog(
                        appLanguage = appLanguage,
                        onDismiss = onCloseNotifDialog
                    )
                }

                // Daily Gita Quiz Dialog
                if (showQuizDialog && dailyQuizState != null) {
                    GitaQuizDialog(
                        quizState = dailyQuizState,
                        appLanguage = appLanguage,
                        onDismiss = onCloseQuiz,
                        onSubmitQuiz = onSubmitQuiz
                    )
                }

                // HD Krishna Wallpaper Full-Screen Detail Dialog
                selectedWallpaper?.let { wp ->
                    WallpaperDetailDialog(
                        wallpaper = wp,
                        appLanguage = appLanguage,
                        onDismiss = { onSelectWallpaper(null) }
                    )
                }

                // Audio Error / Notice Message Dialog
                if (audioErrorMessage != null) {
                    val isNetworkNotice = audioErrorMessage?.contains("internet", ignoreCase = true) == true ||
                            audioErrorMessage?.contains("इंटरनेट", ignoreCase = true) == true
                    val dialogTitle = if (isNetworkNotice) {
                        if (appLanguage == AppLanguage.HINDI) "इंटरनेट कनेक्शन" else "Internet Connection"
                    } else {
                        if (appLanguage == AppLanguage.HINDI) "ऑडियो सूचना" else "Audio Notice"
                    }
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = onClearAudioError,
                        title = {
                            androidx.compose.material3.Text(
                                text = dialogTitle
                            )
                        },
                        text = {
                            androidx.compose.material3.Text(text = audioErrorMessage ?: "")
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = onClearAudioError) {
                                androidx.compose.material3.Text(text = if (appLanguage == AppLanguage.HINDI) "ठीक है" else "OK")
                            }
                        }
                    )
                }
            }
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun GitaSaathiAppPreview() {
    GitaSaathiTheme {
        GitaSaathiAppContent(
            currentScreen = Screen.HOME,
            appLanguage = AppLanguage.ENGLISH,
            isDarkMode = false,
            selectedChapter = null,
            currentVerseList = emptyList(),
            selectedVerse = null,
            searchQuery = "",
            searchResults = emptyList(),
            isPlayingAudio = false,
            activePlayingVerse = null,
            audioMode = AudioMode.SANSKRIT_SHLOKA,
            voiceStyle = VoiceStyle.DEVOTIONAL,
            audioSpeed = 1.0f,
            narrationLanguage = AppLanguage.ENGLISH,
            autoContinueAudio = true,
            bookmarks = emptyList(),
            recentPosition = null,
            aiInsightText = null,
            isInsightLoading = false,
            selectedVerseForInsight = null,
            showHistoryDialog = false,
            showAboutDialog = false,
            showNotifDialog = false,
            showProfileDialog = false,
            userProfile = UserProfile(),
            readingHistory = emptyList(),
            searchHistory = emptyList(),
            customApiKey = "",
            aiChatMessages = emptyList(),
            isAiChatLoading = false,
            showAiDialog = false,
            showApiKeyDialog = false,
            showVoiceSettingsDialog = false,
            showFullScreenPlayer = false,
            isAudioLoading = false,
            audioErrorMessage = null,
            audioProgress = 0f,
            audioCurrentTime = 0L,
            audioTotalTime = 0L,
            shlokaOfTheDay = GitaData.getShlokaOfTheDay(),
            isShlokaOfDayBookmarked = false,
            onProfileClick = {},
            onChaptersClick = {},
            onBookmarksClick = {},
            onHistoryClick = {},
            onDailyShlokaNotifClick = {},
            onVoiceSettingsClick = {},
            onShareClick = {},
            onAboutClick = {},
            onBackClick = {},
            onLanguageToggle = {},
            onDarkModeToggle = {},
            onAiChatClick = {},
            onApiKeyClick = {},
            onSearchQueryChange = {},
            onChapterClick = {},
            onVerseClick = {},
            onPlayVerseAudio = {},
            onToggleBookmark = {},
            onBookmarkPageClick = {},
            onOpenAiChat = {},
            onFetchAiInsight = {},
            onSaveQuery = {},
            onDeleteHistoryQuery = {},
            onClearHistory = {},
            onPlayPauseToggle = {},
            onNextClick = {},
            onPrevClick = {},
            onSpeedChange = {},
            onAudioModeChange = {},
            onNarrationLangChange = {},
            onAutoContinueToggle = {},
            onVoiceStyleSelected = {},
            onSeek = {},
            onSeekStarted = {},
            onTestSample = { _, _, _ -> },
            onSendMessage = {},
            onSaveApiKey = {},
            onRegister = { _, _ -> },
            onLogout = {},
            onCloseProfileDialog = {},
            onCloseNotifDialog = {},
            onCloseAboutDialog = {},
            onCloseHistoryDialog = {},
            onCloseVoiceSettingsDialog = {},
            onCloseAiDialog = {},
            onCloseApiKeyDialog = {},
            onOpenFullScreenPlayer = {},
            onCloseFullScreenPlayer = {},
            onDismissVerseDetail = {},
            onClearAudioError = {},
            isVerseBookmarked = { false },
            navigateTo = {}
        )
    }
}
