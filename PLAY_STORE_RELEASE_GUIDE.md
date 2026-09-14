# 🚀 Google Play Store Release & Monetization Guide - Gita Saathi

यह गाइड **Gita Saathi** ऐप को Google Play Store पर सफलतापूर्वक पब्लिश करने, **Google Play In-App Billing (Subscriptions)** सेटअप करने, और **AdMob** को लाइव करने के लिए बनाई गई है।

---

## 📑 अनुक्रमणिका (Table of Contents)
1. [Google Play Console पर इन-ऐप सब्सक्रिप्शन (Gita Gold) सेटअप](#1-google-play-console-पर-इन-ऐप-सब्सक्रिप्शन-gita-gold-सेटअप)
2. [Sandbox / License Testing (फ्री टेस्टिंग खाता)](#2-sandbox--license-testing-फ्री-टेस्टिंग-खाता)
3. [AdMob प्रोडक्शन IDs सेटअप](#3-admob-प्रोडक्शन-ids-सेटअप)
4. [रिलीज़ की-स्टोर (Keystore) और App Bundle (.aab) जनरेट करना](#4-रिलीज़-की-स्टोर-keystore-और-app-bundle-aab-जनरेट-करना)
5. [ASO (App Store Optimization) - कॉपी-पेस्ट रेडी कंटेंट](#5-aso-app-store-optimization---कॉपी-पेस्ट-रेडी-कंटेन्ट)
6. [डेटा सेफ्टी और प्राइवेसी पॉलिसी (Data Safety & Privacy Policy)](#6-डेटा-सेफ्टी-और-प्राइवेसी-पॉलिसी)

---

## 1. Google Play Console पर इन-ऐप सब्सक्रिप्शन (Gita Gold) सेटअप

ऐप के कोड में Google Play Billing 7.x पहले से पूरी तरह इंटीग्रेट है। आपको Play Console पर केवल 2 प्रोडक्ट्स बनाने हैं:

1. **Google Play Console** में लॉगिन करें: [https://play.google.com/console](https://play.google.com/console)
2. अपनी ऐप **Gita Saathi** चुनें।
3. बायीं मेनू में: **Monetize with Play** ➔ **Products** ➔ **Subscriptions** पर क्लिक करें।
4. **Create subscription** पर क्लिक करें:

### 🔹 प्लान 1: मासिक (Monthly Subscription)
- **Product ID**: `gita_gold_monthly` *(हूबहू यही स्पेलिंग रखें)*
- **Name**: `Gita Saathi Gold - Monthly`
- **Description**: `100% Ad-Free spiritual experience, peaceful audio listening, and Dharma seva support.`
- **Base Plan**:
  - **Base Plan ID**: `monthly-base-plan`
  - **Type**: `Auto-renewing`
  - **Billing period**: `1 month`
  - **Grace period**: `7 days` (अनुशंसित)
  - **Price (India)**: `₹49.00 INR` (अन्य देशों के लिए Google स्वतः कन्वर्ट करेगा)
- **Save & Activate** पर क्लिक करें।

### 🔹 प्लान 2: वार्षिक (Yearly Subscription)
- **Product ID**: `gita_gold_yearly` *(हूबहू यही स्पेलिंग रखें)*
- **Name**: `Gita Saathi Gold - Yearly`
- **Description**: `100% Ad-Free spiritual reading & audio with 32% annual savings.`
- **Base Plan**:
  - **Base Plan ID**: `yearly-base-plan`
  - **Type**: `Auto-renewing`
  - **Billing period**: `1 year`
  - **Grace period**: `16 days` (अनुशंसित)
  - **Price (India)**: `₹399.00 INR`
- **Save & Activate** पर क्लिक करें।

---

## 2. Sandbox / License Testing (फ्री टेस्टिंग खाता)

अपने मोबाइल पर बिना असली पैसे कटे पेमेंट टेस्ट करने के लिए:
1. Play Console के होम पेज पर जाएँ ➔ **Settings** ➔ **License Testing** (या **Developer account** ➔ **Account details** ➔ **License testing**)।
2. अपना Gmail ID (जो आपके फोन के Play Store में लॉगिन है) यहाँ जोड़ें।
3. **License response**: `RESPOND_NORMALLY` चुनें।
4. अब जब आप ऐप में ₹49 या ₹399 वाले बटन पर क्लिक करेंगे, तो Google Play एक "Test Instrument (Always Approves)" का ऑप्शन दिखाएगा, जिससे ₹0 में खरीदारी सफलतापूर्वक टेस्ट हो जाएगी।

---

## 3. AdMob प्रोडक्शन IDs सेटअप

जब आप AdMob पर अपने असली विज्ञापन यूनिट्स बनाएँ:
1. [AdMob Console](https://apps.admob.com) पर जाएँ।
2. अपनी ऐप जोड़ें (Android) और दो Ad Units बनाएँ:
   - **Adaptive Banner Ad Unit**
   - **Interstitial Ad Unit**
3. `app/build.gradle.kts` में debug और release के लिए dynamic placeholders पहले से सेट हैं:
   - **Debug Builds**: अपने आप Google के ऑफिशियल Test IDs इस्तेमाल करती हैं (ताकि AdMob आपका अकाउंट बैन न करे)।
   - **Release Build**: असली AdMob App ID को आप Windows Environment Variable के माध्यम से दे सकते हैं:
     ```powershell
     $env:ADMOB_APP_ID = "ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"
     ```
   - या `local.properties` में अपनी असली Ad Unit IDs रख सकते हैं।

---

## 4. रिलीज़ की-स्टोर (Keystore) और App Bundle (.aab) जनरेट करना

Google Play Store पर APK अपलोड नहीं होता, बल्कि **.aab (Android App Bundle)** अपलोड होता है।

### स्टेप 1: की-स्टोर (Release Keystore) बनाएँ
PowerShell खोलें और यह कमांड चलाएँ (पासवर्ड याद रखें या सुरक्षित जगह लिख लें):

```powershell
keytool -genkey -v -keystore gita-saathi-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias gitasaathi
```

*(यह आपसे आपका नाम, संस्था और एक सुरक्षित पासवर्ड पूछेगा। यह फ़ाइल आपके प्रोजेक्ट फोल्डर में `gita-saathi-release.jks` नाम से बन जाएगी।)*

### स्टेप 2: Release App Bundle (.aab) बनाएँ

Android Studio के मेनू से:
1. **Build** ➔ **Generate Signed Bundle / APK...**
2. **Android App Bundle** चुनें ➔ **Next**
3. अपनी `gita-saathi-release.jks` फ़ाइल चुनें, पासवर्ड और alias (`gitasaathi`) भरें।
4. **release** वेरिएंट चुनें और **Create** दबाएँ।

या टर्मिनल से डायरेक्ट कमांड:
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat bundleRelease
```

तैयार फ़ाइल यहाँ मिलेगी:
📁 `app\build\outputs\bundle\release\app-release.aab`

---

## 5. ASO (App Store Optimization) - कॉपी-पेस्ट रेडी कंटेन्ट

### 🏷️ ऐप टाइटल (App Title - 30 Chars Max):
```
Gita Saathi: Bhagavad Gita AI
```

### 📝 शॉर्ट डिस्क्रिप्शन (Short Description - 80 Chars Max):
```
श्रीमद्भगवद्गीता हिंदी श्लोक, शांत ऑडियो, AI कृष्ण मार्गदर्शन व डिजिटल जप माला।
```
*(English Variant: Shrimad Bhagavad Gita with Hindi & English audio, AI guidance & Japa Mala.)*

### 📖 फुल डिस्क्रिप्शन (Full Description - Play Store Ready):
```markdown
🌸 ॐ श्री परमात्मने नमः 🌸

श्रीमद्भगवद्गीता का दिव्य ज्ञान अब आपकी हथेली में — सरल, सुलभ और आधुनिक अनुभव के साथ। "गीता साथी" केवल एक पुस्तक नहीं, बल्कि आपके दैनिक जीवन में शांति, धर्म और सही निर्णय लेने का आपका व्यक्तिगत आध्यात्मिक मार्गदर्शक है।

✨ गीता साथी की मुख्य विशेषताएं:

📖 संपूर्ण १८ अध्याय और ७०० श्लोक:
- प्रत्येक श्लोक का मूल संस्कृत पाठ, शुद्ध उच्चारण, सरल हिंदी एवं अंग्रेजी अनुवाद।
- प्रत्येक अध्याय का सार और जीवन में उसका व्यावहारिक उपयोग (Real-life Application)।

🎧 शांत और मधुर ऑडियो पाठ:
- श्लोकों का शांत, मनमोहक गायन और अर्थ श्रवण।
- ध्यान और मानसिक शांति के लिए बैकग्राउंड प्लेबैक व स्लीप टाइमर।

🤖 AI गीता मित्र (Divya Margdarshan):
- क्या आप जीवन में तनाव, भ्रम, करियर या रिश्तों की दुविधा में हैं?
- अपनी समस्या लिखें और भगवान श्री कृष्ण द्वारा गीता में दिए गए उपदेशों के आधार पर सटीक और सांत्वनादायक मार्गदर्शन प्राप्त करें।

📿 डिजिटल जप माला एवं साधना ट्रैकर:
- 108 मनकों वाली सुंदर जप माला जिसमें सूक्ष्म कंपन (Haptic Feedback) और ध्वनि है।
- अपनी दैनिक साधना, निरंतरता (Streaks) और श्लोक पठन को ट्रैक करें।

📱 गीता शॉर्ट्स (Spiritual Reels):
- छोटे, ज्ञानवर्धक वीडियो और प्रेरणादायक श्लोक रील्स के माध्यम से अपनी दिनचर्या में सकारात्मक ऊर्जा भरें।

👑 गीता साथी Gold (विज्ञापन-मुक्त सेवा):
- बिना किसी रुकावट के १००% विज्ञापन-मुक्त पवित्र अध्ययन का अनुभव।

🌿 क्यों चुनें गीता साथी?
- सात्विक, सुंदर और आँखों के लिए आरामदायक इंटरफेस (डार्क/गोल्डन मोड)।
- ऑफलाइन सुविधा — इंटरनेट के बिना भी पढ़ें अपने पसंदीदा श्लोक।
- परिवार और मित्रों के साथ सुंदर सुविचार व श्लोक कार्ड शेयर करने की सुविधा।

आज ही "गीता साथी" डाउनलोड करें और अपने जीवन को भागवत चेतना व आंतरिक शांति से आलोकित करें।

जय श्री कृष्णा! 🙏
```

---

## 6. डेटा सेफ्टी और प्राइवेसी पॉलिसी

Google Play Store पर ऐप सबमिट करते समय Data Safety सेक्शन में यह भरें:

1. **डेटा संग्रह (Data Collection)**:
   - **Personal Info**: कोई व्यक्तिगत डेटा (नाम, फोन नंबर, पता) सर्वर पर स्टोर नहीं किया जाता।
   - **Financial Info**: सभी लेन-देन Google Play के सुरक्षित बिलिंग सिस्टम द्वारा प्रोसेस होते हैं, ऐप कोई कार्ड या बैंक विवरण स्टोर नहीं करता।
   - **App Info and Performance**: क्रैश लॉग्स और डायग्नोस्टिक्स (Google Firebase/Play Console)।
   - **Device or other identifiers**: AdMob एडवरटाइजिंग आईडी (विज्ञापन दिखाने के लिए, Gold यूज़र्स के लिए बंद)।

2. **प्राइवेसी पॉलिसी (Privacy Policy URL)**:
   - Google Play Console पर प्राइवेसी पॉलिसी लिंक देना अनिवार्य है।
   - आप GitHub Pages या Notion / Google Sites पर एक फ्री पेज बनाकर उसका लिंक Play Console में डाल सकते हैं।

---

## 7. Firebase Console सेटअप (FCM, Crashlytics, Analytics & Remote Config)

### स्टेप 1: Firebase Project और `google-services.json`
1. [Firebase Console](https://console.firebase.google.com/) खोलें और **Add Project** दबाएँ (नाम: **Gita Saathi**).
2. **Google Analytics** को Enable रखें (100% Free).
3. Project Overview में **Android** आइकन (➕) पर क्लिक करें:
   - **Android package name**: `com.nkapps.gitasaathi`
   - **App nickname**: `Gita Saathi`
   - **Register app** दबाएँ।
4. **`google-services.json`** फ़ाइल डाउनलोड करें और उसे अपने प्रोजेक्ट के `gita-saathi/app/` फ़ोल्डर में पेस्ट कर दें।
5. बस! ऐप के सारे 6 Firebase फीचर्स अपने-आप एक्टिवेट हो जाएँगे।

### स्टेप 2: Push Notifications भेजना (FCM)
1. Firebase Console ➔ बायीं मेनू में **Engage** ➔ **Messaging** (Firebase Cloud Messaging) पर जाएँ।
2. **New campaign** ➔ **Notifications** चुनें।
3. **Notification Title**: `🌸 आज का पावन गीता श्लोक`
4. **Notification Text**: `कर्मण्येवाधिकारस्ते मा फलेषु कदाचन...`
5. **Target**: **Topic** चुनें ➔ `daily_shloka` (ऐप स्वतः सभी यूज़र्स को इस टॉपिक पर सब्सक्राइब कर देती है)।
6. **Review & Publish** दबाएँ — सभी यूज़र्स को तुरंत नोटिफिकेशन पहुँच जाएगा!

### स्टेप 3: Crashlytics और Real-time Crashes देखना
- Firebase Console ➔ **Release & Monitor** ➔ **Crashlytics** पर जाएँ।
- अगर किसी भी डिवाइस पर कोई एरर या क्रैश आता है, तो यहाँ सटीक लाइन नंबर और डिवाइस मॉडल के साथ पूरी रिपोर्ट दिखेगी।

### स्टेप 4: Remote Config (बिना अपडेट डाले सेटिंग्स बदलना)
- Firebase Console ➔ **Engage** ➔ **Remote Config** पर जाएँ।
- यहाँ आप निम्नलिखित पैरामीटर कभी भी बदल सकते हैं:
  - `show_festival_banner`: `true` / `false`
  - `festival_banner_title`: जैसे *"🌸 जन्माष्टमी महापर्व की हार्दिक शुभकामनाएं"*
  - `daily_quiz_enabled`: `true` / `false`
  - `gold_promo_discount`: *"30%"*

---

✅ **सब कुछ तैयार है! आप सीधे Android Studio से `bundleRelease` बनाकर Google Play Console पर Internal Testing या Production में अपलोड कर सकते हैं।**
