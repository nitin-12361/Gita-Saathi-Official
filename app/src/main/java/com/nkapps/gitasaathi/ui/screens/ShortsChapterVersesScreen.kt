package com.nkapps.gitasaathi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.nkapps.gitasaathi.R
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.Chapter
import com.nkapps.gitasaathi.data.GitaVideoResolver
import com.nkapps.gitasaathi.data.Verse

@Composable
fun ShortsChapterVersesScreen(
    chapter: Chapter,
    verses: List<Verse>,
    appLanguage: AppLanguage,
    onVerseClick: (Verse) -> Unit,
    onPlayAllClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val isHindi = appLanguage == AppLanguage.HINDI
    val context = LocalContext.current

    // Proactive background preloader: Cache first 7 videos of the chapter while browsing grid
    androidx.compose.runtime.LaunchedEffect(verses, appLanguage) {
        val initialVerses = verses.take(7)
        val uris = initialVerses.mapNotNull { v ->
            com.nkapps.gitasaathi.data.GitaVideoResolver.getVideoUri(context, v.chapterId, v.verseId, appLanguage)
        }
        com.nkapps.gitasaathi.data.GitaExoPlayerManager.preloadNextVideos(context, uris)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("shorts_chapter_verses_grid_screen"),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner with Back button & Play All
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shorts_verses_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF7ED)
                ),
                border = BorderStroke(1.dp, Color(0xFFFFD8A8))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFFC05621)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "अध्याय ${chapter.id}: ${chapter.nameHindi}" else "Chapter ${chapter.id}: ${chapter.nameEnglish}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFFC05621),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isHindi) "कुल ${verses.size} श्लोक वीडियो • 9:16 शॉर्ट्स" else "Total ${verses.size} Verse Videos • 9:16 Shorts",
                                fontSize = 12.sp,
                                color = Color(0xFF7B341E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onPlayAllClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7A00),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play All",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "▶️ सम्पूर्ण अध्याय के वीडियो देखें" else "▶️ Play All Chapter Videos",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. All Verses Cards Grid
        items(verses, key = { it.verseKey }) { verse ->
            ShortsVerseThumbnailCard(
                verse = verse,
                appLanguage = appLanguage,
                onClick = { onVerseClick(verse) }
            )
        }
    }
}

@Composable
private fun ShortsVerseThumbnailCard(
    verse: Verse,
    appLanguage: AppLanguage,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isHindi = appLanguage == AppLanguage.HINDI
    val fallbackRes = getChapterThumbnailRes(verse.chapterId)

    // Direct Video URI (MP4 from Hugging Face or local cache)
    val videoUri = remember(verse.chapterId, verse.verseId, appLanguage) {
        GitaVideoResolver.getVideoUri(context, verse.chapterId, verse.verseId, appLanguage)
    }

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .crossfade(true)
            .build()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() }
            .testTag("shorts_verse_card_${verse.chapterId}_${verse.verseId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E130B)),
        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Live Video Frame Thumbnail (Auto-extracted from real MP4 video at 1.0s)
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(videoUri)
                    .videoFrameMillis(1000L)
                    .crossfade(true)
                    .error(fallbackRes)
                    .placeholder(fallbackRes)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Verse ${verse.chapterId}.${verse.verseId}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Black.copy(alpha = 0.3f),
                            0.35f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.90f)
                        )
                    )
            )

            // Top-left Verse Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFF7A00),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = if (isHindi) "श्लोक ${verse.chapterId}.${verse.verseId}" else "Verse ${verse.chapterId}.${verse.verseId}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Center Glowing Play Button
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.Center)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Bottom Verse Sanskrit Line & Translation snippet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = verse.shlokaSanskrit.lines().firstOrNull() ?: verse.shlokaSanskrit,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFFFD54F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isHindi) verse.translationHindi else verse.translationEnglish,
                    fontSize = 10.sp,
                    color = Color(0xFFE2E8F0),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

private fun getChapterThumbnailRes(chapterId: Int): Int {
    return when (chapterId) {
        1 -> R.drawable.bg_scene_chariot
        2 -> R.drawable.bg_scene_karma
        3 -> R.drawable.bg_scene_karma
        4 -> R.drawable.bg_scene_flute
        5 -> R.drawable.bg_scene_dhyana
        6 -> R.drawable.bg_scene_dhyana
        7 -> R.drawable.bg_scene_flute
        8 -> R.drawable.bg_scene_dhyana
        9 -> R.drawable.bg_scene_flute
        10 -> R.drawable.bg_scene_flute
        11 -> R.drawable.bg_scene_vishwaroop
        12 -> R.drawable.bg_scene_flute
        13 -> R.drawable.bg_scene_karma
        14 -> R.drawable.bg_scene_dhyana
        15 -> R.drawable.bg_scene_flute
        16 -> R.drawable.bg_scene_karma
        17 -> R.drawable.bg_scene_karma
        18 -> R.drawable.bg_scene_moksha
        else -> R.drawable.bg_scene_chariot
    }
}
