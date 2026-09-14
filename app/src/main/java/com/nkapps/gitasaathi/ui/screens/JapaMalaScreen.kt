package com.nkapps.gitasaathi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.JapaMalaManager
import com.nkapps.gitasaathi.data.JapaMalaState
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JapaMalaScreen(
    state: JapaMalaState,
    appLanguage: AppLanguage,
    onIncrementBead: () -> Unit,
    onResetMala: () -> Unit,
    onSelectMantra: (Int) -> Unit,
    onSetTargetMalas: (Int) -> Unit,
    onToggleHaptic: () -> Unit,
    onToggleSound: () -> Unit,
    onDismissCelebration: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = appLanguage == AppLanguage.HINDI
    val mantras = JapaMalaManager.MANTRAS
    val currentMantra = mantras[state.selectedMantraIndex]

    val view = LocalView.current
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val tapScale = remember { Animatable(1f) }

    val progress = (state.currentBead.toFloat() / 108f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200),
        label = "mala_progress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8EE),
                        Color(0xFFFFF1DC),
                        Color(0xFFFFE8CC)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "📿 डिजिटल जप माला" else "📿 Digital Japa Mala",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF431407)
                        )
                        Text(
                            text = if (isHindi) "१०८ मणके ध्यान साधना" else "108 Beads Chanting Sadhana",
                            fontSize = 11.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("japa_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF431407)
                        )
                    }
                },
                actions = {
                    // Sound Toggle
                    IconButton(onClick = onToggleSound, modifier = Modifier.testTag("japa_toggle_sound")) {
                        Icon(
                            imageVector = if (state.isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Sound",
                            tint = if (state.isSoundEnabled) Color(0xFFD97706) else Color(0xFF9CA3AF)
                        )
                    }

                    // Haptic Toggle
                    IconButton(onClick = onToggleHaptic, modifier = Modifier.testTag("japa_toggle_haptic")) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Vibration",
                            tint = if (state.isHapticEnabled) Color(0xFFD97706) else Color(0xFF9CA3AF)
                        )
                    }

                    // Reset Current Mala
                    IconButton(onClick = { showResetConfirmDialog = true }, modifier = Modifier.testTag("japa_reset_button")) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = Color(0xFF9A3412)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 1. MANTRA SELECTOR CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("japa_mantra_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mantra Title Switcher Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val prev = (state.selectedMantraIndex - 1 + mantras.size) % mantras.size
                                onSelectMantra(prev)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Mantra",
                                tint = Color(0xFFD97706)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = if (isHindi) currentMantra.titleHindi else currentMantra.titleEnglish,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val next = (state.selectedMantraIndex + 1) % mantras.size
                                onSelectMantra(next)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Mantra",
                                tint = Color(0xFFD97706)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sacred Mantra Text
                    Text(
                        text = if (isHindi) currentMantra.mantraHindi else currentMantra.mantraEnglish,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        color = Color(0xFF78350F),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isHindi) currentMantra.benefitHindi else currentMantra.benefitEnglish,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF92400E).copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. THE 108 BEADS SACRED INTERACTIVE DISC
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(290.dp)
                    .testTag("japa_interactive_mala_disc")
            ) {
                // Background Circular Bead Track & Progress Arc
                Canvas(modifier = Modifier.size(280.dp)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.width / 2f - 22f

                    // 1. Outer Track Ring
                    drawCircle(
                        color = Color(0xFFFDE68A).copy(alpha = 0.5f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 16f)
                    )

                    // 2. Active Progress Arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFF59E0B),
                                Color(0xFFEA580C),
                                Color(0xFFD97706),
                                Color(0xFFF59E0B)
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                        style = Stroke(width = 16f, cap = StrokeCap.Round)
                    )

                    // 3. Draw 27 Representative Mala Beads around the perimeter
                    val totalDisplayBeads = 27
                    for (i in 0 until totalDisplayBeads) {
                        val angle = Math.toRadians((i * (360.0 / totalDisplayBeads) - 90.0))
                        val x = center.x + radius * cos(angle).toFloat()
                        val y = center.y + radius * sin(angle).toFloat()
                        val beadIndexThreshold = (i + 1) * (108 / totalDisplayBeads)
                        val isChanted = state.currentBead >= beadIndexThreshold

                        drawCircle(
                            color = if (isChanted) Color(0xFFB45309) else Color(0xFFFCD34D),
                            radius = if (isChanted) 9f else 7f,
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color(0xFF78350F),
                            radius = if (isChanted) 9f else 7f,
                            center = Offset(x, y),
                            style = Stroke(width = 2f)
                        )
                    }
                }

                // Central Large Tappable Chanting Button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(3.dp, Color(0xFFF59E0B)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(195.dp)
                        .scale(tapScale.value)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (state.isHapticEnabled) {
                                try {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                } catch (_: Exception) {}
                            }
                            scope.launch {
                                tapScale.animateTo(0.93f, tween(50))
                                tapScale.animateTo(1f, tween(80))
                            }
                            onIncrementBead()
                        }
                        .testTag("japa_tap_target")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFFBEB),
                                        Color(0xFFFEF3C7)
                                    )
                                )
                            )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🕉️",
                                fontSize = 26.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${state.currentBead}",
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78350F)
                            )

                            Text(
                                text = if (isHindi) "मणके / १०८" else "Beads / 108",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFD97706).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isHindi) "👉 स्पर्श करें" else "👉 Tap to Chant",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF92400E),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. DAILY SADHANA & LIFETIME STATS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("japa_stats_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isHindi) "📊 दैनिक साधना प्रगति (Daily Sadhana)" else "📊 Daily Chanting Progress",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF431407)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Metric 1: Today's Completed Malas
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.todayCompletedMalas} / ${state.targetMalas}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD97706)
                            )
                            Text(
                                text = if (isHindi) "आज की माला" else "Today's Malas",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F)
                            )
                        }

                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(38.dp)
                                .background(Color(0xFFFED7AA))
                        )

                        // Metric 2: Lifetime Total Chants
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.lifetimeTotalChants}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF059669)
                            )
                            Text(
                                text = if (isHindi) "कुल मंत्र जप" else "Total Chants",
                                fontSize = 11.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily Target Selector
                    Text(
                        text = if (isHindi) "दैनिक माला संकल्प (Daily Target):" else "Daily Mala Target:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 4, 16).forEach { target ->
                            val isSelected = state.targetMalas == target
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFD97706) else Color(0xFFFFFBEB),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFFB45309) else Color(0xFFFDE68A)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSetTargetMalas(target) }
                            ) {
                                Text(
                                    text = "$target ${if (isHindi) "माला" else "Mala"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF92400E),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // 4. MALA COMPLETION CELEBRATION DIALOG
        if (state.justCompletedMala) {
            AlertDialog(
                onDismissRequest = onDismissCelebration,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎉", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "जय श्रीकृष्ण! १ माला पूर्ण" else "Mala Completed! Haribol!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = if (isHindi)
                                "आपने १०८ मणकों का पावन जप सफलतापूर्वक पूर्ण कर लिया है! आज आपकी कुल ${state.todayCompletedMalas} मालाएं पूर्ण हो चुकी हैं।"
                            else
                                "You have successfully completed 108 sacred beads! You have chanted ${state.todayCompletedMalas} mala(s) today.",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF431407)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "॥ हरे कृष्ण हरे राम ॥ प्रभु का आशीर्वाद आप पर सदा बना रहे।",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onDismissCelebration,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isHindi) "अगली माला प्रारम्भ करें" else "Start Next Mala",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // 5. RESET CONFIRMATION DIALOG
        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                title = {
                    Text(
                        text = if (isHindi) "मणके रीसेट करें?" else "Reset Current Mala?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = if (isHindi)
                            "क्या आप वर्तमान माला की गिनती को वापस ० से शुरू करना चाहते हैं? (आपकी आज की पूर्ण मालाएं सुरक्षित रहेंगी)"
                        else
                            "Do you want to reset current bead count to 0? (Your completed malas remain saved)",
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetMala()
                            showResetConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = if (isHindi) "हाँ, रीसेट करें" else "Yes, Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmDialog = false }) {
                        Text(text = if (isHindi) "रद्द करें" else "Cancel")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}
