package com.nkapps.gitasaathi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.nkapps.gitasaathi.ui.components.LifeApplicationCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.AudioMode
import com.nkapps.gitasaathi.data.GitaData
import com.nkapps.gitasaathi.data.Verse
import com.nkapps.gitasaathi.ui.components.SpiritualArtworkCanvas
import com.nkapps.gitasaathi.ui.components.getSpiritualSceneForVerse
import com.nkapps.gitasaathi.ui.components.VerseAnimationVideoPlayer
import com.nkapps.gitasaathi.ui.components.getVerseVideoTheme
import kotlinx.coroutines.delay

import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Share
import com.nkapps.gitasaathi.ui.SleepTimerOption

@Composable
fun FullScreenPlayerScreen(
    verse: Verse,
    isPlaying: Boolean,
    audioMode: AudioMode,
    appLanguage: AppLanguage,
    speed: Float,
    autoContinue: Boolean,
    isLoading: Boolean,
    isBookmarked: Boolean,
    aiInsightText: String?,
    isInsightLoading: Boolean,
    audioProgress: Float,
    audioCurrentTime: Long,
    audioTotalTime: Long,
    isJapaModeActive: Boolean = false,
    currentJapaCount: Int = 0,
    targetJapaCount: Int = 108,
    sleepTimerOption: SleepTimerOption = SleepTimerOption.OFF,
    sleepTimerSecondsRemaining: Long = 0L,
    onClose: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onAudioModeChange: (AudioMode) -> Unit,
    onAutoContinueToggle: () -> Unit,
    onToggleBookmark: () -> Unit,
    onVoiceSettingsClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekStarted: () -> Unit,
    onFetchAiInsight: () -> Unit,
    onJapaClick: () -> Unit = {},
    onSleepTimerClick: () -> Unit = {},
    onShareStory: () -> Unit = {},
    onOpenShorts: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val chapter = remember(verse.chapterId) { GitaData.getChapter(verse.chapterId) }
    val videoTheme = remember(verse.chapterId, verse.verseId) {
        getVerseVideoTheme(verse.chapterId, verse.verseId)
    }

    val currentSeconds = (audioCurrentTime / 1000).toInt()
    val totalSeconds = (audioTotalTime / 1000).toInt()
    val formattedCurrent = String.format("%d:%02d", currentSeconds / 60, currentSeconds % 60)
    val formattedTotal = String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60)

    val scrollState = rememberScrollState()

    // Swipe Gesture state for album art / player drag
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "animatedOffsetX"
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = dragOffsetY,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "animatedOffsetY"
    )

    val swipeGestureModifier = Modifier.pointerInput(verse.verseKey) {
        detectDragGestures(
            onDragEnd = {
                val swipeThreshold = 90f
                val absX = Math.abs(dragOffsetX)
                val absY = Math.abs(dragOffsetY)

                if (dragOffsetY > swipeThreshold && absY > absX) {
                    onClose()
                } else if (dragOffsetX < -swipeThreshold && absX > absY) {
                    onNextClick()
                } else if (dragOffsetX > swipeThreshold && absX > absY) {
                    onPrevClick()
                }

                dragOffsetX = 0f
                dragOffsetY = 0f
            },
            onDragCancel = {
                dragOffsetX = 0f
                dragOffsetY = 0f
            },
            onDrag = { change, dragAmount ->
                dragOffsetX += dragAmount.x
                dragOffsetY += dragAmount.y
                if (dragOffsetY > 0 || Math.abs(dragOffsetX) > Math.abs(dragOffsetY)) {
                    change.consume()
                }
            }
        )
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("full_screen_player"),
        color = Color(0xFF140D08) // Deep warm sacred dark background like Spotify player
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Ambient glowing gradient background matching verse theme
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                videoTheme.primaryColor.copy(alpha = 0.28f),
                                Color(0xFF1B110A),
                                Color(0xFF0F0804)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Drag Handle & Top Header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Swipe Down Drag Indicator Handle
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp, bottom = 4.dp)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.35f))
                            .then(swipeGestureModifier)
                    )

                    // Top Header Row (Collapse Arrow | Chapter Title | Settings)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .then(swipeGestureModifier),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("collapse_player_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Collapse Player",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "अध्याय से चल रहा है" else "PLAYING FROM CHAPTER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB74D),
                                letterSpacing = 1.1.sp
                            )
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) {
                                    "अध्याय ${verse.chapterId}: ${chapter?.nameHindi ?: ""}"
                                } else {
                                    "Chapter ${verse.chapterId}: ${chapter?.nameEnglish ?: ""}"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1.5m Video / Shorts Button
                            Surface(
                                onClick = onOpenShorts,
                                shape = CircleShape,
                                color = Color(0xFF261810),
                                border = BorderStroke(1.dp, Color(0xFFFFA726).copy(alpha = 0.7f)),
                                modifier = Modifier.testTag("player_open_shorts_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🎬",
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = if (appLanguage == AppLanguage.HINDI) "शॉर्ट्स" else "Shorts",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFE082)
                                    )
                                }
                            }

                            // Share Verse Story Card Button
                            IconButton(
                                onClick = onShareStory,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_share_story_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Story",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Japa Mode Button
                            IconButton(
                                onClick = onJapaClick,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_japa_button")
                            ) {
                                Text(text = "📿", fontSize = 16.sp)
                            }

                            // Sleep Timer Button
                            IconButton(
                                onClick = onSleepTimerClick,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("player_sleep_timer_button")
                            ) {
                                if (sleepTimerSecondsRemaining > 0) {
                                    val mins = (sleepTimerSecondsRemaining + 59) / 60
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF64B5F6)
                                    ) {
                                        Text(
                                            text = "${mins}m",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Bedtime,
                                        contentDescription = "Sleep Timer",
                                        tint = if (sleepTimerOption != SleepTimerOption.OFF) Color(0xFF64B5F6) else Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Center Section: Flexible Balanced Album Cover & Active Text Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Album Cover / Animation Canvas (Balanced 54% width)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.54f)
                            .aspectRatio(1f)
                            .graphicsLayer {
                                translationX = animatedOffsetX * 0.75f
                                translationY = animatedOffsetY.coerceAtLeast(0f) * 0.75f
                            }
                            .then(swipeGestureModifier)
                            .shadow(12.dp, shape = RoundedCornerShape(18.dp), spotColor = videoTheme.primaryColor)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF24150C)),
                        contentAlignment = Alignment.Center
                    ) {
                        SpiritualArtworkCanvas(
                            verse = verse,
                            isPlaying = isPlaying,
                            appLanguage = appLanguage,
                            showTitleBadge = true,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Authentic Recitation Indicator Pill over Cover
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp),
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "🕉️ संस्कृत श्लोक" else "🕉️ Sanskrit Shloka",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFE082),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Track Title Row (Chapter/Verse Title & Subtitle + Bookmark Heart Button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) {
                                    "अध्याय ${verse.chapterId}, श्लोक ${verse.verseId}"
                                } else {
                                    "Chapter ${verse.chapterId}, Verse ${verse.verseId}"
                                },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) {
                                    "श्रीमद्भगवद्गीता • ${chapter?.nameHindi ?: ""}"
                                } else {
                                    "Bhagavad Gita • ${chapter?.nameEnglish ?: ""}"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD7CCC8)
                            )
                        }

                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("player_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark Verse",
                                tint = if (isBookmarked) Color(0xFFFFB74D) else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Active Text Display Card (Devotional Lyrics Style Display)
                    val textCardScale by animateFloatAsState(
                        targetValue = if (isPlaying) 1.01f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "textCardScale"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = textCardScale
                                scaleY = textCardScale
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A10).copy(alpha = 0.88f)),
                        border = BorderStroke(1.dp, Color(0xFFE67E22).copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = verse.shlokaSanskrit,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFFFFE082),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val translationText = if (appLanguage == AppLanguage.HINDI) {
                                "अर्थ: ${verse.translationHindi}"
                            } else {
                                "Meaning: ${verse.translationEnglish}"
                            }
                            Text(
                                text = translationText,
                                fontSize = 12.sp,
                                color = Color(0xFFFFCC80),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Bottom Controls Section (Slider, Buttons, AI Insight Pill)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    // Audio Scrubbing Slider & Timers
                    var localProgress by remember(audioProgress) { mutableFloatStateOf(audioProgress) }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = localProgress,
                            onValueChange = { 
                                localProgress = it
                                onSeekStarted()
                            },
                            onValueChangeFinished = { onSeek(localProgress) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("player_progress_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFA726),
                                activeTrackColor = Color(0xFFFFA726),
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formattedCurrent,
                                fontSize = 11.sp,
                                color = Color(0xFFB0BEC5)
                            )
                            Text(
                                text = formattedTotal,
                                fontSize = 11.sp,
                                color = Color(0xFFB0BEC5)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Primary Audio Player Controls Row (Spotify Style layout)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Auto-Continue / Repeat All Button
                        IconButton(
                            onClick = onAutoContinueToggle,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("player_auto_continue_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = "Auto Continue",
                                tint = if (autoContinue) Color(0xFFFFA726) else Color.White.copy(alpha = 0.45f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Previous Track Button
                        IconButton(
                            onClick = onPrevClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("player_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Verse",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Big Circular Play / Pause Button
                        Surface(
                            onClick = onPlayPauseToggle,
                            shape = CircleShape,
                            color = Color(0xFFFFA726),
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(10.dp, CircleShape, spotColor = Color(0xFFFFA726))
                                .testTag("player_play_pause_button")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(26.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color(0xFF140D08),
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                        }

                        // Next Track Button
                        IconButton(
                            onClick = onNextClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("player_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Verse",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Speed Toggle Button
                        Surface(
                            onClick = {
                                val nextSpeed = when (speed) {
                                    1.0f -> 1.25f
                                    1.25f -> 1.5f
                                    1.5f -> 0.8f
                                    else -> 1.0f
                                }
                                onSpeedChange(nextSpeed)
                            },
                            shape = CircleShape,
                            color = Color(0xFF332014),
                            border = BorderStroke(1.dp, Color(0xFFE67E22).copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("player_speed_button")
                        ) {
                            Text(
                                text = "${speed}x",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB74D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Compact AI Insight / Meaning Pill Strip (No Scroll needed!)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_lyrics_preview_card"),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF261810),
                        border = BorderStroke(1.dp, Color(0xFFE67E22).copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB74D),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (aiInsightText != null) {
                                        aiInsightText
                                    } else if (appLanguage == AppLanguage.HINDI) {
                                        "ज्ञान: ${verse.meaningHindi}"
                                    } else {
                                        "Wisdom: ${verse.meaningEnglish}"
                                    },
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFECB3),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (aiInsightText == null && !isInsightLoading) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (appLanguage == AppLanguage.HINDI) "AI चिन्तन" else "Get AI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB74D),
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFE67E22).copy(alpha = 0.25f))
                                        .clickable { onFetchAiInsight() }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            } else if (isInsightLoading) {
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    color = Color(0xFFFFB74D),
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 🌿 3-Life Applications per Shloka (व्यक्तिगत, पारिवारिक, करियर)
                    LifeApplicationCard(
                        verse = verse,
                        appLanguage = appLanguage,
                        initiallyExpanded = false
                    )
                }
            }
        }
    }
}
