package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.KeyboardProApp

data class ScreenshotItem(
    val id: Int,
    val tag: String,
    val title: String,
    val description: String,
    val accentColor: Color,
    val gradientColors: List<Color>
)

@Composable
fun DashboardScreen(
    onNavigateToThemes: () -> Unit,
    onNavigateToClipboard: () -> Unit,
    onNavigateToShortcuts: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToLanguages: () -> Unit
) {
    val context = LocalContext.current
    val prefs = KeyboardProApp.instance.preferences
    val imm = remember { context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isSmallScreen = screenWidth < 360.dp

    var testInputText by remember { mutableStateOf("") }
    var wordsCount by remember { mutableStateOf(prefs.wordsTypedCount) }
    var selectedScreenshotIndex by remember { mutableStateOf<Int?>(null) }

    // Helper functions to check IME status
    fun isImeEnabled(): Boolean {
        return try {
            val list = imm?.enabledInputMethodList
            if (list != null && list.any { it.packageName == context.packageName }) {
                true
            } else {
                val enabledImes = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_INPUT_METHODS
                ) ?: ""
                enabledImes.contains(context.packageName)
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isImeDefault(): Boolean {
        return try {
            val defaultIme = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            ) ?: ""
            defaultIme.contains(context.packageName)
        } catch (e: Exception) {
            false
        }
    }

    var isEnabled by remember { mutableStateOf(isImeEnabled()) }
    var isDefault by remember { mutableStateOf(isImeDefault()) }

    // Refresh state when composable is active
    LaunchedEffect(Unit) {
        isEnabled = isImeEnabled()
        isDefault = isImeDefault()
        wordsCount = prefs.wordsTypedCount
    }

    val screenshots = remember {
        listOf(
            ScreenshotItem(
                id = 1,
                tag = "واجهة الكيبورد",
                title = "الكيبورد العربي الكامل",
                description = "حروف عربية مرتبة بعناية مع صف التشكيل السريع والأرقام العلوية وسرعة استجابة فائقة للمس.",
                accentColor = Color(0xFF0284C7),
                gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
            ),
            ScreenshotItem(
                id = 2,
                tag = "الترجمة الفورية",
                title = "المترجم المباشر",
                description = "ترجمة فورية أثناء الكتابة بين أكثر من 40 لغة بدون الحاجة لنسخ أو لصق أو مغادرة التطبيق.",
                accentColor = Color(0xFF10B981),
                gradientColors = listOf(Color(0xFF064E3B), Color(0xFF0F172A))
            ),
            ScreenshotItem(
                id = 3,
                tag = "الحافظة المتطورة",
                title = "الحافظة والتثبيت الذكي",
                description = "حفظ غير محدود للنصوص والروابط، تصنيف في مجلدات وتثبيت العبارات المهمة للوصول الفوري.",
                accentColor = Color(0xFF8B5CF6),
                gradientColors = listOf(Color(0xFF4C1D95), Color(0xFF0F172A))
            ),
            ScreenshotItem(
                id = 4,
                tag = "الفيسات والملصقات",
                title = "معرض الرموز والتعبيرات",
                description = "آلاف الإيموجي المصنفة بدقة، تعبيرات يابانية Kaomoji، ملصقات وصور متحركة GIF سريعة.",
                accentColor = Color(0xFFF59E0B),
                gradientColors = listOf(Color(0xFF78350F), Color(0xFF0F172A))
            ),
            ScreenshotItem(
                id = 5,
                tag = "التخصيص والثيمات",
                title = "الثيمات والوضع الليلي",
                description = "ثيمات داكنة OLED لحفظ البطارية، تدرجات ملونة، ألوان Material You الديناميكية وتحكم بارتفاع المفاتيح.",
                accentColor = Color(0xFFEC4899),
                gradientColors = listOf(Color(0xFF831843), Color(0xFF0F172A))
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (isSmallScreen) 12.dp else 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF0284C7))
                        )
                    )
                    .padding(if (isSmallScreen) 16.dp else 20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isSmallScreen) 42.dp else 48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⌨️", fontSize = if (isSmallScreen) 20.sp else 24.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "كيبورد محمد v1",
                                color = Color.White,
                                fontSize = if (isSmallScreen) 18.sp else 20.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "كيبورد احترافي متكامل لنظام Android",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FeatureBadge("🛡️ خصوصية 100%")
                        FeatureBadge("⚡ استجابة فورية")
                        FeatureBadge("✨ ترجمة وزخرفة")
                    }
                }
            }

            // SECTION: Screenshots & Previews Gallery (معاينة لوحة المفاتيح ولقطات الشاشة)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "معاينة الميزات ولقطات الشاشة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اسحب أفقياً 👈",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    itemsIndexed(screenshots) { index, item ->
                        ScreenshotThumbnailCard(
                            item = item,
                            onClick = { selectedScreenshotIndex = index }
                        )
                    }
                }
            }

            // Activation & Setup Card (حالة تفعيل لوحة المفاتيح)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "حالة تفعيل لوحة المفاتيح",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Step 1: Enable in Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (isEnabled) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                            Column {
                                Text(
                                    text = "1. التفعيل في إعدادات النظام",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isEnabled) "مفعّل وجاهز" else "غير مفعّل بعد",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEnabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isEnabled) "تعديل" else "تفعيل الآن",
                                fontSize = 11.sp,
                                color = if (isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Step 2: Set as Default
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isDefault) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (isDefault) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                            Column {
                                Text(
                                    text = "2. التعيين ككيبورد افتراضي",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isDefault) "لوحة المفاتيح الافتراضية الحالية" else "لوحة مفاتيح أخرى نشطة",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = {
                                imm?.showInputMethodPicker()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDefault) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isDefault) "تبديل" else "اختيار",
                                fontSize = 11.sp,
                                color = if (isDefault) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                            )
                        }
                    }
                }
            }

            // Interactive Sandbox / Playground
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "جرب الكيبورد هنا (Playground)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🔤 $wordsCount كلمة مكتوبة",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "اضغط على مربع النص أدناه لتجربة الكتابة السريعة بالعربية، الإنجليزية، التشكيل، والترجمة فوراً:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = testInputText,
                        onValueChange = {
                            testInputText = it
                            wordsCount = prefs.wordsTypedCount
                        },
                        placeholder = { Text("اكتب هنا للتجربة...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (testInputText.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { testInputText = "" }) {
                                Text("مسح النص")
                            }
                        }
                    }
                }
            }

            // Quick Navigation Grid (أقسام التخصيص والميزات)
            Text(
                text = "أقسام التخصيص والميزات",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "لغات الكيبورد",
                    subtitle = "العربية و40+ لغة عالمية",
                    icon = Icons.Default.Language,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLanguages
                )
                QuickNavCard(
                    title = "الثيمات والمظهر",
                    subtitle = "9 ثيمات وتخصيص كامل",
                    icon = Icons.Default.Palette,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToThemes
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "الحافظة الدائمة",
                    subtitle = "تثبيت وتصنيف النصوص",
                    icon = Icons.Default.ContentPaste,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToClipboard
                )
                QuickNavCard(
                    title = "اختصارات النصوص",
                    subtitle = "توسيع العبارات تلقائياً",
                    icon = Icons.Default.FlashOn,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToShortcuts
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "الإعدادات العامة",
                    subtitle = "الصوت، الاهتزاز، الحجم، الاقتراحات",
                    icon = Icons.Default.Settings,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSettings
                )
            }

            // Bottom Spacing ensuring NO content is EVER overlapped by system navigation or buttons
            Spacer(modifier = Modifier.height(28.dp))
        }

        // Modal Preview Dialog when a screenshot is tapped
        selectedScreenshotIndex?.let { index ->
            val currentItem = screenshots[index]
            ScreenshotPreviewDialog(
                item = currentItem,
                currentIndex = index,
                totalCount = screenshots.size,
                onDismiss = { selectedScreenshotIndex = null },
                onPrevious = {
                    selectedScreenshotIndex = if (index > 0) index - 1 else screenshots.size - 1
                },
                onNext = {
                    selectedScreenshotIndex = if (index < screenshots.size - 1) index + 1 else 0
                }
            )
        }
    }
}

