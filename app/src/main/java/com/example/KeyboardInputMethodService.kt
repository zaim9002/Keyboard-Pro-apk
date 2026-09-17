package com.example

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ClipboardEntity
import com.example.data.pref.KeyboardPreferences
import com.example.data.repository.ClipboardRepository
import com.example.data.repository.ShortcutRepository
import com.example.data.repository.UserWordRepository
import com.example.engine.SuggestionEngine
import com.example.engine.TranslationEngine
import com.example.ime.ComposeInputMethodService
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen
import com.example.ime.ui.SafeFallbackKeyboardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

open class KeyboardInputMethodService : ComposeInputMethodService() {

    private var clipboardRepo: ClipboardRepository? = null
    private var shortcutRepo: ShortcutRepository? = null
    private var userWordRepo: UserWordRepository? = null
    private var suggestionEngine: SuggestionEngine? = null

    private val prefs: KeyboardPreferences by lazy {
        try {
            (applicationContext as? KeyboardProApp)?.preferences ?: KeyboardPreferences(this)
        } catch (e: Throwable) {
            KeyboardPreferences(this)
        }
    }

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    private val suggestions = _suggestions.asStateFlow()

    private val _clipboardItems = MutableStateFlow<List<ClipboardEntity>>(emptyList())
    private val clipboardItems = _clipboardItems.asStateFlow()

    private val _userWords = MutableStateFlow<List<com.example.data.local.entity.UserWordEntity>>(emptyList())
    private val userWords = _userWords.asStateFlow()

    private val _currentTypedWord = MutableStateFlow("")
    private val currentTypedWord = _currentTypedWord.asStateFlow()

    private val _isCurrentWordKnown = MutableStateFlow(true)
    private val isCurrentWordKnown = _isCurrentWordKnown.asStateFlow()

    private val _currentDraftText = MutableStateFlow("")
    private val currentDraftText = _currentDraftText.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var isVoiceListening by mutableStateOf(false)
    private var voiceStatusText by mutableStateOf("اضغط على الميكروفون للبدء")
    private var voicePartialText by mutableStateOf("")

    private var currentEditorInfo: EditorInfo? = null
    private var isPasswordField by mutableStateOf(false)
    private val currentWordBuffer = java.lang.StringBuilder()

