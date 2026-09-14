package com.nkapps.gitasaathi.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class UserManager private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gita_saathi_user_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfileFromPrefs())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val profileMutex = Mutex()

    init {
        // Update last active on startup
        if (_userProfile.value.isRegistered) {
            updateLastActive()
        }
    }

    private fun loadProfileFromPrefs(): UserProfile {
        val isRegistered = prefs.getBoolean(KEY_IS_REGISTERED, false)
        val userId = prefs.getString(KEY_USER_ID, "") ?: ""
        val displayName = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val photoUrl = prefs.getString(KEY_PHOTO_URL, null)
        val registeredAt = prefs.getLong(KEY_REGISTERED_AT, System.currentTimeMillis())
        val lastActiveAt = prefs.getLong(KEY_LAST_ACTIVE_AT, System.currentTimeMillis())
        val versesReadCount = prefs.getInt(KEY_VERSES_READ_COUNT, 0)
        val bookmarksCount = prefs.getInt(KEY_BOOKMARKS_COUNT, 0)
        val isCloudSynced = prefs.getBoolean(KEY_CLOUD_SYNCED, false)

        return UserProfile(
            userId = if (userId.isEmpty()) "user_" + UUID.randomUUID().toString().take(8) else userId,
            displayName = displayName,
            email = email,
            photoUrl = photoUrl,
            registeredAt = registeredAt,
            lastActiveAt = lastActiveAt,
            versesReadCount = versesReadCount,
            bookmarksCount = bookmarksCount,
            isRegistered = isRegistered,
            isCloudSynced = isCloudSynced
        )
    }

    private fun saveProfileToPrefs(profile: UserProfile) {
        prefs.edit()
            .putBoolean(KEY_IS_REGISTERED, profile.isRegistered)
            .putString(KEY_USER_ID, profile.userId)
            .putString(KEY_DISPLAY_NAME, profile.displayName)
            .putString(KEY_EMAIL, profile.email)
            .putString(KEY_PHOTO_URL, profile.photoUrl)
            .putLong(KEY_REGISTERED_AT, profile.registeredAt)
            .putLong(KEY_LAST_ACTIVE_AT, profile.lastActiveAt)
            .putInt(KEY_VERSES_READ_COUNT, profile.versesReadCount)
            .putInt(KEY_BOOKMARKS_COUNT, profile.bookmarksCount)
            .putBoolean(KEY_CLOUD_SYNCED, profile.isCloudSynced)
            .apply()
    }

    fun registerOrUpdateUser(
        displayName: String,
        email: String,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        registerOrUpdateUser(displayName, email, null, null, onComplete)
    }

    fun registerOrUpdateUser(
        displayName: String,
        email: String,
        photoUrl: String? = null,
        customUserId: String? = null,
        onComplete: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        scope.launch {
            profileMutex.withLock {
                val current = _userProfile.value
                val userId = customUserId?.takeIf { it.isNotBlank() }
                    ?: if (current.userId.isNotBlank()) current.userId else "user_" + UUID.randomUUID().toString().take(8)
                val registeredAt = if (current.isRegistered && current.registeredAt > 0) current.registeredAt else System.currentTimeMillis()

                val updatedProfile = current.copy(
                    userId = userId,
                    displayName = displayName.trim(),
                    email = email.trim(),
                    photoUrl = photoUrl ?: current.photoUrl,
                    registeredAt = registeredAt,
                    lastActiveAt = System.currentTimeMillis(),
                    isRegistered = true
                )

                saveProfileToPrefs(updatedProfile)
                _userProfile.value = updatedProfile
                com.nkapps.gitasaathi.firebase.GitaCrashlytics.setUserId(userId)

                // Sync to Firebase Firestore in background
                syncToCloud(updatedProfile) { success, errorMsg ->
                    if (success) {
                        scope.launch {
                            profileMutex.withLock {
                                val currentSynced = _userProfile.value
                                val synced = currentSynced.copy(isCloudSynced = true)
                                saveProfileToPrefs(synced)
                                _userProfile.value = synced
                            }
                        }
                    }
                    onComplete(true, errorMsg)
                }
            }
        }
    }

    fun updateStats(versesRead: Int, bookmarks: Int) {
        scope.launch {
            profileMutex.withLock {
                val current = _userProfile.value
                val updated = current.copy(
                    versesReadCount = versesRead,
                    bookmarksCount = bookmarks,
                    lastActiveAt = System.currentTimeMillis()
                )
                saveProfileToPrefs(updated)
                _userProfile.value = updated

                if (updated.isRegistered) {
                    syncToCloud(updated) { _, _ -> }
                }
            }
        }
    }

    private fun updateLastActive() {
        scope.launch {
            profileMutex.withLock {
                val current = _userProfile.value
                val updated = current.copy(lastActiveAt = System.currentTimeMillis())
                saveProfileToPrefs(updated)
                _userProfile.value = updated

                syncToCloud(updated) { _, _ -> }
            }
        }
    }

    fun logout() {
        scope.launch {
            profileMutex.withLock {
                prefs.edit().clear().apply()
                _userProfile.value = UserProfile(
                    userId = "user_" + UUID.randomUUID().toString().take(8),
                    isRegistered = false
                )
                try {
                    FirebaseAuth.getInstance().signOut()
                } catch (e: Exception) {
                    Log.w("UserManager", "Firebase Auth sign out ignored: ${e.message}")
                }
            }
        }
    }

    private fun syncToCloud(profile: UserProfile, callback: (Boolean, String?) -> Unit) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                Log.d("UserManager", "Firebase not configured on device; profile stored locally.")
                callback(false, "Firebase not configured on device")
                return
            }

            val db = FirebaseFirestore.getInstance()
            val userMap = hashMapOf(
                "userId" to profile.userId,
                "displayName" to profile.displayName,
                "email" to profile.email,
                "registeredAt" to profile.registeredAt,
                "lastActiveAt" to profile.lastActiveAt,
                "versesReadCount" to profile.versesReadCount,
                "bookmarksCount" to profile.bookmarksCount,
                "appVersion" to "1.0",
                "platform" to "Android"
            )

            db.collection("users")
                .document(profile.userId)
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("UserManager", "Successfully synced user profile to Firestore: ${profile.email}")
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    Log.w("UserManager", "Failed to sync to Firestore: ${e.message}")
                    callback(false, e.localizedMessage)
                }
        } catch (e: Exception) {
            Log.w("UserManager", "Firestore sync exception: ${e.message}")
            callback(false, e.localizedMessage)
        }
    }

    companion object {
        private const val KEY_IS_REGISTERED = "key_is_registered"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_DISPLAY_NAME = "key_display_name"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_PHOTO_URL = "key_photo_url"
        private const val KEY_REGISTERED_AT = "key_registered_at"
        private const val KEY_LAST_ACTIVE_AT = "key_last_active_at"
        private const val KEY_VERSES_READ_COUNT = "key_verses_read_count"
        private const val KEY_BOOKMARKS_COUNT = "key_bookmarks_count"
        private const val KEY_CLOUD_SYNCED = "key_cloud_synced"

        @Volatile
        private var INSTANCE: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
