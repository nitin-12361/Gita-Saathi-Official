package com.nkapps.gitasaathi.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nkapps.gitasaathi.audio.GitaAudioEngine
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.AudioMode
import com.nkapps.gitasaathi.data.BookmarkEntity
import com.nkapps.gitasaathi.data.Chapter
import com.nkapps.gitasaathi.data.GitaData
import com.nkapps.gitasaathi.data.GitaDatabase
import com.nkapps.gitasaathi.data.RecentPositionEntity
import com.nkapps.gitasaathi.data.SearchHistoryEntity
import com.nkapps.gitasaathi.data.UserManager
import com.nkapps.gitasaathi.data.UserProfile
import com.nkapps.gitasaathi.data.Verse
import com.nkapps.gitasaathi.data.VoiceStyle
import com.nkapps.gitasaathi.network.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

import android.content.Context
import com.nkapps.gitasaathi.data.GitaMood
import com.nkapps.gitasaathi.data.GitaMoodData
import com.nkapps.gitasaathi.data.SadhanaStreakManager
import com.nkapps.gitasaathi.data.StreakData
import com.nkapps.gitasaathi.data.DailyQuizState
import com.nkapps.gitasaathi.data.GitaQuizManager
import com.nkapps.gitasaathi.data.SubmitQuizResult
import com.nkapps.gitasaathi.data.GitaWallpaper
import com.nkapps.gitasaathi.data.GitaWallpaperData
import com.nkapps.gitasaathi.data.WallpaperCategory
import com.nkapps.gitasaathi.data.JapaMalaManager
import com.nkapps.gitasaathi.data.JapaMalaState
import com.nkapps.gitasaathi.ui.components.GitaCardShareHelper
import com.nkapps.gitasaathi.widget.DailyShlokaWidgetProvider

enum class Screen {
    HOME,
    CHAPTERS_LIST,
    CHAPTER_DETAIL,
    VERSE_DETAIL,
    BOOKMARKS,
    SEARCH,
    SHORTS_CHAPTERS,
    SHORTS_VERSES_GRID,
    SHORTS,
    WALLPAPERS,
    JAPA_MALA,
    GOLD
}

enum class SleepTimerOption(val durationSeconds: Long, val labelHindi: String, val labelEnglish: String) {
    OFF(0L, "बंद", "Off"),
    MINUTES_15(15 * 60L, "15 मिनट", "15 Mins"),
    MINUTES_30(30 * 60L, "30 मिनट", "30 Mins"),
    MINUTES_45(45 * 60L, "45 मिनट", "45 Mins"),
    MINUTES_60(60 * 60L, "60 मिनट", "60 Mins"),
    END_OF_VERSE(-1L, "श्लोक समाप्ति पर", "End of Verse")
}

class GitaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GitaDatabase.getDatabase(application)
    private val bookmarkDao = db.bookmarkDao()

    val bookmarks: StateFlow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentPosition: StateFlow<RecentPositionEntity?> = bookmarkDao.getRecentPosition()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val searchHistory: StateFlow<List<SearchHistoryEntity>> = bookmarkDao.getSearchHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val userManager = UserManager.getInstance(application)
    val userProfile: StateFlow<UserProfile> = userManager.userProfile

    private val quizManager = GitaQuizManager.getInstance(application)
    val dailyQuizState: StateFlow<DailyQuizState> = quizManager.quizState

    private val _showQuizDialog = MutableStateFlow(false)
    val showQuizDialog: StateFlow<Boolean> = _showQuizDialog.asStateFlow()

    fun openQuizDialog() {
        quizManager.refreshState()
        _showQuizDialog.value = true
    }

    fun closeQuizDialog() {
        _showQuizDialog.value = false
    }

    fun submitQuizScore(score: Int): SubmitQuizResult {
        return quizManager.submitQuizResult(score)
    }

    // SharedPreferences for persistent settings
    private val prefs = application.getSharedPreferences("gita_saathi_prefs", android.content.Context.MODE_PRIVATE)

    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _appLanguage = MutableStateFlow(
        if (prefs.getString("app_language", "ENGLISH") == "HINDI") AppLanguage.HINDI else AppLanguage.ENGLISH
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _selectedChapter = MutableStateFlow<Chapter?>(null)
    val selectedChapter: StateFlow<Chapter?> = _selectedChapter.asStateFlow()

    private val _currentVerseList = MutableStateFlow<List<Verse>>(emptyList())
    val currentVerseList: StateFlow<List<Verse>> = _currentVerseList.asStateFlow()

    private val _selectedVerse = MutableStateFlow<Verse?>(null)
    val selectedVerse: StateFlow<Verse?> = _selectedVerse.asStateFlow()

    private val _selectedVerseForInsight = MutableStateFlow<Verse?>(null)
    val selectedVerseForInsight: StateFlow<Verse?> = _selectedVerseForInsight.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Verse>>(emptyList())
    val searchResults: StateFlow<List<Verse>> = _searchResults.asStateFlow()

    private val _selectedShortsChapter = MutableStateFlow<Chapter?>(null)
    val selectedShortsChapter: StateFlow<Chapter?> = _selectedShortsChapter.asStateFlow()

    private val _selectedShortsVerses = MutableStateFlow<List<Verse>>(emptyList())
    val selectedShortsVerses: StateFlow<List<Verse>> = _selectedShortsVerses.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _activePlayingVerse = MutableStateFlow<Verse?>(null)
    val activePlayingVerse: StateFlow<Verse?> = _activePlayingVerse.asStateFlow()

    private val _voiceStyle = MutableStateFlow(VoiceStyle.fromId(prefs.getString("gita_voice_style", VoiceStyle.DEVOTIONAL.id)))
    val voiceStyle: StateFlow<VoiceStyle> = _voiceStyle.asStateFlow()

    private val _audioMode = MutableStateFlow(AudioMode.fromId(prefs.getString("gita_audio_mode", AudioMode.SANSKRIT_SHLOKA.id)))
    val audioMode: StateFlow<AudioMode> = _audioMode.asStateFlow()

    private val _audioSpeed = MutableStateFlow(prefs.getFloat("gita_audio_speed", 1.0f))
    val audioSpeed: StateFlow<Float> = _audioSpeed.asStateFlow()

    private val _narrationLanguage = MutableStateFlow(
        if (prefs.getString("gita_narration_lang", "ENGLISH") == "HINDI") AppLanguage.HINDI else AppLanguage.ENGLISH
    )
    val narrationLanguage: StateFlow<AppLanguage> = _narrationLanguage.asStateFlow()

    private val _autoContinueAudio = MutableStateFlow(true)
    val autoContinueAudio: StateFlow<Boolean> = _autoContinueAudio.asStateFlow()

    private val _shortsTargetVerse = MutableStateFlow<Verse?>(null)
    val shortsTargetVerse: StateFlow<Verse?> = _shortsTargetVerse.asStateFlow()

    private val _shortsLanguage = MutableStateFlow(
        if (prefs.getString("gita_shorts_lang", "HINDI") == "ENGLISH") AppLanguage.ENGLISH else AppLanguage.HINDI
    )
    val shortsLanguage: StateFlow<AppLanguage> = _shortsLanguage.asStateFlow()

    private val _aiInsightText = MutableStateFlow<String?>(null)
    val aiInsightText: StateFlow<String?> = _aiInsightText.asStateFlow()

    private val _isInsightLoading = MutableStateFlow(false)
    val isInsightLoading: StateFlow<Boolean> = _isInsightLoading.asStateFlow()

    // Gemini Custom API Key State stored in SharedPreferences
    private val _customApiKey = MutableStateFlow(prefs.getString("custom_gemini_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // Interactive Ask Gita AI Chat State
    data class AiChatMessage(val sender: String, val text: String, val timestamp: Long = System.currentTimeMillis())
    private val _aiChatMessages = MutableStateFlow<List<AiChatMessage>>(emptyList())
    val aiChatMessages: StateFlow<List<AiChatMessage>> = _aiChatMessages.asStateFlow()

    private val _isAiChatLoading = MutableStateFlow(false)
    val isAiChatLoading: StateFlow<Boolean> = _isAiChatLoading.asStateFlow()

    private val _showAiDialog = MutableStateFlow(false)
    val showAiDialog: StateFlow<Boolean> = _showAiDialog.asStateFlow()

    private val _showApiKeyDialog = MutableStateFlow(false)
    val showApiKeyDialog: StateFlow<Boolean> = _showApiKeyDialog.asStateFlow()

    private val _showVoiceSettingsDialog = MutableStateFlow(false)
    val showVoiceSettingsDialog: StateFlow<Boolean> = _showVoiceSettingsDialog.asStateFlow()

    private val _showHistoryDialog = MutableStateFlow(false)
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    private val _showAboutDialog = MutableStateFlow(false)
    val showAboutDialog: StateFlow<Boolean> = _showAboutDialog.asStateFlow()

    private val _showNotifDialog = MutableStateFlow(false)
    val showNotifDialog: StateFlow<Boolean> = _showNotifDialog.asStateFlow()

    private val _readingHistory = MutableStateFlow<List<Verse>>(loadReadingHistoryFromPrefs())
    val readingHistory: StateFlow<List<Verse>> = _readingHistory.asStateFlow()

    private val _showFullScreenPlayer = MutableStateFlow(false)
    val showFullScreenPlayer: StateFlow<Boolean> = _showFullScreenPlayer.asStateFlow()

    private val _isAudioLoading = MutableStateFlow(false)
    val isAudioLoading: StateFlow<Boolean> = _isAudioLoading.asStateFlow()

    private val _audioErrorMessage = MutableStateFlow<String?>(null)
    val audioErrorMessage: StateFlow<String?> = _audioErrorMessage.asStateFlow()

    private val _audioProgress = MutableStateFlow(0f)
    val audioProgress: StateFlow<Float> = _audioProgress.asStateFlow()

    private val _audioCurrentTime = MutableStateFlow(0L)
    val audioCurrentTime: StateFlow<Long> = _audioCurrentTime.asStateFlow()

    private val _audioTotalTime = MutableStateFlow(0L)
    val audioTotalTime: StateFlow<Long> = _audioTotalTime.asStateFlow()

    // 1. Sadhana Streak Manager
    private val sadhanaStreakManager = SadhanaStreakManager.getInstance(application)
    val sadhanaStreak: StateFlow<StreakData> = sadhanaStreakManager.streakData

    // 2. Gita Mood & Problem Solver State
    private val _showMoodDialog = MutableStateFlow(false)
    val showMoodDialog: StateFlow<Boolean> = _showMoodDialog.asStateFlow()

    private val _selectedMood = MutableStateFlow<GitaMood?>(null)
    val selectedMood: StateFlow<GitaMood?> = _selectedMood.asStateFlow()

    // 3. Digital Japa Mala (108 Beads Counter) State & Manager
    val japaMalaManager = JapaMalaManager(application)
    val japaMalaState: StateFlow<JapaMalaState> = japaMalaManager.state

    private val _showJapaDialog = MutableStateFlow(false)
    val showJapaDialog: StateFlow<Boolean> = _showJapaDialog.asStateFlow()

    private val _isJapaModeActive = MutableStateFlow(false)
    val isJapaModeActive: StateFlow<Boolean> = _isJapaModeActive.asStateFlow()

    private val _targetJapaCount = MutableStateFlow(108)
    val targetJapaCount: StateFlow<Int> = _targetJapaCount.asStateFlow()

    private val _currentJapaCount = MutableStateFlow(0)
    val currentJapaCount: StateFlow<Int> = _currentJapaCount.asStateFlow()

    // 4. Sleep Timer State
    private val _showSleepTimerDialog = MutableStateFlow(false)
    val showSleepTimerDialog: StateFlow<Boolean> = _showSleepTimerDialog.asStateFlow()

    private val _sleepTimerOption = MutableStateFlow(SleepTimerOption.OFF)
    val sleepTimerOption: StateFlow<SleepTimerOption> = _sleepTimerOption.asStateFlow()

    private val _sleepTimerSecondsRemaining = MutableStateFlow(0L)
    val sleepTimerSecondsRemaining: StateFlow<Long> = _sleepTimerSecondsRemaining.asStateFlow()

    // 5. HD Krishna Wallpapers State
    private val _selectedWallpaper = MutableStateFlow<GitaWallpaper?>(null)
    val selectedWallpaper: StateFlow<GitaWallpaper?> = _selectedWallpaper.asStateFlow()

    private val _wallpaperCategory = MutableStateFlow(WallpaperCategory.ALL)
    val wallpaperCategory: StateFlow<WallpaperCategory> = _wallpaperCategory.asStateFlow()

    fun selectWallpaper(wallpaper: GitaWallpaper?) {
        _selectedWallpaper.value = wallpaper
    }

    fun setWallpaperCategory(category: WallpaperCategory) {
        _wallpaperCategory.value = category
    }

    private var sleepTimerJob: Job? = null
    private var isUserSeeking = false
    private var audioEngine: GitaAudioEngine? = null

    init {
        audioEngine = GitaAudioEngine(
            context = application,
            onPlaybackCompleted = { onAudioCompleted() },
            onLoadingStateChanged = { loading -> _isAudioLoading.value = loading },
            onAudioError = { errorMsg ->
                _isPlayingAudio.value = false
                _isAudioLoading.value = false
                _audioErrorMessage.value = errorMsg
            }
        )

        // Poll audio progress
        viewModelScope.launch {
            while (true) {
                if (_isPlayingAudio.value && !isUserSeeking) {
                    val pos = audioEngine?.getCurrentPosition() ?: 0
                    val dur = audioEngine?.getDuration() ?: 0
                    _audioCurrentTime.value = pos.toLong()
                    _audioTotalTime.value = dur.toLong()
                    if (dur > 0) {
                        _audioProgress.value = pos.toFloat() / dur.toFloat()
                    } else {
                        _audioProgress.value = 0f
                    }
                }
                delay(500)
            }
        }
    }

    fun clearAudioError() {
        _audioErrorMessage.value = null
    }

    fun setCustomApiKey(key: String) {
        val trimmed = key.trim()
        _customApiKey.value = trimmed
        prefs.edit().putString("custom_gemini_key", trimmed).apply()
    }

    fun openAiDialog() {
        _showAiDialog.value = true
    }

    fun closeAiDialog() {
        _showAiDialog.value = false
    }

    fun openApiKeyDialog() {
        _showApiKeyDialog.value = true
    }

    fun closeApiKeyDialog() {
        _showApiKeyDialog.value = false
    }

    fun openHistoryDialog() {
        _showHistoryDialog.value = true
    }

    fun closeHistoryDialog() {
        _showHistoryDialog.value = false
    }

    fun openAboutDialog() {
        _showAboutDialog.value = true
    }

    fun closeAboutDialog() {
        _showAboutDialog.value = false
    }

    fun openNotifDialog() {
        _showNotifDialog.value = true
    }

    fun closeNotifDialog() {
        _showNotifDialog.value = false
    }

    fun openProfileDialog() {
        _showProfileDialog.value = true
    }

    fun closeProfileDialog() {
        _showProfileDialog.value = false
    }

    fun resetNavigationAndCloseDialogs(targetScreen: Screen = Screen.HOME) {
        _showFullScreenPlayer.value = false
        _selectedVerse.value = null
        _aiInsightText.value = null
        _selectedVerseForInsight.value = null
        _showHistoryDialog.value = false
        _showAboutDialog.value = false
        _showVoiceSettingsDialog.value = false
        _showApiKeyDialog.value = false
        _showAiDialog.value = false
        _showNotifDialog.value = false
        _showProfileDialog.value = false
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _currentScreen.value = targetScreen
    }

    fun registerUser(name: String, email: String, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        userManager.registerOrUpdateUser(name, email, onComplete)
    }

    fun logoutUser() {
        userManager.logout()
    }

    fun sendAiChatMessage(userQuery: String) {
        if (userQuery.isBlank()) return
        val userMsg = AiChatMessage("user", userQuery)
        _aiChatMessages.value = _aiChatMessages.value + userMsg
        _isAiChatLoading.value = true

        viewModelScope.launch {
            val keyToUse = _customApiKey.value.ifBlank { null }
            val result = GeminiClient.askGitaAi(userQuery, _appLanguage.value, keyToUse)
            _isAiChatLoading.value = false

            val replyText = result.getOrElse { err ->
                if (_appLanguage.value == AppLanguage.HINDI) {
                    "क्षमा करें, AI उत्तर प्राप्त करने में त्रुटि हुई: ${err.localizedMessage ?: "कृपया पुनः प्रयास करें"}"
                } else {
                    "Sorry, unable to get AI response: ${err.localizedMessage ?: "Please try again"}"
                }
            }
            _aiChatMessages.value = _aiChatMessages.value + AiChatMessage("gita_ai", replyText)
        }
    }

    fun toggleAppLanguage() {
        val next = if (_appLanguage.value == AppLanguage.HINDI) AppLanguage.ENGLISH else AppLanguage.HINDI
        _appLanguage.value = next
        _narrationLanguage.value = next
        prefs.edit()
            .putString("app_language", next.name)
            .putString("gita_narration_lang", next.name)
            .apply()
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        _narrationLanguage.value = language
        prefs.edit()
            .putString("app_language", language.name)
            .putString("gita_narration_lang", language.name)
            .apply()
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectChapter(chapterId: Int) {
        val chapter = GitaData.getChapter(chapterId)
        _selectedChapter.value = chapter
        if (chapter != null) {
            _currentVerseList.value = GitaData.getVersesForChapter(chapterId)
            _currentScreen.value = Screen.CHAPTER_DETAIL
        }
    }

    fun openFullScreenPlayer() {
        _showFullScreenPlayer.value = true
    }

    fun closeFullScreenPlayer() {
        _showFullScreenPlayer.value = false
    }

    fun selectVerse(verse: Verse?) {
        _selectedVerse.value = verse
        _aiInsightText.value = null
        if (verse != null) {
            saveRecentPosition(verse.chapterId, verse.verseId)
            _showFullScreenPlayer.value = true
            val isSameVerse = _activePlayingVerse.value?.verseKey == verse.verseKey
            if (isSameVerse) {
                if (!_isPlayingAudio.value) {
                    resumeAudio()
                }
            } else {
                playVerseAudio(verse, forceRestart = true)
            }
        } else {
            _showFullScreenPlayer.value = false
        }
    }

    fun openShortsChapters() {
        _currentScreen.value = Screen.SHORTS_CHAPTERS
    }

    fun openShortsVersesGrid(chapterId: Int) {
        val chapter = GitaData.getChapter(chapterId)
        _selectedShortsChapter.value = chapter
        _selectedShortsVerses.value = GitaData.getVersesForChapter(chapterId)
        _currentScreen.value = Screen.SHORTS_VERSES_GRID
    }

    fun openShortsForChapter(chapterId: Int) {
        openShortsVersesGrid(chapterId)
    }

    fun openShortsFeed(verse: Verse? = null) {
        _shortsTargetVerse.value = verse ?: _selectedVerse.value ?: _activePlayingVerse.value ?: GitaData.getShlokaOfTheDay()
        _showFullScreenPlayer.value = false
        if (_isPlayingAudio.value) {
            pauseAudio()
        }
        _currentScreen.value = Screen.SHORTS
    }

    fun setShortsLanguage(language: AppLanguage) {
        _shortsLanguage.value = language
        prefs.edit()
            .putString("gita_shorts_lang", language.name)
            .apply()
    }

    fun dismissVerseDetail() {
        _selectedVerse.value = null
        _showFullScreenPlayer.value = false
        _aiInsightText.value = null
        _selectedVerseForInsight.value = null
    }

    private var searchJob: Job? = null

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        } else {
            searchJob = viewModelScope.launch(Dispatchers.Default) {
                delay(120) // Non-blocking debounce
                val results = GitaData.searchVerses(query)
                withContext(Dispatchers.Main) {
                    _searchResults.value = results
                }
            }
        }
    }

    fun saveSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return
        viewModelScope.launch {
            bookmarkDao.saveSearchQuery(SearchHistoryEntity(query = trimmed))
        }
    }

    fun deleteSearchQuery(query: String) {
        viewModelScope.launch {
            bookmarkDao.deleteSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            bookmarkDao.clearSearchHistory()
        }
    }

    fun toggleBookmark(verse: Verse) {
        viewModelScope.launch {
            val key = verse.verseKey
            val isBookmarkedNow = bookmarks.value.any { it.verseKey == key }
            if (isBookmarkedNow) {
                bookmarkDao.deleteBookmark(key)
                userManager.updateStats(_readingHistory.value.size, (bookmarks.value.size - 1).coerceAtLeast(0))
            } else {
                bookmarkDao.insertBookmark(
                    BookmarkEntity(
                        verseKey = key,
                        chapterId = verse.chapterId,
                        verseId = verse.verseId
                    )
                )
                userManager.updateStats(_readingHistory.value.size, bookmarks.value.size + 1)
            }
        }
    }

    fun isVerseBookmarked(verseKey: String): Boolean {
        return bookmarks.value.any { it.verseKey == verseKey }
    }

    fun seekTo(progress: Float) {
        val dur = audioEngine?.getDuration() ?: 0
        if (dur > 0) {
            val pos = (progress * dur).toInt()
            audioEngine?.seekTo(pos)
            _audioProgress.value = progress
            _audioCurrentTime.value = pos.toLong()
        }
        isUserSeeking = false
    }

    fun onSeekStarted() {
        isUserSeeking = true
    }

    fun pauseAudio() {
        audioEngine?.pauseAudio()
        _isPlayingAudio.value = false
    }

    fun resumeAudio() {
        val verse = _activePlayingVerse.value ?: _selectedVerse.value ?: GitaData.getShlokaOfTheDay()
        val resumed = audioEngine?.resumeAudio() == true
        if (resumed) {
            _isPlayingAudio.value = true
            _audioErrorMessage.value = null
        } else {
            playVerseAudio(verse, forceRestart = true)
        }
    }

    fun togglePlayPause() {
        if (_isPlayingAudio.value) {
            pauseAudio()
        } else {
            resumeAudio()
        }
    }

    fun playVerseAudio(verse: Verse, forceRestart: Boolean = false) {
        val isSameVerse = _activePlayingVerse.value?.verseKey == verse.verseKey
        if (!forceRestart && isSameVerse) {
            if (_isPlayingAudio.value && !isUserSeeking) return
            if (audioEngine?.isPaused() == true) {
                resumeAudio()
                return
            }
        }

        _audioProgress.value = 0f
        _audioCurrentTime.value = 0
        _activePlayingVerse.value = verse
        _isPlayingAudio.value = true
        _audioErrorMessage.value = null
        audioEngine?.playVerse(
            verse = verse,
            audioMode = _audioMode.value,
            language = _narrationLanguage.value,
            speed = _audioSpeed.value,
            voiceStyle = _voiceStyle.value,
            customApiKey = _customApiKey.value.ifBlank { null }
        )
        saveRecentPosition(verse.chapterId, verse.verseId)
    }

    fun setAudioMode(mode: AudioMode) {
        _audioMode.value = mode
        prefs.edit().putString("gita_audio_mode", mode.id).apply()
        val verse = _activePlayingVerse.value
        if (verse != null && _isPlayingAudio.value) {
            playVerseAudio(verse, forceRestart = true)
        }
    }

    fun setAudioSpeed(speed: Float) {
        _audioSpeed.value = speed
        audioEngine?.setSpeed(speed)
        prefs.edit().putFloat("gita_audio_speed", speed).apply()
    }

    fun setNarrationLanguage(language: AppLanguage) {
        _narrationLanguage.value = language
        prefs.edit().putString("gita_narration_lang", language.name).apply()
        val verse = _activePlayingVerse.value
        if (verse != null && _isPlayingAudio.value) {
            playVerseAudio(verse, forceRestart = true)
        }
    }

    fun setVoiceStyle(style: VoiceStyle) {
        _voiceStyle.value = style
        prefs.edit().putString("gita_voice_style", style.id).apply()
        audioEngine?.setVoiceStyle(style)
        val verse = _activePlayingVerse.value
        if (verse != null && _isPlayingAudio.value) {
            playVerseAudio(verse, forceRestart = true)
        }
    }

    fun openVoiceSettingsDialog() {
        _showVoiceSettingsDialog.value = true
    }

    fun closeVoiceSettingsDialog() {
        _showVoiceSettingsDialog.value = false
    }

    fun testVoiceSample(language: AppLanguage = _narrationLanguage.value, style: VoiceStyle = _voiceStyle.value, speed: Float = _audioSpeed.value) {
        _audioErrorMessage.value = null
        audioEngine?.testVoiceSample(language, style, speed, _customApiKey.value.ifBlank { null })
    }

    fun toggleAutoContinue() {
        _autoContinueAudio.value = !_autoContinueAudio.value
    }

    fun playNextVerse() {
        val current = _activePlayingVerse.value ?: run {
            _isPlayingAudio.value = false
            return
        }
        val currentChapterVerses = GitaData.getVersesForChapter(current.chapterId)
        val currentIndex = currentChapterVerses.indexOfFirst { it.verseId == current.verseId }

        if (currentIndex != -1 && currentIndex < currentChapterVerses.size - 1) {
            val nextVerse = currentChapterVerses[currentIndex + 1]
            playVerseAudio(nextVerse)
        } else if (current.chapterId < 18) {
            val nextChapterVerses = GitaData.getVersesForChapter(current.chapterId + 1)
            if (nextChapterVerses.isNotEmpty()) {
                playVerseAudio(nextChapterVerses.first())
            } else {
                _isPlayingAudio.value = false
            }
        } else {
            // End of Gita reached (Chapter 18, Verse 78)
            _isPlayingAudio.value = false
            _audioProgress.value = 0f
            _audioCurrentTime.value = 0
            _audioErrorMessage.value = if (_appLanguage.value == AppLanguage.HINDI) {
                "सम्पूर्ण श्रीमद्भगवद्गीता का श्रवण पूर्ण हुआ! ॐ तत्सत्।"
            } else {
                "You have completed listening to Srimad Bhagavad Gita! Om Tat Sat."
            }
        }
    }

    fun playPreviousVerse() {
        val current = _activePlayingVerse.value ?: return
        val currentChapterVerses = GitaData.getVersesForChapter(current.chapterId)
        val currentIndex = currentChapterVerses.indexOfFirst { it.verseId == current.verseId }

        if (currentIndex > 0) {
            val prevVerse = currentChapterVerses[currentIndex - 1]
            playVerseAudio(prevVerse)
        } else if (current.chapterId > 1) {
            val prevChapterVerses = GitaData.getVersesForChapter(current.chapterId - 1)
            if (prevChapterVerses.isNotEmpty()) {
                playVerseAudio(prevChapterVerses.last())
            }
        }
    }

    // --- Gita Mood Methods ---
    fun openMoodDialog(mood: GitaMood) {
        _selectedMood.value = mood
        _showMoodDialog.value = true
    }

    fun closeMoodDialog() {
        _showMoodDialog.value = false
        _selectedMood.value = null
    }

    // --- 📿 Digital Japa Mala (108 Beads Counter) Methods ---
    fun openJapaMala() {
        navigateTo(Screen.JAPA_MALA)
    }

    fun incrementJapaBead() {
        val completed = japaMalaManager.incrementBead()
        if (completed) {
            sadhanaStreakManager.recordDailyActivity()
        }
    }

    fun resetJapaMala() {
        japaMalaManager.resetCurrentMala()
    }

    fun selectJapaMantra(index: Int) {
        japaMalaManager.selectMantra(index)
    }

    fun setTargetJapaMalas(target: Int) {
        japaMalaManager.setTargetMalas(target)
    }

    fun toggleJapaHaptic() {
        japaMalaManager.toggleHaptic()
    }

    fun toggleJapaSound() {
        japaMalaManager.toggleSound()
    }

    fun dismissJapaCelebration() {
        japaMalaManager.dismissCompletionCelebration()
    }

    fun openJapaDialog() {
        openJapaMala()
    }

    fun closeJapaDialog() {
        _showJapaDialog.value = false
    }

    fun startJapaMode(targetCount: Int = 108) {
        openJapaMala()
    }

    fun stopJapaMode() {
        _isJapaModeActive.value = false
    }

    // --- Sleep Timer Methods ---
    fun openSleepTimerDialog() {
        _showSleepTimerDialog.value = true
    }

    fun closeSleepTimerDialog() {
        _showSleepTimerDialog.value = false
    }

    fun setSleepTimer(option: SleepTimerOption) {
        _sleepTimerOption.value = option
        sleepTimerJob?.cancel()
        _showSleepTimerDialog.value = false

        if (option == SleepTimerOption.OFF || option == SleepTimerOption.END_OF_VERSE) {
            _sleepTimerSecondsRemaining.value = 0L
            return
        }

        _sleepTimerSecondsRemaining.value = option.durationSeconds
        sleepTimerJob = viewModelScope.launch {
            while (_sleepTimerSecondsRemaining.value > 0) {
                delay(1000L)
                _sleepTimerSecondsRemaining.value -= 1
            }
            // Timer expired: stop audio gracefully
            audioEngine?.pauseOrStop()
            _isPlayingAudio.value = false
            _sleepTimerOption.value = SleepTimerOption.OFF
            _audioErrorMessage.value = if (_appLanguage.value == AppLanguage.HINDI)
                "स्लीप टाइमर समाप्त हुआ। शुभ रात्रि! ॐ तत्सत्।"
            else
                "Sleep timer expired. Good night! Om Tat Sat."
        }
    }

    // --- Social Story Card Sharing ---
    fun shareVerseAsStory(context: Context, verse: Verse, isStoryFormat: Boolean = true) {
        viewModelScope.launch {
            GitaCardShareHelper.generateAndShareVerseCard(context, verse, _appLanguage.value, isStoryFormat)
        }
    }

    private fun onAudioCompleted() {
        viewModelScope.launch {
            _audioProgress.value = 0f
            _audioCurrentTime.value = 0

            // 1. Check Japa Loop
            if (_isJapaModeActive.value) {
                _currentJapaCount.value += 1
                val active = _activePlayingVerse.value ?: _selectedVerse.value ?: GitaData.getShlokaOfTheDay()
                if (_currentJapaCount.value < _targetJapaCount.value) {
                    delay(300L)
                    playVerseAudio(active, forceRestart = true)
                    return@launch
                } else {
                    _isJapaModeActive.value = false
                    _isPlayingAudio.value = false
                    _audioErrorMessage.value = if (_appLanguage.value == AppLanguage.HINDI)
                        "॥ ${_targetJapaCount.value} बार पावन जप पूर्ण हुआ ॥ ॐ तत्सत्।"
                    else
                        "॥ ${_targetJapaCount.value}x Japa Meditation Completed ॥ Om Tat Sat."
                    sadhanaStreakManager.recordDailyActivity()
                    return@launch
                }
            }

            // 2. Check Sleep Timer End-of-Verse
            if (_sleepTimerOption.value == SleepTimerOption.END_OF_VERSE) {
                _sleepTimerOption.value = SleepTimerOption.OFF
                _isPlayingAudio.value = false
                return@launch
            }

            // 3. Auto Continue
            if (_autoContinueAudio.value && _isPlayingAudio.value) {
                playNextVerse()
            } else {
                _isPlayingAudio.value = false
            }
        }
    }

    fun fetchAiInsight(verse: Verse) {
        _selectedVerseForInsight.value = verse
        viewModelScope.launch {
            _isInsightLoading.value = true
            _aiInsightText.value = null
            val keyToUse = _customApiKey.value.ifBlank { null }
            val result = GeminiClient.getVerseInsight(verse, _appLanguage.value, keyToUse)
            _isInsightLoading.value = false
            _aiInsightText.value = result.getOrElse { err ->
                if (_appLanguage.value == AppLanguage.HINDI) {
                    "इस श्लोक से हमें यह दिव्य संदेश मिलता है कि कर्तव्य मार्ग पर निष्काम भाव से निरंतर आगे बढ़ते रहें।"
                } else {
                    "This divine verse guides us to perform our duties with devotion and focus without attachment to results."
                }
            }
        }
    }

    private fun saveRecentPosition(chapterId: Int, verseId: Int) {
        viewModelScope.launch {
            bookmarkDao.saveRecentPosition(
                RecentPositionEntity(
                    id = 1,
                    chapterId = chapterId,
                    verseId = verseId
                )
            )
            sadhanaStreakManager.recordDailyActivity()
            try {
                DailyShlokaWidgetProvider.updateAllWidgets(getApplication())
            } catch (_: Exception) {}
            val verse = GitaData.getVerse(chapterId, verseId)
            if (verse != null) {
                val updated = listOf(verse) + _readingHistory.value.filterNot { it.verseKey == verse.verseKey }
                val trimmed = updated.take(30)
                _readingHistory.value = trimmed
                saveReadingHistoryToPrefs(trimmed)
                userManager.updateStats(trimmed.size, bookmarks.value.size)
            }
        }
    }

    private fun loadReadingHistoryFromPrefs(): List<Verse> {
        val stringVal = prefs.getString("reading_history_keys", "") ?: ""
        if (stringVal.isBlank()) return emptyList()
        val keys = stringVal.split(",").filter { it.isNotBlank() }
        val verseKeyPattern = Regex("""c?(\d+)[._v](\d+)""")
        return keys.mapNotNull { key ->
            val match = verseKeyPattern.matchEntire(key.trim())
            if (match != null) {
                val ch = match.groupValues[1].toIntOrNull()
                val v = match.groupValues[2].toIntOrNull()
                if (ch != null && v != null) GitaData.getVerse(ch, v) else null
            } else {
                val parts = key.split(".", "_", "v", "c").filter { it.isNotBlank() }
                if (parts.size == 2) {
                    val ch = parts[0].toIntOrNull()
                    val v = parts[1].toIntOrNull()
                    if (ch != null && v != null) GitaData.getVerse(ch, v) else null
                } else null
            }
        }
    }

    private fun saveReadingHistoryToPrefs(history: List<Verse>) {
        val stringVal = history.joinToString(",") { it.verseKey }
        prefs.edit().putString("reading_history_keys", stringVal).apply()
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine?.shutdown()
        japaMalaManager.release()
    }
}
