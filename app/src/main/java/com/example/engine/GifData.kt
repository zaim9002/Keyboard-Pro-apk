package com.example.engine

data class GifItem(
    val id: String,
    val title: String,
    val previewIcon: String,
    val description: String,
    val category: String,
    val gifUrl: String,
    val tags: List<String> = emptyList()
)

object GifData {

    val categories = listOf(
        "الرائجة 🔥",
        "ردود أفعال 😂",
        "حب ومودة ❤️",
        "تشجيع وحماس 👏",
        "تهاني واحتفال 🎉",
        "صباح ومساء ☕",
        "قطط وميمز 🐱",
        "سلام وتحية 🤝",
        "أطفال وبراءة 👶",
        "طعام ولذائذ 🍕"
    )

    val allGifs = listOf(
        // الرائجة
        GifItem(
            id = "g_trend_1",
            title = "تصفيق حار واستحسان",
            previewIcon = "👏",
            description = "تصفيق مستمر وإعجاب كبير",
            category = "الرائجة 🔥",
            gifUrl = "https://media.giphy.com/media/l3q2XhfQ8oCkm1GhO/giphy.gif",
            tags = listOf("تصفيق", "برافو", "شكرا", "تحية", "clap", "applause")
        ),
        GifItem(
            id = "g_trend_2",
            title = "ضحك هستيري",
            previewIcon = "😂",
            description = "ضحك شديد حتى البكاء",
            category = "الرائجة 🔥",
            gifUrl = "https://media.giphy.com/media/10JhviFuU2gWD6/giphy.gif",
            tags = listOf("ضحك", "ههههه", "فطس", "كركرة", "laugh", "lol")
        ),
        GifItem(
            id = "g_trend_3",
            title = "إبهام للأعلى موافق",
            previewIcon = "👍",
            description = "موافقة تامة ورضا تام",
            category = "الرائجة 🔥",
            gifUrl = "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif",
            tags = listOf("تمام", "اوك", "موافق", "ممتاز", "thumbs", "yes")
        ),
        GifItem(
            id = "g_trend_4",
            title = "رقصة السعادة والانتصار",
            previewIcon = "🕺",
            description = "رقصة مرحة تعبر عن الفرح والنجاح",
            category = "الرائجة 🔥",
            gifUrl = "https://media.giphy.com/media/blSTtZehjAZ8I/giphy.gif",
            tags = listOf("رقص", "فرح", "وناسة", "طرب", "dance", "victory")
        ),
        GifItem(
            id = "g_trend_5",
            title = "قلوب متحركة ومحبة",
            previewIcon = "💖",
            description = "قلوب وردية متطايرة",
            category = "الرائجة 🔥",
            gifUrl = "https://media.giphy.com/media/l4pTdcifPZLpDjL1e/giphy.gif",
            tags = listOf("حب", "قلب", "روحي", "مودة", "love", "hearts")
        ),
        GifItem(
            id = "g_trend_6",
            title = "نار وحماس مشتعل",
            previewIcon = "🔥",
            description = "شعلة نار تدل على القوة والنشاط",
            category = "الرائجة 🔥",
            gifUrl = "https://media.giphy.com/media/3o72FfM5HJydzafgUE/giphy.gif",
            tags = listOf("نار", "حماس", "جامد", "وحش", "fire", "hype")
        ),

        // ردود أفعال
        GifItem(
            id = "g_react_1",
            title = "مصدوم ومنبهر",
            previewIcon = "😲",
            description = "عيون متسعة من الصدمة والانبهار",
            category = "ردود أفعال 😂",
            gifUrl = "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif",
            tags = listOf("صدمة", "واو", "مستحيل", "مفاجأة", "shock", "wow")
        ),
        GifItem(
            id = "g_react_2",
            title = "غمزة ذكية",
            previewIcon = "😉",
            description = "غمزة مرحة وفاهم اللعبة",
            category = "ردود أفعال 😂",
            gifUrl = "https://media.giphy.com/media/6ra84Uso2hoir3YCgb/giphy.gif",
            tags = listOf("غمزة", "ذكي", "فاهم", "wink", "smart")
        ),
        GifItem(
            id = "g_react_3",
            title = "شك وتفكير عميق",
            previewIcon = "🤔",
            description = "يد على الذقن وتفكير جدي",
            category = "ردود أفعال 😂",
            gifUrl = "https://media.giphy.com/media/a5viI92PAF89q/giphy.gif",
            tags = listOf("تفكير", "شك", "سؤال", "همم", "think", "hmm")
        ),
        GifItem(
            id = "g_react_4",
            title = "لا وألف لا",
            previewIcon = "🙅‍♂️",
            description = "هز الرأس بالرفض التام",
            category = "ردود أفعال 😂",
            gifUrl = "https://media.giphy.com/media/3o7qDDNLf1TCfsCTyU/giphy.gif",
            tags = listOf("لا", "مستحيل", "ارفض", "no", "never")
        ),
        GifItem(
            id = "g_react_5",
            title = "بكاء تمثيلي لطيف",
            previewIcon = "😭",
            description = "دموع كثيفة متساقطة",
            category = "ردود أفعال 😂",
            gifUrl = "https://media.giphy.com/media/OPU6wzx8JrHna/giphy.gif",
            tags = listOf("بكاء", "حزن", "دموع", "cry", "sad")
        ),

        // حب ومودة
        GifItem(
            id = "g_love_1",
            title = "عناق دافئ كرتوني",
            previewIcon = "🤗",
            description = "شخصيتان تتعانقان بحرارة",
            category = "حب ومودة ❤️",
            gifUrl = "https://media.giphy.com/media/od5H3PmEG5EVq/giphy.gif",
            tags = listOf("حضن", "عناق", "حبيبي", "hug", "warm")
        ),
        GifItem(
            id = "g_love_2",
            title = "قبلة هوائية",
            previewIcon = "😘",
            description = "قبلة تطير مع قلوب حمراء",
            category = "حب ومودة ❤️",
            gifUrl = "https://media.giphy.com/media/l4pTdcifPZLpDjL1e/giphy.gif",
            tags = listOf("بوسة", "قبلة", "حب", "kiss", "love")
        ),
        GifItem(
            id = "g_love_3",
            title = "وردة حمراء رومانسية",
            previewIcon = "🌹",
            description = "تفتح زهرة ورد حمراء أنيقة",
            category = "حب ومودة ❤️",
            gifUrl = "https://media.giphy.com/media/3o7TKoWXm3okO1kgHC/giphy.gif",
            tags = listOf("ورد", "زهرة", "جمال", "rose", "flower")
        ),
        GifItem(
            id = "g_love_4",
            title = "عيون تلمع بالحب",
            previewIcon = "😍",
            description = "نظرات عشق وإعجاب كبير",
            category = "حب ومودة ❤️",
            gifUrl = "https://media.giphy.com/media/26BRv0ThflsHCqDrG/giphy.gif",
            tags = listOf("عشق", "إعجاب", "حب", "crush", "in love")
        ),

        // تشجيع وحماس
        GifItem(
            id = "g_hype_1",
            title = "كأس الذهب والبطولة",
            previewIcon = "🏆",
            description = "رفع كأس الفوز الذهبي",
            category = "تشجيع وحماس 👏",
            gifUrl = "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif",
            tags = listOf("كأس", "بطولة", "فوز", "أول", "trophy", "win")
        ),
        GifItem(
            id = "g_hype_2",
            title = "قبضة الحماس القوية",
            previewIcon = "✊",
            description = "قبضة يد مع وميض القوة والاصرار",
            category = "تشجيع وحماس 👏",
            gifUrl = "https://media.giphy.com/media/3o72FfM5HJydzafgUE/giphy.gif",
            tags = listOf("حماس", "قوة", "كفو", "بطل", "power", "fist")
        ),
        GifItem(
            id = "g_hype_3",
            title = "أنت الرقم واحد",
            previewIcon = "🥇",
            description = "إشارة بالأصبع الأول للمركز الأول",
            category = "تشجيع وحماس 👏",
            gifUrl = "https://media.giphy.com/media/l3q2XhfQ8oCkm1GhO/giphy.gif",
            tags = listOf("رقم واحد", "الاول", "ميدالية", "number one")
        ),

        // تهاني واحتفال
        GifItem(
            id = "g_celeb_1",
            title = "ألعاب نارية براقة",
            previewIcon = "🎆",
            description = "انفجار ألوان وألعاب نارية في السماء",
            category = "تهاني واحتفال 🎉",
            gifUrl = "https://media.giphy.com/media/26tOZ42Mg6pbTUPHW/giphy.gif",
            tags = listOf("احتفال", "عيد", "مبروك", "العاب نارية", "fireworks")
        ),
        GifItem(
            id = "g_celeb_2",
            title = "كعكة عيد ميلاد وشموع",
            previewIcon = "🎂",
            description = "كعكة مضاءة بالشموع الاحتفالية",
            category = "تهاني واحتفال 🎉",
            gifUrl = "https://media.giphy.com/media/YPIrsRqqO7oB2/giphy.gif",
            tags = listOf("عيد ميلاد", "كعكة", "سنة حلوة", "birthday", "cake")
        ),
        GifItem(
            id = "g_celeb_3",
            title = "كونفيتي وقصاصات ملونة",
            previewIcon = "🎊",
            description = "تساقط أوراق وشرائط الاحتفال الملونة",
            category = "تهاني واحتفال 🎉",
            gifUrl = "https://media.giphy.com/media/l41JRsph73VokN6ik/giphy.gif",
            tags = listOf("مبروك", "تهنئة", "نجاح", "تخرج", "congrats")
        ),

        // صباح ومساء
        GifItem(
            id = "g_day_1",
            title = "فنجان قهوة ساخن يتبخر",
            previewIcon = "☕",
            description = "صباح الخير مع قهوة تركية لذيذة",
            category = "صباح ومساء ☕",
            gifUrl = "https://media.giphy.com/media/3oriO04qxVReM5rJEA/giphy.gif",
            tags = listOf("قهوة", "صباح الخير", "رايق", "كوفي", "coffee", "morning")
        ),
        GifItem(
            id = "g_day_2",
            title = "شروق الشمس الساطع",
            previewIcon = "🌅",
            description = "شمس الصباح تشرق بنور الأمل",
            category = "صباح ومساء ☕",
            gifUrl = "https://media.giphy.com/media/3o6Zt481isNVuQI1l6/giphy.gif",
            tags = listOf("صباح", "شروق", "نور", "sunrise", "good morning")
        ),
        GifItem(
            id = "g_day_3",
            title = "قمر مضيء ونجوم ليلية",
            previewIcon = "🌙",
            description = "مساء الخير ونوم هنيء تحت القمر",
            category = "صباح ومساء ☕",
            gifUrl = "https://media.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif",
            tags = listOf("مساء الخير", "قمر", "نوم", "تصبح على خير", "moon", "night")
        ),

        // قطط وميمز
        GifItem(
            id = "g_cat_1",
            title = "قطة تعزف على البيانو",
            previewIcon = "🐱",
            description = "ميم القطة الشهيرة تعزف بإتقان",
            category = "قطط وميمز 🐱",
            gifUrl = "https://media.giphy.com/media/JIX9t2j0ZTN9S/giphy.gif",
            tags = listOf("قطة", "بسة", "بيانو", "ميمز", "cat", "piano")
        ),
        GifItem(
            id = "g_cat_2",
            title = "قطة مصدومة ببرائة",
            previewIcon = "😺",
            description = "قطة تفتح عيونها الكبيرة في دهشة",
            category = "قطط وميمز 🐱",
            gifUrl = "https://media.giphy.com/media/mlvseq9yvZhba/giphy.gif",
            tags = listOf("بسة", "كيوت", "صدمة", "لطيف", "cute cat")
        ),
        GifItem(
            id = "g_cat_3",
            title = "كلب يهز ذيله فرحاً",
            previewIcon = "🐶",
            description = "جرو صغير يقفز من السعادة",
            category = "قطط وميمز 🐱",
            gifUrl = "https://media.giphy.com/media/bbshzgyFQDqPHXBo4c/giphy.gif",
            tags = listOf("كلب", "جرو", "وفاء", "dog", "puppy")
        ),

        // سلام وتحية
        GifItem(
            id = "g_greet_1",
            title = "مصافحة واتفاق تام",
            previewIcon = "🤝",
            description = "مصافحة رجال أعمال بكل ثقة",
            category = "سلام وتحية 🤝",
            gifUrl = "https://media.giphy.com/media/26u4cqiYI30juCOGY/giphy.gif",
            tags = listOf("سلام", "مصافحة", "اتفقنا", "handshake", "deal")
        ),
        GifItem(
            id = "g_greet_2",
            title = "تحية ولوح باليد أهلاً",
            previewIcon = "👋",
            description = "تلويح باليد للترحيب أو الوداع",
            category = "سلام وتحية 🤝",
            gifUrl = "https://media.giphy.com/media/ASd0Ukj0BCcGqGOGep/giphy.gif",
            tags = listOf("هلا", "مرحبا", "سلام عليكم", "مع السلامة", "wave", "hello")
        )
    )

    fun getByCategory(cat: String): List<GifItem> {
        if (cat.contains("الرائجة")) return allGifs
        return allGifs.filter { it.category == cat }
    }

    fun searchGifs(query: String): List<GifItem> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return allGifs
        return allGifs.filter { item ->
            item.title.lowercase().contains(q) ||
            item.description.lowercase().contains(q) ||
            item.tags.any { it.lowercase().contains(q) } ||
            item.category.lowercase().contains(q)
        }
    }
}
