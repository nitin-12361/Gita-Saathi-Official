package com.nkapps.gitasaathi.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.content.FileProvider
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object GitaCardShareHelper {

    suspend fun generateAndShareVerseCard(
        context: Context,
        verse: Verse,
        appLanguage: AppLanguage,
        isStoryFormat: Boolean = true // true for 9:16 Story, false for 1:1 Post
    ) = withContext(Dispatchers.IO) {
        try {
            val width = 1080
            val height = if (isStoryFormat) 1920 else 1080
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // 1. Background Gradient (Deep Cosmic Saffron to Dark Golden Brown)
            val bgPaint = Paint().apply {
                isAntiAlias = true
                shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    intArrayOf(
                        Color.rgb(28, 16, 8),    // Top Dark Saffron-Brown
                        Color.rgb(42, 22, 10),   // Mid Deep Maroon-Gold
                        Color.rgb(18, 10, 6)     // Bottom Dark
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            // 2. Divine Decorative Borders
            val borderPaint = Paint().apply {
                color = Color.argb(80, 255, 183, 77) // Soft Gold
                style = Paint.Style.STROKE
                strokeWidth = 4f
                isAntiAlias = true
            }
            val margin = 50f
            canvas.drawRoundRect(
                RectF(margin, margin, width - margin, height - margin),
                36f, 36f, borderPaint
            )

            val innerMargin = 64f
            val innerBorderPaint = Paint().apply {
                color = Color.argb(40, 255, 213, 79)
                style = Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }
            canvas.drawRoundRect(
                RectF(innerMargin, innerMargin, width - innerMargin, height - innerMargin),
                28f, 28f, innerBorderPaint
            )

            // 3. Om Emblem Header
            val omPaint = Paint().apply {
                color = Color.rgb(255, 179, 0) // Radiant Gold
                textSize = if (isStoryFormat) 90f else 75f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            var currentY = if (isStoryFormat) 200f else 140f
            canvas.drawText("ॐ", width / 2f, currentY, omPaint)

            // 4. Header Titles
            val headerPaint = Paint().apply {
                color = Color.rgb(255, 224, 130)
                textSize = if (isStoryFormat) 38f else 32f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            currentY += 60f
            canvas.drawText("श्रीमद्भगवद्गीता • SRIMAD BHAGAVAD GITA", width / 2f, currentY, headerPaint)

            // 5. Verse Reference Badge Box
            val badgeBoxPaint = Paint().apply {
                color = Color.argb(90, 230, 126, 34)
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            val badgeText = if (appLanguage == AppLanguage.HINDI)
                "अध्याय ${verse.chapterId}, श्लोक ${verse.verseId}"
            else
                "Chapter ${verse.chapterId}, Verse ${verse.verseId}"

            val badgeTextPaint = Paint().apply {
                color = Color.rgb(255, 248, 225)
                textSize = 34f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            currentY += 65f
            val badgeWidth = badgeTextPaint.measureText(badgeText) + 60f
            val badgeRect = RectF(
                (width - badgeWidth) / 2f,
                currentY - 40f,
                (width + badgeWidth) / 2f,
                currentY + 20f
            )
            canvas.drawRoundRect(badgeRect, 30f, 30f, badgeBoxPaint)
            canvas.drawText(badgeText, width / 2f, currentY, badgeTextPaint)

            // 6. Sanskrit Shloka Card
            currentY += 80f
            val contentWidth = width - 180
            val cardRect = RectF(
                90f,
                currentY,
                width - 90f,
                if (isStoryFormat) currentY + 420f else currentY + 300f
            )
            val cardBgPaint = Paint().apply {
                color = Color.argb(120, 48, 28, 16)
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawRoundRect(cardRect, 28f, 28f, cardBgPaint)

            val cardBorderPaint = Paint().apply {
                color = Color.argb(80, 255, 167, 38)
                style = Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }
            canvas.drawRoundRect(cardRect, 28f, 28f, cardBorderPaint)

            // Draw Sanskrit Text inside card
            val sanskritPaint = TextPaint().apply {
                color = Color.rgb(255, 224, 130)
                textSize = if (isStoryFormat) 44f else 38f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                isAntiAlias = true
            }
            val sanskritLayout = StaticLayout.Builder.obtain(
                verse.shlokaSanskrit,
                0,
                verse.shlokaSanskrit.length,
                sanskritPaint,
                contentWidth - 40
            ).setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(12f, 1f)
                .build()

            canvas.save()
            canvas.translate(110f, currentY + 40f)
            sanskritLayout.draw(canvas)
            canvas.restore()

            currentY = cardRect.bottom + 50f

            // 7. Transliteration (if story format)
            if (isStoryFormat && verse.transliteration.isNotBlank()) {
                val translitPaint = TextPaint().apply {
                    color = Color.rgb(215, 204, 200)
                    textSize = 30f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                    isAntiAlias = true
                }
                val translitLayout = StaticLayout.Builder.obtain(
                    verse.transliteration,
                    0,
                    verse.transliteration.length,
                    translitPaint,
                    contentWidth
                ).setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(8f, 1f)
                    .build()

                canvas.save()
                canvas.translate(90f, currentY)
                translitLayout.draw(canvas)
                canvas.restore()
                currentY += translitLayout.height + 40f
            }

            // 8. Hindi & English Translation Box
            val transHeaderPaint = Paint().apply {
                color = Color.rgb(255, 183, 77)
                textSize = 32f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(
                if (appLanguage == AppLanguage.HINDI) "॥ पावन भावार्थ ॥" else "॥ Divine Meaning & Wisdom ॥",
                width / 2f,
                currentY,
                transHeaderPaint
            )
            currentY += 45f

            val primaryTranslation = if (appLanguage == AppLanguage.HINDI)
                verse.translationHindi
            else
                verse.translationEnglish

            val transPaint = TextPaint().apply {
                color = Color.rgb(245, 245, 245)
                textSize = if (isStoryFormat) 36f else 32f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            val transLayout = StaticLayout.Builder.obtain(
                primaryTranslation,
                0,
                primaryTranslation.length,
                transPaint,
                contentWidth
            ).setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(10f, 1f)
                .build()

            canvas.save()
            canvas.translate(90f, currentY)
            transLayout.draw(canvas)
            canvas.restore()

            // 9. Life Lesson (if story format)
            currentY += transLayout.height + 45f
            if (isStoryFormat) {
                val lifeLessonText = if (appLanguage == AppLanguage.HINDI)
                    "💡 जीवन की सीख: " + verse.meaningHindi
                else
                    "💡 Practical Lesson: " + verse.meaningEnglish

                val lessonPaint = TextPaint().apply {
                    color = Color.rgb(255, 213, 79)
                    textSize = 32f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val lessonLayout = StaticLayout.Builder.obtain(
                    lifeLessonText,
                    0,
                    lifeLessonText.length,
                    lessonPaint,
                    contentWidth
                ).setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(8f, 1f)
                    .build()

                canvas.save()
                canvas.translate(90f, currentY)
                lessonLayout.draw(canvas)
                canvas.restore()
            }

            // 10. Footer Branding Badge
            val footerBoxY = height - 140f
            val footerTextPaint = Paint().apply {
                color = Color.rgb(255, 183, 77)
                textSize = 30f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("🌸 गीता साथी • Gita Saathi App", width / 2f, footerBoxY, footerTextPaint)

            val subFooterPaint = Paint().apply {
                color = Color.argb(180, 215, 204, 200)
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("Your Daily Spiritual AI Companion • Built with ❤️ by Nitin", width / 2f, footerBoxY + 36f, subFooterPaint)

            // 11. Save to Cache and Launch Share Intent
            val shareDir = File(context.cacheDir, "story_cards")
            if (!shareDir.exists()) shareDir.mkdirs()
            val imageFile = File(shareDir, "gita_verse_${verse.chapterId}_${verse.verseId}_story.png")

            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    if (appLanguage == AppLanguage.HINDI)
                        "ॐ श्रीमद्भगवद्गीता (अध्याय ${verse.chapterId}, श्लोक ${verse.verseId})\n\n'${verse.shlokaSanskrit}'\n\nअनुवाद: ${verse.translationHindi}\n\n🌸 गीता साथी (Gita Saathi) ऐप द्वारा साझा"
                    else
                        "Srimad Bhagavad Gita (Chapter ${verse.chapterId}, Verse ${verse.verseId})\n\n'${verse.shlokaSanskrit}'\n\nTranslation: ${verse.translationEnglish}\n\n🌸 Shared via Gita Saathi App"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Gita Verse Story / Post").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)

        } catch (e: Exception) {
            Log.e("GitaCardShareHelper", "Failed to generate verse story card", e)
        }
    }
}
