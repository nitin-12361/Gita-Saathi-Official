package com.nkapps.gitasaathi.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.GitaLifeApplicationData
import com.nkapps.gitasaathi.data.ShlokaLifeApplication
import com.nkapps.gitasaathi.data.Verse

@Composable
fun LifeApplicationCard(
    verse: Verse,
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    val isHindi = appLanguage == AppLanguage.HINDI
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    val lifeApp: ShlokaLifeApplication = remember(verse.chapterId, verse.verseId) {
        GitaLifeApplicationData.getLifeApplication(verse.chapterId, verse.verseId, verse, context)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("life_app_card_${verse.chapterId}_${verse.verseId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, Color(0xFFE2B714).copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Toggle Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFF9800).copy(alpha = 0.15f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier
                                .padding(5.dp)
                                .size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = if (isHindi) "🌿 जीवन में प्रयोग (3 Life Applications)" else "🌿 Life Applications (3 Dimensions)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "मन • परिवार व संबंध • करियर व निर्णय" else "Personal Mind • Family • Workplace",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isExpanded) {
                        IconButton(
                            onClick = {
                                val shareText = buildString {
                                    append(if (isHindi) "🌿 श्रीमद्भगवद्गीता श्लोक ${verse.chapterId}.${verse.verseId} - जीवन में प्रयोग\n\n" else "🌿 Bhagavad Gita Verse ${verse.chapterId}.${verse.verseId} - Life Applications\n\n")
                                    append(if (isHindi) "🧘 1. व्यक्तिगत शांति (Personal Mind):\n${lifeApp.personalHindi}\n\n" else "🧘 1. Personal Mind:\n${lifeApp.personalEnglish}\n\n")
                                    append(if (isHindi) "👨‍👩‍👧 2. परिवार व संबंध (Relationships):\n${lifeApp.familyHindi}\n\n" else "👨‍👩‍👧 2. Family & Relationships:\n${lifeApp.familyEnglish}\n\n")
                                    append(if (isHindi) "💼 3. कार्यक्षेत्र व करियर (Career):\n${lifeApp.careerHindi}\n\n" else "💼 3. Career & Leadership:\n${lifeApp.careerEnglish}\n\n")
                                    append("📲 गीता साथी (Gita Saathi) ऐप पर पढ़ें")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Life Applications"))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Expandable Content: 3 Dedicated Life Application Dimension Cards
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dimension 1: 🧘 Personal Mind & Peace
                    DimensionCard(
                        icon = Icons.Default.Psychology,
                        title = if (isHindi) "1. व्यक्तिगत शांति एवं मन (Personal Mind)" else "1. Personal Peace & Mind",
                        content = if (isHindi) lifeApp.personalHindi else lifeApp.personalEnglish,
                        accentColor = Color(0xFFD97706),
                        bgColor = Color(0xFFFFFBEB)
                    )

                    // Dimension 2: 👨‍👩‍👧 Family & Relationships
                    DimensionCard(
                        icon = Icons.Default.Diversity1,
                        title = if (isHindi) "2. परिवार व सामाजिक संबंध (Relationships)" else "2. Family & Relationships",
                        content = if (isHindi) lifeApp.familyHindi else lifeApp.familyEnglish,
                        accentColor = Color(0xFF0D9488),
                        bgColor = Color(0xFFF0FDFA)
                    )

                    // Dimension 3: 💼 Career & Workplace Leadership
                    DimensionCard(
                        icon = Icons.Default.BusinessCenter,
                        title = if (isHindi) "3. कार्यक्षेत्र व करियर (Career & Leadership)" else "3. Career & Leadership",
                        content = if (isHindi) lifeApp.careerHindi else lifeApp.careerEnglish,
                        accentColor = Color(0xFF2563EB),
                        bgColor = Color(0xFFEFF6FF)
                    )
                }
            }
        }
    }
}

@Composable
private fun DimensionCard(
    icon: ImageVector,
    title: String,
    content: String,
    accentColor: Color,
    bgColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = content,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}
