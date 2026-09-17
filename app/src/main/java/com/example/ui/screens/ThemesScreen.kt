package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.theme.KeyboardThemes

enum class ThemeCategoryTab {
    PRESET_THEMES,
    CUSTOM_STUDIO
}

enum class ColorElement(val label: String, val icon: String) {
    KEYBOARD_BG("خلفية اللوحة", "⬛"),
    KEY_BG("خلفية المفاتيح", "⌨️"),
    KEY_TEXT("نص الأحرف", "🔤"),
    SPECIAL_KEY_BG("المفاتيح الخاصة", "⚙️"),
    ACCENT("اللون المميز", "🌟"),
    SUGGESTION_BG("شريط الاقتراحات", "💬")
}

@Composable
fun ThemesScreen() {
    val prefs = KeyboardProApp.instance.preferences
    var selectedTab by remember { mutableStateOf(ThemeCategoryTab.PRESET_THEMES) }
    var currentThemeName by remember { mutableStateOf(prefs.theme) }

    // Custom Theme Builder Colors
    var customBg by remember { mutableStateOf(prefs.customThemeBg) }
    var customKeyBg by remember { mutableStateOf(prefs.customThemeKeyBg) }
    var customKeyText by remember { mutableStateOf(prefs.customThemeKeyText) }
    var customSpecialBg by remember { mutableStateOf(prefs.customThemeSpecialBg) }
    var customSpecialText by remember { mutableStateOf(prefs.customThemeSpecialText) }
    var customAccent by remember { mutableStateOf(prefs.customThemeAccent) }
    var customSuggestionBg by remember { mutableStateOf(prefs.customThemeSuggestionBg) }
    var customSuggestionText by remember { mutableStateOf(prefs.customThemeSuggestionText) }

    var selectedElement by remember { mutableStateOf(ColorElement.KEY_BG) }
    var showSavedMessage by remember { mutableStateOf(false) }

    val livePreviewTheme = remember(
        selectedTab, currentThemeName, customBg, customKeyBg, customKeyText,
        customSpecialBg, customSpecialText, customAccent, customSuggestionBg, customSuggestionText
    ) {
        if (selectedTab == ThemeCategoryTab.CUSTOM_STUDIO) {
            KeyboardColorScheme(
                name = "مخصص (معاينة حية)",
                isDark = true,
                background = parseColorSafe(customBg, Color(0xFF121824)),
                keyBackground = parseColorSafe(customKeyBg, Color(0xFF1F293D)),
                keyText = parseColorSafe(customKeyText, Color(0xFFFFFFFF)),
                specialKeyBackground = parseColorSafe(customSpecialBg, Color(0xFF162032)),
                specialKeyText = parseColorSafe(customSpecialText, Color(0xFF60A5FA)),
                accent = parseColorSafe(customAccent, Color(0xFF38BDF8)),
                suggestionBar = parseColorSafe(customSuggestionBg, Color(0xFF121824)),
                suggestionText = parseColorSafe(customSuggestionText, Color(0xFFE2E8F0)),
                borderColor = parseColorSafe(customAccent, Color(0xFF38BDF8)).copy(alpha = 0.4f)
            )
        } else {
            KeyboardThemes.getTheme(currentThemeName, prefs = prefs)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tab Navigation: Presets vs Custom Studio
        TabRow(
            selectedTabIndex = if (selectedTab == ThemeCategoryTab.PRESET_THEMES) 0 else 1,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == ThemeCategoryTab.PRESET_THEMES,
                onClick = { selectedTab = ThemeCategoryTab.PRESET_THEMES },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("الثيمات الجاهزة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = selectedTab == ThemeCategoryTab.CUSTOM_STUDIO,
                onClick = { selectedTab = ThemeCategoryTab.CUSTOM_STUDIO },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("تصميم ثيمك الخاص", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
        }

        // Live Keyboard Theme Preview Card (Always visible at top)
        LiveKeyboardPreviewCard(theme = livePreviewTheme)

        // Tab Content
        if (selectedTab == ThemeCategoryTab.PRESET_THEMES) {
            // Preset Themes Tab
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "اختر من التشكيلة المتنوعة:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${KeyboardThemes.allThemes.size} ثيمات",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add custom theme option card at start if previously customized
                item {
                    val isCustomSelected = currentThemeName.equals("Custom", ignoreCase = true)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                prefs.theme = "Custom"
                                currentThemeName = "Custom"
                            }
                            .then(
                                if (isCustomSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                                else Modifier
                            ),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val customScheme = prefs.getCustomColorScheme()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(customScheme.background)
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(customScheme.keyBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎨", fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(customScheme.accent)
                                )
                                if (isCustomSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "محدد",
                                        tint = customScheme.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ثيمي المخصص", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("تخصيصك", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                items(KeyboardThemes.allThemes) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme.name == currentThemeName,
                        onSelect = {
                            prefs.theme = theme.name
                            currentThemeName = theme.name
                        }
                    )
                }
            }
        } else {
            // Custom Theme Studio Tab (Scrollable)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Pre-built Starter Presets
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("قوالب سريعة للبدء بها:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(starterThemeTemplates) { template ->
                                AssistChip(
                                    onClick = {
                                        customBg = template.bg
                                        customKeyBg = template.keyBg
                                        customKeyText = template.keyText
                                        customSpecialBg = template.specialBg
                                        customSpecialText = template.specialText
                                        customAccent = template.accent
                                        customSuggestionBg = template.suggestionBg
                                        customSuggestionText = template.suggestionText
                                    },
                                    label = { Text(template.title, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }

                // Element to Customize
                Text("اختر العنصر المراد تلوينه:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ColorElement.values()) { element ->
                        FilterChip(
                            selected = selectedElement == element,
                            onClick = { selectedElement = element },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(element.icon, fontSize = 12.sp)
                                    Text(element.label, fontSize = 12.sp)
                                }
                            }
                        )
                    }
                }

                // Color Swatches Palette
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        val currentColor = when (selectedElement) {
                            ColorElement.KEYBOARD_BG -> customBg
                            ColorElement.KEY_BG -> customKeyBg
                            ColorElement.KEY_TEXT -> customKeyText
                            ColorElement.SPECIAL_KEY_BG -> customSpecialBg
                            ColorElement.ACCENT -> customAccent
                            ColorElement.SUGGESTION_BG -> customSuggestionBg
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("لوحة الألوان (${selectedElement.label}):", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(parseColorSafe(currentColor, Color.Gray))
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                )
                                Text(currentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Color Grid
                        val swatches = listOf(
                            "#000000", "#0D1117", "#121824", "#1E293B", "#334155",
                            "#FFFFFF", "#F1F5F9", "#CBD5E1", "#94A3B8", "#64748B",
                            "#38BDF8", "#0284C7", "#2563EB", "#1D4ED8", "#4F46E5",
                            "#10B981", "#059669", "#14B8A6", "#06B6D4", "#22C55E",
                            "#F59E0B", "#D97706", "#F97316", "#EA580C", "#EF4444",
                            "#DC2626", "#EC4899", "#DB2777", "#A855F7", "#7C3AED"
                        )

                        // 5 columns of color swatches
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            swatches.chunked(6).forEach { rowSwatches ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowSwatches.forEach { hex ->
                                        val color = parseColorSafe(hex, Color.Gray)
                                        val isSelected = currentColor.equals(hex, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(color)
                                                .clickable {
                                                    when (selectedElement) {
                                                        ColorElement.KEYBOARD_BG -> customBg = hex
                                                        ColorElement.KEY_BG -> customKeyBg = hex
                                                        ColorElement.KEY_TEXT -> customKeyText = hex
                                                        ColorElement.SPECIAL_KEY_BG -> customSpecialBg = hex
                                                        ColorElement.ACCENT -> customAccent = hex
                                                        ColorElement.SUGGESTION_BG -> customSuggestionBg = hex
                                                    }
                                                }
                                                .border(
                                                    width = if (isSelected) 2.5.dp else 1.dp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Save & Apply Button
                Button(
                    onClick = {
                        prefs.saveCustomTheme(
                            bg = customBg,
                            keyBg = customKeyBg,
                            keyText = customKeyText,
                            specialBg = customSpecialBg,
                            specialText = customSpecialText,
                            accent = customAccent,
                            suggestionBg = customSuggestionBg,
                            suggestionText = customSuggestionText
                        )
                        currentThemeName = "Custom"
                        showSavedMessage = true
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حفظ وتطبيق الثيم المخصص الآن", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                if (showSavedMessage) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981))
                            Text("تم تفعيل وتطبيق ثيمك الخاص بنجاح على الكيبورد!", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private data class StarterTemplate(
    val title: String,
    val bg: String,
    val keyBg: String,
    val keyText: String,
    val specialBg: String,
    val specialText: String,
    val accent: String,
    val suggestionBg: String,
    val suggestionText: String
)

private val starterThemeTemplates = listOf(
    StarterTemplate("سايبر نيون", "#090D16", "#121A2D", "#E0F2FE", "#0D1424", "#38BDF8", "#38BDF8", "#090D16", "#BAE6FD"),
    StarterTemplate("ذهبي فاخر", "#0F0F10", "#212124", "#FFFBEB", "#18181B", "#F59E0B", "#F59E0B", "#0F0F10", "#FDE68A"),
    StarterTemplate("أميبوليد نقي", "#000000", "#18181B", "#FFFFFF", "#111113", "#22C55E", "#22C55E", "#000000", "#E2E8F0"),
    StarterTemplate("زمردي هادئ", "#0A1713", "#132D24", "#E6F4EA", "#0E211A", "#10B981", "#10B981", "#0A1713", "#A7F3D0"),
    StarterTemplate("بنفسجي ملكي", "#110D1D", "#211A36", "#F3E8FF", "#171226", "#A855F7", "#A855F7", "#110D1D", "#E9D5FF"),
    StarterTemplate("أبيض نظيف", "#F1F5F9", "#FFFFFF", "#0F172A", "#E2E8F0", "#2563EB", "#2563EB", "#F1F5F9", "#1E293B")
)

@Composable
private fun LiveKeyboardPreviewCard(theme: KeyboardColorScheme) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "معاينة حية: ${theme.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = "تحديث فوري أثناء التعديل",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Simulated Mini-Keyboard View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.background)
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Toolbar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🔍", fontSize = 11.sp)
                        Text("📋", fontSize = 11.sp)
                        Text("😊", fontSize = 11.sp)
                        Text("✨", fontSize = 11.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("العربية", color = theme.keyText, fontSize = 10.sp)
                    }

                    // Suggestion Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(theme.suggestionBar)
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("السلام عليكم", color = theme.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("مرحباً", color = theme.suggestionText, fontSize = 11.sp)
                        Text("شكراً", color = theme.suggestionText, fontSize = 11.sp)
                    }

                    // Key Rows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val sampleKeys = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح")
                        for (k in sampleKeys) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.keyBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = k, color = theme.keyText, fontSize = 11.sp)
                            }
                        }
                    }

                    // Bottom Row with Space and Accent Enter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(theme.specialKeyBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("١٢٣", color = theme.specialKeyText, fontSize = 10.sp)
                        }
                        Box(
                            modifier = Modifier
                                .weight(3.5f)
                                .height(26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(theme.keyBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("مسافة", color = theme.keyText.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(theme.accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↵", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: KeyboardColorScheme,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.background)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(theme.keyBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = theme.keyText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(theme.accent)
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "محدد",
                        tint = theme.accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = theme.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (theme.isDark) "داكن" else "فاتح",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun parseColorSafe(hex: String, defaultColor: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Throwable) {
        defaultColor
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}
