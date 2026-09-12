package com.example.ime

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
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
import com.example.KeyboardProApp
import com.example.MainActivity
import com.example.data.local.entity.ClipboardEntity
import com.example.data.repository.ClipboardRepository
import com.example.data.repository.ShortcutRepository
import com.example.data.repository.UserWordRepository
import com.example.engine.SuggestionEngine
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class KeyboardImeService : ComposeInputMethodService() {

    private lateinit var clipboardRepo: ClipboardRepository
    private lateinit var shortcutRepo: ShortcutRepository
    private lateinit var userWordRepo: UserWordRepository
    private lateinit var suggestionEngine: SuggestionEngine

    private val prefs by lazy { KeyboardProApp.instance.preferences }

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
    private var currentWordBuffer = StringBuilder()

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
                                clipboardRepo.insertOrUpdate(text)
                            } catch (e: Exception) {
                                // Ignore DB error
                            }
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            // Android 10+ restricts clipboard access if not active IME; catch SecurityException safely
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            val db = KeyboardProApp.instance.database
            clipboardRepo = ClipboardRepository(db.clipboardDao())
            shortcutRepo = ShortcutRepository(db.shortcutDao())
            userWordRepo = UserWordRepository(db.userWordDao())
            suggestionEngine = SuggestionEngine(userWordRepo, shortcutRepo)

            clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipManager?.addPrimaryClipChangedListener(clipListener)

            lifecycleScope.launch {
                clipboardRepo.allClips.collect { list ->
                    _clipboardItems.value = list
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("KeyboardImeService", "Error in onCreate", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            clipManager?.removePrimaryClipChangedListener(clipListener)
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            // Ignore clean-up error
        }
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }

        composeView.setViewTreeLifecycleOwner(this)
        composeView.setViewTreeViewModelStoreOwner(this)
        composeView.setViewTreeSavedStateRegistryOwner(this)

        composeView.setContent {
            val currentThemeName by prefs.themeState.collectAsState()
            val currentLang by prefs.languageState.collectAsState()
            val hapticSetting by prefs.hapticState.collectAsState()
            val keyboardHeight by prefs.heightState.collectAsState()
            val showNumberRow by prefs.numberRowState.collectAsState()
            val isIncognitoPref by prefs.incognitoState.collectAsState()

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
                onTextInput = { text -> handleTextInput(text) },
                onDelete = { handleDelete() },
                onEnter = { handleEnter() },
                onSpace = { handleSpace() },
                onSwitchLanguage = {
                    val newLang = if (prefs.currentLanguage == "ar") "en" else "ar"
                    prefs.currentLanguage = newLang
                    updateSuggestions()
                },
                onMoveCursor = { delta -> moveCursor(delta) },
                onSelectSuggestion = { suggestion -> handleSelectSuggestion(suggestion) },
                onTogglePinClip = { id, pinned ->
                    lifecycleScope.launch { clipboardRepo.togglePin(id, pinned) }
                },
                onDeleteClip = { id ->
                    lifecycleScope.launch { clipboardRepo.deleteById(id) }
                },
                onClearUnpinnedClips = {
                    lifecycleScope.launch { clipboardRepo.clearUnpinned() }
                },
                onStartVoice = { startVoiceTyping() },
                onStopVoice = { stopVoiceTyping() },
                onSelectAll = { currentInputConnection?.performContextMenuAction(android.R.id.selectAll) },
                onCut = { currentInputConnection?.performContextMenuAction(android.R.id.cut) },
                onCopy = { currentInputConnection?.performContextMenuAction(android.R.id.copy) },
                onPaste = { currentInputConnection?.performContextMenuAction(android.R.id.paste) },
                onUndo = { currentInputConnection?.performContextMenuAction(android.R.id.undo) },
                onRedo = { currentInputConnection?.performContextMenuAction(android.R.id.redo) },
                onOpenSettings = {
                    val intent = Intent(this@KeyboardImeService, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                },
                onToggleOneHanded = { mode ->
                    prefs.oneHandedMode = mode
                }
            )
        }

        return composeView
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentEditorInfo = attribute

        // Detect if the target field is a password field
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
        } catch (e: Exception) {
            android.util.Log.e("KeyboardImeService", "Error in handleTextInput", e)
        }
    }

    private fun handleDelete() {
        try {
            val ic = currentInputConnection ?: return
            val selectedText = ic.getSelectedText(0)
            if (selectedText != null && selectedText.isNotEmpty()) {
                ic.commitText("", 1)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
            if (currentWordBuffer.isNotEmpty()) {
                currentWordBuffer.deleteCharAt(currentWordBuffer.length - 1)
            }
            updateSuggestions()
        } catch (e: Exception) {
            android.util.Log.e("KeyboardImeService", "Error in handleDelete", e)
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
                        val shortcut = shortcutRepo.findByTrigger(word)
                        if (shortcut != null) {
                            ic.deleteSurroundingText(word.length, 0)
                            ic.commitText("${shortcut.replacement} ", 1)
                        } else {
                            ic.commitText(" ", 1)
                            if (!isPasswordField && !prefs.isIncognito) {
                                suggestionEngine.learnWord(word)
                            }
                        }
                    } catch (e: Exception) {
                        ic.commitText(" ", 1)
                    }
                    currentWordBuffer.clear()
                    updateSuggestions()
                }
            } else {
                ic.commitText(" ", 1)
                currentWordBuffer.clear()
                updateSuggestions()
            }
        } catch (e: Exception) {
            android.util.Log.e("KeyboardImeService", "Error in handleSpace", e)
        }
    }

    private fun handleEnter() {
        try {
            val ic = currentInputConnection ?: return
            val info = currentEditorInfo
            val action = (info?.imeOptions ?: 0) and EditorInfo.IME_MASK_ACTION

            if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
                ic.performEditorAction(action)
            } else {
                // Default to newline
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
            currentWordBuffer.clear()
            updateSuggestions()
        } catch (e: Exception) {
            android.util.Log.e("KeyboardImeService", "Error in handleEnter", e)
        }
    }

    private fun handleSelectSuggestion(suggestion: String) {
        try {
            val ic = currentInputConnection ?: return
            val currentLen = currentWordBuffer.length
            if (currentLen > 0) {
                ic.deleteSurroundingText(currentLen, 0)
            }
            ic.commitText("$suggestion ", 1)
            currentWordBuffer.clear()
            updateSuggestions()
        } catch (e: Exception) {
            android.util.Log.e("KeyboardImeService", "Error in handleSelectSuggestion", e)
        }
    }

    private fun moveCursor(delta: Int) {
        try {
            val ic = currentInputConnection ?: return
            val extracted = ic.getExtractedText(ExtractedTextRequest(), 0)
            if (extracted != null) {
                val start = extracted.selectionStart
                val newPos = (start + delta).coerceIn(0, extracted.text.length)
                ic.setSelection(newPos, newPos)
            }
        } catch (e: Exception) {
            android.util.Log.e("KeyboardImeService", "Error in moveCursor", e)
        }
    }

    private fun updateSuggestions() {
        if (isPasswordField || prefs.isIncognito) {
            _suggestions.value = emptyList()
            return
        }

        lifecycleScope.launch {
            try {
                val ic = currentInputConnection
                val textBefore = ic?.getTextBeforeCursor(30, 0)?.toString() ?: ""
                val tokens = textBefore.trim().split("\\s+".toRegex())
                val prevWord = if (tokens.size >= 2) tokens[tokens.size - 2] else null
                val currentWord = currentWordBuffer.toString()

                val isArabic = prefs.currentLanguage == "ar"
                val results = suggestionEngine.getSuggestions(currentWord, prevWord, isArabic)
                _suggestions.value = results
            } catch (e: Exception) {
                // Keep existing suggestions on error
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
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    if (prefs.currentLanguage == "ar") "ar-SA" else Locale.getDefault().toString()
                )
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
        } catch (e: Exception) {
            isVoiceListening = false
            voiceStatusText = "تعذر تشغيل التعرف الصوتي: ${e.message}"
        }
    }

    private fun stopVoiceTyping() {
        isVoiceListening = false
        speechRecognizer?.stopListening()
        voiceStatusText = "تم إيقاف الاستماع"
    }
}
