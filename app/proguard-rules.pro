# ===================================================================
# Gita Saathi - Production ProGuard & R8 Configuration Rules
# ===================================================================

# 1. Stack Traces & Debugging Information (For Firebase Crashlytics)
-keepattributes SourceFile,LineNumberTable,*Annotation*
-renamesourcefileattribute SourceFile

# 2. Room Database Entities & DAOs
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public void <init>();
    <fields>;
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Database class * { *; }
-dontwarn androidx.room.paging.**

# 3. Data Models & Entities (Ensure fields are not renamed or stripped)
-keep class com.nkapps.gitasaathi.data.** { *; }
-keepclassmembers class com.nkapps.gitasaathi.data.** { *; }

# 4. Moshi & JSON Serialization (Network Models)
-keep class com.nkapps.gitasaathi.network.** { *; }
-keepclassmembers class com.nkapps.gitasaathi.network.** { *; }
-keep class com.squareup.moshi.** { *; }
-keepclassmembers class com.squareup.moshi.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter { *; }
-keep @com.squareup.moshi.JsonQualifier interface * { *; }
-dontwarn com.squareup.moshi.**

# 5. Retrofit & OkHttp
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**

# 6. Media3 / ExoPlayer (Audio & Video Engines)
-keep class androidx.media3.** { *; }
-keepclassmembers class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# 7. Google Play Billing Client (Gita Gold Subscription)
-keep class com.android.billingclient.api.** { *; }
-keepclassmembers class com.android.billingclient.api.** { *; }
-keep class com.nkapps.gitasaathi.billing.** { *; }
-keepclassmembers class com.nkapps.gitasaathi.billing.** { *; }

# 8. Google Mobile Ads (AdMob)
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep public class com.google.ads.** {
   public *;
}

# 9. Firebase Services (FCM, Analytics, Crashlytics, App Check)
-keepattributes *Annotation*,InnerClasses,EnclosingMethod
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }
-keep class com.nkapps.gitasaathi.notifications.** { *; }
-keepclassmembers class com.nkapps.gitasaathi.notifications.** { *; }

# 10. Kotlin Coroutines & Flow
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

