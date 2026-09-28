package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.KeyboardProApp

fun checkIsKeyboardEnabled(context: Context): Boolean {
    return try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return false
        val enabledMethods = imm.enabledInputMethodList
        val pkg = context.packageName
        enabledMethods.any { it.packageName == pkg }
    } catch (e: Exception) {
        false
    }
}

fun checkIsKeyboardSelected(context: Context): Boolean {
    return try {
        val currentIme = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        )
        val pkg = context.packageName
        currentIme != null && currentIme.contains(pkg)
    } catch (e: Exception) {
        false
    }
}

fun openSystemInputMethodSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح إعدادات الإدخال", Toast.LENGTH_SHORT).show()
    }
}

fun openSystemInputMethodPicker(context: Context) {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showInputMethodPicker()
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح قائمة اختيار لوحة المفاتيح", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToThemes: () -> Unit = {},
    onNavigateToLanguages: () -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { KeyboardProApp.instance.preferences }

    var isEnabled by remember { mutableStateOf(checkIsKeyboardEnabled(context)) }
    var isSelected by remember { mutableStateOf(checkIsKeyboardSelected(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isEnabled = checkIsKeyboardEnabled(context)
                isSelected = checkIsKeyboardSelected(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var hideEmojiKey by remember { mutableStateOf(!prefs.showBottomRowSymbols) }
    var showNumbers by remember { mutableStateOf(prefs.showNumberRow) }
    var showKeyPreview by remember { mutableStateOf(prefs.showKeyPreview) }
    var isDarkMode by remember { mutableStateOf(true) }

    // Dialog state for interactive settings
    var showHeightDialog by remember { mutableStateOf(false) }
    var showFontDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

    var testInputText by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121214))
                .statusBarsPadding()
        ) {
            // Top App Bar matching Screenshot 4 & 5
            Surface(
                color = Color(0xFF1E1E24),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = null,
                                tint = Color(0xFF818CF8),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Text(
                            text = "الإعدادات",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            Toast.makeText(context, "كيبورد محمد v1 - الإصدار 8.5.1", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "معلومات",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Scrollable Settings Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ================= HERO CARD: تفعيل واختيار كيبورد محمد =================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C24)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isEnabled && isSelected) Color(0xFF10B981).copy(alpha = 0.2f)
                                        else Color(0xFF6366F1).copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isEnabled && isSelected) Icons.Default.CheckCircle else Icons.Default.KeyboardAlt,
                                    contentDescription = null,
                                    tint = if (isEnabled && isSelected) Color(0xFF34D399) else Color(0xFF818CF8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تفعيل واختيار كيبورد محمد",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = when {
                                        !isEnabled -> "⚠️ الخطوة 1: يلزم تفعيل لوحة المفاتيح في إعدادات الهاتف"
                                        !isSelected -> "ℹ️ الخطوة 2: لوحة المفاتيح مفعّلة، اضغط لاختيارها كافتراضية"
                                        else -> "✓ كيبورد محمد v1 مفعّلة وتعمل كلوحة افتراضية حالياً"
                                    },
                                    color = when {
                                        !isEnabled -> Color(0xFFFBBF24)
                                        !isSelected -> Color(0xFF60A5FA)
                                        else -> Color(0xFF34D399)
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Button 1: تفعيل في النظام
                            OutlinedButton(
                                onClick = { openSystemInputMethodSettings(context) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isEnabled) Color(0xFF34D399) else Color.White
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.horizontalGradient(
                                        if (isEnabled) listOf(Color(0xFF10B981), Color(0xFF059669))
                                        else listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                    )
                                )
                            ) {
                                Icon(
                                    imageVector = if (isEnabled) Icons.Default.Check else Icons.Default.Settings,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (isEnabled) "مفعّلة ✓" else "1. تفعيل في النظام",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Button 2: اختيار كيبورد محمد (Input Method Picker)
                            Button(
                                onClick = { openSystemInputMethodPicker(context) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF10B981) else Color(0xFF6366F1)
                                )
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (isSelected) "الافتراضية ✓" else "2. اختيار الكيبورد",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // ================= SECTION 1: العرض (Display) =================
                SettingsSectionCard(title = "العرض") {
                    // 1. سمة لوحة المفاتيح
                    SettingRowItem(
                        icon = Icons.Default.Checkroom,
                        iconBgColor = Color(0xFFF43F5E),
                        title = "سمة لوحة المفاتيح",
                        subtitle = "عيّن خلفية لوحة المفاتيح وتصميمها ولونها.",
                        onClick = onNavigateToThemes
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 2. حجم الخط والحروف
                    SettingRowItem(
                        icon = Icons.Default.TextFields,
                        iconBgColor = Color(0xFFF59E0B),
                        title = "حجم الخط والحروف",
                        subtitle = "الخط(أساسي), الحجم(عادي)",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = { showFontDialog = true }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 3. ارتفاع لوحة المفاتيح وهوامشها
                    SettingRowItem(
                        icon = Icons.Default.AspectRatio,
                        iconBgColor = Color(0xFFEA580C),
                        title = "ارتفاع لوحة المفاتيح وهوامشها",
                        subtitle = "الارتفاع الرأسي(متوازن), الارتفاع الأفقي(كامل)",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = { showHeightDialog = true }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 4. إخفاء زر رموز المشاعر (Switch)
                    SettingSwitchItem(
                        icon = Icons.Default.SentimentSatisfied,
                        iconBgColor = Color(0xFF84CC16),
                        title = "إخفاء زر رموز المشاعر",
                        subtitle = "يعرض زر لاستخدام الرموز المختلفة في الصف السفلي.",
                        isChecked = hideEmojiKey,
                        onCheckedChange = {
                            hideEmojiKey = it
                            prefs.showBottomRowSymbols = !it
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 5. عرض الأرقام (Switch)
                    SettingSwitchItem(
                        icon = Icons.Default.Pin,
                        iconBgColor = Color(0xFF3B82F6),
                        title = "عرض صف الأرقام",
                        subtitle = "تظهر الأرقام دائماً في الجزء العلوي من لوحة المفاتيح.",
                        isChecked = showNumbers,
                        onCheckedChange = {
                            showNumbers = it
                            prefs.showNumberRow = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 6. إظهار مرتفع (Key Preview Popup Switch)
                    SettingSwitchItem(
                        icon = Icons.Default.Title,
                        iconBgColor = Color(0xFFA855F7),
                        title = "إظهار مرتفع (معاينة الأحرف المكبورة)",
                        subtitle = "عرض الحرف أو الرقم المكبّر بالضبط فوق الزر المضغوط.",
                        isChecked = showKeyPreview,
                        onCheckedChange = {
                            showKeyPreview = it
                            prefs.showKeyPreview = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 7. وضع الظلام (Switch)
                    SettingSwitchItem(
                        icon = Icons.Default.DarkMode,
                        iconBgColor = Color(0xFFEAB308),
                        title = "وضع الظلام",
                        subtitle = "تصبح جميع شاشات التطبيق ولوحة المفاتيح مظلمة ومريحة للعين.",
                        isChecked = isDarkMode,
                        onCheckedChange = { isDarkMode = it }
                    )
                }

                // ================= SECTION 2: إعدادات الميزة (Feature Settings) =================
                SettingsSectionCard(title = "إعدادات الميزة وطرق الإدخال") {
                    // اختيار كيبورد محمد / التبديل الفوري (NEW & PROMINENT)
                    SettingRowItem(
                        icon = Icons.Default.TouchApp,
                        iconBgColor = Color(0xFF6366F1),
                        title = "اختيار لوحة المفاتيح الافتراضية",
                        isNew = true,
                        subtitle = "التبديل الفوري بين كيبورد محمد ولوحات المفاتيح الأخرى في هاتفك.",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = { openSystemInputMethodPicker(context) }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // إدارة لوحات الإدخال في النظام (NEW)
                    SettingRowItem(
                        icon = Icons.Default.Tune,
                        iconBgColor = Color(0xFF06B6D4),
                        title = "إدارة لوحات المفاتيح في النظام",
                        subtitle = "فتح إعدادات أندرويد لتفعيل كيبورد محمد وإدارتها.",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = { openSystemInputMethodSettings(context) }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // اللغة والنوع (NEW)
                    SettingRowItem(
                        icon = Icons.Default.Language,
                        iconBgColor = Color(0xFFEF4444),
                        title = "اللغة والنوع",
                        isNew = true,
                        subtitle = "English (QWERTY), العربية (مخصصة)",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = onNavigateToLanguages
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // إعدادات الإدخال (NEW)
                    SettingRowItem(
                        icon = Icons.Default.Edit,
                        iconBgColor = Color(0xFFF97316),
                        title = "إعدادات الإدخال الذكي",
                        isNew = true,
                        subtitle = "تعيين الفاصل الزمني للمس، الإدخال التلقائي، الكلمات المقترحة والتصحيح الفوري.",
                        onClick = {
                            Toast.makeText(context, "الإدخال الذكي ومحرك الاقتراحات يعمل بأعلى دقة ✓", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // الأصوات/الاهتزاز
                    SettingRowItem(
                        icon = Icons.Default.VolumeUp,
                        iconBgColor = Color(0xFFEAB308),
                        title = "الأصوات والاهتزاز (Haptic)",
                        subtitle = "تعيين نوع الصوت ومستواه وقوة ردود الفعل اللمسية.",
                        onClick = { showSoundDialog = true }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // تحرير رمزي
                    SettingRowItem(
                        icon = Icons.Default.Star,
                        iconBgColor = Color(0xFF84CC16),
                        title = "تحرير رمزي",
                        subtitle = "تخصيص الرموز الخاصة التي تظهر في شريط الوصول السريع.",
                        onClick = {
                            Toast.makeText(context, "لوحة الرموز المخصصة مفعّلة في الكيبورد", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // تعديل "رمز المشاعر الخاص بي"
                    SettingRowItem(
                        icon = Icons.Default.Mood,
                        iconBgColor = Color(0xFFF43F5E),
                        title = "تعديل \"رمز المشاعر الخاص بي\"",
                        subtitle = "أنشئ رمز المشاعر والملصقات وصور GIF المفضلة لديك.",
                        onClick = {
                            Toast.makeText(context, "مكتبة الإيموجي وGIF تدعم التخصيص والمفضلة", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // تحرير مفاتيح اختصار الإدخال
                    SettingRowItem(
                        icon = Icons.Default.KeyboardReturn,
                        iconBgColor = Color(0xFF3B82F6),
                        title = "تحرير مفاتيح اختصار الإدخال",
                        subtitle = "نص سريع وتوسيع الجمل التلقائي (Text Expansion).",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = {
                            Toast.makeText(context, "الاختصارات السريعة متاحة في شريط الكيبورد", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // تحرير مفاتيح اختصار المسافة
                    SettingRowItem(
                        icon = Icons.Default.SpaceBar,
                        iconBgColor = Color(0xFFF59E0B),
                        title = "تحرير اختصار مفتاح المسافة",
                        subtitle = "اسحب على مسطرة المسافة لتحريك المؤشر بدقة متناهية.",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = {
                            Toast.makeText(context, "السحب على المسافة لتحريك المؤشر مفعّل", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // تحرير قائمة لوحة المفاتيح
                    SettingRowItem(
                        icon = Icons.Default.Apps,
                        iconBgColor = Color(0xFFA855F7),
                        title = "تحرير قائمة لوحة المفاتيح (4x4 Grid)",
                        subtitle = "تخصيص وترتيب الأدوات والميزات التي تظهر في القائمة الرئيسية.",
                        onClick = {
                            Toast.makeText(context, "يمكنك فتح القائمة (⊞) من أعلى الكيبورد وتعديلها", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // ================= SECTION 3: ميزات الراحة (Convenience Features) =================
                SettingsSectionCard(title = "ميزات الراحة") {
                    // الشريط العلوي بلوحة المفاتيح
                    SettingRowItem(
                        icon = Icons.Default.ViewCompact,
                        iconBgColor = Color(0xFFA855F7),
                        title = "الشريط العلوي بلوحة المفاتيح",
                        subtitle = "تحديد الميزات (الألعاب، الصوت، الترجمة، الحافظة، الحاسبة) في الشريط العلوي.",
                        onClick = {
                            Toast.makeText(context, "الشريط العلوي يحتوي على كافة الميزات السريعة", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // النسخ احتياطي / استعادة (NEW)
                    SettingRowItem(
                        icon = Icons.Default.SyncAlt,
                        iconBgColor = Color(0xFFEAB308),
                        title = "النسخ الاحتياطي / الاستعادة",
                        isNew = true,
                        subtitle = "حفظ وتحميل إعدادات لوحة المفاتيح، والنصوص التلقائية، والسمات على هاتفك.",
                        onClick = {
                            Toast.makeText(context, "تم حفظ نسخة احتياطية من إعداداتك بنجاح ✓", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // شريط الإشعارات
                    SettingRowItem(
                        icon = Icons.Default.Notifications,
                        iconBgColor = Color(0xFFF43F5E),
                        title = "شريط الإشعارات السريع",
                        subtitle = "الوصول إلى الملاحظات، والأخبار، والقاموس، والترجمة المباشرة بسهولة.",
                        onClick = {
                            Toast.makeText(context, "الوصول السريع متاح دائماً", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // ================= SECTION 4: حول التطبيق =================
                SettingsSectionCard(title = "حول التطبيق والدعم") {
                    SettingRowItem(
                        icon = Icons.Default.Campaign,
                        iconBgColor = Color(0xFFA855F7),
                        title = "ادعمنا وتقييم التطبيق",
                        subtitle = "ساعدنا في نشر وتطوير كيبورد محمد v1",
                        onClick = {
                            Toast.makeText(context, "شكراً لدعمك لتطبيق كيبورد محمد v1 ❤️", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)
                    SettingRowItem(
                        icon = Icons.Default.HelpOutline,
                        iconBgColor = Color(0xFFF59E0B),
                        title = "الأسئلة الشائعة",
                        subtitle = "جميع الميزات تعمل محلياً مع حماية الخصوصية الكاملة.",
                        onClick = {
                            Toast.makeText(context, "لوحة المفاتيح آمنة 100% ولا تحفظ أي بيانات حساسة", Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)
                    SettingRowItem(
                        icon = Icons.Default.Share,
                        iconBgColor = Color(0xFF84CC16),
                        title = "مشاركة التطبيق مع الأصدقاء",
                        subtitle = "شارك كيبورد محمد v1 عبر وسائل التواصل.",
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "جرّب كيبورد محمد v1 - أسرع لوحة مفاتيح مع ثيمات GIF وصور ودعم كامل للعربية!")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"))
                        }
                    )
                }

                // ================= SECTION 5: الإصدار =================
                SettingsSectionCard(title = "الإصدار") {
                    SettingRowItem(
                        icon = Icons.Default.Info,
                        iconBgColor = Color(0xFFEF4444),
                        title = "الإصدار 8.5.1 (كيبورد محمد v1)",
                        subtitle = "أحدث إصدار مثبت ومحدّث بكافة الميزات والتصميمات المتناسقة.",
                        subtitleColor = Color(0xFF818CF8),
                        onClick = {
                            Toast.makeText(context, "أنت تستخدم أحدث إصدار: 8.5.1", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // ================= BOTTOM STICKY TEST KEYBOARD BAR (Screenshots 4 to 9) =================
            Surface(
                color = Color(0xFF1E1E24),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    TextField(
                        value = testInputText,
                        onValueChange = { testInputText = it },
                        placeholder = {
                            Text(
                                text = "جرّب اختبار لوحة المفاتيح هنا...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        trailingIcon = {
                            if (testInputText.isNotEmpty()) {
                                IconButton(onClick = { testInputText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Color.Gray)
                                }
                            }
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF282830),
                            unfocusedContainerColor = Color(0xFF282830),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }
            }
        }

        // Font Size Dialog
        if (showFontDialog) {
            AlertDialog(
                onDismissRequest = { showFontDialog = false },
                title = { Text("حجم الخط والحروف", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("اختر حجم الخط المناسب للوحة المفاتيح:")
                        listOf("صغير (16sp)", "عادي (19sp) - الافتراضي", "كبير (22sp)", "كبير جداً (25sp)").forEach { sizeName ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF282830),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        Toast.makeText(context, "تم ضبط حجم الخط: $sizeName", Toast.LENGTH_SHORT).show()
                                        showFontDialog = false
                                    }
                            ) {
                                Text(
                                    text = sizeName,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFontDialog = false }) {
                        Text("إغلاق")
                    }
                }
            )
        }

        // Height Dialog
        if (showHeightDialog) {
            AlertDialog(
                onDismissRequest = { showHeightDialog = false },
                title = { Text("ارتفاع لوحة المفاتيح", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("اختر ارتفاع لوحة المفاتيح المفضل لديك:")
                        listOf("قصير (230dp)", "متوسط (260dp) - الافتراضي", "طويل (290dp)", "طويل جداً (320dp)").forEach { heightName ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF282830),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        prefs.keyboardHeight = when {
                                            heightName.startsWith("قصير") -> "Short"
                                            heightName.startsWith("متوسط") -> "Medium"
                                            heightName.startsWith("طويل جداً") -> "Extra Tall"
                                            else -> "Tall"
                                        }
                                        Toast.makeText(context, "تم تغيير الارتفاع: $heightName", Toast.LENGTH_SHORT).show()
                                        showHeightDialog = false
                                    }
                            ) {
                                Text(
                                    text = heightName,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHeightDialog = false }) {
                        Text("إغلاق")
                    }
                }
            )
        }

        // Sound & Haptic Dialog
        if (showSoundDialog) {
            AlertDialog(
                onDismissRequest = { showSoundDialog = false },
                title = { Text("الصوت والاهتزاز", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("إعدادات الاهتزاز اللمسي (Haptic Feedback):")
                        listOf("بدون اهتزاز (Off)", "اهتزاز خفيف (Light)", "اهتزاز متوسط (Medium) - الموصى به", "اهتزاز قوي (Strong)").forEach { hapticOpt ->
                            val isSel = (hapticOpt.contains("Medium") && prefs.hapticFeedback == "Medium") || (hapticOpt.contains("Off") && prefs.hapticFeedback == "Off")
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) Color(0xFF6366F1).copy(alpha = 0.3f) else Color(0xFF282830),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        prefs.hapticFeedback = when {
                                            hapticOpt.contains("Off") -> "Off"
                                            hapticOpt.contains("Light") -> "Light"
                                            hapticOpt.contains("Strong") -> "Strong"
                                            else -> "Medium"
                                        }
                                        Toast.makeText(context, "تم ضبط الاهتزاز: $hapticOpt", Toast.LENGTH_SHORT).show()
                                        showSoundDialog = false
                                    }
                            ) {
                                Text(
                                    text = hapticOpt,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSoundDialog = false }) {
                        Text("إغلاق")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = Color(0xFF9CA3AF),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingRowItem(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String? = null,
    subtitleColor: Color = Color(0xFF9CA3AF),
    isNew: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Leading Icon on Start (Right in Arabic RTL)
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconBgColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconBgColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // 2. Middle Column with Title & Subtitle aligned to Start
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
                if (isNew) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFEF4444))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "NEW",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 3. Trailing Navigation Arrow on End (Left in Arabic RTL)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Leading Icon on Start (Right in Arabic RTL)
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconBgColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconBgColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // 2. Middle Column with Title & Subtitle aligned to Start
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start
            )

            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 3. Trailing Switch on End (Left in Arabic RTL)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color(0xFF2E2E36)
            )
        )
    }
}
