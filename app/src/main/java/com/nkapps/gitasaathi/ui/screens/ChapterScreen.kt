package com.nkapps.gitasaathi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.ads.AdmobBanner
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.Chapter
import com.nkapps.gitasaathi.data.Verse
import com.nkapps.gitasaathi.ui.components.LifeApplicationCard
import com.nkapps.gitasaathi.ui.components.VerseAnimationVideoPlayer

@Composable
fun ChapterScreen(
    chapter: Chapter,
    verses: List<Verse>,
    appLanguage: AppLanguage,
    activePlayingVerse: Verse?,
    isPlayingAudio: Boolean,
    isBookmarked: (String) -> Boolean,
    aiInsightText: String?,
    isInsightLoading: Boolean,
    selectedVerseForInsight: Verse?,
    onVerseClick: (Verse) -> Unit,
    onPlayVerseAudio: (Verse) -> Unit,
    onToggleBookmark: (Verse) -> Unit,
    onFetchAiInsight: (Verse) -> Unit,
    onShareStory: (Verse) -> Unit = {},
    onOpenShorts: (Verse) -> Unit = {}
) {
    val listState = rememberLazyListState()
    var fontScale by remember { mutableFloatStateOf(1.0f) }

    // Smooth Auto-Scroll to actively playing verse (Read-Along / Karaoke mode)
    LaunchedEffect(activePlayingVerse?.chapterId, activePlayingVerse?.verseId) {
        if (activePlayingVerse != null && activePlayingVerse.chapterId == chapter.id) {
            val verseIndex = verses.indexOfFirst { it.verseId == activePlayingVerse.verseId }
            if (verseIndex >= 0) {
                listState.animateScrollToItem(verseIndex + 1)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("chapter_screen_lazy_column"),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chapter Header & Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chapter_header_summary_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "${if (appLanguage == AppLanguage.HINDI) "अध्याय" else "Chapter"} ${chapter.id}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Font Size Zoom Pill: [ A- ] [ 100% ] [ A+ ]
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier.testTag("font_size_zoom_pill")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            fontScale = (fontScale - 0.15f).coerceAtLeast(0.85f)
                                        },
                                        modifier = Modifier.size(28.dp).testTag("font_zoom_out_button"),
                                        enabled = fontScale > 0.86f
                                    ) {
                                        Text(
                                            text = "A-",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (fontScale > 0.86f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                    }

                                    Text(
                                        text = "${(fontScale * 100).toInt()}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            fontScale = (fontScale + 0.15f).coerceAtMost(1.45f)
                                        },
                                        modifier = Modifier.size(28.dp).testTag("font_zoom_in_button"),
                                        enabled = fontScale < 1.44f
                                    ) {
                                        Text(
                                            text = "A+",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (fontScale < 1.44f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${verses.size} ${if (appLanguage == AppLanguage.HINDI) "श्लोक" else "Verses"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) chapter.nameHindi else chapter.nameEnglish,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (appLanguage == AppLanguage.HINDI) {
                        Text(
                            text = chapter.nameEnglish,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) chapter.summaryHindi else chapter.summaryEnglish,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // AdMob Adaptive Banner (Discreetly hidden for Gold users)
        item {
            AdmobBanner()
        }

        // List of Verses in this Chapter
        items(
            items = verses,
            key = { "${it.chapterId}_${it.verseId}" },
            contentType = { "verse_item" }
        ) { verse ->
            val isActivePlaying = activePlayingVerse?.chapterId == verse.chapterId &&
                    activePlayingVerse?.verseId == verse.verseId &&
                    isPlayingAudio

            val isSelectedForAi = selectedVerseForInsight?.chapterId == verse.chapterId &&
                    selectedVerseForInsight?.verseId == verse.verseId

            VerseItemCard(
                verse = verse,
                appLanguage = appLanguage,
                isActivePlaying = isActivePlaying,
                isBookmarked = isBookmarked("${verse.chapterId}_${verse.verseId}"),
                aiInsightText = if (isSelectedForAi) aiInsightText else null,
                isInsightLoading = isSelectedForAi && isInsightLoading,
                fontScale = fontScale,
                onClick = { onVerseClick(verse) },
                onPlayAudio = { onPlayVerseAudio(verse) },
                onToggleBookmark = { onToggleBookmark(verse) },
                onFetchAiInsight = { onFetchAiInsight(verse) },
                onShareStory = { onShareStory(verse) },
                onOpenShorts = { onOpenShorts(verse) }
            )
        }
    }
}

@Composable
fun VerseItemCard(
    verse: Verse,
    appLanguage: AppLanguage,
    isActivePlaying: Boolean,
    isBookmarked: Boolean,
    aiInsightText: String?,
    isInsightLoading: Boolean,
    fontScale: Float = 1.0f,
    onClick: () -> Unit,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit,
    onFetchAiInsight: () -> Unit,
    onShareStory: () -> Unit = {},
    onOpenShorts: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("verse_card_${verse.chapterId}_${verse.verseId}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActivePlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.dp,
            if (isActivePlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Verse Header Row (Reference Pill, Share & Bookmark)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${if (appLanguage == AppLanguage.HINDI) "श्लोक" else "Verse"} ${verse.chapterId}.${verse.verseId}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onShareStory,
                        modifier = Modifier.size(36.dp).testTag("share_story_verse_${verse.chapterId}_${verse.verseId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp).testTag("bookmark_verse_${verse.chapterId}_${verse.verseId}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sanskrit Shloka Text (Extra Large, High Contrast for Elders)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = verse.shlokaSanskrit,
                    fontSize = (17 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center,
                    lineHeight = (26 * fontScale).sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hindi / English Translation
            Text(
                text = if (appLanguage == AppLanguage.HINDI) verse.translationHindi else verse.translationEnglish,
                fontSize = (14 * fontScale).sp,
                lineHeight = (21 * fontScale).sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 🌿 3-Life Applications per Shloka (व्यक्तिगत, पारिवारिक, करियर)
            LifeApplicationCard(
                verse = verse,
                appLanguage = appLanguage,
                initiallyExpanded = false
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 BIG ACTION BUTTONS: [ 🎬 1.5 Min वीडियो ] [ 🔊 सुनें ] [ 🤖 AI अर्थ ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. 1.5m Video / Shorts Button
                Button(
                    onClick = onOpenShorts,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE67E22),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("shorts_verse_${verse.chapterId}_${verse.verseId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Shorts",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "शॉर्ट्स" else "Shorts",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 2. Play Audio Button
                Button(
                    onClick = onPlayAudio,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActivePlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isActivePlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("play_verse_${verse.chapterId}_${verse.verseId}")
                ) {
                    Icon(
                        imageVector = if (isActivePlaying) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.PlayArrow,
                        contentDescription = "Listen",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isActivePlaying) (if (appLanguage == AppLanguage.HINDI) "चल रहा है" else "Playing") else (if (appLanguage == AppLanguage.HINDI) "सुनें" else "Listen"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 3. AI Insight Button
                OutlinedButton(
                    onClick = onFetchAiInsight,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("ai_insight_verse_${verse.chapterId}_${verse.verseId}")
                ) {
                    if (isInsightLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Gyan",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (appLanguage == AppLanguage.HINDI) "AI अर्थ" else "AI Insight",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // AI Insight Text Box (if fetched)
            if (aiInsightText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "श्रीकृष्ण AI मार्गदर्शन" else "Sri Krishna AI Insight",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = aiInsightText,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
