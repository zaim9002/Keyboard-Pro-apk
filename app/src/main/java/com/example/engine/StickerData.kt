package com.example.engine

data class StickerPack(
    val name: String,
    val icon: String,
    val stickers: List<StickerItem>
)

data class StickerItem(
    val id: String,
    val text: String,
    val displayIcon: String,
    val packName: String
)

object StickerData {

    val packs = listOf(
        StickerPack(
            name = "تحيات وعبارات",
            icon = "🌸",
            stickers = listOf(
                StickerItem("s1", "أهلاً وسهلاً بكم 🌸", "🌸", "تحيات"),
                StickerItem("s2", "صباح الورد والياسمين ☀️", "☀️", "تحيات"),
                StickerItem("s3", "مساء الخير والأنوار 🌙", "🌙", "تحيات"),
                StickerItem("s4", "شكراً لك من القلب ❤️", "❤️", "تحيات"),
                StickerItem("s5", "جزاك الله كل خير 🤲", "🤲", "تحيات"),
                StickerItem("s6", "بالتوفيق والنجاح دائماً 🎓", "🎓", "تحيات"),
                StickerItem("s7", "في أمان الله وحفظه 🕊️", "🕊️", "تحيات"),
                StickerItem("s8", "جمعة مباركة طيبة 🕌", "🕌", "تحيات")
            )
        ),
        StickerPack(
            name = "تهاني وتبريكات",
            icon = "🎉",
            stickers = listOf(
                StickerItem("c1", "ألف ألف مبروك! 🎊", "🎊", "تهاني"),
                StickerItem("c2", "كل عام وأنتم بألف خير 🎈", "🎈", "تهاني"),
                StickerItem("c3", "عيد مبارك وسعيد 🌙", "🌙", "تهاني"),
                StickerItem("c4", "مبارك النجاح الباهر 🏆", "🏆", "تهاني"),
                StickerItem("c5", "بارك الله لكم وعليكم 💍", "💍", "تهاني"),
                StickerItem("c6", "تهانينا الحارة لكم 🥳", "🥳", "تهاني")
            )
        ),
        StickerPack(
            name = "تفاعلات سريعة",
            icon = "⚡",
            stickers = listOf(
                StickerItem("r1", "ممتاز جداً! استمر 👍", "👍", "تفاعلات"),
                StickerItem("r2", "عمل رائع ومتقن ⭐", "⭐", "تفاعلات"),
                StickerItem("r3", "إبداع لا مثيل له 🔥", "🔥", "تفاعلات"),
                StickerItem("r4", "أوافقك الرأي تماماً 💯", "💯", "تفاعلات"),
                StickerItem("r5", "فكرة عبقرية ومميزة 💡", "💡", "تفاعلات"),
                StickerItem("r6", "ههههههه أضحكتني جداً 😂", "😂", "تفاعلات")
            )
        ),
        StickerPack(
            name = "مشاعر ولطافة",
            icon = "🐱",
            stickers = listOf(
                StickerItem("k1", "أحبكم في الله 💖", "💖", "لطافة"),
                StickerItem("k2", "مع كل الود والاحترام 💐", "💐", "لطافة"),
                StickerItem("k3", "سلامي للجميع 🕊️", "🕊️", "لطافة"),
                StickerItem("k4", "كن بخير دائماً 🌱", "🌱", "لطافة"),
                StickerItem("k5", "دمت بود وسعادة 🌺", "🌺", "لطافة")
            )
        )
    )
}
