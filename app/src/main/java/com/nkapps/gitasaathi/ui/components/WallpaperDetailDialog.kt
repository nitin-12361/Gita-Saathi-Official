package com.nkapps.gitasaathi.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nkapps.gitasaathi.data.AppLanguage
import com.nkapps.gitasaathi.data.GitaWallpaper
import com.nkapps.gitasaathi.utils.WallpaperHelper
import com.nkapps.gitasaathi.utils.WallpaperTarget
import kotlinx.coroutines.launch

@Composable
fun WallpaperDetailDialog(
    wallpaper: GitaWallpaper,
    appLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isHindi = appLanguage == AppLanguage.HINDI

    var showQuote by remember { mutableStateOf(true) }
    var showTargetSelector by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("wallpaper_detail_dialog")
        ) {
            // Full screen background wallpaper
            Image(
                painter = painterResource(id = wallpaper.drawableResId),
                contentDescription = wallpaper.titleEnglish,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Top scrim & Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 36.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("wallpaper_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isHindi) wallpaper.titleHindi else wallpaper.titleEnglish,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "HD 4K Divine Wallpaper",
                            fontSize = 11.sp,
                            color = Color(0xFFFFD8A8)
                        )
                    }

                    // Toggle Quote Button
                    IconButton(
                        onClick = { showQuote = !showQuote },
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (showQuote) Color(0xFFFF7A00).copy(alpha = 0.85f)
                                else Color.Black.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .testTag("wallpaper_toggle_quote_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Toggle Quote",
                            tint = Color.White
                        )
                    }
                }
            }

            // Bottom Shloka Card & Actions Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 24.dp)
            ) {
                // Sacred Shloka Quote Display
                AnimatedVisibility(
                    visible = showQuote,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1E1610).copy(alpha = 0.88f),
                        border = BorderStroke(1.dp, Color(0xFFE5A93C).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "॥ ${wallpaper.shlokaReference} ॥",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBD38D),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = wallpaper.shlokaSanskrit,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isHindi) wallpaper.shlokaMeaningHindi else wallpaper.shlokaMeaningEnglish,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                textAlign = TextAlign.Center,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row: [Save] [Share] [SET WALLPAPER]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save to Gallery
                    IconButton(
                        onClick = {
                            if (!isProcessing) {
                                isProcessing = true
                                coroutineScope.launch {
                                    val bitmap = WallpaperHelper.renderWallpaperBitmap(context, wallpaper, showQuote)
                                    val result = WallpaperHelper.saveToGallery(context, bitmap, wallpaper.id)
                                    isProcessing = false
                                    if (result.isSuccess) {
                                        Toast.makeText(
                                            context,
                                            if (isHindi) "✅ गैलरी (Pictures/GitaSaathi) में सेव हो गया!" else "✅ Saved to Pictures/GitaSaathi!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            if (isHindi) "सेव करने में त्रुटि हुई" else "Failed to save wallpaper",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .testTag("wallpaper_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Save",
                            tint = Color.White
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val bitmap = WallpaperHelper.renderWallpaperBitmap(context, wallpaper, showQuote)
                            val title = if (isHindi) wallpaper.titleHindi else wallpaper.titleEnglish
                            val quote = "${wallpaper.shlokaReference}\n${wallpaper.shlokaSanskrit}\n\n${if (isHindi) wallpaper.shlokaMeaningHindi else wallpaper.shlokaMeaningEnglish}"
                            WallpaperHelper.shareWallpaper(context, bitmap, title, quote)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                            .testTag("wallpaper_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }

                    // Primary Set Wallpaper Button
                    Button(
                        onClick = { showTargetSelector = true },
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7A00),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("wallpaper_set_button")
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "वॉलपेपर लग रहा है..." else "Applying...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Wallpaper,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "वॉलपेपर लगाएं 📲" else "Set Wallpaper 📲",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Target Selector Modal Dialog (Home Screen / Lock Screen / Both)
            if (showTargetSelector) {
                Dialog(onDismissRequest = { showTargetSelector = false }) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFF1E1610),
                        border = BorderStroke(1.dp, Color(0xFFFFD8A8).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isHindi) "कहाँ लगाना चाहते हैं?" else "Choose Wallpaper Location",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBD38D)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Option 1: Home Screen
                            WallpaperTargetOption(
                                title = if (isHindi) "📱 होम स्क्रीन (Home Screen)" else "📱 Home Screen",
                                subtitle = if (isHindi) "मुख्य स्क्रीन पर वॉलपेपर सेट करें" else "Apply on main home screen",
                                onClick = {
                                    showTargetSelector = false
                                    isProcessing = true
                                    coroutineScope.launch {
                                        val bitmap = WallpaperHelper.renderWallpaperBitmap(context, wallpaper, showQuote)
                                        val res = WallpaperHelper.setPhoneWallpaper(context, bitmap, WallpaperTarget.HOME_SCREEN)
                                        isProcessing = false
                                        if (res.isSuccess) {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "✨ होम स्क्रीन पर वॉलपेपर सफलतापूर्वक लगाया गया!" else "✨ Home Screen wallpaper set successfully!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "वॉलपेपर लगाने में त्रुटि हुई" else "Failed to apply wallpaper",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Option 2: Lock Screen
                            WallpaperTargetOption(
                                title = if (isHindi) "🔒 लॉक स्क्रीन (Lock Screen)" else "🔒 Lock Screen",
                                subtitle = if (isHindi) "फोन अनलॉक करने से पहले दर्शन" else "Apply on phone lock screen",
                                onClick = {
                                    showTargetSelector = false
                                    isProcessing = true
                                    coroutineScope.launch {
                                        val bitmap = WallpaperHelper.renderWallpaperBitmap(context, wallpaper, showQuote)
                                        val res = WallpaperHelper.setPhoneWallpaper(context, bitmap, WallpaperTarget.LOCK_SCREEN)
                                        isProcessing = false
                                        if (res.isSuccess) {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "✨ लॉक स्क्रीन पर वॉलपेपर सफलतापूर्वक लगाया गया!" else "✨ Lock Screen wallpaper set successfully!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "वॉलपेपर लगाने में त्रुटि हुई" else "Failed to apply wallpaper",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Option 3: Both
                            WallpaperTargetOption(
                                title = if (isHindi) "📱🔒 दोनों (Home & Lock Screen)" else "📱🔒 Both Screens",
                                subtitle = if (isHindi) "होम और लॉक दोनों स्क्रीन पर लगाएं" else "Apply on both Home & Lock screens",
                                onClick = {
                                    showTargetSelector = false
                                    isProcessing = true
                                    coroutineScope.launch {
                                        val bitmap = WallpaperHelper.renderWallpaperBitmap(context, wallpaper, showQuote)
                                        val res = WallpaperHelper.setPhoneWallpaper(context, bitmap, WallpaperTarget.BOTH)
                                        isProcessing = false
                                        if (res.isSuccess) {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "✨ दोनों स्क्रीन पर पावन वॉलपेपर लगाया गया!" else "✨ Wallpaper applied to both screens successfully!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "वॉलपेपर लगाने में त्रुटि हुई" else "Failed to apply wallpaper",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = { showTargetSelector = false },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = if (isHindi) "रद्द करें" else "Cancel", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperTargetOption(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF2D2218),
        border = BorderStroke(1.dp, Color(0xFFE5A93C).copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }
    }
}
