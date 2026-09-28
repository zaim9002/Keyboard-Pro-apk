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

    // Numbers Row (١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ ٠)
    val arabicNumbersRow = listOf(
        KeyModel("١", secondaryText = "1"),
        KeyModel("٢", secondaryText = "2"),
        KeyModel("٣", secondaryText = "3"),
        KeyModel("٤", secondaryText = "4"),
        KeyModel("٥", secondaryText = "5"),
        KeyModel("٦", secondaryText = "6"),
        KeyModel("٧", secondaryText = "7"),
        KeyModel("٨", secondaryText = "8"),
        KeyModel("٩", secondaryText = "9"),
        KeyModel("٠", secondaryText = "0")
    )

    val englishNumbersRow = listOf(
        KeyModel("1", secondaryText = "١"),
        KeyModel("2", secondaryText = "٢"),
        KeyModel("3", secondaryText = "٣"),
        KeyModel("4", secondaryText = "٤"),
        KeyModel("5", secondaryText = "٥"),
        KeyModel("6", secondaryText = "٦"),
        KeyModel("7", secondaryText = "٧"),
        KeyModel("8", secondaryText = "٨"),
        KeyModel("9", secondaryText = "٩"),
        KeyModel("0", secondaryText = "٠")
    )

    // Row 1 (ض ص ث ق ف غ ع ه خ ح ج) - 11 Keys matching Design Keyboard (Screenshot 1)
    val arabicRow1 = listOf(
        KeyModel("ض", secondaryText = "+", popupOptions = listOf("+", "ضـ", "ضَ", "ضِ", "ضُ", "ضّ")),
        KeyModel("ص", secondaryText = "×", popupOptions = listOf("×", "صـ", "صَ", "صِ", "صُ", "صّ")),
        KeyModel("ث", secondaryText = "÷", popupOptions = listOf("÷", "ثـ", "ٿ", "ثَ", "ثِ", "ثُ", "ثّ")),
        KeyModel("ق", secondaryText = "=", popupOptions = listOf("=", "ڨ", "قـ", "قَ", "قِ", "قُ", "قّ")),
        KeyModel("ف", secondaryText = "/", popupOptions = listOf("/", "ڤ", "فـ", "ڥ", "فَ", "فِ", "فُ", "فّ")),
        KeyModel("غ", secondaryText = "-", popupOptions = listOf("-", "غـ", "غَ", "غِ", "غُ", "غّ")),
        KeyModel("ع", secondaryText = "<", popupOptions = listOf("<", "عـ", "عَ", "عِ", "عُ", "عّ")),
        KeyModel("ه", secondaryText = ">", popupOptions = listOf(">", "هـ", "ھ", "هَ", "هِ", "هُ", "هّ")),
        KeyModel("خ", secondaryText = "[", popupOptions = listOf("[", "خـ", "خَ", "خِ", "خُ", "خّ")),
        KeyModel("ح", secondaryText = "]", popupOptions = listOf("]", "حـ", "حَ", "حِ", "حُ", "حّ")),
        KeyModel("ج", secondaryText = "~", popupOptions = listOf("~", "چ", "جـ", "جَ", "جِ", "جُ", "جّ"))
    )

    // Row 2 (ش س ي ب ل ا ت ن م ك ط) - 11 Keys matching Design Keyboard (Screenshot 1)
    val arabicRow2 = listOf(
        KeyModel("ش", secondaryText = "!", popupOptions = listOf("!", "شـ", "شَ", "شِ", "شُ", "شّ")),
        KeyModel("س", secondaryText = "@", popupOptions = listOf("@", "سـ", "سَ", "سِ", "سُ", "سّ")),
        KeyModel("ي", secondaryText = "#", popupOptions = listOf("#", "ى", "يـ", "ې", "ێ", "يَ", "يِ", "يُ", "يَّ")),
        KeyModel("ب", secondaryText = "$", popupOptions = listOf("$", "پ", "بـ", "ٻ", "بَ", "بِ", "بُ", "بّ")),
        KeyModel("ل", secondaryText = "%", popupOptions = listOf("%", "لـ", "ڷ", "لَ", "لِ", "لُ", "لّ")),
        KeyModel("ا", secondaryText = "أ", popupOptions = listOf("أ", "إ", "آ", "ء", "ٱ", "ـ", "اٰ", "اّ")),
        KeyModel("ت", secondaryText = "^", popupOptions = listOf("^", "ة", "تـ", "ٿ", "تَ", "تِ", "تُ", "تّ")),
        KeyModel("ن", secondaryText = "&", popupOptions = listOf("&", "نـ", "ں", "ڼ", "نَ", "نِ", "نُ", "نّ")),
        KeyModel("م", secondaryText = "*", popupOptions = listOf("*", "مـ", "۾", "مَ", "مِ", "مُ", "مّ")),
        KeyModel("ك", secondaryText = ")", popupOptions = listOf(")", "گ", "ک", "كـ", "ڪ", "كَ", "كِ", "كُ", "كّ")),
        KeyModel("ط", secondaryText = "(", popupOptions = listOf("(", "طـ", "طَ", "طِ", "طُ", "طّ"))
    )

    // Row 3 (ذ ء ؤ ر ى ة و ز ظ د + ⌫) - 10 keys + Backspace matching Design Keyboard (Screenshot 1)
    val arabicRow3 = listOf(
        KeyModel("ذ", secondaryText = "-", popupOptions = listOf("-", "ڏ", "ذَ", "ذِ", "ذُ", "ذّ")),
        KeyModel("ء", secondaryText = "'", popupOptions = listOf("'", "أ", "إ", "آ", "ؤ", "ئ", "ٱ")),
        KeyModel("ؤ", secondaryText = "\"", popupOptions = listOf("\"", "و", "ؤَ")),
        KeyModel("ر", secondaryText = ";", popupOptions = listOf(";", "ژ", "ڕ", "ڑ", "رَ", "رِ", "رُ", "رّ")),
        KeyModel("ى", secondaryText = "ى", popupOptions = listOf("ى", "ي", "ىٰ", "ىَ")),
        KeyModel("ة", secondaryText = "ة", popupOptions = listOf("ة", "ه", "ةً", "ةٌ", "ةٍ")),
        KeyModel("و", secondaryText = "؟", popupOptions = listOf("؟", "ۆ", "ۉ", "وَ", "وِ", "وُ", "وّ")),
        KeyModel("ز", secondaryText = ":", popupOptions = listOf(":", "ژ", "زّ", "زَ", "زِ", "زُ")),
        KeyModel("ظ", secondaryText = "\\", popupOptions = listOf("\\", "ظـ", "ظَ", "ظِ", "ظُ", "ظّ")),
        KeyModel("د", secondaryText = ",", popupOptions = listOf(",", "ڈ", "دـ", "دَ", "دِ", "دُ", "دّ"))
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

    // Symbols Page 1 (Matching Screenshot 3)
    val symbols1Row1 = listOf(
        KeyModel("1"), KeyModel("2"), KeyModel("3"), KeyModel("4"), KeyModel("5"),
        KeyModel("6"), KeyModel("7"), KeyModel("8"), KeyModel("9"), KeyModel("0")
    )

    val symbols1Row2 = listOf(
        KeyModel("+"), KeyModel("×"), KeyModel("÷"), KeyModel("="), KeyModel("/"),
        KeyModel("_"), KeyModel("<"), KeyModel(">"), KeyModel("♡"), KeyModel("☆")
    )

    val symbols1Row3 = listOf(
        KeyModel("!"), KeyModel("@"), KeyModel("#"), KeyModel("~"), KeyModel("%"),
        KeyModel("^"), KeyModel("&"), KeyModel("*"), KeyModel("("), KeyModel(")")
    )

    val symbols1Row4 = listOf(
        KeyModel("-"), KeyModel("'"), KeyModel("\""), KeyModel(":"), KeyModel("؛"),
        KeyModel("،"), KeyModel("؟")
    )

    // Symbols Page 2 (Matching Screenshot 4)
    val symbols2Row1 = listOf(
        KeyModel("1"), KeyModel("2"), KeyModel("3"), KeyModel("4"), KeyModel("5"),
        KeyModel("6"), KeyModel("7"), KeyModel("8"), KeyModel("9"), KeyModel("0")
    )

    val symbols2Row2 = listOf(
        KeyModel("`"), KeyModel("₩"), KeyModel("\\"), KeyModel("|"), KeyModel("♠"),
        KeyModel("♣"), KeyModel("{"), KeyModel("}"), KeyModel("["), KeyModel("]")
    )

    val symbols2Row3 = listOf(
        KeyModel("•"), KeyModel("°"), KeyModel("●"), KeyModel("□"), KeyModel("■"),
        KeyModel("◇"), KeyModel("$"), KeyModel("€"), KeyModel("£"), KeyModel("¥")
    )

    val symbols2Row4 = listOf(
        KeyModel("°"), KeyModel("※"), KeyModel("¤"), KeyModel("《"), KeyModel("》"),
        KeyModel("¡"), KeyModel("¿")
    )

    val numpadRow1 = listOf(
        KeyModel("ABC", type = KeyType.SWITCH_MODE, weight = 1.2f),
        KeyModel("1", weight = 1f),
        KeyModel("2", weight = 1f),
        KeyModel("3", weight = 1f)
    )

    val numpadRow2 = listOf(
        KeyModel("+", weight = 1f),
        KeyModel("4", weight = 1f),
        KeyModel("5", weight = 1f),
        KeyModel("6", weight = 1f)
    )

    val numpadRow3 = listOf(
        KeyModel("-", weight = 1f),
        KeyModel("7", weight = 1f),
        KeyModel("8", weight = 1f),
        KeyModel("9", weight = 1f)
    )

    val numpadRow4 = listOf(
        KeyModel("0", weight = 1.5f),
        KeyModel(".", weight = 1f),
        KeyModel("⌫", type = KeyType.BACKSPACE, weight = 1.2f)
    )
}
