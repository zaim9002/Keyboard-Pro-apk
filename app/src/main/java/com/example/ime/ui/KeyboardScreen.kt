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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
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
import com.example.engine.DecorationEngine
import com.example.data.local.entity.ClipboardEntity
import com.example.data.local.entity.UserWordEntity
import com.example.ime.ui.panels.AiAssistantPanel
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
    SYMBOLS_2,
    NUMPAD
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
    spacebarLanguageSwitch: Boolean = true,
    onToggleAutoTranslate: (Boolean) -> Unit = {},
    onChangeKeyboardHeight: (String) -> Unit = {},
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: () -> Unit = {},
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
    onToggleOneHanded: (String) -> Unit,
    currentTypedWord: String? = null,
    isCurrentWordKnown: Boolean = true,
    userWords: List<UserWordEntity> = emptyList(),
    geminiApiKey: String? = null,
    currentDraftText: String = "",
    onApplyAiText: (String) -> Unit = {},
    onAddWordToDictionary: (String) -> Unit = {},
    onDeleteUserWord: (Long) -> Unit = {},
    onLongPressEnter: (() -> Unit)? = null,
    onTranslateNow: ((String, String) -> Unit)? = null
) {
    var layoutMode by remember { mutableStateOf(LayoutMode.ALPHA) }
    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    var activePanel by remember { mutableStateOf(KeyboardPanel.NONE) }
    var showTashkeelRow by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    var activePopupKey by remember { mutableStateOf<KeyModel?>(null) }
    var isInlineTranslateOpen by remember { mutableStateOf(false) }
    var showTermuxKeys by remember { mutableStateOf(false) }
    var showQuickSnippets by remember { mutableStateOf(false) }
    var translateSourceLang by remember { mutableStateOf("ar") }
    var translateTargetLang by remember { mutableStateOf("en") }
    val coroutineScope = rememberCoroutineScope()

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
            oneHandedMode = oneHandedMode,
            showTermuxKeys = showTermuxKeys,
            showQuickSnippets = showQuickSnippets,
            colorScheme = colorScheme,
            onPanelSelect = { panel ->
                if (panel == KeyboardPanel.TRANSLATE) {
                    isInlineTranslateOpen = !isInlineTranslateOpen
                    activePanel = KeyboardPanel.NONE
                } else {
                    activePanel = panel
                }
            },
            onSwitchLanguage = onSwitchLanguage,
            onOpenSettings = onOpenSettings,
            onToggleOneHanded = onToggleOneHanded,
            onToggleTermuxKeys = { showTermuxKeys = !showTermuxKeys },
            onToggleQuickSnippets = { showQuickSnippets = !showQuickSnippets }
        )

        // 1.5 Inline GBoard-style Translation Bar (Google GBoard style, Image 8)
        if (isInlineTranslateOpen && activePanel == KeyboardPanel.NONE) {
            InlineTranslateBar(
                sourceLang = translateSourceLang,
                targetLang = translateTargetLang,
                colorScheme = colorScheme,
                onSourceLangChange = { translateSourceLang = it },
                onTargetLangChange = { translateTargetLang = it },
                onSwapLanguages = {
                    val temp = translateSourceLang
                    translateSourceLang = translateTargetLang
                    translateTargetLang = temp
                },
                onTranslateNow = {
                    if (onTranslateNow != null) {
                        onTranslateNow(translateSourceLang, translateTargetLang)
                    } else {
                        val textToTranslate = currentDraftText.ifBlank { currentTypedWord ?: "" }
                        if (textToTranslate.isNotBlank()) {
                            coroutineScope.launch {
                                val translated = com.example.engine.TranslationEngine.translateAsync(
                                    textToTranslate,
                                    translateSourceLang,
                                    translateTargetLang
                                )
                                onTextInput(translated)
                            }
                        }
                    }
                },
                onClose = { isInlineTranslateOpen = false }
            )
        }

        // 2. Suggestion Bar (عندما لا تكون اللوحات المخصصة مفتوحة وتكون مفعلة في الإعدادات)
        if (activePanel == KeyboardPanel.NONE && showSuggestions) {
            val latestClipText = remember(clipboardList) { clipboardList.firstOrNull()?.text }
            SuggestionBar(
                suggestions = suggestions,
                latestClip = latestClipText,
                currentTypedWord = currentTypedWord,
                isCurrentWordKnown = isCurrentWordKnown,
                colorScheme = colorScheme,
                onSelectSuggestion = onSelectSuggestion,
                onPasteClip = { clipText ->
                    onTextInput(clipText)
                },
                onAddWordToDictionary = onAddWordToDictionary
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
                KeyboardPanel.AI_ASSISTANT -> {
                    AiAssistantPanel(
                        modifier = Modifier.fillMaxWidth().height(panelHeight),
                        initialText = currentDraftText.ifBlank { suggestions.firstOrNull() ?: "" },
                        apiKey = geminiApiKey,
                        userWords = userWords,
                        colorScheme = colorScheme,
                        onApplyText = { text ->
                            onApplyAiText(text)
                            activePanel = KeyboardPanel.NONE
                        },
                        onAddWordToDictionary = onAddWordToDictionary,
                        onDeleteUserWord = onDeleteUserWord,
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

                                // Optional Quick Snippets & Emoji Bar
                                if (showQuickSnippets && layoutMode == LayoutMode.ALPHA) {
                                    QuickSnippetsBar(
                                        colorScheme = colorScheme,
                                        hapticEnabled = hapticEnabled,
                                        onInsertText = onTextInput
                                    )
                                }

                                // Optional Termux & Developer Keys Row
                                if (showTermuxKeys) {
                                    TermuxKeysRow(
                                        colorScheme = colorScheme,
                                        hapticEnabled = hapticEnabled,
                                        onTextInput = onTextInput,
                                        onMoveCursor = onMoveCursor,
                                        onHome = { onMoveCursor(-999) },
                                        onEnd = { onMoveCursor(999) },
                                        onTab = { onTextInput("\t") },
                                        onEsc = { /* ESC action */ }
                                    )
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
                                                spacebarLanguageSwitch = spacebarLanguageSwitch,
                                                onTextInput = onTextInput,
                                                onDelete = onDelete,
                                                onDeleteWord = onDeleteWord,
                                                onDeleteAll = onDeleteAll,
                                                onSpace = onSpace,
                                                onEnter = onEnter,
                                                onLongPressEnter = onLongPressEnter,
                                                onSwitchMode = { layoutMode = LayoutMode.SYMBOLS_1 },
                                                onSwitchLanguage = onSwitchLanguage,
                                                onLongPressLanguage = { showLanguagePicker = true },
                                                onOpenClipboard = { activePanel = KeyboardPanel.CLIPBOARD },
                                                onOpenEmoji = { activePanel = KeyboardPanel.EMOJI },
                                                onOpenTranslate = { isInlineTranslateOpen = !isInlineTranslateOpen },
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
                                                spacebarLanguageSwitch = spacebarLanguageSwitch,
                                                onTextInput = { char ->
                                                    val text = if (shiftState != ShiftState.OFF) char.uppercase() else char.lowercase()
                                                    onTextInput(text)
                                                    if (shiftState == ShiftState.ON) {
                                                        shiftState = ShiftState.OFF
                                                    }
                                                },
                                                onDelete = onDelete,
                                                onDeleteWord = onDeleteWord,
                                                onDeleteAll = onDeleteAll,
                                                onSpace = onSpace,
                                                onEnter = onEnter,
                                                onLongPressEnter = onLongPressEnter,
                                                onOpenClipboard = { activePanel = KeyboardPanel.CLIPBOARD },
                                                onOpenTranslate = { isInlineTranslateOpen = !isInlineTranslateOpen },
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
                                            spacebarLanguageSwitch = spacebarLanguageSwitch,
                                            onTextInput = onTextInput,
                                            onDelete = onDelete,
                                            onDeleteWord = onDeleteWord,
                                            onSpace = onSpace,
                                            onEnter = onEnter,
                                            onLongPressEnter = onLongPressEnter,
                                            onSwitchToAlpha = { layoutMode = LayoutMode.ALPHA },
                                            onSwitchToSymbols2 = { layoutMode = LayoutMode.SYMBOLS_2 },
                                            onSwitchToNumpad = { layoutMode = LayoutMode.NUMPAD },
                                            onOpenClipboard = { activePanel = KeyboardPanel.CLIPBOARD },
                                            onOpenTranslate = { isInlineTranslateOpen = !isInlineTranslateOpen },
                                            onOpenEmoji = { activePanel = KeyboardPanel.EMOJI },
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
                                            spacebarLanguageSwitch = spacebarLanguageSwitch,
                                            onTextInput = onTextInput,
                                            onDelete = onDelete,
                                            onDeleteWord = onDeleteWord,
                                            onSpace = onSpace,
                                            onEnter = onEnter,
                                            onLongPressEnter = onLongPressEnter,
                                            onSwitchToAlpha = { layoutMode = LayoutMode.ALPHA },
                                            onSwitchToSymbols1 = { layoutMode = LayoutMode.SYMBOLS_1 },
                                            onOpenClipboard = { activePanel = KeyboardPanel.CLIPBOARD },
                                            onOpenTranslate = { isInlineTranslateOpen = !isInlineTranslateOpen },
                                            onOpenEmoji = { activePanel = KeyboardPanel.EMOJI },
                                            onSwitchLanguage = onSwitchLanguage,
                                            onMoveCursor = onMoveCursor
                                        )
                                    }
                                    LayoutMode.NUMPAD -> {
                                        NumpadKeyboardLayout(
                                            colorScheme = colorScheme,
                                            keyHeight = keyHeight,
                                            hapticEnabled = hapticEnabled,
                                            soundEnabled = soundEnabled,
                                            actionIcon = actionIcon,
                                            onTextInput = onTextInput,
                                            onDelete = onDelete,
                                            onDeleteWord = onDeleteWord,
                                            onEnter = onEnter,
                                            onSwitchToAlpha = { layoutMode = LayoutMode.ALPHA },
                                            onSwitchToSymbols = { layoutMode = LayoutMode.SYMBOLS_1 }
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

            // Long Press Popup Overlay with alternative letters, Hamzat, and Tashkeel
            if (activePopupKey != null) {
                val key = activePopupKey!!
                val decorations = DecorationEngine.getLetterDecorations(key.primaryText)
                val baseOptions = key.popupOptions.ifEmpty { listOf(key.primaryText) }
                val allOptions = (baseOptions + decorations + listOfNotNull(key.secondaryText)).distinct().filter { it.isNotBlank() }
                val tashkeelOptions = listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ", "ـ")
                
                val context = LocalContext.current
                val view = LocalView.current
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { activePopupKey = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.background),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        modifier = Modifier
                            .padding(12.dp)
                            .clickable(enabled = false) {}
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الحروف والتشكيلات لـ (${key.primaryText})",
                                    color = colorScheme.keyText.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(colorScheme.specialKeyBackground)
                                        .clickable { activePopupKey = null },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✕",
                                        color = colorScheme.keyText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // 1. Primary Alternate Letters & Variants
                            if (allOptions.isNotEmpty()) {
                                androidx.compose.foundation.lazy.LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    items(allOptions) { option ->
                                        Box(
                                            modifier = Modifier
                                                .size(width = 48.dp, height = 52.dp)
                                                .shadow(1.dp, RoundedCornerShape(10.dp))
                                                .clip(RoundedCornerShape(10.dp))
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

                            // 2. Quick Tashkeel Row (حركات التشكيل السريعة)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (tashkeel in tashkeelOptions) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(colorScheme.specialKeyBackground)
                                            .clickable {
                                                HapticHelper.performKeyHaptic(context, view)
                                                onTextInput(tashkeel)
                                                activePopupKey = null
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tashkeel,
                                            color = colorScheme.accent,
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
    spacebarLanguageSwitch: Boolean = true,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
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
                onLongClick = { onLongPressKey(key) }
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
                onLongClick = { onLongPressKey(key) }
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
                onLongClick = { onLongPressKey(key) }
            ) {
                onTextInput(key.primaryText)
            }
        }

        // Repeating Delete Button on the Right with left-swipe word deletion
        RepeatingDeleteKeyButton(
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteWord = onDeleteWord,
            onDeleteAll = onDeleteAll
        )
    }

    // Row 4: Professional Bottom Control Row
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
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onOpenEmoji = onOpenEmoji,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
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
    spacebarLanguageSwitch: Boolean = true,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onDeleteAll: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onOpenClipboard: (() -> Unit)? = null,
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
                onLongClick = { onLongPressKey(key) }
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
                onLongClick = { onLongPressKey(key) }
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
                onLongClick = { onLongPressKey(key) }
            ) {
                onTextInput(letter)
            }
        }

        // Repeating Backspace Key with word-delete swipe
        RepeatingDeleteKeyButton(
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.35f),
            onDelete = onDelete,
            onDeleteWord = onDeleteWord,
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
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onOpenEmoji = onOpenEmoji,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
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
    spacebarLanguageSwitch: Boolean = false,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols2: () -> Unit,
    onSwitchToNumpad: (() -> Unit)? = null,
    onOpenClipboard: (() -> Unit)? = null,
    onOpenTranslate: (() -> Unit)? = null,
    onOpenEmoji: (() -> Unit)? = null,
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
            modifier = Modifier.weight(1.0f)
        ) { onSwitchToSymbols2() }

        if (onSwitchToNumpad != null) {
            KeyButton(
                text = "123",
                isSpecial = true,
                fontSize = 12.sp,
                height = keyHeight,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(0.9f)
            ) { onSwitchToNumpad() }
        }

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
            onDelete = onDelete,
            onDeleteWord = onDeleteWord
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
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onOpenEmoji = onOpenEmoji,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
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
    spacebarLanguageSwitch: Boolean = false,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols1: () -> Unit,
    onOpenClipboard: (() -> Unit)? = null,
    onOpenTranslate: (() -> Unit)? = null,
    onOpenEmoji: (() -> Unit)? = null,
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
            onDelete = onDelete,
            onDeleteWord = onDeleteWord
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
        onOpenClipboard = onOpenClipboard,
        onOpenTranslate = onOpenTranslate,
        onOpenEmoji = onOpenEmoji,
        onSpace = onSpace,
        onEnter = onEnter,
        onLongPressEnter = onLongPressEnter,
        onMoveCursor = onMoveCursor,
        onTextInput = onTextInput,
        spacebarLanguageSwitch = spacebarLanguageSwitch
    )
}

@Composable
private fun NumpadKeyboardLayout(
    colorScheme: KeyboardColorScheme,
    keyHeight: androidx.compose.ui.unit.Dp,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onTextInput: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteWord: (() -> Unit)? = null,
    onEnter: () -> Unit,
    onSwitchToAlpha: () -> Unit,
    onSwitchToSymbols: () -> Unit
) {
    // Row 1: ( ) 1 2 3 ABC
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow1) {
            KeyButton(
                text = key.primaryText,
                isSpecial = key.type != KeyType.CHARACTER,
                height = keyHeight,
                fontSize = if (key.type == KeyType.CHARACTER) 20.sp else 14.sp,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight)
            ) {
                if (key.type == KeyType.SWITCH_MODE) {
                    onSwitchToAlpha()
                } else {
                    onTextInput(key.primaryText)
                }
            }
        }
    }

    // Row 2: + - 4 5 6 =
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow2) {
            KeyButton(
                text = key.primaryText,
                isSpecial = key.type != KeyType.CHARACTER,
                height = keyHeight,
                fontSize = 20.sp,
                colorScheme = colorScheme,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                modifier = Modifier.weight(key.weight)
            ) {
                onTextInput(key.primaryText)
            }
        }
    }

    // Row 3: / % 7 8 9 ⌫
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow3) {
            if (key.type == KeyType.BACKSPACE) {
                RepeatingDeleteKeyButton(
                    height = keyHeight,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    modifier = Modifier.weight(key.weight),
                    onDelete = onDelete,
                    onDeleteWord = onDeleteWord
                )
            } else {
                KeyButton(
                    text = key.primaryText,
                    height = keyHeight,
                    fontSize = 20.sp,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    modifier = Modifier.weight(key.weight)
                ) {
                    onTextInput(key.primaryText)
                }
            }
        }
    }

    // Row 4: 123!#() , * 0 . ↵
    Row(modifier = Modifier.fillMaxWidth()) {
        for (key in KeyboardLayouts.numpadRow4) {
            if (key.type == KeyType.ENTER) {
                Box(
                    modifier = Modifier
                        .weight(key.weight)
                        .height(keyHeight)
                        .padding(horizontal = 1.5.dp, vertical = 2.dp)
                        .shadow(1.dp, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
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
            } else if (key.type == KeyType.SWITCH_MODE) {
                KeyButton(
                    text = key.primaryText,
                    isSpecial = true,
                    height = keyHeight,
                    fontSize = 12.sp,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    modifier = Modifier.weight(key.weight)
                ) {
                    onSwitchToSymbols()
                }
            } else {
                KeyButton(
                    text = key.primaryText,
                    height = keyHeight,
                    fontSize = 20.sp,
                    colorScheme = colorScheme,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    modifier = Modifier.weight(key.weight)
                ) {
                    onTextInput(key.primaryText)
                }
            }
        }
    }
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
    onOpenClipboard: (() -> Unit)? = null,
    onOpenTranslate: (() -> Unit)? = null,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onLongPressEnter: (() -> Unit)? = null,
    onOpenEmoji: (() -> Unit)? = null,
    onMoveCursor: (Int) -> Unit,
    onTextInput: (String) -> Unit,
    spacebarLanguageSwitch: Boolean = false
) {
    val context = LocalContext.current
    val view = LocalView.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Mode Switch (e.g. ?123, ١٢٣, ABC)
        KeyButton(
            text = modeLabel,
            isSpecial = true,
            fontSize = 13.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(1.0f)
        ) {
            onSwitchMode()
        }

        // 2. Language Switch (🌐)
        KeyButton(
            text = langLabel,
            isSpecial = true,
            fontSize = 14.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.85f),
            onLongClick = onLongPressLanguage
        ) {
            onSwitchLanguage()
        }

        // 3. Clipboard Button (📋) - Placed in the bottom row (where faces used to be)
        KeyButton(
            text = "📋",
            isSpecial = true,
            fontSize = 15.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.85f)
        ) {
            if (onOpenClipboard != null) {
                onOpenClipboard()
            }
        }

        // 4. Spacebar - Tap for space; swipe left/right to switch language
        var totalDragX by remember { mutableStateOf(0f) }
        Box(
            modifier = Modifier
                .weight(3.4f)
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
                                onSwitchLanguage()
                                totalDragX = 0f
                            } else if (totalDragX < -35f) {
                                if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                                onSwitchLanguage()
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

        // 5. Period Key (.)
        KeyButton(
            text = ".",
            height = keyHeight,
            fontSize = 18.sp,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.75f)
        ) {
            onTextInput(".")
        }

        // 6. Emoji / Faces Button (😊) - In place of the translation button
        KeyButton(
            text = "😊",
            isSpecial = true,
            fontSize = 15.sp,
            height = keyHeight,
            colorScheme = colorScheme,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled,
            modifier = Modifier.weight(0.85f)
        ) {
            if (onOpenEmoji != null) {
                onOpenEmoji()
            }
        }

        // 7. Action / Enter Key - Tap triggers Enter; Long Press (~3.5-4s) triggers Translation
        var isEnterPressed by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .weight(1.15f)
                .height(keyHeight)
                .padding(horizontal = 1.5.dp, vertical = 2.dp)
                .shadow(
                    elevation = 1.dp,
                    shape = RoundedCornerShape(6.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(6.dp))
                .background(if (isEnterPressed) colorScheme.accent.copy(alpha = 0.75f) else colorScheme.accent)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isEnterPressed = true
                            if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                            if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
                            tryAwaitRelease()
                            isEnterPressed = false
                        },
                        onLongPress = {
                            if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                            if (soundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
                            if (onLongPressEnter != null) {
                                onLongPressEnter()
                            } else if (onOpenTranslate != null) {
                                onOpenTranslate()
                            }
                        },
                        onTap = {
                            onEnter()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = "إدخال (اضغط مطولاً للترجمة)",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
