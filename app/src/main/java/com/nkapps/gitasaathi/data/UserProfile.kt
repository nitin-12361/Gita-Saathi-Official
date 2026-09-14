package com.nkapps.gitasaathi.data

data class UserProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val registeredAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val versesReadCount: Int = 0,
    val bookmarksCount: Int = 0,
    val isRegistered: Boolean = false,
    val isCloudSynced: Boolean = false
)
