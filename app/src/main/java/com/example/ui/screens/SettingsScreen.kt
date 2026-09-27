package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.KeyboardProApp
import com.example.voice.VoiceInputActivity

@Composable
fun SettingsScreen(
    onNavigateToLanguages: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = KeyboardProApp.instance.preferences
    val langManager = KeyboardProApp.instance.languageManager

    // Preferences states
    var keyboardHeight by remember { mutableStateOf(prefs.keyboardHeight) }
    var heightPercent by remember { mutableStateOf(prefs.keyboardHeightPercent) }
    var widthPercent by remember { mutableStateOf(prefs.keyboardWidthPercent) }
    var keyFontSizeSp by remember { mutableStateOf(prefs.keyFontSizeSp) }
    var secondaryFontSizeSp by remember { mutableStateOf(prefs.secondaryFontSizeSp) }
    var keyCornerRadiusDp by remember { mutableStateOf(prefs.keyCornerRadiusDp) }
    var keyStrokeBorderEnabled by remember { mutableStateOf(prefs.keyStrokeBorderEnabled) }
    var keyGapDp by remember { mutableStateOf(prefs.keyGapDp) }

    var keySound by remember { mutableStateOf(prefs.keySound) }
    var keySoundType by remember { mutableStateOf(prefs.keySoundType) }
    var keySoundVolume by remember { mutableStateOf(prefs.keySoundVolume) }
    var hapticFeedback by remember { mutableStateOf(prefs.hapticFeedback) }
    var hapticDurationMs by remember { mutableStateOf(prefs.hapticDurationMs) }

    var showNumberRow by remember { mutableStateOf(prefs.showNumberRow) }
    var showArrowRow by remember { mutableStateOf(prefs.showArrowRow) }
    var showBottomRowSymbols by remember { mutableStateOf(prefs.showBottomRowSymbols) }
    var showKeyPreview by remember { mutableStateOf(prefs.showKeyPreview) }
    var bottomChinPadding by remember { mutableStateOf(prefs.bottomChinPadding) }
    var showToolbarUndoRedo by remember { mutableStateOf(prefs.showToolbarUndoRedo) }

    var doubleSpacePeriod by remember { mutableStateOf(prefs.doubleSpacePeriod) }
    var autoCapitalization by remember { mutableStateOf(prefs.autoCapitalization) }
    var showSuggestions by remember { mutableStateOf(prefs.showSuggestions) }
    var autoCorrectEnabled by remember { mutableStateOf(prefs.autoCorrectEnabled) }
    var arabicNumerals by remember { mutableStateOf(prefs.arabicNumerals) }
    var spacebarLanguageSwitch by remember { mutableStateOf(prefs.spacebarLanguageSwitchEnabled) }
    var oneHandedMode by remember { mutableStateOf(prefs.oneHandedMode) }

    var emojiStyle by remember { mutableStateOf(prefs.emojiStyle) }
    var clipboardRetentionDays by remember { mutableStateOf(prefs.clipboardRetentionDays) }
    var clipboardAutoClean by remember { mutableStateOf(prefs.clipboardAutoClean) }

    var isIncognito by remember { mutableStateOf(prefs.isIncognito) }
    var isGamingMode by remember { mutableStateOf(prefs.isGamingMode) }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Voice Typing & Microphone Setup Card (حل مشكلة الكتابة بالصوت)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (hasMicPermission) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFF78350F).copy(alpha = 0.4f)
            ),
            modifier = Modifier.border(
                1.dp,
                if (hasMicPermission) Color(0xFF10B981).copy(alpha = 0.5f) else Color(0xFFF59E0B).copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (hasMicPermission) Color(0xFF10B981) else Color(0xFFF59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "الكتابة بالصوت (Voice Typing)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (hasMicPermission) "صلاحية الميكروفون مفعّلة ✓ والكتابة جاهزة" else "مطلوب منح إذن الميكروفون لتعمل الكتابة بالصوت",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(context, VoiceInputActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(if (hasMicPermission) "تجربة الصوت" else "تفعيل الآن", fontSize = 11.sp)
                    }
                }
            }
        }

        // 1. إرتفاع الكيبورد وحجم الأحرف
        SettingsGroupTitle("إرتفاع الكيبورد وحجم الأحرف")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Height Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("إرتفاع الكيبورد: $heightPercent%", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        TextButton(onClick = {
                            heightPercent = 100
                            prefs.keyboardHeightPercent = 100
                            prefs.keyboardHeight = "Medium"
                            keyboardHeight = "Medium"
                        }) {
                            Text("إعادة للافتراضي", fontSize = 11.sp)
                        }
                    }
                    Slider(
                        value = heightPercent.toFloat(),
                        onValueChange = {
                            heightPercent = it.toInt()
                            prefs.keyboardHeightPercent = it.toInt()
                        },
                        valueRange = 70f..140f
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Width Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("عرض الكيبورد: $widthPercent%", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(80, 90, 100).forEach { w ->
                                FilterChip(
                                    selected = widthPercent == w,
                                    onClick = {
                                        widthPercent = w
                                        prefs.keyboardWidthPercent = w
                                    },
                                    label = { Text("$w%", fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                    Slider(
                        value = widthPercent.toFloat(),
                        onValueChange = {
                            widthPercent = it.toInt()
                            prefs.keyboardWidthPercent = it.toInt()
                        },
                        valueRange = 70f..100f
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Primary Key Font Size
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("حجم خط الأحرف على المفاتيح: ${keyFontSizeSp}sp", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(16 to "صغير", 19 to "وسط", 23 to "كبير").forEach { (sz, lbl) ->
                                FilterChip(
                                    selected = keyFontSizeSp == sz,
                                    onClick = {
                                        keyFontSizeSp = sz
                                        prefs.keyFontSizeSp = sz
                                    },
                                    label = { Text(lbl, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                    Slider(
                        value = keyFontSizeSp.toFloat(),
                        onValueChange = {
                            keyFontSizeSp = it.toInt()
                            prefs.keyFontSizeSp = it.toInt()
                        },
                        valueRange = 14f..26f
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Secondary Hint Font Size
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("حجم الرموز الثانوية والتشكيل: ${secondaryFontSizeSp}sp", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Slider(
                        value = secondaryFontSizeSp.toFloat(),
                        onValueChange = {
                            secondaryFontSizeSp = it.toInt()
                            prefs.secondaryFontSizeSp = it.toInt()
                        },
                        valueRange = 7f..14f
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Key Corner Radius
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("انحناء زوايا المفاتيح: ${keyCornerRadiusDp}dp", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Slider(
                        value = keyCornerRadiusDp.toFloat(),
                        onValueChange = {
                            keyCornerRadiusDp = it.toInt()
                            prefs.keyCornerRadiusDp = it.toInt()
                        },
                        valueRange = 2f..16f
                    )
                }

                // Live Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(keyCornerRadiusDp.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            if (keyStrokeBorderEnabled) 1.dp else 0.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(keyCornerRadiusDp.dp)
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "معاينة حجم الأحرف: ض ١   س ٢   ع ٣",
                        fontSize = keyFontSizeSp.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 2. الصوت والإهتزاز
        SettingsGroupTitle("الصوت والإهتزاز")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Key Sound Switch
                SettingsSwitchRow(
                    title = "صوت النقر على المفاتيح",
                    subtitle = "إصدار نغمة مسموعة عند لمس كل زر",
                    checked = keySound != "Off",
                    onCheckedChange = {
                        val sound = if (it) "Light" else "Off"
                        keySound = sound
                        prefs.keySound = sound
                    }
                )

                if (keySound != "Off") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("نوع نغمة المفاتيح", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "CLICK" to "نقرة (Click)",
                                "STANDARD" to "قياسي (Standard)",
                                "TYPEWRITER" to "آلة كاتبة",
                                "WOOD" to "خشب (Wood)",
                                "WATER_DROP" to "قطرة ماء"
                            ).forEach { (type, lbl) ->
                                FilterChip(
                                    selected = keySoundType == type,
                                    onClick = {
                                        keySoundType = type
                                        prefs.keySoundType = type
                                    },
                                    label = { Text(lbl, fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("مستوى صوت المفاتيح: $keySoundVolume%", fontSize = 12.sp)
                        Slider(
                            value = keySoundVolume.toFloat(),
                            onValueChange = {
                                keySoundVolume = it.toInt()
                                prefs.keySoundVolume = it.toInt()
                            },
                            valueRange = 10f..100f
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Haptic Feedback
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("قوة الاهتزاز عند اللمس (Haptic Feedback)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Off" to "إيقاف",
                            "Light" to "خفيف",
                            "Medium" to "متوسط",
                            "Strong" to "قوي"
                        ).forEach { (level, lbl) ->
                            FilterChip(
                                selected = hapticFeedback == level,
                                onClick = {
                                    hapticFeedback = level
                                    prefs.hapticFeedback = level
                                },
                                label = { Text(lbl, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (hapticFeedback != "Off") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("مدة الاهتزاز بالمللي ثانية: ${hapticDurationMs}ms", fontSize = 12.sp)
                        Slider(
                            value = hapticDurationMs.toFloat(),
                            onValueChange = {
                                hapticDurationMs = it.toInt()
                                prefs.hapticDurationMs = it.toInt()
                            },
                            valueRange = 5f..80f
                        )
                    }
                }
            }
        }

        // 3. تخطيط لوحة المفاتيح
        SettingsGroupTitle("تخطيط لوحة المفاتيح والأزرار")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsSwitchRow(
                    title = "إبراز حدود المفاتيح (Key Borders)",
                    subtitle = "رسم حد أنيق حول كل زر كما في صور Transboard الاحترافية",
                    checked = keyStrokeBorderEnabled,
                    onCheckedChange = {
                        keyStrokeBorderEnabled = it
                        prefs.keyStrokeBorderEnabled = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // One handed mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("وضع اليد الواحدة", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("إزاحة الكيبورد لليسار أو اليمين لتسهيل الكتابة بيد واحدة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("OFF" to "إيقاف", "LEFT" to "يسار", "RIGHT" to "يمين").forEach { (mode, label) ->
                            FilterChip(
                                selected = oneHandedMode == mode,
                                onClick = {
                                    oneHandedMode = mode
                                    prefs.oneHandedMode = mode
                                },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Key Preview Popup
                SettingsSwitchRow(
                    title = "معاينة الحرف المنبثق (Key Preview)",
                    subtitle = "ظهور الحرف بشكل مكبّر فور الضغط على الزر مع سرعة استجابة فائقة",
                    checked = showKeyPreview,
                    onCheckedChange = {
                        showKeyPreview = it
                        prefs.showKeyPreview = it
                    }
                )
            }
        }

        // 4. الإبتسامات والفيسات
        SettingsGroupTitle("الإبتسامات والفيسات (Emoji)")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("مظهر وحزمة السمايلات", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("اختر مظهر الإيموجي المفضل لديك في لوحة المفاتيح", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("iOS 27", "iOS 26", "Google Modern", "Classic").forEach { style ->
                            FilterChip(
                                selected = emojiStyle == style,
                                onClick = {
                                    emojiStyle = style
                                    prefs.emojiStyle = style
                                },
                                label = { Text(style, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 5. الحافظة
        SettingsGroupTitle("الحافظة (Clipboard)")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("مدة حفظ النصوص المنسوخة", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("$clipboardRetentionDays يوماً قبل مسح النصوص غير المثبتة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(7, 30, 90).forEach { days ->
                            FilterChip(
                                selected = clipboardRetentionDays == days,
                                onClick = {
                                    clipboardRetentionDays = days
                                    prefs.clipboardRetentionDays = days
                                },
                                label = { Text("$days يوم", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "التنظيف التلقائي للنصوص غير المثبتة",
                    subtitle = "مسح النصوص العادية تلقائياً بعد انقضاء المدة المحددة مع الحفاظ على المثبتات",
                    checked = clipboardAutoClean,
                    onCheckedChange = {
                        clipboardAutoClean = it
                        prefs.clipboardAutoClean = it
                    }
                )
            }
        }

        // 6. الاختصارات
        SettingsGroupTitle("الاختصارات (Shortcuts)")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "تتيح لك الاختصارات كتابة كلمات قصيرة مثل (سلام) لتتحول فوراً إلى (السلام عليكم ورحمة الله وبركاته) تلقائياً عند الضغط على المسافة.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            }
        }

        // 7. الكتابة والتصحيح
        SettingsGroupTitle("الكتابة والتصحيح التلقائي")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsSwitchRow(
                    title = "إظهار شريط الاقتراحات",
                    subtitle = "عرض الكلمات المتوقعة والإكمال التلقائي أثناء الكتابة",
                    checked = showSuggestions,
                    onCheckedChange = {
                        showSuggestions = it
                        prefs.showSuggestions = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "التصحيح التلقائي للأخطاء (Auto-Correct)",
                    subtitle = "تصحيح الأخطاء الإملائية والهمزات والتاء المربوطة تلقائياً عند الضغط على المسافة",
                    checked = autoCorrectEnabled,
                    onCheckedChange = {
                        autoCorrectEnabled = it
                        prefs.autoCorrectEnabled = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "التكبير التلقائي (Auto Capitalization)",
                    subtitle = "تكبير أول حرف بعد النقطة تلقائياً باللغة الإنجليزية",
                    checked = autoCapitalization,
                    onCheckedChange = {
                        autoCapitalization = it
                        prefs.autoCapitalization = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "نقطة بالمسافة المزدوجة",
                    subtitle = "الضغط مرتين متتاليتين على المسافة يدرج نقطة ومسافة",
                    checked = doubleSpacePeriod,
                    onCheckedChange = {
                        doubleSpacePeriod = it
                        prefs.doubleSpacePeriod = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "الأرقام المشرقية (٠١٢٣٤٥٦٧٨٩)",
                    subtitle = "استخدام الأرقام العربية ٠-٩ في اللوحة العربية",
                    checked = arabicNumerals,
                    onCheckedChange = {
                        arabicNumerals = it
                        prefs.arabicNumerals = it
                    }
                )
            }
        }

        // 8. الصف السفلي والعلوي وشريط الأدوات
        SettingsGroupTitle("الصف السفلي والعلوي وشريط الأدوات")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsSwitchRow(
                    title = "صف الأرقام العلوي",
                    subtitle = "عرض صف الأرقام بشكل دائم في أعلى الحروف",
                    checked = showNumberRow,
                    onCheckedChange = {
                        showNumberRow = it
                        prefs.showNumberRow = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "صف الأسهم للتنقل (Arrow Navigation Row)",
                    subtitle = "إظهار صف أسهم للتنقل السريع بين الكلمات والحروف والبداية والنهاية",
                    checked = showArrowRow,
                    onCheckedChange = {
                        showArrowRow = it
                        prefs.showArrowRow = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "أزرار التراجع والإعادة والبحث بشريط الأدوات",
                    subtitle = "إظهار أزرار Undo و Redo في شريط الأدوات العلوي",
                    checked = showToolbarUndoRedo,
                    onCheckedChange = {
                        showToolbarUndoRedo = it
                        prefs.showToolbarUndoRedo = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Bottom Chin Clearance
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("رفع الكيبورد عن حافة الشاشة السفلية", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "AUTO" to "تلقائي (موصى به)",
                            "SMALL" to "صغيرة (16dp)",
                            "MEDIUM" to "متوسطة (28dp)",
                            "LARGE" to "كبيرة (40dp)",
                            "NONE" to "بدون مسافة"
                        ).forEach { (pad, label) ->
                            FilterChip(
                                selected = bottomChinPadding == pad,
                                onClick = {
                                    bottomChinPadding = pad
                                    prefs.bottomChinPadding = pad
                                },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SettingsGroupTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
