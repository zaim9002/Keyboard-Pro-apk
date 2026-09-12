package com.example.data.pref

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeState = MutableStateFlow(theme)
    val themeState: StateFlow<String> = _themeState.asStateFlow()

    private val _languageState = MutableStateFlow(currentLanguage)
    val languageState: StateFlow<String> = _languageState.asStateFlow()

    private val _hapticState = MutableStateFlow(hapticFeedback)
    val hapticState: StateFlow<String> = _hapticState.asStateFlow()

    private val _heightState = MutableStateFlow(keyboardHeight)
    val heightState: StateFlow<String> = _heightState.asStateFlow()

    private val _numberRowState = MutableStateFlow(showNumberRow)
    val numberRowState: StateFlow<Boolean> = _numberRowState.asStateFlow()

    private val _incognitoState = MutableStateFlow(isIncognito)
    val incognitoState: StateFlow<Boolean> = _incognitoState.asStateFlow()

    var theme: String
        get() = prefs.getString(KEY_THEME, "Midnight") ?: "Midnight"
        set(value) {
            prefs.edit().putString(KEY_THEME, value).apply()
            _themeState.value = value
        }

    var currentLanguage: String
        get() = prefs.getString(KEY_CURRENT_LANG, "ar") ?: "ar"
        set(value) {
            prefs.edit().putString(KEY_CURRENT_LANG, value).apply()
            _languageState.value = value
        }

    var enabledLanguages: Set<String>
        get() = prefs.getStringSet(KEY_ENABLED_LANGS, setOf("ar", "en")) ?: setOf("ar", "en")
        set(value) {
            prefs.edit().putStringSet(KEY_ENABLED_LANGS, value).apply()
        }

    var keyboardHeight: String
        get() = prefs.getString(KEY_HEIGHT, "Medium") ?: "Medium"
        set(value) {
            prefs.edit().putString(KEY_HEIGHT, value).apply()
            _heightState.value = value
        }

    var fontSize: String
        get() = prefs.getString(KEY_FONT_SIZE, "Medium") ?: "Medium"
        set(value) = prefs.edit().putString(KEY_FONT_SIZE, value).apply()

    var hapticFeedback: String
        get() = prefs.getString(KEY_HAPTIC, "Light") ?: "Light"
        set(value) {
            prefs.edit().putString(KEY_HAPTIC, value).apply()
            _hapticState.value = value
        }

    var keySound: String
        get() = prefs.getString(KEY_SOUND, "Off") ?: "Off"
        set(value) = prefs.edit().putString(KEY_SOUND, value).apply()

    var showNumberRow: Boolean
        get() = prefs.getBoolean(KEY_NUMBER_ROW, true)
        set(value) {
            prefs.edit().putBoolean(KEY_NUMBER_ROW, value).apply()
            _numberRowState.value = value
        }

    var doubleSpacePeriod: Boolean
        get() = prefs.getBoolean(KEY_DOUBLE_SPACE, true)
        set(value) = prefs.edit().putBoolean(KEY_DOUBLE_SPACE, value).apply()

    var autoCapitalization: Boolean
        get() = prefs.getBoolean(KEY_AUTO_CAPS, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_CAPS, value).apply()

    var autoCorrectStrength: String
        get() = prefs.getString(KEY_AUTOCORRECT, "Medium") ?: "Medium"
        set(value) = prefs.edit().putString(KEY_AUTOCORRECT, value).apply()

    var spacebarSwipe: String
        get() = prefs.getString(KEY_SPACE_SWIPE, "SWITCH_LANG") ?: "SWITCH_LANG"
        set(value) = prefs.edit().putString(KEY_SPACE_SWIPE, value).apply()

    var isIncognito: Boolean
        get() = prefs.getBoolean(KEY_INCOGNITO, false)
        set(value) {
            prefs.edit().putBoolean(KEY_INCOGNITO, value).apply()
            _incognitoState.value = value
        }

    var isGamingMode: Boolean
        get() = prefs.getBoolean(KEY_GAMING, false)
        set(value) = prefs.edit().putBoolean(KEY_GAMING, value).apply()

    var oneHandedMode: String
        get() = prefs.getString(KEY_ONE_HANDED, "OFF") ?: "OFF"
        set(value) = prefs.edit().putString(KEY_ONE_HANDED, value).apply()

    var wordsTypedCount: Long
        get() = prefs.getLong(KEY_WORD_COUNT, 0L)
        set(value) = prefs.edit().putLong(KEY_WORD_COUNT, value).apply()

    fun incrementWordCount() {
        if (!isIncognito) {
            wordsTypedCount = wordsTypedCount + 1
        }
    }

    companion object {
        private const val PREFS_NAME = "keyboard_pro_prefs"
        private const val KEY_THEME = "theme"
        private const val KEY_CURRENT_LANG = "current_lang"
        private const val KEY_ENABLED_LANGS = "enabled_langs"
        private const val KEY_HEIGHT = "keyboard_height"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_HAPTIC = "haptic_feedback"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_NUMBER_ROW = "show_number_row"
        private const val KEY_DOUBLE_SPACE = "double_space_period"
        private const val KEY_AUTO_CAPS = "auto_capitalization"
        private const val KEY_AUTOCORRECT = "autocorrect_strength"
        private const val KEY_SPACE_SWIPE = "spacebar_swipe"
        private const val KEY_INCOGNITO = "is_incognito"
        private const val KEY_GAMING = "is_gaming_mode"
        private const val KEY_ONE_HANDED = "one_handed_mode"
        private const val KEY_WORD_COUNT = "words_typed_count"
    }
}
