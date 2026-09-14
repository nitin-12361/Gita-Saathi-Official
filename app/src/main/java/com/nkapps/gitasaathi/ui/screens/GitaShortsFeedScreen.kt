package com.nkapps.gitasaathi.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.nkapps.gitasaathi.data.GitaExoPlayerManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.nkapps.gitasaathi.ads.GitaAdManager
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.BookmarkEntity
import com.nkapps.gitasaathi.data.GitaData
import com.nkapps.gitasaathi.data.GitaVideoResolver
import com.nkapps.gitasaathi.data.Verse
import com.nkapps.gitasaathi.ui.components.SpiritualArtworkCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun GitaShortsFeedScreen(
    initialVerse: Verse?,
    appLanguage: AppLanguage,
    shortsLanguage: AppLanguage,
    bookmarks: List<BookmarkEntity>,
    onToggleBookmark: (Verse) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onBackClick: () -> Unit,
    onVerseSelectedForAudio: (Verse) -> Unit,
    onGoldClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val allVerses = remember {
        (1..18).flatMap { ch -> GitaData.getVersesForChapter(ch) }
    }

    val initialIndex = remember(initialVerse, allVerses) {
        if (initialVerse != null) {
            val idx = allVerses.indexOfFirst {
                it.chapterId == initialVerse.chapterId && it.verseId == initialVerse.verseId
            }
            if (idx >= 0) idx else 0
        } else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { allVerses.size }
    )

    LaunchedEffect(initialVerse, allVerses) {
        if (initialVerse != null) {
            val idx = allVerses.indexOfFirst {
                it.chapterId == initialVerse.chapterId && it.verseId == initialVerse.verseId
            }
            if (idx >= 0 && idx != pagerState.currentPage) {
                pagerState.scrollToPage(idx)
            }
        }
    }

    // Interstitial Ad Frequency: Triggered after watching every 6th video (0 ads for Gold subscribers)
    var videosWatchedCount by remember { mutableIntStateOf(0) }
    var previousPage by remember { mutableIntStateOf(initialIndex) }

    LaunchedEffect(pagerState.currentPage) {
        val current = pagerState.currentPage
        if (current != previousPage) {
            previousPage = current
            videosWatchedCount++
            if (videosWatchedCount >= 6) {
                videosWatchedCount = 0
                (context as? Activity)?.let { activity ->
                    GitaAdManager.showShortsInterstitial(activity) {}
                }
            }
        }
    }

    // Smart Background Segment Preloader: Downloads initial 2MB of immediate upcoming & previous videos in order
    LaunchedEffect(pagerState.currentPage, shortsLanguage, allVerses) {
        val current = pagerState.currentPage
        val targetIndices = listOf(
            current + 1,
            current + 2,
            current - 1
        )
        val uris = targetIndices.mapNotNull { idx ->
            if (idx in allVerses.indices) {
                val verse = allVerses[idx]
                GitaVideoResolver.getVideoUri(context, verse.chapterId, verse.verseId, shortsLanguage)
            } else null
        }
        GitaExoPlayerManager.preloadNextVideos(context, uris)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("gita_shorts_feed_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 0
        ) { pageIndex ->
            val verse = allVerses.getOrNull(pageIndex) ?: return@VerticalPager
            val isCurrentPage = pagerState.currentPage == pageIndex
            val isBookmarked = bookmarks.any { it.chapterId == verse.chapterId && it.verseId == verse.verseId }

            InstagramReelItem(
                verse = verse,
                appLanguage = appLanguage,
                shortsLanguage = shortsLanguage,
                isCurrentPage = isCurrentPage,
                isBookmarked = isBookmarked,
                onToggleBookmark = { onToggleBookmark(verse) },
                onShareClick = {
                    val shareText = if (appLanguage == AppLanguage.HINDI) {
                        "🕉️ श्रीमद्भगवद्गीता (अध्याय ${verse.chapterId}, श्लोक ${verse.verseId})\n\n${verse.shlokaSanskrit}\n\nहिंदी अर्थ: ${verse.translationHindi}\n\nगीता साथी ऐप द्वारा साझा।"
                    } else {
                        "🕉️ Srimad Bhagavad Gita (Chapter ${verse.chapterId}, Verse ${verse.verseId})\n\n${verse.shlokaSanskrit}\n\nMeaning: ${verse.translationEnglish}\n\nShared via Gita Saathi App."
                    }
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, if (appLanguage == AppLanguage.HINDI) "श्लोक रील साझा करें" else "Share Shloka Reel"))
                },
                onAudioListenClick = { onVerseSelectedForAudio(verse) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                    .testTag("shorts_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            val currentVerse = allVerses.getOrNull(pagerState.currentPage) ?: initialVerse
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.5f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    val label = if (currentVerse != null) {
                        if (appLanguage == AppLanguage.HINDI) "🕉️ अध्याय ${currentVerse.chapterId}.${currentVerse.verseId}" else "🕉️ Verse ${currentVerse.chapterId}.${currentVerse.verseId}"
                    } else {
                        if (appLanguage == AppLanguage.HINDI) "🕉️ दिव्य रील्स" else "🕉️ Gita Reels"
                    }
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE082)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, Color(0xFFFFA726).copy(alpha = 0.6f)),
                modifier = Modifier.testTag("shorts_language_toggle_button")
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { if (shortsLanguage != AppLanguage.HINDI) onLanguageChange(AppLanguage.HINDI) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (shortsLanguage == AppLanguage.HINDI) Color(0xFFE67E22) else Color.Transparent,
                        modifier = Modifier.height(26.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 7.dp)
                        ) {
                            Text(
                                text = "हिंदी",
                                fontSize = 11.sp,
                                fontWeight = if (shortsLanguage == AppLanguage.HINDI) FontWeight.ExtraBold else FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Surface(
                        onClick = { if (shortsLanguage != AppLanguage.ENGLISH) onLanguageChange(AppLanguage.ENGLISH) },
                        shape = RoundedCornerShape(16.dp),
                        color = if (shortsLanguage == AppLanguage.ENGLISH) Color(0xFFE67E22) else Color.Transparent,
                        modifier = Modifier.height(26.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 7.dp)
                        ) {
                            Text(
                                text = "En",
                                fontSize = 11.sp,
                                fontWeight = if (shortsLanguage == AppLanguage.ENGLISH) FontWeight.ExtraBold else FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}



/**
 * Individual Instagram-Style Reel Item
 */
@OptIn(UnstableApi::class)
@Composable
fun InstagramReelItem(
    verse: Verse,
    appLanguage: AppLanguage,
    shortsLanguage: AppLanguage,
    isCurrentPage: Boolean,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onShareClick: () -> Unit,
    onAudioListenClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var showPlayPauseIndicator by remember { mutableStateOf(false) }
    var showDoubleTapHeart by remember { mutableStateOf(false) }
    var hasVideoError by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var bufferedPercent by remember { mutableIntStateOf(0) }

    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var isScrubbing by remember { mutableStateOf(false) }
    var scrubFraction by remember { mutableFloatStateOf(0f) }
    var pendingSeekPositionMs by remember { mutableStateOf<Long?>(null) }

    val context = LocalContext.current
    val videoUri = remember(verse.chapterId, verse.verseId, shortsLanguage) {
        GitaVideoResolver.getVideoUri(context, verse.chapterId, verse.verseId, shortsLanguage)
    }

    // High-performance ExoPlayer with disk caching and fast start
    val exoPlayer = remember(videoUri) {
        if (videoUri != null) {
            hasVideoError = false
            GitaExoPlayerManager.createFastExoPlayer(context).apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        isBuffering = (playbackState == Player.STATE_BUFFERING)
                        if (playbackState == Player.STATE_READY) {
                            isBuffering = false
                        }
                    }

                    override fun onPositionDiscontinuity(
                        oldPosition: Player.PositionInfo,
                        newPosition: Player.PositionInfo,
                        reason: Int
                    ) {
                        // Keep seek target until position synchronizes
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        hasVideoError = true
                        isBuffering = false
                        pendingSeekPositionMs = null
                    }
                })
                val mediaSource = GitaExoPlayerManager.createCachedMediaSource(context, videoUri)
                setMediaSource(mediaSource)
                if (isCurrentPage) {
                    prepare()
                    playWhenReady = isPlaying
                }
                volume = if (isMuted) 0f else 1f
            }
        } else null
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    exoPlayer?.playWhenReady = false
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (isCurrentPage && isPlaying) {
                        exoPlayer?.playWhenReady = true
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    exoPlayer?.release()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer?.release()
        }
    }

    LaunchedEffect(isCurrentPage, isPlaying, exoPlayer) {
        if (exoPlayer != null) {
            if (isCurrentPage) {
                if (exoPlayer.playbackState == Player.STATE_IDLE) {
                    if (videoUri != null) {
                        val mediaSource = GitaExoPlayerManager.createCachedMediaSource(context, videoUri)
                        exoPlayer.setMediaSource(mediaSource)
                    }
                    exoPlayer.prepare()
                }
                exoPlayer.playWhenReady = isPlaying
            } else {
                exoPlayer.playWhenReady = false
                if (exoPlayer.playbackState != Player.STATE_IDLE) {
                    exoPlayer.stop()
                }
            }
        }
    }

    LaunchedEffect(isMuted) {
        exoPlayer?.volume = if (isMuted) 0f else 1f
    }

    LaunchedEffect(exoPlayer, isPlaying, isCurrentPage, isScrubbing) {
        if (exoPlayer != null && isCurrentPage) {
            while (isActive) {
                if (!isScrubbing) {
                    val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
                    val dur = exoPlayer.duration.coerceAtLeast(0L)
                    if (dur > 0) {
                        totalDurationMs = dur
                    }

                    val pending = pendingSeekPositionMs
                    if (pending != null) {
                        if (Math.abs(pos - pending) < 1200L) {
                            currentPositionMs = pos
                            pendingSeekPositionMs = null
                        } else {
                            currentPositionMs = pending
                        }
                    } else {
                        currentPositionMs = pos
                    }
                }
                isBuffering = exoPlayer.playbackState == Player.STATE_BUFFERING
                bufferedPercent = exoPlayer.bufferedPercentage.coerceIn(0, 100)
                delay(100)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isPlaying = !isPlaying
                        showPlayPauseIndicator = true
                    },
                    onDoubleTap = {
                        if (!isBookmarked) {
                            onToggleBookmark()
                        }
                        showDoubleTapHeart = true
                    }
                )
            }
    ) {
        // 1. Fullscreen Video / Canvas
        if (videoUri != null && exoPlayer != null && !hasVideoError) {
            key(videoUri) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            SpiritualArtworkCanvas(
                verse = verse,
                isPlaying = isPlaying && isCurrentPage,
                appLanguage = appLanguage,
                showTitleBadge = false,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. Minimalist subtle top gradient for header contrast (Bottom is 100% transparent for subtitles)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.50f),
                        0.15f to Color.Transparent,
                        1.0f to Color.Transparent
                    )
                )
        )

        // 3. Central Animated Spinning Loader with "Loading..." (No percentage, continuously rotating circle)
        AnimatedVisibility(
            visible = isBuffering && isCurrentPage && videoUri != null && !hasVideoError,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Black.copy(alpha = 0.75f),
                border = BorderStroke(1.5.dp, Color(0xFFFFB74D).copy(alpha = 0.8f)),
                shadowElevation = 8.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(46.dp),
                        color = Color(0xFFFF9800),
                        trackColor = Color.White.copy(alpha = 0.15f),
                        strokeWidth = 3.5.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "लोड हो रहा है..." else "Loading...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFE082)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showDoubleTapHeart,
            enter = scaleIn(spring(dampingRatio = 0.5f, stiffness = 400f)) + fadeIn(),
            exit = scaleOut() + fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            LaunchedEffect(showDoubleTapHeart) {
                if (showDoubleTapHeart) {
                    delay(800)
                    showDoubleTapHeart = false
                }
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = Color(0xFFFF2D55),
                modifier = Modifier.size(100.dp)
            )
        }

        AnimatedVisibility(
            visible = showPlayPauseIndicator,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            LaunchedEffect(showPlayPauseIndicator) {
                if (showPlayPauseIndicator) {
                    delay(550)
                    showPlayPauseIndicator = false
                }
            }
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f),
                border = BorderStroke(1.5.dp, Color(0xFFFFA726)),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = null,
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Floating Minimalist Side Actions (Positioned safely above bottom subtitle area)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InstagramActionButton(
                icon = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = if (appLanguage == AppLanguage.HINDI) (if (isBookmarked) "सहेजा" else "पसंद") else (if (isBookmarked) "Saved" else "Save"),
                tint = if (isBookmarked) Color(0xFFFF2D55) else Color.White,
                onClick = onToggleBookmark
            )

            InstagramActionButton(
                icon = Icons.AutoMirrored.Filled.Send,
                label = if (appLanguage == AppLanguage.HINDI) "शेयर" else "Share",
                tint = Color.White,
                onClick = onShareClick
            )

            InstagramActionButton(
                icon = Icons.Default.Headphones,
                label = if (appLanguage == AppLanguage.HINDI) "सुनें" else "Listen",
                tint = Color(0xFFFFD54F),
                onClick = onAudioListenClick
            )

            InstagramActionButton(
                icon = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                label = if (appLanguage == AppLanguage.HINDI) (if (isMuted) "म्यूट" else "ध्वनि") else (if (isMuted) "Muted" else "Audio"),
                tint = Color.White,
                onClick = { isMuted = !isMuted }
            )
        }

        // 3. Ultra-Slim Interactive Progress & Seek Bar
        if (totalDurationMs > 0) {
            ShortsSlimProgressBar(
                currentPositionMs = currentPositionMs,
                totalDurationMs = totalDurationMs,
                isScrubbing = isScrubbing,
                scrubFraction = scrubFraction,
                onScrubStart = {
                    isScrubbing = true
                },
                onScrub = { fraction ->
                    scrubFraction = fraction
                },
                onScrubEnd = { finalFraction ->
                    val targetMs = (finalFraction * totalDurationMs).toLong().coerceIn(0L, totalDurationMs)
                    pendingSeekPositionMs = targetMs
                    currentPositionMs = targetMs
                    exoPlayer?.seekTo(targetMs)
                    isScrubbing = false
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 2.dp)
            )
        }
    }
}

