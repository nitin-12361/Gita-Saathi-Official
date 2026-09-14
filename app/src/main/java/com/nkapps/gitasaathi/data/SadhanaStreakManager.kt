package com.nkapps.gitasaathi.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class SpiritualBadge(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val emoji: String,
    val requiredStreakDays: Int,
    val descriptionHindi: String,
    val descriptionEnglish: String
)

data class StreakData(
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val totalDaysActive: Int = 0,
    val isReadToday: Boolean = false,
    val unlockedBadges: List<SpiritualBadge> = emptyList(),
    val nextBadge: SpiritualBadge? = null
)

object SpiritualBadges {
    val ALL_BADGES = listOf(
        SpiritualBadge(
            id = "first_step",
            titleHindi = "प्रथम स्पर्श",
            titleEnglish = "First Step",
            emoji = "🌱",
            requiredStreakDays = 1,
            descriptionHindi = "गीता की पावन यात्रा का प्रथम दिवस",
            descriptionEnglish = "First day of the holy Gita journey"
        ),
        SpiritualBadge(
            id = "arjuna_focus",
            titleHindi = "अर्जुन एकाग्रता",
            titleEnglish = "Arjuna's Focus",
            emoji = "🏹",
            requiredStreakDays = 3,
            descriptionHindi = "लगातार 3 दिन का निरंतर पठन",
            descriptionEnglish = "3 consecutive days of divine reading"
        ),
        SpiritualBadge(
            id = "karma_yogi",
            titleHindi = "कर्म योगी",
            titleEnglish = "Karma Yogi",
            emoji = "⚖️",
            requiredStreakDays = 7,
            descriptionHindi = "एक सप्ताह की पूर्ण निष्काम साधना",
            descriptionEnglish = "1 full week of steady spiritual sadhana"
        ),
        SpiritualBadge(
            id = "dhyana_sadhak",
            titleHindi = "ध्यान साधक",
            titleEnglish = "Dhyana Sadhak",
            emoji = "🧘‍♂️",
            requiredStreakDays = 14,
            descriptionHindi = "2 सप्ताह की निरंतर चित्त शुद्धि",
            descriptionEnglish = "2 weeks of daily mindful contemplation"
        ),
        SpiritualBadge(
            id = "gita_jnani",
            titleHindi = "गीता ज्ञानी",
            titleEnglish = "Gita Jnani",
            emoji = "🕉️",
            requiredStreakDays = 30,
            descriptionHindi = "1 माह की अखंड आध्यात्मिक तपस्या",
            descriptionEnglish = "1 month of unbroken spiritual devotion"
        ),
        SpiritualBadge(
            id = "siddha_yogi",
            titleHindi = "सिद्ध योगी",
            titleEnglish = "Siddha Yogi",
            emoji = "👑",
            requiredStreakDays = 108,
            descriptionHindi = "108 दिनों का पावन दिव्य संकल्प",
            descriptionEnglish = "108 days of supreme spiritual mastery"
        )
    )
}

class SadhanaStreakManager private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gita_sadhana_streak_prefs", Context.MODE_PRIVATE)

    private val _streakData = MutableStateFlow(loadStreak())
    val streakData: StateFlow<StreakData> = _streakData.asStateFlow()

    private fun getCurrentEpochDay(): Long {
        val cal = Calendar.getInstance()
        val localMillis = cal.timeInMillis + cal.timeZone.getOffset(cal.timeInMillis)
        return TimeUnit.MILLISECONDS.toDays(localMillis)
    }

    private fun loadStreak(): StreakData {
        val currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0)
        val longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0)
        val totalActive = prefs.getInt(KEY_TOTAL_ACTIVE_DAYS, 0)
        val lastDay = prefs.getLong(KEY_LAST_ACTIVE_DAY, 0L)
        val today = getCurrentEpochDay()

        val isReadToday = (lastDay == today)
        val effectiveCurrentStreak = when {
            lastDay == 0L -> 0
            lastDay == today -> currentStreak
            lastDay == today - 1 -> currentStreak
            else -> 0 // Missed more than 1 day
        }

        val unlocked = SpiritualBadges.ALL_BADGES.filter { it.requiredStreakDays <= effectiveCurrentStreak }
        val next = SpiritualBadges.ALL_BADGES.firstOrNull { it.requiredStreakDays > effectiveCurrentStreak }

        return StreakData(
            currentStreakDays = effectiveCurrentStreak,
            longestStreakDays = longestStreak,
            totalDaysActive = totalActive,
            isReadToday = isReadToday,
            unlockedBadges = unlocked,
            nextBadge = next
        )
    }

    fun recordDailyActivity(): StreakData {
        val today = getCurrentEpochDay()
        val lastDay = prefs.getLong(KEY_LAST_ACTIVE_DAY, 0L)
        var currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0)
        var longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0)
        var totalActive = prefs.getInt(KEY_TOTAL_ACTIVE_DAYS, 0)

        if (lastDay == today) {
            // Already recorded today
            return _streakData.value
        }

        if (lastDay == today - 1) {
            // Consecutive day
            currentStreak += 1
        } else {
            // First day or streak broken
            currentStreak = 1
        }

        totalActive += 1
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak
        }

        prefs.edit()
            .putLong(KEY_LAST_ACTIVE_DAY, today)
            .putInt(KEY_CURRENT_STREAK, currentStreak)
            .putInt(KEY_LONGEST_STREAK, longestStreak)
            .putInt(KEY_TOTAL_ACTIVE_DAYS, totalActive)
            .apply()

        val unlocked = SpiritualBadges.ALL_BADGES.filter { it.requiredStreakDays <= currentStreak }
        val next = SpiritualBadges.ALL_BADGES.firstOrNull { it.requiredStreakDays > currentStreak }

        val updated = StreakData(
            currentStreakDays = currentStreak,
            longestStreakDays = longestStreak,
            totalDaysActive = totalActive,
            isReadToday = true,
            unlockedBadges = unlocked,
            nextBadge = next
        )

        _streakData.value = updated
        return updated
    }

    companion object {
        private const val KEY_CURRENT_STREAK = "key_current_streak"
        private const val KEY_LONGEST_STREAK = "key_longest_streak"
        private const val KEY_LAST_ACTIVE_DAY = "key_last_active_day"
        private const val KEY_TOTAL_ACTIVE_DAYS = "key_total_active_days"

        @Volatile
        private var INSTANCE: SadhanaStreakManager? = null

        fun getInstance(context: Context): SadhanaStreakManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SadhanaStreakManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
