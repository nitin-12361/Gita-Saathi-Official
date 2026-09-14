package com.nkapps.gitasaathi.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.Verse
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.nkapps.gitasaathi.R
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Data Model for Shloka-specific Spiritual Artwork & Scene
 */
data class SpiritualSceneData(
    val archetypeId: Int,
    val title: String,
    val titleHindi: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val backgroundColors: List<Color>,
    val sacredGlyph: String,
    val particleColor: Color,
    val drawableResId: Int
)

/**
 * Maps each chapter & verse to a distinctive sacred artwork scene
 * using thematic mapping and verse seed hashing.
 */
fun getSpiritualSceneForVerse(chapterId: Int, verseId: Int): SpiritualSceneData {
    val seed = (chapterId * 37 + verseId)
    val baseArchetype = when (chapterId) {
        1, 2 -> 0 // Kurukshetra Chariot & Gandiva
        3, 5 -> 2 // Sacred Lotus & Water of Detachment
        4 -> 5    // Sacred Yajna Agni
        6 -> 4    // Himalayan Yogi & Chakras
        7, 9, 12 -> 3 // Flute & Peacock in Vrindavan
        10, 11 -> 1   // Cosmic Vishwaroopa Galaxy
        15 -> 6   // Ashvattha Tree of Life
        8, 13 -> 7    // Surya Mandala of Moksha
        14, 17 -> 8   // Sacred Om Pranava
        16 -> 9   // Sudarshana Chakra of Dharma
        else -> (seed % 12)
    }

    // Dynamic color variations based on verse seed so every shloka has distinct warmth & tone
    val colorShift = seed % 6
    val (primary, secondary, accent, bgColors, sparkColor) = when (colorShift) {
        0 -> Tuple5(
            Color(0xFFFF9933), Color(0xFFE65100), Color(0xFFFFD54F),
            listOf(Color(0xFF2E1505), Color(0xFF1A0A02), Color(0xFF0D0400)),
            Color(0xFFFFE082)
        )
        1 -> Tuple5(
            Color(0xFF7E57C2), Color(0xFF4527A0), Color(0xFFFFB74D),
            listOf(Color(0xFF1E1035), Color(0xFF120824), Color(0xFF080214)),
            Color(0xFFCE93D8)
        )
        2 -> Tuple5(
            Color(0xFF26A69A), Color(0xFF00695C), Color(0xFF80CBC4),
            listOf(Color(0xFF04201D), Color(0xFF021412), Color(0xFF000A09)),
            Color(0xFFB2DFDB)
        )
        3 -> Tuple5(
            Color(0xFFEF5350), Color(0xFFC62828), Color(0xFFFFE082),
            listOf(Color(0xFF2A0808), Color(0xFF180303), Color(0xFF0D0000)),
            Color(0xFFFFCDD2)
        )
        4 -> Tuple5(
            Color(0xFFFFB300), Color(0xFFF57F17), Color(0xFFFFF59D),
            listOf(Color(0xFF291E04), Color(0xFF171001), Color(0xFF0A0700)),
            Color(0xFFFFF9C4)
        )
        else -> Tuple5(
            Color(0xFF42A5F5), Color(0xFF1565C0), Color(0xFFFFCC80),
            listOf(Color(0xFF091C30), Color(0xFF040E1A), Color(0xFF01060D)),
            Color(0xFFBBDEFB)
        )
    }

    val glyphs = listOf("ॐ", "卐", "☸", "🪷", "🦚", "🏹", "🔱", "📿", "☀️", "🌟", "🕯️", "🕊️")
    val sacredGlyph = glyphs[seed % glyphs.size]

    return when (baseArchetype) {
        0 -> SpiritualSceneData(
            archetypeId = 0,
            title = "Kurukshetra Divine Chariot",
            titleHindi = "कुरुक्षेत्र दिव्य रथ एवं श्रीकृष्ण उपदेश",
            primaryColor = primary,
            secondaryColor = secondary,
            accentColor = accent,
            backgroundColors = bgColors,
            sacredGlyph = sacredGlyph,
            particleColor = sparkColor,
            drawableResId = R.drawable.bg_scene_chariot
        )
        1 -> SpiritualSceneData(
            archetypeId = 1,
            title = "Cosmic Vishwaroopa Darshan",
            titleHindi = "अनन्त विश्वरूप दर्शन एवं ब्रह्माण्ड",
            primaryColor = Color(0xFFAB47BC),
            secondaryColor = Color(0xFF6A1B9A),
            accentColor = Color(0xFFFFD54F),
            backgroundColors = listOf(Color(0xFF1D0B2E), Color(0xFF10051C), Color(0xFF08010E)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFE1BEE7),
            drawableResId = R.drawable.bg_scene_vishwaroop
        )
        2 -> SpiritualSceneData(
            archetypeId = 2,
            title = "Sacred Lotus of Karma Yoga",
            titleHindi = "पवित्र कमल एवं निष्काम कर्म योग",
            primaryColor = Color(0xFFEC407A),
            secondaryColor = Color(0xFFAD1457),
            accentColor = Color(0xFFFFE082),
            backgroundColors = listOf(Color(0xFF240A15), Color(0xFF14040B), Color(0xFF080104)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFF8BBD0),
            drawableResId = R.drawable.bg_scene_karma
        )
        3 -> SpiritualSceneData(
            archetypeId = 3,
            title = "Divine Mor-Pankh & Bansuri",
            titleHindi = "श्रीकृष्ण मोरपंख एवं दिव्य वंशी",
            primaryColor = Color(0xFF00ACC1),
            secondaryColor = Color(0xFF006064),
            accentColor = Color(0xFFFFD54F),
            backgroundColors = listOf(Color(0xFF051C20), Color(0xFF021013), Color(0xFF00080A)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFF80DEEA),
            drawableResId = R.drawable.bg_scene_flute
        )
        4 -> SpiritualSceneData(
            archetypeId = 4,
            title = "Himalayan Dhyana Yoga",
            titleHindi = "हिमालय ध्यानमग्न श्रीकृष्ण एवं समाधि",
            primaryColor = Color(0xFF5C6BC0),
            secondaryColor = Color(0xFF283593),
            accentColor = Color(0xFFFFE082),
            backgroundColors = listOf(Color(0xFF0F1530), Color(0xFF080B1C), Color(0xFF03050E)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFC5CAE9),
            drawableResId = R.drawable.bg_scene_dhyana
        )
        5 -> SpiritualSceneData(
            archetypeId = 5,
            title = "Sacred Agni & Yajna Kund",
            titleHindi = "पवित्र ज्ञान यज्ञ एवं दिव्य उपदेश",
            primaryColor = Color(0xFFFF7043),
            secondaryColor = Color(0xFFD84315),
            accentColor = Color(0xFFFFEE58),
            backgroundColors = listOf(Color(0xFF2B0F06), Color(0xFF1A0802), Color(0xFF0D0300)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFFFCCBC),
            drawableResId = R.drawable.bg_scene_chariot
        )
        6 -> SpiritualSceneData(
            archetypeId = 6,
            title = "Ashvattha Cosmic Tree & Vrindavan",
            titleHindi = "सनातन अश्वत्थ दिव्य वृक्ष एवं वृन्दाबन",
            primaryColor = Color(0xFF66BB6A),
            secondaryColor = Color(0xFF2E7D32),
            accentColor = Color(0xFFFFD54F),
            backgroundColors = listOf(Color(0xFF0E2211), Color(0xFF071409), Color(0xFF020904)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFC8E6C9),
            drawableResId = R.drawable.bg_scene_flute
        )
        7 -> SpiritualSceneData(
            archetypeId = 7,
            title = "Surya Mandala of Moksha",
            titleHindi = "परम मोक्ष एवं शरणागति",
            primaryColor = Color(0xFFFFA726),
            secondaryColor = Color(0xFFE65100),
            accentColor = Color(0xFFFFF59D),
            backgroundColors = listOf(Color(0xFF2B1604), Color(0xFF170A01), Color(0xFF0A0400)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFFFE082),
            drawableResId = R.drawable.bg_scene_moksha
        )
        8 -> SpiritualSceneData(
            archetypeId = 8,
            title = "Sacred Pranava Om Sound",
            titleHindi = "प्रणव ॐ एवं दिव्य नाद ध्यान",
            primaryColor = Color(0xFFFFCA28),
            secondaryColor = Color(0xFFFF8F00),
            accentColor = Color(0xFFFFF9C4),
            backgroundColors = listOf(Color(0xFF291F05), Color(0xFF171101), Color(0xFF0A0700)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFFFF8E1),
            drawableResId = R.drawable.bg_scene_dhyana
        )
        9 -> SpiritualSceneData(
            archetypeId = 9,
            title = "Dharma Sudarshana Chakra",
            titleHindi = "धर्म कवच एवं सुदर्शन चक्र",
            primaryColor = Color(0xFF29B6F6),
            secondaryColor = Color(0xFF0277BD),
            accentColor = Color(0xFFFFD54F),
            backgroundColors = listOf(Color(0xFF061B29), Color(0xFF030E17), Color(0xFF01060B)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFB3E5FC),
            drawableResId = R.drawable.bg_scene_karma
        )
        10 -> SpiritualSceneData(
            archetypeId = 10,
            title = "Ocean of Samsara & Boat of Wisdom",
            titleHindi = "संसार सागर एवं ज्ञान नौका",
            primaryColor = Color(0xFF4DB6AC),
            secondaryColor = Color(0xFF00695C),
            accentColor = Color(0xFFFFE082),
            backgroundColors = listOf(Color(0xFF051D1A), Color(0xFF02100E), Color(0xFF000807)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFB2DFDB),
            drawableResId = R.drawable.bg_scene_karma
        )
        else -> SpiritualSceneData(
            archetypeId = 11,
            title = "Supreme Moksha & Divine Refuge",
            titleHindi = "परम मोक्ष एवं दिव्य शरणागति",
            primaryColor = Color(0xFFBA68C8),
            secondaryColor = Color(0xFF7B1FA2),
            accentColor = Color(0xFFFFD54F),
            backgroundColors = listOf(Color(0xFF1E0C24), Color(0xFF110514), Color(0xFF07010A)),
            sacredGlyph = sacredGlyph,
            particleColor = Color(0xFFE1BEE7),
            drawableResId = R.drawable.bg_scene_moksha
        )
    }
}

