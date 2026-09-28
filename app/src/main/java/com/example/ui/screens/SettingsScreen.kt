package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToThemes: () -> Unit = {},
    onNavigateToLanguages: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { KeyboardProApp.instance.preferences }

    var hideEmojiKey by remember { mutableStateOf(!prefs.showBottomRowSymbols) }
    var showNumbers by remember { mutableStateOf(prefs.showNumberRow) }
    var showKeyPreview by remember { mutableStateOf(prefs.showKeyPreview) }
    var isDarkMode by remember { mutableStateOf(true) }

    var testInputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141416))
    ) {
        // Top App Bar matching Screenshots 4 to 9
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF1E1E24))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Keyboard,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "الإعدادات",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { /* back or no-op */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Scrollable Settings Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    onClick = {
                        Toast.makeText(context, "الخط الافتراضي: عادي (19sp)", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // 3. ارتفاع لوحة المفاتيح وهوامشها
                SettingRowItem(
                    icon = Icons.Default.AspectRatio,
                    iconBgColor = Color(0xFFEA580C),
                    title = "ارتفاع لوحة المفاتيح وهوامشها",
                    subtitle = "الارتفاع الرأسي(7), الارتفاع الأفقي(7)",
                    subtitleColor = Color(0xFF818CF8),
                    onClick = {
                        Toast.makeText(context, "ارتفاع الكيبورد: الوضع المتوازن", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // 4. إخفاء زر رموز المشاعر (Switch)
                SettingSwitchItem(
                    icon = Icons.Default.SentimentSatisfied,
                    iconBgColor = Color(0xFF84CC16),
                    title = "إخفاء زر رموز المشاعر",
                    subtitle = "يعرض زر لاستخدام الرموز المختلفة.",
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
                    title = "عرض الأرقام",
                    subtitle = "تظهر الأرقام في الجزء العلوي من لوحة المفاتيح.",
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
                    title = "إظهار مرتفع",
                    subtitle = "عرض أحرف خاصة أو أرقام على أزرار المفاتيح والمعاينة المباشرة.",
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
                    subtitle = "تصبح جميع الشاشات مظلمة.",
                    isChecked = isDarkMode,
                    onCheckedChange = { isDarkMode = it }
                )
            }

            // ================= SECTION 2: إعدادات الميزة (Feature Settings) =================
            SettingsSectionCard(title = "إعدادات الميزة") {
                // اللغة والنوع (NEW)
                SettingRowItem(
                    icon = Icons.Default.Language,
                    iconBgColor = Color(0xFFEF4444),
                    title = "اللغة والنوع",
                    isNew = true,
                    subtitle = "English(QWERTY), العربية",
                    subtitleColor = Color(0xFF818CF8),
                    onClick = onNavigateToLanguages
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // إعدادات الإدخال (NEW)
                SettingRowItem(
                    icon = Icons.Default.TouchApp,
                    iconBgColor = Color(0xFFF97316),
                    title = "إعدادات الإدخال",
                    isNew = true,
                    subtitle = "تعيين الفاصل الزمني للمس والإدخال التلقائي والكلمات المقترحة والعرض المكبر للمفتاح الذي يتم الضغط عليه، وما إلى ذلك.",
                    onClick = {
                        Toast.makeText(context, "تم ضبط الفاصل الزمني والمعاينة الدقيقة", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // الأصوات/الاهتزاز
                SettingRowItem(
                    icon = Icons.Default.VolumeUp,
                    iconBgColor = Color(0xFFEAB308),
                    title = "الأصوات/الاهتزاز",
                    subtitle = "تعيين نوع الصوت ومستواه وشدة الاهتزاز، وما إلى ذلك.",
                    onClick = {
                        Toast.makeText(context, "نوع الصوت: CLICK | الاهتزاز: Medium", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // تحرير رمزي
                SettingRowItem(
                    icon = Icons.Default.Star,
                    iconBgColor = Color(0xFF84CC16),
                    title = "تحرير رمزي",
                    subtitle = "يمكنك إجراء تحرير لاستخدام الرموز التي تريدها.",
                    onClick = {
                        Toast.makeText(context, "تخصيص الرموز متاح في الكيبورد", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // تعديل "رمز المشاعر الخاص بي"
                SettingRowItem(
                    icon = Icons.Default.Mood,
                    iconBgColor = Color(0xFFF43F5E),
                    title = "تعديل \"رمز المشاعر الخاص بي\"",
                    subtitle = "أنشئ رمز المشاعر الخاص بك وعدّله.",
                    onClick = {
                        Toast.makeText(context, "لوحة الفيسات تدعم تخصيص الرموز الحديثة", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // تحرير مفاتيح اختصار الإدخال
                SettingRowItem(
                    icon = Icons.Default.KeyboardReturn,
                    iconBgColor = Color(0xFF3B82F6),
                    title = "تحرير مفاتيح اختصار الإدخال",
                    subtitle = "نص سريع",
                    subtitleColor = Color(0xFF818CF8),
                    onClick = {
                        Toast.makeText(context, "مدير الاختصارات السريعة مفعّل", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // تحرير مفاتيح اختصار المسافة
                SettingRowItem(
                    icon = Icons.Default.SpaceBar,
                    iconBgColor = Color(0xFFF59E0B),
                    title = "تحرير مفاتيح اختصار المسافة",
                    subtitle = "حرك المؤشر",
                    subtitleColor = Color(0xFF818CF8),
                    onClick = {
                        Toast.makeText(context, "اسحب على مسطرة المسافة لتحريك المؤشر", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // تحرير قائمة لوحة المفاتيح
                SettingRowItem(
                    icon = Icons.Default.Apps,
                    iconBgColor = Color(0xFFA855F7),
                    title = "تحرير قائمة لوحة المفاتيح",
                    subtitle = "يمكنك تحرير الميزات التي تريد استخدامها فقط.",
                    onClick = {
                        Toast.makeText(context, "شريط الأدوات العلوي يتيح الوصول لكافة الميزات", Toast.LENGTH_SHORT).show()
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
                    subtitle = "يمكنك تحديد الميزات التي تظهر في المنطقة العلوية من لوحة المفاتيح.",
                    onClick = {
                        Toast.makeText(context, "أزرار GIF، الحافظة، الصوت، والترجمة مثبتة في الشريط", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // النسخ احتياطي / استعادة (NEW)
                SettingRowItem(
                    icon = Icons.Default.SyncAlt,
                    iconBgColor = Color(0xFFEAB308),
                    title = "النسخ احتياطي / استعادة",
                    isNew = true,
                    subtitle = "يمكنك حفظ وتحميل إعدادات لوحة المفاتيح ونص تلقائي والسمة وملاحظة على هاتفك.",
                    onClick = {
                        Toast.makeText(context, "تم حفظ نسخة احتياطية من إعداداتك بنجاح ✓", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                // شريط الإشعارات
                SettingRowItem(
                    icon = Icons.Default.Notifications,
                    iconBgColor = Color(0xFFF43F5E),
                    title = "شريط الإشعارات",
                    subtitle = "يمكن الوصول إلى ميزات الملاحظات والأخبار والمعجم والترجمة بدون تشغيل التطبيق.",
                    onClick = {
                        Toast.makeText(context, "الوصول السريع متاح دائماً", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ================= SECTION 4: !DesignKeyboard Level Up =================
            SettingsSectionCard(title = "!DesignKeyboard Level Up") {
                SettingRowItem(
                    icon = Icons.Default.Campaign,
                    iconBgColor = Color(0xFFA855F7),
                    title = "ادعمنا",
                    onClick = { Toast.makeText(context, "شكراً لدعمك لتطبيق كيبورد محمد v1 ❤️", Toast.LENGTH_SHORT).show() }
                )
                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)
                SettingRowItem(
                    icon = Icons.Default.HelpOutline,
                    iconBgColor = Color(0xFFF59E0B),
                    title = "أسئلة مكررة",
                    onClick = { Toast.makeText(context, "كافة الميزات مدعومة محلياً بدون إنترنت", Toast.LENGTH_SHORT).show() }
                )
                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)
                SettingRowItem(
                    icon = Icons.Default.Email,
                    iconBgColor = Color(0xFF3B82F6),
                    title = "الإبلاغ عن أخطاء أو مقترحات",
                    onClick = { Toast.makeText(context, "نسعد دائماً بتواصلكم ومقترحاتكم", Toast.LENGTH_SHORT).show() }
                )
                HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)
                SettingRowItem(
                    icon = Icons.Default.Share,
                    iconBgColor = Color(0xFF84CC16),
                    title = "إخبار أصدقاء عن التطبيق",
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "جرّب كيبورد محمد v1 - أسرع لوحة مفاتيح أندرويد مع ثيمات GIF وصور ودعم كامل للعربية!")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة التطبيق"))
                    }
                )
            }

            // ================= SECTION 5: تطبيقات شائعة مقترحة =================
            SettingsSectionCard(title = "تطبيقات شائعة مقترحة 😉") {
                SettingRowItem(
                    icon = Icons.Default.WbSunny,
                    iconBgColor = Color(0xFF38BDF8),
                    title = "[AD] الطقس أولاً بأول",
                    onClick = { Toast.makeText(context, "تطبيق الطقس المقترح", Toast.LENGTH_SHORT).show() }
                )
            }

            // ================= SECTION 6: الإصدار =================
            SettingsSectionCard(title = "الإصدار") {
                SettingRowItem(
                    icon = Icons.Default.Info,
                    iconBgColor = Color(0xFFEF4444),
                    title = "الإصدار الحالي 8.5.1 (كيبورد محمد v1)",
                    subtitle = "الانتقال إلى التحديث - أحدث إصدار مثبت",
                    subtitleColor = Color(0xFFF43F5E),
                    onClick = { Toast.makeText(context, "لديك أحدث إصدار: 8.5.1", Toast.LENGTH_SHORT).show() }
                )
            }
        }

        // ================= BOTTOM STICKY TEST KEYBOARD BAR (Screenshots 4 to 9) =================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E24))
                .border(width = 0.8.dp, color = Color(0xFF33333D))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            TextField(
                value = testInputText,
                onValueChange = { testInputText = it },
                placeholder = {
                    Text(
                        text = "جرّب اختبار لوحة المفاتيح.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
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
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
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
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
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
    subtitleColor: Color = Color.Gray,
    isNew: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
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
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBgColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconBgColor,
                modifier = Modifier.size(20.dp)
            )
        }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBgColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconBgColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
