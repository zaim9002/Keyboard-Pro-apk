package com.example.engine

import com.example.data.repository.ShortcutRepository
import com.example.data.repository.UserWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SuggestionEngine(
    private val userWordRepository: UserWordRepository,
    private val shortcutRepository: ShortcutRepository
) {

    // Common Arabic N-grams (Next Word Predictions)
    private val arabicNextWords = mapOf(
        "كيف" to listOf("حالك", "حالكم", "أنت", "الأمور"),
        "السلام" to listOf("عليكم", "عليكم ورحمة الله", "ورحمة الله"),
        "شكرا" to listOf("جزيلاً", "لك", "جزيلاً لك", "عليك"),
        "صباح" to listOf("الخير", "النور", "الورد", "الجمال"),
        "مساء" to listOf("الخير", "النور", "الورد", "الياسمين"),
        "جزاك" to listOf("الله خيراً", "الله خيراً كثيراً", "الله ألف خير"),
        "بارك" to listOf("الله فيك", "الله لك", "الله بك"),
        "إن" to listOf("شاء الله", "كنت", "كان", "شاء الرحمن"),
        "ما" to listOf("شاء الله", "هو", "هذا", "رأيك"),
        "الحمد" to listOf("لله", "لله رب العالمين", "لله دائماً وأبداً"),
        "سبحان" to listOf("الله", "الله وبحمده", "الله العظيم"),
        "لا" to listOf("إله إلا الله", "تنسى", "عليك", "تؤاخذنا"),
        "كل" to listOf("عام وأنتم بخير", "شيء", "يوم", "خير"),
        "في" to listOf("أمان الله", "الوقت الحالي", "خدمتكم", "انتظارك"),
        "من" to listOf("فضلك", "أجل", "دواعي سروري", "جديد"),
        "الله" to listOf("يسعدك", "يحفظك", "يعطيك العافية", "يبارك فيك"),
        "يعطيك" to listOf("العافية", "الصحة", "ألف عافية"),
        "تسلم" to listOf("يدك", "يا غالي", "يا رب"),
        "أهلاً" to listOf("وسهلاً", "بك", "وسهلاً بك"),
        "أتمنى" to listOf("لك يوماً سعيداً", "لك التوفيق", "لك كل خير"),
        "هل" to listOf("يمكنك", "أنت بخير", "تريد", "هناك"),
        "أنا" to listOf("بخير", "في الطريق", "أوافقك", "هنا")
    )

    // Common English N-grams
    private val englishNextWords = mapOf(
        "how" to listOf("are", "is", "about", "do"),
        "thank" to listOf("you", "you very much", "you so much"),
        "good" to listOf("morning", "afternoon", "evening", "night", "luck"),
        "see" to listOf("you", "you soon", "you later", "it"),
        "let" to listOf("me", "us", "it", "know"),
        "i" to listOf("am", "will", "have", "think", "love", "can"),
        "you" to listOf("are", "can", "have", "will", "know"),
        "please" to listOf("let me know", "find attached", "call me", "check"),
        "looking" to listOf("forward", "for", "at", "good"),
        "what" to listOf("is", "are", "do you think", "time"),
        "have" to listOf("a great day", "a good one", "been", "done"),
        "nice" to listOf("to meet you", "work", "day", "one"),
        "where" to listOf("are you", "is", "were you"),
        "call" to listOf("me", "back", "you later")
    )

    // Standard Arabic Common Words (offline dictionary)
    private val arabicDictionary = (listOf(
        "السلام", "عليكم", "ورحمة", "الله", "وبركاته", "شكراً", "جزيلاً",
        "مرحباً", "صباح", "الخير", "مساء", "النور", "كيف", "حالك", "حالكم",
        "الحمد", "لله", "سبحان", "استغفر", "جزاك", "خيراً", "بارك", "فيك",
        "أهلاً", "وسهلاً", "إن", "شاء", "ما", "جميل", "رائع", "ممتاز",
        "أنا", "أنت", "نحن", "هو", "هي", "هم", "هذا", "هذه", "ذلك",
        "نعم", "لا", "ربما", "حسناً", "تمام", "أكيد", "بالتأكيد", "طبعاً",
        "اليوم", "غداً", "أمس", "الآن", "قريباً", "دائماً", "أبداً",
        "سعيد", "فرحان", "مبارك", "تهانينا", "مبروك", "بالتوفيق", "النجاح",
        "رسالة", "مكالمة", "تطبيق", "هاتف", "صورة", "فيديو", "ملف", "رابط",
        "عمل", "دراسة", "جامعة", "مدرسة", "بيت", "طريق", "سيارة", "سفر",
        "أحبك", "صديقي", "أخي", "أختي", "عزيزي", "أستاذ", "مهندس", "دكتور",
        "أرجو", "أتمنى", "يمكنك", "مساعدة", "خدمة", "سؤال", "استفسار",
        "عفواً", "معذرة", "آسف", "اعتذر", "حقك", "علي", "بسيطة", "ولا يهمك"
    ) + com.example.language.pack.ArabicLanguagePack.richVocabulary).distinct()

    // Standard English Common Words
    private val englishDictionary = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "hello", "thanks", "welcome", "please", "sorry", "meeting", "message", "today"
    )

    // Comprehensive Arabic Typo & Orthographic Correction Dictionary
    private val arabicTypoMap = mapOf(
        // Hamzas
        "الي" to "إلى", "اليكم" to "إليكم", "اليهم" to "إليهم", "اذا" to "إذا",
        "اكثر" to "أكثر", "اكبر" to "أكبر", "اصغر" to "أصغر", "افضل" to "أفضل",
        "احسن" to "أحسن", "اول" to "أول", "اخر" to "آخر", "الان" to "الآن",
        "اصبح" to "أصبح", "امس" to "أمس", "ابدا" to "أبداً", "ايضا" to "أيضاً",
        "شكرا" to "شكراً", "عفوا" to "عفواً", "جدا" to "جداً", "حقا" to "حقاً",
        "تقريبا" to "تقريباً", "دائما" to "دائماً", "طبعا" to "طبعاً", "اهلا" to "أهلاً",
        "سهلا" to "سهلاً", "مرحبا" to "مرحباً", "ايمان" to "إيمان", "اسلام" to "إسلام",
        "انسان" to "إنسان", "اريد" to "أريد", "ارجو" to "أرجو", "اتمنى" to "أتمنى",
        "اعتقد" to "أعتقد", "احبك" to "أحبك", "اخي" to "أخي", "اختي" to "أختي",
        "ابي" to "أبي", "امي" to "أمي", "استاذ" to "أستاذ", "ايام" to "أيام",
        "اشياء" to "أشياء", "اهم" to "أهم", "انك" to "أنك", "انكم" to "أنكم",
        // Taa Marbuta & Haa
        "مدرسه" to "مدرسة", "سياره" to "سيارة", "صوره" to "صورة", "حياه" to "حياة",
        "رساله" to "رسالة", "مكالمه" to "مكالمة", "طبيعه" to "طبيعة", "جميله" to "جميلة",
        "رائعه" to "رائعة", "طريقه" to "طريقة", "لغه" to "لغة", "خدمه" to "خدمة",
        "شركه" to "شركة", "فكره" to "فكرة", "قدره" to "قدرة", "ساعه" to "ساعة",
        "دقيقه" to "دقيقة", "قوه" to "قوة", "جامعه" to "جامعة", "مدينه" to "مدينة",
        "قصه" to "قصة", "نقطه" to "نقطة", "فرصه" to "فرصة", "صفحه" to "صفحة",
        "صحه" to "صحة", "عافيه" to "عافية", "فتره" to "فترة",
        // Yaa & Alef Maqsura
        "متي" to "متى", "حتي" to "حتى", "لدي" to "لدى", "سوي" to "سوى",
        "أخري" to "أخرى", "كبري" to "كبرى", "صغري" to "صغرى", "مستشفي" to "مستشفى",
        "معني" to "معنى", "فتي" to "فتى", "دعوي" to "دعوى", "موسي" to "موسى",
        "عيسي" to "عيسى", "يحيا" to "يحيى",
        // Common Spelling blunders & joined words
        "انشاءالله" to "إن شاء الله", "انشاء الله" to "إن شاء الله", "إنشاء الله" to "إن شاء الله",
        "ماشاءالله" to "ما شاء الله", "ماشاء الله" to "ما شاء الله",
        "باذن الله" to "بإذن الله", "بأذن الله" to "بإذن الله",
        "لاكن" to "لكن", "هاذا" to "هذا", "هاذه" to "هذه", "هكدا" to "هكذا", "ذالك" to "ذلك",
        "مسؤل" to "مسؤول", "شؤن" to "شؤون", "هيئه" to "هيئة", "بيئه" to "بيئة",
        "خطاء" to "خطأ", "جزاكالله" to "جزاك الله خيراً", "يعطيكالعافيه" to "يعطيك العافية"
    )

    // English Typo Dictionary
    private val englishTypoMap = mapOf(
        "teh" to "the", "recieve" to "receive", "seperate" to "separate", "definately" to "definitely",
        "dont" to "don't", "cant" to "can't", "wont" to "won't", "im" to "I'm", "youre" to "you're",
        "theyre" to "they're", "alot" to "a lot", "untill" to "until", "occured" to "occurred",
        "goverment" to "government", "tommorow" to "tomorrow", "truely" to "truly", "wierd" to "weird",
        "thier" to "their", "becuase" to "because", "beleive" to "believe", "acheive" to "achieve"
    )

    /**
     * Check if a word is a known typo and get its accurate correction.
     */
    fun getAutoCorrection(word: String, isArabic: Boolean): String? {
        val clean = word.trim()
        if (clean.isEmpty()) return null

        if (isArabic) {
            arabicTypoMap[clean]?.let { return it }
        } else {
            englishTypoMap[clean.lowercase()]?.let { return it }
        }

        // Fuzzy 1-character edit distance check if word is long enough and not in dictionary
        val dict = if (isArabic) arabicDictionary else englishDictionary
        if (clean.length >= 4 && !dict.contains(clean)) {
            val candidate = dict.firstOrNull { editDistance(it, clean) == 1 }
            if (candidate != null) return candidate
        }

        return null
    }

    /**
     * Simple Levenshtein distance for fuzzy matching
     */
    private fun editDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    /**
     * Check if word is known in personal dictionary or standard vocabulary
     */
    suspend fun isWordKnown(word: String, isArabic: Boolean): Boolean {
        val clean = word.trim()
        if (clean.isEmpty()) return true
        val dict = if (isArabic) arabicDictionary else englishDictionary
        if (dict.contains(clean)) return true
        val userMatches = userWordRepository.getMatchingWords(clean)
        return userMatches.any { it.equals(clean, ignoreCase = true) }
    }

    suspend fun getSuggestions(
        currentWord: String,
        previousWord: String?,
        isArabic: Boolean
    ): List<String> = withContext(Dispatchers.Default) {
        val cleanCurrent = currentWord.trim()
        val suggestions = mutableListOf<String>()

        // 1. Check if user typed a shortcut trigger (e.g. "سلام" -> "السلام عليكم...")
        if (cleanCurrent.isNotEmpty()) {
            val shortcut = shortcutRepository.findByTrigger(cleanCurrent)
            if (shortcut != null) {
                suggestions.add(shortcut.replacement)
            }
        }

        // 2. Immediate Typo Auto-Correction check (Put correction first!)
        if (cleanCurrent.isNotEmpty()) {
            val correction = getAutoCorrection(cleanCurrent, isArabic)
            if (correction != null && !correction.equals(cleanCurrent, ignoreCase = true)) {
                suggestions.add(correction)
            }
        }

        // 3. If current word is empty, predict next word based on previous word
        if (cleanCurrent.isEmpty()) {
            val prev = previousWord?.trim()?.lowercase() ?: ""
            if (prev.isNotEmpty()) {
                val nexts = if (isArabic) arabicNextWords[prev] else englishNextWords[prev]
                if (!nexts.isNullOrEmpty()) {
                    suggestions.addAll(nexts.take(4))
                }
            }
            if (suggestions.isEmpty()) {
                // Default quick starters
                if (isArabic) {
                    suggestions.addAll(listOf("السلام عليكم", "مرحباً", "شكراً", "تمام"))
                } else {
                    suggestions.addAll(listOf("Hello", "Thanks", "How are you", "Sounds good"))
                }
            }
            return@withContext suggestions.distinct().take(10)
        }

        // 4. User personalized words matching current prefix (High Priority!)
        val userMatches = userWordRepository.getMatchingWords(cleanCurrent)
        suggestions.addAll(userMatches)

        // 5. Word completion from dictionary matching prefix
        val dict = if (isArabic) arabicDictionary else englishDictionary
        val prefixMatches = dict.filter {
            it.startsWith(cleanCurrent, ignoreCase = true) && !it.equals(cleanCurrent, ignoreCase = true)
        }
        suggestions.addAll(prefixMatches.take(8))

        // 6. If still few suggestions, find words containing the substring or close matches
        if (suggestions.size < 6) {
            val containsMatches = dict.filter {
                it.contains(cleanCurrent, ignoreCase = true) && !it.equals(cleanCurrent, ignoreCase = true)
            }
            suggestions.addAll(containsMatches.take(6))
        }

        // Ensure current typed word is available if no direct exact match
        if (suggestions.isEmpty()) {
            suggestions.add(cleanCurrent)
        }

        return@withContext suggestions.distinct().take(10)
    }

    suspend fun learnWord(word: String) {
        userWordRepository.learnWord(word)
    }
}
