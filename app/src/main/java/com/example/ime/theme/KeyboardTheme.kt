package com.example.ime.theme

import androidx.compose.ui.graphics.Color

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
    val borderColor: Color = Color.Transparent
)

object KeyboardThemes {

    val GboardDark = KeyboardColorScheme(
        name = "Gboard Dark",
        isDark = true,
        background = Color(0xFF1F2125),
        keyBackground = Color(0xFF33353A),
        keyText = Color(0xFFE8EAED),
        specialKeyBackground = Color(0xFF282A2E),
        specialKeyText = Color(0xFFE8EAED),
        accent = Color(0xFF8AB4F8),
        suggestionBar = Color(0xFF1F2125),
        suggestionText = Color(0xFFE8EAED),
        borderColor = Color(0x15FFFFFF)
    )

    val GboardLight = KeyboardColorScheme(
        name = "Gboard Light",
        isDark = false,
        background = Color(0xFFECEFF1),
        keyBackground = Color(0xFFFFFFFF),
        keyText = Color(0xFF202124),
        specialKeyBackground = Color(0xFFDFE3E8),
        specialKeyText = Color(0xFF202124),
        accent = Color(0xFF1A73E8),
        suggestionBar = Color(0xFFECEFF1),
        suggestionText = Color(0xFF202124),
        borderColor = Color(0x18000000)
    )

    val SwiftKeyDark = KeyboardColorScheme(
        name = "SwiftKey Dark",
        isDark = true,
        background = Color(0xFF13171F),
        keyBackground = Color(0xFF242F3E),
        keyText = Color(0xFFF8FAFC),
        specialKeyBackground = Color(0xFF1A222E),
        specialKeyText = Color(0xFFF8FAFC),
        accent = Color(0xFF38BDF8),
        suggestionBar = Color(0xFF13171F),
        suggestionText = Color(0xFFE2E8F0),
        borderColor = Color(0x1AFFFFFF)
    )

