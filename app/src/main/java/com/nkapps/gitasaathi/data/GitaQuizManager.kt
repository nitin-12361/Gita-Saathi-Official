package com.nkapps.gitasaathi.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyQuizState(
    val dateKey: String,
    val isCompletedToday: Boolean,
    val todayScore: Int,
    val todayEarnedPoints: Int,
    val totalKarmaPoints: Int,
    val quizStreak: Int,
    val unlockedBadgeIds: Set<String>,
    val currentQuestions: List<QuizQuestion>
)

data class SubmitQuizResult(
    val earnedPoints: Int,
    val newTotalPoints: Int,
    val newStreak: Int,
    val newBadges: List<KarmaBadge>
)

class GitaQuizManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gita_quiz_prefs", Context.MODE_PRIVATE)

    private val _quizState = MutableStateFlow(loadQuizState())
    val quizState: StateFlow<DailyQuizState> = _quizState.asStateFlow()

    private fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private fun getYesterdayDateKey(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }

    fun loadQuizState(): DailyQuizState {
        val todayKey = getTodayDateKey()
        val lastCompletedDate = prefs.getString(KEY_LAST_COMPLETED_DATE, "") ?: ""
        val isCompletedToday = (lastCompletedDate == todayKey)
        val todayScore = if (isCompletedToday) prefs.getInt(KEY_TODAY_SCORE, 0) else 0
        val todayEarnedPoints = if (isCompletedToday) prefs.getInt(KEY_TODAY_EARNED_POINTS, 0) else 0
        val totalKarmaPoints = prefs.getInt(KEY_TOTAL_KARMA_POINTS, 0)
        val quizStreak = prefs.getInt(KEY_QUIZ_STREAK, 0)
        val badgesSet = prefs.getStringSet(KEY_UNLOCKED_BADGES, emptySet()) ?: emptySet()

        val cal = Calendar.getInstance()
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val questions = GitaQuizData.getDailyQuestions(dayOfYear)

        return DailyQuizState(
            dateKey = todayKey,
            isCompletedToday = isCompletedToday,
            todayScore = todayScore,
            todayEarnedPoints = todayEarnedPoints,
            totalKarmaPoints = totalKarmaPoints,
            quizStreak = quizStreak,
            unlockedBadgeIds = badgesSet,
            currentQuestions = questions
        )
    }

    fun submitQuizResult(score: Int, totalQuestions: Int = 5): SubmitQuizResult {
        val todayKey = getTodayDateKey()
        val yesterdayKey = getYesterdayDateKey()
        val lastCompletedDate = prefs.getString(KEY_LAST_COMPLETED_DATE, "") ?: ""
        val currentStreak = prefs.getInt(KEY_QUIZ_STREAK, 0)
        val currentTotalPoints = prefs.getInt(KEY_TOTAL_KARMA_POINTS, 0)
        val currentBadges = (prefs.getStringSet(KEY_UNLOCKED_BADGES, emptySet()) ?: emptySet()).toMutableSet()

        // 10 points per correct question + 10 bonus points for 5/5 perfect score!
        val basePoints = (score.coerceIn(0, totalQuestions)) * 10
        val bonus = if (score == totalQuestions && totalQuestions > 0) 10 else 0
        val earnedPoints = basePoints + bonus
        val newTotalPoints = currentTotalPoints + earnedPoints

        // Calculate Streak
        val newStreak = when (lastCompletedDate) {
            todayKey -> currentStreak // already answered today
            yesterdayKey -> currentStreak + 1 // continued streak
            else -> 1 // fresh streak start
        }

        // Check new badges
        val newlyUnlockedBadges = mutableListOf<KarmaBadge>()
        for (badge in GitaQuizData.BADGES) {
            if (!currentBadges.contains(badge.id) && newTotalPoints >= badge.requiredPoints) {
                currentBadges.add(badge.id)
                newlyUnlockedBadges.add(badge)
            }
        }

        // Persist to SharedPreferences
        prefs.edit()
            .putString(KEY_LAST_COMPLETED_DATE, todayKey)
            .putInt(KEY_TODAY_SCORE, score)
            .putInt(KEY_TODAY_EARNED_POINTS, earnedPoints)
            .putInt(KEY_TOTAL_KARMA_POINTS, newTotalPoints)
            .putInt(KEY_QUIZ_STREAK, newStreak)
            .putStringSet(KEY_UNLOCKED_BADGES, currentBadges)
            .apply()

        // Update StateFlow
        _quizState.value = loadQuizState()

        return SubmitQuizResult(
            earnedPoints = earnedPoints,
            newTotalPoints = newTotalPoints,
            newStreak = newStreak,
            newBadges = newlyUnlockedBadges
        )
    }

    fun refreshState() {
        _quizState.value = loadQuizState()
    }

    companion object {
        private const val KEY_LAST_COMPLETED_DATE = "key_quiz_last_completed_date"
        private const val KEY_TODAY_SCORE = "key_quiz_today_score"
        private const val KEY_TODAY_EARNED_POINTS = "key_quiz_today_earned_points"
        private const val KEY_TOTAL_KARMA_POINTS = "key_quiz_total_karma_points"
        private const val KEY_QUIZ_STREAK = "key_quiz_streak"
        private const val KEY_UNLOCKED_BADGES = "key_quiz_unlocked_badges"

        @Volatile
        private var INSTANCE: GitaQuizManager? = null

        fun getInstance(context: Context): GitaQuizManager {
            return INSTANCE ?: synchronized(this) {
                val instance = GitaQuizManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
