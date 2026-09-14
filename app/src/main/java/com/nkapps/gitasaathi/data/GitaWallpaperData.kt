package com.nkapps.gitasaathi.data

import androidx.annotation.DrawableRes
import com.nkapps.gitasaathi.R

enum class WallpaperCategory(val titleHindi: String, val titleEnglish: String) {
    ALL("सभी", "All"),
    CHARIOT("कृष्ण-अर्जुन", "Krishna & Arjuna"),
    VISHWAROOP("विश्वरूप", "Vishwaroop"),
    VRINDAVAN("वृन्दावन", "Vrindavan"),
    DHYANA("ध्यान व योग", "Meditation"),
    UPADESHA("कर्म व उपदेश", "Teachings")
}

data class GitaWallpaper(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val category: WallpaperCategory,
    @DrawableRes val drawableResId: Int,
    val shlokaSanskrit: String,
    val shlokaReference: String,
    val shlokaMeaningHindi: String,
    val shlokaMeaningEnglish: String
)

object GitaWallpaperData {

    val WALLPAPERS = listOf(
        GitaWallpaper(
            id = "chariot_4k_portrait",
            titleHindi = "कुरुक्षेत्र विजय रथ",
            titleEnglish = "Kurukshetra Victory Chariot",
            category = WallpaperCategory.CHARIOT,
            drawableResId = R.drawable.bg_scene_chariot_portrait,
            shlokaSanskrit = "यत्र योगेश्वरः कृष्णो यत्र पार्थो धनुर्धरः।\nतत्र श्रीर्विजयो भूतिर्ध्रुवा नीतिर्मतिर्मम॥",
            shlokaReference = "श्रीमद्भगवद्गीता १८.७८",
            shlokaMeaningHindi = "जहाँ योगेश्वर श्रीकृष्ण हैं और जहाँ गाण्डीवधारी अर्जुन हैं, वहीं श्री, विजय, अलौकिक विभूति और अचल नीति है।",
            shlokaMeaningEnglish = "Wherever there is Krishna, the Lord of Yoga, and wherever there is Arjuna, the supreme archer, there will certainly be victory, fortune, and righteousness."
        ),
        GitaWallpaper(
            id = "flute_4k_portrait",
            titleHindi = "मुरलीधर दिव्य दर्शन",
            titleEnglish = "Murlidhar Divine Grace",
            category = WallpaperCategory.VRINDAVAN,
            drawableResId = R.drawable.bg_scene_flute_portrait,
            shlokaSanskrit = "सर्वधर्मान्परित्यज्य मामेकं शरणं व्रज।\nअहं त्वां सर्वपापेभ्यो मोक्षयिष्यामि मा शुचः॥",
            shlokaReference = "श्रीमद्भगवद्गीता १८.६६",
            shlokaMeaningHindi = "सब धर्मों को त्यागकर केवल मेरी शरण में आ जाओ। मैं तुम्हें समस्त पापों से मुक्त कर दूँगा, शोक मत करो।",
            shlokaMeaningEnglish = "Abandon all varieties of dharma and simply surrender unto Me alone. I shall liberate you from all sins; do not grieve."
        ),
        GitaWallpaper(
            id = "vishwaroop_darshan",
            titleHindi = "परम दिव्य विश्वरूप",
            titleEnglish = "Cosmic Vishwaroop Vision",
            category = WallpaperCategory.VISHWAROOP,
            drawableResId = R.drawable.bg_scene_vishwaroop,
            shlokaSanskrit = "दिवि सूर्यसहस्रस्य भवेद्युगपदुत्थिता।\nयदि भाः सदृशी सा स्याद्भासस्तस्य महात्मनः॥",
            shlokaReference = "श्रीमद्भगवद्गीता ११.१२",
            shlokaMeaningHindi = "यदि आकाश में एक साथ सहस्र सूर्यों का तेज प्रकट हो जाए, तो वह भी उस परम पुरुष के तेज के समान शायद ही हो सके।",
            shlokaMeaningEnglish = "If thousands of suns were to blaze forth simultaneously in the sky, their radiance might resemble the glory of that Supreme Being."
        ),
        GitaWallpaper(
            id = "karma_yoga_chariot",
            titleHindi = "कर्मण्येवाधिकारस्ते",
            titleEnglish = "Nishkama Karma Chariot",
            category = WallpaperCategory.UPADESHA,
            drawableResId = R.drawable.bg_scene_karma,
            shlokaSanskrit = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि॥",
            shlokaReference = "श्रीमद्भगवद्गीता २.४७",
            shlokaMeaningHindi = "तुम्हारा अधिकार केवल कर्म करने में है, फल में कभी नहीं। कर्मफल के हेतु मत बनो और न ही अकर्म में तुम्हारी आसक्ति हो।",
            shlokaMeaningEnglish = "You have a right only to perform your duty, but never to the fruits of action. Never let the fruits be your motive, nor be attached to inaction."
        ),
        GitaWallpaper(
            id = "dhyana_samadhi",
            titleHindi = "ध्यानस्थ आत्मशांति",
            titleEnglish = "Inner Peace & Meditation",
            category = WallpaperCategory.DHYANA,
            drawableResId = R.drawable.bg_scene_dhyana,
            shlokaSanskrit = "यतो यतो निश्चरति मनश्चञ्चलमस्थिरम्।\nततस्ततो नियम्यैतदात्मन्येव वशं नयेत्॥",
            shlokaReference = "श्रीमद्भगवद्गीता ६.२६",
            shlokaMeaningHindi = "यह चंचल और अस्थिर मन जहाँ-जहाँ भी भटके, वहाँ-वहाँ से इसे रोककर आत्मा में ही स्थिर करना चाहिए।",
            shlokaMeaningEnglish = "From whatever cause the restless and unsteady mind wanders away, from that one should restrain it and bring it back under the control of the Self."
        ),
        GitaWallpaper(
            id = "dharma_raksha_chariot",
            titleHindi = "यदा यदा हि धर्मस्य",
            titleEnglish = "Descent of Righteousness",
            category = WallpaperCategory.CHARIOT,
            drawableResId = R.drawable.bg_scene_chariot,
            shlokaSanskrit = "यदा यदा हि धर्मस्य ग्लानिर्भवति भारत।\nअभ्युत्थानमधर्मस्य तदात्मानं सृजाम्यहम्॥",
            shlokaReference = "श्रीमद्भगवद्गीता ४.७",
            shlokaMeaningHindi = "हे भारत! जब-जब धर्म की हानि और अधर्म की वृद्धि होती है, तब-तब मैं अपने रूप को रचता हूँ अर्थात प्रकट होता हूँ।",
            shlokaMeaningEnglish = "Whenever there is a decline in righteousness and an increase in unrighteousness, O Bharata, at that time I manifest Myself."
        ),
        GitaWallpaper(
            id = "vrindavan_peace_flute",
            titleHindi = "वृन्दावन रास माधुरी",
            titleEnglish = "Vrindavan Spiritual Bliss",
            category = WallpaperCategory.VRINDAVAN,
            drawableResId = R.drawable.bg_scene_flute,
            shlokaSanskrit = "मन्मना भव मद्भक्तो मद्याजी मां नमस्कुरु।\nमामेवैष्यसि सत्यं ते प्रतिजाने प्रियोऽसि मे॥",
            shlokaReference = "श्रीमद्भगवद्गीता १८.६५",
            shlokaMeaningHindi = "मुझमें मन लगाओ, मेरे भक्त बनो, मेरा पूजन करो और मुझे प्रणाम करो। तुम निश्चित ही मुझे प्राप्त होगे, यह मेरा सत्य वचन है।",
            shlokaMeaningEnglish = "Fix your mind on Me, be devoted to Me, sacrifice unto Me, bow down to Me. Truly you shall come to Me; I promise you, for you are dear to Me."
        ),
        GitaWallpaper(
            id = "moksha_grace",
            titleHindi = "परम मोक्ष एवं अभय",
            titleEnglish = "Supreme Grace & Moksha",
            category = WallpaperCategory.UPADESHA,
            drawableResId = R.drawable.bg_scene_moksha,
            shlokaSanskrit = "अनन्याश्चिन्तयन्तो मां ये जनाः पर्युपासते।\nतेषां नित्याभियुक्तानां योगक्षेमं वहाम्यहम्॥",
            shlokaReference = "श्रीमद्भगवद्गीता ९.२२",
            shlokaMeaningHindi = "जो अनन्य भाव से मेरा चिन्तन करते हुए मेरी उपासना करते हैं, उन नित्य युक्त भक्तों के योगक्षेम (रक्षा व भरण-पोषण) का वहन मैं स्वयं करता हूँ।",
            shlokaMeaningEnglish = "For those who always worship Me with exclusive devotion, meditating on My transcendental form, to them I carry what they lack and preserve what they have."
        )
    )

    fun getByCategory(category: WallpaperCategory): List<GitaWallpaper> {
        return if (category == WallpaperCategory.ALL) {
            WALLPAPERS
        } else {
            WALLPAPERS.filter { it.category == category }
        }
    }
}
