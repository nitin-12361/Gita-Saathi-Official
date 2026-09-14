package com.nkapps.gitasaathi.ui.components

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import com.nkapps.gitasaathi.ads.GitaAdManager
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.DailyQuizState
import com.nkapps.gitasaathi.data.GitaQuizData
import com.nkapps.gitasaathi.data.KarmaBadge
import com.nkapps.gitasaathi.data.QuizQuestion
import com.nkapps.gitasaathi.data.SubmitQuizResult

@Composable
fun GitaQuizDialog(
    quizState: DailyQuizState,
    appLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSubmitQuiz: (Int) -> SubmitQuizResult
) {
    val isHindi = (appLanguage == AppLanguage.HINDI)
    val context = LocalContext.current
    val questions = quizState.currentQuestions

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var correctAnswersCount by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(quizState.isCompletedToday) }
    var submitResult by remember { mutableStateOf<SubmitQuizResult?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("gita_quiz_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFFFD8A8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF7ED),
                            border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🎯", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "दैनिक गीता प्रश्नोत्तरी" else "Daily Gita Quiz",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isHindi) "ज्ञान साधना एवं पुण्य अंक" else "Sacred Wisdom & Karma Points",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_quiz_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isQuizFinished) {
                    // QUIZ RESULT / CELEBRATION VIEW
                    val score = if (quizState.isCompletedToday) quizState.todayScore else correctAnswersCount
                    val earnedPoints = submitResult?.earnedPoints ?: quizState.todayEarnedPoints
                    val streak = submitResult?.newStreak ?: quizState.quizStreak
                    val totalPoints = submitResult?.newTotalPoints ?: quizState.totalKarmaPoints
                    val newBadges = submitResult?.newBadges ?: emptyList()

                    QuizResultView(
                        score = score,
                        totalQuestions = questions.size,
                        earnedPoints = earnedPoints,
                        totalPoints = totalPoints,
                        streak = streak,
                        newBadges = newBadges,
                        isHindi = isHindi,
                        onShare = {
                            val shareText = if (isHindi) {
                                "🎯 मैंने आज 'गीता साथी' दैनिक प्रश्नोत्तरी में $score/${questions.size} सही उत्तर देकर +$earnedPoints पुण्य अंक अर्जित किए! 🔥 $streak दिन की साधना। आप भी जुड़ें!"
                            } else {
                                "🎯 I scored $score/${questions.size} in 'Gita Saathi' Daily Quiz and earned +$earnedPoints Karma Points! 🔥 $streak-day streak. Test your Gita wisdom too!"
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Quiz Score"))
                        },
                        onClose = onDismiss
                    )
                } else {
                    // ACTIVE QUIZ QUESTIONS VIEW
                    if (questions.isNotEmpty() && currentIndex in questions.indices) {
                        val currentQ = questions[currentIndex]

                        // Progress Indicator & Live Running Score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "प्रश्न ${currentIndex + 1} / ${questions.size}" else "Question ${currentIndex + 1} of ${questions.size}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDCFCE7),
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                                ) {
                                    Text(
                                        text = if (isHindi) "स्कोर: $correctAnswersCount / ${questions.size} ⭐" else "Score: $correctAnswersCount / ${questions.size} ⭐",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF7ED),
                                border = BorderStroke(1.dp, Color(0xFFFFD8A8))
                            ) {
                                Text(
                                    text = if (isHindi) currentQ.categoryHindi else currentQ.categoryEnglish,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC05621),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFF7A00),
                            trackColor = Color(0xFFFFE8D6)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Question Box
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFFBF5),
                            border = BorderStroke(1.dp, Color(0xFFFFE4C4)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isHindi) currentQ.questionHindi else currentQ.questionEnglish,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                lineHeight = 24.sp,
                                color = Color(0xFF2D3748),
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Options
                        val options = if (isHindi) currentQ.optionsHindi else currentQ.optionsEnglish
                        options.forEachIndexed { optIndex, optionText ->
                            val isSelected = (selectedOptionIndex == optIndex)
                            val isAnswered = (selectedOptionIndex != null)
                            val isCorrectOption = (optIndex == currentQ.correctOptionIndex)

                            val containerColor = when {
                                !isAnswered -> Color(0xFFFFFFFF)
                                isCorrectOption -> Color(0xFFE6F4EA) // Green
                                isSelected && !isCorrectOption -> Color(0xFFFCE8E6) // Red
                                else -> Color(0xFFFFFFFF)
                            }

                            val borderColor = when {
                                !isAnswered -> if (isSelected) Color(0xFFFF7A00) else Color(0xFFE2E8F0)
                                isCorrectOption -> Color(0xFF10B981)
                                isSelected && !isCorrectOption -> Color(0xFFEF4444)
                                else -> Color(0xFFE2E8F0)
                            }

                            val textColor = when {
                                !isAnswered -> Color(0xFF2D3748)
                                isCorrectOption -> Color(0xFF065F46)
                                isSelected && !isCorrectOption -> Color(0xFF991B1B)
                                else -> Color(0xFF718096)
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = containerColor,
                                border = BorderStroke(if (isAnswered && (isCorrectOption || isSelected)) 1.5.dp else 1.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(enabled = !isAnswered) {
                                        selectedOptionIndex = optIndex
                                        if (optIndex == currentQ.correctOptionIndex) {
                                            correctAnswersCount++
                                        }
                                    }
                                    .testTag("quiz_option_")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = when {
                                                !isAnswered -> Color(0xFFF7FAFC)
                                                isCorrectOption -> Color(0xFF10B981)
                                                isSelected -> Color(0xFFEF4444)
                                                else -> Color(0xFFEDF2F7)
                                            },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = ('A' + optIndex).toString(),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAnswered && (isCorrectOption || isSelected)) Color.White else Color(0xFF4A5568)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = optionText,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected || (isAnswered && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
                                            color = textColor
                                        )
                                    }

                                    if (isAnswered) {
                                        if (isCorrectOption) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Correct",
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Incorrect",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Explanation Box (Appears after answer)
                        AnimatedVisibility(
                            visible = (selectedOptionIndex != null),
                            enter = fadeIn() + scaleIn()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFFFFBEB),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "💡", fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isHindi) "गीता संदर्भ एवं रहस्य" else "Sacred Context & Meaning",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB45309)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isHindi) currentQ.explanationHindi else currentQ.explanationEnglish,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = Color(0xFF78350F)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (currentIndex < questions.size - 1) {
                                            currentIndex++
                                            selectedOptionIndex = null
                                        } else {
                                            // Finish Quiz: Show Interstitial Ad before revealing score card (0 ads for Gold)
                                            val activity = context.findActivity()
                                            if (activity != null) {
                                                GitaAdManager.showInterstitial(activity) {
                                                    val res = onSubmitQuiz(correctAnswersCount)
                                                    submitResult = res
                                                    isQuizFinished = true
                                                }
                                            } else {
                                                val res = onSubmitQuiz(correctAnswersCount)
                                                submitResult = res
                                                isQuizFinished = true
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF7A00),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("quiz_next_button")
                                ) {
                                    Text(
                                        text = if (currentIndex < questions.size - 1) {
                                            if (isHindi) "अगला प्रश्न ▶️" else "Next Question ▶️"
                                        } else {
                                            if (isHindi) "परिणाम देखें 🏆" else "View Results 🏆"
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizResultView(
    score: Int,
    totalQuestions: Int,
    earnedPoints: Int,
    totalPoints: Int,
    streak: Int,
    newBadges: List<KarmaBadge>,
    isHindi: Boolean,
    onShare: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (score == totalQuestions) "🎉 सम्पूर्ण ज्ञान साधना! 🎉" else "✨ साधना पूर्ण हुई! ✨",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC05621),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Score Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFFF7ED),
            border = BorderStroke(1.dp, Color(0xFFFFD8A8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$score / $totalQuestions",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFDD6B20)
                )
                Text(
                    text = if (isHindi) "सही उत्तर" else "Correct Answers",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C4221)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "+$earnedPoints 🌟",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                        Text(
                            text = if (isHindi) "पुण्य अंक मिले" else "Karma Points",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🔥 $streak",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                        Text(
                            text = if (isHindi) "दिन की स्ट्रीक" else "Day Streak",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalPoints 👑",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                        Text(
                            text = if (isHindi) "कुल पुण्य अंक" else "Total Points",
                            fontSize = 11.sp,
                            color = Color(0xFF065F46)
                        )
                    }
                }
            }
        }

        // New Badge Celebration
        if (newBadges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFECFDF5),
                border = BorderStroke(1.dp, Color(0xFF6EE7B7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎖️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "नया दिव्य बैज अनलॉक हुआ!" else "New Divine Badge Unlocked!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF047857)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    newBadges.forEach { badge ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(text = badge.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isHindi) badge.titleHindi else badge.titleEnglish,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = if (isHindi) badge.descriptionHindi else badge.descriptionEnglish,
                                    fontSize = 11.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onShare,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDD6B20)),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("quiz_share_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "शेयर करें" else "Share Score",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onClose,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7A00),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("quiz_done_button")
            ) {
                Text(
                    text = if (isHindi) "पूर्ण (समाप्त)" else "Done",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun android.content.Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}
