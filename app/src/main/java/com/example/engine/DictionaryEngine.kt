package com.example.engine

data class WordDefinition(
    val word: String,
    val definition: String,
    val synonyms: List<String>,
    val example: String
)

object DictionaryEngine {

    private val entries = mapOf(
        "سلام" to WordDefinition("سلام", "الأمان والسكينة، التحية الطيبة، اسم من أسماء الله الحسنى.", listOf("أمان", "سكينة", "صلح", "وئام"), "حلّ السلام في أرجاء المكان."),
        "شكرا" to WordDefinition("شكراً", "التعبير عن الامتنان والاعتراف بالجميل.", listOf("امتنان", "تقدير", "عرفان"), "شكراً لك على حسن صنيعك."),
        "كتاب" to WordDefinition("كتاب", "مجموعة أوراق متصلة تحوي نصوصاً وعلماً.", listOf("مؤلف", "سفر", "مصحف"), "الكتاب خير جليس في الزمان."),
        "علم" to WordDefinition("علم", "إدراك الشيء على حقيقته، والمعرفة المكتسبة.", listOf("معرفة", "دراية", "ثقافة"), "العلم نور والجهل ظلام."),
        "جميل" to WordDefinition("جميل", "ما يتصف بالحسن والبهاء، صنيع طيب.", listOf("رائع", "بديع", "حسن"), "هذا منظر جميل جداً."),
        "صداقة" to WordDefinition("صداقة", "علاقة مودة ومحبة وثيقة بين شخصين أو أكثر.", listOf("أخوة", "مودة", "وفاء"), "الصداقة كنز ثمين لا يفنى."),
        "سعادة" to WordDefinition("سعادة", "شعور بالبهجة والرضا والسرور الداخلي.", listOf("فرح", "بهجة", "سرور"), "أتمنى لكم دوام السعادة والنجاح."),
        "تطبيق" to WordDefinition("تطبيق", "برنامج حاسوبي يؤدي وظيفة محددة للمستخدم.", listOf("برنامج", "أداة", "نظام"), "هذا التطبيق سريع وعملي."),
        "قوة" to WordDefinition("قوة", "القدرة والشدة على إنجاز الأعمال ومواجهة الصعاب.", listOf("عزم", "بأس", "شكيمة"), "في الاتحاد قوة."),
        "إتقان" to WordDefinition("إتقان", "أداء العمل بأعلى معايير الجودة والإحكام.", listOf("إحكام", "براعة", "جودة"), "إن الله يحب إذا عمل أحدكم عملاً أن يتقنه."),
        // English words
        "hello" to WordDefinition("hello", "Used as a greeting or to begin a phone conversation.", listOf("hi", "greetings", "welcome"), "Hello, how are you today?"),
        "peace" to WordDefinition("peace", "Freedom from disturbance, tranquility, and harmony.", listOf("tranquility", "serenity", "calm"), "May peace prevail across the world."),
        "friend" to WordDefinition("friend", "A person whom one knows and with whom one has a bond of mutual affection.", listOf("companion", "pal", "ally"), "A friend in need is a friend indeed."),
        "code" to WordDefinition("code", "Instructions for a computer or a system of symbols.", listOf("program", "cipher", "script"), "The application is built with clean Kotlin code."),
        "smart" to WordDefinition("smart", "Having or showing a quick-witted intelligence.", listOf("clever", "intelligent", "sharp"), "This is a smart and fast mobile keyboard.")
    )

    fun lookup(query: String): WordDefinition? {
        val clean = query.trim().lowercase()
        return entries[clean] ?: entries.values.find {
            it.word.contains(clean, ignoreCase = true) || it.synonyms.any { s -> s.contains(clean, ignoreCase = true) }
        }
    }
}
