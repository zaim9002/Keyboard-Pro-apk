package com.example.language.pack

import com.example.ime.layout.KeyModel
import com.example.ime.layout.KeyType

object ArabicLanguagePack {

    val tashkeelSymbols = listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ", "ٰ", "ـ")
    val arabicNumerals = listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠")
    val englishNumerals = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val arabicPunctuation = listOf("،", "؟", "؛", "!", ".", ":", "«", "»", "(", ")", "—", "…")

    val row1 = listOf(
        KeyModel("ض", secondaryText = "١", popupOptions = listOf("١", "1")),
        KeyModel("ص", secondaryText = "٢", popupOptions = listOf("٢", "2")),
        KeyModel("ث", secondaryText = "٣", popupOptions = listOf("٣", "3")),
        KeyModel("ق", secondaryText = "٤", popupOptions = listOf("٤", "4")),
        KeyModel("ف", secondaryText = "٥", popupOptions = listOf("٥", "5", "ڤ")),
        KeyModel("غ", secondaryText = "٦", popupOptions = listOf("٦", "6")),
        KeyModel("ع", secondaryText = "٧", popupOptions = listOf("٧", "7")),
        KeyModel("ه", secondaryText = "٨", popupOptions = listOf("٨", "8", "ة", "هـ")),
        KeyModel("خ", secondaryText = "٩", popupOptions = listOf("٩", "9")),
        KeyModel("ح", secondaryText = "٠", popupOptions = listOf("٠", "0")),
        KeyModel("ج", popupOptions = listOf("چ")),
        KeyModel("د", popupOptions = listOf("ذ"))
    )

    val row2 = listOf(
        KeyModel("ش"),
        KeyModel("س"),
        KeyModel("ي", popupOptions = listOf("ى", "ئ", "ي")),
        KeyModel("ب", popupOptions = listOf("پ")),
        KeyModel("ل", popupOptions = listOf("لا", "لأ", "لإ", "لآ")),
        KeyModel("ا", popupOptions = listOf("أ", "إ", "آ", "ء", "ٱ")),
        KeyModel("ت", popupOptions = listOf("ة", "ث")),
        KeyModel("ن"),
        KeyModel("م"),
        KeyModel("ك", popupOptions = listOf("گ", "ڨ")),
        KeyModel("ط", popupOptions = listOf("ظ"))
    )

    val row3 = listOf(
        KeyModel("تشكيل", type = KeyType.TASHKEEL, weight = 1.3f),
        KeyModel("ئ"),
        KeyModel("ء"),
        KeyModel("ؤ"),
        KeyModel("ر", popupOptions = listOf("ز", "ژ")),
        KeyModel("ى", popupOptions = listOf("ي")),
        KeyModel("ة", popupOptions = listOf("ه")),
        KeyModel("و", popupOptions = listOf("ؤ")),
        KeyModel("ز", popupOptions = listOf("ژ")),
        KeyModel("ظ"),
        KeyModel("حذف", type = KeyType.BACKSPACE, weight = 1.3f)
    )

    // Shift row for Arabic containing extended symbols and letters
    val row1Shift = listOf(
        KeyModel("َ", popupOptions = listOf("ً")),
        KeyModel("ً"),
        KeyModel("ُ", popupOptions = listOf("ٌ")),
        KeyModel("ٌ"),
        KeyModel("لإ"),
        KeyModel("إ"),
        KeyModel("‘"),
        KeyModel("÷"),
        KeyModel("×"),
        KeyModel("؛"),
        KeyModel("<"),
        KeyModel(">")
    )

    val row2Shift = listOf(
        KeyModel("ِ", popupOptions = listOf("ٍ")),
        KeyModel("ٍ"),
        KeyModel("]"),
        KeyModel("["),
        KeyModel("لأ"),
        KeyModel("أ"),
        KeyModel("ـ"),
        KeyModel("،"),
        KeyModel("/"),
        KeyModel(":"),
        KeyModel("\"")
    )

    val row3Shift = listOf(
        KeyModel("تشكيل", type = KeyType.TASHKEEL, weight = 1.3f),
        KeyModel("ْ"),
        KeyModel("ّ"),
        KeyModel("آ"),
        KeyModel("لآ"),
        KeyModel("،"),
        KeyModel("؟"),
        KeyModel("!"),
        KeyModel("«"),
        KeyModel("»"),
        KeyModel("حذف", type = KeyType.BACKSPACE, weight = 1.3f)
    )

    // Rich Arabic dictionary for autocomplete and correction (Standard + Colloquial)
    val richVocabulary = listOf(
        "السلام", "عليكم", "ورحمة", "الله", "وبركاته", "شكراً", "جزيلاً", "مرحباً",
        "صباح", "الخير", "مساء", "النور", "كيف", "حالك", "حالكم", "الحمد", "لله",
        "سبحان", "أستغفر", "جزاك", "خيراً", "بارك", "فيك", "أهلاً", "وسهلاً", "إن",
        "شاء", "ما", "جميل", "رائع", "ممتاز", "أنا", "أنت", "نحن", "هو", "هي", "هم",
        "هذا", "هذه", "ذلك", "تلك", "هؤلاء", "نعم", "لا", "ربما", "حسناً", "تمام",
        "أكيد", "بالتأكيد", "طبعاً", "اليوم", "غداً", "أمس", "الآن", "قريباً", "دائماً",
        "أبداً", "سعيد", "فرحان", "مبارك", "تهانينا", "مبروك", "بالتوفيق", "النجاح",
        "رسالة", "مكالمة", "تطبيق", "هاتف", "صورة", "فيديو", "ملف", "رابط", "عمل",
        "دراسة", "جامعة", "مدرسة", "بيت", "طريق", "سيارة", "سفر", "أحبك", "صديقي",
        "أخي", "أختي", "عزيزي", "أستاذ", "مهندس", "دكتور", "أرجو", "أتمنى", "يمكنك",
        "مساعدة", "خدمة", "سؤال", "استفسار", "عفواً", "معذرة", "آسف", "أعتذر", "حقك",
        "علي", "بسيطة", "ولا يهمك", "حبيبي", "يا غالي", "الله يسعدك", "الله يحفظك",
        "تسلم", "يعطيك العافية", "في أمان الله", "مع السلامة", "إلى اللقاء", "بخير",
        "الحمدلله", "أشوفك", "على خير", "إن شاء الله", "بإذن الله", "كل عام وأنتم بخير",
        "رمضان كريم", "عيد مبارك", "تقبل الله", "طمني عنك", "وينك", "أهلاً بك",
        "ما تقصر", "كفو", "أبشر", "من عيوني", "يا هلا", "حياك الله", "منور", "الله يبارك"
    )
}
