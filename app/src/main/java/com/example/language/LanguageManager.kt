package com.example.language

import android.content.Context
import com.example.data.pref.KeyboardPreferences
import com.example.language.cache.LanguageCache
import com.example.language.download.LanguageDownloadManager
import com.example.language.layout.KeyboardLayoutData
import com.example.language.layout.KeyboardLayoutManager
import com.example.language.model.LanguageInfo
import com.example.language.model.LayoutFamily
import com.example.language.repository.LanguageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageManager(
    private val context: Context,
    val preferences: KeyboardPreferences
) {
    val cache = LanguageCache(context)
    val downloadManager = LanguageDownloadManager(context, cache)
    val repository = LanguageRepository(cache)
    val packManager = LanguagePackManager(repository, cache, downloadManager)

    private val _currentLanguage = MutableStateFlow(preferences.currentLanguage)
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun switchLanguage(targetLangId: String) {
        val safeLang = if (cache.isInstalled(targetLangId) || targetLangId == "ar") {
            targetLangId
        } else {
            "ar"
        }
        preferences.currentLanguage = safeLang
        _currentLanguage.value = safeLang
    }

    fun cycleNextLanguage(): String {
        val enabled = preferences.enabledLanguages.toList()
        val installedList = enabled.filter { cache.isInstalled(it) || it == "ar" }
        val effectiveList = if (installedList.isEmpty()) listOf("ar", "en") else installedList

        val currentIndex = effectiveList.indexOf(preferences.currentLanguage)
        val nextIndex = if (currentIndex >= 0 && currentIndex < effectiveList.size - 1) {
            currentIndex + 1
        } else {
            0
        }
        val nextLang = effectiveList[nextIndex]
        switchLanguage(nextLang)
        return nextLang
    }

    fun getCurrentLayout(): KeyboardLayoutData {
        val langId = preferences.currentLanguage
        val langInfo = repository.getLanguageById(langId)
        val family = langInfo?.layoutFamily ?: LayoutFamily.QWERTY
        return KeyboardLayoutManager.getLayout(langId, family)
    }

    fun getLanguageInfo(langId: String): LanguageInfo? {
        return repository.getLanguageById(langId)
    }

    fun isArabic(): Boolean {
        return preferences.currentLanguage == "ar"
    }

    fun isRtl(): Boolean {
        return repository.getLanguageById(preferences.currentLanguage)?.isRtl ?: (preferences.currentLanguage == "ar")
    }
}
