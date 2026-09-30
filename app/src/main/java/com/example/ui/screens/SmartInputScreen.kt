package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartInputScreen(
    onBack: (() -> Unit)? = null
) {
    val prefs = remember { KeyboardProApp.instance.preferences }

    var autoCorrectEnabled by remember { mutableStateOf(prefs.autoCorrectEnabled) }
    var autoCorrectStrength by remember { mutableStateOf(prefs.autoCorrectStrength) }
    var showSuggestions by remember { mutableStateOf(prefs.showSuggestions) }
    var doubleSpacePeriod by remember { mutableStateOf(prefs.doubleSpacePeriod) }
    var autoCapitalization by remember { mutableStateOf(prefs.autoCapitalization) }
    var showNumberRow by remember { mutableStateOf(prefs.showNumberRow) }
    var arabicNumerals by remember { mutableStateOf(prefs.arabicNumerals) }
    var showToolbarUndoRedo by remember { mutableStateOf(prefs.showToolbarUndoRedo) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "إعدادات الإدخال الذكي والتصحيح",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF141416),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF141416)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: تصحيح الكتابة والاقتراحات
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C24))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "تصحيح الكتابة والاقتراحات",
                        color = Color(0xFF818CF8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 1. تصحيح الكتابة التلقائي
                    SmartToggleItem(
                        icon = Icons.Default.Spellcheck,
                        title = "تصحيح الكتابة التلقائي",
                        subtitle = "تصحيح الأخطاء الإملائية الشائعة تلقائياً عند الضغط على مفتاح المسافة.",
                        isChecked = autoCorrectEnabled,
                        onCheckedChange = {
                            autoCorrectEnabled = it
                            prefs.autoCorrectEnabled = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 2. قوة التصحيح
                    if (autoCorrectEnabled) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "مستوى حساسية التصحيح",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("خفيف" to "Low", "متوازن" to "Medium", "قوي" to "High").forEach { (label, value) ->
                                    val isSelected = autoCorrectStrength == value
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            autoCorrectStrength = value
                                            prefs.autoCorrectStrength = value
                                        },
                                        label = { Text(label, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF6366F1),
                                            selectedLabelColor = Color.White,
                                            containerColor = Color(0xFF262630),
                                            labelColor = Color.LightGray
                                        )
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)
                    }

                    // 3. شريط الاقتراحات الذكية
                    SmartToggleItem(
                        icon = Icons.Default.Lightbulb,
                        title = "اقتراحات الكلمات التالية",
                        subtitle = "عرض الكلمات المتوقعة والشائعة في شريط أعلى الكيبورد لتسريع الكتابة.",
                        isChecked = showSuggestions,
                        onCheckedChange = {
                            showSuggestions = it
                            prefs.showSuggestions = it
                        }
                    )
                }
            }

            // Section 2: علامات الترقيم والأرقام
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C24))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "الترقيم والأرقام",
                        color = Color(0xFF818CF8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 1. صف الأرقام العلوي
                    SmartToggleItem(
                        icon = Icons.Default.Pin,
                        title = "صف الأرقام العلوي الدائم",
                        subtitle = "إظهار صف الأرقام (0-9) دائماً أعلى لوحة المفاتيح دون الحاجة للتبديل.",
                        isChecked = showNumberRow,
                        onCheckedChange = {
                            showNumberRow = it
                            prefs.showNumberRow = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 2. نوع الأرقام (عربية / إنجليزية)
                    SmartToggleItem(
                        icon = Icons.Default.Numbers,
                        title = "الأرقام العربية المشرقية (١، ٢، ٣)",
                        subtitle = if (arabicNumerals) "تظهر الأرقام بالشكل العربي المشرقي (١٢٣)." else "تظهر الأرقام بالشكل الغربي القياسي (123).",
                        isChecked = arabicNumerals,
                        onCheckedChange = {
                            arabicNumerals = it
                            prefs.arabicNumerals = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 3. نقطة بالمسافة المزدوجة
                    SmartToggleItem(
                        icon = Icons.Default.SpaceBar,
                        title = "نقطة بالضغط المزدوج على المسافة",
                        subtitle = "إدراج نقطة متبوعة بمسافة تلقائياً عند الضغط مرتين متتاليتين على مفتاح المسافة.",
                        isChecked = doubleSpacePeriod,
                        onCheckedChange = {
                            doubleSpacePeriod = it
                            prefs.doubleSpacePeriod = it
                        }
                    )

                    HorizontalDivider(color = Color(0xFF2E2E36), thickness = 0.6.dp)

                    // 4. تكبير الحروف التلقائي
                    SmartToggleItem(
                        icon = Icons.Default.Title,
                        title = "تكبير أول حرف تلقائياً (Auto Caps)",
                        subtitle = "تكبير الحرف الأول بعد النقاط وبداية الجمل الجديدة باللغة الإنجليزية واللغات اللاتينية.",
                        isChecked = autoCapitalization,
                        onCheckedChange = {
                            autoCapitalization = it
                            prefs.autoCapitalization = it
                        }
                    )
                }
            }

            // Section 3: شريط الأدوات ومساعد الكتابة
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C24))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "شريط الأدوات وأزرار التحكم",
                        color = Color(0xFF818CF8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 1. أزرار التراجع والإعادة
                    SmartToggleItem(
                        icon = Icons.Default.Undo,
                        title = "أزرار التراجع والإعادة (Undo / Redo)",
                        subtitle = "عرض أزرار التراجع الفوري عن الكتابة في شريط أدوات الكيبورد.",
                        isChecked = showToolbarUndoRedo,
                        onCheckedChange = {
                            showToolbarUndoRedo = it
                            prefs.showToolbarUndoRedo = it
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SmartToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0xFF262630), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isChecked) Color(0xFF818CF8) else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.Gray,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color(0xFF2A2A34)
            )
        )
    }
}
