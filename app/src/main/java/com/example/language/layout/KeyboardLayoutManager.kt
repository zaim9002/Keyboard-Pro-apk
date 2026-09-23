package com.example.language.layout

import com.example.ime.layout.KeyModel
import com.example.ime.layout.KeyType
import com.example.language.model.LayoutFamily
import com.example.language.pack.ArabicLanguagePack

data class KeyboardLayoutData(
    val id: String,
    val row1: List<KeyModel>,
    val row2: List<KeyModel>,
    val row3: List<KeyModel>,
    val numberRow: List<String> = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
    val spaceLabel: String = "Space",
    val isRtl: Boolean = false,
    val hasShift: Boolean = true
)

object KeyboardLayoutManager {

    fun getLayout(langId: String, layoutFamily: LayoutFamily): KeyboardLayoutData {
        return when {
            langId.startsWith("ar") || layoutFamily == LayoutFamily.ARABIC -> getArabicLayout(langId)
            langId == "fr" || layoutFamily == LayoutFamily.AZERTY -> getAzertyLayout()
            langId == "de" || layoutFamily == LayoutFamily.QWERTZ -> getQwertzLayout()
            langId == "ru" || langId == "uk" || langId == "bg" || langId == "sr" || layoutFamily == LayoutFamily.CYRILLIC -> getCyrillicLayout(langId)
            langId == "el" || layoutFamily == LayoutFamily.GREEK -> getGreekLayout()
            langId == "he" || layoutFamily == LayoutFamily.HEBREW -> getHebrewLayout()
            langId == "fa" || layoutFamily == LayoutFamily.PERSIAN -> getPersianLayout()
            langId == "ur" || layoutFamily == LayoutFamily.URDU -> getUrduLayout()
            langId == "hi" || layoutFamily == LayoutFamily.DEVANAGARI -> getHindiLayout()
            langId == "ko" || layoutFamily == LayoutFamily.KOREAN_HANGUL -> getKoreanLayout()
            langId == "ja" || layoutFamily == LayoutFamily.JAPANESE_KANA -> getJapaneseLayout()
            langId == "es" -> getSpanishLayout()
            langId == "tr" -> getTurkishLayout()
            else -> getStandardQwertyLayout(langId)
        }
    }

    private fun getArabicLayout(langId: String = "ar"): KeyboardLayoutData {
        val label = when (langId) {
            "ar-sa" -> "العربية (السعودية)"
            "ar-eg" -> "العربية (مصر)"
            "ar-sy" -> "العربية (الشام)"
            "ar-ae" -> "العربية (الخليج)"
            "ar-ma" -> "العربية (المغرب)"
            "ar-dz" -> "العربية (الجزائر)"
            "ar-tn" -> "العربية (تونس)"
            "ar-iq" -> "العربية (العراق)"
            "ar-jo" -> "العربية (الأردن)"
            "ar-lb" -> "العربية (لبنان)"
            "ar-kw" -> "العربية (الكويت)"
            "ar-ps" -> "العربية (فلسطين)"
            "ar-sd" -> "العربية (السودان)"
            "ar-ye" -> "العربية (اليمن)"
            else -> "العربية"
        }
        return KeyboardLayoutData(
            id = langId,
            row1 = ArabicLanguagePack.row1,
            row2 = ArabicLanguagePack.row2,
            row3 = ArabicLanguagePack.row3,
            numberRow = ArabicLanguagePack.arabicNumerals,
            spaceLabel = label,
            isRtl = true,
            hasShift = false // Arabic has Tashkeel toggle
        )
    }

