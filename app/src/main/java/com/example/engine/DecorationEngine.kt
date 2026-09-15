package com.example.engine

object DecorationEngine {

    val readyPhrases = listOf(
        "السلام عليكم ورحمة الله وبركاته",
        "وعليكم السلام ورحمة الله وبركاته",
        "بسم الله الرحمن الرحيم",
        "الحمد لله رب العالمين",
        "سبحان الله وبحمده سبحان الله العظيم",
        "لا إله إلا الله وحده لا شريك له",
        "لا حول ولا قوة إلا بالله العلي العظيم",
        "اللهم صلّ وسلم على نبينا محمد ﷺ",
        "جزاك الله خيراً وبارك فيك",
        "بارك الله لكما وبارك عليكما",
        "أستغفر الله العظيم وأتوب إليه",
        "صباح الخير والبركة والمسرات ☀️",
        "مساء الخير والأنوار والعافية 🌙",
        "جمعة مباركة طيبة عليكم جميعاً 🕌",
        "كل عام وأنتم بخير وصحة وسلامة 🎉"
    )

    fun decorateText(input: String): List<DecoratedItem> {
        val text = input.trim()
        if (text.isEmpty()) return emptyList()

        val results = mutableListOf<DecoratedItem>()

        // 1. Arabic Kashida / Tatweel
        val kashida = text.mapIndexed { index, c ->
            if (index < text.length - 1 && isArabicLetter(c)) "${c}ـ" else "$c"
        }.joinToString("")
        results.add(DecoratedItem("تطويل الحروف", kashida, "عربي"))

        // 2. Ornaments and Borders
        results.add(DecoratedItem("إطار ياباني", "『 $text 』", "إطارات"))
        results.add(DecoratedItem("إطار عريض", "【 $text 】", "إطارات"))
        results.add(DecoratedItem("زخرفة ملكية", "꧁ $text ꧂", "إطارات"))
        results.add(DecoratedItem("نجوم لامعة", "★ $text ★", "نجوم"))
        results.add(DecoratedItem("أجنحة وريش", "彡 $text 彡", "ألعاب"))
        results.add(DecoratedItem("أقواس قرآنية", "﴿ $text ﴾", "إطارات"))
        results.add(DecoratedItem("قلوب رومانسية", "♥️ $text ♥️", "قلوب"))
        results.add(DecoratedItem("ورود وزهور", "🌸 $text 🌸", "بايو"))
        results.add(DecoratedItem("تاج الملك", "👑 $text 👑", "أسماء"))
        results.add(DecoratedItem("بريق وشرار", "✨ $text ✨", "نجوم"))
        results.add(DecoratedItem("أقواس دائرية", "❮ $text ❯", "إطارات"))
        results.add(DecoratedItem("مستطيل ظلي", "░ $text ░", "رموز"))
        results.add(DecoratedItem("مقصات وسهام", "➸ $text ➸", "رموز"))
        results.add(DecoratedItem("سيوف الألعاب", "⚔️ $text ⚔️", "ألعاب"))
        results.add(DecoratedItem("نار ولهب", "🔥 $text 🔥", "ألعاب"))

        // 3. English Unicode Styles (if contains English letters)
        if (text.any { it in 'a'..'z' || it in 'A'..'Z' }) {
            results.add(DecoratedItem("دائري مفرغ (Circles)", toCircled(text), "إنجليزي"))
            results.add(DecoratedItem("مربعات (Squares)", toSquared(text), "إنجليزي"))
            results.add(DecoratedItem("مخطط مزدوج (Outline)", toDoubleStruck(text), "إنجليزي"))
            results.add(DecoratedItem("عريض مائل (Bold Italic)", toBoldItalic(text), "إنجليزي"))
            results.add(DecoratedItem("متباعد (Spaced)", text.map { "$it " }.joinToString("").trim(), "إنجليزي"))
        }

        return results
    }

    private fun isArabicLetter(c: Char): Boolean {
        return c in '\u0600'..'\u06FF' && c !in listOf('ا', 'د', 'ذ', 'ر', 'ز', 'و', 'ء', ' ')
    }

