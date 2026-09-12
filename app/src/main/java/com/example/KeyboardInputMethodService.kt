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
import com.example.ime.ComposeInputMethodService
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen
import com.example.ime.ui.SafeFallbackKeyboardView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewTreeLifecycleOwner(this@KeyboardInputMethodService)
            setViewTreeViewModelStoreOwner(this@KeyboardInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@KeyboardInputMethodService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }

        composeView.setContent {
            val currentThemeName by prefs.themeState.collectAsState()
            val currentLang by prefs.languageState.collectAsState()
            val hapticSetting by prefs.hapticState.collectAsState()
            val keyboardHeight by prefs.heightState.collectAsState()
            val showNumberRow by prefs.numberRowState.collectAsState()
            val showSuggestions by prefs.suggestionsState.collectAsState()
            val arabicNumerals by prefs.arabicNumeralsState.collectAsState()
            val isIncognitoPref by prefs.incognitoState.collectAsState()
            val langManager = (applicationContext as? KeyboardProApp)?.languageManager

            val effectiveIncognito = isIncognitoPref || isPasswordField
            val currentTheme = KeyboardThemes.getTheme(currentThemeName)
            val currentSuggestions by suggestions.collectAsState()
            val clips by clipboardItems.collectAsState()

            KeyboardScreen(
                colorScheme = currentTheme,
                currentLanguage = currentLang,
                imeOptions = currentEditorInfo?.imeOptions ?: EditorInfo.IME_ACTION_DONE,
                isIncognito = effectiveIncognito,
                keyboardHeight = keyboardHeight,
                showNumberRow = showNumberRow,
                hapticEnabled = hapticSetting != "Off",
                soundEnabled = prefs.keySound != "Off",
                oneHandedMode = prefs.oneHandedMode,
                suggestions = currentSuggestions,
                clipboardList = clips,
                isVoiceListening = isVoiceListening,
                voiceStatusText = voiceStatusText,
                voicePartialText = voicePartialText,
                showSuggestions = showSuggestions,
                arabicNumerals = arabicNumerals,
                onTextInput = { text -> handleTextInput(text) },
                onDelete = { handleDelete() },
                onDeleteAll = { handleDeleteAll() },
                onEnter = { handleEnter() },
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
            val info = currentEditorInfo
            val action = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE

            if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                ic.performEditorAction(action)
            } else {
                sendKeyEvents(KeyEvent.KEYCODE_ENTER)
            }
            currentWordBuffer.clear()
            updateSuggestions()
        } catch (e: Throwable) {
            Log.e("KeyboardIME", "Error in handleEnter", e)
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

    private fun updateSuggestions() {
        try {
            if (isPasswordField || prefs.isIncognito) {
                _suggestions.value = emptyList()
                return
            }

            val engine = suggestionEngine ?: return

            lifecycleScope.launch {
                try {
                    val fullText = currentWordBuffer.toString()
                    val words = fullText.split("\\s+".toRegex()).filter { it.isNotBlank() }
                    val currentWord = words.lastOrNull() ?: ""
                    val prevWord = if (words.size >= 2) words[words.size - 2] else null

                    val isArabic = prefs.currentLanguage == "ar"
                    val results = engine.getSuggestions(currentWord, prevWord, isArabic)
                    _suggestions.value = results
                } catch (e: Throwable) {
                    // Fallback to empty on error
                    _suggestions.value = emptyList()
                }
            }
        } catch (e: Throwable) {
            _suggestions.value = emptyList()
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
