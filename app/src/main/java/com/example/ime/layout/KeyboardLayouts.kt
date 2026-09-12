package com.example.ime.layout

data class KeyModel(
    val primaryText: String,
    val secondaryText: String? = null,
    val type: KeyType = KeyType.CHARACTER,
    val popupOptions: List<String> = emptyList(),
    val weight: Float = 1f
)

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    SWITCH_MODE,     // 123, ABC, #+=
    SWITCH_LANGUAGE, // 🌐
    TASHKEEL,        // َ ً ُ ...
    ACTION,
    SETTINGS
}

object KeyboardLayouts {

    val arabicTashkeel = listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ", "ٰ", "ـ")

    val arabicQuickRow = listOf(
        KeyModel("👑"),
        KeyModel("💋"),
        KeyModel("ة", popupOptions = listOf("ه")),
        KeyModel("ؤ", popupOptions = listOf("و")),
        KeyModel("ء", popupOptions = listOf("ئ", "ؤ", "أ", "إ", "آ")),
        KeyModel("ـ"),
        KeyModel("ئ", popupOptions = listOf("ي", "ى")),
        KeyModel("ى", popupOptions = listOf("ي")),
        KeyModel("ڷ", popupOptions = listOf("ل", "لا")),
        KeyModel("😂"),
        KeyModel("خاص")
    )

    val arabicRow1 = listOf(
        KeyModel("ض", secondaryText = "¨", popupOptions = listOf("١", "1", "ص")),
        KeyModel("ص", secondaryText = "¨", popupOptions = listOf("٢", "2", "ض")),
        KeyModel("ق", secondaryText = "¨", popupOptions = listOf("٣", "3")),
        KeyModel("ف", secondaryText = "¨", popupOptions = listOf("٤", "4", "ڤ")),
        KeyModel("غ", secondaryText = "¨", popupOptions = listOf("٥", "5")),
        KeyModel("ع", secondaryText = "¨", popupOptions = listOf("٦", "6", "غ")),
        KeyModel("ه", secondaryText = "¨", popupOptions = listOf("٧", "7", "ة", "هـ")),
        KeyModel("خ", secondaryText = "¨", popupOptions = listOf("٨", "8")),
        KeyModel("ح", secondaryText = "¨", popupOptions = listOf("٩", "9", "خ", "ج")),
        KeyModel("ج", secondaryText = "¨", popupOptions = listOf("٠", "0", "چ", "ح"))
    )

    val arabicRow2 = listOf(
        KeyModel("ش", secondaryText = "¨", popupOptions = listOf("س")),
        KeyModel("س", secondaryText = "¨", popupOptions = listOf("ش")),
        KeyModel("ي", secondaryText = "¨", popupOptions = listOf("ى", "ئ")),
        KeyModel("ب", secondaryText = "¨", popupOptions = listOf("پ", "ـ")),
        KeyModel("ل", secondaryText = "¨", popupOptions = listOf("لا", "لأ", "لإ", "لآ")),
        KeyModel("ا", secondaryText = "¨", popupOptions = listOf("أ", "إ", "آ", "ء", "ٱ")),
        KeyModel("ت", secondaryText = "¨", popupOptions = listOf("ة", "ث")),
        KeyModel("ن", secondaryText = "¨"),
        KeyModel("م", secondaryText = "¨"),
        KeyModel("ك", secondaryText = "¨", popupOptions = listOf("گ", "ڨ"))
    )

    val arabicRow3 = listOf(
        KeyModel("ظ", secondaryText = "¨", popupOptions = listOf("ط")),
        KeyModel("ط", secondaryText = "¨", popupOptions = listOf("ظ")),
        KeyModel("ذ", secondaryText = "¨", popupOptions = listOf("د")),
        KeyModel("د", secondaryText = "¨", popupOptions = listOf("ذ")),
        KeyModel("ز", secondaryText = "¨", popupOptions = listOf("ژ", "ر")),
        KeyModel("ر", secondaryText = "¨", popupOptions = listOf("ز", "ژ")),
        KeyModel("و", secondaryText = "¨", popupOptions = listOf("ؤ")),
        KeyModel("ة", secondaryText = "¨", popupOptions = listOf("ه")),
        KeyModel("ث", secondaryText = "¨", popupOptions = listOf("ت"))
    )

    val englishRow1 = listOf(
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

    val englishRow2 = listOf(
        KeyModel("a", popupOptions = listOf("á", "à", "â", "ä", "ã")),
        KeyModel("s", popupOptions = listOf("ß", "$")),
        KeyModel("d"),
        KeyModel("f"),
        KeyModel("g"),
        KeyModel("h"),
        KeyModel("j"),
        KeyModel("k"),
        KeyModel("l")
    )

    val englishRow3 = listOf(
        KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
        KeyModel("z"),
        KeyModel("x"),
        KeyModel("c", popupOptions = listOf("ç")),
        KeyModel("v"),
        KeyModel("b"),
        KeyModel("n", popupOptions = listOf("ñ")),
        KeyModel("m"),
        KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
    )

    val symbols1Row1 = listOf(
        KeyModel("1"), KeyModel("2"), KeyModel("3"), KeyModel("4"), KeyModel("5"),
        KeyModel("6"), KeyModel("7"), KeyModel("8"), KeyModel("9"), KeyModel("0")
    )

    val symbols1Row2 = listOf(
        KeyModel("@"), KeyModel("#"), KeyModel("$"), KeyModel("%"), KeyModel("&"),
        KeyModel("-"), KeyModel("+"), KeyModel("("), KeyModel(")"), KeyModel("/")
    )

    val symbols1Row3 = listOf(
        KeyModel("#+=", type = KeyType.SWITCH_MODE, weight = 1.3f),
        KeyModel("*"), KeyModel("\""), KeyModel("'"), KeyModel(":"), KeyModel(";"),
        KeyModel("!"), KeyModel("?"),
        KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
    )

    val symbols2Row1 = listOf(
        KeyModel("~"), KeyModel("`"), KeyModel("|"), KeyModel("•"), KeyModel("√"),
        KeyModel("π"), KeyModel("÷"), KeyModel("×"), KeyModel("¶"), KeyModel("Δ")
    )

    val symbols2Row2 = listOf(
        KeyModel("£"), KeyModel("¢"), KeyModel("€"), KeyModel("¥"), KeyModel("^"),
        KeyModel("°"), KeyModel("="), KeyModel("{"), KeyModel("}"), KeyModel("\\")
    )

    val symbols2Row3 = listOf(
        KeyModel("123", type = KeyType.SWITCH_MODE, weight = 1.3f),
        KeyModel("%"), KeyModel("©"), KeyModel("®"), KeyModel("™"), KeyModel("✓"),
        KeyModel("<"), KeyModel(">"), KeyModel("["), KeyModel("]"),
        KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
    )

    val englishNumberRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val arabicNumberRow = listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠")
}
