package com.nkapps.gitasaathi.data

data class QuizQuestion(
    val id: Int,
    val questionHindi: String,
    val questionEnglish: String,
    val optionsHindi: List<String>,
    val optionsEnglish: List<String>,
    val correctOptionIndex: Int,
    val explanationHindi: String,
    val explanationEnglish: String,
    val categoryHindi: String,
    val categoryEnglish: String
)

data class KarmaBadge(
    val id: String,
    val titleHindi: String,
    val titleEnglish: String,
    val descriptionHindi: String,
    val descriptionEnglish: String,
    val requiredPoints: Int,
    val icon: String
)

object GitaQuizData {

    val BADGES = listOf(
        KarmaBadge(
            id = "seeker",
            titleHindi = "जिज्ञासु",
            titleEnglish = "Spiritual Seeker",
            descriptionHindi = "गीता प्रश्नोत्तरी की पहली यात्रा पूर्ण की।",
            descriptionEnglish = "Completed your first Gita Quiz journey.",
            requiredPoints = 10,
            icon = "🌟"
        ),
        KarmaBadge(
            id = "karma_yogi",
            titleHindi = "कर्मयोगी",
            titleEnglish = "Karma Yogi",
            descriptionHindi = "50 पुण्य अंक अर्जित कर कर्म के मार्ग पर अग्रसर हुए।",
            descriptionEnglish = "Earned 50+ Karma Points on the path of selfless duty.",
            requiredPoints = 50,
            icon = "🧘"
        ),
        KarmaBadge(
            id = "wisdom_seeker",
            titleHindi = "ज्ञानपिपासु",
            titleEnglish = "Wisdom Seeker",
            descriptionHindi = "150 पुण्य अंक अर्जित कर दिव्य ज्ञान की साधना की।",
            descriptionEnglish = "Earned 150+ Karma Points pursuing divine spiritual wisdom.",
            requiredPoints = 150,
            icon = "🪷"
        ),
        KarmaBadge(
            id = "gita_scholar",
            titleHindi = "गीता मनीषी",
            titleEnglish = "Gita Scholar",
            descriptionHindi = "300 पुण्य अंक अर्जित कर गीता के सिद्धांतों को आत्मसात किया।",
            descriptionEnglish = "Earned 300+ Karma Points mastering core Gita teachings.",
            requiredPoints = 300,
            icon = "👑"
        ),
        KarmaBadge(
            id = "enlightened",
            titleHindi = "आत्मज्ञानी",
            titleEnglish = "Self-Realized Soul",
            descriptionHindi = "500 पुण्य अंक अर्जित कर सर्वोच्च आध्यात्मिक प्रज्ञा प्राप्त की।",
            descriptionEnglish = "Earned 500+ Karma Points achieving the pinnacle of spiritual insight.",
            requiredPoints = 500,
            icon = "🕉️"
        )
    )

