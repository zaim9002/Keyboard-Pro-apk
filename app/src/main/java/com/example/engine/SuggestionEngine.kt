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

        // 2. If current word is empty, predict next word based on previous word
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
            return@withContext suggestions.distinct().take(4)
        }

        // 3. User personalized words matching current prefix
        val userMatches = userWordRepository.getMatchingWords(cleanCurrent)
        suggestions.addAll(userMatches)

        // 4. Word completion from dictionary matching prefix
        val dict = if (isArabic) arabicDictionary else englishDictionary
        val prefixMatches = dict.filter {
            it.startsWith(cleanCurrent, ignoreCase = true) && !it.equals(cleanCurrent, ignoreCase = true)
        }
        suggestions.addAll(prefixMatches.take(4))

        // 5. If still few suggestions, find words containing the substring or close matches
        if (suggestions.size < 3) {
            val containsMatches = dict.filter {
                it.contains(cleanCurrent, ignoreCase = true) && !it.equals(cleanCurrent, ignoreCase = true)
            }
            suggestions.addAll(containsMatches.take(3))
        }

        // Ensure current typed word is not blank and add proper formatting
        if (suggestions.isEmpty()) {
            suggestions.add(cleanCurrent)
        }

        return@withContext suggestions.distinct().take(4)
    }

    suspend fun learnWord(word: String) {
        userWordRepository.learnWord(word)
    }
}