private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)

/**
 * High-Fidelity Ken-Burns Spiritual Artwork Component
 * Features cinematic slow pan & zoom over authentic paintings of Lord Krishna and Gita scenes.
 */
@Composable
fun SpiritualArtworkCanvas(
    verse: Verse,
    isPlaying: Boolean,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    showTitleBadge: Boolean = true
) {
    val scene = remember(verse.chapterId, verse.verseId) {
        getSpiritualSceneForVerse(verse.chapterId, verse.verseId)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns_engine")

    // Ken Burns Slow Zoom (1.0x to 1.15x)
    val kenBurnsScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "kenBurnsScale"
    )

    // Ken Burns Slow Diagonal Drift (Pan X & Y)
    val panX by infiniteTransition.animateFloat(
        initialValue = -14f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "panX"
    )

    val panY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "panY"
    )

    // Slow ambient rotation for background halo mandala
    val haloRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "haloRotation"
    )

    // Upward floating particle progress
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particlePhase"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F0804))
            .testTag("spiritual_artwork_${verse.chapterId}_${verse.verseId}"),
        contentAlignment = Alignment.Center
    ) {
        // 1. High Definition Authentic Painting of Bhagwan Shri Krishna & Gita Scenes with Ken-Burns Motion
        Image(
            painter = painterResource(id = scene.drawableResId),
            contentDescription = scene.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = if (isPlaying) kenBurnsScale else 1.04f
                    scaleY = if (isPlaying) kenBurnsScale else 1.04f
                    translationX = if (isPlaying) panX * 1.5f else 0f
                    translationY = if (isPlaying) panY * 1.5f else 0f
                }
        )

        // 2. Divine Golden Aura, Floating Embers & Subtle Ambient Shimmer Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)

            // Rotating Subtle Divine Halo in corner
            rotate(degrees = if (isPlaying) haloRotation else 0f, pivot = center) {
                drawSacredBackgroundHalo(center, w.coerceAtMost(h) * 0.46f, scene)
            }

            // Upward Floating Golden Embers & Sparks
            drawFloatingEmbers(w, h, scene, particlePhase)

            // Cinematic Vignette Overlay
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.38f)),
                    center = center,
                    radius = (w.coerceAtLeast(h) * 0.76f)
                ),
                size = size
            )
        }

        // 3. Bottom Scene Title Badge
        if (showTitleBadge) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.75f),
                border = androidx.compose.foundation.BorderStroke(1.dp, scene.accentColor.copy(alpha = 0.55f))
            ) {
                Text(
                    text = "${scene.sacredGlyph} " + if (appLanguage == AppLanguage.HINDI) scene.titleHindi else scene.title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = scene.accentColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// SACRED ARTWORK DRAWING ENGINES
// -----------------------------------------------------------------------------------------

/**
 * Background Sacred Geometric Halo / Mandala
 */
private fun DrawScope.drawSacredBackgroundHalo(center: Offset, radius: Float, scene: SpiritualSceneData) {
    // Outer dashed ring
    drawCircle(
        color = scene.primaryColor.copy(alpha = 0.22f),
        radius = radius,
        center = center,
        style = Stroke(width = 2.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(15f, 10f)))
    )

    // Inner glowing ring
    drawCircle(
        color = scene.accentColor.copy(alpha = 0.15f),
        radius = radius * 0.75f,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // 12 Radiating rays
    for (i in 0 until 12) {
        val angle = (i * 30) * (PI / 180f)
        val startX = center.x + cos(angle).toFloat() * (radius * 0.5f)
        val startY = center.y + sin(angle).toFloat() * (radius * 0.5f)
        val endX = center.x + cos(angle).toFloat() * radius
        val endY = center.y + sin(angle).toFloat() * radius

        drawLine(
            color = scene.accentColor.copy(alpha = 0.18f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Twinkling Constellation Stars in Background
 */
private fun DrawScope.drawCelestialStars(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, phase: Float) {
    val starCoords = listOf(
        Offset(w * 0.15f, h * 0.2f), Offset(w * 0.85f, h * 0.18f),
        Offset(w * 0.25f, h * 0.35f), Offset(w * 0.78f, h * 0.32f),
        Offset(w * 0.1f, h * 0.6f), Offset(w * 0.9f, h * 0.58f),
        Offset(w * 0.3f, h * 0.8f), Offset(w * 0.7f, h * 0.82f)
    )

    starCoords.forEachIndexed { idx, coord ->
        val starAlpha = (0.3f + 0.6f * sin((phase * 2 * PI + idx).toFloat())).coerceIn(0.1f, 1.0f)
        drawCircle(
            color = scene.particleColor.copy(alpha = starAlpha),
            radius = (1.5f + (idx % 3) * 0.8f).dp.toPx(),
            center = coord
        )
    }
}

/**
 * Floating Golden Embers & Sparks
 */
private fun DrawScope.drawFloatingEmbers(w: Float, h: Float, scene: SpiritualSceneData, phase: Float) {
    val emberCount = 16
    for (i in 0 until emberCount) {
        val xNorm = ((i * 37 + 13) % 100) / 100f
        val yOffset = ((phase + (i.toFloat() / emberCount)) % 1.0f)
        val yNorm = 1.0f - yOffset
        val emberAlpha = sin(yOffset * PI.toFloat()).coerceIn(0f, 0.85f)

        val posX = w * xNorm + (sin((phase * 4 + i) * PI.toFloat()) * 12f)
        val posY = h * yNorm

        drawCircle(
            color = scene.accentColor.copy(alpha = emberAlpha),
            radius = (1.2f + (i % 3) * 0.6f).dp.toPx(),
            center = Offset(posX, posY)
        )
    }
}

/**
 * 1. Kurukshetra Chariot & Gandiva Silhouette Scene
 */
private fun DrawScope.drawKurukshetraChariotScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Divine Sun of Dharma in background
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(scene.primaryColor.copy(alpha = 0.6f * glow), scene.secondaryColor.copy(alpha = 0.15f), Color.Transparent),
            center = Offset(center.x, center.y - r * 0.2f),
            radius = r * 0.9f
        ),
        radius = r * 0.9f,
        center = Offset(center.x, center.y - r * 0.2f)
    )

    // Chariot Wheel (Rath Chakra) with 12 Spokes
    val wheelCenter = Offset(center.x - r * 0.3f, center.y + r * 0.25f)
    val wheelRadius = r * 0.32f
    drawCircle(
        color = scene.accentColor.copy(alpha = 0.85f),
        radius = wheelRadius,
        center = wheelCenter,
        style = Stroke(width = 3.dp.toPx())
    )
    drawCircle(
        color = scene.primaryColor,
        radius = wheelRadius * 0.25f,
        center = wheelCenter,
        style = Fill
    )
    for (i in 0 until 8) {
        val ang = (i * 45) * (PI / 180f)
        drawLine(
            color = scene.accentColor.copy(alpha = 0.75f),
            start = wheelCenter,
            end = Offset(wheelCenter.x + cos(ang).toFloat() * wheelRadius, wheelCenter.y + sin(ang).toFloat() * wheelRadius),
            strokeWidth = 2.dp.toPx()
        )
    }

    // Chariot Chassis & Canopy
    val path = Path().apply {
        moveTo(center.x - r * 0.7f, center.y + r * 0.2f)
        lineTo(center.x + r * 0.1f, center.y + r * 0.2f)
        lineTo(center.x + r * 0.25f, center.y - r * 0.1f)
        lineTo(center.x - r * 0.1f, center.y - r * 0.5f)
        lineTo(center.x - r * 0.4f, center.y - r * 0.45f)
        lineTo(center.x - r * 0.65f, center.y + r * 0.05f)
        close()
    }
    drawPath(path, color = scene.secondaryColor.copy(alpha = 0.9f))

    // Hanuman Dhwaja Flag Pole & Fluttering Flag
    drawLine(
        color = scene.accentColor,
        start = Offset(center.x - r * 0.1f, center.y - r * 0.5f),
        end = Offset(center.x - r * 0.1f, center.y - r * 0.85f),
        strokeWidth = 2.5.dp.toPx()
    )
    val flagPath = Path().apply {
        moveTo(center.x - r * 0.1f, center.y - r * 0.85f)
        lineTo(center.x + r * 0.35f, center.y - r * 0.75f)
        lineTo(center.x - r * 0.1f, center.y - r * 0.65f)
        close()
    }
    drawPath(flagPath, color = scene.primaryColor)

    // Golden Reins extending forward
    drawLine(
        color = scene.accentColor.copy(alpha = 0.8f),
        start = Offset(center.x - r * 0.1f, center.y - r * 0.1f),
        end = Offset(center.x + r * 0.75f, center.y + r * 0.05f),
        strokeWidth = 1.8.dp.toPx()
    )
}

/**
 * 2. Cosmic Vishwaroopa Galaxy Scene
 */
private fun DrawScope.drawCosmicVishwaroopaScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.4f

    // Concentric glowing nebula rings
    for (i in 4 downTo 1) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    if (i % 2 == 0) scene.primaryColor.copy(alpha = 0.4f * glow) else scene.accentColor.copy(alpha = 0.3f * glow),
                    Color.Transparent
                ),
                center = center,
                radius = r * (i * 0.25f)
            ),
            radius = r * (i * 0.25f),
            center = center
        )
    }

    // Radiating Golden Sunburst of 16 divine rays
    for (i in 0 until 16) {
        val angle = (i * 22.5) * (PI / 180f)
        val outerX = center.x + cos(angle).toFloat() * (r * 0.85f)
        val outerY = center.y + sin(angle).toFloat() * (r * 0.85f)
        drawLine(
            brush = Brush.linearGradient(listOf(scene.accentColor, Color.Transparent)),
            start = center,
            end = Offset(outerX, outerY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Golden Central Core (Bindu)
    drawCircle(
        color = Color.White,
        radius = 8.dp.toPx() * glow,
        center = center
    )
}

/**
 * 3. Sacred Lotus of Detachment Scene
 */
private fun DrawScope.drawSacredLotusScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.35f
    val lotusCenter = Offset(center.x, center.y + r * 0.15f)

    // Shimmering tranquil water ripple ellipses
    for (i in 1..3) {
        drawOval(
            color = scene.accentColor.copy(alpha = (0.35f / i)),
            topLeft = Offset(lotusCenter.x - r * (0.5f + i * 0.25f), lotusCenter.y + r * 0.3f - (i * 3f)),
            size = Size(r * (1.0f + i * 0.5f), r * 0.25f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }

    // Multi-layered Lotus Petals
    val petalCount = 8
    for (i in 0 until petalCount) {
        val angle = (i * (180f / (petalCount - 1)) - 90f) * (PI / 180f)
        val pX = lotusCenter.x + cos(angle).toFloat() * (r * 0.7f)
        val pY = lotusCenter.y - sin(abs(angle.toFloat())).toFloat() * (r * 0.55f)

        val petalPath = Path().apply {
            moveTo(lotusCenter.x, lotusCenter.y + r * 0.1f)
            quadraticTo(
                (lotusCenter.x + pX) / 2f + 15f,
                (lotusCenter.y + pY) / 2f,
                pX, pY
            )
            quadraticTo(
                (lotusCenter.x + pX) / 2f - 15f,
                (lotusCenter.y + pY) / 2f,
                lotusCenter.x, lotusCenter.y + r * 0.1f
            )
            close()
        }
        drawPath(petalPath, brush = Brush.verticalGradient(listOf(scene.primaryColor, scene.secondaryColor)))
    }

    // Glowing Golden Lotus Center Core
    drawCircle(
        color = scene.accentColor,
        radius = 10.dp.toPx() * glow,
        center = lotusCenter
    )
}

/**
 * 4. Divine Mor-Pankh & Bansuri Scene
 */
private fun DrawScope.drawFlutePeacockScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Peacock Feather Central Spine
    val spineStart = Offset(center.x - r * 0.4f, center.y + r * 0.6f)
    val spineEnd = Offset(center.x + r * 0.2f, center.y - r * 0.6f)
    drawLine(
        color = scene.accentColor,
        start = spineStart,
        end = spineEnd,
        strokeWidth = 2.dp.toPx()
    )

    // Peacock Eye Multi-Layered Ovals
    val eyeCenter = Offset(center.x + r * 0.15f, center.y - r * 0.35f)
    drawOval(
        color = scene.primaryColor.copy(alpha = 0.85f),
        topLeft = Offset(eyeCenter.x - r * 0.25f, eyeCenter.y - r * 0.3f),
        size = Size(r * 0.5f, r * 0.6f)
    )
    drawOval(
        color = Color(0xFF00E676),
        topLeft = Offset(eyeCenter.x - r * 0.18f, eyeCenter.y - r * 0.22f),
        size = Size(r * 0.36f, r * 0.44f)
    )
    drawOval(
        color = Color(0xFF0D47A1),
        topLeft = Offset(eyeCenter.x - r * 0.1f, eyeCenter.y - r * 0.14f),
        size = Size(r * 0.2f, r * 0.28f)
    )
    drawCircle(
        color = scene.accentColor,
        radius = 5.dp.toPx() * glow,
        center = eyeCenter
    )

    // Golden Bansuri (Flute) crossing across
    val fluteStart = Offset(center.x - r * 0.7f, center.y - r * 0.1f)
    val fluteEnd = Offset(center.x + r * 0.7f, center.y + r * 0.3f)
    drawLine(
        color = scene.accentColor,
        start = fluteStart,
        end = fluteEnd,
        strokeWidth = 5.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Bansuri finger holes
    for (i in 1..5) {
        val hX = fluteStart.x + (fluteEnd.x - fluteStart.x) * (0.25f + i * 0.1f)
        val hY = fluteStart.y + (fluteEnd.y - fluteStart.y) * (0.25f + i * 0.1f)
        drawCircle(color = Color(0xFF3E2723), radius = 2.dp.toPx(), center = Offset(hX, hY))
    }
}

/**
 * 5. Himalayan Yogi in Samadhi with 7 Glowing Chakras Scene
 */
private fun DrawScope.drawHimalayanYogiScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Snowy Mountain Peaks in background
    val peakPath = Path().apply {
        moveTo(center.x - r * 0.9f, center.y + r * 0.5f)
        lineTo(center.x - r * 0.3f, center.y - r * 0.2f)
        lineTo(center.x + r * 0.2f, center.y + r * 0.2f)
        lineTo(center.x + r * 0.7f, center.y - r * 0.35f)
        lineTo(center.x + r * 1.0f, center.y + r * 0.5f)
        close()
    }
    drawPath(peakPath, color = scene.secondaryColor.copy(alpha = 0.4f))

    // Meditating Yogi Silhouette (Head & Torso in Padmasana)
    val yogiCenter = Offset(center.x, center.y + r * 0.15f)
    // Head
    drawCircle(color = Color(0xFF151020), radius = r * 0.12f, center = Offset(yogiCenter.x, yogiCenter.y - r * 0.35f))
    // Body & Cross-legged Base
    val bodyPath = Path().apply {
        moveTo(yogiCenter.x, yogiCenter.y - r * 0.22f)
        lineTo(yogiCenter.x + r * 0.28f, yogiCenter.y + r * 0.15f)
        lineTo(yogiCenter.x + r * 0.45f, yogiCenter.y + r * 0.35f)
        lineTo(yogiCenter.x - r * 0.45f, yogiCenter.y + r * 0.35f)
        lineTo(yogiCenter.x - r * 0.28f, yogiCenter.y + r * 0.15f)
        close()
    }
    drawPath(bodyPath, color = Color(0xFF151020))

    // 7 Glowing Chakras along the vertical axis (Muladhara to Sahasrara)
    val chakraColors = listOf(
        Color(0xFFFF1744), Color(0xFFFF9100), Color(0xFFFFEA00),
        Color(0xFF00E676), Color(0xFF00B0FF), Color(0xFF651FFF), Color(0xFFD500F9)
    )
    for (i in 0 until 7) {
        val cY = (yogiCenter.y + r * 0.28f) - (i * (r * 0.1f))
        drawCircle(
            color = chakraColors[i],
            radius = (3.5f + if (i == 6) 2.5f * glow else 0f).dp.toPx(),
            center = Offset(yogiCenter.x, cY)
        )
    }
}

/**
 * 6. Sacred Agni & Yajna Kund Scene
 */
private fun DrawScope.drawSacredAgniYajnaScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f
    val altarCenter = Offset(center.x, center.y + r * 0.25f)

    // Vedic Stepped Yajna Kund Altar
    drawRect(
        color = Color(0xFF4E342E),
        topLeft = Offset(altarCenter.x - r * 0.5f, altarCenter.y),
        size = Size(r * 1.0f, r * 0.2f)
    )
    drawRect(
        color = Color(0xFF6D4C41),
        topLeft = Offset(altarCenter.x - r * 0.38f, altarCenter.y - r * 0.08f),
        size = Size(r * 0.76f, r * 0.08f)
    )

    // Licking Sacred Golden Flames
    val flameCount = 5
    for (i in 0 until flameCount) {
        val fX = altarCenter.x + (i - 2) * (r * 0.12f)
        val fHeight = (r * 0.45f) * (if (i % 2 == 0) glow else 1.0f)

        val flamePath = Path().apply {
            moveTo(fX - r * 0.08f, altarCenter.y - r * 0.08f)
            quadraticTo(fX - r * 0.04f, altarCenter.y - fHeight * 0.6f, fX, altarCenter.y - fHeight)
            quadraticTo(fX + r * 0.04f, altarCenter.y - fHeight * 0.6f, fX + r * 0.08f, altarCenter.y - r * 0.08f)
            close()
        }
        drawPath(
            flamePath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFF9C4), scene.primaryColor, scene.secondaryColor),
                startY = altarCenter.y - fHeight,
                endY = altarCenter.y
            )
        )
    }
}

