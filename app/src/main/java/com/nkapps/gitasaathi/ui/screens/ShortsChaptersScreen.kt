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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.R
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.Chapter
import com.nkapps.gitasaathi.data.GitaData

@Composable
fun ShortsChaptersScreen(
    appLanguage: AppLanguage,
    chapters: List<Chapter> = GitaData.CHAPTERS,
    onChapterVideoClick: (Int) -> Unit,
    onPlayAllVideosClick: () -> Unit
) {
    val isHindi = appLanguage == AppLanguage.HINDI

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("shorts_chapters_grid_screen"),
        contentPadding = PaddingValues(top = 10.dp, bottom = 120.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner
        item(span = { GridItemSpan(2) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shorts_hub_header_card"),
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
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFF7A00),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Shorts",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "🎬 गीता शॉर्ट्स (१८ अध्याय)" else "🎬 Gita Shorts (18 Chapters)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFFC05621)
                            )
                            Text(
                                text = if (isHindi) "अध्याय चुनें और 9:16 रील्स वीडियो देखें" else "Select Chapter to Watch 9:16 Reels",
                                fontSize = 12.sp,
                                color = Color(0xFF7B341E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onPlayAllVideosClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7A00),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "▶️ सभी वीडियो रील्स देखें" else "▶️ Play All Video Reels",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. All 18 Chapter Thumbnail Cards (2-Column Grid)
        items(chapters, key = { it.id }) { chapter ->
            ShortsChapterThumbnailCard(
                chapter = chapter,
                appLanguage = appLanguage,
                onClick = { onChapterVideoClick(chapter.id) }
            )
        }
    }
}

@Composable
private fun ShortsChapterThumbnailCard(
    chapter: Chapter,
    appLanguage: AppLanguage,
    onClick: () -> Unit
) {
    val isHindi = appLanguage == AppLanguage.HINDI
    val thumbnailRes = getChapterThumbnailRes(chapter.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clickable { onClick() }
            .testTag("shorts_chapter_card_"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF23150D)),
        border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Artwork Thumbnail
            Image(
                painter = painterResource(id = thumbnailRes),
                contentDescription = chapter.nameHindi,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Black.copy(alpha = 0.25f),
                            0.45f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.88f)
                        )
                    )
            )

            // Top-left Chapter Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFF7A00),
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = if (isHindi) "अध्याय ${chapter.id}" else "CH ${chapter.id}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            // Center Play Icon Circle
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Bottom Chapter Information
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = if (isHindi) chapter.nameHindi else chapter.nameEnglish,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isHindi) "${chapter.versesCount} श्लोक" else "${chapter.versesCount} Verses",
                        fontSize = 10.sp,
                        color = Color(0xFFFFD54F)
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isHindi) "▶️ शॉर्ट्स" else "▶️ Shorts",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Returns a high quality thumbnail image resource for each of the 18 chapters.
 */
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
