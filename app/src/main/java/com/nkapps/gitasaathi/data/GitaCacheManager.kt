package com.nkapps.gitasaathi.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object GitaCacheManager {

    // 700 MB max hard limit for video shorts cache (stores ~250-300 videos offline)
    const val MAX_VIDEO_CACHE_BYTES = 700 * 1024 * 1024L

    // 30 MB max hard limit for audio shlokas cache (~150-200 shlokas)
    const val MAX_AUDIO_CACHE_BYTES = 30 * 1024 * 1024L

    /**
     * Proactively cleans legacy multi-version cache folders, orphan temp files,
     * and trims audio/wallpaper caches so total storage stays tightly controlled.
     */
    fun performStartupCleanup(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cacheDir = context.cacheDir ?: return@launch

                // 1. Delete all legacy video cache folders from older builds (v1, v2, v3, etc.)
                val legacyDirs = listOf(
                    "gita_video_cache",
                    "gita_video_cache_v1",
                    "gita_video_cache_v2",
                    "gita_video_cache_v3",
                    "video_cache",
                    "asset_videos"
                )
                for (dirName in legacyDirs) {
                    val dir = File(cacheDir, dirName)
                    if (dir.exists()) {
                        dir.deleteRecursively()
                    }
                }

                // 2. Delete all loose tmp, wav, mp3 files in root cacheDir
                cacheDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        val name = file.name.lowercase()
                        if (name.endsWith(".tmp") || name.startsWith("shloka_tmp") ||
                            name.startsWith("shloka_audio_") || name.startsWith("gemini_")
                        ) {
                            file.delete()
                        }
                    }
                }

                // 3. Clean shared wallpapers (keep only the latest one)
                val wallpapersDir = File(cacheDir, "shared_wallpapers")
                if (wallpapersDir.exists()) {
                    val files = wallpapersDir.listFiles()?.sortedByDescending { it.lastModified() }
                    files?.drop(1)?.forEach { it.delete() }
                }

                // 4. Prune audio cache if exceeding limit
                pruneAudioCache(context)
            } catch (_: Exception) {}
        }
    }

    /**
     * Prunes audio cache folder using LRU (deletes oldest files first)
     */
    fun pruneAudioCache(context: Context) {
        try {
            val audioDir = File(context.cacheDir, "shloka_audio")
            if (!audioDir.exists()) return

            val files = audioDir.listFiles() ?: return
            var totalSize = files.sumOf { it.length() }

            if (totalSize > MAX_AUDIO_CACHE_BYTES) {
                val sortedFiles = files.sortedBy { it.lastModified() }
                for (file in sortedFiles) {
                    val len = file.length()
                    if (file.delete()) {
                        totalSize -= len
                    }
                    if (totalSize <= MAX_AUDIO_CACHE_BYTES * 0.7) {
                        break
                    }
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Returns the formatted total cache size in MB or KB (e.g., "35.2 MB")
     */
    fun getFormattedCacheSize(context: Context): String {
        return try {
            val bytes = getDirSize(context.cacheDir)
            val mb = bytes / (1024.0 * 1024.0)
            if (mb < 1.0) {
                String.format(java.util.Locale.US, "%.1f KB", bytes / 1024.0)
            } else {
                String.format(java.util.Locale.US, "%.1f MB", mb)
            }
        } catch (_: Exception) {
            "0 MB"
        }
    }

    /**
     * Clears all cache files and re-initializes clean directories.
     */
    fun clearAllCache(context: Context) {
        try {
            GitaExoPlayerManager.releaseCache()
            context.cacheDir?.listFiles()?.forEach { file ->
                file.deleteRecursively()
            }
        } catch (_: Exception) {}
    }

    private fun getDirSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) getDirSize(file) else file.length()
        }
        return size
    }
}
