package com.example.ime.ui

import android.view.inputmethod.EditorInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import com.example.KeyboardProApp
import com.example.data.local.entity.ClipboardEntity
import com.example.ime.layout.KeyboardLayouts
import com.example.ime.layout.KeyModel
import com.example.ime.layout.KeyType
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.ui.components.*
import com.example.ime.ui.panels.*
import com.example.language.layout.KeyboardLayoutData
import com.example.language.layout.KeyboardLayoutManager
import com.example.language.model.LayoutFamily

enum class LayoutMode {
    ALPHA,
    SYMBOLS_1,
    SYMBOLS_2
}

enum class ShiftState {
    OFF,
    ON,
    CAPS_LOCK
}

@Composable
fun KeyboardScreen(
    colorScheme: KeyboardColorScheme,
    currentLanguage: String, // "ar" or "en" or any 40+ supported language
    imeOptions: Int,
    isIncognito: Boolean,
    keyboardHeight: String, // "Small", "Medium", "Large"
    showNumberRow: Boolean,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    oneHandedMode: String, // "OFF", "LEFT", "RIGHT"
    suggestions: List<String>,
    clipboardList: List<ClipboardEntity>,
    isVoiceListening: Boolean,
    voiceStatusText: String,
    voicePartialText: String,
    showSuggestions: Boolean = true,
    arabicNumerals: Boolean = true,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteAll: () -> Unit = {},
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onSelectLanguage: (String) -> Unit = {},
    onMoveCursor: (Int) -> Unit,
    onSelectSuggestion: (String) -> Unit,
    onTogglePinClip: (Long, Boolean) -> Unit,
    onDeleteClip: (Long) -> Unit,
    onClearUnpinnedClips: () -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onSelectAll: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleOneHanded: (String) -> Unit
) {
    var layoutMode by remember { mutableStateOf(LayoutMode.ALPHA) }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    var activePanel by remember { mutableStateOf(KeyboardPanel.NONE) }
    var showTashkeelRow by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var activePopupKey by remember { mutableStateOf<KeyModel?>(null) }

    val keyHeight = when (keyboardHeight) {
        "Small" -> 44.dp
        "Large" -> 54.dp
        else -> 48.dp
    }

    val actionIcon = remember(imeOptions) {
        val action = imeOptions and EditorInfo.IME_MASK_ACTION
        when (action) {
            EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
            EditorInfo.IME_ACTION_SEND -> Icons.Default.Send
            EditorInfo.IME_ACTION_GO -> Icons.Default.ArrowForward
            EditorInfo.IME_ACTION_DONE -> Icons.Default.Check
            EditorInfo.IME_ACTION_NEXT -> Icons.Default.ArrowDownward
            else -> Icons.Default.KeyboardReturn
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background)
    ) {
        // 1. Toolbar ( всегда сверху )
        KeyboardToolbar(
            activePanel = activePanel,
            currentLanguage = currentLanguage,
            isIncognito = isIncognito,
            colorScheme = colorScheme,
            onPanelSelect = { panel ->
                activePanel = panel
            },
            onSwitchLanguage = onSwitchLanguage,
            onOpenSettings = onOpenSettings
        )

        // 2. Suggestion Bar (عندما لا تكون اللوحات المخصصة مفتوحة وتكون مفعلة في الإعدادات)
        if (activePanel == KeyboardPanel.NONE && showSuggestions) {
            SuggestionBar(
                suggestions = suggestions,
                colorScheme = colorScheme,
                onSelectSuggestion = onSelectSuggestion
            )
        }

        // 3. Main Area: Either Feature Panel or Keyboard Keys
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 230.dp, max = 290.dp)
        ) {
            when (activePanel) {
                KeyboardPanel.CLIPBOARD -> {
                    ClipboardPanel(
                        clips = clipboardList,
                        colorScheme = colorScheme,
                        onClipClick = { text ->
                            onTextInput(text)
                            activePanel = KeyboardPanel.NONE
                        },
                        onTogglePin = onTogglePinClip,
                        onDeleteClip = onDeleteClip,
                        onClearUnpinned = onClearUnpinnedClips,
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.EMOJI -> {
                    EmojiPanel(
                        colorScheme = colorScheme,
                        onEmojiClick = { emoji -> onTextInput(emoji) },
                        onBackspace = onDelete,
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.STICKERS -> {
                    StickersPanel(
                        colorScheme = colorScheme,
                        onStickerClick = { text ->
                            onTextInput(text)
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.GIFS -> {
                    GifsPanel(
                        colorScheme = colorScheme,
                        onGifSelect = { gifText ->
                            onTextInput(gifText)
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.DECORATIONS -> {
                    DecorationsPanel(
                        initialText = suggestions.firstOrNull() ?: "",
                        colorScheme = colorScheme,
                        onInsertText = { decorated ->
                            onTextInput(decorated)
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.VOICE -> {
                    VoicePanel(
                        isListening = isVoiceListening,
                        statusText = voiceStatusText,
                        partialResult = voicePartialText,
                        colorScheme = colorScheme,
                        onStartListening = onStartVoice,
                        onStopListening = onStopVoice,
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.TRANSLATE -> {
                    TranslatePanel(
                        initialText = suggestions.firstOrNull() ?: "",
                        colorScheme = colorScheme,
                        onCommitTranslation = { translated ->
                            onTextInput(translated)
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.DICTIONARY -> {
                    DictionaryPanel(
                        initialWord = suggestions.firstOrNull() ?: "",
                        colorScheme = colorScheme,
                        onReplaceWord = { word ->
                            onTextInput(word)
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.EDITING -> {
                    TextEditingPanel(
                        colorScheme = colorScheme,
                        onMoveCursor = onMoveCursor,
                        onSelectAll = onSelectAll,
                        onCut = onCut,
                        onCopy = onCopy,
                        onPaste = onPaste,
                        onUndo = onUndo,
                        onRedo = onRedo,
                        onHome = { onMoveCursor(-999) },
                        onEnd = { onMoveCursor(999) },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.NONE -> {
                    // Actual Keyboard layout with One-Handed Mode support
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Left sidebar if one handed right
                        if (oneHandedMode == "RIGHT") {
                            OneHandedSidebar(
                                colorScheme = colorScheme,
                                isLeft = true,
                                onSwitchSide = { onToggleOneHanded("LEFT") },
                                onExpand = { onToggleOneHanded("OFF") }
                            )
                        }

                        // The Keyboard layout
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                        ) {
                            // Tashkeel bar if activated
                            if (currentLanguage == "ar" && showTashkeelRow && layoutMode == LayoutMode.ALPHA) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colorScheme.specialKeyBackground.copy(alpha = 0.5f))
                                        .padding(horizontal = 2.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (tashkeel in KeyboardLayouts.arabicTashkeel) {
                                        KeyButton(
                                            text = tashkeel,
                                            isSpecial = true,
                                            height = 36.dp,
                                            fontSize = 20.sp,
                                            colorScheme = colorScheme,
                                            hapticEnabled = hapticEnabled,
                                            soundEnabled = soundEnabled,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            onTextInput(tashkeel)
                                        }
                                    }
                                }
                            }

                            // Optional Number Row on top
                            if (showNumberRow && layoutMode == LayoutMode.ALPHA) {
                                val numRow = if (currentLanguage == "ar" && arabicNumerals) KeyboardLayouts.arabicNumberRow else KeyboardLayouts.englishNumberRow
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (num in numRow) {
                                        KeyButton(
                                            text = num,
                                            isSpecial = true,
                                            height = 36.dp,
                                            fontSize = 15.sp,
                                            colorScheme = colorScheme,
                                            hapticEnabled = hapticEnabled,
                                            soundEnabled = soundEnabled,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            onTextInput(num)
                                        }
                                    }
                                }
                            }

                            // Dynamic Layout Rows based on Language & Mode
                            when (layoutMode) {
                                LayoutMode.ALPHA -> {
                                    if (currentLanguage == "ar") {
                                        ArabicKeyboardLayout(
                                            colorScheme = colorScheme,
                                            keyHeight = keyHeight,
                                            hapticEnabled = hapticEnabled,
                                            soundEnabled = soundEnabled,
                                            actionIcon = actionIcon,
                                            onTextInput = onTextInput,
                                            onDelete = onDelete,
                                            onDeleteAll = onDeleteAll,
                                            onSpace = onSpace,
                                            onEnter = onEnter,
                                            onSwitchMode = { layoutMode = LayoutMode.SYMBOLS_1 },
                                            onSwitchLanguage = onSwitchLanguage,
                                            onLongPressLanguage = { showLanguagePicker = true },
                                            onOpenClipboard = { activePanel = KeyboardPanel.CLIPBOARD },
                                            onOpenEmoji = { activePanel = KeyboardPanel.EMOJI },
                                            onToggleTashkeel = { showTashkeelRow = !showTashkeelRow },
                                            onMoveCursor = onMoveCursor,
                                            onLongPressKey = { activePopupKey = it }
                                        )
                                    } else {
                                        val layoutData = remember(currentLanguage) {
                                            try {
                                                KeyboardProApp.instance.languageManager.getCurrentLayout()
                                            } catch (e: Throwable) {
                                                KeyboardLayoutManager.getLayout("en", LayoutFamily.QWERTY)
                                            }
                                        }
                                        DynamicKeyboardLayout(
                                            layoutData = layoutData,
                                            colorScheme = colorScheme,
                                            keyHeight = keyHeight,
                                            shiftState = shiftState,
                                            hapticEnabled = hapticEnabled,
                                            soundEnabled = soundEnabled,
                                            actionIcon = actionIcon,
                                            onTextInput = { char ->
                                                val text = if (shiftState != ShiftState.OFF) char.uppercase() else char.lowercase()
                                                onTextInput(text)
                                                if (shiftState == ShiftState.ON) {
                                                    shiftState = ShiftState.OFF
                                                }
                                            },
                                            onDelete = onDelete,
                                            onSpace = onSpace,
                                            onEnter = onEnter,
                                            onShiftClick = {
                                                shiftState = when (shiftState) {
                                                    ShiftState.OFF -> ShiftState.ON
                                                    ShiftState.ON -> ShiftState.CAPS_LOCK
                                                    ShiftState.CAPS_LOCK -> ShiftState.OFF
                                                }
                                            },
                                            onSwitchMode = { layoutMode = LayoutMode.SYMBOLS_1 },
                                            onSwitchLanguage = onSwitchLanguage,
                                            onLongPressLanguage = { showLanguagePicker = true },
                                            onMoveCursor = onMoveCursor,
                                            onLongPressKey = { activePopupKey = it }
                                        )
                                    }
                                }
                                LayoutMode.SYMBOLS_1 -> {
                                    Symbols1Layout(
                                        colorScheme = colorScheme,
                                        keyHeight = keyHeight,
                                        hapticEnabled = hapticEnabled,
                                        soundEnabled = soundEnabled,
                                        actionIcon = actionIcon,
                                        onTextInput = onTextInput,
                                        onDelete = onDelete,
                                        onSpace = onSpace,
                                        onEnter = onEnter,
                                        onSwitchToAlpha = { layoutMode = LayoutMode.ALPHA },
                                        onSwitchToSymbols2 = { layoutMode = LayoutMode.SYMBOLS_2 },
                                        onSwitchLanguage = onSwitchLanguage,
                                        onMoveCursor = onMoveCursor
                                    )
                                }
                                LayoutMode.SYMBOLS_2 -> {
                                    Symbols2Layout(
                                        colorScheme = colorScheme,
                                        keyHeight = keyHeight,
                                        hapticEnabled = hapticEnabled,
                                        soundEnabled = soundEnabled,
                                        actionIcon = actionIcon,
                                        onTextInput = onTextInput,
                                        onDelete = onDelete,
                                        onSpace = onSpace,
                                        onEnter = onEnter,
                                        onSwitchToAlpha = { layoutMode = LayoutMode.ALPHA },
                                        onSwitchToSymbols1 = { layoutMode = LayoutMode.SYMBOLS_1 },
                                        onSwitchLanguage = onSwitchLanguage,
                                        onMoveCursor = onMoveCursor
                                    )
                                }
                            }
                        }

                        // Right sidebar if one handed left
                        if (oneHandedMode == "LEFT") {
                            OneHandedSidebar(
                                colorScheme = colorScheme,
                                isLeft = false,
                                onSwitchSide = { onToggleOneHanded("RIGHT") },
                                onExpand = { onToggleOneHanded("OFF") }
                            )
                        }
                    }
                }
            }

            // Long Press Popup Overlay
            if (activePopupKey != null && activePopupKey!!.popupOptions.isNotEmpty()) {
                val key = activePopupKey!!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable { activePopupKey = null },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.background)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (option in key.popupOptions) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colorScheme.keyBackground)
                                    .clickable {
                                        onTextInput(option)
                                        activePopupKey = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    color = colorScheme.keyText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Language Picker Overlay
            if (showLanguagePicker) {
                LanguagePickerModal(
                    currentLanguage = currentLanguage,
                    colorScheme = colorScheme,
                    onSelect = { langId ->
                        onSelectLanguage(langId)
                        showLanguagePicker = false
                    },
                    onOpenManager = {
                        showLanguagePicker = false
                        onOpenSettings()
                    },
                    onDismiss = { showLanguagePicker = false }
                )
            }
        }
    }
}

@Composable
private fun OneHandedSidebar(
    colorScheme: KeyboardColorScheme,
    isLeft: Boolean,
    onSwitchSide: () -> Unit,
    onExpand: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(48.dp)
            .fillMaxHeight()
            .background(colorScheme.specialKeyBackground.copy(alpha = 0.5f))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground)
                .clickable(onClick = onSwitchSide),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLeft) Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                contentDescription = "تبديل الاتجاه",
                tint = colorScheme.accent,
                modifier = Modifier.size(20.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground)
                .clickable(onClick = onExpand),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "ملء الشاشة",
                tint = colorScheme.accent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ArabicKeyboardLayout(
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteAll: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onLongPressLanguage: (() -> Unit)? = null,
    onOpenClipboard: () -> Unit,
    onOpenEmoji: () -> Unit,
    onToggleTashkeel: () -> Unit,
    onMoveCursor: (Int) -> Unit,
    onLongPressKey: (KeyModel) -> Unit
) {
    // Quick Shortcut Row (👑 💋 ة ؤ ء ـ ئ ى ڷ 😂 خاص)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (key in KeyboardLayouts.arabicQuickRow) {
            KeyButton(
                text = key.primaryText,
                height = 36.dp,
                fontSize = if (key.primaryText.length > 2) 11.sp else 16.sp,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(if (key.primaryText.length > 2) 1.25f else 1f),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(key.primaryText)
            }
        }
    }

    // Row 1 (ض ص ق ف غ ع ه خ ح ج)
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow1) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = 20.sp,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(key.primaryText)
            }
        }
    }

    // Row 2 (ش س ي ب ل ا ت ن م ك)
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow2) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = 20.sp,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(key.primaryText)
            }
        }
    }

    // Row 3 (ظ ط ذ د ز ر و ة ث + Delete on the right!)
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow3) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = 20.sp,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(key.primaryText)
            }
        }

        // Repeating Delete Button on the Right
        RepeatingDeleteKeyButton(
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f),
            onDelete = onDelete,
            onDeleteAll = onDeleteAll
        )
    }

    // Row 4: Bottom control row matching screenshot
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 123!#()
        KeyButton(
            text = "123!#()",
            isSpecial = true,
            fontSize = 11.5.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) {
            onSwitchMode()
        }

        // Clipboard shortcut icon
        KeyButton(
            text = "📋",
            isSpecial = true,
            fontSize = 15.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.9f)
        ) {
            onOpenClipboard()
        }

        // Dash key
        KeyButton(
            text = "—",
            height = keyHeight,
            fontSize = 16.sp,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.8f)
        ) {
            onTextInput("—")
        }

        // Spacebar with "عربي اساسي" and gesture
        var totalDragX by remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier
                .weight(3.6f)
                .height(keyHeight)
                .padding(horizontal = 2.dp, vertical = 2.5.dp)
                .shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(8.dp),
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            if (totalDragX > 35f) {
                                onMoveCursor(1)
                                totalDragX = 0f
                            } else if (totalDragX < -35f) {
                                onMoveCursor(-1)
                                totalDragX = 0f
                            }
                        },
                        onDragEnd = { totalDragX = 0f }
                    )
                }
                .clickable { onSpace() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "عربي اساسي",
                    color = colorScheme.keyText.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = "└───┘",
                    color = colorScheme.keyText.copy(alpha = 0.4f),
                    fontSize = 9.sp,
                    lineHeight = 9.sp
                )
            }
        }

        // Period
        KeyButton(
            text = ".",
            height = keyHeight,
            fontSize = 18.sp,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.8f)
        ) {
            onTextInput(".")
        }

        // Question mark
        KeyButton(
            text = "؟",
            height = keyHeight,
            fontSize = 18.sp,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.8f)
        ) {
            onTextInput("؟")
        }

        // Emoji shortcut icon
        KeyButton(
            text = "😊",
            isSpecial = true,
            fontSize = 16.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.9f)
        ) {
            onOpenEmoji()
        }

        // Enter key
        Box(
            modifier = Modifier
                .weight(1.25f)
                .height(keyHeight)
                .padding(horizontal = 2.dp, vertical = 2.5.dp)
                .shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(8.dp),
                    ambientColor = Color.Black.copy(alpha = 0.35f),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.specialKeyBackground)
                .clickable { onEnter() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = "إدخال",
                tint = colorScheme.specialKeyText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DynamicKeyboardLayout(
    layoutData: KeyboardLayoutData,
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    shiftState: ShiftState,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onShiftClick: () -> Unit,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onLongPressLanguage: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onLongPressKey: (KeyModel) -> Unit
) {
    val isUpper = shiftState != ShiftState.OFF

    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in layoutData.row1) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                secondaryText = key.secondaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(letter)
            }
        }
    }

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in layoutData.row2) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(letter)
            }
        }
    }

    // Row 3
    Row(modifier = Modifier.fillMaxWidth()) {
        // Shift Key
        val shiftIcon = when (shiftState) {
            ShiftState.CAPS_LOCK -> "⇪"
            ShiftState.ON -> "⇧"
            ShiftState.OFF -> "⇧"
        }
        KeyButton(
            text = shiftIcon,
            isSpecial = true,
            fontSize = 18.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) {
            onShiftClick()
        }

        for (key in layoutData.row3.filter { it.type == KeyType.CHARACTER }) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(letter)
            }
        }

        // Backspace
        KeyButton(
            text = "⌫",
            isSpecial = true,
            fontSize = 18.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) {
            onDelete()
        }
    }

    // Bottom Row
    BottomControlRow(
        modeLabel = "?123",
        langLabel = "🌐",
        spaceLabel = layoutData.spaceLabel,
        commaLabel = ",",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        onSwitchMode = onSwitchMode,
        onSwitchLanguage = onSwitchLanguage,
        onLongPressLanguage = onLongPressLanguage,
        onSpace = onSpace,
        onEnter = onEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput
    )
}

