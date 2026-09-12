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

    val arabicRow1 = listOf(
        KeyModel("ض", secondaryText = "1", popupOptions = listOf("1", "١")),
        KeyModel("ص", secondaryText = "2", popupOptions = listOf("2", "٢")),
        KeyModel("ث", secondaryText = "3", popupOptions = listOf("3", "٣")),
        KeyModel("ق", secondaryText = "4", popupOptions = listOf("4", "٤")),
        KeyModel("ف", secondaryText = "5", popupOptions = listOf("5", "٥")),
        KeyModel("غ", secondaryText = "6", popupOptions = listOf("6", "٦")),
        KeyModel("ع", secondaryText = "7", popupOptions = listOf("7", "٧")),
        KeyModel("ه", secondaryText = "8", popupOptions = listOf("8", "٨", "ة")),
        KeyModel("خ", secondaryText = "9", popupOptions = listOf("9", "٩")),
        KeyModel("ح", secondaryText = "0", popupOptions = listOf("0", "٠")),
        KeyModel("ج"),
        KeyModel("د")
    )

    val arabicRow2 = listOf(
        KeyModel("ش"),
        KeyModel("س"),
        KeyModel("ي", popupOptions = listOf("ئ", "ى")),
        KeyModel("ب"),
        KeyModel("ل", popupOptions = listOf("لآ", "لأ", "لإ")),
        KeyModel("ا", popupOptions = listOf("أ", "إ", "آ", "ٱ")),
        KeyModel("ت"),
        KeyModel("ن"),
        KeyModel("م"),
        KeyModel("ك"),
        KeyModel("ط")
    )

    val arabicRow3 = listOf(
        KeyModel("تشكيل", type = KeyType.TASHKEEL, weight = 1.3f),
        KeyModel("ئ"),
        KeyModel("ء"),
        KeyModel("ؤ"),
        KeyModel("ر"),
        KeyModel("ى"),
        KeyModel("ة"),
        KeyModel("و", popupOptions = listOf("ؤ")),
        KeyModel("ز"),
        KeyModel("ظ"),
        KeyModel("حذف", type = KeyType.BACKSPACE, weight = 1.3f)
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
