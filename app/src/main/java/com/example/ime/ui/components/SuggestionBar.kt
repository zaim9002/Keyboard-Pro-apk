package com.example.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun SuggestionBar(
    modifier: Modifier = Modifier,
    suggestions: List<String>,
    latestClip: String? = null,
    colorScheme: KeyboardColorScheme,
    onSelectSuggestion: (String) -> Unit,
    onPasteClip: (String) -> Unit = {}
) {
    if (suggestions.isEmpty() && latestClip.isNullOrBlank()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(colorScheme.suggestionBar)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Show Latest Clipboard item as first chip if available
        if (!latestClip.isNullOrBlank()) {
            val clipPreview = latestClip.replace("\n", " ").trim()
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.accent.copy(alpha = 0.22f))
                    .clickable { onPasteClip(latestClip) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📋 $clipPreview",
                    color = colorScheme.accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }

        val displaySuggestions = if (!latestClip.isNullOrBlank()) suggestions.take(2) else suggestions.take(3)
        for ((index, suggestion) in displaySuggestions.withIndex()) {
            val isPrimary = index == 0 && latestClip.isNullOrBlank()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isPrimary) colorScheme.accent.copy(alpha = 0.15f)
                        else colorScheme.keyBackground.copy(alpha = 0.5f)
                    )
                    .clickable { onSelectSuggestion(suggestion) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = suggestion,
                    color = if (isPrimary) colorScheme.accent else colorScheme.suggestionText,
                    fontSize = 13.sp,
                    fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
