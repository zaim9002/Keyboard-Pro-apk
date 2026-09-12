package com.example.engine

object TranslationEngine {

    // Common phrase translations AR <-> EN
    private val arToEnPhrases = mapOf(
        "مرحبا" to "Hello",
        "مرحباً" to "Hello",
        "أهلاً وسهلاً" to "Welcome",
        "كيف حالك" to "How are you?",
        "كيف حالكم" to "How are you all?",
        "أنا بخير" to "I am doing well",
        "شكرا" to "Thank you",
        "شكراً" to "Thank you",
        "شكراً جزيلاً" to "Thank you very much",
        "عفواً" to "You're welcome",
        "من فضلك" to "Please",
        "لو سمحت" to "Excuse me",
        "نعم" to "Yes",
        "لا" to "No",
        "مع السلامة" to "Goodbye",
        "إلى اللقاء" to "See you later",
        "صباح الخير" to "Good morning",
        "مساء الخير" to "Good evening",
        "تصبح على خير" to "Good night",
        "مبروك" to "Congratulations",
        "بالتوفيق" to "Good luck",
        "أحبك" to "I love you",
        "أنا في الطريق" to "I am on my way",
        "أين أنت" to "Where are you?",
        "ما رأيك" to "What do you think?",
        "حسناً" to "Alright / Okay",
        "تمام" to "Great / Perfect",
        "كل عام وأنتم بخير" to "Happy New Year / Best wishes"
    )

    private val enToArPhrases = arToEnPhrases.entries.associate { (k, v) ->
        v.lowercase().trimEnd('?', '!') to k
    }

    fun translate(text: String, sourceLang: String, targetLang: String): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return ""

        if (sourceLang == "ar" && targetLang == "en") {
            val direct = arToEnPhrases[trimmed]
            if (direct != null) return direct

            // Word by word lookup
            val words = trimmed.split("\\s+".toRegex())
            val translatedWords = words.map { word ->
                arToEnPhrases[word] ?: word
            }
            return translatedWords.joinToString(" ")
        }

        if (sourceLang == "en" && targetLang == "ar") {
            val key = trimmed.lowercase().trimEnd('?', '!')
            val direct = enToArPhrases[key]
            if (direct != null) return direct

            val words = trimmed.split("\\s+".toRegex())
            val translatedWords = words.map { word ->
                enToArPhrases[word.lowercase()] ?: word
            }
            return translatedWords.joinToString(" ")
        }

        // Generic fallback
        return trimmed
    }
}
