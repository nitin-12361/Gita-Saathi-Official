package com.nkapps.gitasaathi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.ui.Screen

sealed class BottomNavTab(
    val screen: Screen?,
    val labelHindi: String,
    val labelEnglish: String,
    val icon: ImageVector,
    val tag: String
) {
    object Home : BottomNavTab(Screen.HOME, "होम", "Home", Icons.Default.Home, "bottom_tab_home")
    object Shorts : BottomNavTab(Screen.SHORTS_CHAPTERS, "शॉर्ट्स", "Shorts", Icons.Default.Videocam, "bottom_tab_shorts")
    object Chapters : BottomNavTab(Screen.CHAPTERS_LIST, "अध्याय", "Chapters", Icons.Default.Book, "bottom_tab_chapters")
    object AiGuide : BottomNavTab(null, "गीता AI", "Gita AI", Icons.Default.AutoAwesome, "bottom_tab_ai")
}

@Composable
fun GitaBottomNavigationBar(
    currentScreen: Screen,
    appLanguage: AppLanguage,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        BottomNavTab.Home,
        BottomNavTab.Shorts,
        BottomNavTab.Chapters,
        BottomNavTab.AiGuide
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.height(72.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = when (tab) {
                    is BottomNavTab.Home -> currentScreen == Screen.HOME
                    is BottomNavTab.Shorts -> currentScreen == Screen.SHORTS_CHAPTERS || currentScreen == Screen.SHORTS
                    is BottomNavTab.Chapters -> currentScreen == Screen.CHAPTERS_LIST || currentScreen == Screen.CHAPTER_DETAIL || currentScreen == Screen.VERSE_DETAIL
                    is BottomNavTab.AiGuide -> false
                }

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = if (appLanguage == AppLanguage.HINDI) tab.labelHindi else tab.labelEnglish,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (appLanguage == AppLanguage.HINDI) tab.labelHindi else tab.labelEnglish,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.testTag(tab.tag)
                )
            }
        }
    }
}
