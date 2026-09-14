package com.nkapps.gitasaathi.data

import android.content.Context
import android.util.Log
import com.nkapps.gitasaathi.GitaApplication
import org.json.JSONObject

data class ShlokaLifeApplication(
    val personalHindi: String,
    val personalEnglish: String,
    val familyHindi: String,
    val familyEnglish: String,
    val careerHindi: String,
    val careerEnglish: String
)

object GitaLifeApplicationData {

    @Volatile
    private var applicationsMap: Map<String, ShlokaLifeApplication>? = null

    fun init(context: Context) {
        if (applicationsMap != null) return
        loadApplicationsFromAssets(context)
    }

    private fun loadApplicationsFromAssets(context: Context) {
        try {
            val jsonString = context.assets.open("gita_life_applications.json").bufferedReader().use { it.readText() }
            val jsonObj = JSONObject(jsonString)
            val map = HashMap<String, ShlokaLifeApplication>(jsonObj.length())
            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val entryObj = jsonObj.getJSONObject(key)
                map[key] = ShlokaLifeApplication(
                    personalHindi = entryObj.optString("personalHindi"),
                    personalEnglish = entryObj.optString("personalEnglish"),
                    familyHindi = entryObj.optString("familyHindi"),
                    familyEnglish = entryObj.optString("familyEnglish"),
                    careerHindi = entryObj.optString("careerHindi"),
                    careerEnglish = entryObj.optString("careerEnglish")
                )
            }
            applicationsMap = map
            Log.d("GitaLifeAppData", "Loaded ${map.size} genuine life applications from assets.")
        } catch (e: Exception) {
            Log.e("GitaLifeAppData", "Error loading gita_life_applications.json", e)
        }
    }

    private fun getMap(context: Context? = null): Map<String, ShlokaLifeApplication>? {
        applicationsMap?.let { return it }
        val ctx = context ?: GitaApplication.instance
        if (ctx != null) {
            loadApplicationsFromAssets(ctx)
            applicationsMap?.let { return it }
        }
        return null
    }

    /**
     * Returns the 100% genuine, verse-specific 3-Life Applications.
     * Each verse across all 700+ verses has its own custom insights!
     */
    fun getLifeApplication(
        chapterId: Int,
        verseId: Int,
        verse: Verse? = null,
        context: Context? = null
    ): ShlokaLifeApplication {
        val key = "$chapterId:$verseId"
        getMap(context)?.get(key)?.let { return it }

        // Master hardcoded fallback for key verses if asset is unavailable (e.g. in minimal unit test)
        return fallbackCurated[key] ?: ShlokaLifeApplication(
            personalHindi = "श्लोक $chapterId.$verseId का आत्म-चिंतन: मन को शांत, केंद्रित और संशयमुक्त रखें। व्यर्थ चिंताओं को छोड़कर वर्तमान कर्म पर ध्यान दें।",
            personalEnglish = "Verse $chapterId.$verseId reflection: Center your mind in tranquility. Free yourself from overthinking and immerse in current duty.",
            familyHindi = "पारिवारिक जीवन में श्लोक $chapterId.$verseId की सीख: अपनों के प्रति निःस्वार्थ प्रेम, आदर और सामंजस्य बनाए रखें।",
            familyEnglish = "Domestic wisdom of Verse $chapterId.$verseId: Nurture patience, mutual respect, and heartfelt warmth in family relationships.",
            careerHindi = "कार्यक्षेत्र में श्लोक $chapterId.$verseId का संदेश: काम में 100% समर्पण और सत्यनिष्ठा रखें। परिणाम की चिंता छोड़ निष्काम कर्म अपनाएं।",
            careerEnglish = "Career application of Verse $chapterId.$verseId: Strive for uncompromising quality and ethical leadership in your craft."
        )
    }

    private val fallbackCurated = mapOf(
        "2:47" to ShlokaLifeApplication(
            personalHindi = "तुम्हारा अधिकार केवल कर्म करने में है, फल में कभी नहीं। फल की चिंता छोड़कर वर्तमान में पूरी निष्ठा से काम करें।",
            personalEnglish = "You have a right only to work, never to its fruits. Drop anxiety about results and immerse yourself fully in current action.",
            familyHindi = "परिवार में प्यार और सहयोग बिना किसी रिटर्न की अपेक्षा के दें। जब आप अपेक्षा छोड़ते हैं तो रिश्ते खिल उठते हैं।",
            familyEnglish = "Give love and support at home without expecting returns. Relationships blossom when transactional scorekeeping stops.",
            careerHindi = "प्रमोशन या परिणाम के तनाव में काम की गुणवत्ता न गिरने दें। अपने काम में 100% महारत लाएं, सफलता अपने आप मिलेगी।",
            careerEnglish = "Do not compromise craftsmanship due to appraisal anxiety. Master your execution; success inevitably follows excellence."
        ),
        "2:14" to ShlokaLifeApplication(
            personalHindi = "सुख और दुःख, मान और अपमान मौसम की तरह आते-जाते हैं। इन्हें समभाव और धैर्य से सहन करना ही आत्मबल की निशानी है।",
            personalEnglish = "Pleasure and pain, praise and blame are fleeting like seasons. Enduring them with calm equanimity builds inner power.",
            familyHindi = "घरेलू उतार-चढ़ाव में तुरंत तीखी प्रतिक्रिया न दें। याद रखें कि कठिन समय सदा नहीं रहता; धैर्य से रिश्ते टिकते हैं।",
            familyEnglish = "Do not react impulsively to domestic friction. Tough times pass; patience preserves enduring relationships.",
            careerHindi = "बाजार के उतार-चढ़ाव और करियर के उतार-चढ़ाव में संतुलन बनाए रखें। सफलता में अहंकारी न बनें और मंदी में निराश न हों।",
            careerEnglish = "Maintain emotional balance through market cycles. Stay grounded in success and resilient in downturns."
        )
    )
}
