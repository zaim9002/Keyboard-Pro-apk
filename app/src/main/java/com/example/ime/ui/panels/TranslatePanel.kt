package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TranslationEngine
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun TranslatePanel(
    modifier: Modifier = Modifier,
    initialText: String,
    colorScheme: KeyboardColorScheme,
    onCommitTranslation: (String) -> Unit,
    onClose: () -> Unit
) {
    var sourceLang by remember { mutableStateOf("ar") }
    var targetLang by remember { mutableStateOf("en") }
    var inputText by remember { mutableStateOf(if (initialText.isNotBlank()) initialText else "مرحبا") }

    val translatedText = remember(inputText, sourceLang, targetLang) {
        TranslationEngine.translate(inputText, sourceLang, targetLang)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(colorScheme.background)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header & Language selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🌐 ترجمة فورية",
                color = colorScheme.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Language swap bar
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = if (sourceLang == "ar") "العربية" else "English",
                    color = colorScheme.keyText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(
                    onClick = {
                        val temp = sourceLang
                        sourceLang = targetLang
                        targetLang = temp
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "تبديل اللغات",
                        tint = colorScheme.accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = if (targetLang == "ar") "العربية" else "English",
                    color = colorScheme.keyText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Input and Result
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Source text input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .padding(8.dp)
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = colorScheme.keyText,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Output translation card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.accent.copy(alpha = 0.12f))
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = if (translatedText.isNotEmpty()) translatedText else "الترجمة ستظهر هنا...",
                    color = colorScheme.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (translatedText.isNotEmpty()) {
                        onCommitTranslation(translatedText)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "إدخال الترجمة في النص ↵",
                    fontSize = 12.sp,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