    val Midnight = KeyboardColorScheme(
        name = "Midnight",
        isDark = true,
        background = Color(0xFF141921),
        keyBackground = Color(0xFF38465B),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF2A3647),
        specialKeyText = Color(0xFFFFFFFF),
        accent = Color(0xFF435670),
        suggestionBar = Color(0xFF141921),
        suggestionText = Color(0xFFE2E8F0),
        borderColor = Color(0x18FFFFFF)
    )

    val ProSlateDark = KeyboardColorScheme(
        name = "ProSlateDark",
        isDark = true,
        background = Color(0xFF141921),
        keyBackground = Color(0xFF38465B),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF2A3647),
        specialKeyText = Color(0xFFFFFFFF),
        accent = Color(0xFF435670),
        suggestionBar = Color(0xFF141921),
        suggestionText = Color(0xFFE2E8F0),
        borderColor = Color(0x18FFFFFF)
    )

    val Dark = KeyboardColorScheme(
        name = "Dark",
        isDark = true,
        background = Color(0xFF18181B),
        keyBackground = Color(0xFF27272A),
        keyText = Color(0xFFFAFAFA),
        specialKeyBackground = Color(0xFF3F3F46),
        specialKeyText = Color(0xFFE4E4E7),
        accent = Color(0xFF6366F1),
        suggestionBar = Color(0xFF27272A),
        suggestionText = Color(0xFFE4E4E7)
    )

    val Ocean = KeyboardColorScheme(
        name = "Ocean",
        isDark = true,
        background = Color(0xFF0B192C),
        keyBackground = Color(0xFF1E3E62),
        keyText = Color(0xFFF1F5F9),
        specialKeyBackground = Color(0xFF000000).copy(alpha = 0.4f),
        specialKeyText = Color(0xFF00ADB5),
        accent = Color(0xFF00ADB5),
        suggestionBar = Color(0xFF1E3E62),
        suggestionText = Color(0xFFE0F2FE),
        borderColor = Color(0x3300ADB5)
    )

    val Purple = KeyboardColorScheme(
        name = "Purple",
        isDark = true,
        background = Color(0xFF1A102F),
        keyBackground = Color(0xFF2C1B4D),
        keyText = Color(0xFFFDF4FF),
        specialKeyBackground = Color(0xFF442B75),
        specialKeyText = Color(0xFFD946EF),
        accent = Color(0xFFD946EF),
        suggestionBar = Color(0xFF2C1B4D),
        suggestionText = Color(0xFFF5D0FE),
        borderColor = Color(0x33D946EF)
    )

    val Red = KeyboardColorScheme(
        name = "Red",
        isDark = true,
        background = Color(0xFF230D0D),
        keyBackground = Color(0xFF3B1515),
        keyText = Color(0xFFFFF1F2),
        specialKeyBackground = Color(0xFF571C1C),
        specialKeyText = Color(0xFFF43F5E),
        accent = Color(0xFFF43F5E),
        suggestionBar = Color(0xFF3B1515),
        suggestionText = Color(0xFFFFE4E6),
        borderColor = Color(0x33F43F5E)
    )

    val Green = KeyboardColorScheme(
        name = "Green",
        isDark = true,
        background = Color(0xFF062016),
        keyBackground = Color(0xFF0F3927),
        keyText = Color(0xFFECFDF5),
        specialKeyBackground = Color(0xFF155339),
        specialKeyText = Color(0xFF10B981),
        accent = Color(0xFF10B981),
        suggestionBar = Color(0xFF0F3927),
        suggestionText = Color(0xFFD1FAE5),
        borderColor = Color(0x3310B981)
    )

    val Neon = KeyboardColorScheme(
        name = "Neon",
        isDark = true,
        background = Color(0xFF030712),
        keyBackground = Color(0xFF111827),
        keyText = Color(0xFF22D3EE),
        specialKeyBackground = Color(0xFF1F2937),
        specialKeyText = Color(0xFFA855F7),
        accent = Color(0xFF22D3EE),
        suggestionBar = Color(0xFF111827),
        suggestionText = Color(0xFF67E8F9),
        borderColor = Color(0x5522D3EE)
    )

    val Amoled = KeyboardColorScheme(
        name = "AMOLED",
        isDark = true,
        background = Color(0xFF000000),
        keyBackground = Color(0xFF121212),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF1E1E1E),
        specialKeyText = Color(0xFF3B82F6),
        accent = Color(0xFF3B82F6),
        suggestionBar = Color(0xFF121212),
        suggestionText = Color(0xFFE5E7EB),
        borderColor = Color(0x22FFFFFF)
    )

    val CleanLight = KeyboardColorScheme(
        name = "Clean Light",
        isDark = false,
        background = Color(0xFFECEFF1),
        keyBackground = Color(0xFFFFFFFF),
        keyText = Color(0xFF1E293B),
        specialKeyBackground = Color(0xFFCFD8DC),
        specialKeyText = Color(0xFF0284C7),
        accent = Color(0xFF0284C7),
        suggestionBar = Color(0xFFF1F5F9),
        suggestionText = Color(0xFF334155),
        borderColor = Color(0x1E000000)
    )

    val Transparent = KeyboardColorScheme(
        name = "Transparent",
        isDark = true,
        background = Color(0x55000000),
        keyBackground = Color(0x44FFFFFF),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0x33000000),
        specialKeyText = Color(0xFF38BDF8),
        accent = Color(0xFF38BDF8),
        suggestionBar = Color(0x55000000),
        suggestionText = Color(0xFFF1F5F9),
        borderColor = Color(0x44FFFFFF)
    )

    val MatrixTerminal = KeyboardColorScheme(
        name = "Matrix Terminal",
        isDark = true,
        background = Color(0xFF0D1117),
        keyBackground = Color(0xFF161B22),
        keyText = Color(0xFF39D353),
        specialKeyBackground = Color(0xFF21262D),
        specialKeyText = Color(0xFF58A6FF),
        accent = Color(0xFF39D353),
        suggestionBar = Color(0xFF161B22),
        suggestionText = Color(0xFF7EE787),
        borderColor = Color(0x4439D353)
    )

    val EmeraldDark = KeyboardColorScheme(
        name = "Emerald Dark",
        isDark = true,
        background = Color(0xFF062016),
        keyBackground = Color(0xFF0E3D2A),
        keyText = Color(0xFFECFDF5),
        specialKeyBackground = Color(0xFF134E36),
        specialKeyText = Color(0xFF34D399),
        accent = Color(0xFF10B981),
        suggestionBar = Color(0xFF0E3D2A),
        suggestionText = Color(0xFFA7F3D0),
        borderColor = Color(0x3310B981)
    )

    val RoseGold = KeyboardColorScheme(
        name = "Rose Gold",
        isDark = true,
        background = Color(0xFF24151C),
        keyBackground = Color(0xFF3A212D),
        keyText = Color(0xFFFFF1F2),
        specialKeyBackground = Color(0xFF522F3F),
        specialKeyText = Color(0xFFFB7185),
        accent = Color(0xFFF43F5E),
        suggestionBar = Color(0xFF3A212D),
        suggestionText = Color(0xFFFECDD3),
        borderColor = Color(0x33F43F5E)
    )

    val Cyberpunk = KeyboardColorScheme(
        name = "Cyberpunk",
        isDark = true,
        background = Color(0xFF100B2B),
        keyBackground = Color(0xFF201648),
        keyText = Color(0xFFFACC15),
        specialKeyBackground = Color(0xFF321A6B),
        specialKeyText = Color(0xFF22D3EE),
        accent = Color(0xFF22D3EE),
        suggestionBar = Color(0xFF201648),
        suggestionText = Color(0xFFFDE047),
        borderColor = Color(0x55FACC15)
    )

    val RoyalLavender = KeyboardColorScheme(
        name = "Royal Lavender",
        isDark = true,
        background = Color(0xFF1E1B2E),
        keyBackground = Color(0xFF2E294A),
        keyText = Color(0xFFF3E8FF),
        specialKeyBackground = Color(0xFF433C68),
        specialKeyText = Color(0xFFC084FC),
        accent = Color(0xFFA855F7),
        suggestionBar = Color(0xFF2E294A),
        suggestionText = Color(0xFFE9D5FF),
        borderColor = Color(0x33A855F7)
    )

    val SunsetCrimson = KeyboardColorScheme(
        name = "Sunset Crimson",
        isDark = true,
        background = Color(0xFF210E14),
        keyBackground = Color(0xFF3D1A25),
        keyText = Color(0xFFFFEDD5),
        specialKeyBackground = Color(0xFF592636),
        specialKeyText = Color(0xFFFB923C),
        accent = Color(0xFFF97316),
        suggestionBar = Color(0xFF3D1A25),
        suggestionText = Color(0xFFFED7AA),
        borderColor = Color(0x33F97316)
    )

    val IOSGlass = KeyboardColorScheme(
        name = "iOS Glass",
        isDark = true,
        background = Color(0xFF1C1C1E),
        keyBackground = Color(0xFF3A3A3C),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF2C2C2E),
        specialKeyText = Color(0xFF0A84FF),
        accent = Color(0xFF0A84FF),
        suggestionBar = Color(0xFF1C1C1E),
        suggestionText = Color(0xFFE5E5EA),
        borderColor = Color(0x33FFFFFF)
    )

    val TitaniumDark = KeyboardColorScheme(
        name = "Titanium Pro",
        isDark = true,
        background = Color(0xFF181A1D),
        keyBackground = Color(0xFF2B2E33),
        keyText = Color(0xFFF0F3F6),
        specialKeyBackground = Color(0xFF22252A),
        specialKeyText = Color(0xFF8E95A2),
        accent = Color(0xFF64748B),
        suggestionBar = Color(0xFF181A1D),
        suggestionText = Color(0xFFCBD5E1),
        borderColor = Color(0x228E95A2)
    )

    val ObsidianGold = KeyboardColorScheme(
        name = "Obsidian Gold",
        isDark = true,
        background = Color(0xFF0D0D0D),
        keyBackground = Color(0xFF1C1A14),
        keyText = Color(0xFFFFDF73),
        specialKeyBackground = Color(0xFF2A2415),
        specialKeyText = Color(0xFFFFC72C),
        accent = Color(0xFFFFD700),
        suggestionBar = Color(0xFF0D0D0D),
        suggestionText = Color(0xFFFFE899),
        borderColor = Color(0x44FFD700)
    )

    val AuroraBorealis = KeyboardColorScheme(
        name = "Aurora Borealis",
        isDark = true,
        background = Color(0xFF08131E),
        keyBackground = Color(0xFF0E273C),
        keyText = Color(0xFF7DF9FF),
        specialKeyBackground = Color(0xFF143753),
        specialKeyText = Color(0xFF2DD4BF),
        accent = Color(0xFF06B6D4),
        suggestionBar = Color(0xFF08131E),
        suggestionText = Color(0xFFA5F3FC),
        borderColor = Color(0x332DD4BF)
    )

    val SakuraPastel = KeyboardColorScheme(
        name = "Sakura Blossom",
        isDark = false,
        background = Color(0xFFFFF0F5),
        keyBackground = Color(0xFFFFFFFF),
        keyText = Color(0xFF831843),
        specialKeyBackground = Color(0xFFFCE7F3),
        specialKeyText = Color(0xFFDB2777),
        accent = Color(0xFFEC4899),
        suggestionBar = Color(0xFFFFF0F5),
        suggestionText = Color(0xFF9D174D),
        borderColor = Color(0x22EC4899)
    )

    val CrimsonRuby = KeyboardColorScheme(
        name = "Crimson Ruby",
        isDark = true,
        background = Color(0xFF1F0B11),
        keyBackground = Color(0xFF3B121F),
        keyText = Color(0xFFFFE4E6),
        specialKeyBackground = Color(0xFF50182A),
        specialKeyText = Color(0xFFFB7185),
        accent = Color(0xFFE11D48),
        suggestionBar = Color(0xFF1F0B11),
        suggestionText = Color(0xFFFECDD3),
        borderColor = Color(0x33E11D48)
    )

    val MochaCoffee = KeyboardColorScheme(
        name = "Mocha Coffee",
        isDark = true,
        background = Color(0xFF1A1412),
        keyBackground = Color(0xFF2C221E),
        keyText = Color(0xFFEDE0D4),
        specialKeyBackground = Color(0xFF3D302A),
        specialKeyText = Color(0xFFDDB892),
        accent = Color(0xFFB08968),
        suggestionBar = Color(0xFF1A1412),
        suggestionText = Color(0xFFE6CCB2),
        borderColor = Color(0x22DDB892)
    )

    val ForestZen = KeyboardColorScheme(
        name = "Forest Zen",
        isDark = true,
        background = Color(0xFF0D1B13),
        keyBackground = Color(0xFF173022),
        keyText = Color(0xFFE6F4EA),
        specialKeyBackground = Color(0xFF20422F),
        specialKeyText = Color(0xFF6EE7B7),
        accent = Color(0xFF10B981),
        suggestionBar = Color(0xFF0D1B13),
        suggestionText = Color(0xFFA7F3D0),
        borderColor = Color(0x2210B981)
    )

    val DeepSapphire = KeyboardColorScheme(
        name = "Royal Sapphire",
        isDark = true,
        background = Color(0xFF0A1128),
        keyBackground = Color(0xFF1C2D5A),
        keyText = Color(0xFFE0E7FF),
        specialKeyBackground = Color(0xFF131F43),
        specialKeyText = Color(0xFF60A5FA),
        accent = Color(0xFF3B82F6),
        suggestionBar = Color(0xFF0A1128),
        suggestionText = Color(0xFFBFDBFE),
        borderColor = Color(0x333B82F6)
    )

    val CustomThemeDefault = KeyboardColorScheme(
        name = "Custom",
        isDark = true,
        background = Color(0xFF121824),
        keyBackground = Color(0xFF1F293D),
        keyText = Color(0xFFFFFFFF),
        specialKeyBackground = Color(0xFF162032),
        specialKeyText = Color(0xFF60A5FA),
        accent = Color(0xFF38BDF8),
        suggestionBar = Color(0xFF121824),
        suggestionText = Color(0xFFE2E8F0),
        borderColor = Color(0x4438BDF8)
    )

    val allThemes = listOf(
        GboardDark,
        IOSGlass,
        TitaniumDark,
        ObsidianGold,
        DeepSapphire,
        AuroraBorealis,
        SwiftKeyDark,
        GboardLight,
        Amoled,
        Midnight,
        ProSlateDark,
        SakuraPastel,
        CrimsonRuby,
        MochaCoffee,
        ForestZen,
        Cyberpunk,
        RoyalLavender,
        MatrixTerminal,
        EmeraldDark,
        RoseGold,
        SunsetCrimson,
        Ocean,
        Purple,
        Green,
        Red,
        Neon,
        CleanLight,
        Transparent,
        Dark
    )

    fun getTheme(
        name: String,
        customKeyColor: String? = null,
        prefs: com.example.data.pref.KeyboardPreferences? = null
    ): KeyboardColorScheme {
        if (name.equals("Custom", ignoreCase = true)) {
            return prefs?.getCustomColorScheme() ?: CustomThemeDefault
        }
        val base = allThemes.find { it.name.equals(name, ignoreCase = true) } ?: Midnight
        if (!customKeyColor.isNullOrEmpty() && customKeyColor != "default") {
            try {
                val parsed = Color(android.graphics.Color.parseColor(customKeyColor))
                return base.copy(keyBackground = parsed)
            } catch (e: Throwable) {
                // Fallback
            }
        }
        return base
    }
}
