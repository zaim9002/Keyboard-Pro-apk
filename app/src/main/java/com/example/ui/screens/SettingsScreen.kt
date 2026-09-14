package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onNavigateToLanguages: () -> Unit = {}
) {
    val prefs = KeyboardProApp.instance.preferences
    val langManager = KeyboardProApp.instance.languageManager
    val coroutineScope = rememberCoroutineScope()

    var keyboardHeight by remember { mutableStateOf(prefs.keyboardHeight) }
    var hapticFeedback by remember { mutableStateOf(prefs.hapticFeedback) }
    var keySound by remember { mutableStateOf(prefs.keySound) }
    var showNumberRow by remember { mutableStateOf(prefs.showNumberRow) }
    var doubleSpacePeriod by remember { mutableStateOf(prefs.doubleSpacePeriod) }
    var autoCapitalization by remember { mutableStateOf(prefs.autoCapitalization) }
    var showSuggestions by remember { mutableStateOf(prefs.showSuggestions) }
    var arabicNumerals by remember { mutableStateOf(prefs.arabicNumerals) }
    var isIncognito by remember { mutableStateOf(prefs.isIncognito) }
    var isGamingMode by remember { mutableStateOf(prefs.isGamingMode) }
    var oneHandedMode by remember { mutableStateOf(prefs.oneHandedMode) }
    var autoCorrectEnabled by remember { mutableStateOf(prefs.autoCorrectEnabled) }
    var aiTone by remember { mutableStateOf(prefs.aiTone) }
    var geminiApiKey by remember { mutableStateOf(prefs.geminiApiKey) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Privacy & Security Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "الخصوصية والأمان أولاً (Local & Private)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "• يعمل الكيبورد محلياً بنسبة 100% داخل جهازك دون إرسال نصوص إلى أي خوادم خارجية.\n" +
                            "• يتم تفعيل وضع التصفح الخفي تلقائياً في حقول كلمات المرور ولا يتم حفظها في الحافظة أو القاموس إطلاقاً.\n" +
                            "• بياناتك الشخصية واختصاراتك تبقى بأمان تام في قاعدة بياناتك المحلية.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )
            }
        }

        // Languages Management Card
        SettingsGroupTitle("اللغات وحزم الكتابة")
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text("مدير اللغات والحزم العالمية", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "اللغة الحالية: ${langManager.getLanguageInfo(prefs.currentLanguage)?.nameArabic ?: "العربية"} (${prefs.enabledLanguages.size} لغات مفعلة)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onNavigateToLanguages,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إدارة اللغات", fontSize = 12.sp)
                    }
                }
            }
        }

        // Appearance & Sizing
        SettingsGroupTitle("المظهر والارتفاع")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Keyboard Height
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ارتفاع الكيبورد", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("الحجم الحالي: $keyboardHeight", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Small", "Medium", "Large").forEach { size ->
                            FilterChip(
                                selected = keyboardHeight == size,
                                onClick = {
                                    keyboardHeight = size
                                    prefs.keyboardHeight = size
                                },
                                label = {
                                    Text(
                                        when (size) {
                                            "Small" -> "صغير"
                                            "Medium" -> "متوسط"
                                            else -> "كبير"
                                        },
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // One-handed mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("وضع اليد الواحدة", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("تقريب الكيبورد لليد المفضلة", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                // Show number row
                SettingsSwitchRow(
                    title = "صف الأرقام العلوي",
                    subtitle = "عرض شريط الأرقام دائماً في أعلى اللوحة",
                    checked = showNumberRow,
                    onCheckedChange = {
                        showNumberRow = it
                        prefs.showNumberRow = it
                    }
                )
            }
        }

        // Feedback & Typing
        SettingsGroupTitle("الاهتزاز والصوت والكتابة")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("الاهتزاز اللمسي (Haptic)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("قوة استجابة اللمس: $hapticFeedback", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Off", "Light", "Medium", "Strong").forEach { level ->
                            FilterChip(
                                selected = hapticFeedback == level,
                                onClick = {
                                    hapticFeedback = level
                                    prefs.hapticFeedback = level
                                },
                                label = {
                                    Text(
                                        when (level) {
                                            "Off" -> "إيقاف"
                                            "Light" -> "خفيف"
                                            "Medium" -> "وسط"
                                            else -> "قوي"
                                        },
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Key Sound
                SettingsSwitchRow(
                    title = "صوت النقر على المفاتيح",
                    subtitle = "إصدار نغمة خفيفة عند كتابة الأحرف",
                    checked = keySound != "Off",
                    onCheckedChange = {
                        val sound = if (it) "Light" else "Off"
                        keySound = sound
                        prefs.keySound = sound
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Double space period
                SettingsSwitchRow(
                    title = "نقطة بالمسافة المزدوجة",
                    subtitle = "الضغط مرتين على المسافة يضع نقطة ومسافة",
                    checked = doubleSpacePeriod,
                    onCheckedChange = {
                        doubleSpacePeriod = it
                        prefs.doubleSpacePeriod = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Auto capitalization
                SettingsSwitchRow(
                    title = "التكبير التلقائي (Auto Capitalization)",
                    subtitle = "تكبير أول حرف بعد النقطة في اللغة الإنجليزية",
                    checked = autoCapitalization,
                    onCheckedChange = {
                        autoCapitalization = it
                        prefs.autoCapitalization = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Word Suggestions
                SettingsSwitchRow(
                    title = "إظهار اقتراحات الكلمات",
                    subtitle = "عرض الكلمات المتوقعة والإكمال التلقائي أثناء الكتابة",
                    checked = showSuggestions,
                    onCheckedChange = {
                        showSuggestions = it
                        prefs.showSuggestions = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Arabic Numerals
                SettingsSwitchRow(
                    title = "الأرقام المشرقية (٠١٢٣٤٥٦٧٨٩)",
                    subtitle = "استخدام الأرقام العربية ٠-٩ في صف أرقام اللوحة العربية",
                    checked = arabicNumerals,
                    onCheckedChange = {
                        arabicNumerals = it
                        prefs.arabicNumerals = it
                    }
                )
            }
        }

        // AI & Smart Correction
        SettingsGroupTitle("الذكاء الاصطناعي والتصحيح الذكي")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Auto Correct Switch
                SettingsSwitchRow(
                    title = "التصحيح التلقائي للأخطاء (Auto-Correct)",
                    subtitle = "تصحيح الأخطاء الإملائية الشائعة والهمزات والتاء المربوطة تلقائياً عند الضغط على المسافة",
                    checked = autoCorrectEnabled,
                    onCheckedChange = {
                        autoCorrectEnabled = it
                        prefs.autoCorrectEnabled = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Default Tone
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("نبرة الكتابة الافتراضية", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text("اختر النبرة المفضلة لإعادة صياغة النصوص في المساعد الذكي", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "FORMAL" to "🎩 رسمي",
                            "PROFESSIONAL" to "💼 احترافي",
                            "FRIENDLY" to "🌸 ودود",
                            "CONCISE" to "⚡ موجز",
                            "POETIC" to "📜 أدبي",
                            "CASUAL_EMOJI" to "🎉 مرح",
                            "PERSUASIVE" to "🤝 مقنع"
                        ).forEach { (toneKey, toneLabel) ->
                            FilterChip(
                                selected = aiTone == toneKey,
                                onClick = {
                                    aiTone = toneKey
                                    prefs.aiTone = toneKey
                                },
                                label = { Text(toneLabel, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Gemini API Key
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("مفتاح Google Gemini API", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        val status = if (geminiApiKey.isNotBlank()) "تم تعيين مفتاح خاص ✓" else "يعمل بنموذج الذكاء المحلي فائق السرعة"
                        Text(status, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    OutlinedButton(
                        onClick = { showApiKeyDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("تعديل", fontSize = 11.sp)
                    }
                }
            }
        }

        // Modes & Management
        SettingsGroupTitle("الأوضاع الخاصة وإدارة البيانات")
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Incognito Mode
                SettingsSwitchRow(
                    title = "وضع التصفح الخفي الدائم",
                    subtitle = "تعطيل حفظ الكلمات الجديدة والحافظة تماماً",
                    checked = isIncognito,
                    onCheckedChange = {
                        isIncognito = it
                        prefs.isIncognito = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Gaming mode
                SettingsSwitchRow(
                    title = "وضع الألعاب (Gaming Mode)",
                    subtitle = "تقليل الحجم وإيقاف الاقتراحات المشتتة أثناء الألعاب",
                    checked = isGamingMode,
                    onCheckedChange = {
                        isGamingMode = it
                        prefs.isGamingMode = it
                    }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Reset Defaults
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("إعادة ضبط الإعدادات", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("استعادة الخيارات الافتراضية للكيبورد", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("إعادة ضبط", fontSize = 11.sp)
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("تأكيد إعادة الضبط") },
            text = { Text("هل تريد بالتأكيد استعادة الإعدادات الافتراضية للوحة المفاتيح؟") },
            confirmButton = {
                Button(
                    onClick = {
                        prefs.theme = "Midnight"
                        prefs.keyboardHeight = "Medium"
                        prefs.hapticFeedback = "Light"
                        prefs.keySound = "Off"
                        prefs.showNumberRow = true
                        prefs.doubleSpacePeriod = true
                        prefs.autoCapitalization = true
                        prefs.isIncognito = false
                        prefs.isGamingMode = false
                        prefs.oneHandedMode = "OFF"

                        keyboardHeight = "Medium"
                        hapticFeedback = "Light"
                        keySound = "Off"
                        showNumberRow = true
                        doubleSpacePeriod = true
                        autoCapitalization = true
                        isIncognito = false
                        isGamingMode = false
                        oneHandedMode = "OFF"

                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("نعم، إعادة الضبط")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showApiKeyDialog) {
        var tempKey by remember { mutableStateOf(geminiApiKey) }
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("مفتاح Google Gemini API") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "أدخل مفتاح Gemini API الخاص بك لتفعيل معالجة الصياغة والنبرة المتقدمة عبر أحدث نماذج Gemini. اتركه فارغاً للاعتماد على الذكاء المحلي فائق السرعة وبدون إنترنت.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        placeholder = { Text("AIzaSy...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        geminiApiKey = tempKey.trim()
                        prefs.geminiApiKey = tempKey.trim()
                        showApiKeyDialog = false
                    }
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun SettingsGroupTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
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
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
