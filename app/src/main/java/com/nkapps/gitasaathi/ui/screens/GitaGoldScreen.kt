package com.nkapps.gitasaathi.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nkapps.gitasaathi.ads.GitaAdManager
import com.nkapps.gitasaathi.billing.GitaBillingManager
import com.nkapps.gitasaathi.billing.GoldPlan
import com.nkapps.gitasaathi.data.AppLanguage

@Composable
fun GitaGoldScreen(
    appLanguage: AppLanguage,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val isPremium by GitaAdManager.isPremiumUser.collectAsState()
    val monthlyPrice by GitaBillingManager.monthlyPrice.collectAsState()
    val yearlyPrice by GitaBillingManager.yearlyPrice.collectAsState()
    var selectedPlan by remember { mutableStateOf(GoldPlan.YEARLY) }

    val goldGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2C1E14),
            Color(0xFF19120C),
            Color(0xFF100C09)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(goldGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF3D2A1C).copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFD700)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (appLanguage == AppLanguage.HINDI) "गीता साथी Gold" else "Gita Saathi Gold",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700),
                    fontFamily = FontFamily.Serif
                )
            }

            // Hero Crown / Peacock Feather Header Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF251A12)
                ),
                border = BorderStroke(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFFD700), Color(0xFFB8860B), Color(0xFFE27228))
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Golden Avatar with OM and Crown
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = Color(0xFFFFD700).copy(alpha = 0.15f),
                        border = BorderStroke(2.dp, Color(0xFFFFD700))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "👑",
                                fontSize = 36.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (appLanguage == AppLanguage.HINDI) "परम शांति व विज्ञापन-मुक्त अनुभव" else "Pure Spiritual Peace & 100% Ad-Free",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE082),
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (appLanguage == AppLanguage.HINDI)
                            "बिना किसी रुकावट या विज्ञापन के गीता ज्ञान में लीन हों और धर्म सेवा में सहभागी बनें।"
                        else
                            "Immerse in divine Bhagavad Gita wisdom with zero interruptions and support our spiritual mission.",
                        fontSize = 13.sp,
                        color = Color(0xFFD7CCC8),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    // Active Badge
                    AnimatedVisibility(
                        visible = isPremium,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF2E7D32).copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, Color(0xFF81C784)),
                            modifier = Modifier.padding(top = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (appLanguage == AppLanguage.HINDI) "आपकी Gold सदस्यता सक्रिय है" else "Gold Membership Active",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF81C784)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Benefits List
            Text(
                text = if (appLanguage == AppLanguage.HINDI) "Gold सदस्यता के मुख्य लाभ" else "Gold Member Benefits",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700),
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            BenefitRow(
                icon = Icons.Default.Diamond,
                title = if (appLanguage == AppLanguage.HINDI) "100% विज्ञापन-मुक्त (Ad-Free)" else "100% Ad-Free Experience",
                desc = if (appLanguage == AppLanguage.HINDI) "पूरे ऐप में कभी कोई बैनर या वीडियो ऐड नहीं दिखेगा।" else "No banner, interstitial, or video ads ever."
            )

            BenefitRow(
                icon = Icons.Default.Spa,
                title = if (appLanguage == AppLanguage.HINDI) "धर्म सेवा व ज्ञान प्रसार सहयोग" else "Support Dharma & Community",
                desc = if (appLanguage == AppLanguage.HINDI) "आपका सहयोग इस पवित्र ज्ञान को करोड़ों लोगों तक निःशुल्क पहुँचाने में मदद करता है।" else "Your contribution helps bring Gita wisdom freely to millions."
            )

            BenefitRow(
                icon = Icons.Default.Hearing,
                title = if (appLanguage == AppLanguage.HINDI) "शांत व एकाग्र गीता श्रवण" else "Uninterrupted Audio & Focus",
                desc = if (appLanguage == AppLanguage.HINDI) "बिना किसी बाधा के श्लोक पाठ और मधुर उच्चारण का आनंद लें।" else "Enjoy continuous Sanskrit chanting and translations peacefully."
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Plan Selection Cards
            Text(
                text = if (appLanguage == AppLanguage.HINDI) "अपनी योजना चुनें" else "Choose Your Plan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700),
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            // Yearly Card (Recommended - ₹501)
            PlanCard(
                title = if (appLanguage == AppLanguage.HINDI) "वार्षिक योजना (Yearly)" else "Annual Plan",
                price = yearlyPrice,
                period = if (appLanguage == AppLanguage.HINDI) "प्रति वर्ष (₹42/माह)" else "per year (₹42/mo)",
                badge = if (appLanguage == AppLanguage.HINDI) "58% बचत • शुभ 501" else "Save 58% • Best Value",
                isSelected = selectedPlan == GoldPlan.YEARLY,
                onClick = { selectedPlan = GoldPlan.YEARLY }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Monthly Card (₹101)
            PlanCard(
                title = if (appLanguage == AppLanguage.HINDI) "मासिक योजना (Monthly)" else "Monthly Plan",
                price = monthlyPrice,
                period = if (appLanguage == AppLanguage.HINDI) "प्रति माह" else "per month",
                badge = null,
                isSelected = selectedPlan == GoldPlan.MONTHLY,
                onClick = { selectedPlan = GoldPlan.MONTHLY }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Action Button (Google Play In-App Billing)
            Button(
                onClick = {
                    if (!isPremium) {
                        val activity = context as? Activity
                        if (activity != null) {
                            GitaBillingManager.launchBillingFlow(activity, selectedPlan) { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Activity context not available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Testing: toggle off in developer mode
                        GitaAdManager.setPremiumStatus(context, false)
                        val msg = if (appLanguage == AppLanguage.HINDI)
                            "Gold सदस्यता निष्क्रिय की गई (Free Mode)।"
                        else
                            "Gold membership deactivated (Free Mode)."
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFB300),
                    contentColor = Color(0xFF2C1802)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPremium) Icons.Default.CheckCircle else Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPremium) {
                            if (appLanguage == AppLanguage.HINDI) "Gold सक्रिय है (Free मोड टेस्ट करें)" else "Gold Active (Test Free Mode)"
                        } else {
                            if (selectedPlan == GoldPlan.YEARLY) {
                                if (appLanguage == AppLanguage.HINDI) "$yearlyPrice/वर्ष में Gold सदस्य बनें" else "Join Gold for $yearlyPrice/year"
                            } else {
                                if (appLanguage == AppLanguage.HINDI) "$monthlyPrice/माह में Gold सदस्य बनें" else "Join Gold for $monthlyPrice/month"
                            }
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (appLanguage == AppLanguage.HINDI)
                    "सुरक्षित भुगतान • किसी भी समय रद्द करें • Google Play सुरक्षा"
                else
                    "Secure Payment • Cancel Anytime • Google Play Protected",
                fontSize = 11.sp,
                color = Color(0xFF8D6E63),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Restore Purchases Button (Mandatory Google Play Policy Requirement)
            OutlinedButton(
                onClick = {
                    GitaBillingManager.restorePurchases { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                },
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = if (appLanguage == AppLanguage.HINDI) "🔄 पुरानी खरीदारी पुनर्स्थापित करें (Restore Purchases)" else "🔄 Restore Purchases",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun BenefitRow(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFFD700).copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFECB3)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = Color(0xFFBCAAA4),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF332418) else Color(0xFF1E150F)
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFFFFD700) else Color(0xFF4E342E)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE27228),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFF8E1)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = period,
                    fontSize = 12.sp,
                    color = Color(0xFFA1887F)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = price,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD700),
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}
