package com.example.engine

data class GifItem(
    val id: String,
    val title: String,
    val previewIcon: String,
    val description: String,
    val category: String
)

object GifData {

    val categories = listOf("الرائجة", "تفاعلات", "ضحك", "تهاني", "حب", "تشجيع")

    val allGifs = listOf(
        GifItem("g1", "تصفيق حار", "👏", "تصفيق وإعجاب شديد", "تفاعلات"),
        GifItem("g2", "ضحك من القلب", "😂", "ضحك هستيري ومرح", "ضحك"),
        GifItem("g3", "رقصة النصر", "🕺", "احتفال بالفوز والإنجاز", "تهاني"),
        GifItem("g4", "قلوب متطايرة", "💖", "محبة وامتنان", "حب"),
        GifItem("g5", "إبهام للأعلى", "👍", "موافقة وتأكيد رائع", "تفاعلات"),
        GifItem("g6", "نار وحماس", "🔥", "حماس وطاقة هائلة", "تشجيع"),
        GifItem("g7", "واو مدهش", "😲", "انبهار تام ومفاجأة", "تفاعلات"),
        GifItem("g8", "حفلة وألعاب نارية", "🎆", "ألعاب نارية واحتفال", "تهاني"),
        GifItem("g9", "عناق دافئ", "🤗", "عناق ومودة للأصدقاء", "حب"),
        GifItem("g10", "كوب قهوة دافئ", "☕", "صباح رايق واسترخاء", "الرائجة"),
        GifItem("g11", "سقوط من الضحك", "🤣", "ضحك حتى البكاء", "ضحك"),
        GifItem("g12", "كأس البطولة", "🏆", "المركز الأول والنجاح", "تشجيع")
    )

    fun getByCategory(cat: String): List<GifItem> {
        if (cat == "الرائجة") return allGifs
        return allGifs.filter { it.category == cat }
    }
}
