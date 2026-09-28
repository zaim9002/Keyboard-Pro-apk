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
        "😂 ضحك",
        "🤣 مضحك",
        "❤️ حب",
        "😍 إعجاب",
        "😘 قبلات",
        "😢 حزن",
        "😭 بكاء",
        "😡 غضب",
        "😱 صدمة",
        "👏 تصفيق",
        "🔥 حماس",
        "🎉 احتفال",
        "👍 موافقة",
        "👎 رفض",
        "🤔 تفكير",
        "😎 فخم",
        "🤣 مواقف",
        "🙄 ملل",
        "🙏 شكر",
        "💔 فراق",
        "🥰 لطيف",
        "🤯 صدمة",
        "💀 ميمز",
        "🐱 قطط",
        "🐶 كلاب",
        "⚽ رياضة",
        "🎮 ألعاب",
        "🎬 أفلام",
        "📱 تقنية",
        "🎂 أعياد ميلاد",
        "🌹 رومانسي",
        "💯 ردود فعل"
    )

    val allGifs = listOf(
        // 😂 ضحك
        GifItem(
            id = "g_laugh_1",
            title = "ضحك هستيري متواصل",
            previewIcon = "😂",
            description = "ضحك شديد متواصل",
            category = "😂 ضحك",
            gifUrl = "https://media.giphy.com/media/10JhviFuU2gWD6/giphy.gif",
            tags = listOf("ضحك", "ههههه", "فطس", "كركرة", "laugh", "lol", "funny", "joy")
        ),
        GifItem(
            id = "g_laugh_2",
            title = "ضحك قطة لطيفة",
            previewIcon = "🐱",
            description = "قطة تضحك بحركات مضحكة",
            category = "😂 ضحك",
            gifUrl = "https://media.giphy.com/media/JIX9t2j0ZTN9S/giphy.gif",
            tags = listOf("ضحك", "قطة", "مياو", "cat", "laugh", "cute")
        ),
        GifItem(
            id = "g_laugh_3",
            title = "ضحك طفل بريء",
            previewIcon = "👶",
            description = "طفل صغير يضحك من قلبه",
            category = "😂 ضحك",
            gifUrl = "https://media.giphy.com/media/9u1J84ZtCSlys/giphy.gif",
            tags = listOf("طفل", "ضحكة", "براءة", "baby", "laugh", "giggle")
        ),

        // 🤣 مضحك
        GifItem(
            id = "g_funny_1",
            title = "انفجار من الضحك",
            previewIcon = "🤣",
            description = "سقوط على الأرض من شدة الضحك",
            category = "🤣 مضحك",
            gifUrl = "https://media.giphy.com/media/26n6Gx9moCgs1qxxt/giphy.gif",
            tags = listOf("مضحك", "نكتة", "فصلة", "rofl", "lmao", "hilarious")
        ),
        GifItem(
            id = "g_funny_2",
            title = "رقصة الموز الكوميدية",
            previewIcon = "🍌",
            description = "موزة ترقص بطريقة طريفة",
            category = "🤣 مضحك",
            gifUrl = "https://media.giphy.com/media/IB9foBA4PVAGI/giphy.gif",
            tags = listOf("موز", "رقص", "طريف", "banana", "dance", "meme")
        ),

        // ❤️ حب
        GifItem(
            id = "g_love_1",
            title = "قلوب متحركة مشعة",
            previewIcon = "💖",
            description = "قلوب متطايرة مليئة بالحب",
            category = "❤️ حب",
            gifUrl = "https://media.giphy.com/media/l4pTdcifPZLpDjL1e/giphy.gif",
            tags = listOf("حب", "قلب", "روحي", "مودة", "عشق", "love", "heart", "hearts")
        ),
        GifItem(
            id = "g_love_2",
            title = "عناق دافئ",
            previewIcon = "🤗",
            description = "شخصيتان تتعانقان بحرارة ومحبة",
            category = "❤️ حب",
            gifUrl = "https://media.giphy.com/media/od5H3PmEG5EVq/giphy.gif",
            tags = listOf("حضن", "عناق", "حبيبي", "دفء", "hug", "cuddle", "love")
        ),

        // 😍 إعجاب
        GifItem(
            id = "g_admire_1",
            title = "عيون تلمع بالقلوب",
            previewIcon = "😍",
            description = "نظرات إعجاب وانبهار بالجمال",
            category = "😍 إعجاب",
            gifUrl = "https://media.giphy.com/media/26BRv0ThflsHCqDrG/giphy.gif",
            tags = listOf("إعجاب", "عيون", "خقق", "جميل", "admire", "crush", "gorgeous")
        ),
        GifItem(
            id = "g_admire_2",
            title = "انبهار تام وجمال",
            previewIcon = "✨",
            description = "تألق وإبهار غير مسبوق",
            category = "😍 إعجاب",
            gifUrl = "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif",
            tags = listOf("روعة", "فخامة", "مبهر", "amazing", "stunning")
        ),

        // 😘 قبلات
        GifItem(
            id = "g_kiss_1",
            title = "قبلة هوائية طائرة",
            previewIcon = "😘",
            description = "قبلة مع قلب أحمر متطاير",
            category = "😘 قبلات",
            gifUrl = "https://media.giphy.com/media/3o7TKoWXm3okO1kgHC/giphy.gif",
            tags = listOf("بوسة", "قبلة", "مواااه", "kiss", "blow kiss", "love")
        ),

        // 😢 حزن
        GifItem(
            id = "g_sad_1",
            title = "حزن ودمعة خفيفة",
            previewIcon = "😢",
            description = "نظرة حزينة ودمعة تنزل بهدوء",
            category = "😢 حزن",
            gifUrl = "https://media.giphy.com/media/L95W4wv8nnb9K/giphy.gif",
            tags = listOf("حزن", "دمعة", "زعل", "مقهور", "sad", "tear", "lonely")
        ),

        // 😭 بكاء
        GifItem(
            id = "g_cry_1",
            title = "بكاء شديد وانهيار",
            previewIcon = "😭",
            description = "شلالات دموع من شدة التأثر",
            category = "😭 بكاء",
            gifUrl = "https://media.giphy.com/media/OPU6wzx8JrHna/giphy.gif",
            tags = listOf("بكاء", "صياح", "دموع", "cry", "crying", "sob", "bawl")
        ),

        // 😡 غضب
        GifItem(
            id = "g_angry_1",
            title = "غضب عارم ودخان",
            previewIcon = "😡",
            description = "وجه غاضب يشتعل حرارة",
            category = "😡 غضب",
            gifUrl = "https://media.giphy.com/media/l1J9u3TZfpmeDLkD6/giphy.gif",
            tags = listOf("غضب", "عصبية", "معصب", "نار", "angry", "rage", "mad")
        ),

        // 😱 صدمة
        GifItem(
            id = "g_shock_1",
            title = "صدمة وشهقة قوية",
            previewIcon = "😱",
            description = "عيون جاحظة من شدة المفاجأة",
            category = "😱 صدمة",
            gifUrl = "https://media.giphy.com/media/51Uiuy5QBZNkoF3b2Z/giphy.gif",
            tags = listOf("صدمة", "مفاجأة", "واو", "مستحيل", "shock", "gasp", "surprised")
        ),

        // 👏 تصفيق
        GifItem(
            id = "g_clap_1",
            title = "تصفيق حار ومستمر",
            previewIcon = "👏",
            description = "تصفيق احترافي وإشادة بالنجاح",
            category = "👏 تصفيق",
            gifUrl = "https://media.giphy.com/media/l3q2XhfQ8oCkm1GhO/giphy.gif",
            tags = listOf("تصفيق", "برافو", "تحية", "أحسنت", "clap", "applause", "bravo")
        ),

        // 🔥 حماس
        GifItem(
            id = "g_hype_1",
            title = "شعلة نار وقوة",
            previewIcon = "🔥",
            description = "طاقة نارية وحماس عالي",
            category = "🔥 حماس",
            gifUrl = "https://media.giphy.com/media/3o72FfM5HJydzafgUE/giphy.gif",
            tags = listOf("نار", "حماس", "ولعت", "قوة", "hype", "fire", "energy")
        ),

        // 🎉 احتفال
        GifItem(
            id = "g_party_1",
            title = "قصاصات وأفراح",
            previewIcon = "🎉",
            description = "أجواء احتفالية وكونفيتي",
            category = "🎉 احتفال",
            gifUrl = "https://media.giphy.com/media/26tOZ42Mg6pbTUPHW/giphy.gif",
            tags = listOf("احتفال", "فرح", "مبروك", "party", "celebration", "confetti")
        ),

        // 👍 موافقة
        GifItem(
            id = "g_yes_1",
            title = "إبهام للأعلى موافق",
            previewIcon = "👍",
            description = "إشارة تمام والموافقة التامة",
            category = "👍 موافقة",
            gifUrl = "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif",
            tags = listOf("تمام", "اوك", "موافق", "تم", "yes", "agree", "thumbs up")
        ),

        // 👎 رفض
        GifItem(
            id = "g_no_1",
            title = "هز الرأس بالرفض",
            previewIcon = "👎",
            description = "رفض قاطع ولا يمكن الموافقة",
            category = "👎 رفض",
            gifUrl = "https://media.giphy.com/media/3o7qDDNLf1TCfsCTyU/giphy.gif",
            tags = listOf("لا", "ارفض", "ممنوع", "كلا", "no", "disagree", "reject", "nope")
        ),

        // 🤔 تفكير
        GifItem(
            id = "g_think_1",
            title = "تفكير وتحليل عميق",
            previewIcon = "🤔",
            description = "يد على الذقن وتأمل في الفكرة",
            category = "🤔 تفكير",
            gifUrl = "https://media.giphy.com/media/a5viI92PAF89q/giphy.gif",
            tags = listOf("تفكير", "همم", "سؤال", "ذكاء", "think", "thinking", "hmm", "ponder")
        ),

        // 😎 فخم
        GifItem(
            id = "g_cool_1",
            title = "نظارات شمسية وفخامة",
            previewIcon = "😎",
            description = "شياكة وفخامة وهدوء وثقة",
            category = "😎 فخم",
            gifUrl = "https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif",
            tags = listOf("فخم", "كشخة", "هيبة", "ثقة", "cool", "boss", "swag")
        ),

        // 🤣 مواقف
        GifItem(
            id = "g_situ_1",
            title = "موقف كوميدي غير متوقع",
            previewIcon = "🎭",
            description = "موقف طريف مضحك يثير الضحك",
            category = "🤣 مواقف",
            gifUrl = "https://media.giphy.com/media/3o6ozvv0zsWUPAbHgY/giphy.gif",
            tags = listOf("موقف", "مقالب", "طرفة", "situation", "comedy", "funny")
        ),

        // 🙄 ملل
        GifItem(
            id = "g_bored_1",
            title = "قلب العيون من الملل",
            previewIcon = "🙄",
            description = "نظرة ملل وانتظار طويل",
            category = "🙄 ملل",
            gifUrl = "https://media.giphy.com/media/3o7bu3XilJ5BOiSGic/giphy.gif",
            tags = listOf("ملل", "زهق", "طفش", "bored", "eyeroll", "whatever")
        ),

        // 🙏 شكر
        GifItem(
            id = "g_thanks_1",
            title = "شكر وامتنان ودعاء",
            previewIcon = "🙏",
            description = "يدان مرفوعتان بالشكر والامتنان",
            category = "🙏 شكر",
            gifUrl = "https://media.giphy.com/media/osjgQPWRx3cac/giphy.gif",
            tags = listOf("شكر", "تسلم", "جزاك الله خير", "مشكور", "thanks", "thank you", "grateful")
        ),

        // 💔 فراق
        GifItem(
            id = "g_heartbreak_1",
            title = "قلب مكسور وحسرة",
            previewIcon = "💔",
            description = "انقسام القلب إلى نصفين",
            category = "💔 فراق",
            gifUrl = "https://media.giphy.com/media/l2Je2M4Nfrit0L7sQ/giphy.gif",
            tags = listOf("فراق", "كسر", "وداع", "حسرة", "heartbroken", "sad", "broken")
        ),

        // 🥰 لطيف
        GifItem(
            id = "g_cute_1",
            title = "كائن لطيف وخدود وردية",
            previewIcon = "🥰",
            description = "لطافة متناهية وسعادة",
            category = "🥰 لطيف",
            gifUrl = "https://media.giphy.com/media/MDJ9IbxxvDUQM/giphy.gif",
            tags = listOf("كيوت", "لطيف", "حلو", "طيبة", "cute", "adorable", "kawaii")
        ),

        // 🤯 صدمة
        GifItem(
            id = "g_mindblow_1",
            title = "انفجار العقل من المفاجأة",
            previewIcon = "🤯",
            description = "صدمة فكرية وانفجار في المخ",
            category = "🤯 صدمة",
            gifUrl = "https://media.giphy.com/media/26ufdipQqU2lhNA4g/giphy.gif",
            tags = listOf("مستحيل", "صدمة", "انفجار", "mindblown", "insane", "genius")
        ),

        // 💀 ميمز
        GifItem(
            id = "g_meme_1",
            title = "ميمز شهيرة وطريفة",
            previewIcon = "💀",
            description = "ضحك حتى الموت والميمز الحديثة",
            category = "💀 ميمز",
            gifUrl = "https://media.giphy.com/media/10JhviFuU2gWD6/giphy.gif",
            tags = listOf("ميمز", "ميت ضحك", "جمجمة", "meme", "dank", "dead")
        ),

        // 🐱 قطط
        GifItem(
            id = "g_cat_1",
            title = "قطة لطيفة تطرف بعينيها",
            previewIcon = "🐱",
            description = "قطة كرتونية لطيفة للغاية",
            category = "🐱 قطط",
            gifUrl = "https://media.giphy.com/media/JIX9t2j0ZTN9S/giphy.gif",
            tags = listOf("قطة", "بسة", "مياو", "هريرة", "cat", "kitty", "kitten")
        ),

        // 🐶 كلاب
        GifItem(
            id = "g_dog_1",
            title = "جرو وفي يهز ذيله",
            previewIcon = "🐶",
            description = "كلب صغير متحمس وسعيد",
            category = "🐶 كلاب",
            gifUrl = "https://media.giphy.com/media/bbshzgyFQDqPHXBo4c/giphy.gif",
            tags = listOf("كلب", "جرو", "وفاء", "dog", "puppy", "happy")
        ),

        // ⚽ رياضة
        GifItem(
            id = "g_sport_1",
            title = "هدف رائع واحتفال كروي",
            previewIcon = "⚽",
            description = "كرة في الشباك واحتفال جماهيري",
            category = "⚽ رياضة",
            gifUrl = "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif",
            tags = listOf("هدف", "كورة", "رياضة", "نادي", "goal", "soccer", "football", "sports")
        ),

        // 🎮 ألعاب
        GifItem(
            id = "g_game_1",
            title = "جيمر محترف وفوز كاسح",
            previewIcon = "🎮",
            description = "يد تحكم الألعاب والنصر التام",
            category = "🎮 ألعاب",
            gifUrl = "https://media.giphy.com/media/blSTtZehjAZ8I/giphy.gif",
            tags = listOf("العاب", "جيمينج", "فوز", "سوني", "gaming", "game", "victory")
        ),

        // 🎬 أفلام
        GifItem(
            id = "g_movie_1",
            title = "مشهد سينمائي درامي",
            previewIcon = "🎬",
            description = "لقطة كلاسيكية من السينما العالمية",
            category = "🎬 أفلام",
            gifUrl = "https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif",
            tags = listOf("فيلم", "سينما", "دراما", "بطل", "cinema", "movie", "film")
        ),

        // 📱 تقنية
        GifItem(
            id = "g_tech_1",
            title = "شفرات برمجية وهكر ذكي",
            previewIcon = "📱",
            description = "كود برمجي متدفق وسرعة كتابة",
            category = "📱 تقنية",
            gifUrl = "https://media.giphy.com/media/3o72FfM5HJydzafgUE/giphy.gif",
            tags = listOf("تقنية", "برمجة", "هاتف", "كمبيوتر", "tech", "code", "matrix")
        ),

        // 🎂 أعياد ميلاد
        GifItem(
            id = "g_birthday_1",
            title = "كعكة عيد الميلاد والشموع",
            previewIcon = "🎂",
            description = "كل عام وأنت بألف خير مع شمعة مضيئة",
            category = "🎂 أعياد ميلاد",
            gifUrl = "https://media.giphy.com/media/YPIrsRqqO7oB2/giphy.gif",
            tags = listOf("عيد ميلاد", "سنة حلوة", "تورتة", "birthday", "hbd", "cake")
        ),

        // 🌹 رومانسي
        GifItem(
            id = "g_romantic_1",
            title = "باقة ورود حمراء متألقة",
            previewIcon = "🌹",
            description = "ورود حمراء نضرة ورومانسية ساحرة",
            category = "🌹 رومانسي",
            gifUrl = "https://media.giphy.com/media/3o7TKoWXm3okO1kgHC/giphy.gif",
            tags = listOf("رومانسية", "ورد", "عشق", "شوق", "romantic", "rose", "flowers")
        ),

        // 💯 ردود فعل
        GifItem(
            id = "g_reaction_1",
            title = "مئة بالمئة صح وكامل",
            previewIcon = "💯",
            description = "علامة 100 تدل على التميز والموافقة المطلقة",
            category = "💯 ردود فعل",
            gifUrl = "https://media.giphy.com/media/l3q2XhfQ8oCkm1GhO/giphy.gif",
            tags = listOf("ردود فعل", "مية بالمية", "صح", "كفو", "100", "perfect", "facts")
        )
    )

    fun getByCategory(category: String): List<GifItem> {
        val direct = allGifs.filter { it.category == category }
        if (direct.isNotEmpty()) return direct
        val cleanCat = category.replace(Regex("[^\\p{L}\\p{Nd}]"), "").trim()
        return allGifs.filter { item ->
            val itemCatClean = item.category.replace(Regex("[^\\p{L}\\p{Nd}]"), "").trim()
            itemCatClean.contains(cleanCat) || cleanCat.contains(itemCatClean)
        }.ifEmpty { allGifs.take(6) }
    }

    fun searchGifs(query: String): List<GifItem> {
        if (query.isBlank()) return allGifs
        val q = query.trim().lowercase()
        return allGifs.filter { item ->
            item.title.lowercase().contains(q) ||
            item.description.lowercase().contains(q) ||
            item.category.lowercase().contains(q) ||
            item.tags.any { it.lowercase().contains(q) }
        }
    }
}