/**
 * 7. Ashvattha Cosmic Tree of Life Scene
 */
private fun DrawScope.drawAshvatthaTreeScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Canopy of luminous leaves
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(scene.primaryColor.copy(alpha = 0.7f * glow), scene.secondaryColor.copy(alpha = 0.3f), Color.Transparent),
            center = center,
            radius = r * 0.65f
        ),
        radius = r * 0.65f,
        center = center
    )

    // Trunk & Radiant Golden Branches
    val trunkBase = Offset(center.x, center.y + r * 0.5f)
    drawLine(
        color = scene.accentColor,
        start = trunkBase,
        end = center,
        strokeWidth = 6.dp.toPx(),
        cap = StrokeCap.Round
    )

    // 6 Radiating Branches
    for (i in 0 until 6) {
        val ang = (i * 60) * (PI / 180f)
        val bEnd = Offset(center.x + cos(ang).toFloat() * (r * 0.45f), center.y + sin(ang).toFloat() * (r * 0.45f))
        drawLine(
            color = scene.accentColor.copy(alpha = 0.85f),
            start = center,
            end = bEnd,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Glowing Leaf node
        drawCircle(color = scene.particleColor, radius = 4.dp.toPx(), center = bEnd)
    }
}

/**
 * 8. Surya Mandala of Moksha Scene
 */
