package com.nkapps.gitasaathi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.ui.Screen

import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RecordVoiceOver

import com.nkapps.gitasaathi.data.DailyQuizState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitaTopBar(
    currentScreen: Screen,
    appLanguage: AppLanguage,
    isDarkMode: Boolean,
    onMenuClick: () -> Unit = {},
    onBackClick: () -> Unit,
    onLanguageToggle: () -> Unit,
    onDarkModeToggle: () -> Unit,
    onAiChatClick: () -> Unit = {},
    onApiKeyClick: () -> Unit = {},
    onGoldClick: () -> Unit = {},
    quizState: DailyQuizState? = null,
    onQuizClick: () -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("gita_top_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.clip(CircleShape),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "ॐ",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (appLanguage == AppLanguage.HINDI) "गीता साथी" else "Gita Saathi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            if (currentScreen != Screen.HOME) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.testTag("top_bar_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            // Gita Saathi Gold Button
            IconButton(
                onClick = onGoldClick,
                modifier = Modifier.testTag("top_bar_gold_button")
            ) {
                Text(
                    text = "👑",
                    fontSize = 18.sp
                )
            }

            // Daily Streak Badge (Habit Loop) or AI Button
            if (quizState != null && quizState.quizStreak > 0) {
                Surface(
                    onClick = onQuizClick,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .testTag("top_bar_streak_badge")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${quizState.quizStreak}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                IconButton(
                    onClick = onAiChatClick,
                    modifier = Modifier.testTag("top_bar_ai_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ask Gita AI",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Language Dual Switch: [ 🇮🇳 हिंदी ] | [ 🇬🇧 English ]
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.testTag("language_toggle_button")
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { if (appLanguage != AppLanguage.HINDI) onLanguageToggle() },
                        shape = RoundedCornerShape(16.dp),
                        color = if (appLanguage == AppLanguage.HINDI) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.height(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 7.dp)
                        ) {
                            Text(
                                text = "हिंदी",
                                fontSize = 11.sp,
                                fontWeight = if (appLanguage == AppLanguage.HINDI) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (appLanguage == AppLanguage.HINDI) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Surface(
                        onClick = { if (appLanguage != AppLanguage.ENGLISH) onLanguageToggle() },
                        shape = RoundedCornerShape(16.dp),
                        color = if (appLanguage == AppLanguage.ENGLISH) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.height(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 7.dp)
                        ) {
                            Text(
                                text = "En",
                                fontSize = 11.sp,
                                fontWeight = if (appLanguage == AppLanguage.ENGLISH) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (appLanguage == AppLanguage.ENGLISH) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Dark/Light Mode Switch
            IconButton(
                onClick = onDarkModeToggle,
                modifier = Modifier.testTag("theme_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
