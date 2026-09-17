package com.example.ime.util

object CharacterVariants {

    private val arabicVariants: Map<String, List<String>> = mapOf(
        "ا" to listOf("ا", "أ", "إ", "آ", "ء", "ٱ", "ـ", "اٰ", "أَ", "إِ"),
        "أ" to listOf("أ", "إ", "آ", "ا", "ء", "ٱ"),
        "إ" to listOf("إ", "أ", "آ", "ا", "ء"),
        "آ" to listOf("آ", "أ", "إ", "ا"),
        "ء" to listOf("ء", "أ", "إ", "آ", "ئ", "ؤ", "ٱ"),
        "و" to listOf("و", "ؤ", "ۆ", "ۉ", "وَ", "وُ", "وِ", "وّ"),
        "ي" to listOf("ي", "ى", "ئ", "يـ", "ې", "ێ", "يَّ", "يَ", "يِ"),
        "ه" to listOf("ه", "ة", "هـ", "ھ", "هَ", "هِ", "هُ"),
        "ب" to listOf("ب", "پ", "ـ", "بـ", "ٻ", "بَ", "بِ", "بُ", "بّ"),
        "ت" to listOf("ت", "ة", "ث", "تـ", "ٿ", "تَ", "تِ", "تُ"),
        "ث" to listOf("ث", "ت", "ثـ", "ٿ", "ثَ"),
        "ج" to listOf("ج", "چ", "ح", "خ", "جـ", "جَ"),
        "ح" to listOf("ح", "خ", "ج", "چ", "حـ", "حَ"),
        "خ" to listOf("خ", "ح", "ج", "خـ", "خَ"),
        "د" to listOf("د", "ذ", "ڈ", "دـ", "دَ"),
        "ذ" to listOf("ذ", "د", "ظ", "ڏ", "ذَ"),
        "ر" to listOf("ر", "ز", "ژ", "ڕ", "ڑ", "رَ"),
        "ز" to listOf("ز", "ژ", "ر", "زَ"),
        "س" to listOf("س", "ش", "ص", "سـ", "سَ"),
        "ش" to listOf("ش", "س", "شـ", "شَ"),
        "ص" to listOf("ص", "ض", "س", "صـ", "صَ"),
        "ض" to listOf("ض", "ص", "ظ", "ضـ", "ضَ"),
        "ط" to listOf("ط", "ظ", "طـ", "طَ"),
        "ظ" to listOf("ظ", "ط", "ض", "ظـ", "ظَ"),
        "ع" to listOf("ع", "غ", "عـ", "عَ"),
        "غ" to listOf("غ", "ع", "غـ", "غَ"),
        "ف" to listOf("ف", "ڤ", "فـ", "ڥ", "فَ"),
        "ق" to listOf("ق", "ڨ", "قـ", "قَ"),
        "ك" to listOf("ك", "گ", "ک", "كـ", "ڪ", "ڨ", "كَ"),
        "ل" to listOf("ل", "لا", "لأ", "لإ", "لآ", "لـ", "ڷ", "لَ"),
        "م" to listOf("م", "مـ", "۾", "مَ"),
        "ن" to listOf("ن", "نـ", "ں", "ڼ", "نَ"),
        "ة" to listOf("ة", "ه", "ت", "ةً", "ةٌ", "ةٍ"),
        "ى" to listOf("ى", "ي", "ئ", "ىٰ", "ىَ"),
        "ئ" to listOf("ئ", "ي", "ى", "ء", "ئـ"),
        "ؤ" to listOf("ؤ", "و", "ء", "ؤَ")
    )

