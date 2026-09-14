package com.example.ime.ui

import android.view.SoundEffectConstants
import android.view.inputmethod.EditorInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.util.HapticHelper
import com.example.ime.util.KeyboardLayoutController
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
    keyboardHeight: String, // "Small", "Medium", "Large", "ExtraLarge"
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
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: (Boolean) -> Unit = {},
    onChangeKeyboardHeight: (String) -> Unit = {},
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

    val keyHeight = KeyboardLayoutController.getKeyHeight(keyboardHeight)
    val panelHeight = remember(keyboardHeight, showNumberRow) {
        KeyboardLayoutController.getPanelHeight(keyboardHeight, showNumberRow)
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
            .wrapContentHeight(align = Alignment.Bottom)
            .background(colorScheme.background)
            .navigationBarsPadding()
    ) {
        // 1. Toolbar (always on top)
        KeyboardToolbar(
            activePanel = activePanel,
            currentLanguage = currentLanguage,
            isIncognito = isIncognito,
            autoTranslateOnEnter = autoTranslateOnEnter,
            colorScheme = colorScheme,
            onPanelSelect = { panel ->
                activePanel = panel
            },
            onSwitchLanguage = onSwitchLanguage,
            onOpenSettings = onOpenSettings
        )

        // 2. Suggestion Bar (عندما لا تكون اللوحات المخصصة مفتوحة وتكون مفعلة في الإعدادات)
        if (activePanel == KeyboardPanel.NONE && showSuggestions) {
            val latestClipText = remember(clipboardList) { clipboardList.firstOrNull()?.text }
            SuggestionBar(
                suggestions = suggestions,
                latestClip = latestClipText,
                colorScheme = colorScheme,
                onSelectSuggestion = onSelectSuggestion,
                onPasteClip = { clipText ->
                    onTextInput(clipText)
                }
            )
        }

        // 3. Main Area: Either Feature Panel or Keyboard Keys
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            when (activePanel) {
                KeyboardPanel.RESIZE -> {
                    ResizePanel(
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
                        currentHeight = keyboardHeight,
                        colorScheme = colorScheme,
                        onSelectHeight = { newH ->
                            onChangeKeyboardHeight(newH)
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.CLIPBOARD -> {
                    ClipboardPanel(
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
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
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
                        colorScheme = colorScheme,
                        onEmojiClick = { emoji -> onTextInput(emoji) },
                        onBackspace = onDelete,
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.STICKERS -> {
                    StickersPanel(
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
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
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
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
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
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
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
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
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
                        initialText = suggestions.firstOrNull() ?: "",
                        autoTranslateOnEnter = autoTranslateOnEnter,
                        colorScheme = colorScheme,
                        onToggleAutoTranslate = onToggleAutoTranslate,
                        onCommitTranslation = { translated ->
                            onTextInput(translated)
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }
                KeyboardPanel.DICTIONARY -> {
                    DictionaryPanel(
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
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
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
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
                    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
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
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                                                autoTranslateOnEnter = autoTranslateOnEnter,
                                                onToggleAutoTranslate = onToggleAutoTranslate,
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
                                                autoTranslateOnEnter = autoTranslateOnEnter,
                                                onToggleAutoTranslate = onToggleAutoTranslate,
                                                onTextInput = { char ->
                                                    val text = if (shiftState != ShiftState.OFF) char.uppercase() else char.lowercase()
                                                    onTextInput(text)
                                                    if (shiftState == ShiftState.ON) {
                                                        shiftState = ShiftState.OFF
                                                    }
                                                },
                                                onDelete = onDelete,
                                                onDeleteAll = onDeleteAll,
                                                onSpace = onSpace,
                                                onEnter = onEnter,
                                                onOpenEmoji = { activePanel = KeyboardPanel.EMOJI },
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
                                            autoTranslateOnEnter = autoTranslateOnEnter,
                                            onToggleAutoTranslate = onToggleAutoTranslate,
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
                                            autoTranslateOnEnter = autoTranslateOnEnter,
                                            onToggleAutoTranslate = onToggleAutoTranslate,
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
                val context = LocalContext.current
                val view = LocalView.current
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { activePopupKey = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.background),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable(enabled = false) {}
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "حروف بديلة لـ (${key.primaryText})",
                                color = colorScheme.keyText.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (option in key.popupOptions) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 48.dp, height = 54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colorScheme.keyBackground)
                                            .clickable(
                                                role = androidx.compose.ui.semantics.Role.Button,
                                                onClick = {
                                                    HapticHelper.performKeyHaptic(context, view)
                                                    onTextInput(option)
                                                    activePopupKey = null
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option,
                                            color = colorScheme.keyText,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
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
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
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
    onLongPressKey: (KeyModel) -> Unit,
    onOpenTranslate: (() -> Unit)? = null
) {
    // Row 1 (ض ص ث ق ف غ ع ه خ ح ج)
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow1) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = 19.sp,
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

    // Row 2 (ش س ي ب ل ا ت ن م ك ط)
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow2) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = 19.sp,
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

    // Row 3 (ذ ئ ء ؤ ر لا ى ة و ز ظ + Delete)
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.arabicRow3) {
            KeyButton(
                text = key.primaryText,
                secondaryText = key.secondaryText,
                height = keyHeight,
                fontSize = 19.sp,
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
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteAll = onDeleteAll
        )
    }

    // Row 4: Professional Bottom Control Row with Language Switcher in bottom row
    BottomControlRow(
        modeLabel = "١٢٣",
        langLabel = "🌐",
        spaceLabel = "العربية",
        commaLabel = "،",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
        onSwitchMode = onSwitchMode,
        onSwitchLanguage = onSwitchLanguage,
        onLongPressLanguage = onLongPressLanguage,
        onSpace = onSpace,
        onEnter = onEnter,
        onOpenEmoji = onOpenEmoji,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput
    )
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
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteAll: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onOpenEmoji: (() -> Unit)? = null,
    onShiftClick: () -> Unit,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onLongPressLanguage: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onLongPressKey: (KeyModel) -> Unit,
    onOpenTranslate: (() -> Unit)? = null
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
                fontSize = 19.sp,
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
                fontSize = 19.sp,
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
                fontSize = 19.sp,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight),
                onLongClick = if (key.popupOptions.isNotEmpty()) { { onLongPressKey(key) } } else null
            ) {
                onTextInput(letter)
            }
        }

        // Repeating Backspace Key
        RepeatingDeleteKeyButton(
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteAll = onDeleteAll
        )
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
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
        onSwitchMode = onSwitchMode,
        onSwitchLanguage = onSwitchLanguage,
        onLongPressLanguage = onLongPressLanguage,
        onSpace = onSpace,
        onEnter = onEnter,
        onOpenEmoji = onOpenEmoji,
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
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
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

        RepeatingDeleteKeyButton(
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete
        )
    }

    // Bottom Row
    BottomControlRow(
        modeLabel = "ABC",
        langLabel = "🌐",
        spaceLabel = "مسافة",
        commaLabel = ",",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
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
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
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

        RepeatingDeleteKeyButton(
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete
        )
    }

    // Bottom Row
    BottomControlRow(
        modeLabel = "ABC",
        langLabel = "🌐",
        spaceLabel = "مسافة",
        commaLabel = ",",
        colorScheme = colorScheme,
        keyHeight = keyHeight,
        hapticEnabled = hapticEnabled,
        soundEnabled = soundEnabled,
        actionIcon = actionIcon,
        autoTranslateOnEnter = autoTranslateOnEnter,
        onToggleAutoTranslate = onToggleAutoTranslate,
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
    langLabel: String = "🌐",
    spaceLabel: String = "مسافة",
    commaLabel: String = "،",
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    autoTranslateOnEnter: Boolean = false,
    onToggleAutoTranslate: ((Boolean) -> Unit)? = null,
    onSwitchMode: () -> Unit,
    onSwitchLanguage: () -> Unit,
    onLongPressLanguage: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onOpenEmoji: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onTextInput: (String) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode Switch (e.g. ?123, ١٢٣, ABC)
        KeyButton(
            text = modeLabel,
            isSpecial = true,
            fontSize = 13.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.25f)
        ) {
            onSwitchMode()
        }

        // Language Switch (🌐) located prominently at the bottom for easy access!
        KeyButton(
            text = langLabel,
            isSpecial = true,
            fontSize = 15.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.95f),
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
            modifier = Modifier.weight(0.85f)
        ) {
            onTextInput(commaLabel)
        }

        // Spacebar with sleek styling and cursor swipe gesture
        var totalDragX by remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier
                .weight(3.6f)
                .height(keyHeight)
                .padding(horizontal = 1.5.dp, vertical = 2.dp)
                .shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(6.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(6.dp))
                .background(colorScheme.keyBackground)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            if (totalDragX > 35f) {
                                if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                                onMoveCursor(1)
                                totalDragX = 0f
                            } else if (totalDragX < -35f) {
                                if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                                onMoveCursor(-1)
                                totalDragX = 0f
                            }
                        },
                        onDragEnd = { totalDragX = 0f }
                    )
                }
                .clickable {
                    if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                    if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
                    onSpace()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = spaceLabel,
                color = colorScheme.keyText.copy(alpha = 0.65f),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
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
            modifier = Modifier.weight(0.85f)
        ) {
            onTextInput(".")
        }

        // Emoji Button (😊)
        if (onOpenEmoji != null) {
            KeyButton(
                text = "😊",
                isSpecial = true,
                fontSize = 16.sp,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(0.95f)
            ) {
                onOpenEmoji()
            }
        }

        // Enter / Action Key with accent color, tap to execute and long-press for auto-translate toggle
        Box(
            modifier = Modifier
                .weight(1.35f)
                .height(keyHeight)
                .padding(horizontal = 1.5.dp, vertical = 2.dp)
                .shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(6.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(6.dp))
                .background(colorScheme.accent)
                .pointerInput(autoTranslateOnEnter) {
                    detectTapGestures(
                        onTap = {
                            if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                            if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
                            onEnter()
                        },
                        onLongPress = {
                            if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                            onToggleAutoTranslate?.invoke(!autoTranslateOnEnter)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = "إدخال",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            if (autoTranslateOnEnter) {
                Text(
                    text = "⚡",
                    fontSize = 9.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 1.dp, end = 2.dp)
                )
            }
        }
    }
}
