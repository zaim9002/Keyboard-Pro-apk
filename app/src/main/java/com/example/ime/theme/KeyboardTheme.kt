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

    val allThemes = listOf(
        GboardDark,
        SwiftKeyDark,
        GboardLight,
        Transparent,
        ProSlateDark,
        Midnight,
        Dark,
        Ocean,
        Purple,
        Green,
        Red,
        Neon,
        Amoled,
        CleanLight
    )

    fun getTheme(name: String, customKeyColor: String? = null): KeyboardColorScheme {
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
