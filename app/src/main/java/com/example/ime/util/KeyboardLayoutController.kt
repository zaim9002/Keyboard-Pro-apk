package com.example.ime.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object KeyboardLayoutController {

    fun getKeyHeight(heightPref: String): Dp {
        return when (heightPref) {
            "Small" -> 42.dp
            "Large" -> 54.dp
            "ExtraLarge" -> 60.dp
            else -> 48.dp
        }
    }

    fun getPanelHeight(heightPref: String, showNumberRow: Boolean): Dp {
        val keyHeight = getKeyHeight(heightPref)
        val numberRowHeight = if (showNumberRow) keyHeight * 0.85f else 0.dp
        // Standard height matching the 4 letter rows + bottom control row + optional number row
        return (keyHeight * 4) + numberRowHeight + 8.dp
    }
}