    private fun getStandardQwertyLayout(langId: String): KeyboardLayoutData {
        val label = when (langId) {
            "en" -> "English"
            "en-us" -> "English (US)"
            "en-gb" -> "English (UK)"
            "en-ca" -> "English (Canada)"
            "en-au" -> "English (Australia)"
            "id" -> "Indonesia"
            "nl" -> "Nederlands"
            "it" -> "Italiano"
            "pt" -> "Português"
            else -> "Space"
        }
        val r1 = listOf(
            KeyModel("q", secondaryText = "1"),
            KeyModel("w", secondaryText = "2"),
            KeyModel("e", secondaryText = "3", popupOptions = listOf("é", "è", "ê", "ë")),
            KeyModel("r", secondaryText = "4"),
            KeyModel("t", secondaryText = "5"),
            KeyModel("y", secondaryText = "6"),
            KeyModel("u", secondaryText = "7", popupOptions = listOf("ú", "ù", "û", "ü")),
            KeyModel("i", secondaryText = "8", popupOptions = listOf("í", "ì", "î", "ï")),
            KeyModel("o", secondaryText = "9", popupOptions = listOf("ó", "ò", "ô", "ö")),
            KeyModel("p", secondaryText = "0")
        )
        val r2 = listOf(
            KeyModel("a", popupOptions = listOf("á", "à", "â", "ä", "ã")),
            KeyModel("s", popupOptions = listOf("ß", "$")),
            KeyModel("d"), KeyModel("f"), KeyModel("g"), KeyModel("h"),
            KeyModel("j"), KeyModel("k"), KeyModel("l")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("z"), KeyModel("x"),
            KeyModel("c", popupOptions = listOf("ç")),
            KeyModel("v"), KeyModel("b"),
            KeyModel("n", popupOptions = listOf("ñ")),
            KeyModel("m"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("en", r1, r2, r3, spaceLabel = label)
    }

    private fun getSpanishLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("q", secondaryText = "1"), KeyModel("w", secondaryText = "2"),
            KeyModel("e", secondaryText = "3", popupOptions = listOf("é")),
            KeyModel("r", secondaryText = "4"), KeyModel("t", secondaryText = "5"),
            KeyModel("y", secondaryText = "6"),
            KeyModel("u", secondaryText = "7", popupOptions = listOf("ú", "ü")),
            KeyModel("i", secondaryText = "8", popupOptions = listOf("í")),
            KeyModel("o", secondaryText = "9", popupOptions = listOf("ó")),
            KeyModel("p", secondaryText = "0")
        )
        val r2 = listOf(
            KeyModel("a", popupOptions = listOf("á")), KeyModel("s"), KeyModel("d"),
            KeyModel("f"), KeyModel("g"), KeyModel("h"), KeyModel("j"), KeyModel("k"),
            KeyModel("l"), KeyModel("ñ")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("z"), KeyModel("x"), KeyModel("c"), KeyModel("v"),
            KeyModel("b"), KeyModel("n"), KeyModel("m"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("es", r1, r2, r3, spaceLabel = "Español")
    }

    private fun getAzertyLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("a", secondaryText = "1", popupOptions = listOf("à", "â")),
            KeyModel("z", secondaryText = "2"),
            KeyModel("e", secondaryText = "3", popupOptions = listOf("é", "è", "ê", "ë", "€")),
            KeyModel("r", secondaryText = "4"), KeyModel("t", secondaryText = "5"),
            KeyModel("y", secondaryText = "6"),
            KeyModel("u", secondaryText = "7", popupOptions = listOf("ù", "û", "ü")),
            KeyModel("i", secondaryText = "8", popupOptions = listOf("î", "ï")),
            KeyModel("o", secondaryText = "9", popupOptions = listOf("ô", "œ")),
            KeyModel("p", secondaryText = "0")
        )
        val r2 = listOf(
            KeyModel("q"), KeyModel("s"), KeyModel("d"), KeyModel("f"),
            KeyModel("g"), KeyModel("h"), KeyModel("j"), KeyModel("k"), KeyModel("l"),
            KeyModel("m")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("w"), KeyModel("x"),
            KeyModel("c", popupOptions = listOf("ç")),
            KeyModel("v"), KeyModel("b"), KeyModel("n"),
            KeyModel("'", popupOptions = listOf("’")),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("fr", r1, r2, r3, spaceLabel = "Français")
    }

    private fun getQwertzLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("q", secondaryText = "1"), KeyModel("w", secondaryText = "2"),
            KeyModel("e", secondaryText = "3", popupOptions = listOf("€")),
            KeyModel("r", secondaryText = "4"), KeyModel("t", secondaryText = "5"),
            KeyModel("z", secondaryText = "6"), KeyModel("u", secondaryText = "7"),
            KeyModel("i", secondaryText = "8"), KeyModel("o", secondaryText = "9"),
            KeyModel("p", secondaryText = "0"), KeyModel("ü")
        )
        val r2 = listOf(
            KeyModel("a", popupOptions = listOf("ä")), KeyModel("s", popupOptions = listOf("ß")),
            KeyModel("d"), KeyModel("f"), KeyModel("g"), KeyModel("h"), KeyModel("j"),
            KeyModel("k"), KeyModel("l"), KeyModel("ö"), KeyModel("ä")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("y"), KeyModel("x"), KeyModel("c"), KeyModel("v"),
            KeyModel("b"), KeyModel("n"), KeyModel("m"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("de", r1, r2, r3, spaceLabel = "Deutsch")
    }

    private fun getCyrillicLayout(langId: String): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("й"), KeyModel("ц"), KeyModel("у"), KeyModel("к"),
            KeyModel("е"), KeyModel("н"), KeyModel("г"), KeyModel("ш"),
            KeyModel("щ"), KeyModel("з"), KeyModel("х"), KeyModel("ъ")
        )
        val r2 = listOf(
            KeyModel("ф"), KeyModel("ы"), KeyModel("в"), KeyModel("а"),
            KeyModel("п"), KeyModel("р"), KeyModel("о"), KeyModel("л"),
            KeyModel("д"), KeyModel("ж"), KeyModel("э")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("я"), KeyModel("ч"), KeyModel("с"), KeyModel("м"),
            KeyModel("и"), KeyModel("т"), KeyModel("ь"), KeyModel("б"), KeyModel("ю"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        val spaceLabel = if (langId == "uk") "Українська" else "Русский"
        return KeyboardLayoutData(langId, r1, r2, r3, spaceLabel = spaceLabel)
    }

    private fun getGreekLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel(";"), KeyModel("ς"), KeyModel("ε"), KeyModel("ρ"), KeyModel("τ"),
            KeyModel("υ"), KeyModel("θ"), KeyModel("ι"), KeyModel("ο"), KeyModel("π")
        )
        val r2 = listOf(
            KeyModel("α"), KeyModel("σ"), KeyModel("δ"), KeyModel("φ"), KeyModel("γ"),
            KeyModel("η"), KeyModel("ξ"), KeyModel("κ"), KeyModel("λ")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("ζ"), KeyModel("χ"), KeyModel("ψ"), KeyModel("ω"), KeyModel("β"),
            KeyModel("ν"), KeyModel("μ"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("el", r1, r2, r3, spaceLabel = "Ελληνικά")
    }

    private fun getHebrewLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("ק"), KeyModel("ר"), KeyModel("א"), KeyModel("ט"), KeyModel("ו"),
            KeyModel("ן"), KeyModel("ם"), KeyModel("פ")
        )
        val r2 = listOf(
            KeyModel("ש"), KeyModel("ד"), KeyModel("ג"), KeyModel("כ"), KeyModel("ע"),
            KeyModel("י"), KeyModel("ח"), KeyModel("ל"), KeyModel("ך"), KeyModel("ף")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("ז"), KeyModel("ס"), KeyModel("ב"), KeyModel("ה"), KeyModel("נ"),
            KeyModel("מ"), KeyModel("צ"), KeyModel("ת"), KeyModel("ץ"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("he", r1, r2, r3, spaceLabel = "עברית", isRtl = true, hasShift = false)
    }

    private fun getPersianLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("ض"), KeyModel("ص"), KeyModel("ث"), KeyModel("ق"), KeyModel("ف"),
            KeyModel("غ"), KeyModel("ع"), KeyModel("ه"), KeyModel("خ"), KeyModel("ح"),
            KeyModel("ج"), KeyModel("چ")
        )
        val r2 = listOf(
            KeyModel("ش"), KeyModel("س"), KeyModel("ی"), KeyModel("ب"), KeyModel("ل"),
            KeyModel("ا"), KeyModel("ت"), KeyModel("ن"), KeyModel("م"), KeyModel("ک"),
            KeyModel("گ")
        )
        val r3 = listOf(
            KeyModel("ظ"), KeyModel("ط"), KeyModel("ز"), KeyModel("ر"), KeyModel("ذ"),
            KeyModel("د"), KeyModel("پ"), KeyModel("و"), KeyModel("ژ"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("fa", r1, r2, r3, spaceLabel = "فارسی", isRtl = true, hasShift = false)
    }

    private fun getUrduLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("ٹ"), KeyModel("ڈ"), KeyModel("ڑ"), KeyModel("ق"), KeyModel("ف"),
            KeyModel("غ"), KeyModel("ع"), KeyModel("ہ"), KeyModel("خ"), KeyModel("ح"),
            KeyModel("ج"), KeyModel("چ")
        )
        val r2 = listOf(
            KeyModel("ش"), KeyModel("س"), KeyModel("ی"), KeyModel("ب"), KeyModel("ل"),
            KeyModel("ا"), KeyModel("ت"), KeyModel("ن"), KeyModel("م"), KeyModel("ک"),
            KeyModel("گ")
        )
        val r3 = listOf(
            KeyModel("ظ"), KeyModel("ط"), KeyModel("ز"), KeyModel("ر"), KeyModel("ذ"),
            KeyModel("د"), KeyModel("و"), KeyModel("ے"), KeyModel("ں"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("ur", r1, r2, r3, spaceLabel = "اردو", isRtl = true, hasShift = false)
    }

    private fun getHindiLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("क"), KeyModel("ख"), KeyModel("ग"), KeyModel("घ"), KeyModel("ङ"),
            KeyModel("च"), KeyModel("छ"), KeyModel("ज"), KeyModel("झ"), KeyModel("ञ")
        )
        val r2 = listOf(
            KeyModel("ट"), KeyModel("ठ"), KeyModel("ड"), KeyModel("ढ"), KeyModel("ण"),
            KeyModel("त"), KeyModel("थ"), KeyModel("द"), KeyModel("ध"), KeyModel("न")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("प"), KeyModel("फ"), KeyModel("ब"), KeyModel("भ"), KeyModel("म"),
            KeyModel("य"), KeyModel("र"), KeyModel("ल"), KeyModel("व"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("hi", r1, r2, r3, spaceLabel = "हिन्दी")
    }

    private fun getKoreanLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("ㅂ"), KeyModel("ㅈ"), KeyModel("ㄷ"), KeyModel("ㄱ"), KeyModel("ㅅ"),
            KeyModel("ㅛ"), KeyModel("ㅕ"), KeyModel("ㅑ"), KeyModel("ㅐ"), KeyModel("ㅔ")
        )
        val r2 = listOf(
            KeyModel("ㅁ"), KeyModel("ㄴ"), KeyModel("ㅇ"), KeyModel("ㄹ"), KeyModel("ㅎ"),
            KeyModel("ㅗ"), KeyModel("ㅓ"), KeyModel("ㅏ"), KeyModel("ㅣ")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("ㅋ"), KeyModel("ㅌ"), KeyModel("ㅊ"), KeyModel("ㅍ"), KeyModel("ㅠ"),
            KeyModel("ㅜ"), KeyModel("ㅡ"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("ko", r1, r2, r3, spaceLabel = "한국어")
    }

    private fun getJapaneseLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("あ"), KeyModel("か"), KeyModel("さ"), KeyModel("た"), KeyModel("な"),
            KeyModel("は"), KeyModel("ま"), KeyModel("や"), KeyModel("ら"), KeyModel("わ")
        )
        val r2 = listOf(
            KeyModel("い"), KeyModel("き"), KeyModel("し"), KeyModel("ち"), KeyModel("に"),
            KeyModel("ひ"), KeyModel("み"), KeyModel("り"), KeyModel("を")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("う"), KeyModel("く"), KeyModel("す"), KeyModel("つ"), KeyModel("ぬ"),
            KeyModel("ふ"), KeyModel("む"), KeyModel("ゆ"), KeyModel("ん"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("ja", r1, r2, r3, spaceLabel = "日本語")
    }

    private fun getTurkishLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("q"), KeyModel("w"), KeyModel("e"), KeyModel("r"), KeyModel("t"),
            KeyModel("y"), KeyModel("u"), KeyModel("ı"), KeyModel("o"), KeyModel("p"),
            KeyModel("ğ"), KeyModel("ü")
        )
        val r2 = listOf(
            KeyModel("a"), KeyModel("s"), KeyModel("d"), KeyModel("f"), KeyModel("g"),
            KeyModel("h"), KeyModel("j"), KeyModel("k"), KeyModel("l"), KeyModel("ş"),
            KeyModel("i")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("z"), KeyModel("x"), KeyModel("c"), KeyModel("v"), KeyModel("b"),
            KeyModel("n"), KeyModel("m"), KeyModel("ö"), KeyModel("ç"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("tr", r1, r2, r3, spaceLabel = "Türkçe")
    }
}