private fun DrawScope.drawSuryaMandalaScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Glowing Outer Corona
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(scene.accentColor.copy(alpha = 0.8f * glow), scene.primaryColor.copy(alpha = 0.2f), Color.Transparent),
            center = center,
            radius = r * 0.85f
        ),
        radius = r * 0.85f,
        center = center
    )

    // 12 Sunburst Triangular Rays
    for (i in 0 until 12) {
        val ang = (i * 30) * (PI / 180f)
        val rOut = r * 0.72f
        val rayPath = Path().apply {
            moveTo(center.x + cos(ang - 0.1).toFloat() * (r * 0.35f), center.y + sin(ang - 0.1).toFloat() * (r * 0.35f))
            lineTo(center.x + cos(ang).toFloat() * rOut, center.y + sin(ang).toFloat() * rOut)
            lineTo(center.x + cos(ang + 0.1).toFloat() * (r * 0.35f), center.y + sin(ang + 0.1).toFloat() * (r * 0.35f))
            close()
        }
        drawPath(rayPath, color = scene.primaryColor)
    }

    // Central Radiant Solar Disc
    drawCircle(color = Color(0xFFFFFDE7), radius = r * 0.35f, center = center)
}

/**
 * 9. Sacred Pranava Om Sound Scene
 */