    private var clipManager: ClipboardManager? = null
    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        try {
            if (!isPasswordField && !prefs.isIncognito) {
                val clip = clipManager?.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val text = clip.getItemAt(0).text?.toString()
                    if (!text.isNullOrBlank()) {
                        lifecycleScope.launch {
                            try {
                                clipboardRepo?.insertOrUpdate(text)
                            } catch (e: Throwable) {
                                // Ignore DB error
                            }
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            // Android 10+ clipboard access restriction handled safely
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            val db = try {
                (applicationContext as? KeyboardProApp)?.database ?: AppDatabase.getDatabase(this)
            } catch (e: Throwable) {
                AppDatabase.getDatabase(this)
            }

            clipboardRepo = ClipboardRepository(db.clipboardDao())
            shortcutRepo = ShortcutRepository(db.shortcutDao())
            userWordRepo = UserWordRepository(db.userWordDao())
            suggestionEngine = SuggestionEngine(userWordRepo!!, shortcutRepo!!)

            clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipManager?.addPrimaryClipChangedListener(clipListener)

            lifecycleScope.launch {
                try {
                    clipboardRepo?.allClips?.collect { list ->
                        _clipboardItems.value = list
                    }
                } catch (e: Throwable) {
                    Log.w("KeyboardIME", "Clips collector error: ${e.message}")
                }
            }

            lifecycleScope.launch {
                try {
                    userWordRepo?.allWords?.collect { list ->
                        _userWords.value = list
                    }
                } catch (e: Throwable) {
                    Log.w("KeyboardIME", "UserWords collector error: ${e.message}")
                }
            }
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in onCreate", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            clipManager?.removePrimaryClipChangedListener(clipListener)
        } catch (e: Throwable) {}
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Throwable) {}
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        // Never cover full screen with extract view, always keep keyboard bottom-anchored
        return false
    }

    override fun onEvaluateInputViewShown(): Boolean {
        return super.onEvaluateInputViewShown() || true
    }

    override fun onCreateInputView(): View {
        return try {
            createKeyboardView()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Failed to create Compose keyboard, falling back to Safe Mode", e)
            createSafeFallbackKeyboard()
        }
    }

    private fun createKeyboardView(): View {
        // Attach ViewTree owners to SoftInputWindow's decor view
        try {
            window?.window?.decorView?.let { decorView ->
                decorView.setViewTreeLifecycleOwner(this)
                decorView.setViewTreeViewModelStoreOwner(this)
                decorView.setViewTreeSavedStateRegistryOwner(this)
            }
        } catch (e: Throwable) {
            Log.w("KeyboardIME", "Could not set decorView tree owners: ${e.message}")
        }

        val composeView = ComposeView(this).apply {
            layoutParams = android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.BOTTOM
            }
            setViewTreeLifecycleOwner(this@KeyboardInputMethodService)
            setViewTreeViewModelStoreOwner(this@KeyboardInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@KeyboardInputMethodService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
                val oldH = oldBottom - oldTop
                val newH = bottom - top
                if (oldH != newH && newH > 0) {
                    window?.window?.decorView?.let { decor ->
                        decor.post {
                            try {
                                decor.requestLayout()
                            } catch (e: Throwable) {
                                // Ignore layout request errors
                            }
                        }
                    }
                }
            }
        }
        keyboardRootView = composeView

        composeView.setContent {
            val currentThemeName by prefs.themeState.collectAsState()
            val currentLang by prefs.languageState.collectAsState()
            val hapticSetting by prefs.hapticState.collectAsState()
            val keyboardHeight by prefs.heightState.collectAsState()
            val showNumberRow by prefs.numberRowState.collectAsState()
            val showSuggestions by prefs.suggestionsState.collectAsState()
            val arabicNumerals by prefs.arabicNumeralsState.collectAsState()
            val isIncognitoPref by prefs.incognitoState.collectAsState()
            val autoTranslateOnEnter by prefs.autoTranslateOnEnterState.collectAsState()
            val spacebarLangSwitch by prefs.spacebarLanguageSwitchState.collectAsState()
            val oneHandedModePref by prefs.oneHandedState.collectAsState()
            val langManager = (applicationContext as? KeyboardProApp)?.languageManager

            val effectiveIncognito = isIncognitoPref || isPasswordField
            val currentTheme = KeyboardThemes.getTheme(currentThemeName)
            val currentSuggestions by suggestions.collectAsState()
            val clips by clipboardItems.collectAsState()
            val currentWord by currentTypedWord.collectAsState()
            val isKnown by isCurrentWordKnown.collectAsState()
            val wordsList by userWords.collectAsState()
            val draftText by currentDraftText.collectAsState()

            KeyboardScreen(
                colorScheme = currentTheme,
                currentLanguage = currentLang,
                imeOptions = currentEditorInfo?.imeOptions ?: EditorInfo.IME_ACTION_DONE,
                isIncognito = effectiveIncognito,
                keyboardHeight = keyboardHeight,
                showNumberRow = showNumberRow,
                hapticEnabled = hapticSetting != "Off",
                soundEnabled = prefs.keySound != "Off",
                oneHandedMode = oneHandedModePref,
                suggestions = currentSuggestions,
                clipboardList = clips,
                isVoiceListening = isVoiceListening,
                voiceStatusText = voiceStatusText,
                voicePartialText = voicePartialText,
                showSuggestions = showSuggestions,
                arabicNumerals = arabicNumerals,
                autoTranslateOnEnter = autoTranslateOnEnter,
                spacebarLanguageSwitch = spacebarLangSwitch,
                currentTypedWord = currentWord,
                isCurrentWordKnown = isKnown,
                userWords = wordsList,
                geminiApiKey = prefs.geminiApiKey,
                currentDraftText = draftText,
                onApplyAiText = { text -> handleApplyAiText(text) },
                onAddWordToDictionary = { word -> handleAddWordToDictionary(word) },
                onDeleteUserWord = { id -> handleDeleteUserWord(id) },
                onToggleAutoTranslate = { enabled ->
                    prefs.autoTranslateOnEnter = enabled
                },
                onChangeKeyboardHeight = { newHeight ->
                    prefs.keyboardHeight = newHeight
                },
                onTextInput = { text -> handleTextInput(text) },
                onDelete = { handleDelete() },
                onDeleteWord = { handleDeleteWord() },
                onDeleteAll = { handleDeleteAll() },
                onEnter = { handleEnter() },
                onLongPressEnter = {
                    handleTranslate(prefs.translateSourceLang, prefs.translateTargetLang)
                },
                onTranslateNow = { src, tgt ->
                    handleTranslate(src, tgt)
                },
                onSpace = { handleSpace() },
                onSwitchLanguage = {
                    if (langManager != null) {
                        langManager.cycleNextLanguage()
                    } else {
                        val newLang = if (prefs.currentLanguage == "ar") "en" else "ar"
                        prefs.currentLanguage = newLang
                    }
                    updateSuggestions()
                },
                onSelectLanguage = { langId ->
                    if (langManager != null) {
                        langManager.switchLanguage(langId)
                    } else {
                        prefs.currentLanguage = langId
                    }
                    updateSuggestions()
                },
                onMoveCursor = { delta -> moveCursor(delta) },
                onSelectSuggestion = { suggestion -> handleSelectSuggestion(suggestion) },
                onTogglePinClip = { id, pinned ->
                    lifecycleScope.launch {
                        try { clipboardRepo?.togglePin(id, pinned) } catch (e: Throwable) {}
                    }
                },
                onDeleteClip = { id ->
                    lifecycleScope.launch {
                        try { clipboardRepo?.deleteById(id) } catch (e: Throwable) {}
                    }
                },
                onClearUnpinnedClips = {
                    lifecycleScope.launch {
                        try { clipboardRepo?.clearUnpinned() } catch (e: Throwable) {}
                    }
                },
                onStartVoice = { startVoiceTyping() },
                onStopVoice = { stopVoiceTyping() },
                onSelectAll = { performContextAction(android.R.id.selectAll) },
                onCut = { performContextAction(android.R.id.cut) },
                onCopy = { performContextAction(android.R.id.copy) },
                onPaste = { performContextAction(android.R.id.paste) },
                onUndo = { performContextAction(android.R.id.undo) },
                onRedo = { performContextAction(android.R.id.redo) },
                onOpenSettings = {
                    try {
                        val intent = Intent(this@KeyboardInputMethodService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    } catch (e: Throwable) {
                        Log.e("KeyboardIME", "Error opening settings activity", e)
                    }
                },
                onToggleOneHanded = { mode ->
                    prefs.oneHandedMode = mode
                }
            )
        }

        return composeView
    }

    private fun createSafeFallbackKeyboard(): View {
        return SafeFallbackKeyboardView(
            context = this,
            onTextInput = { text -> handleTextInput(text) },
            onDelete = { handleDelete() },
            onEnter = { handleEnter() },
            onRetryCompose = {
                try {
                    setInputView(createKeyboardView())
                } catch (e: Throwable) {
                    Log.e("KeyboardIME", "Retry compose keyboard failed", e)
                }
            }
        )
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentEditorInfo = attribute

        // Detect password variation
        val inputType = attribute?.inputType ?: 0
        val isPassword = when (inputType and InputType.TYPE_MASK_CLASS) {
            InputType.TYPE_CLASS_TEXT -> {
                val variation = inputType and InputType.TYPE_MASK_VARIATION
                variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
            }
            InputType.TYPE_CLASS_NUMBER -> {
                val variation = inputType and InputType.TYPE_MASK_VARIATION
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }
            else -> false
        }
        isPasswordField = isPassword
        currentWordBuffer.clear()
        updateSuggestions()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        syncPrimaryClip()
        try {
            val ic = currentInputConnection
            val before = ic?.getTextBeforeCursor(500, 0)?.toString() ?: ""
            _currentDraftText.value = before.substringAfterLast('\n').trim()
        } catch (e: Throwable) {}
    }

    private fun syncPrimaryClip() {
        try {
            val clip = clipManager?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()
                if (!text.isNullOrBlank()) {
                    lifecycleScope.launch {
                        try {
                            clipboardRepo?.insertOrUpdate(text)
                        } catch (e: Throwable) {
                            // Ignore
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            // Handled
        }
    }

    private fun handleTextInput(text: String) {
        try {
            val ic = currentInputConnection ?: return
            ic.commitText(text, 1)
            currentWordBuffer.append(text)
            prefs.incrementWordCount()
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleTextInput", e)
        }
    }

    private fun handleDelete() {
        try {
            val ic = currentInputConnection ?: return
            val selectedText = ic.getSelectedText(0)
            if (!selectedText.isNullOrEmpty()) {
                ic.commitText("", 1)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
            if (currentWordBuffer.isNotEmpty()) {
                currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
            }
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleDelete", e)
        }
    }

    private fun handleDeleteAll() {
        try {
            val ic = currentInputConnection ?: return
            val selectedText = ic.getSelectedText(0)
            if (!selectedText.isNullOrEmpty()) {
                ic.commitText("", 1)
            } else {
                // Delete line or surroundings
                ic.deleteSurroundingText(2500, 2500)
            }
            currentWordBuffer.clear()
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleDeleteAll", e)
        }
    }

    private fun handleDeleteWord() {
        try {
            val ic = currentInputConnection ?: return
            val selectedText = ic.getSelectedText(0)
            if (!selectedText.isNullOrEmpty()) {
                ic.commitText("", 1)
                currentWordBuffer.clear()
                updateSuggestions()
                return
            }
            val textBefore = ic.getTextBeforeCursor(120, 0)?.toString() ?: ""
            if (textBefore.isEmpty()) {
                ic.deleteSurroundingText(1, 0)
                return
            }
            var i = textBefore.length - 1
            // Skip trailing whitespace
            while (i >= 0 && textBefore[i].isWhitespace()) {
                i--
            }
            // Skip word characters
            while (i >= 0 && !textBefore[i].isWhitespace()) {
                i--
            }
            val deleteCount = textBefore.length - 1 - i
            if (deleteCount > 0) {
                ic.deleteSurroundingText(deleteCount, 0)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
            currentWordBuffer.clear()
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleDeleteWord", e)
        }
    }

    private fun handleSpace() {
        try {
            val ic = currentInputConnection ?: return

            // 1. Check if user typed a shortcut trigger (e.g. "سلام" -> "السلام عليكم ورحمة الله وبركاته")
            val word = currentWordBuffer.toString().trim()
            if (word.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val shortcut = shortcutRepo?.findByTrigger(word)
                        if (shortcut != null) {
                            ic.deleteSurroundingText(word.length, 0)
                            ic.commitText(shortcut.replacement + " ", 1)
                            currentWordBuffer.clear()
                            updateSuggestions()
                            return@launch
                        }
                    } catch (e: Throwable) {
                        // Ignore shortcut lookup error
                    }
                }

                // 2. Real-time auto-correction on space
                if (prefs.autoCorrectEnabled) {
                    val isArabic = prefs.currentLanguage == "ar"
                    val autoCorrection = suggestionEngine?.getAutoCorrection(word, isArabic)
                    if (autoCorrection != null && autoCorrection != word) {
                        ic.deleteSurroundingText(word.length, 0)
                        ic.commitText(autoCorrection + " ", 1)
                        currentWordBuffer.clear()
                        updateSuggestions()
                        return
                    }
                }
            }

            ic.commitText(" ", 1)
            currentWordBuffer.append(" ")
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleSpace", e)
        }
    }

    private fun handleEnter() {
        try {
            val ic = currentInputConnection ?: return

            // If Auto-Translate on Enter is enabled, translate current typed text before sending/entering
            if (prefs.autoTranslateOnEnter) {
                val textBefore = ic.getTextBeforeCursor(500, 0)?.toString() ?: ""
                val lastLine = textBefore.substringAfterLast('\n').trim()
                if (lastLine.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val translated = TranslationEngine.translateAsync(
                            text = lastLine,
                            sourceLang = prefs.translateSourceLang,
                            targetLang = prefs.translateTargetLang
                        )
                        withContext(Dispatchers.Main) {
                            try {
                                val currentIc = currentInputConnection ?: return@withContext
                                val currentTextBefore = currentIc.getTextBeforeCursor(500, 0)?.toString() ?: ""
                                val currentLastLine = currentTextBefore.substringAfterLast('\n')
                                currentIc.deleteSurroundingText(currentLastLine.length, 0)
                                currentIc.commitText(translated, 1)
                                executeStandardEnter(currentIc)
                            } catch (e: Throwable) {
                                Log.e("KeyboardIME", "Error replacing text with translation", e)
                                executeStandardEnter(currentInputConnection ?: return@withContext)
                            }
                        }
                    }
                    return
                }
            }

            executeStandardEnter(ic)
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleEnter", e)
        }
    }

    private fun executeStandardEnter(ic: android.view.inputmethod.InputConnection) {
        try {
            val info = currentEditorInfo
            val imeOptions = info?.imeOptions ?: 0
            val action = imeOptions and EditorInfo.IME_MASK_ACTION
            val inputType = info?.inputType ?: 0
            val isMultiLine = (inputType and InputType.TYPE_TEXT_FLAG_MULTI_LINE) != 0
            val noEnterAction = (imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0

            // 1. Explicit custom action from target app (e.g. custom send/search ID)
            if (info?.actionId != null && info.actionId != 0) {
                val handled = ic.performEditorAction(info.actionId)
                if (!handled) {
                    sendKeyEvents(KeyEvent.KEYCODE_ENTER)
                }
            }
            // 2. Explicit standard action (Send, Search, Go, Next, Done) when not restricted by multiline
            else if (!isMultiLine && !noEnterAction && action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                val handled = ic.performEditorAction(action)
                if (!handled) {
                    sendKeyEvents(KeyEvent.KEYCODE_ENTER)
                }
            }
            // 3. For Telegram, WhatsApp, Messenger, multiline chat and text fields:
            // Insert newline directly or trigger send key event
            else {
                if (action == EditorInfo.IME_ACTION_SEND) {
                    val handled = ic.performEditorAction(EditorInfo.IME_ACTION_SEND)
                    if (!handled) {
                        val committed = ic.commitText("\n", 1)
                        if (!committed) {
                            sendKeyEvents(KeyEvent.KEYCODE_ENTER)
                        }
                    }
                } else {
                    // Standard multiline / return: commit \n and fallback to KEYCODE_ENTER
                    val committed = ic.commitText("\n", 1)
                    if (!committed) {
                        sendKeyEvents(KeyEvent.KEYCODE_ENTER)
                    }
                }
            }
            currentWordBuffer.clear()
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in executeStandardEnter", e)
        }
    }

    private fun handleTranslate(sourceLang: String = prefs.translateSourceLang, targetLang: String = prefs.translateTargetLang) {
        try {
            val ic = currentInputConnection ?: return
            val selectedText = ic.getSelectedText(0)?.toString() ?: ""
            val textToTranslate = if (selectedText.isNotBlank()) {
                selectedText
            } else {
                val textBefore = ic.getTextBeforeCursor(1000, 0)?.toString() ?: ""
                val lastLine = textBefore.substringAfterLast('\n').trim()
                if (lastLine.isNotBlank()) lastLine else textBefore.trim()
            }

            if (textToTranslate.isBlank()) {
                // If nothing in editor, try translating latest clip or word buffer
                val word = currentWordBuffer.toString().trim()
                if (word.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val translated = TranslationEngine.translateAsync(word, sourceLang, targetLang)
                        withContext(Dispatchers.Main) {
                            try {
                                val currentIc = currentInputConnection ?: return@withContext
                                currentIc.commitText(translated, 1)
                                currentWordBuffer.clear()
                                updateSuggestions()
                            } catch (e: Throwable) {}
                        }
                    }
                }
                return
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val translated = TranslationEngine.translateAsync(textToTranslate, sourceLang, targetLang)
                withContext(Dispatchers.Main) {
                    try {
                        val currentIc = currentInputConnection ?: return@withContext
                        if (selectedText.isNotBlank()) {
                            currentIc.commitText(translated, 1)
                        } else {
                            val currentTextBefore = currentIc.getTextBeforeCursor(1000, 0)?.toString() ?: ""
                            val currentLastLine = currentTextBefore.substringAfterLast('\n')
                            val deleteLen = if (currentLastLine.isNotBlank()) currentLastLine.length else textToTranslate.length
                            currentIc.deleteSurroundingText(deleteLen, 0)
                            currentIc.commitText(translated, 1)
                        }
                        currentWordBuffer.clear()
                        updateSuggestions()
                    } catch (e: Throwable) {
                        Log.e("KeyboardIME", "Error committing translation", e)
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleTranslate", e)
        }
    }

    private fun sendKeyEvents(keyCode: Int) {
        try {
            val ic = currentInputConnection ?: return
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in sendKeyEvents", e)
        }
    }

    private fun moveCursor(delta: Int) {
        try {
            val ic = currentInputConnection ?: return
            val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
            if (extracted != null) {
                val cur = extracted.selectionStart
                val newPos = (cur + delta).coerceIn(0, extracted.text.length)
                ic.setSelection(newPos, newPos)
            } else {
                val key = if (delta < 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
                val count = kotlin.math.abs(delta).coerceAtMost(10)
                repeat(count) {
                    sendKeyEvents(key)
                }
            }
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in moveCursor", e)
        }
    }

    private fun handleSelectSuggestion(suggestion: String) {
        try {
            val ic = currentInputConnection ?: return
            val currentWord = currentWordBuffer.toString().split("\\s+".toRegex()).lastOrNull() ?: ""
            if (currentWord.isNotEmpty()) {
                ic.deleteSurroundingText(currentWord.length, 0)
            }
            ic.commitText("$suggestion ", 1)
            currentWordBuffer.clear()

            // Learn word frequency
            lifecycleScope.launch {
                try {
                    userWordRepo?.learnWord(suggestion)
                } catch (e: Throwable) {
                    // Ignore DB learning error
                }
            }
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleSelectSuggestion", e)
        }
    }

    private fun performContextAction(actionId: Int) {
        try {
            currentInputConnection?.performContextMenuAction(actionId)
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in performContextAction", e)
        }
    }

    private var suggestionJob: Job? = null

    private fun updateSuggestions() {
        try {
            if (isPasswordField || prefs.isIncognito) {
                _suggestions.value = emptyList()
                return
            }

            val engine = suggestionEngine ?: return
            suggestionJob?.cancel()
            suggestionJob = lifecycleScope.launch(kotlinx.coroutines.Dispatchers.Default) {
                try {
                    val fullText = currentWordBuffer.toString()
                    val words = fullText.split("\\s+".toRegex()).filter { it.isNotBlank() }
                    val currentWord = words.lastOrNull() ?: ""
                    val prevWord = if (words.size >= 2) words[words.size - 2] else null

                    val isArabic = prefs.currentLanguage == "ar"
                    val results = engine.getSuggestions(currentWord, prevWord, isArabic)
                    _suggestions.value = results
                    _currentTypedWord.value = currentWord
                    _isCurrentWordKnown.value = if (currentWord.length >= 2) engine.isWordKnown(currentWord, isArabic) else true
                } catch (e: Throwable) {
                    if (e !is kotlinx.coroutines.CancellationException) {
                        _suggestions.value = emptyList()
                    }
                }
            }
        } catch (e: Throwable) {
            _suggestions.value = emptyList()
        }
    }

    private fun handleApplyAiText(newText: String) {
        try {
            val ic = currentInputConnection ?: return
            val selected = ic.getSelectedText(0)?.toString()
            if (!selected.isNullOrEmpty()) {
                ic.commitText(newText, 1)
            } else {
                val textBefore = ic.getTextBeforeCursor(1000, 0)?.toString() ?: ""
                val lastLine = textBefore.substringAfterLast('\n')
                if (lastLine.isNotEmpty()) {
                    ic.deleteSurroundingText(lastLine.length, 0)
                }
                ic.commitText(newText, 1)
            }
            currentWordBuffer.clear()
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleApplyAiText", e)
        }
    }

    private fun handleAddWordToDictionary(word: String) {
        if (word.isBlank()) return
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                userWordRepo?.learnWord(word.trim())
                _isCurrentWordKnown.value = true
                updateSuggestions()
            } catch (e: Throwable) {
                Log.e("KeyboardIME", "Error in handleAddWordToDictionary", e)
            }
        }
    }

    private fun handleDeleteUserWord(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                userWordRepo?.deleteById(id)
                updateSuggestions()
            } catch (e: Throwable) {
                Log.e("KeyboardIME", "Error in handleDeleteUserWord", e)
            }
        }
    }

    // Voice Typing logic
    private fun startVoiceTyping() {
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(this)) {
                voiceStatusText = "التعرف الصوتي غير متوفر على هذا الجهاز"
                return
            }

            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                val voiceLocale = when (prefs.currentLanguage) {
                    "ar" -> "ar-SA"
                    "en" -> "en-US"
                    "fr" -> "fr-FR"
                    "es" -> "es-ES"
                    "de" -> "de-DE"
                    "ru" -> "ru-RU"
                    "tr" -> "tr-TR"
                    "it" -> "it-IT"
                    "pt" -> "pt-BR"
                    "zh" -> "zh-CN"
                    "ja" -> "ja-JP"
                    "ko" -> "ko-KR"
                    "hi" -> "hi-IN"
                    "ur" -> "ur-PK"
                    "fa" -> "fa-IR"
                    "id" -> "id-ID"
                    else -> prefs.currentLanguage
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, voiceLocale)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isVoiceListening = true
                    voiceStatusText = "جارٍ الاستماع... تكلّم بوضوح"
                    voicePartialText = ""
                }

                override fun onBeginningOfSpeech() {
                    voiceStatusText = "جارٍ التقاط الصوت..."
                }

                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    voiceStatusText = "معالجة الكلام..."
                }

                override fun onError(error: Int) {
                    isVoiceListening = false
                    voiceStatusText = "لم نتمكن من التعرف على الصوت (كود $error)"
                }

                override fun onResults(results: Bundle?) {
                    isVoiceListening = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spokenText = matches[0]
                        handleTextInput("$spokenText ")
                        voiceStatusText = "تم إدخال: $spokenText"
                    } else {
                        voiceStatusText = "لم يتم التقاط نص"
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val partials = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!partials.isNullOrEmpty()) {
                        voicePartialText = partials[0]
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            speechRecognizer?.startListening(intent)
        } catch (e: Throwable) {
            isVoiceListening = false
            voiceStatusText = "تعذر تشغيل التعرف الصوتي: ${e.message}"
        }
    }

    private fun stopVoiceTyping() {
        isVoiceListening = false
        try {
            speechRecognizer?.stopListening()
        } catch (e: Throwable) {}
        voiceStatusText = "تم إيقاف الاستماع"
    }
}