@Composable
private fun LanguagePickerModal(
    currentLanguage: String,
    colorScheme: KeyboardColorScheme,
    onSelect: (String) -> Unit,
    onOpenManager: () -> Unit,
    onDismiss: () -> Unit
) {
    val langManager = KeyboardProApp.instance.languageManager
    val prefs = KeyboardProApp.instance.preferences
    val enabledLanguages = remember(prefs.enabledLanguages) {
        langManager.repository.getEnabledLanguages(prefs.enabledLanguages)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth(0.9f)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.background)
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
                        text = "🌐 اختر لغة الكتابة",
                        color = colorScheme.keyText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = colorScheme.keyText)
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(enabledLanguages, key = { it.id }) { lang ->
                        val isSelected = lang.id == currentLanguage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colorScheme.accent.copy(alpha = 0.2f) else colorScheme.keyBackground)
                                .clickable { onSelect(lang.id) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(lang.flag, fontSize = 22.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    lang.nameArabic,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) colorScheme.accent else colorScheme.keyText
                                )
                                Text(
                                    lang.nativeName,
                                    fontSize = 11.sp,
                                    color = colorScheme.keyText.copy(alpha = 0.6f)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = colorScheme.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = colorScheme.specialKeyBackground)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorScheme.specialKeyBackground)
                        .clickable(onClick = onOpenManager)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Language,
                        contentDescription = null,
                        tint = colorScheme.accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "إدارة اللغات وتنزيل الحزم (40+ لغة)",
                        color = colorScheme.accent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Symbols1Layout(
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols2: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onMoveCursor: (Int) -> Unit
) {
    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols1Row1) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols1Row2) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    // Row 3
    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            text = "#+=",
            isSpecial = true,
            fontSize = 13.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) { onSwitchToSymbols2() }

        for (key in KeyboardLayouts.symbols1Row3.filter { it.type == KeyType.CHARACTER }) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }

        KeyButton(
            text = "⌫",
            isSpecial = true,
            fontSize = 18.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) { onDelete() }
    }

    // Bottom Row
    BottomControlRow(
        modeLabel = "ABC",
        langLabel = "🌐",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        onSwitchMode = onSwitchToAlpha,
        onSwitchLanguage = onSwitchLanguage,
        onSpace = onSpace,
        onEnter = onEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput
    )
}

