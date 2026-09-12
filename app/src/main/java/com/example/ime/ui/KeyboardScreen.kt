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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ClipboardEntity
import com.example.ime.layout.KeyboardLayouts
import com.example.ime.layout.KeyModel
import com.example.ime.layout.KeyType
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.ui.components.*
import com.example.ime.ui.panels.*

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
    currentLanguage: String, // "ar" or "en"
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
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    onSpace: () -> Unit,
    onSwitchLanguage: () -> Unit,
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

        // 2. Suggestion Bar (عندما لا تكون اللوحات المخصصة مفتوحة)
        if (activePanel == KeyboardPanel.NONE) {
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
                                val numRow = if (currentLanguage == "ar") KeyboardLayouts.arabicNumberRow else KeyboardLayouts.englishNumberRow
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
                                            onSpace = onSpace,
                                            onEnter = onEnter,
                                            onSwitchMode = { layoutMode = LayoutMode.SYMBOLS_1 },
                                            onSwitchLanguage = onSwitchLanguage,
                                            onToggleTashkeel = { showTashkeelRow = !showTashkeelRow },
                                            onMoveCursor = onMoveCursor,
                                            onLongPressKey = { activePopupKey = it }
                                        )
                                    } else {
                                        EnglishKeyboardLayout(
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
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onToggleTashkeel: () -> Unit,
    onMoveCursor: (Int) -> Unit,
    onLongPressKey: (KeyModel) -> Unit
) {
    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow1) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
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

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow2) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
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

    // Row 3
    Row(modifier = Modifier.fillMaxWidth()) {
        // Tashkeel trigger button
        KeyButton(
            text = "تشكيل َ",
            isSpecial = true,
            fontSize = 11.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.3f)
        ) {
            onToggleTashkeel()
        }

        // Arabic Letters
        for (key in KeyboardLayouts.arabicRow3.filter { it.type == KeyType.CHARACTER }) {
            KeyButton(
                text = key.primaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(key.primaryText)
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

    // Row 4: Bottom row
    BottomControlRow(
        modeLabel = "١٢٣",
        langLabel = "🌐",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        onSwitchMode = onSwitchMode,
        onSwitchLanguage = onSwitchLanguage,
        onSpace = onSpace,
        onEnter = onEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput
    )
}

@Composable
private fun EnglishKeyboardLayout(
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
    onMoveCursor: (Int) -> Unit,
    onLongPressKey: (KeyModel) -> Unit
) {
    val isUpper = shiftState != ShiftState.OFF

    // Row 1
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.englishRow1) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                secondaryText = key.secondaryText,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(letter)
            }
        }
    }

    // Row 2
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.englishRow2) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f),
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

        for (key in KeyboardLayouts.englishRow3.filter { it.type == KeyType.CHARACTER }) {
            val letter = if (isUpper) key.primaryText.uppercase() else key.primaryText.lowercase()
            KeyButton(
                text = letter,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(1f),
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
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        onSwitchMode = onSwitchMode,
        onSwitchLanguage = onSwitchLanguage,
        onSpace = onSpace,
        onEnter = onEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput
    )
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
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
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
            modifier = Modifier.weight(0.9f)
        ) {
            onSwitchLanguage()
        }

        // Comma
        KeyButton(
            text = "،",
            height = keyHeight,
            fontSize = 16.sp,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.8f)
        ) {
            onTextInput("،")
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
                text = "مسافة",
                color = colorScheme.keyText.copy(alpha = 0.5f),
                fontSize = 12.sp
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
