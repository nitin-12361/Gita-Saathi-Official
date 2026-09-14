package com.nkapps.gitasaathi.utils

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.nkapps.gitasaathi.data.GitaWallpaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class WallpaperTarget {
    HOME_SCREEN,
    LOCK_SCREEN,
    BOTH
}

object WallpaperHelper {

    suspend fun setPhoneWallpaper(
        context: Context,
        bitmap: Bitmap,
        target: WallpaperTarget
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val wallpaperManager = WallpaperManager.getInstance(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val flag = when (target) {
                    WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
                    WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
                    WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wallpaperManager.setBitmap(bitmap, null, true, flag)
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            Unit
        }
    }

    suspend fun saveToGallery(
        context: Context,
        bitmap: Bitmap,
        title: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanTitle = title.replace(Regex("[^a-zA-Z0-9_]"), "_").take(30)
            val filename = "Gita_Krishna_${cleanTitle}_${System.currentTimeMillis()}.jpg"

            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/GitaSaathi")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IllegalStateException("Failed to create MediaStore entry")

            resolver.openOutputStream(imageUri)?.use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IllegalStateException("Failed to compress bitmap into JPEG")
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }

            imageUri
        }
    }

    fun shareWallpaper(
        context: Context,
        bitmap: Bitmap,
        title: String,
        shlokaText: String
    ): Result<Unit> = runCatching {
        val cacheDir = File(context.cacheDir, "shared_wallpapers")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        try { cacheDir.listFiles()?.forEach { it.delete() } } catch (_: Exception) {}
        val imageFile = File(cacheDir, "gita_wallpaper_share.jpg")
        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(
                Intent.EXTRA_TEXT,
                "॥ श्री कृष्णाय नमः ॥\n\n$title\n$shlokaText\n\n— श्रीमद्भगवद्गीता (Gita Saathi App)"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Krishna Wallpaper")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun renderWallpaperBitmap(
        context: Context,
        wallpaper: GitaWallpaper,
        includeQuote: Boolean
    ): Bitmap {
        val original = BitmapFactory.decodeResource(context.resources, wallpaper.drawableResId)
            ?: Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)

        if (!includeQuote) {
            return original
        }

        // Create mutable copy to draw sacred shloka typography
        val width = original.width
        val height = original.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // Draw background wallpaper
        canvas.drawBitmap(original, 0f, 0f, null)

        // Draw elegant gradient vignette scrim at the bottom for high contrast readability
        val scrimHeight = height * 0.38f
        val scrimPaint = Paint().apply {
            shader = LinearGradient(
                0f, height - scrimHeight,
                0f, height.toFloat(),
                intArrayOf(Color.TRANSPARENT, Color.argb(180, 10, 5, 0), Color.argb(235, 15, 8, 2)),
                floatArrayOf(0f, 0.4f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, height - scrimHeight, width.toFloat(), height.toFloat(), scrimPaint)

        // Draw golden accent line
        val linePaint = Paint().apply {
            color = Color.parseColor("#E5A93C")
            strokeWidth = width * 0.004f
            isAntiAlias = true
        }
        val lineY = height - (scrimHeight * 0.85f)
        val lineMargin = width * 0.15f
        canvas.drawLine(lineMargin, lineY, width - lineMargin, lineY, linePaint)

        // Draw Reference Pill / Header
        val refPaint = Paint().apply {
            color = Color.parseColor("#FBD38D")
            textSize = width * 0.035f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("॥ ${wallpaper.shlokaReference} ॥", width / 2f, lineY + (width * 0.05f), refPaint)

        // Draw Sanskrit Shloka
        val shlokaPaint = Paint().apply {
            color = Color.WHITE
            textSize = width * 0.042f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
            setShadowLayer(6f, 0f, 2f, Color.BLACK)
        }

        val shlokaLines = wallpaper.shlokaSanskrit.split("\n")
        var currentY = lineY + (width * 0.12f)
        for (line in shlokaLines) {
            canvas.drawText(line.trim(), width / 2f, currentY, shlokaPaint)
            currentY += (width * 0.058f)
        }

        // Draw Hindi Meaning
        val meaningPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = width * 0.030f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
            setShadowLayer(4f, 0f, 1f, Color.BLACK)
        }

        currentY += (width * 0.02f)
        val words = wallpaper.shlokaMeaningHindi.split(" ")
        val line1 = StringBuilder()
        val line2 = StringBuilder()
        for (w in words) {
            if (line1.length < 40) {
                line1.append(w).append(" ")
            } else {
                line2.append(w).append(" ")
            }
        }
        if (line1.isNotEmpty()) {
            canvas.drawText(line1.toString().trim(), width / 2f, currentY, meaningPaint)
            currentY += (width * 0.042f)
        }
        if (line2.isNotEmpty()) {
            canvas.drawText(line2.toString().trim(), width / 2f, currentY, meaningPaint)
        }

        return output
    }
}