    val QUESTIONS_POOL = listOf(
        QuizQuestion(
            id = 1,
            questionHindi = "अर्जुन के प्रसिद्ध दिव्य धनुष का क्या नाम था?",
            questionEnglish = "What was the name of Arjuna's famous divine celestial bow?",
            optionsHindi = listOf("पिनाक", "गाण्डीव", "शारंग", "कोदण्ड"),
            optionsEnglish = listOf("Pinaka", "Gandiva", "Sharanga", "Kodanda"),
            correctOptionIndex = 1,
            explanationHindi = "अर्जुन के दिव्य धनुष का नाम 'गाण्डीव' था, जिसे उन्हें अग्निदेव और वरुणदेव द्वारा प्रदान किया गया था।",
            explanationEnglish = "Arjuna's celestial bow was named 'Gandiva', gifted to him by Agni and Varuna (Bhagavad Gita 1.29).",
            categoryHindi = "महाभारत",
            categoryEnglish = "Mahabharata"
        ),
        QuizQuestion(
            id = 2,
            questionHindi = "भगवान श्रीकृष्ण के पावन शंख का क्या नाम है?",
            questionEnglish = "What was the name of Lord Krishna's divine conch shell?",
            optionsHindi = listOf("पाञ्चजन्य", "देवदत्त", "पौण्ड्र", "अनन्तविजय"),
            optionsEnglish = listOf("Panchajanya", "Devadatta", "Paundra", "Anantavijaya"),
            correctOptionIndex = 0,
            explanationHindi = "भगवान श्रीकृष्ण ने 'पाञ्चजन्य' नामक शंख फूंका, अर्जुन ने 'देवदत्त' और भीम ने 'पौण्ड्र' शंख बजाया (अध्याय 1, श्लोक 15)।",
            explanationEnglish = "Lord Krishna blew his divine conch 'Panchajanya', Arjuna blew 'Devadatta', and Bhima blew 'Paundra' (Gita 1.15).",
            categoryHindi = "गीता प्रसंग",
            categoryEnglish = "Gita Chronicles"
        ),
        QuizQuestion(
            id = 3,
            questionHindi = "श्रीमद्भगवद्गीता में कुल कितने अध्याय और श्लोक हैं?",
            questionEnglish = "How many chapters and verses are there in Srimad Bhagavad Gita?",
            optionsHindi = listOf("12 अध्याय, 500 श्लोक", "18 अध्याय, 700 श्लोक", "16 अध्याय, 650 श्लोक", "24 अध्याय, 1000 श्लोक"),
            optionsEnglish = listOf("12 Chapters, 500 Verses", "18 Chapters, 700 Verses", "16 Chapters, 650 Verses", "24 Chapters, 1000 Verses"),
            correctOptionIndex = 1,
            explanationHindi = "श्रीमद्भगवद्गीता में कुल 18 अध्याय और 700 श्लोक हैं, जो महाभारत के भीष्म पर्व का अंग हैं।",
            explanationEnglish = "The Bhagavad Gita consists of 18 Chapters and 700 Verses, forming an integral part of the Bhishma Parva of Mahabharata.",
            categoryHindi = "गीता संरचना",
            categoryEnglish = "Gita Structure"
        ),
        QuizQuestion(
            id = 4,
            questionHindi = "'कर्मण्येवाधिकारस्ते मा फलेषु कदाचन' किस अध्याय का श्लोक है?",
            questionEnglish = "'Karmanye Vadhikaraste Ma Phaleshu Kadachana' belongs to which chapter?",
            optionsHindi = listOf("अध्याय 1 (अर्जुनविषादयोग)", "अध्याय 2 (सांख्ययोग)", "अध्याय 3 (कर्मयोग)", "अध्याय 4 (ज्ञानकर्मसंन्यासयोग)"),
            optionsEnglish = listOf("Chapter 1 (Arjuna Vishada)", "Chapter 2 (Sankhya Yoga)", "Chapter 3 (Karma Yoga)", "Chapter 4 (Jnana Yoga)"),
            correctOptionIndex = 1,
            explanationHindi = "यह प्रसिद्ध श्लोक अध्याय 2 (सांख्ययोग) के श्लोक 47 में आता है, जहाँ निष्काम कर्म का उपदेश दिया गया है।",
            explanationEnglish = "This celebrated verse is Chapter 2, Verse 47, where Krishna gives the supreme teaching of Nishkam Karma (selfless action).",
            categoryHindi = "कर्मयोग",
            categoryEnglish = "Karma Yoga"
        ),
        QuizQuestion(
            id = 5,
            questionHindi = "भगवान श्रीकृष्ण ने अपना 'विश्वरूप' किस अध्याय में प्रकट किया था?",
            questionEnglish = "In which chapter did Lord Krishna reveal His cosmic 'Vishwaroopa' form?",
            optionsHindi = listOf("अध्याय 9", "अध्याय 10", "अध्याय 11", "अध्याय 12"),
            optionsEnglish = listOf("Chapter 9", "Chapter 10", "Chapter 11", "Chapter 12"),
            correctOptionIndex = 2,
            explanationHindi = "अध्याय 11 'विश्वरूपदर्शनयोग' में श्रीकृष्ण ने अर्जुन को दिव्य दृष्टि प्रदान कर अपना विराट विश्वरूप दिखाया।",
            explanationEnglish = "In Chapter 11 ('Vishwaroopa Darshana Yoga'), Lord Krishna bestowed divine vision upon Arjuna to reveal His cosmic form.",
            categoryHindi = "विश्वरूप दर्शन",
            categoryEnglish = "Cosmic Vision"
        ),
        QuizQuestion(
            id = 6,
            questionHindi = "गीता के अनुसार प्रकृति के तीन गुण कौन-कौन से हैं?",
            questionEnglish = "According to the Gita, what are the three modes (Gunas) of material nature?",
            optionsHindi = listOf("सत्व, रज, तम", "धर्म, अर्थ, काम", "मन, बुद्धि, अहंकार", "जाग्रत, स्वप्न, सुषुप्ति"),
            optionsEnglish = listOf("Sattva, Rajas, Tamas", "Dharma, Artha, Kama", "Manas, Buddhi, Ahankara", "Jagrat, Swapna, Sushupti"),
            correctOptionIndex = 0,
            explanationHindi = "प्रकृति के तीन गुण सत्व (शुद्धता/प्रकाश), रज (वासना/क्रियाशीलता) और तम (अज्ञान/आलस्य) हैं (अध्याय 14)।",
            explanationEnglish = "The three primal gunas are Sattva (purity/illumination), Rajas (passion/activity), and Tamas (ignorance/inertia) (Chapter 14).",
            categoryHindi = "गुणत्रय विभाग",
            categoryEnglish = "Three Modes"
        ),
        QuizQuestion(
            id = 7,
            questionHindi = "कुरुक्षेत्र के युद्ध का आँखों देखा हाल धृतराष्ट्र को किसने सुनाया?",
            questionEnglish = "Who narrated the live account of the Kurukshetra war to King Dhritarashtra?",
            optionsHindi = listOf("विदुर जी", "संजय", "द्रोणाचार्य", "कृपाचार्य"),
            optionsEnglish = listOf("Vidura", "Sanjaya", "Dronacharya", "Kripacharya"),
            correctOptionIndex = 1,
            explanationHindi = "महर्षि वेदव्यास की दी गई दिव्य दृष्टि से संजय ने राजमहल में बैठकर धृतराष्ट्र को सम्पूर्ण युद्ध सुनाया।",
            explanationEnglish = "Blessed with divine celestial vision by Sage Vedavyasa, Sanjaya narrated the entire war to Dhritarashtra.",
            categoryHindi = "महाभारत",
            categoryEnglish = "Mahabharata"
        ),
        QuizQuestion(
            id = 8,
            questionHindi = "गीता के किस अध्याय को 'भक्तियोग' कहा जाता है?",
            questionEnglish = "Which chapter of the Bhagavad Gita is titled 'Bhakti Yoga'?",
            optionsHindi = listOf("अध्याय 7", "अध्याय 9", "अध्याय 12", "अध्याय 15"),
            optionsEnglish = listOf("Chapter 7", "Chapter 9", "Chapter 12", "Chapter 15"),
            correctOptionIndex = 2,
            explanationHindi = "अध्याय 12 को 'भक्तियोग' कहा जाता है, जिसमें भगवान के प्रति अनन्य प्रेम और भक्ति के सर्वोच्च लक्षण बताए गए हैं।",
            explanationEnglish = "Chapter 12 is titled 'Bhakti Yoga', elucidating the qualities of the true devotee and unalloyed surrender to the Divine.",
            categoryHindi = "भक्तियोग",
            categoryEnglish = "Bhakti Yoga"
        ),
        QuizQuestion(
            id = 9,
            questionHindi = "'नैनं छिन्दन्ति शस्त्राणि नैनं दहति पावकः' श्लोक में किसकी अमरता का वर्णन है?",
            questionEnglish = "In the verse 'Nainam Chhindanti Shastrani...', whose immortality is described?",
            optionsHindi = listOf("शरीर की", "आत्मा की", "मन की", "इंद्रियों की"),
            optionsEnglish = listOf("The Physical Body", "The Immortal Soul (Atman)", "The Mind", "The Senses"),
            correctOptionIndex = 1,
            explanationHindi = "भगवान श्रीकृष्ण कहते हैं कि आत्मा को न शस्त्र काट सकते हैं, न आग जला सकती है, न जल गीला कर सकता है, न वायु सुखा सकती है (अध्याय 2, श्लोक 23)।",
            explanationEnglish = "Lord Krishna teaches that the Soul (Atman) can never be pierced by weapons, burned by fire, moistened by water, nor dried by wind (Gita 2.23).",
            categoryHindi = "आत्म ज्ञान",
            categoryEnglish = "Self Knowledge"
        ),
        QuizQuestion(
            id = 10,
            questionHindi = "गीता के अनुसार चंचल मन को वश में करने के दो उपाय क्या हैं?",
            questionEnglish = "According to Gita (6.35), what are the two supreme ways to subdue the restless mind?",
            optionsHindi = listOf("धन और शक्ति", "अभ्यास और वैराग्य", "यज्ञ और दान", "क्रोध और दण्ड"),
            optionsEnglish = listOf("Wealth and Power", "Abhyasa (Practice) & Vairagya (Detachment)", "Rituals and Charity", "Anger and Punishment"),
            correctOptionIndex = 1,
            explanationHindi = "'अभ्यासेन तु कौन्तेय वैराग्येण च गृह्यते' (6.35) - निरंतर अभ्यास और अनासक्ति (वैराग्य) से मन को पूर्णतः वश में किया जा सकता है।",
            explanationEnglish = "'Abhyasena tu kaunteya vairagyena cha grihyate' (6.35) - The turbulent mind is conquered by constant practice and detachment.",
            categoryHindi = "ध्यानयोग",
            categoryEnglish = "Dhyana Yoga"
        ),
        QuizQuestion(
            id = 11,
            questionHindi = "गीता का अंतिम 18वाँ अध्याय किस नाम से जाना जाता है?",
            questionEnglish = "What is the title of the final 18th Chapter of the Bhagavad Gita?",
            optionsHindi = listOf("मोक्षसंन्यासयोग", "पुरुषोत्तमयोग", "गुणत्रयविभागयोग", "दैवासुरसम्पद्विभागयोग"),
            optionsEnglish = listOf("Moksha Sannyasa Yoga", "Purushottama Yoga", "Gunatraya Vibhaga Yoga", "Daivasura Sampad Yoga"),
            correctOptionIndex = 0,
            explanationHindi = "अध्याय 18 का नाम 'मोक्षसंन्यासयोग' है, जिसमें सम्पूर्ण गीता का सार और शरणागति का अंतिम उपदेश दिया गया है।",
            explanationEnglish = "Chapter 18 is titled 'Moksha Sannyasa Yoga', summarizing the entire Gita and culminating in absolute surrender to Krishna.",
            categoryHindi = "गीता संरचना",
            categoryEnglish = "Gita Structure"
        ),
        QuizQuestion(
            id = 12,
            questionHindi = "भगवान श्रीकृष्ण के अनुसार मनुष्य का सबसे बड़ा आंतरिक शत्रु क्या है?",
            questionEnglish = "According to Lord Krishna (3.37), what is humanity's greatest internal enemy?",
            optionsHindi = listOf("काम और क्रोध (वासना)", "दरिद्रता", "शारीरिक दुर्बलता", "शत्रु सेना"),
            optionsEnglish = listOf("Kama (Lust/Desire) & Krodha (Anger)", "Poverty", "Physical Weakness", "Enemy Soldiers"),
            correctOptionIndex = 0,
            explanationHindi = "'काम एष क्रोध एष रजोगुणसमुद्भवः' (3.37) - रजोगुण से उत्पन्न होने वाला काम (अतृप्त वासना) और क्रोध मनुष्य का सबसे बड़ा विनाशक शत्रु है।",
            explanationEnglish = "'Kama esha krodha esha' (3.37) - Uncontrolled lust/selfish desires born of passion inevitably turn into blinding anger.",
            categoryHindi = "मनोनिग्रह",
            categoryEnglish = "Mind Mastery"
        ),
        QuizQuestion(
            id = 13,
            questionHindi = "अर्जुन के रथ की ध्वजा (झंडे) पर कौन विराजमान थे?",
            questionEnglish = "Who was seated upon the flag/emblem of Arjuna's chariot?",
            optionsHindi = listOf("गरुड़ देव", "पवनपुत्र हनुमान जी", "नंदी", "सूर्य देव"),
            optionsEnglish = listOf("Garuda", "Lord Hanuman", "Nandi", "Surya"),
            correctOptionIndex = 1,
            explanationHindi = "अर्जुन के रथ को 'कपिध्वज' कहा जाता है क्योंकि उनकी ध्वजा पर साक्षात पवनपुत्र हनुमान जी विराजमान थे।",
            explanationEnglish = "Arjuna's chariot was called 'Kapidhwaja' because Lord Hanuman resided on its celestial banner.",
            categoryHindi = "महाभारत",
            categoryEnglish = "Mahabharata"
        ),
        QuizQuestion(
            id = 14,
            questionHindi = "गीता के अनुसार 'स्थितप्रज्ञ' मनुष्य का मुख्य लक्षण क्या है?",
            questionEnglish = "According to Gita (2.56), what is the key characteristic of a 'Sthitaprajna' (one of steady wisdom)?",
            optionsHindi = listOf("अत्यधिक धन कमाना", "सुख और दुःख में समभाव रहना", "युद्ध से भाग जाना", "हमेशा मौन रहना"),
            optionsEnglish = listOf("Amassing wealth", "Remaining equipoised in sorrow and joy", "Fleeing battle", "Remaining strictly silent"),
            correctOptionIndex = 1,
            explanationHindi = "'दुःखेष्वनुद्विग्नमनाः सुखेषु विगतस्पृहः' - जो दुःखों में विचलित नहीं होता और सुखों में लिप्त नहीं होता, वह स्थितप्रज्ञ है।",
            explanationEnglish = "One whose mind remains unperturbed in grief and free from craving in pleasure is of steady wisdom (Gita 2.56).",
            categoryHindi = "सांख्ययोग",
            categoryEnglish = "Sankhya Yoga"
        ),
        QuizQuestion(
            id = 15,
            questionHindi = "भगवान श्रीकृष्ण ने वृक्षों में स्वयं को कौन-सा वृक्ष बताया है?",
            questionEnglish = "In Chapter 10 (Vibhuti Yoga), which tree did Lord Krishna declare Himself to be?",
            optionsHindi = listOf("वट (बरगद)", "अश्वत्थ (पीपल)", "कल्पवृक्ष", "चन्दन"),
            optionsEnglish = listOf("Banyan", "Ashvattha (Peepal)", "Kalpavriksha", "Sandalwood"),
            correctOptionIndex = 1,
            explanationHindi = "'अश्वत्थः सर्ववृक्षाणां' (10.26) - भगवान श्रीकृष्ण कहते हैं कि समस्त वृक्षों में मैं 'अश्वत्थ' (पीपल) का वृक्ष हूँ।",
            explanationEnglish = "'Ashvatthah sarva-vrikshanam' (10.26) - Krishna declares: 'Among all trees, know Me to be the sacred Ashvattha (Peepal)'.",
            categoryHindi = "विभूतियोग",
            categoryEnglish = "Divine Glories"
        ),
        QuizQuestion(
            id = 16,
            questionHindi = "गीता में 'योग' की सबसे प्रसिद्ध परिभाषा क्या दी गई है?",
            questionEnglish = "What is the most famous definition of 'Yoga' given in Gita (2.48)?",
            optionsHindi = listOf("समत्वं योग उच्यते (मन का समभाव)", "कठिन शारीरिक आसन", "आँख बंद करके बैठना", "संसार का त्याग"),
            optionsEnglish = listOf("Samatvam Yoga Uchyate (Equanimity of Mind)", "Difficult physical postures", "Sitting with eyes closed", "Renouncing the world"),
            correctOptionIndex = 0,
            explanationHindi = "'समत्वं योग उच्यते' (2.48) - सफलता और असफलता, सिद्धि और असिद्धि में समभाव (संतुलन) बनाए रखना ही सच्चा योग है।",
            explanationEnglish = "'Samatvam yoga uchyate' (2.48) - Equanimity of mind in both success and failure is called Yoga.",
            categoryHindi = "कर्मयोग",
            categoryEnglish = "Karma Yoga"
        ),
        QuizQuestion(
            id = 17,
            questionHindi = "कुरुक्षेत्र के मैदान में दोनों सेनाओं के बीच अर्जुन को रथ खड़ा करने का आदेश किसने दिया?",
            questionEnglish = "Who instructed the chariot to be placed in the middle of both armies at Kurukshetra?",
            optionsHindi = listOf("भगवान श्रीकृष्ण ने", "अर्जुन ने श्रीकृष्ण को", "भीष्म पितामह ने", "युधिष्ठिर ने"),
            optionsEnglish = listOf("Lord Krishna", "Arjuna to Lord Krishna", "Bhishma", "Yudhishthira"),
            correctOptionIndex = 1,
            explanationHindi = "अर्जुन ने कहा: 'सेनयोरुभयोर्मध्ये रथं स्थापय मेऽच्युत' - हे अच्युत! दोनों सेनाओं के बीच मेरे रथ को खड़ा कीजिए (1.21)।",
            explanationEnglish = "Arjuna requested Krishna: 'Place my chariot between the two armies, O Infallible One' (Gita 1.21).",
            categoryHindi = "गीता प्रसंग",
            categoryEnglish = "Gita Chronicles"
        ),
        QuizQuestion(
            id = 18,
            questionHindi = "भगवान श्रीकृष्ण ने गीता में अर्जुन को किस नाम से पुकारा है?",
            questionEnglish = "Which of the following sacred names did Lord Krishna use to address Arjuna in the Gita?",
            optionsHindi = listOf("पार्थ और कौन्तेय", "गुड़ाकेश (निद्रा को जीतने वाला)", "परन्तप (शत्रुतापी)", "उपरोक्त सभी"),
            optionsEnglish = listOf("Partha and Kaunteya", "Gudakesha (Conqueror of Sleep)", "Parantapa (Scorcher of Foes)", "All of the above"),
            correctOptionIndex = 3,
            explanationHindi = "श्रीकृष्ण ने अर्जुन को पार्थ, कौन्तेय, गुड़ाकेश, परन्तप, सव्यसाची और भारत जैसे कई गौरवशाली नामों से संबोधित किया।",
            explanationEnglish = "Krishna affectionately addressed Arjuna as Partha, Kaunteya, Gudakesha, Parantapa, Savyasachi, and Bharata.",
            categoryHindi = "महाभारत",
            categoryEnglish = "Mahabharata"
        ),
        QuizQuestion(
            id = 19,
            questionHindi = "गीता के अनुसार किस प्रकार का दान सात्विक दान माना जाता है?",
            questionEnglish = "According to Gita (17.20), which type of charity is considered Sattvic (pure)?",
            optionsHindi = listOf("जो प्रत्युपकार (बदले) की आशा के बिना योग्य पात्र को दिया जाए", "जो नाम कमाने के लिए दिया जाए", "जो अनिच्छा से दुःखी होकर दिया जाए", "जो किसी अयोग्य को अनुचित स्थान पर दिया जाए"),
            optionsEnglish = listOf("Given as a duty without expectation of return to a worthy person", "Given for fame or pride", "Given reluctantly with grief", "Given to an unworthy person at an improper place"),
            correctOptionIndex = 0,
            explanationHindi = "'दातव्यमिति यद्दानं दीयतेऽनुपकारिणे' (17.20) - देश, काल और पात्र देखकर बिना किसी स्वार्थ के दिया गया दान ही सात्विक है।",
            explanationEnglish = "Charity given purely out of duty, at a proper place and time to a deserving person without return expectation is Sattvic.",
            categoryHindi = "श्रद्धात्रय विभाग",
            categoryEnglish = "Threefold Faith"
        ),
        QuizQuestion(
            id = 20,
            questionHindi = "भगवान श्रीकृष्ण के अनुसार मनुष्य का सच्चा मित्र कौन है?",
            questionEnglish = "According to Gita (6.5), who is man's truest friend?",
            optionsHindi = listOf("उसका अपना वश में किया हुआ मन और आत्मा", "धन-दौलत", "शारीरिक बल", "सांसारिक संबंधी"),
            optionsEnglish = listOf("His own disciplined mind and soul", "Wealth", "Physical Strength", "Worldly relatives"),
            correctOptionIndex = 0,
            explanationHindi = "'आत्मैव ह्यात्मनो बन्धुरात्मैव रिपुरात्मनः' (6.5) - मनुष्य का अपना मन ही उसका सबसे बड़ा मित्र है और अनियंत्रित मन ही सबसे बड़ा शत्रु है।",
            explanationEnglish = "'Atmaiva hy atmano bandhur' (6.5) - For one who has conquered the mind, the mind is the best of friends.",
            categoryHindi = "ध्यानयोग",
            categoryEnglish = "Dhyana Yoga"
        )
    )

    fun getDailyQuestions(dayOfYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)): List<QuizQuestion> {
        val total = QUESTIONS_POOL.size
        val count = 5.coerceAtMost(total)
        val startIndex = ((dayOfYear * 5) % total).coerceAtLeast(0)
        val selected = mutableListOf<QuizQuestion>()
        for (i in 0 until count) {
            val index = (startIndex + i) % total
            selected.add(QUESTIONS_POOL[index])
        }
        return selected
    }
}