private fun DrawScope.drawSacredOmPranavaScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Expanding sound wave ripple rings
    for (i in 1..4) {
        drawCircle(
            color = scene.accentColor.copy(alpha = (0.4f / i) * glow),
            radius = r * (0.3f + i * 0.18f),
            center = center,
            style = Stroke(width = 1.8.dp.toPx())
        )
    }

    // Glowing Central Om Sanctum Disc
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(scene.accentColor, scene.primaryColor),
            center = center,
            radius = r * 0.32f
        ),
        radius = r * 0.32f,
        center = center
    )
}

/**
 * 10. Dharma Sudarshana Chakra Scene
 */
private fun DrawScope.drawSudarshanaChakraScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // 16 Serrated Spinning Teeth
    for (i in 0 until 16) {
        val ang = (i * 22.5) * (PI / 180f)
        val toothPath = Path().apply {
            moveTo(center.x + cos(ang).toFloat() * (r * 0.6f), center.y + sin(ang).toFloat() * (r * 0.6f))
            lineTo(center.x + cos(ang + 0.15).toFloat() * (r * 0.82f), center.y + sin(ang + 0.15).toFloat() * (r * 0.82f))
            lineTo(center.x + cos(ang + 0.3).toFloat() * (r * 0.6f), center.y + sin(ang + 0.3).toFloat() * (r * 0.6f))
            close()
        }
        drawPath(toothPath, color = scene.accentColor)
    }

    // Central Discus Rim & Hub
    drawCircle(color = scene.primaryColor, radius = r * 0.58f, center = center, style = Stroke(width = 4.dp.toPx()))
    drawCircle(color = scene.secondaryColor, radius = r * 0.22f, center = center)
    drawCircle(color = Color.White, radius = 6.dp.toPx() * glow, center = center)
}

