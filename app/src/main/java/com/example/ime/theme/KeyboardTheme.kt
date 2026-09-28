package com.example.ime.theme

import androidx.compose.ui.graphics.Color
import com.example.data.pref.KeyboardPreferences

data class KeyboardColorScheme(
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val keyBackground: Color,
    val keyText: Color,
    val specialKeyBackground: Color,
    val specialKeyText: Color,
    val accent: Color,
    val suggestionBar: Color,
    val suggestionText: Color,
    val borderColor: Color = Color.Transparent,
    val backgroundImageUri: String? = null,
    val category: String = "لون"
)

object KeyboardThemes {

    // === 24 Official Color Themes from "سمة الألوان" (Screenshot 3) ===
    val Asasi = KeyboardColorScheme(
        name = "أساسي",
        isDark = true,
        background = Color(0xFF141921),
        keyBackground = Color(0xFF38465B),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF2A3647),
        specialKeyText = Color(0xFFFFFFFF),
        accent = Color(0xFF435670),
        suggestionBar = Color(0xFF141921),
        suggestionText = Color(0xFFE2E8F0),
        borderColor = Color(0x18FFFFFF),
        category = "لون"
    )

    val GhazalBanat = KeyboardColorScheme(
        name = "غزل بنات",
        isDark = false,
        background = Color(0xFFFDF2F8),
        keyBackground = Color(0xFFFCE7F3),
        keyText = Color(0xFF831843),
        specialKeyBackground = Color(0xFFFBCFE8),
        specialKeyText = Color(0xFF9D174D),
        accent = Color(0xFFF472B6),
        suggestionBar = Color(0xFFFDF2F8),
        suggestionText = Color(0xFF9D174D),
        borderColor = Color(0x33F472B6),
        category = "لون"
    )

    val Wardi = KeyboardColorScheme(
        name = "وردي",
        isDark = false,
        background = Color(0xFFFFF1F2),
        keyBackground = Color(0xFFFFE4E6),
        keyText = Color(0xFF9F1239),
        specialKeyBackground = Color(0xFFFECDD3),
        specialKeyText = Color(0xFFBE123C),
        accent = Color(0xFFFB7185),
        suggestionBar = Color(0xFFFFF1F2),
        suggestionText = Color(0xFF9F1239),
        borderColor = Color(0x33FB7185),
        category = "لون"
    )

    val Morjani = KeyboardColorScheme(
        name = "مرجاني",
        isDark = false,
        background = Color(0xFFFFF7ED),
        keyBackground = Color(0xFFFFEDD5),
        keyText = Color(0xFF9A3412),
        specialKeyBackground = Color(0xFFFED7AA),
        specialKeyText = Color(0xFFC2410C),
        accent = Color(0xFFFB923C),
        suggestionBar = Color(0xFFFFF7ED),
        suggestionText = Color(0xFF9A3412),
        borderColor = Color(0x33FB923C),
        category = "لون"
    )

    val Lavender = KeyboardColorScheme(
        name = "لافندر",
        isDark = false,
        background = Color(0xFFFAF5FF),
        keyBackground = Color(0xFFF3E8FF),
        keyText = Color(0xFF581C87),
        specialKeyBackground = Color(0xFFE9D5FF),
        specialKeyText = Color(0xFF6B21A8),
        accent = Color(0xFFC084FC),
        suggestionBar = Color(0xFFFAF5FF),
        suggestionText = Color(0xFF581C87),
        borderColor = Color(0x33C084FC),
        category = "لون"
    )

    val QaranfiliAsfar = KeyboardColorScheme(
        name = "قرنفلي أصفر",
        isDark = false,
        background = Color(0xFFFEF3C7),
        keyBackground = Color(0xFFFDE68A),
        keyText = Color(0xFF78350F),
        specialKeyBackground = Color(0xFFFCD34D),
        specialKeyText = Color(0xFF92400E),
        accent = Color(0xFFF59E0B),
        suggestionBar = Color(0xFFFEF3C7),
        suggestionText = Color(0xFF78350F),
        borderColor = Color(0x33F59E0B),
        category = "لون"
    )

    val AhmarMango = KeyboardColorScheme(
        name = "أحمر مانجو",
        isDark = true,
        background = Color(0xFF3B1506),
        keyBackground = Color(0xFF60210A),
        keyText = Color(0xFFFFEDD5),
        specialKeyBackground = Color(0xFF7C2D12),
        specialKeyText = Color(0xFFFB923C),
        accent = Color(0xFFF97316),
        suggestionBar = Color(0xFF3B1506),
        suggestionText = Color(0xFFFFEDD5),
        borderColor = Color(0x33F97316),
        category = "لون"
    )

    val Ahmar = KeyboardColorScheme(
        name = "أحمر",
        isDark = true,
        background = Color(0xFF350610),
        keyBackground = Color(0xFF5E0D21),
        keyText = Color(0xFFFFF1F2),
        specialKeyBackground = Color(0xFF801430),
        specialKeyText = Color(0xFFFB7185),
        accent = Color(0xFFF43F5E),
        suggestionBar = Color(0xFF350610),
        suggestionText = Color(0xFFFFF1F2),
        borderColor = Color(0x33F43F5E),
        category = "لون"
    )

    val Vanillia = KeyboardColorScheme(
        name = "فانيليا",
        isDark = false,
        background = Color(0xFFFEFCE8),
        keyBackground = Color(0xFFFEF9C3),
        keyText = Color(0xFF713F12),
        specialKeyBackground = Color(0xFFFEF08A),
        specialKeyText = Color(0xFF854D0E),
        accent = Color(0xFFEAB308),
        suggestionBar = Color(0xFFFEFCE8),
        suggestionText = Color(0xFF713F12),
        borderColor = Color(0x33EAB308),
        category = "لون"
    )

    val Enabi = KeyboardColorScheme(
        name = "عنبي",
        isDark = true,
        background = Color(0xFF1E0A3C),
        keyBackground = Color(0xFF341366),
        keyText = Color(0xFFFAF5FF),
        specialKeyBackground = Color(0xFF4C1D95),
        specialKeyText = Color(0xFFC084FC),
        accent = Color(0xFFA855F7),
        suggestionBar = Color(0xFF1E0A3C),
        suggestionText = Color(0xFFFAF5FF),
        borderColor = Color(0x33A855F7),
        category = "لون"
    )

    val Barghandi = KeyboardColorScheme(
        name = "برغندي",
        isDark = true,
        background = Color(0xFF2C0A15),
        keyBackground = Color(0xFF4A1224),
        keyText = Color(0xFFFFF1F2),
        specialKeyBackground = Color(0xFF6B1B36),
        specialKeyText = Color(0xFFFDA4AF),
        accent = Color(0xFFE11D48),
        suggestionBar = Color(0xFF2C0A15),
        suggestionText = Color(0xFFFFF1F2),
        borderColor = Color(0x33E11D48),
        category = "لون"
    )

    val Arjuwani = KeyboardColorScheme(
        name = "أرجواني",
        isDark = true,
        background = Color(0xFF3B0764),
        keyBackground = Color(0xFF581C87),
        keyText = Color(0xFFFDF4FF),
        specialKeyBackground = Color(0xFF701A75),
        specialKeyText = Color(0xFFE879F9),
        accent = Color(0xFFD946EF),
        suggestionBar = Color(0xFF3B0764),
        suggestionText = Color(0xFFFDF4FF),
        borderColor = Color(0x33D946EF),
        category = "لون"
    )

    val Bunni = KeyboardColorScheme(
        name = "بني",
        isDark = true,
        background = Color(0xFF23140D),
        keyBackground = Color(0xFF3D2318),
        keyText = Color(0xFFFEF3C7),
        specialKeyBackground = Color(0xFF563222),
        specialKeyText = Color(0xFFFBBF24),
        accent = Color(0xFFD97706),
        suggestionBar = Color(0xFF23140D),
        suggestionText = Color(0xFFFEF3C7),
        borderColor = Color(0x33D97706),
        category = "لون"
    )

    val Bortuqali = KeyboardColorScheme(
        name = "برتقالي",
        isDark = true,
        background = Color(0xFF331306),
        keyBackground = Color(0xFF5C230C),
        keyText = Color(0xFFFFEDD5),
        specialKeyBackground = Color(0xFF7C2D12),
        specialKeyText = Color(0xFFFB923C),
        accent = Color(0xFFEA580C),
        suggestionBar = Color(0xFF331306),
        suggestionText = Color(0xFFFFEDD5),
        borderColor = Color(0x33EA580C),
        category = "لون"
    )

    val Mostarda = KeyboardColorScheme(
        name = "مستردة",
        isDark = true,
        background = Color(0xFF301B05),
        keyBackground = Color(0xFF532F09),
        keyText = Color(0xFFFEFCE8),
        specialKeyBackground = Color(0xFF713F12),
        specialKeyText = Color(0xFFFDE047),
        accent = Color(0xFFCA8A04),
        suggestionBar = Color(0xFF301B05),
        suggestionText = Color(0xFFFEFCE8),
        borderColor = Color(0x33CA8A04),
        category = "لون"
    )

    val Moz = KeyboardColorScheme(
        name = "موز",
        isDark = true,
        background = Color(0xFF2E2204),
        keyBackground = Color(0xFF553F09),
        keyText = Color(0xFFFEF08A),
        specialKeyBackground = Color(0xFF71540C),
        specialKeyText = Color(0xFFFACC15),
        accent = Color(0xFFEAB308),
        suggestionBar = Color(0xFF2E2204),
        suggestionText = Color(0xFFFEF08A),
        borderColor = Color(0x33EAB308),
        category = "لون"
    )

    val AkhdarGreenland = KeyboardColorScheme(
        name = "أخضر جرينلاند",
        isDark = true,
        background = Color(0xFF04211A),
        keyBackground = Color(0xFF0A3B2E),
        keyText = Color(0xFFECFDF5),
        specialKeyBackground = Color(0xFF0F5241),
        specialKeyText = Color(0xFF34D399),
        accent = Color(0xFF10B981),
        suggestionBar = Color(0xFF04211A),
        suggestionText = Color(0xFFECFDF5),
        borderColor = Color(0x3310B981),
        category = "لون"
    )

    val Zomorodi = KeyboardColorScheme(
        name = "زمردي",
        isDark = true,
        background = Color(0xFF063327),
        keyBackground = Color(0xFF0C5642),
        keyText = Color(0xFFECFDF5),
        specialKeyBackground = Color(0xFF117359),
        specialKeyText = Color(0xFF6EE7B7),
        accent = Color(0xFF059669),
        suggestionBar = Color(0xFF063327),
        suggestionText = Color(0xFFECFDF5),
        borderColor = Color(0x33059669),
        category = "لون"
    )

    val Laymooni = KeyboardColorScheme(
        name = "ليموني",
        isDark = true,
        background = Color(0xFF152605),
        keyBackground = Color(0xFF2B460D),
        keyText = Color(0xFFF7FEE7),
        specialKeyBackground = Color(0xFF3C6114),
        specialKeyText = Color(0xFFA3E635),
        accent = Color(0xFF84CC16),
        suggestionBar = Color(0xFF152605),
        suggestionText = Color(0xFFF7FEE7),
        borderColor = Color(0x3384CC16),
        category = "لون"
    )

    val ChocolateNana = KeyboardColorScheme(
        name = "شوكولاتة نعناع",
        isDark = true,
        background = Color(0xFF161A19),
        keyBackground = Color(0xFF25332F),
        keyText = Color(0xFFF0FDFA),
        specialKeyBackground = Color(0xFF32453F),
        specialKeyText = Color(0xFF5EEAD4),
        accent = Color(0xFF2DD4BF),
        suggestionBar = Color(0xFF161A19),
        suggestionText = Color(0xFFF0FDFA),
        borderColor = Color(0x332DD4BF),
        category = "لون"
    )

    val Nani = KeyboardColorScheme(
        name = "نعناعي",
        isDark = true,
        background = Color(0xFF032625),
        keyBackground = Color(0xFF0B4644),
        keyText = Color(0xFFF0FDFA),
        specialKeyBackground = Color(0xFF11605E),
        specialKeyText = Color(0xFF2DD4BF),
        accent = Color(0xFF14B8A6),
        suggestionBar = Color(0xFF032625),
        suggestionText = Color(0xFFF0FDFA),
        borderColor = Color(0x3314B8A6),
        category = "لون"
    )

    val AzraqSilmi = KeyboardColorScheme(
        name = "الأزرق السلمي",
        isDark = true,
        background = Color(0xFF06253A),
        keyBackground = Color(0xFF0B4267),
        keyText = Color(0xFFF0F9FF),
        specialKeyBackground = Color(0xFF0F5A8C),
        specialKeyText = Color(0xFF7DD3FC),
        accent = Color(0xFF38BDF8),
        suggestionBar = Color(0xFF06253A),
        suggestionText = Color(0xFFF0F9FF),
        borderColor = Color(0x3338BDF8),
        category = "لون"
    )

    val AzraqDakin = KeyboardColorScheme(
        name = "أزرق داكن",
        isDark = true,
        background = Color(0xFF051026),
        keyBackground = Color(0xFF0E2250),
        keyText = Color(0xFFEFF6FF),
        specialKeyBackground = Color(0xFF153375),
        specialKeyText = Color(0xFF60A5FA),
        accent = Color(0xFF3B82F6),
        suggestionBar = Color(0xFF051026),
        suggestionText = Color(0xFFEFF6FF),
        borderColor = Color(0x333B82F6),
        category = "لون"
    )

    val ShayAkhdar = KeyboardColorScheme(
        name = "شاي أخضر",
        isDark = true,
        background = Color(0xFF131D16),
        keyBackground = Color(0xFF223528),
        keyText = Color(0xFFECFDF5),
        specialKeyBackground = Color(0xFF304838),
        specialKeyText = Color(0xFF86EFAC),
        accent = Color(0xFF4ADE80),
        suggestionBar = Color(0xFF131D16),
        suggestionText = Color(0xFFECFDF5),
        borderColor = Color(0x334ADE80),
        category = "لون"
    )

    // === Design Themes (Screenshot 2) ===
    val CrunchyAppleBite = KeyboardColorScheme(
        name = "Crunchy Apple Bite",
        isDark = false,
        background = Color(0xFFFFF7ED),
        keyBackground = Color(0xFFFFEDD5),
        keyText = Color(0xFF9A3412),
        specialKeyBackground = Color(0xFFFED7AA),
        specialKeyText = Color(0xFFC2410C),
        accent = Color(0xFFEA580C),
        suggestionBar = Color(0xFFFFF7ED),
        suggestionText = Color(0xFF9A3412),
        borderColor = Color(0x44EA580C),
        category = "تصميم"
    )

    val PuddingTime = KeyboardColorScheme(
        name = "Pudding time",
        isDark = false,
        background = Color(0xFFFEFCE8),
        keyBackground = Color(0xFFFEF08A),
        keyText = Color(0xFF713F12),
        specialKeyBackground = Color(0xFFFDE047),
        specialKeyText = Color(0xFF854D0E),
        accent = Color(0xFFCA8A04),
        suggestionBar = Color(0xFFFEFCE8),
        suggestionText = Color(0xFF713F12),
        borderColor = Color(0x44CA8A04),
        category = "تصميم"
    )

    val SuitcaseFullOfJoy = KeyboardColorScheme(
        name = "Suitcase Full of Joy",
        isDark = false,
        background = Color(0xFFF0F9FF),
        keyBackground = Color(0xFFE0F2FE),
        keyText = Color(0xFF0369A1),
        specialKeyBackground = Color(0xFFBAE6FD),
        specialKeyText = Color(0xFF0284C7),
        accent = Color(0xFF0284C7),
        suggestionBar = Color(0xFFF0F9FF),
        suggestionText = Color(0xFF0369A1),
        borderColor = Color(0x440284C7),
        category = "تصميم"
    )

    val ChihuahuasNeverHoldBack = KeyboardColorScheme(
        name = "Chihuahuas never hold back",
        isDark = false,
        background = Color(0xFFFDFBF7),
        keyBackground = Color(0xFFF5EBE1),
        keyText = Color(0xFF523624),
        specialKeyBackground = Color(0xFFEADBCE),
        specialKeyText = Color(0xFF6B4730),
        accent = Color(0xFF96684B),
        suggestionBar = Color(0xFFFDFBF7),
        suggestionText = Color(0xFF523624),
        borderColor = Color(0x4496684B),
        category = "تصميم"
    )

    val IceCreamSelection = KeyboardColorScheme(
        name = "Ice cream selection",
        isDark = false,
        background = Color(0xFFFDF4FF),
        keyBackground = Color(0xFFFAE8FF),
        keyText = Color(0xFF701A75),
        specialKeyBackground = Color(0xFFF5D0FE),
        specialKeyText = Color(0xFF86198F),
        accent = Color(0xFFD946EF),
        suggestionBar = Color(0xFFFDF4FF),
        suggestionText = Color(0xFF701A75),
        borderColor = Color(0x44D946EF),
        category = "تصميم"
    )

    val SummerForestFireflies = KeyboardColorScheme(
        name = "Summer forest fireflies",
        isDark = true,
        background = Color(0xFF0B1924),
        keyBackground = Color(0xFF162D3E),
        keyText = Color(0xFFFEF08A),
        specialKeyBackground = Color(0xFF1F4259),
        specialKeyText = Color(0xFFFACC15),
        accent = Color(0xFFFDE047),
        suggestionBar = Color(0xFF0B1924),
        suggestionText = Color(0xFFFEF08A),
        borderColor = Color(0x44FDE047),
        category = "تصميم"
    )

    // Master list of all available themes
    val allThemes = listOf(
        Asasi, GhazalBanat, Wardi, Morjani, Lavender, QaranfiliAsfar,
        AhmarMango, Ahmar, Vanillia, Enabi, Barghandi, Arjuwani,
        Bunni, Bortuqali, Mostarda, Moz, AkhdarGreenland, Zomorodi,
        Laymooni, ChocolateNana, Nani, AzraqSilmi, AzraqDakin, ShayAkhdar,
        CrunchyAppleBite, PuddingTime, SuitcaseFullOfJoy,
        ChihuahuasNeverHoldBack, IceCreamSelection, SummerForestFireflies
    )

    fun getTheme(name: String, prefs: KeyboardPreferences? = null): KeyboardColorScheme {
        return allThemes.find { it.name.equals(name, ignoreCase = true) }
            ?: Asasi
    }

    fun getColorScheme(name: String): KeyboardColorScheme {
        return getTheme(name)
    }
}
