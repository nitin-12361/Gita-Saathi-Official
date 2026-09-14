package com.nkapps.gitasaathi.data

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class JapaMantra(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val mantraHindi: String,
    val mantraEnglish: String,
    val benefitHindi: String,
    val benefitEnglish: String
)

data class JapaMalaState(
    val currentBead: Int = 0,
    val todayCompletedMalas: Int = 0,
    val lifetimeTotalChants: Long = 0L,
    val targetMalas: Int = 1,
    val selectedMantraIndex: Int = 0,
    val isHapticEnabled: Boolean = true,
    val isSoundEnabled: Boolean = true,
    val justCompletedMala: Boolean = false
)

class JapaMalaManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("japa_mala_preferences", Context.MODE_PRIVATE)

    companion object {
        val MANTRAS = listOf(
            JapaMantra(
                id = "hare_krishna",
                titleHindi = "हरे कृष्ण महामंत्र",
                titleEnglish = "Hare Krishna Mahamantra",
                mantraHindi = "हरे कृष्ण हरे कृष्ण कृष्ण कृष्ण हरे हरे ।\nहरे राम हरे राम राम राम हरे हरे ॥",
                mantraEnglish = "Hare Krishna Hare Krishna Krishna Krishna Hare Hare |\nHare Rama Hare Rama Rama Rama Hare Hare ||",
                benefitHindi = "कलियुग में चित्त शुद्धि एवं परम शांति का सर्वोच्च साधन।",
                benefitEnglish = "The supreme prayer for inner purification and transcendental peace."
            ),
            JapaMantra(
                id = "om_namo_bhagavate",
                titleHindi = "द्वादशाक्षर वासुदेव मन्त्र",
                titleEnglish = "Om Namo Bhagavate Vasudevaya",
                mantraHindi = "ॐ नमो भगवते वासुदेवाय",
                mantraEnglish = "Om Namo Bhagavate Vasudevaya",
                benefitHindi = "श्रीमद्भागवत का मूल मन्त्र; सभी प्रकार के भयों से मुक्तिदाता।",
                benefitEnglish = "Root mantra of Srimad Bhagavatam; liberates from all fears."
            ),
            JapaMantra(
                id = "shri_krishna_sharanam",
                titleHindi = "श्रीकृष्ण शरण मन्त्र",
                titleEnglish = "Sri Krishna Sharanam Mama",
                mantraHindi = "श्रीकृष्णः शरणं मम",
                mantraEnglish = "Sri Krishnah Sharanam Mama",
                benefitHindi = "भगवान श्रीकृष्ण के चरणों में पूर्ण शरणागति और रक्षा का भाव।",
                benefitEnglish = "Unconditional surrender and divine shelter at Lord Krishna's lotus feet."
            ),
            JapaMantra(
                id = "om_namah_shivaya",
                titleHindi = "पंचाक्षर शिव मन्त्र",
                titleEnglish = "Om Namah Shivaya",
                mantraHindi = "ॐ नमः शिवाय",
                mantraEnglish = "Om Namah Shivaya",
                benefitHindi = "आत्म-संयम, ध्यान एवं अंतर्मन की गहन स्थिरता हेतु।",
                benefitEnglish = "For self-mastery, meditation, and profound inner stillness."
            ),
            JapaMantra(
                id = "gayatri_mantra",
                titleHindi = "महा गायत्री मन्त्र",
                titleEnglish = "Gayatri Mantra",
                mantraHindi = "ॐ भूर्भुवः स्वः तत्सवितुर्वरेण्यं भर्गो देवस्य धीमहि धियो यो नः प्रचोदयात् ॥",
                mantraEnglish = "Om Bhur Bhuvah Svah Tat Savitur Varenyam Bhargo Devasya Dheemahi Dhiyo Yo Nah Prachodayat ||",
                benefitHindi = "बुद्धि को दिव्य प्रकाश और सकारात्मक ऊर्जा से ओतप्रोत करने वाला मन्त्र।",
                benefitEnglish = "Illuminates intellect with divine radiance and discernment."
            )
        )

        private const val KEY_CURRENT_BEAD = "key_current_bead"
        private const val KEY_TODAY_MALAS = "key_today_malas"
        private const val KEY_LIFETIME_CHANTS = "key_lifetime_chants"
        private const val KEY_LAST_DATE = "key_last_date"
        private const val KEY_TARGET_MALAS = "key_target_malas"
        private const val KEY_SELECTED_MANTRA = "key_selected_mantra"
        private const val KEY_HAPTIC_ENABLED = "key_haptic_enabled"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
    }

    private val _state = MutableStateFlow(loadInitialState())
    val state: StateFlow<JapaMalaState> = _state.asStateFlow()

    private var toneGenerator: ToneGenerator? = null

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun loadInitialState(): JapaMalaState {
        val today = getTodayDateString()
        val savedDate = prefs.getString(KEY_LAST_DATE, "")
        val todayMalas = if (today == savedDate) {
            prefs.getInt(KEY_TODAY_MALAS, 0)
        } else {
            // New day: reset daily completed count
            prefs.edit {
                putInt(KEY_TODAY_MALAS, 0)
                putString(KEY_LAST_DATE, today)
            }
            0
        }

        return JapaMalaState(
            currentBead = prefs.getInt(KEY_CURRENT_BEAD, 0),
            todayCompletedMalas = todayMalas,
            lifetimeTotalChants = prefs.getLong(KEY_LIFETIME_CHANTS, 0L),
            targetMalas = prefs.getInt(KEY_TARGET_MALAS, 1),
            selectedMantraIndex = prefs.getInt(KEY_SELECTED_MANTRA, 0).coerceIn(0, MANTRAS.size - 1),
            isHapticEnabled = prefs.getBoolean(KEY_HAPTIC_ENABLED, true),
            isSoundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true),
            justCompletedMala = false
        )
    }

    /**
     * Increments the bead count by 1.
     * Triggers tactile haptics and sound.
     * When bead reaches 108: completes 1 Mala, updates stats, and returns true.
     */
    fun incrementBead(): Boolean {
        val current = _state.value
        val nextBead = current.currentBead + 1
        val newLifetime = current.lifetimeTotalChants + 1

        val vibrator = getVibrator()

        if (nextBead >= 108) {
            // 108 Beads Reached: 1 Mala Completed!
            val newTodayMalas = current.todayCompletedMalas + 1
            prefs.edit {
                putInt(KEY_CURRENT_BEAD, 0)
                putInt(KEY_TODAY_MALAS, newTodayMalas)
                putLong(KEY_LIFETIME_CHANTS, newLifetime)
                putString(KEY_LAST_DATE, getTodayDateString())
            }

            _state.value = current.copy(
                currentBead = 0,
                todayCompletedMalas = newTodayMalas,
                lifetimeTotalChants = newLifetime,
                justCompletedMala = true
            )

            // Celebration Haptic & Bell Chime
            if (current.isHapticEnabled) {
                triggerMalaCompletionHaptic(vibrator)
            }
            if (current.isSoundEnabled) {
                playBellSound()
            }
            return true
        } else {
            // Normal Bead Increment
            prefs.edit {
                putInt(KEY_CURRENT_BEAD, nextBead)
                putLong(KEY_LIFETIME_CHANTS, newLifetime)
            }

            _state.value = current.copy(
                currentBead = nextBead,
                lifetimeTotalChants = newLifetime,
                justCompletedMala = false
            )

            // Single Bead Haptic & Click Sound
            if (current.isHapticEnabled) {
                triggerBeadHaptic(vibrator)
            }
            if (current.isSoundEnabled) {
                playClickSound()
            }
            return false
        }
    }

    fun dismissCompletionCelebration() {
        _state.value = _state.value.copy(justCompletedMala = false)
    }

    fun resetCurrentMala() {
        prefs.edit { putInt(KEY_CURRENT_BEAD, 0) }
        _state.value = _state.value.copy(currentBead = 0, justCompletedMala = false)
    }

    fun selectMantra(index: Int) {
        val validIndex = index.coerceIn(0, MANTRAS.size - 1)
        prefs.edit { putInt(KEY_SELECTED_MANTRA, validIndex) }
        _state.value = _state.value.copy(selectedMantraIndex = validIndex)
    }

    fun setTargetMalas(target: Int) {
        val validTarget = target.coerceAtLeast(1)
        prefs.edit { putInt(KEY_TARGET_MALAS, validTarget) }
        _state.value = _state.value.copy(targetMalas = validTarget)
    }

    fun toggleHaptic() {
        val newHaptic = !_state.value.isHapticEnabled
        prefs.edit { putBoolean(KEY_HAPTIC_ENABLED, newHaptic) }
        _state.value = _state.value.copy(isHapticEnabled = newHaptic)
        if (newHaptic) {
            triggerBeadHaptic(getVibrator())
        }
    }

    fun toggleSound() {
        val newSound = !_state.value.isSoundEnabled
        prefs.edit { putBoolean(KEY_SOUND_ENABLED, newSound) }
        _state.value = _state.value.copy(isSoundEnabled = newSound)
        if (newSound) {
            playClickSound()
        }
    }

    private fun getVibrator(): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun triggerBeadHaptic(vibrator: Vibrator?) {
        try {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(55L, VibrationEffect.DEFAULT_AMPLITUDE)
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .build()
                    vibrator.vibrate(effect, audioAttributes)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(55L)
                }
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(55L)
            } catch (_: Exception) {}
        }
    }

    private fun triggerMalaCompletionHaptic(vibrator: Vibrator?) {
        try {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(
                        longArrayOf(0, 120, 80, 180, 80, 250),
                        -1
                    )
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .build()
                    vibrator.vibrate(effect, audioAttributes)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 120, 80, 180, 80, 250), -1)
                }
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 120, 80, 180, 80, 250), -1)
            } catch (_: Exception) {}
        }
    }

    @Synchronized
    private fun getOrCreateToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
            } catch (_: Exception) {}
        }
        return toneGenerator
    }

    private fun playClickSound() {
        try {
            getOrCreateToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (_: Exception) {}
    }

    private fun playBellSound() {
        try {
            getOrCreateToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
        } catch (_: Exception) {}
    }

    @Synchronized
    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (_: Exception) {}
    }
}
