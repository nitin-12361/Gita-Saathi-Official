package com.nkapps.gitasaathi.data

/**
 * Represents a life situation or emotional state that users can select to find Gita solutions.
 */
data class GitaMood(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val emoji: String,
    val descriptionHindi: String,
    val descriptionEnglish: String,
    val guidanceHindi: String,
    val guidanceEnglish: String,
    val recommendedVerseKeys: List<Pair<Int, Int>> // (chapterId, verseId)
)

object GitaMoodData {
    val MOODS = listOf(
        GitaMood(
            id = "anxiety_stress",
            titleHindi = "तनाव एवं चिंता",
            titleEnglish = "Anxiety & Stress",
            emoji = "🧘‍♂️",
            descriptionHindi = "मन की अशांति, भविष्य का भय और अत्यधिक सोच से मुक्ति हेतु",
            descriptionEnglish = "Overcoming overthinking, fear of the future, and mental unrest",
            guidanceHindi = "श्री कृष्ण सिखाते हैं कि चंचल मन को अभ्यास और वैराग्य से शांत किया जा सकता है। भविष्य की चिंता छोड़कर वर्तमान क्षण में अपना कर्तव्य निभाएं।",
            guidanceEnglish = "Lord Krishna teaches that a turbulent mind is stilled through steady practice and non-attachment. Focus on your duty in the present moment.",
            recommendedVerseKeys = listOf(
                Pair(6, 5),   // Ch 6.5: Elevate yourself through the mind
                Pair(6, 35),  // Ch 6.35: Mind is mastered through practice
                Pair(2, 14),  // Ch 2.14: Dualities are temporary, endure them
                Pair(2, 47)   // Ch 2.47: Focus on effort, not results
            )
        ),
        GitaMood(
            id = "anger_frustration",
            titleHindi = "क्रोध एवं अशांति",
            titleEnglish = "Anger & Frustration",
            emoji = "🔥",
            descriptionHindi = "क्रोध, असंतोष और उत्तेजना को शांत कर विवेक जागृत करने हेतु",
            descriptionEnglish = "Cooling anger and regaining calm clarity and wisdom",
            guidanceHindi = "क्रोध से सम्मोहन (मूढ़ता) उत्पन्न होता है और बुद्धि नष्ट हो जाती है। जब भी क्रोध आए, तुरंत प्रतिक्रिया देने के बजाय मौन होकर आत्म-निरीक्षण करें।",
            guidanceEnglish = "From anger arises delusion, which destroys intellect and peace. Pause in silence and self-awareness before reacting.",
            recommendedVerseKeys = listOf(
                Pair(2, 62),  // Ch 2.62: From attachment arises desire and anger
                Pair(2, 63),  // Ch 2.63: Anger leads to loss of reason
                Pair(16, 21), // Ch 16.21: Three gates of hell - lust, anger, greed
                Pair(5, 26)   // Ch 5.26: Liberated from desire and anger
            )
        ),
        GitaMood(
            id = "confusion_decision",
            titleHindi = "अनिर्णय एवं असमंजस",
            titleEnglish = "Confusion & Dilemma",
            emoji = "🤔",
            descriptionHindi = "धर्म-संकट, करियर व जीवन के कठिन निर्णयों में सही दिशा पाने हेतु",
            descriptionEnglish = "Finding the right path during moral dilemmas and career choices",
            guidanceHindi = "जब अर्जुन कुरुक्षेत्र में भ्रमित हुए, तो उन्होंने अहंकार छोड़कर श्री कृष्ण के चरणों में आत्म-समर्पण किया। अपनी अंतरात्मा और स्वधर्म को पहचानकर निर्णय लें।",
            guidanceEnglish = "When Arjuna felt paralyzed, he surrendered to divine guidance. Seek clarity through your true inner purpose (Swadharma) and higher duty.",
            recommendedVerseKeys = listOf(
                Pair(2, 7),   // Ch 2.7: Arjuna seeks guidance
                Pair(18, 63), // Ch 18.63: Reflect deeply and act as you choose
                Pair(3, 35),  // Ch 3.35: Better is one's own duty though imperfect
                Pair(4, 18)   // Ch 4.18: Wisdom in action and inaction
            )
        ),
        GitaMood(
            id = "demotivated_failure",
            titleHindi = "निराशा एवं असफलता",
            titleEnglish = "Failure & Demotivation",
            emoji = "🌱",
            descriptionHindi = "विफलता के बाद पुनः उठने और आत्म-विश्वास जगाने हेतु",
            descriptionEnglish = "Rising again after setbacks and rebuilding unshakeable confidence",
            guidanceHindi = "कर्म का अधिकार आपका है, परिणाम का नहीं। असफलता केवल एक पड़ाव है। समत्व भाव से पुनः पूर्ण निष्ठा के साथ कार्य में जुट जाएं।",
            guidanceEnglish = "Setbacks are stepping stones. You have control over your effort, not the external outcome. Rise again with renewed vigor.",
            recommendedVerseKeys = listOf(
                Pair(2, 47),  // Ch 2.47: Karmanye Vadhikaraste
                Pair(2, 38),  // Ch 2.38: Treat joy and sorrow, gain and loss equally
                Pair(18, 78), // Ch 18.78: Where there is Krishna and Arjuna, victory is certain
                Pair(3, 19)   // Ch 3.19: Perform duty without attachment to reach the highest
            )
        ),
        GitaMood(
            id = "fear_grief",
            titleHindi = "शोक एवं भय",
            titleEnglish = "Grief & Fear",
            emoji = "🛡️",
            descriptionHindi = "किसी प्रियजन को खोने, अकेलेपन या मृत्यु के भय से मुक्ति हेतु",
            descriptionEnglish = "Overcoming fear of loss, loneliness, and impermanence",
            guidanceHindi = "आत्मा अमर है—न इसे शस्त्र काट सकते हैं, न अग्नि जला सकती है। परिवर्तन संसार का नियम है, इसलिए मोह और शोक को ज्ञान के प्रकाश से दूर करें।",
            guidanceEnglish = "The soul is eternal and imperishable. Recognize that physical forms change, but the divine consciousness within is ever-present and fearless.",
            recommendedVerseKeys = listOf(
                Pair(2, 20),  // Ch 2.20: Soul is unborn, eternal, and undying
                Pair(2, 22),  // Ch 2.22: Changing clothes like worn garments
                Pair(2, 11),  // Ch 2.11: The wise grieve neither for living nor dead
                Pair(4, 10)   // Ch 4.10: Freed from attachment, fear, and anger
            )
        ),
        GitaMood(
            id = "peace_devotion",
            titleHindi = "शांति एवं भक्ति",
            titleEnglish = "Peace & Devotion",
            emoji = "🕉️",
            descriptionHindi = "परमात्मा से जुड़ने, मन को पावन करने और भक्ति रस में डूबने हेतु",
            descriptionEnglish = "Connecting with the Supreme Divine, inner stillness, and pure love",
            guidanceHindi = "जो भक्त अनन्य भाव से ईश्वर का स्मरण करते हैं, उनके योग-क्षेम का वहन स्वयं परमात्मा करते हैं। संपूर्ण समर्पण से ही परमानंद और परम शांति मिलती है।",
            guidanceEnglish = "Surrendering worries to the Supreme Divine brings profound bliss. Trust in the cosmic order and feel the divine presence everywhere.",
            recommendedVerseKeys = listOf(
                Pair(9, 22),  // Ch 9.22: Yoga-Kshemam Vahamyaham
                Pair(12, 15), // Ch 12.15: One who neither disturbs nor is disturbed
                Pair(18, 66), // Ch 18.66: Surrender unto Me alone, fear not
                Pair(7, 7)    // Ch 7.7: Everything is strung on Me like pearls on a thread
            )
        )
    )
}
