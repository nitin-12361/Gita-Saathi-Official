package com.nkapps.gitasaathi.data

import android.content.Context
import android.net.Uri
import java.io.File

object GitaVideoResolver {

    const val HUGGING_FACE_BASE_URL = "https://huggingface.co/datasets/nitinkoli12361/gita-saathi-videos/resolve/main"

    /**
     * Resolves the video URI for a given verse and language.
     * Checks in this order:
     * 1. res/raw/ resource (e.g. hindi_1_1, c1_v1_hi, english_1_1, c1_v1_en)
     * 2. assets/videos/{lang}/{chapter}_{verse}.mp4 (copied to cache for VideoView)
     * 3. Ultra-Fast Hugging Face Cloudflare CDN Streaming (nitinkoli12361/gita-saathi-videos)
     */
    fun getVideoUri(context: Context, chapterId: Int, verseId: Int, language: AppLanguage): Uri? {
        val langPrefix = if (language == AppLanguage.HINDI) "hindi" else "english"
        val langShort = if (language == AppLanguage.HINDI) "hi" else "en"

        // 1. Check local raw resources: hindi_1_1, c1_v1_hi, ch1_v1_hindi, etc.
        val rawNames = listOf(
            "${langPrefix}_${chapterId}_${verseId}",
            "c${chapterId}_v${verseId}_${langShort}",
            "ch${chapterId}_v${verseId}_${langPrefix}",
            "verse_${chapterId}_${verseId}_${langShort}"
        )

        for (name in rawNames) {
            val resId = context.resources.getIdentifier(name, "raw", context.packageName)
            if (resId != 0) {
                return Uri.parse("android.resource://${context.packageName}/$resId")
            }
        }

        // 2. Check local assets folder: videos/hindi/1_1.mp4 or videos/1_1_hi.mp4
        val assetPaths = listOf(
            "videos/$langPrefix/${chapterId}_${verseId}.mp4",
            "videos/${chapterId}_${verseId}_$langShort.mp4",
            "videos/c${chapterId}_v${verseId}_$langShort.mp4"
        )

        for (assetPath in assetPaths) {
            try {
                context.assets.open(assetPath).use {
                    val cachedFile = File(context.cacheDir, "asset_videos/${assetPath.replace('/', '_')}")
                    if (!cachedFile.exists() || cachedFile.length() == 0L) {
                        cachedFile.parentFile?.mkdirs()
                        cachedFile.outputStream().use { out ->
                            context.assets.open(assetPath).copyTo(out)
                        }
                    }
                    return Uri.fromFile(cachedFile)
                }
            } catch (_: Exception) {
                // Not in assets, continue
            }
        }

        // 3. Ultra-Fast Hugging Face Cloudflare CDN (Direct MP4 Stream)
        val huggingFaceUrl = "$HUGGING_FACE_BASE_URL/${langPrefix}_${chapterId}_${verseId}.mp4"
        return Uri.parse(huggingFaceUrl)
    }

    fun hasCustomVideo(context: Context, chapterId: Int, verseId: Int, language: AppLanguage): Boolean {
        return getVideoUri(context, chapterId, verseId, language) != null
    }
}
