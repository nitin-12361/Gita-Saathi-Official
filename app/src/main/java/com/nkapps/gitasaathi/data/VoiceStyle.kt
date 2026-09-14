package com.nkapps.gitasaathi.data

enum class VoiceStyle(
    val id: String,
    val titleEnglish: String,
    val titleHindi: String,
    val descriptionEnglish: String,
    val descriptionHindi: String,
    val pitch: Float,
    val defaultSpeed: Float,
    val geminiVoiceName: String,
    val pauseDandaMs: Int,
    val pauseCommaMs: Int,
    val pauseSectionMs: Int
) {
    DEVOTIONAL(
        id = "devotional",
        titleEnglish = "Devotional (Deep)",
        titleHindi = "भक्तिमय (गंभीर)",
        descriptionEnglish = "Deep, resonant & spiritual voice",
        descriptionHindi = "गंभीर और आध्यात्मिक वाणी",
        pitch = 0.9f,
        defaultSpeed = 0.95f,
        geminiVoiceName = "Fenrir",
        pauseDandaMs = 900,
        pauseCommaMs = 450,
        pauseSectionMs = 1200
    ),
    CALM_SAGE(
        id = "sage",
        titleEnglish = "Wise Sage (Calm)",
        titleHindi = "शांत ऋषि (धीर)",
        descriptionEnglish = "Calm, steady and wise narration style",
        descriptionHindi = "शांत, स्थिर और ज्ञानमय वाचन",
        pitch = 1.0f,
        defaultSpeed = 1.0f,
        geminiVoiceName = "Charon",
        pauseDandaMs = 800,
        pauseCommaMs = 400,
        pauseSectionMs = 1000
    ),
    SOOTHING_FEMALE(
        id = "female_soothing",
        titleEnglish = "Soothing Female",
        titleHindi = "मधुर स्त्री स्वर",
        descriptionEnglish = "Soft, melodic and peaceful female voice",
        descriptionHindi = "कोमल, मधुर और शांतिपूर्ण स्वर",
        pitch = 1.1f,
        defaultSpeed = 1.05f,
        geminiVoiceName = "Aoede",
        pauseDandaMs = 850,
        pauseCommaMs = 420,
        pauseSectionMs = 1100
    ),
    DYNAMIC_MENTOR(
        id = "mentor",
        titleEnglish = "Modern Mentor",
        titleHindi = "आधुनिक शिक्षक",
        descriptionEnglish = "Clear, engaging and expressive human voice",
        descriptionHindi = "स्पष्ट, प्रभावशाली और जीवंत वाणी",
        pitch = 1.0f,
        defaultSpeed = 1.1f,
        geminiVoiceName = "Puck",
        pauseDandaMs = 700,
        pauseCommaMs = 350,
        pauseSectionMs = 900
    );

    companion object {
        fun fromId(id: String?): VoiceStyle {
            return entries.find { it.id == id } ?: DEVOTIONAL
        }
    }
}