    private fun toCircled(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x24D0 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x24B6 + (c - 'A')).concatToString()
                in '1'..'9' -> Character.toChars(0x2460 + (c - '1')).concatToString()
                '0' -> "⓪"
                else -> c.toString()
            }
        }.joinToString("")
    }

    private fun toSquared(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x1F130 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x1F130 + (c - 'A')).concatToString()
                else -> c.toString()
            }
        }.joinToString("")
    }

    private fun toDoubleStruck(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x1D552 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x1D538 + (c - 'A')).concatToString()
                in '0'..'9' -> Character.toChars(0x1D7D8 + (c - '0')).concatToString()
                else -> c.toString()
            }
        }.joinToString("")
    }

    private fun toBoldItalic(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x1D482 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x1D468 + (c - 'A')).concatToString()
                else -> c.toString()
            }
        }.joinToString("")
    }

    val letterDecorationsMap: Map<String, List<String>> = mapOf(
        "ا" to listOf("أ", "إ", "آ", "ٱ", "اّ", "اٰ", "اۛ", "اـ", "آهـ", "أَ", "إِ"),
        "ب" to listOf("ٻ", "پ", "بّ", "بـ", "بَ", "بِ", "بُ", "بً", "بۨ"),
        "ت" to listOf("ٿ", "ٺ", "تّ", "تـ", "تَ", "تِ", "تُ", "تً", "ة"),
        "ث" to listOf("ٽ", "ثّ", "ثـ", "ثَ", "ثِ", "ثُ"),
        "ج" to listOf("چ", "ڃ", "جّ", "جـ", "ڄ"),
        "ح" to listOf("حّ", "حـ", "ځ", "حَ", "حِ", "حُ"),
        "خ" to listOf("خّ", "خـ", "ڂ", "خَ", "خِ"),
        "د" to listOf("ڌ", "دّ", "ڈ", "دَ", "دِ"),
        "ذ" to listOf("ذّ", "ڏ", "ذَ"),
        "ر" to listOf("ز", "ژ", "ڕ", "رّ", "رَ", "رِ"),
        "ز" to listOf("ژ", "زّ", "ڙ", "زَ"),
        "س" to listOf("ش", "ښ", "سّ", "سـ", "ڛ", "سَ", "سِ"),
        "ش" to listOf("س", "شّ", "شـ", "ڜ", "شَ", "شِ"),
        "ص" to listOf("ض", "صّ", "صـ", "ڝ", "صَ", "صِ"),
        "ض" to listOf("ص", "ضّ", "ضـ", "ڞ", "ضَ", "ضِ"),
        "ط" to listOf("ظ", "طّ", "طـ", "طَ"),
        "ظ" to listOf("ط", "ظّ", "ظـ", "ظَ"),
        "ع" to listOf("غ", "عّ", "عـ", "ڠ", "عَ", "عِ"),
        "غ" to listOf("ع", "غّ", "غـ", "ۼ", "غَ"),
        "ف" to listOf("ڤ", "فّ", "فـ", "ڢ", "ڥ", "فَ", "فِ"),
        "ق" to listOf("ڨ", "قّ", "قـ", "ڧ", "قَ"),
        "ك" to listOf("گ", "ک", "كّ", "كـ", "ڪ", "كَ"),
        "ل" to listOf("لا", "لأ", "لإ", "لآ", "ڷ", "لّ", "لـ"),
        "م" to listOf("مّ", "مـ", "۾", "مَ", "مِ", "مُ"),
        "ن" to listOf("ڹ", "نّ", "نـ", "ڼ", "نَ", "نِ"),
        "ه" to listOf("هـ", "ة", "ھ", "هّ", "ۂ", "هَ"),
        "و" to listOf("ؤ", "ۄ", "وّ", "ۆ", "وَ", "وُ"),
        "ي" to listOf("ى", "ئ", "يـ", "يَّ", "يِ", "ێ", "ې", "يَ"),
        "ى" to listOf("ي", "ئ", "ىٰ"),
        "ة" to listOf("ه", "ت", "ةً"),
        "ء" to listOf("ئ", "ؤ", "أ", "إ", "آ")
    )

    fun getLetterDecorations(letter: String): List<String> {
        val lower = letter.lowercase()
        val customList = letterDecorationsMap[lower] ?: letterDecorationsMap[letter]
        if (customList != null) return customList

        if (lower.length == 1 && lower[0] in 'a'..'z') {
            val c = lower[0]
            val circled = Character.toChars(0x24D0 + (c - 'a')).concatToString()
            val squared = Character.toChars(0x1F130 + (c - 'a')).concatToString()
            val outline = Character.toChars(0x1D552 + (c - 'a')).concatToString()
            val bold = Character.toChars(0x1D41A + (c - 'a')).concatToString()
            val italic = Character.toChars(0x1D44E + (c - 'a')).concatToString()
            return listOf(circled, squared, outline, bold, italic)
        }
        return emptyList()
    }
}

data class DecoratedItem(
    val title: String,
    val result: String,
    val category: String
)
