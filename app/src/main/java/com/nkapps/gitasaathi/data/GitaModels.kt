package com.nkapps.gitasaathi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AppLanguage(val code: String, val displayName: String, val ttsLocale: String) {
    HINDI("hi", "हिंदी", "hi_IN"),
    ENGLISH("en", "English", "en_US")
}

enum class AudioMode(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val subtitleHindi: String,
    val subtitleEnglish: String
) {
    SANSKRIT_SHLOKA(
        id = "sanskrit_shloka",
        titleHindi = "संस्कृत श्लोक",
        titleEnglish = "Sanskrit Shloka",
        subtitleHindi = "प्रामाणिक संस्कृत श्लोक गायन",
        subtitleEnglish = "Authentic Sanskrit recitation"
    );

    companion object {
        fun fromId(id: String?): AudioMode {
            return entries.firstOrNull { it.id == id } ?: SANSKRIT_SHLOKA
        }
    }
}

data class Chapter(
    val id: Int,
    val nameHindi: String,
    val nameEnglish: String,
    val titleHindi: String,
    val titleEnglish: String,
    val summaryHindi: String,
    val summaryEnglish: String,
    val versesCount: Int
)

data class Verse(
    val chapterId: Int,
    val verseId: Int,
    val shlokaSanskrit: String,
    val transliteration: String,
    val translationHindi: String,
    val translationEnglish: String,
    val meaningHindi: String,
    val meaningEnglish: String
) {
    val verseKey: String
        get() = "c${chapterId}_v${verseId}"

    val verseReference: String
        get() = "$chapterId.$verseId"
}

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val verseKey: String,
    val chapterId: Int,
    val verseId: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_position")
data class RecentPositionEntity(
    @PrimaryKey val id: Int = 1,
    val chapterId: Int,
    val verseId: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AudioState(
    val isPlaying: Boolean = false,
    val currentChapterId: Int = 0,
    val currentVerseId: Int = 0,
    val narrationLanguage: AppLanguage = AppLanguage.HINDI,
    val speed: Float = 1.0f,
    val autoContinue: Boolean = true
)
