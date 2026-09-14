package com.nkapps.gitasaathi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.ads.AdmobBanner
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.Chapter
import com.nkapps.gitasaathi.data.DailyQuizState
import com.nkapps.gitasaathi.data.GitaMood
import com.nkapps.gitasaathi.data.GitaMoodData
import com.nkapps.gitasaathi.data.RecentPositionEntity
import com.nkapps.gitasaathi.data.StreakData
import com.nkapps.gitasaathi.data.Verse

@Composable
fun HomeScreen(
    appLanguage: AppLanguage,
    searchQuery: String,
    recentPosition: RecentPositionEntity?,
    chapters: List<Chapter>,
    shlokaOfTheDay: Verse,
    isBookmarked: Boolean,
    sadhanaStreak: StreakData = StreakData(),
    quizState: DailyQuizState? = null,
    onSearchQueryChange: (String) -> Unit,
    onChapterClick: (Int) -> Unit,
    onVerseClick: (Verse) -> Unit,
    onPlayVerseAudio: (Verse) -> Unit,
    onToggleBookmark: (Verse) -> Unit,
    onBookmarkPageClick: () -> Unit,
    onOpenAiChat: () -> Unit = {},
    onMoodClick: (GitaMood) -> Unit = {},
    onShareStory: (Verse) -> Unit = {},
    onOpenShorts: (Verse?) -> Unit = {},
    onViewAllChaptersClick: () -> Unit = {},
    onOpenQuiz: () -> Unit = {},
    onOpenWallpapers: () -> Unit = {},
    onOpenJapaMala: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val isHindi = appLanguage == AppLanguage.HINDI

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Sacred Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "🕉️",
                                fontSize = 22.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "ॐ श्री परमात्मने नमः" else "Om Shri Paramatmane Namah",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isHindi) "श्रीमद्भगवद्गीता • दिव्य ज्ञान एवं साधना" else "Srimad Bhagavad Gita • Divine Wisdom",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. MAIN 3x3 GRID (Matching Reference Image Style)
        item {
            val gridDividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_3x3_grid_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // ROW 1: 18 Chapters | Shorts Videos | Audio Shloka
                    Row(modifier = Modifier.fillMaxWidth().height(115.dp)) {
                        GridCellItem(
                            icon = Icons.Default.Book,
                            title = if (isHindi) "१८ अध्याय" else "18 Chapters",
                            subtitle = if (isHindi) "सम्पूर्ण गीता" else "All Chapters",
                            onClick = { onViewAllChaptersClick() },
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(color = gridDividerColor, thickness = 1.dp)
                        GridCellItem(
                            icon = Icons.Default.Videocam,
                            title = if (isHindi) "शॉर्ट्स" else "Shorts Videos",
                            subtitle = if (isHindi) "वीडियो श्लोक" else "Video Reels",
                            onClick = { onOpenShorts(null) },
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(color = gridDividerColor, thickness = 1.dp)
                        GridCellItem(
                            icon = Icons.Default.Headphones,
                            title = if (isHindi) "ऑडियो" else "Audio",
                            subtitle = if (isHindi) "संस्कृत पाठ" else "Chanting",
                            onClick = { onPlayVerseAudio(shlokaOfTheDay) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = gridDividerColor, thickness = 1.dp)

                    // ROW 2: Gita AI | Daily Shloka | Bookmarks
                    Row(modifier = Modifier.fillMaxWidth().height(115.dp)) {
                        GridCellItem(
                            icon = Icons.Default.AutoAwesome,
                            title = if (isHindi) "गीता AI साथी" else "Gita AI Guru",
                            subtitle = if (isHindi) "मार्गदर्शन" else "Ask Krishna",
                            onClick = { onOpenAiChat() },
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(color = gridDividerColor, thickness = 1.dp)
                        GridCellItem(
                            icon = Icons.Default.WbSunny,
                            title = if (isHindi) "आज का श्लोक" else "Daily Shloka",
                            subtitle = if (isHindi) "दैनिक उपदेश" else "Today's Verse",
                            onClick = { onVerseClick(shlokaOfTheDay) },
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(color = gridDividerColor, thickness = 1.dp)
                        GridCellItem(
                            icon = Icons.Default.Star,
                            title = if (isHindi) "मेरी पसंद" else "Bookmarks",
                            subtitle = if (isHindi) "सहेजे श्लोक" else "Saved Verses",
                            onClick = { onBookmarkPageClick() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = gridDividerColor, thickness = 1.dp)

                    // ROW 3: Search | Gita Summary | Mind Peace
                    Row(modifier = Modifier.fillMaxWidth().height(115.dp)) {
                        GridCellItem(
                            icon = Icons.Default.Search,
                            title = if (isHindi) "श्लोक खोजें" else "Search",
                            subtitle = if (isHindi) "विषय या शब्द" else "Find Verse",
                            onClick = onSearchClick,
                            modifier = Modifier.weight(1f).testTag("home_grid_search")
                        )
                        VerticalDivider(color = gridDividerColor, thickness = 1.dp)
                        GridCellItem(
                            icon = Icons.Default.Circle,
                            title = if (isHindi) "जप माला" else "Japa Mala",
                            subtitle = if (isHindi) "१०८ मणके" else "108 Beads",
                            onClick = { onOpenJapaMala() },
                            modifier = Modifier.weight(1f).testTag("home_grid_japa_mala")
                        )
                        VerticalDivider(color = gridDividerColor, thickness = 1.dp)
                        GridCellItem(
                            icon = Icons.Default.SelfImprovement,
                            title = if (isHindi) "मन की शांति" else "Mind Peace",
                            subtitle = if (isHindi) "समाधान" else "Solutions",
                            onClick = { onMoodClick(GitaMoodData.MOODS.first()) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // AdMob Adaptive Banner (Positioned right below 3x3 Grid, hidden for Gold users)
        item {
            AdmobBanner()
        }

        // 3. DAILY GITA QUIZ CARD (दैनिक गीता प्रश्नोत्तरी)
        item {
            val isCompleted = quizState?.isCompletedToday == true
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenQuiz() }
                    .testTag("home_daily_quiz_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) Color(0xFF16A34A).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.dp,
                    if (isCompleted) Color(0xFF16A34A).copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "आज की गीता प्रश्नोत्तरी" else "Daily Gita Quiz",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) Color(0xFF22C55E) else MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isCompleted) {
                                        if (isHindi) "आज की साधना पूर्ण • +${quizState?.todayEarnedPoints ?: 0} पुण्य अंक" else "Completed Today • +${quizState?.todayEarnedPoints ?: 0} Karma Pts"
                                    } else {
                                        if (isHindi) "5 प्रश्न • +50 पुण्य अंक (Karma Points)" else "5 Questions • +50 Karma Points"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (quizState != null && quizState.quizStreak > 0) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "🔥", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${quizState.quizStreak} ${if (isHindi) "दिन" else "days"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isCompleted) {
                                if (isHindi) "आज का स्कोर: ${quizState?.todayScore ?: 0}/5 सही उत्तर 🌟" else "Today's Score: ${quizState?.todayScore ?: 0}/5 Correct 🌟"
                            } else {
                                if (isHindi) "महाभारत व गीता के 5 ज्ञानवर्धक प्रश्न हल करें" else "Answer 5 sacred questions & earn Karma Badges"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = onOpenQuiz,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) Color(0xFF16A34A) else Color(0xFFFF7A00),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (isCompleted) {
                                    if (isHindi) "परिणाम देखें 🏆" else "View Score 🏆"
                                } else {
                                    if (isHindi) "शुरू करें ▶️" else "Start Quiz ▶️"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3.5. HD Krishna Wallpapers & Quotes Gallery Preview Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_wallpapers_banner_card")
                    .clickable { onOpenWallpapers() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1610)
                ),
                border = BorderStroke(1.dp, Color(0xFFE5A93C).copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🖼️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "पावन कृष्ण वॉलपेपर्स" else "HD Krishna Wallpapers",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFBD38D)
                                    )
                                    Text(
                                        text = if (isHindi) "4K दर्शन • गीता श्लोक • 1-क्लिक में फोन पर लगाएं" else "4K Art • Gita Shlokas • 1-Click Set",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFFD8A8).copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Button(
                                onClick = onOpenWallpapers,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF7A00),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "सभी देखें ▶️" else "View All ▶️",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4 Miniature Wallpaper Thumbnails Preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val previewDrawables = listOf(
                                com.nkapps.gitasaathi.R.drawable.bg_scene_flute_portrait,
                                com.nkapps.gitasaathi.R.drawable.bg_scene_chariot_portrait,
                                com.nkapps.gitasaathi.R.drawable.bg_scene_vishwaroop,
                                com.nkapps.gitasaathi.R.drawable.bg_scene_dhyana
                            )
                            for (resId in previewDrawables) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = resId),
                                    contentDescription = null,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(9f / 14f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. FEATURED TODAY: 🌅 Shloka of the Day (Clean, Senior Friendly)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shloka_of_the_day_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌅", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "आज का पावन श्लोक" else "Today's Sacred Verse",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = if (isHindi) "अध्याय ${shlokaOfTheDay.chapterId}.${shlokaOfTheDay.verseId}" else "Chapter ${shlokaOfTheDay.chapterId}.${shlokaOfTheDay.verseId}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sanskrit Verse Text
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = shlokaOfTheDay.shlokaSanskrit,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Meaning Translation
                    Text(
                        text = if (isHindi) shlokaOfTheDay.translationHindi else shlokaOfTheDay.translationEnglish,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons (Listen & Watch Shorts)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onPlayVerseAudio(shlokaOfTheDay) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "श्लोक सुनें" else "Listen",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onOpenShorts(shlokaOfTheDay) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF7A00),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Watch Shorts",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "शॉर्ट्स देखें" else "Watch Shorts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. CONTINUE READING (If Available)
        if (recentPosition != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onChapterClick(recentPosition.chapterId) }
                        .testTag("continue_reading_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "📖 जहाँ छोड़ा था वहीं से पढ़ें" else "📖 Continue Reading",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isHindi) {
                                    "अध्याय ${recentPosition.chapterId}, श्लोक ${recentPosition.verseId}"
                                } else {
                                    "Chapter ${recentPosition.chapterId}, Verse ${recentPosition.verseId}"
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { onChapterClick(recentPosition.chapterId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF7A00),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (isHindi) "पढ़ें ▶️" else "Resume ▶️",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Clean Single Grid Cell Item matching reference image
 */
@Composable
private fun GridCellItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