    private val latinVariants: Map<Char, List<String>> = mapOf(
        'a' to listOf("a", "á", "à", "â", "ä", "ã", "å", "ā", "æ"),
        'c' to listOf("c", "ç", "ć", "č"),
        'd' to listOf("d", "ð", "ď"),
        'e' to listOf("e", "é", "è", "ê", "ë", "ē", "ė", "ę"),
        'g' to listOf("g", "ğ"),
        'i' to listOf("i", "í", "ì", "î", "ï", "ī"),
        'l' to listOf("l", "ł"),
        'n' to listOf("n", "ñ", "ń"),
        'o' to listOf("o", "ó", "ò", "ô", "ö", "õ", "ø", "ō", "œ"),
        'r' to listOf("r", "ř"),
        's' to listOf("s", "ß", "ś", "š", "ş", "$"),
        't' to listOf("t", "ţ", "ť"),
        'u' to listOf("u", "ú", "ù", "û", "ü", "ū"),
        'y' to listOf("y", "ý", "ÿ"),
        'z' to listOf("z", "ž", "ź", "ż")
    )

    private val numberVariants: Map<String, List<String>> = mapOf(
        "0" to listOf("0", "٠", "⁰", "°", "∅"),
        "٠" to listOf("٠", "0", "⁰", "°"),
        "1" to listOf("1", "١", "¹", "½", "⅓", "¼"),
        "١" to listOf("١", "1", "¹", "½"),
        "2" to listOf("2", "٢", "²", "⅔"),
        "٢" to listOf("٢", "2", "²"),
        "3" to listOf("3", "٣", "³", "¾"),
        "٣" to listOf("٣", "3", "³"),
        "4" to listOf("4", "٤", "⁴"),
        "٤" to listOf("٤", "4", "⁴"),
        "5" to listOf("5", "٥", "⅝"),
        "٥" to listOf("٥", "5"),
        "6" to listOf("6", "٦"),
        "٦" to listOf("٦", "6"),
        "7" to listOf("7", "٧", "⅞"),
        "٧" to listOf("٧", "7"),
        "8" to listOf("8", "٨"),
        "٨" to listOf("٨", "8"),
        "9" to listOf("9", "٩"),
        "٩" to listOf("٩", "9")
    )

    private val symbolVariants: Map<String, List<String>> = mapOf(
        "$" to listOf("$", "€", "£", "¥", "﷼", "د.ك", "د.إ", "ج.م", "₹", "₿"),
        "؟" to listOf("؟", "?", "¿"),
        "?" to listOf("?", "؟", "¿"),
        "!" to listOf("!", "¡", "‼"),
        "%" to listOf("%", "٪", "‰"),
        "٪" to listOf("٪", "%", "‰"),
        "." to listOf(".", "…", "،", ","),
        "،" to listOf("،", ",", ";", "؛"),
        "," to listOf(",", "،", ";", "؛"),
        "-" to listOf("-", "—", "_", "–")
    )

    fun getVariants(primaryText: String, explicitPopupOptions: List<String> = emptyList()): List<String> {
        val result = mutableListOf<String>()

        // 1. Explicit popup options on key
        if (explicitPopupOptions.isNotEmpty()) {
            result.addAll(explicitPopupOptions)
        }

        // 2. Arabic variant lookup
        arabicVariants[primaryText]?.let {
            result.addAll(it)
        }

        // 3. Latin variant lookup (supports upper & lower case!)
        if (primaryText.length == 1 && primaryText[0].isLetter()) {
            val char = primaryText[0]
            val isUpper = char.isUpperCase()
            val lowerChar = char.lowercaseChar()
            latinVariants[lowerChar]?.let { variants ->
                val adjusted = variants.map { if (isUpper) it.uppercase() else it.lowercase() }
                result.addAll(adjusted)
            }
        }

        // 4. Number lookup
        numberVariants[primaryText]?.let {
            result.addAll(it)
        }

        // 5. Symbol lookup
        symbolVariants[primaryText]?.let {
            result.addAll(it)
        }

        // Always ensure primary text is included as the first or among the items
        if (!result.contains(primaryText)) {
            result.add(0, primaryText)
        }

        return result.distinct().filter { it.isNotBlank() }
    }
}