@Composable
private fun Symbols2Layout(
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols1: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onMoveCursor: (Int) -> Unit
) {
    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols2Row1) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.symbols2Row2) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }
    }

    // Row 3
    Row(modifier = Modifier.fillMaxWidth()) {
        KeyButton(
            text = "123",
            isSpecial = true,
            fontSize = 13.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) { onSwitchToSymbols1() }

        for (key in KeyboardLayouts.symbols2Row3.filter { it.type == KeyType.CHARACTER }) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f)
            ) { onTextInput(key.primaryText) }
        }

        KeyButton(
            text = "⌫",
            isSpecial = true,
            fontSize = 18.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) { onDelete() }
    }

    // Bottom Row
    BottomControlRow(
        modeLabel = "ABC",
        langLabel = "🌐",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        onSwitchMode = onSwitchToAlpha,
        onSwitchLanguage = onSwitchLanguage,
        onSpace = onSpace,
        onEnter = onEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput
    )
}

@Composable
private fun BottomControlRow(
    modeLabel: String,
    langLabel: String,
    spaceLabel: String = "مسافة",
    commaLabel: String = "،",
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onLongPressLanguage: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onMoveCursor: (Int) -> Unit,
    onTextInput: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode Switch (e.g. ?123 or ABC)
        KeyButton(
            text = modeLabel,
            isSpecial = true,
            fontSize = 13.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.2f)
        ) {
            onSwitchMode()
        }

        // Language Switch (🌐)
        KeyButton(
            text = langLabel,
            isSpecial = true,
            fontSize = 15.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.9f),
            onLongClick = onLongPressLanguage
        ) {
            onSwitchLanguage()
        }

        // Comma
        KeyButton(
            text = commaLabel,
            height = keyHeight,
            fontSize = 16.sp,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.8f)
        ) {
            onTextInput(commaLabel)
        }

        // Spacebar (with swipe gesture to move cursor or switch language)
        var totalDragX by remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier
                .weight(3.8f)
                .height(keyHeight)
                .padding(horizontal = 2.dp, vertical = 3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            if (totalDragX > 35f) {
                                onMoveCursor(1)
                                totalDragX = 0f
                            } else if (totalDragX < -35f) {
                                onMoveCursor(-1)
                                totalDragX = 0f
                            }
                        },
                        onDragEnd = { totalDragX = 0f }
                    )
                }
                .clickable { onSpace() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = spaceLabel,
                color = colorScheme.keyText.copy(alpha = 0.5f),
                fontSize = 12.sp,
                maxLines = 1
            )
        }

        // Period
        KeyButton(
            text = ".",
            height = keyHeight,
            fontSize = 18.sp,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.8f)
        ) {
            onTextInput(".")
        }

        // Enter / Action Key
        Box(
            modifier = Modifier
                .weight(1.3f)
                .height(keyHeight)
                .padding(horizontal = 2.dp, vertical = 3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.accent)
                .clickable { onEnter() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = "إدخال",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
