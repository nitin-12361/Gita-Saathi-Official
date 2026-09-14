package com.nkapps.gitasaathi.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.AudioMode
import com.nkapps.gitasaathi.data.Verse
import com.nkapps.gitasaathi.data.VoiceStyle
import com.nkapps.gitasaathi.network.GeminiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

class GitaAudioEngine(
    context: Context,
    private val onPlaybackCompleted: () -> Unit,
    private val onLoadingStateChanged: (Boolean) -> Unit = {},
    private val onAudioError: (String) -> Unit = {}
) : TextToSpeech.OnInitListener, AudioManager.OnAudioFocusChangeListener {

    private val context: Context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && context.attributionTag == null) {
        try {
            context.createAttributionContext("audio")
        } catch (_: Exception) {
            context.applicationContext
        }
    } else {
        context.applicationContext
    }

    private var mediaPlayer: MediaPlayer? = null
    private var nativeTts: TextToSpeech? = null
    @Volatile
    private var isTtsInitialized = false
    private var isPlayingNativeTts = false
    @Volatile
    private var ttsStartTime: Long = 0L
    @Volatile
    private var ttsEstimatedDurationMs: Long = 0L
    private var activeFocusRequest: AudioFocusRequest? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var audioJob: Job? = null
    private var ttsJob: Job? = null

    @Volatile
    private var currentTrackSessionId: Long = 0L

    private var currentVerse: Verse? = null
    private var currentAudioMode: AudioMode = AudioMode.SANSKRIT_SHLOKA
    private var currentLanguage: AppLanguage = AppLanguage.HINDI
    private var currentSpeed: Float = 1.0f
    private var currentVoiceStyle: VoiceStyle = VoiceStyle.DEVOTIONAL

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    init {
        try {
            nativeTts = TextToSpeech(this.context, this)
        } catch (e: Exception) {
            Log.e("GitaAudioEngine", "Failed to initialize System TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val attrs = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                    nativeTts?.setAudioAttributes(attrs)
                }

                val result = nativeTts?.setLanguage(Locale("hi", "IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    nativeTts?.setLanguage(Locale.US)
                }
            } catch (e: Exception) {
                Log.w("GitaAudioEngine", "Error setting TTS language", e)
            }

            nativeTts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isPlayingNativeTts = true
                    coroutineScope.launch {
                        onLoadingStateChanged(false)
                    }
                }

                override fun onDone(utteranceId: String?) {
                    isPlayingNativeTts = false
                    ttsStartTime = 0L
                    ttsEstimatedDurationMs = 0L
                    coroutineScope.launch {
                        onPlaybackCompleted()
                    }
                }

                override fun onError(utteranceId: String?) {
                    isPlayingNativeTts = false
                    ttsStartTime = 0L
                    ttsEstimatedDurationMs = 0L
                    coroutineScope.launch {
                        onPlaybackCompleted()
                    }
                }
            })
        } else {
            Log.e("GitaAudioEngine", "TTS Initialization failed with status $status")
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                stopAudio()
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return true
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setOnAudioFocusChangeListener(this)
                    .build()
                activeFocusRequest = focusRequest
                audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        } catch (e: Exception) {
            Log.w("GitaAudioEngine", "Error requesting audio focus", e)
            true
        }
    }

    private fun abandonAudioFocus() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activeFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
                activeFocusRequest = null
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(this)
            }
        } catch (_: Exception) {}
    }

    fun playVerse(
        verse: Verse,
        audioMode: AudioMode = currentAudioMode,
        language: AppLanguage = currentLanguage,
        speed: Float = currentSpeed,
        voiceStyle: VoiceStyle = currentVoiceStyle,
        customApiKey: String? = null
    ) {
        val sessionId = ++currentTrackSessionId
        currentVerse = verse
        currentAudioMode = AudioMode.SANSKRIT_SHLOKA
        currentLanguage = language
        currentSpeed = speed
        currentVoiceStyle = voiceStyle

        stopAudioForNewTrack()
        onLoadingStateChanged(true)

        audioJob?.cancel()
        audioJob = coroutineScope.launch {
            if (sessionId != currentTrackSessionId || !isActive) return@launch
            playArchiveAudioStream(verse, speed, sessionId)
        }
    }

    private suspend fun downloadShlokaAudio(
        verse: Verse,
        sessionId: Long
    ): File? = withContext(Dispatchers.IO) {
        val chapterStr = verse.chapterId.toString()
        val verseStr = verse.verseId.toString()
        val audioDir = File(context.cacheDir, "shloka_audio").apply { if (!exists()) mkdirs() }
        val cacheFile = File(audioDir, "${chapterStr}_${verseStr}.mp3")

        // 1. If valid cached file exists, return immediately for instant offline playback
        if (cacheFile.exists() && cacheFile.length() > 5000) {
            return@withContext cacheFile
        }

        if (sessionId != currentTrackSessionId || !isActive) return@withContext null

        val primaryUrl = "https://raw.githubusercontent.com/nitin-12361/gita-saathi-audio/main/${chapterStr}/${verseStr}.mp3"
        val secondaryUrl = "https://raw.githubusercontent.com/gita/gita/main/data/verse_recitation/${chapterStr}/${verseStr}.mp3"

        val tempFile = File(audioDir, "shloka_tmp_${System.currentTimeMillis()}_${chapterStr}_${verseStr}.tmp")

        val urlsToTry = listOf(primaryUrl, secondaryUrl)
        var downloadSuccess = false

        for (url in urlsToTry) {
            if (sessionId != currentTrackSessionId || !isActive) {
                try { tempFile.delete() } catch (_: Exception) {}
                return@withContext null
            }
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "GitaSaathi/1.0 (Android)")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body
                        if (body != null) {
                            val bytes = body.bytes()
                            if (bytes.size > 2000) {
                                tempFile.writeBytes(bytes)
                                downloadSuccess = true
                            }
                        }
                    }
                }
                if (downloadSuccess) break
            } catch (e: Exception) {
                Log.w("GitaAudioEngine", "Failed to download from $url: ${e.message}")
            }
        }

        if (sessionId != currentTrackSessionId || !isActive) {
            try { tempFile.delete() } catch (_: Exception) {}
            return@withContext null
        }

        if (downloadSuccess && tempFile.exists() && tempFile.length() > 2000) {
            try {
                if (cacheFile.exists()) cacheFile.delete()
                if (tempFile.renameTo(cacheFile)) {
                    com.nkapps.gitasaathi.data.GitaCacheManager.pruneAudioCache(context)
                    return@withContext cacheFile
                } else {
                    tempFile.copyTo(cacheFile, overwrite = true)
                    tempFile.delete()
                    com.nkapps.gitasaathi.data.GitaCacheManager.pruneAudioCache(context)
                    return@withContext cacheFile
                }
            } catch (e: Exception) {
                Log.e("GitaAudioEngine", "Error moving temp audio file to cache", e)
                return@withContext tempFile
            }
        } else {
            try { tempFile.delete() } catch (_: Exception) {}
            return@withContext null
        }
    }

    private suspend fun playArchiveAudioStream(
        verse: Verse,
        speed: Float,
        sessionId: Long
    ) {
        val audioFile = downloadShlokaAudio(verse, sessionId)

        if (sessionId != currentTrackSessionId) return

        if (audioFile != null && audioFile.exists() && audioFile.length() > 0) {
            onLoadingStateChanged(false)
            playAudioFile(audioFile, speed, sessionId)
        } else {
            Log.w("GitaAudioEngine", "Shloka server audio download failed or offline. Falling back to System Voice.")
            onLoadingStateChanged(false)
            
            // Only show error if we are truly offline and both failed
            // But if we are switching tracks, maybe don't show it as an "Error" dialog immediately
            // speakWithNativeTts will handle the playback
            speakWithNativeTts(verse, AppLanguage.HINDI, speed, sessionId)
        }
    }

    private fun speakTextWithNativeTts(
        textToSpeak: String,
        targetLanguage: AppLanguage,
        speed: Float,
        voiceStyle: VoiceStyle = currentVoiceStyle,
        sessionId: Long = currentTrackSessionId
    ) {
        ttsJob?.cancel()
        ttsJob = coroutineScope.launch {
            var waitCount = 0
            while (!isTtsInitialized && waitCount < 30) {
                delay(100)
                waitCount++
            }

            if (sessionId != currentTrackSessionId || !isActive) return@launch

            onLoadingStateChanged(false)
            if (!isTtsInitialized || nativeTts == null) {
                return@launch
            }

            try {
                stopAndReleasePlayer()
                requestAudioFocus()

                val targetLocale = if (targetLanguage == AppLanguage.HINDI) Locale("hi", "IN") else Locale("en", "IN")
                try {
                    val langRes = nativeTts?.setLanguage(targetLocale)
                    if (langRes == TextToSpeech.LANG_MISSING_DATA || langRes == TextToSpeech.LANG_NOT_SUPPORTED) {
                        if (targetLanguage == AppLanguage.ENGLISH) {
                            nativeTts?.setLanguage(Locale.US)
                        }
                    }
                } catch (_: Exception) {}

                // Select high-quality / natural voice matching style on Android 21+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        val availableVoices = nativeTts?.voices?.filter {
                            it.locale.language.equals(targetLocale.language, ignoreCase = true)
                        }
                        if (!availableVoices.isNullOrEmpty()) {
                            val matchedVoice = when (voiceStyle) {
                                VoiceStyle.SOOTHING_FEMALE -> {
                                    availableVoices.firstOrNull { 
                                        it.name.contains("female", ignoreCase = true) || 
                                        it.name.contains("fem", ignoreCase = true) ||
                                        it.name.contains("woman", ignoreCase = true)
                                    }
                                }
                                VoiceStyle.DEVOTIONAL, VoiceStyle.CALM_SAGE -> {
                                    availableVoices.firstOrNull { 
                                        it.name.contains("male", ignoreCase = true) && !it.name.contains("female", ignoreCase = true)
                                    }
                                }
                                else -> availableVoices.firstOrNull()
                            } ?: availableVoices.firstOrNull { it.locale.country.equals(targetLocale.country, ignoreCase = true) }
                              ?: availableVoices.firstOrNull()

                            if (matchedVoice != null) {
                                nativeTts?.voice = matchedVoice
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("GitaAudioEngine", "Error setting voice selection", e)
                    }
                }

                nativeTts?.setPitch(voiceStyle.pitch)
                nativeTts?.setSpeechRate(speed)

                val utteranceId = "text_${System.currentTimeMillis()}"
                isPlayingNativeTts = true

                // Format text with human-like breathing pauses
                val cleanText = textToSpeak
                    .replace("॥", ".\n")
                    .replace("।", ",\n")
                    .replace(":\n", ": ")
                    .replace(":", ", ")

                ttsStartTime = System.currentTimeMillis()
                val baseDuration = cleanText.length * 75L
                ttsEstimatedDurationMs = ((baseDuration / speed.coerceAtLeast(0.5f)).toLong()).coerceAtLeast(2500L)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val params = Bundle()
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
                    nativeTts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                } else {
                    @Suppress("DEPRECATION")
                    val params = HashMap<String, String>()
                    params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = utteranceId
                    @Suppress("DEPRECATION")
                    nativeTts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params)
                }
            } catch (e: Exception) {
                Log.e("GitaAudioEngine", "Error speaking text with System TTS", e)
                isPlayingNativeTts = false
            }
        }
    }

    private fun speakWithNativeTts(verse: Verse, language: AppLanguage, speed: Float, sessionId: Long = currentTrackSessionId) {
        val textToSpeak = if (language == AppLanguage.HINDI) {
            verse.shlokaSanskrit.ifBlank { verse.translationHindi }
        } else {
            verse.transliteration.ifBlank { verse.translationEnglish }
        }
        speakTextWithNativeTts(textToSpeak, language, speed, currentVoiceStyle, sessionId)
    }

    fun testVoiceSample(
        language: AppLanguage,
        style: VoiceStyle,
        speed: Float,
        customApiKey: String? = null
    ) {
        val sessionId = ++currentTrackSessionId
        stopAudio()
        onLoadingStateChanged(true)

        audioJob?.cancel()
        audioJob = coroutineScope.launch {
            val result = GeminiClient.getOrFetchSampleTtsAudio(
                context = context,
                language = language,
                voiceStyle = style,
                customApiKey = customApiKey
            )

            if (sessionId != currentTrackSessionId) return@launch

            result.fold(
                onSuccess = { audioFile ->
                    if (sessionId != currentTrackSessionId) return@fold
                    onLoadingStateChanged(false)
                    playAudioFile(audioFile, speed, sessionId)
                },
                onFailure = { error ->
                    if (sessionId != currentTrackSessionId) return@fold
                    Log.w("GitaAudioEngine", "Sample Gemini TTS error (${error.message}). Falling back to System Voice.", error)
                    onLoadingStateChanged(false)
                    if (isTtsInitialized && nativeTts != null) {
                        val sampleText = "Om Namo Bhagavate Vasudevaya. Karmanye vadhikaraste ma phaleshu kadachana."
                        try {
                            requestAudioFocus()
                            nativeTts?.setPitch(style.pitch)
                            nativeTts?.setSpeechRate(speed)
                            val loc = if (language == AppLanguage.HINDI) Locale("hi", "IN") else Locale.US
                            val res = nativeTts?.setLanguage(loc)
                            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                                nativeTts?.setLanguage(Locale.US)
                            }
                            val utteranceId = "sample_${System.currentTimeMillis()}"
                            isPlayingNativeTts = true
                            val params = Bundle().apply { putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId) }
                            nativeTts?.speak(sampleText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                        } catch (e: Exception) {
                            isPlayingNativeTts = false
                            onAudioError(if (language == AppLanguage.HINDI) "वॉयस नमूना विफल रहा।" else "Voice sample failed.")
                        }
                    } else {
                        onAudioError(if (language == AppLanguage.HINDI) "वॉयस नमूना विफल रहा।" else "Voice sample failed.")
                    }
                }
            )
        }
    }

    private fun playAudioFile(file: File, speed: Float, sessionId: Long = currentTrackSessionId) {
        if (sessionId != currentTrackSessionId) return
        try {
            requestAudioFocus()
            stopAndReleasePlayer()
            val newPlayer = MediaPlayer().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                } else {
                    @Suppress("DEPRECATION")
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                }
                setDataSource(file.absolutePath)
                setOnPreparedListener { mp ->
                    if (sessionId != currentTrackSessionId || mediaPlayer != mp) {
                        try { mp.release() } catch (_: Exception) {}
                        return@setOnPreparedListener
                    }
                    onLoadingStateChanged(false)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            mp.playbackParams = PlaybackParams().setSpeed(speed)
                        } catch (e: Exception) {
                            Log.w("GitaAudioEngine", "Could not set playback speed", e)
                        }
                    }
                    mp.start()
                }
                setOnCompletionListener { mp ->
                    if (sessionId == currentTrackSessionId && mediaPlayer == mp) {
                        onPlaybackCompleted()
                    }
                }
                setOnErrorListener { mp, what, extra ->
                    if (sessionId != currentTrackSessionId || mediaPlayer != mp) {
                        try { mp.release() } catch (_: Exception) {}
                        return@setOnErrorListener true
                    }
                    Log.e("GitaAudioEngine", "MediaPlayer error: $what, $extra")
                    try { file.delete() } catch (_: Exception) {}
                    val verse = currentVerse
                    if (verse != null) {
                        speakWithNativeTts(verse, currentLanguage, currentSpeed, sessionId)
                    }
                    true
                }
                prepareAsync()
            }
            mediaPlayer = newPlayer
        } catch (e: Exception) {
            Log.e("GitaAudioEngine", "Error playing audio file", e)
            try { file.delete() } catch (_: Exception) {}
            val verse = currentVerse
            if (verse != null) {
                speakWithNativeTts(verse, currentLanguage, currentSpeed, sessionId)
            }
        }
    }

    fun setSpeed(speed: Float) {
        currentSpeed = speed
        mediaPlayer?.let { player ->
            if (player.isPlaying && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    player.playbackParams = player.playbackParams.setSpeed(speed)
                } catch (e: Exception) {
                    Log.w("GitaAudioEngine", "Could not set playback speed", e)
                }
            }
        }
        if (isPlayingNativeTts) {
            try {
                nativeTts?.setSpeechRate(speed)
            } catch (_: Exception) {}
        }
    }

    fun setVoiceStyle(style: VoiceStyle) {
        currentVoiceStyle = style
    }

    @Volatile
    private var isAudioPaused = false
    @Volatile
    private var pausedPositionMs: Int = 0

    fun isPaused(): Boolean = isAudioPaused

    fun pauseAudio() {
        if (isAudioPaused) return
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                pausedPositionMs = mediaPlayer?.currentPosition ?: 0
                isAudioPaused = true
                abandonAudioFocus()
            } else if (isPlayingNativeTts) {
                pausedPositionMs = getCurrentPosition()
                isPlayingNativeTts = false
                isAudioPaused = true
                try {
                    nativeTts?.stop()
                } catch (e: Exception) {
                    Log.w("GitaAudioEngine", "Error pausing native TTS", e)
                }
                abandonAudioFocus()
            }
        } catch (e: Exception) {
            Log.w("GitaAudioEngine", "Error pausing audio", e)
        }
    }

    fun resumeAudio(): Boolean {
        if (!isAudioPaused) return false
        try {
            val player = mediaPlayer
            if (player != null) {
                requestAudioFocus()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        player.playbackParams = player.playbackParams.setSpeed(currentSpeed)
                    } catch (_: Exception) {}
                }
                player.start()
                isAudioPaused = false
                return true
            } else {
                isAudioPaused = false
                return false
            }
        } catch (e: Exception) {
            Log.e("GitaAudioEngine", "Error resuming audio", e)
            isAudioPaused = false
        }
        return false
    }

    fun pauseOrStop() {
        pauseAudio()
    }

    private fun stopAndReleasePlayer() {
        val p = mediaPlayer
        mediaPlayer = null
        p?.let { player ->
            try {
                player.setOnPreparedListener(null)
                player.setOnErrorListener(null)
                player.setOnCompletionListener(null)
                player.setOnBufferingUpdateListener(null)
                player.setOnSeekCompleteListener(null)
                if (player.isPlaying) {
                    player.stop()
                }
            } catch (_: Exception) {}
            try {
                player.reset()
            } catch (_: Exception) {}
            try {
                player.release()
            } catch (_: Exception) {}
        }
    }

    private fun stopAudioForNewTrack() {
        isAudioPaused = false
        pausedPositionMs = 0
        isPlayingNativeTts = false
        ttsStartTime = 0L
        ttsEstimatedDurationMs = 0L
        ttsJob?.cancel()
        // Cancel only the call being replaced, not all calls which might affect new track
        // Actually dispatcher.cancelAll() is risky if not careful. 
        // Let's rely on sessionId checks in downloadShlokaAudio instead.
        abandonAudioFocus()
        try {
            if (nativeTts?.isSpeaking == true) {
                nativeTts?.stop()
            }
        } catch (e: Exception) {
            Log.w("GitaAudioEngine", "Error stopping native TTS", e)
        }
        stopAndReleasePlayer()
    }

    private fun stopAudio() {
        audioJob?.cancel()
        stopAudioForNewTrack()
    }

    fun isSpeaking(): Boolean {
        return try {
            (mediaPlayer?.isPlaying == true) || (isPlayingNativeTts && nativeTts?.isSpeaking == true)
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentPosition(): Int {
        return try {
            if (isAudioPaused && pausedPositionMs > 0) {
                pausedPositionMs
            } else if (isPlayingNativeTts && ttsStartTime > 0L) {
                val elapsed = (System.currentTimeMillis() - ttsStartTime).toInt()
                if (ttsEstimatedDurationMs > 0) {
                    elapsed.coerceAtMost(ttsEstimatedDurationMs.toInt())
                } else {
                    elapsed
                }
            } else {
                mediaPlayer?.currentPosition ?: pausedPositionMs
            }
        } catch (e: Exception) {
            pausedPositionMs
        }
    }

    fun getDuration(): Int {
        return try {
            if (isPlayingNativeTts && ttsEstimatedDurationMs > 0L) {
                ttsEstimatedDurationMs.toInt()
            } else {
                mediaPlayer?.duration ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            pausedPositionMs = positionMs
            if (isPlayingNativeTts && ttsEstimatedDurationMs > 0L) {
                ttsStartTime = System.currentTimeMillis() - positionMs
            } else {
                mediaPlayer?.seekTo(positionMs)
            }
        } catch (e: Exception) {
            Log.w("GitaAudioEngine", "Error seeking MediaPlayer", e)
        }
    }

    fun shutdown() {
        audioJob?.cancel()
        stopAudio()
        try {
            nativeTts?.shutdown()
            nativeTts = null
        } catch (e: Exception) {
            Log.w("GitaAudioEngine", "Error shutting down TTS", e)
        }
        stopAndReleasePlayer()
    }
}

