package com.nkapps.gitasaathi.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.RecentPositionEntity
import com.nkapps.gitasaathi.data.UserProfile
import com.nkapps.gitasaathi.data.Verse

import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Switch
import com.nkapps.gitasaathi.notifications.DailyShlokaScheduler

@Composable
fun GitaNavigationDrawerContent(
    appLanguage: AppLanguage,
    userProfile: UserProfile? = null,
    onProfileClick: () -> Unit = {},
    onChaptersClick: () -> Unit,
    onShortsClick: () -> Unit = {},
    onQuizClick: () -> Unit = {},
    onWallpapersClick: () -> Unit = {},
    onJapaClick: () -> Unit = {},
    onBookmarksClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onDailyShlokaNotifClick: () -> Unit = {},
    onVoiceSettingsClick: () -> Unit = {},
    onShareClick: () -> Unit,
    onAboutClick: () -> Unit,
    onGoldClick: () -> Unit = {}
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .testTag("gita_navigation_drawer"),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "ॐ",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "गीता साथी" else "Gita Saathi",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "श्रीमद्भगवद्गीता" else "Srimad Bhagavad Gita",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (appLanguage == AppLanguage.HINDI)
                            "दिव्य आध्यात्मिक ज्ञान एवं AI साथी"
                        else
                            "Divine Spiritual Wisdom & AI Companion",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )

                    // Profile / Registration Chip in Drawer Header
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onProfileClick() }
                            .testTag("drawer_user_profile_chip"),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (userProfile?.photoUrl != null) {
                                AsyncImage(
                                    model = userProfile.photoUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp).clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (userProfile?.isRegistered == true && userProfile.displayName.isNotBlank())
                                        userProfile.displayName
                                    else if (appLanguage == AppLanguage.HINDI)
                                        "अतिथि पाठक (साइन इन / रजिस्टर करें)"
                                    else
                                        "Guest Reader (Sign In / Register)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (userProfile?.isRegistered == true)
                                        userProfile.email.ifEmpty { if (appLanguage == AppLanguage.HINDI) "खाता सक्रिय" else "Account Active" }
                                    else if (appLanguage == AppLanguage.HINDI)
                                        "प्रगति सहेजने हेतु टैप करें"
                                    else
                                        "Tap to register & track progress",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Items (Scrollable)
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                DrawerMenuItem(
                    icon = Icons.Default.Person,
                    label = if (appLanguage == AppLanguage.HINDI) "पाठक प्रोफ़ाइल एवं खाता" else "Reader Profile & Account",
                    testTag = "drawer_item_profile",
                    onClick = onProfileClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Gita Saathi Gold (Ad-Free) Menu Item
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFF2C1E14)),
                    border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFFFD700)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable(onClick = onGoldClick)
                        .testTag("drawer_item_gold")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "👑", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "गीता साथी Gold" else "Gita Saathi Gold",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color(0xFFFFD700)
                            )
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "100% विज्ञापन-मुक्त • धर्म सेवा" else "100% Ad-Free • Dharma Seva",
                                fontSize = 10.sp,
                                color = androidx.compose.ui.graphics.Color(0xFFD7CCC8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                DrawerMenuItem(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    label = if (appLanguage == AppLanguage.HINDI) "अध्याय सूची (1-18)" else "Chapters List (1–18)",
                    testTag = "drawer_item_chapters",
                    onClick = onChaptersClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Gita Shorts / 1.5m Video Reels Menu Item
                DrawerMenuItem(
                    icon = Icons.Default.AutoAwesome,
                    label = if (appLanguage == AppLanguage.HINDI) "🎬 गीता शॉर्ट्स (वीडियो रील्स)" else "🎬 Gita Shorts (Video Reels)",
                    testTag = "drawer_item_shorts",
                    onClick = onShortsClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Daily Gita Quiz Menu Item
                DrawerMenuItem(
                    icon = Icons.Default.EmojiEvents,
                    label = if (appLanguage == AppLanguage.HINDI) "🎯 दैनिक गीता प्रश्नोत्तरी" else "🎯 Daily Gita Quiz",
                    testTag = "drawer_item_quiz",
                    onClick = onQuizClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // HD Krishna Wallpapers Menu Item
                DrawerMenuItem(
                    icon = Icons.Default.Wallpaper,
                    label = if (appLanguage == AppLanguage.HINDI) "🖼️ पावन कृष्ण वॉलपेपर्स" else "🖼️ Krishna HD Wallpapers",
                    testTag = "drawer_item_wallpapers",
                    onClick = onWallpapersClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Digital Japa Mala (108 Beads) Menu Item
                DrawerMenuItem(
                    icon = Icons.Default.Circle,
                    label = if (appLanguage == AppLanguage.HINDI) "📿 डिजिटल जप माला (१०८ मणके)" else "📿 Digital Japa Mala (108 Beads)",
                    testTag = "drawer_item_japa_mala",
                    onClick = onJapaClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                DrawerMenuItem(
                    icon = Icons.Default.Bookmark,
                    label = if (appLanguage == AppLanguage.HINDI) "बुकमार्क एवं सहेजे गए श्लोक" else "Bookmarks & Saved Verses",
                    testTag = "drawer_item_bookmarks",
                    onClick = onBookmarksClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                DrawerMenuItem(
                    icon = Icons.Default.History,
                    label = if (appLanguage == AppLanguage.HINDI) "पठन इतिहास" else "Reading History",
                    testTag = "drawer_item_history",
                    onClick = onHistoryClick
                )

                Spacer(modifier = Modifier.height(4.dp))

                DrawerMenuItem(
                    icon = Icons.Default.Notifications,
                    label = if (appLanguage == AppLanguage.HINDI) "दैनिक श्लोक अधिसूचना" else "Daily Shloka Notification",
                    testTag = "drawer_item_notifications",
                    onClick = onDailyShlokaNotifClick
                )


                Spacer(modifier = Modifier.height(4.dp))

                DrawerMenuItem(
                    icon = Icons.Default.Share,
                    label = if (appLanguage == AppLanguage.HINDI) "ऐप साझा करें" else "Share App",
                    testTag = "drawer_item_share",
                    onClick = onShareClick
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                DrawerMenuItem(
                    icon = Icons.Default.Info,
                    label = if (appLanguage == AppLanguage.HINDI) "गीता साथी के बारे में" else "About Gita Saathi",
                    testTag = "drawer_item_about",
                    onClick = onAboutClick
                )
            }

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Built with ❤️ by Nitin",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        selected = false,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun AboutGitaSaathiDialog(
    appLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("about_gita_saathi_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.clip(CircleShape),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "ॐ",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (appLanguage == AppLanguage.HINDI) "गीता साथी" else "Gita Saathi",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "v1.0 • Divine AI Companion",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (appLanguage == AppLanguage.HINDI)
                        "श्रीमद्भगवद्गीता के अमर संदेशों का अध्ययन, श्रवण एवं AI चिंतन हेतु आपका व्यक्तिगत आध्यात्मिक साथी।"
                    else
                        "Your divine spiritual companion to read, listen, and reflect upon the timeless wisdom of Srimad Bhagavad Gita with real-time AI guidance.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp))

                // Built by Nitin card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Built by Nitin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (appLanguage == AppLanguage.HINDI) "बग या सुझाव के लिए संपर्क करें:" else "For bugs / suggestions contact:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "nitinsingh12345678@gmail.com",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:nitinsingh12345678@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Gita Saathi App Feedback / Bug Suggestion")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (appLanguage == AppLanguage.HINDI) "ईमेल करें" else "Contact Us")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (appLanguage == AppLanguage.HINDI) "बंद करें" else "Close")
                    }
                }
            }
        }
    }
}

@Composable
fun ReadingHistoryDialog(
    appLanguage: AppLanguage,
    recentPosition: RecentPositionEntity?,
    readingHistory: List<Verse>,
    onVerseClick: (Verse) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("reading_history_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "पठन इतिहास" else "Reading History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (recentPosition == null && readingHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (appLanguage == AppLanguage.HINDI)
                                "कोई हालिया पठन इतिहास नहीं मिला।"
                            else
                                "No reading history recorded yet.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    ) {
                        if (readingHistory.isEmpty() && recentPosition != null) {
                            item {
                                Text(
                                    text = if (appLanguage == AppLanguage.HINDI)
                                        "अंतिम पढ़ा गया स्थान: अध्याय ${recentPosition.chapterId}, श्लोक ${recentPosition.verseId}"
                                    else
                                        "Last Read: Chapter ${recentPosition.chapterId}, Verse ${recentPosition.verseId}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            items(readingHistory) { verse ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            onVerseClick(verse)
                                            onDismiss()
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = verse.verseReference,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (appLanguage == AppLanguage.HINDI) verse.transliteration else verse.shlokaSanskrit,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (appLanguage == AppLanguage.HINDI) verse.translationHindi else verse.translationEnglish,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (appLanguage == AppLanguage.HINDI) "बंद करें" else "Close")
                    }
                }
            }
        }
    }
}

@Composable
fun DailyNotificationDialog(
    appLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var isEnabled by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(DailyShlokaScheduler.isNotificationEnabled(context))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("daily_notification_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "दैनिक श्लोक अधिसूचना" else "Daily Shloka Notification",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "रोज़ाना सुबह 8:00 बजे श्लोक" else "Daily Morning Shloka at 8:00 AM",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (appLanguage == AppLanguage.HINDI) "हर सुबह आध्यात्मिक प्रेरणा प्राप्त करें" else "Receive spiritual wisdom every morning",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { checked ->
                                isEnabled = checked
                                DailyShlokaScheduler.setNotificationEnabled(context, checked)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        DailyShlokaScheduler.showImmediateNotification(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "अभी अधिसूचना टेस्ट करें" else "Send Test Notification Now",
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (appLanguage == AppLanguage.HINDI) "बंद करें" else "Close")
                    }
                }
            }
        }
    }
}

