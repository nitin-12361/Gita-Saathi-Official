package com.nkapps.gitasaathi.network

import android.content.Context
import android.util.Base64
import android.util.Log
import com.nkapps.gitasaathi.BuildConfig
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.AudioMode
import com.nkapps.gitasaathi.data.Verse
import com.nkapps.gitasaathi.data.VoiceStyle
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.File
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "topP") val topP: Float? = null,
    @Json(name = "responseModalities") val responseModalities: List<String>? = null,
    @Json(name = "speechConfig") val speechConfig: SpeechConfig? = null
)

@JsonClass(generateAdapter = true)
data class SpeechConfig(
    @Json(name = "voiceConfig") val voiceConfig: VoiceConfig
)

@JsonClass(generateAdapter = true)
data class VoiceConfig(
    @Json(name = "prebuiltVoiceConfig") val prebuiltVoiceConfig: PrebuiltVoiceConfig
)

@JsonClass(generateAdapter = true)
data class PrebuiltVoiceConfig(
    @Json(name = "voiceName") val voiceName: String
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

interface GeminiApiService {
    @POST
    suspend fun generateWithUrl(
        @Url url: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val DEFAULT_API_KEY = ""

    private class NetworkLoggingAndErrorInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)
            val cleanUrl = request.url.toString().replace(Regex("key=[^&]+"), "key=REDACTED")
            if (!response.isSuccessful) {
                Log.e("GeminiNetworkInterceptor", "HTTP Error status ${response.code} for URL: $cleanUrl")
            } else {
                Log.d("GeminiNetworkInterceptor", "HTTP Success status ${response.code} for URL: ${request.url.encodedPath}")
            }
            return response
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkLoggingAndErrorInterceptor())
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    fun formatHttpException(e: Exception, language: AppLanguage): String {
        return when (e) {
            is java.net.SocketTimeoutException -> {
                if (language == AppLanguage.HINDI)
                    "सर्वर प्रतिक्रिया में समय सीमा समाप्त हो गई। कृपया पुनः प्रयास करें।"
                else
                    "Network timeout while connecting to Gemini service. Please try again."
            }
            is java.net.UnknownHostException -> {
                if (language == AppLanguage.HINDI)
                    "इंटरनेट कनेक्शन उपलब्ध नहीं है। कृपया अपना नेटवर्क जांचें।"
                else
                    "No internet connection. Please check your network connection."
            }
            is retrofit2.HttpException -> {
                val code = e.code()
                val errorBody = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
                Log.e("GeminiClient", "API HTTP $code error body: $errorBody")

                when {
                    code == 404 -> {
                        if (language == AppLanguage.HINDI)
                            "अनुरोधित ऑडियो मॉडल या स्वर उपलब्ध नहीं है (HTTP 404)। कृपया दूसरा स्वर चुनें।"
                        else
                            "The requested audio model or voice endpoint was not found (HTTP 404). Please try a different voice."
                    }
                    code == 400 -> {
                        if (errorBody?.contains("API key", ignoreCase = true) == true) {
                            if (language == AppLanguage.HINDI) "अमान्य API कुंजी (HTTP 400)। कृपया सेटिंग्स से अपनी Gemini Key जांचें।"
                            else "Invalid API Key (HTTP 400). Please verify your Gemini API key in Settings."
                        } else {
                            if (language == AppLanguage.HINDI) "अमान्य अनुरोध (HTTP 400)। कृपया कोई अन्य स्वर शैली चुनें।"
                            else "Invalid request formatting (HTTP 400). Please try a different voice style."
                        }
                    }
                    code == 401 || code == 403 -> {
                        if (language == AppLanguage.HINDI)
                            "अमान्य या अनधिकृत API कुंजी (HTTP $code)। कृपया सेटिंग्स से अपनी Gemini Key अद्यतन करें।"
                        else
                            "Invalid or unauthorized API key (HTTP $code). Please update your API key in Settings."
                    }
                    code == 429 -> {
                        if (language == AppLanguage.HINDI)
                            "API उपयोग सीमा समाप्त हो गई है (HTTP 429)। कृपया कुछ समय बाद प्रयास करें।"
                        else
                            "API rate limit reached (HTTP 429). Please wait a few seconds and try again."
                    }
                    code >= 500 -> {
                        if (language == AppLanguage.HINDI)
                            "Gemini सर्वर में समस्या आई है (HTTP $code)। कृपया थोड़ी देर में प्रयास करें।"
                        else
                            "Gemini service temporary server error (HTTP $code). Please try again shortly."
                    }
                    else -> {
                        if (language == AppLanguage.HINDI) "सर्वर त्रुटि (HTTP $code)"
                        else "Server Error (HTTP $code)"
                    }
                }
            }
            else -> {
                e.localizedMessage ?: if (language == AppLanguage.HINDI) "अज्ञात त्रुटि हुई।" else "An unexpected error occurred."
            }
        }
    }

    private fun getEffectiveApiKey(customKey: String?): String {
        if (!customKey.isNullOrBlank()) return customKey.trim()
        val buildKey = BuildConfig.GEMINI_API_KEY
        if (!buildKey.isNullOrBlank() && buildKey != "MY_GEMINI_API_KEY") return buildKey.trim()
        return DEFAULT_API_KEY
    }

    private fun generateLocalGitaWisdom(userQuery: String, language: AppLanguage, isFallbackFromError: Boolean = true): String {
        val q = userQuery.lowercase()
        val isHindi = language == AppLanguage.HINDI

        val body = when {
            // Anger / Rage / Frustration
            q.contains("anger") || q.contains("angry") || q.contains("क्रोध") || q.contains("गुस्सा") -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 2, श्लोक 62-63) में श्री कृष्ण कहते हैं:\n\n" +
                    "'ध्यायतो विषयान्पुंसः सङ्गस्तेषूपजायते। सङ्गात्सञ्जायते कामः कामात्क्रोधोऽभिजायते॥'\n\n" +
                    "अर्थात: विषयों का निरंतर चिंतन करने से आसक्ति पैदा होती है, आसक्ति से काम (इच्छा) और इच्छा पूर्ति न होने पर क्रोध उत्पन्न होता है। क्रोध से सम्मोह (मूढ़ता) और स्मृति-भ्रम होता है, जिससे बुद्धि का नाश हो जाता है।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. क्रोध के क्षण में तुरंत प्रतिक्रिया देने के बजाय मौन धारण करें।\n" +
                    "2. याद रखें कि परिस्थितियों पर आपका पूर्ण नियंत्रण नहीं हो सकता, लेकिन अपनी प्रतिक्रिया पर आपका नियंत्रण है।\n" +
                    "3. ध्यान एवं श्वास-प्रश्वास के अभ्यास से चित्त को शांत करें।"
                } else {
                    "In Bhagavad Gita (Chapter 2, Verses 62-63), Lord Krishna explains:\n\n" +
                    "'From attachment arises desire, and from unfulfilled desire arises anger. From anger comes delusion, and from delusion comes loss of memory and loss of intellect.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. Pause and maintain silence during moments of anger before reacting.\n" +
                    "2. Recognize that while external circumstances are beyond full control, your internal response is entirely within your control.\n" +
                    "3. Practice deep breathing and self-observation to restore peace to your mind."
                }
            }

            // Mind Control / Anxiety / Stress / Focus / Confusion
            q.contains("mind") || q.contains("focus") || q.contains("anxiety") || q.contains("stress") ||
            q.contains("मन") || q.contains("चिंता") || q.contains("तनाव") || q.contains("एकाग्रता") || q.contains("डर") || q.contains("fear") -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 6, श्लोक 5-6) में श्री कृष्ण मार्गदर्शन देते हैं:\n\n" +
                    "'उद्धरेदात्मनात्मानं नात्मानमवसादयेत्। आत्मैव ह्यात्मनो बन्धुरात्मैव रिपुरात्मनः॥'\n\n" +
                    "अर्थात: मनुष्य को अपने मन के द्वारा अपना उद्धार करना चाहिए, अपने आपको पतन की ओर नहीं ले जाना चाहिए। क्योंकि यह मन ही मनुष्य का मित्र है और मन ही उसका शत्रु भी है।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. मन चंचल है, इसे अभ्यास और वैराग्य से वश में किया जा सकता है (अभ्यासेन तु कौन्तेय वैराग्येण च गृह्यते - Ch 6.35)।\n" +
                    "2. भविष्य के भय के स्थान पर वर्तमान क्षण के कर्तव्य पर ध्यान केंद्रित करें।\n" +
                    "3. प्रतिदिन कुछ समय ध्यान, प्राणायाम और शांत चिंतन में बिताएं।"
                } else {
                    "In Bhagavad Gita (Chapter 6, Verses 5-6), Lord Krishna illuminates:\n\n" +
                    "'Elevate yourself through the power of your mind, and do not degrade yourself. For the mind alone is your best friend, and the mind alone is your worst enemy.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. The mind is turbulent, but it can be mastered through steady practice and detachment (Gita 6.35).\n" +
                    "2. Shift your attention away from future anxieties and anchor yourself in present duty.\n" +
                    "3. Dedicate time daily to quiet reflection, meditation, and calm breath control."
                }
            }

            // Work / Karma / Duty / Success / Results / Business
            q.contains("karma") || q.contains("work") || q.contains("duty") || q.contains("success") ||
            q.contains("कर्म") || q.contains("काम") || q.contains("कर्तव्य") || q.contains("सफलता") || q.contains("फल") || q.contains("job") -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 2, श्लोक 47) का अमर सिद्धांत है:\n\n" +
                    "'कर्मण्येवाधिकारस्ते मा फलेषु कदाचन। मा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि॥'\n\n" +
                    "अर्थात: तुम्हारा अधिकार केवल कर्म करने में है, उसके फलों में कभी नहीं। इसलिए कर्मफल का हेतु मत बनो और न ही अकर्म (कर्म न करने) में तुम्हारी आसक्ति हो।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. अपनी संपूर्ण क्षमता और ईमानदारी के साथ निष्काम भाव से कार्य करें।\n" +
                    "2. परिणाम की अत्यधिक चिंता करने से कार्य की गुणवत्ता प्रभावित होती है।\n" +
                    "3. फल को ईश्वर की इच्छा मानकर समत्व भाव (सफलता-विफलता में समान) से स्वीकार करें।"
                } else {
                    "In Bhagavad Gita (Chapter 2, Verse 47), Lord Krishna delivers the core lesson of Nishkama Karma:\n\n" +
                    "'You have a right to perform your prescribed duties, but never to the fruits of your actions. Never consider yourself the cause of results, nor be attached to inaction.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. Pour your entire heart and effort into your duty without being consumed by worry over outcomes.\n" +
                    "2. Worrying about results drains energy away from excellence in execution.\n" +
                    "3. Cultivate equanimity (Samatvam) - remain balanced in both success and temporary setbacks."
                }
            }

            // Devotion / God / Love / Krishna / Bhakti / Surrender
            q.contains("god") || q.contains("krishna") || q.contains("bhakti") || q.contains("love") ||
            q.contains("भगवान") || q.contains("कृष्ण") || q.contains("भक्ति") || q.contains("ईश्वर") || q.contains("प्रेम") -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 9, श्लोक 22) में श्री कृष्ण आश्वस्त करते हैं:\n\n" +
                    "'अनन्याश्चिन्तयन्तो मां ये जनाः पर्युपासते। तेषां नित्याभियुक्तानां योगक्षेमं वहाम्यहम्॥'\n\n" +
                    "अर्थात: जो भक्त अनन्य भाव से मेरा चिंतन करते हुए मेरी उपासना करते हैं, उन नित्य-अभियुक्त भक्तों के योग (अप्राप्त की प्राप्ति) और क्षेम (प्राप्त की रक्षा) का वहन मैं स्वयं करता हूँ।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. ईश्वर में अटूट विश्वास रखें और अपने जीवन की सभी चिंताओं को उनके चरणों में समर्पित कर दें।\n" +
                    "2. भक्ति का अर्थ केवल कर्मकांड नहीं, बल्कि सभी जीवों में परमात्मा के दर्शन करना है।"
                } else {
                    "In Bhagavad Gita (Chapter 9, Verse 22), Lord Krishna promises:\n\n" +
                    "'For those who always worship Me with unswerving devotion, meditating on My divine form, I personally carry what they lack and preserve what they have.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. Trust deeply in the Supreme Divine and surrender your worries with a clean heart.\n" +
                    "2. True devotion is seeing the Divine presence in all living beings and acting with love and integrity."
                }
            }

            // Fear / Grief / Death / Sorrow
            q.contains("fear") || q.contains("death") || q.contains("grief") || q.contains("sorrow") ||
            q.contains("डर") || q.contains("मृत्यु") || q.contains("शोक") || q.contains("दुःख") -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 2, श्लोक 20) में आत्मा की अमरता का ज्ञान है:\n\n" +
                    "'न जायते म्रियते वा कदाचिन्... न हन्यते हन्यमाने शरीरे॥'\n\n" +
                    "अर्थात: आत्मा न कभी जन्म लेती है और न मरती है। शरीर के मारे जाने पर भी आत्मा नहीं मारी जाती।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. मृत्यु और परिवर्तन सृष्टि का नियम है, इसे स्वीकार करें।\n" +
                    "2. शोक और मोह को ज्ञान के प्रकाश से दूर करें।"
                } else {
                    "In Bhagavad Gita (Chapter 2, Verse 20), Lord Krishna reveals the soul's immortality:\n\n" +
                    "'The soul is never born, nor does it ever die... it is not slain when the body is slain.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. Accept that change and death are natural cycles of life.\n" +
                    "2. Dissolve fear and grief through the realization of your eternal spiritual nature."
                }
            }

            // Leadership / Example / Influence
            q.contains("lead") || q.contains("leader") || q.contains("example") || q.contains("influence") ||
            q.contains("नेतृत्व") || q.contains("आदर्श") || q.contains("प्रभाव") -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 3, श्लोक 21) में श्री कृष्ण कहते हैं:\n\n" +
                    "'यद्यदाचरति श्रेष्ठस्तत्तदेवेतरो जनः। स यत्प्रमाणं कुरुते लोकस्तदनुवर्तते॥'\n\n" +
                    "अर्थात: श्रेष्ठ पुरुष जो-जो आचरण करता है, अन्य लोग भी वैसा ही करते हैं। वह जो आदर्श स्थापित करता है, समस्त संसार उसका अनुसरण करता है।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. दूसरों को उपदेश देने से पहले स्वयं उदाहरण बनें।\n" +
                    "2. नेतृत्व का अर्थ सेवा और उच्च चरित्र का प्रदर्शन है।"
                } else {
                    "In Bhagavad Gita (Chapter 3, Verse 21), Lord Krishna emphasizes leading by example:\n\n" +
                    "'Whatever action a great leader performs, common people follow. Whatever standards they set, the world pursues.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. Be the change you wish to see; lead through personal integrity.\n" +
                    "2. Leadership is not about authority, but about setting a noble example for others to follow."
                }
            }

            // Love / Relationships / Family / Friends
            q.contains("relation") || q.contains("love") || q.contains("family") || q.contains("friend") ||
            q.contains("रिश्ता") || q.contains("परिवार") || q.contains("मित्र") || q.contains("दोस्त") || q.contains("प्रेम") -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 12, श्लोक 13-14) में श्री कृष्ण समदर्शी और प्रेमपूर्ण व्यवहार का उपदेश देते हैं:\n\n" +
                    "'अद्वेष्टा सर्वभूतानां मैत्रः करुण एव च। निर्ममो निरहङ्कारः समदुःखसुखः क्षमी॥'\n\n" +
                    "अर्थात: जो किसी भी प्राणी से द्वेष नहीं करता, सबका मित्र और दयालु है, ममता और अहंकार से रहित है, सुख-दुःख में समान और क्षमावान है, वह भक्त मुझे अति प्रिय है।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. रिश्तों में अपेक्षा (Expectations) कम करें और निःस्वार्थ प्रेम व सहयोग दें।\n" +
                    "2. अहंकार को त्यागकर दूसरों के दृष्टिकोण को समझने का प्रयास करें।\n" +
                    "3. क्षमाशीलता और दया को अपने स्वभाव का अंग बनाएं।"
                } else {
                    "In Bhagavad Gita (Chapter 12, Verses 13-14), Lord Krishna describes true harmony in relationships:\n\n" +
                    "'One who is not envious but a kind friend to all living entities, free from possessiveness and ego, equipoised in happiness and distress, and forgiving — that person is very dear to Me.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. Reduce excessive expectations from others and offer selfless understanding and kindness.\n" +
                    "2. Release ego in personal relationships and listen with an open, compassionate heart.\n" +
                    "3. Practice forgiveness and cultivate patience during misunderstandings."
                }
            }

            // Default / General Life Advice
            else -> {
                if (isHindi) {
                    "भगवद्गीता (अध्याय 2, श्लोक 14) में श्री कृष्ण उपदेश देते हैं:\n\n" +
                    "'मात्रास्पर्शास्तु कौन्तेय शीतोष्णसुखदुःखदाः। आगमापायिनोऽनित्यस्तांस्तितिक्षस्व भारत॥'\n\n" +
                    "अर्थात: हे कुंतीपुत्र! सुख और दुःख, शीत और उष्ण का अनुभव कराने वाले इंद्रिय-विषय परिवर्तनशील और अनित्य हैं। इसलिए इन्हें धैर्यपूर्वक सहन करो।\n\n" +
                    "मार्गदर्शन:\n" +
                    "1. जीवन का प्रत्येक कठिन समय बीत जाता है; समभाव बनाकर रखें।\n" +
                    "2. अपने धर्म और सत्य के मार्ग पर निरंतर बढ़ते रहें।"
                } else {
                    "In Bhagavad Gita (Chapter 2, Verse 14), Lord Krishna teaches:\n\n" +
                    "'O son of Kunti, the contact of the senses with external objects gives rise to cold and heat, pleasure and pain. They are fleeting and impermanent; endure them bravely.'\n\n" +
                    "Wisdom Guidance:\n" +
                    "1. Remember that all phases in life are temporary - remain grounded in wisdom.\n" +
                    "2. Stay steadfast on the path of truth, duty, and inner peace."
                }
            }
        }

        val footer = if (isFallbackFromError) {
            if (isHindi) {
                "\n\n💡 (गीता साथी शाश्वत उपदेश)"
            } else {
                "\n\n💡 (Gita Saathi Spiritual Wisdom)"
            }
        } else ""

        return body + footer
    }

    private suspend fun callTextWithFallback(apiKey: String, request: GenerateContentRequest): GenerateContentResponse {
        val models = listOf(
            "v1beta/models/gemini-3.6-flash:generateContent",
            "v1beta/models/gemini-flash-latest:generateContent",
            "v1beta/models/gemini-3.5-flash:generateContent",
            "v1beta/models/gemini-2.5-flash-lite:generateContent",
            "v1beta/models/gemini-2.5-pro:generateContent"
        )
        var lastException: Exception? = null
        for (url in models) {
            try {
                return service.generateWithUrl(url, apiKey, request)
            } catch (e: Exception) {
                lastException = e
                Log.w("GeminiClient", "Model $url failed with ${e.message}, trying next available model...")
            }
        }
        throw lastException ?: Exception("All Gemini models failed")
    }

    private suspend fun callTtsWithFallback(apiKey: String, request: GenerateContentRequest): GenerateContentResponse {
        val ttsModels = listOf(
            "v1beta/models/gemini-2.5-flash-preview-tts:generateContent",
            "v1beta/models/gemini-3.1-flash-tts-preview:generateContent",
            "v1beta/models/gemini-2.5-pro-preview-tts:generateContent"
        )
        var lastException: Exception? = null
        for (url in ttsModels) {
            try {
                val response = service.generateWithUrl(url, apiKey, request)
                val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData
                if (inlineData != null && inlineData.data.isNotBlank()) {
                    return response
                }
            } catch (e: Exception) {
                lastException = e
                Log.w("GeminiClient", "TTS model $url failed with ${e.message}, trying fallback...")
            }
        }
        throw lastException ?: Exception("TTS generation failed.")
    }

    suspend fun getVerseInsight(verse: Verse, language: AppLanguage, customApiKey: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customApiKey)
        if (apiKey.isBlank()) {
            val fallback = if (language == AppLanguage.HINDI) {
                "अध्याय ${verse.chapterId}, श्लोक ${verse.verseId}\n\nश्लोक: ${verse.shlokaSanskrit}\n\nभावार्थ: ${verse.translationHindi}\n\nजीवनोपयोगी सीख: ${verse.meaningHindi}\n\nश्री कृष्ण का यह उपदेश हमें कठिन समय में भी धर्म और विवेक का मार्ग अपनाने का संदेश देता है।"
            } else {
                "Chapter ${verse.chapterId}, Verse ${verse.verseId}\n\nVerse: ${verse.transliteration}\n\nTranslation: ${verse.translationEnglish}\n\nLife Lesson: ${verse.meaningEnglish}\n\nLord Krishna teaches us that aligning with our inner duty and truth brings ultimate peace and resilience."
            }
            return@withContext Result.success(fallback)
        }

        val prompt = if (language == AppLanguage.HINDI) {
            """
            आप श्रीमद्भगवद्गीता के एक परम विद्वान और आध्यात्मिक मार्गदर्शक हैं। 
            कृपया अध्याय ${verse.chapterId}, श्लोक ${verse.verseId} (${verse.shlokaSanskrit}) का पावन और आधुनिक जीवनोपयोगी अर्थ समझाइए।
            उत्तर को निम्नलिखित 3 स्पष्ट और व्यावहारिक खंडों में प्रस्तुत करें:
            1. 🧘 व्यक्तिगत शांति एवं मन (Personal Mind & Peace): मानसिक तनाव, अति-विचार या भावनाओं पर नियंत्रण कैसे रखें।
            2. 👨‍👩‍👧 परिवार व सामाजिक संबंध (Family & Relationships): अपनों के साथ प्रेम, धैर्य और सामंजस्य कैसे बनाएं।
            3. 💼 कार्यक्षेत्र व करियर (Career & Leadership): काम में एकाग्रता, निर्णय लेने की क्षमता और निष्काम कर्म कैसे अपनाएं।
            भाषा अत्यंत मधुर, प्रेरणादायक और सहज रखें।
            """.trimIndent()
        } else {
            """
            You are a compassionate spiritual guide of the Bhagavad Gita. 
            Please explain Chapter ${verse.chapterId}, Verse ${verse.verseId} (${verse.transliteration}) with deep practical wisdom for modern life.
            Structure your response into 3 clear, inspiring dimensions:
            1. 🧘 Personal Mind & Peace: Overcoming anxiety, overthinking, and emotional turbulence.
            2. 👨‍👩‍👧 Family & Relationships: Nurturing patience, unconditional love, and harmony at home.
            3. 💼 Career & Leadership: Applying focus, ethical decision-making, and detachment from results at work.
            Keep the tone warm, uplifting, and practical.
            """.trimIndent()
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = "You are Gita Saathi, a calm, spiritual AI companion providing peaceful and clear wisdom from the Bhagavad Gita.")))
        )

        try {
            val response = callTextWithFallback(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                val fallback = if (language == AppLanguage.HINDI) {
                    "अध्याय ${verse.chapterId}, श्लोक ${verse.verseId}\n\nभावार्थ: ${verse.translationHindi}\n\nजीवन सीख: ${verse.meaningHindi}"
                } else {
                    "Chapter ${verse.chapterId}, Verse ${verse.verseId}\n\nTranslation: ${verse.translationEnglish}\n\nLife Lesson: ${verse.meaningEnglish}"
                }
                Result.success(fallback)
            }
        } catch (e: Exception) {
            Log.w("GeminiClient", "API call failed with ${e.message}, returning local verse wisdom")
            val fallback = if (language == AppLanguage.HINDI) {
                "अध्याय ${verse.chapterId}, श्लोक ${verse.verseId}\n\nश्लोक: ${verse.shlokaSanskrit}\n\nभावार्थ: ${verse.translationHindi}\n\nजीवन सीख: ${verse.meaningHindi}"
            } else {
                "Chapter ${verse.chapterId}, Verse ${verse.verseId}\n\nVerse: ${verse.transliteration}\n\nTranslation: ${verse.translationEnglish}\n\nLife Lesson: ${verse.meaningEnglish}"
            }
            Result.success(fallback)
        }
    }

    suspend fun askGitaAi(userQuery: String, language: AppLanguage, customApiKey: String? = null): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customApiKey)
        if (apiKey.isBlank()) {
            return@withContext Result.success(generateLocalGitaWisdom(userQuery, language, isFallbackFromError = true))
        }

        val systemPrompt = if (language == AppLanguage.HINDI) {
            "आप 'गीता साथी' हैं - श्रीमद्भगवद्गीता के कालातीत ज्ञान पर आधारित एक करुणामय एवं ज्ञानी AI मार्गदर्शक। प्रयोगकर्ता के प्रश्नों का उत्तर केवल और केवल भगवद्गीता के श्लोकों, सिद्धांतों (कर्मयोग, ज्ञानयोग, भक्तियोग, मन-नियंत्रण) और श्री कृष्ण के उपदेशों के संदर्भ में दें। उत्तर सरल, प्रेरणादायक और व्यावहारिक जीवनोपयोगी रखें।"
        } else {
            "You are 'Gita Saathi' - a compassionate and wise AI spiritual guide rooted in the timeless teachings of the Bhagavad Gita. Answer user questions directly by drawing from the Bhagavad Gita's verses, philosophy (Karma Yoga, Bhakti Yoga, Jnana Yoga, mind control), and Lord Krishna's guidance. Provide practical, peaceful, and inspiring advice for daily life."
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userQuery)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        try {
            val response = callTextWithFallback(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.success(generateLocalGitaWisdom(userQuery, language, isFallbackFromError = true))
            }
        } catch (e: Exception) {
            Log.w("GeminiClient", "Gemini API call failed with ${e.message}, falling back to local Gita wisdom")
            Result.success(generateLocalGitaWisdom(userQuery, language, isFallbackFromError = true))
        }
    }

    suspend fun getOrFetchTtsAudio(
        context: Context,
        verse: Verse,
        language: AppLanguage,
        voiceStyle: VoiceStyle,
        customApiKey: String? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        val cacheKey = "gemini_tts_v3_${verse.verseKey}_${voiceStyle.id}.wav"
        val cacheFile = File(context.cacheDir, cacheKey)

        if (cacheFile.exists() && cacheFile.length() > 0) {
            return@withContext Result.success(cacheFile)
        }

        val apiKey = getEffectiveApiKey(customApiKey)
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception(if (language == AppLanguage.HINDI) "स्वर वाचन के लिए Gemini API कुंजी की आवश्यकता है।" else "Gemini API key is required for Gemini Text-to-Speech narration."))
        }

        val ttsPrompt = buildTtsPrompt(verse, language)
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = ttsPrompt)))),
            generationConfig = GenerationConfig(
                responseModalities = listOf("AUDIO"),
                speechConfig = SpeechConfig(
                    voiceConfig = VoiceConfig(
                        prebuiltVoiceConfig = PrebuiltVoiceConfig(voiceName = voiceStyle.geminiVoiceName)
                    )
                )
            )
        )

        try {
            val response = callTtsWithFallback(apiKey, request)
            val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData

            if (inlineData != null && inlineData.data.isNotBlank()) {
                val rawBytes = Base64.decode(inlineData.data, Base64.DEFAULT)
                val playableBytes = convertToPlayableAudio(rawBytes, inlineData.mimeType)
                cacheFile.writeBytes(playableBytes)
                Result.success(cacheFile)
            } else {
                Result.failure(Exception(if (language == AppLanguage.HINDI) "खाली ऑडियो प्राप्त हुआ।" else "Gemini TTS returned empty audio stream."))
            }
        } catch (e: Exception) {
            Result.failure(Exception(formatHttpException(e, language)))
        }
    }

    suspend fun getOrFetchSampleTtsAudio(
        context: Context,
        language: AppLanguage,
        voiceStyle: VoiceStyle,
        customApiKey: String? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        val cacheKey = "gemini_sample_tts_v3_${voiceStyle.id}.wav"
        val cacheFile = File(context.cacheDir, cacheKey)

        if (cacheFile.exists() && cacheFile.length() > 0) {
            return@withContext Result.success(cacheFile)
        }

        val apiKey = getEffectiveApiKey(customApiKey)
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception(if (language == AppLanguage.HINDI) "API कुंजी आवश्यक है।" else "Gemini API key is required."))
        }

        val (sampleSystemPrompt, sampleText) = if (language == AppLanguage.HINDI) {
            "You are a revered spiritual master speaking in a warm, deeply human, and expressive Hindi voice." to
            "ॐ नमो भगवते वासुदेवाय। कर्मण्येवाधिकारस्ते मा फलेषु कदाचन। आपका कर्म में ही अधिकार है, फल में कभी नहीं।"
        } else {
            "You are a revered spiritual master speaking in a warm, deeply human, and inspiring English voice." to
            "You have a right to perform your prescribed duty, but you are not entitled to the fruits of action."
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = sampleText)))),
            systemInstruction = Content(parts = listOf(Part(text = sampleSystemPrompt))),
            generationConfig = GenerationConfig(
                responseModalities = listOf("AUDIO"),
                speechConfig = SpeechConfig(
                    voiceConfig = VoiceConfig(
                        prebuiltVoiceConfig = PrebuiltVoiceConfig(voiceName = voiceStyle.geminiVoiceName)
                    )
                )
            )
        )

        try {
            val response = callTtsWithFallback(apiKey, request)
            val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData

            if (inlineData != null && inlineData.data.isNotBlank()) {
                val rawBytes = Base64.decode(inlineData.data, Base64.DEFAULT)
                val playableBytes = convertToPlayableAudio(rawBytes, inlineData.mimeType)
                cacheFile.writeBytes(playableBytes)
                Result.success(cacheFile)
            } else {
                Result.failure(Exception(if (language == AppLanguage.HINDI) "नमूना ऑडियो खाली प्राप्त हुआ।" else "Gemini TTS sample returned empty audio."))
            }
        } catch (e: Exception) {
            Result.failure(Exception(formatHttpException(e, language)))
        }
    }

    private fun convertToPlayableAudio(audioBytes: ByteArray, mimeType: String?): ByteArray {
        if (audioBytes.isEmpty()) return audioBytes

        val isWav = audioBytes.size >= 4 &&
                audioBytes[0] == 'R'.code.toByte() &&
                audioBytes[1] == 'I'.code.toByte() &&
                audioBytes[2] == 'F'.code.toByte() &&
                audioBytes[3] == 'F'.code.toByte()

        val isMp3 = (audioBytes.size >= 3 &&
                audioBytes[0] == 'I'.code.toByte() &&
                audioBytes[1] == 'D'.code.toByte() &&
                audioBytes[2] == '3'.code.toByte()) ||
                (audioBytes.size >= 2 &&
                (audioBytes[0].toInt() and 0xFF) == 0xFF &&
                (audioBytes[1].toInt() and 0xE0) == 0xE0)

        val isOgg = audioBytes.size >= 4 &&
                audioBytes[0] == 'O'.code.toByte() &&
                audioBytes[1] == 'g'.code.toByte() &&
                audioBytes[2] == 'g'.code.toByte() &&
                audioBytes[3] == 'S'.code.toByte()

        if (isWav || isMp3 || isOgg) {
            return audioBytes
        }

        var sampleRate = 24000
        if (!mimeType.isNullOrBlank()) {
            val rateRegex = Regex("rate=(\\d+)")
            val match = rateRegex.find(mimeType)
            if (match != null) {
                match.groupValues.getOrNull(1)?.toIntOrNull()?.let {
                    sampleRate = it
                }
            }
        }

        return createWavHeader(audioBytes, sampleRate = sampleRate, channels = 1, bitsPerSample = 16)
    }

    private fun createWavHeader(
        pcmData: ByteArray,
        sampleRate: Int = 24000,
        channels: Int = 1,
        bitsPerSample: Int = 16
    ): ByteArray {
        val totalDataLen = pcmData.size + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * bitsPerSample / 8).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (pcmData.size and 0xff).toByte()
        header[41] = ((pcmData.size shr 8) and 0xff).toByte()
        header[42] = ((pcmData.size shr 16) and 0xff).toByte()
        header[43] = ((pcmData.size shr 24) and 0xff).toByte()

        val wavData = ByteArray(44 + pcmData.size)
        System.arraycopy(header, 0, wavData, 0, 44)
        System.arraycopy(pcmData, 0, wavData, 44, pcmData.size)
        return wavData
    }

    private fun buildTtsPrompt(verse: Verse, language: AppLanguage): String {
        val cleanShloka = verse.shlokaSanskrit
            .replace("॥", " . ")
            .replace("।", " , ")
            .replace("\n", " ")
        return "You are a professional Vedic chanter. Please recite this sacred Sanskrit Bhagavad Gita verse in a deeply human, soul-stirring, and resonant devotional voice. Use natural Vedic chanting intonation with profound reverence and clear Sanskrit pronunciation:\n\n$cleanShloka"
    }
}

