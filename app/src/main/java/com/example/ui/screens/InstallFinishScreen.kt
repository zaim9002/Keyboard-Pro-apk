package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.KeyboardProApp
import com.example.data.local.entity.UserWordEntity
import com.example.engine.TranslationEngine
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen

@Composable
fun InstallFinishScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToThemes: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val app = KeyboardProApp.instance
    val prefs = app.preferences
    val langManager = app.languageManager
    val db = app.database
    val clips by db.clipboardDao().getAllClips().collectAsState(initial = emptyList())

    var testText by remember { mutableStateOf("") }
    var currentLanguage by remember { mutableStateOf(prefs.currentLanguage) }
    val currentThemeName by prefs.themeState.collectAsState()
    val colorScheme = remember(currentThemeName) { KeyboardThemes.getColorScheme(currentThemeName) }
    val isNumberRowPref by prefs.numberRowState.collectAsState()
    val isArabicNumeralsPref by prefs.arabicNumeralsState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141416))
            .statusBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Header Card (Compact & Informative)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "لوحة المفاتيح جاهزة ومفعّلة",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "جرّب الكتابة والأرقام والإيموجي بالأسفل",
                            color = Color(0xFF9E9EA4),
                            fontSize = 11.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onNavigateToThemes,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "السمات",
                            tint = Color(0xFF818CF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Interactive Test Area Card (Directly Above Keyboard)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24)),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E2E38))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "مربع معاينة الكتابة:",
                            color = Color(0xFF818CF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (testText.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = ClipData.newPlainText("KeyboardTest", testText)
                                        clipboard?.setPrimaryClip(clip)
                                        Toast.makeText(context, "تم نسخ النص إلى الحافظة ✓", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF818CF8))
                                    Spacer(Modifier.width(3.dp))
                                    Text("نسخ", fontSize = 11.sp, color = Color(0xFF818CF8))
                                }

                                TextButton(
                                    onClick = { testText = "" },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFEF4444))
                                    Spacer(Modifier.width(3.dp))
                                    Text("مسح", fontSize = 11.sp, color = Color(0xFFEF4444))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Text Content Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 36.dp, max = 64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF141418))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (testText.isEmpty()) {
                            Text(
                                text = "اضغط على أزرار الكيبورد بالأسفل لتجربة الكتابة، زر 123 للأرقام، والرموز والـ GIF...",
                                color = Color(0xFF71717A),
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = testText + " ▎",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Quick test pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "مرحباً بك ❤️" to "مرحباً بك ❤️ ",
                            "١٢٣٤٥" to "١٢٣٤٥ ",
                            "12345" to "12345 ",
                            "الحمد لله" to "الحمد لله ",
                            "كيبورد رائع!" to "كيبورد رائع! "
                        ).forEach { (label, toInsert) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF262630))
                                    .clickable { testText += toInsert }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = label, color = Color(0xFFA1A1AA), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. Live Interactive Keyboard Preview (Fully Functional)
        Surface(
            color = colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            KeyboardScreen(
                colorScheme = colorScheme,
                currentLanguage = currentLanguage,
                imeOptions = EditorInfo.IME_ACTION_DONE,
                isIncognito = false,
                keyboardHeight = prefs.keyboardHeight,
                showNumberRow = isNumberRowPref,
                hapticEnabled = prefs.hapticFeedback != "Off",
                soundEnabled = prefs.keySound != "Off",
                oneHandedMode = prefs.oneHandedMode,
                applyNavigationBarsPadding = false,
                arabicNumerals = isArabicNumeralsPref,
                suggestions = listOf("السلام", "شكراً", "تمام", "أهلاً", "مرحباً", "ممتاز"),
                clipboardList = clips,
                isVoiceListening = false,
                voiceStatusText = "جاهز للاستماع",
                voicePartialText = "",
                currentDraftText = testText,
                onTextInput = { text ->
                    testText += text
                },
                onDelete = {
                    if (testText.isNotEmpty()) {
                        testText = testText.dropLast(1)
                    }
                },
                onDeleteWord = {
                    val trimmed = testText.trimEnd()
                    val lastSpace = trimmed.lastIndexOf(' ')
                    testText = if (lastSpace >= 0) {
                        trimmed.substring(0, lastSpace + 1)
                    } else {
                        ""
                    }
                },
                onDeleteAll = {
                    testText = ""
                },
                onEnter = {
                    testText += "\n"
                },
                onSpace = {
                    testText += " "
                },
                onSwitchLanguage = {
                    try {
                        currentLanguage = langManager.cycleNextLanguage()
                    } catch (e: Throwable) {
                        currentLanguage = if (currentLanguage == "ar") "en" else "ar"
                    }
                },
                onSwitchPreviousLanguage = {
                    try {
                        currentLanguage = langManager.cyclePreviousLanguage()
                    } catch (e: Throwable) {
                        currentLanguage = if (currentLanguage == "ar") "en" else "ar"
                    }
                },
                onSelectLanguage = { langId ->
                    try {
                        langManager.switchLanguage(langId)
                        currentLanguage = langId
                    } catch (e: Throwable) {}
                },
                onMoveCursor = {},
                onSelectSuggestion = { sug ->
                    testText += "$sug "
                },
                onTogglePinClip = { id, pinned ->
                    coroutineScope.launch {
                        try { db.clipboardDao().togglePin(id, pinned) } catch (e: Throwable) {}
                    }
                },
                onDeleteClip = { id ->
                    coroutineScope.launch {
                        try { db.clipboardDao().deleteById(id) } catch (e: Throwable) {}
                    }
                },
                onClearUnpinnedClips = {
                    coroutineScope.launch {
                        try { db.clipboardDao().clearUnpinned() } catch (e: Throwable) {}
                    }
                },
                onStartVoice = {},
                onStopVoice = {},
                onSelectAll = {},
                onCut = { testText = "" },
                onCopy = {},
                onPaste = {},
                onUndo = { if (testText.isNotEmpty()) testText = testText.dropLast(1) },
                onRedo = {},
                onOpenSettings = onNavigateToSettings,
                onSwitchIme = { openSystemInputMethodPicker(context) },
                onToggleOneHanded = { mode -> prefs.oneHandedMode = mode },
                onCommitGif = { gifItem ->
                    testText += " [GIF: ${gifItem.title}] "
                    Toast.makeText(context, "تم إدراج صورة GIF: ${gifItem.title}", Toast.LENGTH_SHORT).show()
                },
                onTranslateNow = { src, tgt ->
                    if (testText.isNotBlank()) {
                        coroutineScope.launch {
                            try {
                                val translated = TranslationEngine.translateAsync(testText, src, tgt)
                                testText = translated
                                Toast.makeText(context, "تمت الترجمة الفورية بنجاح ✓", Toast.LENGTH_SHORT).show()
                            } catch (e: Throwable) {
                                Toast.makeText(context, "تعذر إتمام الترجمة الفورية", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                onAddWordToDictionary = { word ->
                    coroutineScope.launch {
                        try {
                            db.userWordDao().insertWord(UserWordEntity(word = word))
                            Toast.makeText(context, "تمت إضافة '$word' إلى القاموس الشخصي", Toast.LENGTH_SHORT).show()
                        } catch (e: Throwable) {}
                    }
                },
                onDeleteUserWord = { id ->
                    coroutineScope.launch {
                        try { db.userWordDao().deleteById(id) } catch (e: Throwable) {}
                    }
                }
            )
        }
    }
}
