package com.example.language.repository

import com.example.language.cache.LanguageCache
import com.example.language.model.LanguageInfo
import com.example.language.model.LanguagePackStatus
import com.example.language.model.LayoutFamily
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LanguageRepository(private val cache: LanguageCache) {

    // Master catalog of all supported languages
    private val masterCatalog = listOf(
        LanguageInfo(
            id = "ar",
            nameArabic = "العربية",
            nativeName = "العربية",
            flag = "🇸🇦",
            sizeMb = 2.4,
            isBuiltIn = true,
            layoutFamily = LayoutFamily.ARABIC,
            isRtl = true
        ),
        LanguageInfo(
            id = "en",
            nameArabic = "الإنجليزية",
            nativeName = "English",
            flag = "🇺🇸",
            sizeMb = 1.8,
            isBuiltIn = true,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "fr",
            nameArabic = "الفرنسية",
            nativeName = "Français",
            flag = "🇫🇷",
            sizeMb = 1.5,
            layoutFamily = LayoutFamily.AZERTY
        ),
        LanguageInfo(
            id = "es",
            nameArabic = "الإسبانية",
            nativeName = "Español",
            flag = "🇪🇸",
            sizeMb = 1.6,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "de",
            nameArabic = "الألمانية",
            nativeName = "Deutsch",
            flag = "🇩🇪",
            sizeMb = 1.7,
            layoutFamily = LayoutFamily.QWERTZ
        ),
        LanguageInfo(
            id = "it",
            nameArabic = "الإيطالية",
            nativeName = "Italiano",
            flag = "🇮🇹",
            sizeMb = 1.3,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "pt",
            nameArabic = "البرتغالية",
            nativeName = "Português",
            flag = "🇵🇹",
            sizeMb = 1.4,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "tr",
            nameArabic = "التركية",
            nativeName = "Türkçe",
            flag = "🇹🇷",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "ru",
            nameArabic = "الروسية",
            nativeName = "Русский",
            flag = "🇷🇺",
            sizeMb = 2.1,
            layoutFamily = LayoutFamily.CYRILLIC
        ),
        LanguageInfo(
            id = "zh",
            nameArabic = "الصينية",
            nativeName = "中文",
            flag = "🇨🇳",
            sizeMb = 3.5,
            layoutFamily = LayoutFamily.CHINESE_PINYIN
        ),
        LanguageInfo(
            id = "ja",
            nameArabic = "اليابانية",
            nativeName = "日本語",
            flag = "🇯🇵",
            sizeMb = 3.2,
            layoutFamily = LayoutFamily.JAPANESE_KANA
        ),
        LanguageInfo(
            id = "ko",
            nameArabic = "الكورية",
            nativeName = "한국어",
            flag = "🇰🇷",
            sizeMb = 2.8,
            layoutFamily = LayoutFamily.KOREAN_HANGUL
        ),
        LanguageInfo(
            id = "hi",
            nameArabic = "الهندية",
            nativeName = "हिन्दी",
            flag = "🇮🇳",
            sizeMb = 2.0,
            layoutFamily = LayoutFamily.DEVANAGARI
        ),
        LanguageInfo(
            id = "ur",
            nameArabic = "الأردية",
            nativeName = "اردو",
            flag = "🇵🇰",
            sizeMb = 1.9,
            layoutFamily = LayoutFamily.URDU,
            isRtl = true
        ),
        LanguageInfo(
            id = "fa",
            nameArabic = "الفارسية",
            nativeName = "فارسی",
            flag = "🇮🇷",
            sizeMb = 1.7,
            layoutFamily = LayoutFamily.PERSIAN,
            isRtl = true
        ),
        LanguageInfo(
            id = "bn",
            nameArabic = "البنغالية",
            nativeName = "বাংলা",
            flag = "🇧🇩",
            sizeMb = 1.8,
            layoutFamily = LayoutFamily.BENGALI
        ),
        LanguageInfo(
            id = "ta",
            nameArabic = "التاميلية",
            nativeName = "தமிழ்",
            flag = "🇮🇳",
            sizeMb = 1.7,
            layoutFamily = LayoutFamily.TAMIL
        ),
        LanguageInfo(
            id = "te",
            nameArabic = "التيلوغو",
            nativeName = "తెలుగు",
            flag = "🇮🇳",
            sizeMb = 1.6,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "ml",
            nameArabic = "المالايالامية",
            nativeName = "മലയാളം",
            flag = "🇮🇳",
            sizeMb = 1.6,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "th",
            nameArabic = "التايلاندية",
            nativeName = "ไทย",
            flag = "🇹🇭",
            sizeMb = 1.9,
            layoutFamily = LayoutFamily.THAI
        ),
        LanguageInfo(
            id = "id",
            nameArabic = "الإندونيسية",
            nativeName = "Bahasa Indonesia",
            flag = "🇮🇩",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "vi",
            nameArabic = "الفيتنامية",
            nativeName = "Tiếng Việt",
            flag = "🇻🇳",
            sizeMb = 1.5,
            layoutFamily = LayoutFamily.VIETNAMESE
        ),
        LanguageInfo(
            id = "nl",
            nameArabic = "الهولندية",
            nativeName = "Nederlands",
            flag = "🇳🇱",
            sizeMb = 1.3,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "pl",
            nameArabic = "البولندية",
            nativeName = "Polski",
            flag = "🇵🇱",
            sizeMb = 1.4,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "uk",
            nameArabic = "الأوكرانية",
            nativeName = "Українська",
            flag = "🇺🇦",
            sizeMb = 1.7,
            layoutFamily = LayoutFamily.CYRILLIC
        ),
        LanguageInfo(
            id = "el",
            nameArabic = "اليونانية",
            nativeName = "Ελληνικά",
            flag = "🇬🇷",
            sizeMb = 1.5,
            layoutFamily = LayoutFamily.GREEK
        ),
        LanguageInfo(
            id = "he",
            nameArabic = "العبرية",
            nativeName = "עברית",
            flag = "🇮🇱",
            sizeMb = 1.4,
            layoutFamily = LayoutFamily.HEBREW,
            isRtl = true
        ),
        LanguageInfo(
            id = "ro",
            nameArabic = "الرومانية",
            nativeName = "Română",
            flag = "🇷🇴",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "sv",
            nameArabic = "السويدية",
            nativeName = "Svenska",
            flag = "🇸🇪",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "no",
            nameArabic = "النرويجية",
            nativeName = "Norsk",
            flag = "🇳🇴",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "da",
            nameArabic = "الدنماركية",
            nativeName = "Dansk",
            flag = "🇩🇰",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "fi",
            nameArabic = "الفنلندية",
            nativeName = "Suomi",
            flag = "🇫🇮",
            sizeMb = 1.3,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "cs",
            nameArabic = "التشيكية",
            nativeName = "Čeština",
            flag = "🇨🇿",
            sizeMb = 1.3,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "hu",
            nameArabic = "المجرية",
            nativeName = "Magyar",
            flag = "🇭🇺",
            sizeMb = 1.4,
            layoutFamily = LayoutFamily.QWERTZ
        ),
        LanguageInfo(
            id = "sk",
            nameArabic = "السلوفاكية",
            nativeName = "Slovenčina",
            flag = "🇸🇰",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "bg",
            nameArabic = "البلغارية",
            nativeName = "Български",
            flag = "🇧🇬",
            sizeMb = 1.4,
            layoutFamily = LayoutFamily.CYRILLIC
        ),
        LanguageInfo(
            id = "sr",
            nameArabic = "الصربية",
            nativeName = "Српски",
            flag = "🇷🇸",
            sizeMb = 1.3,
            layoutFamily = LayoutFamily.CYRILLIC
        ),
        LanguageInfo(
            id = "hr",
            nameArabic = "الكرواتية",
            nativeName = "Hrvatski",
            flag = "🇭🇷",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "sl",
            nameArabic = "السلوفينية",
            nativeName = "Slovenščina",
            flag = "🇸🇮",
            sizeMb = 1.1,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "ms",
            nameArabic = "الملايو",
            nativeName = "Bahasa Melayu",
            flag = "🇲🇾",
            sizeMb = 1.1,
            layoutFamily = LayoutFamily.QWERTY
        ),
        LanguageInfo(
            id = "fil",
            nameArabic = "الفلبينية",
            nativeName = "Filipino",
            flag = "🇵🇭",
            sizeMb = 1.2,
            layoutFamily = LayoutFamily.QWERTY
        )
    )

    fun getAllLanguages(): Flow<List<LanguageInfo>> {
        return cache.installedLanguages.map { installedSet ->
            masterCatalog.map { lang ->
                val isInstalled = installedSet.contains(lang.id) || lang.id == "ar"
                val installedVer = cache.getInstalledVersion(lang.id)
                val status = when {
                    isInstalled && lang.version > installedVer && installedVer > 0 -> LanguagePackStatus.UPDATE_AVAILABLE
                    isInstalled -> LanguagePackStatus.INSTALLED
                    else -> LanguagePackStatus.NOT_INSTALLED
                }
                lang.copy(status = status)
            }
        }
    }

    fun getLanguageById(id: String): LanguageInfo? {
        val lang = masterCatalog.find { it.id == id } ?: return null
        val isInstalled = cache.isInstalled(id)
        val status = if (isInstalled) LanguagePackStatus.INSTALLED else LanguagePackStatus.NOT_INSTALLED
        return lang.copy(status = status)
    }

    fun getInstalledLanguagesSync(): List<LanguageInfo> {
        val installedSet = cache.installedLanguages.value
        return masterCatalog.filter { installedSet.contains(it.id) || it.id == "ar" }
    }

    fun getEnabledLanguages(enabledIds: Set<String>): List<LanguageInfo> {
        val set = if (enabledIds.isEmpty()) setOf("ar", "en") else enabledIds
        return masterCatalog.filter { set.contains(it.id) || it.id == "ar" }
    }
}