/**
 * Ultra-Slim Interactive Progress & Seek Bar for Shorts/Reels
 */
@Composable
fun ShortsSlimProgressBar(
    currentPositionMs: Long,
    totalDurationMs: Long,
    isScrubbing: Boolean,
    scrubFraction: Float,
    onScrubStart: () -> Unit,
    onScrub: (Float) -> Unit,
    onScrubEnd: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalDurationMs <= 0) return

    val currentOnScrubStart by rememberUpdatedState(onScrubStart)
    val currentOnScrub by rememberUpdatedState(onScrub)
    val currentOnScrubEnd by rememberUpdatedState(onScrubEnd)

    val progressFraction = if (isScrubbing) {
        scrubFraction
    } else {
        (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .pointerInput(totalDurationMs) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val widthPx = size.width.toFloat().coerceAtLeast(1f)
                    val downFraction = (down.position.x / widthPx).coerceIn(0f, 1f)
                    var lastFraction = downFraction

                    currentOnScrubStart()
                    currentOnScrub(downFraction)

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            // User released finger (from either a tap or a drag)
                            change.consume()
                            currentOnScrubEnd(lastFraction)
                            break
                        }
                        val currentFraction = (change.position.x / widthPx).coerceIn(0f, 1f)
                        if (Math.abs(currentFraction - lastFraction) > 0.001f) {
                            lastFraction = currentFraction
                            change.consume()
                            currentOnScrub(lastFraction)
                        }
                    }
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Scrubbing Timestamp Preview Pill
        if (isScrubbing) {
            val previewMs = (progressFraction * totalDurationMs).toLong().coerceIn(0L, totalDurationMs)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = "${formatDuration(previewMs)} / ${formatDuration(totalDurationMs)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        // Visible Ultra-Slim Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isScrubbing) 5.dp else 2.5.dp)
                .background(Color.White.copy(alpha = 0.28f))
        ) {
            // Filled Progress
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progressFraction)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF7A00), Color(0xFFFFD54F))
                        )
                    )
            )
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
}

@Composable
fun InstagramActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.45f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}