/**
 * 11. Ocean of Samsara & Boat of Wisdom Scene
 */
private fun DrawScope.drawOceanOfSamsaraScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Moon in celestial sky
    drawCircle(
        color = Color(0xFFFFF9C4),
        radius = r * 0.22f,
        center = Offset(center.x + r * 0.45f, center.y - r * 0.4f)
    )

    // Ocean Waves Line Paths
    for (i in 1..4) {
        val waveY = center.y + (i * r * 0.12f)
        val wavePath = Path().apply {
            moveTo(center.x - r * 0.9f, waveY)
            for (seg in 0..6) {
                val sX = center.x - r * 0.9f + (seg * (r * 1.8f / 6f))
                val waveHeight = if (seg % 2 == 0) 6.dp.toPx() else -6.dp.toPx()
                lineTo(sX, waveY + waveHeight)
            }
        }
        drawPath(wavePath, color = scene.primaryColor.copy(alpha = (0.25f + i * 0.15f)), style = Stroke(width = 2.dp.toPx()))
    }

    // Boat of Wisdom Silhouette
    val boatPath = Path().apply {
        moveTo(center.x - r * 0.3f, center.y + r * 0.15f)
        lineTo(center.x + r * 0.3f, center.y + r * 0.15f)
        lineTo(center.x + r * 0.2f, center.y + r * 0.28f)
        lineTo(center.x - r * 0.2f, center.y + r * 0.28f)
        close()
    }
    drawPath(boatPath, color = scene.accentColor)
}

