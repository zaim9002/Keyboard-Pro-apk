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
        KeyModel("ض", secondaryText = "١", popupOptions = listOf("١", "1", "ص", "ظ", "ضـ", "ضَ", "ضِ", "ضُ", "ضّ")),
        KeyModel("ص", secondaryText = "٢", popupOptions = listOf("٢", "2", "ض", "س", "صـ", "صَ", "صِ", "صُ", "صّ")),
        KeyModel("ث", secondaryText = "٣", popupOptions = listOf("٣", "3", "ت", "ثـ", "ٿ", "ثَ", "ثِ", "ثُ", "ثّ")),
        KeyModel("ق", secondaryText = "٤", popupOptions = listOf("٤", "4", "ڨ", "قـ", "قَ", "قِ", "قُ", "قّ")),
        KeyModel("ف", secondaryText = "٥", popupOptions = listOf("٥", "5", "ڤ", "فـ", "ڥ", "فَ", "فِ", "فُ", "فّ")),
        KeyModel("غ", secondaryText = "٦", popupOptions = listOf("٦", "6", "ع", "غـ", "غَ", "غِ", "غُ", "غّ")),
        KeyModel("ع", secondaryText = "٧", popupOptions = listOf("٧", "7", "غ", "عـ", "عَ", "عِ", "عُ", "عّ")),
        KeyModel("ه", secondaryText = "٨", popupOptions = listOf("٨", "8", "ة", "هـ", "ھ", "هَ", "هِ", "هُ", "هّ")),
        KeyModel("خ", secondaryText = "٩", popupOptions = listOf("٩", "9", "ح", "ج", "خـ", "خَ", "خِ", "خُ", "خّ")),
        KeyModel("ح", secondaryText = "٠", popupOptions = listOf("٠", "0", "خ", "ج", "چ", "حـ", "حَ", "حِ", "حُ", "حّ")),
        KeyModel("ج", popupOptions = listOf("چ", "ح", "خ", "جـ", "جَ", "جِ", "جُ", "جّ"))
    )

    val arabicRow2 = listOf(
        KeyModel("ش", popupOptions = listOf("س", "شـ", "شَ", "شِ", "شُ", "شّ")),
        KeyModel("س", popupOptions = listOf("ش", "ص", "سـ", "سَ", "سِ", "سُ", "سّ")),
        KeyModel("ي", popupOptions = listOf("ى", "ئ", "يـ", "ې", "ێ", "يَ", "يِ", "يُ", "يَّ")),
        KeyModel("ب", popupOptions = listOf("پ", "ـ", "بـ", "ٻ", "بَ", "بِ", "بُ", "بّ")),
        KeyModel("ل", popupOptions = listOf("لا", "لأ", "لإ", "لآ", "لـ", "ڷ", "لَ", "لِ", "لُ", "لّ")),
        KeyModel("ا", popupOptions = listOf("أ", "إ", "آ", "ء", "ٱ", "ـ", "اٰ", "اّ", "أَ", "إِ")),
        KeyModel("ت", popupOptions = listOf("ة", "ث", "تـ", "ٿ", "تَ", "تِ", "تُ", "تّ")),
        KeyModel("ن", popupOptions = listOf("نـ", "ں", "ڼ", "نَ", "نِ", "نُ", "نّ")),
        KeyModel("م", popupOptions = listOf("مـ", "۾", "مَ", "مِ", "مُ", "مّ")),
        KeyModel("ك", popupOptions = listOf("گ", "ک", "كـ", "ڪ", "ڨ", "كَ", "كِ", "كُ", "كّ")),
        KeyModel("ط", popupOptions = listOf("ظ", "طـ", "طَ", "طِ", "طُ", "طّ"))
    )

    val arabicRow3 = listOf(
        KeyModel("ذ", popupOptions = listOf("د", "ظ", "ڏ", "ذَ", "ذِ", "ذُ", "ذّ")),
        KeyModel("ء", popupOptions = listOf("أ", "إ", "آ", "ئ", "ؤ", "ٱ", "ء")),
        KeyModel("ؤ", popupOptions = listOf("و", "ء", "ئ", "أ", "ؤَ")),
        KeyModel("ر", popupOptions = listOf("ز", "ژ", "ڕ", "ڑ", "رَ", "رِ", "رُ", "رّ")),
        KeyModel("ى", popupOptions = listOf("ي", "ئ", "ىٰ", "ىَ")),
        KeyModel("ة", popupOptions = listOf("ه", "ت", "ةً", "ةٌ", "ةٍ")),
        KeyModel("و", popupOptions = listOf("ؤ", "و", "ۆ", "ۉ", "وَ", "وِ", "وُ", "وّ")),
        KeyModel("ز", popupOptions = listOf("ژ", "ر", "زّ", "زَ", "زِ", "زُ")),
        KeyModel("ظ", popupOptions = listOf("ط", "ض", "ظـ", "ظَ", "ظِ", "ظُ", "ظّ")),
        KeyModel("د", popupOptions = listOf("ذ", "ڈ", "دـ", "دَ", "دِ", "دُ", "دّ"))
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

    // Calculator / Numpad Keypad (Image 6 from user)
    val numpadRow1 = listOf(
        KeyModel("(", weight = 1f),
        KeyModel(")", weight = 1f),
        KeyModel("1", weight = 1.3f),
        KeyModel("2", weight = 1.3f),
        KeyModel("3", weight = 1.3f),
        KeyModel("ABC", type = KeyType.SWITCH_MODE, weight = 1.2f)
    )

    val numpadRow2 = listOf(
        KeyModel("+", weight = 1f),
        KeyModel("-", weight = 1f),
        KeyModel("4", weight = 1.3f),
        KeyModel("5", weight = 1.3f),
        KeyModel("6", weight = 1.3f),
        KeyModel("=", weight = 1.2f)
    )

    val numpadRow3 = listOf(
        KeyModel("/", weight = 1f),
        KeyModel("%", weight = 1f),
        KeyModel("7", weight = 1.3f),
        KeyModel("8", weight = 1.3f),
        KeyModel("9", weight = 1.3f),
        KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.2f)
    )

    val numpadRow4 = listOf(
        KeyModel("123!#()", type = KeyType.SWITCH_MODE, weight = 1.2f),
        KeyModel(",", weight = 1f),
        KeyModel("*", weight = 1.3f),
        KeyModel("0", weight = 1.3f),
        KeyModel(".", weight = 1f),
        KeyModel("enter", type = KeyType.ENTER, weight = 1.2f)
    )
}