@Composable
private fun ScreenshotThumbnailCard(
    item: ScreenshotItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Visual Preview Box preserving aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(item.gradientColors)
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(item.accentColor.copy(alpha = 0.25f))
                            .border(1.dp, item.accentColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.tag,
                            fontSize = 9.sp,
                            color = item.accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Keyboard simulation mock lines
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        repeat(3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(7) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color.White.copy(alpha = 0.25f))
                                    )
                                }
                            }
                        }
                        // Spacebar simulation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(item.accentColor.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }

            // Caption
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "اضغط للمعاينة",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ScreenshotPreviewDialog(
    item: ScreenshotItem,
    currentIndex: Int,
    totalCount: Int,
    onDismiss: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clickable(enabled = false) {}, // Prevent dismiss when clicking card
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with title, counter and close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Badge(
                                containerColor = item.accentColor.copy(alpha = 0.2f),
                                contentColor = item.accentColor
                            ) {
                                Text(
                                    text = "(${currentIndex + 1} / $totalCount)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Detailed preview mockup
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.verticalGradient(item.gradientColors))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.accentColor.copy(alpha = 0.3f))
                                    .border(1.dp, item.accentColor, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.tag,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = item.description,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Navigation Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onPrevious,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "السابق",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("السابق", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onNext,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("التالي", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "التالي",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