/**
 * 12. Mount Meru & Golden Sanctum Scene
 */
private fun DrawScope.drawMountMeruSanctumScene(center: Offset, w: Float, h: Float, scene: SpiritualSceneData, glow: Float) {
    val r = w.coerceAtMost(h) * 0.38f

    // Majestic Triangular Mount Meru Peak
    val mountainPath = Path().apply {
        moveTo(center.x - r * 0.85f, center.y + r * 0.6f)
        lineTo(center.x, center.y - r * 0.35f)
        lineTo(center.x + r * 0.85f, center.y + r * 0.6f)
        close()
    }
    drawPath(
        mountainPath,
        brush = Brush.verticalGradient(
            colors = listOf(scene.primaryColor, scene.secondaryColor),
            startY = center.y - r * 0.35f,
            endY = center.y + r * 0.6f
        )
    )

    // Golden Temple Spire (Shikhara) at top
    val templePath = Path().apply {
        moveTo(center.x - r * 0.12f, center.y - r * 0.35f)
        lineTo(center.x, center.y - r * 0.7f)
        lineTo(center.x + r * 0.12f, center.y - r * 0.35f)
        close()
    }
    drawPath(templePath, color = scene.accentColor)

    // Radiant Golden Kalasha Light
    drawCircle(
        color = Color.White,
        radius = 6.dp.toPx() * glow,
        center = Offset(center.x, center.y - r * 0.72f)
    )
}

private fun abs(value: Float): Float = if (value < 0) -value else value
