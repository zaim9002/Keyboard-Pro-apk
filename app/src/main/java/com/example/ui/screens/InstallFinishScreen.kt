package com.example.ui.screens

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp
import com.example.ime.theme.KeyboardThemes
import com.example.ime.ui.KeyboardScreen

@Composable
fun InstallFinishScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToThemes: () -> Unit
) {
    val prefs = KeyboardProApp.instance.preferences
    var testText by remember { mutableStateOf("") }
    var currentLanguage by remember { mutableStateOf(prefs.currentLanguage) }
    val colorScheme = remember(prefs.theme) { KeyboardThemes.getColorScheme(prefs.theme) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF161618))
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Mascot & Congratulatory Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Cute Mascot Card
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE4E6)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧸💖", fontSize = 38.sp)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "اكتملت إعدادات لوحة\nمفاتيح التصميم!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "اكتملت جميع الإعدادات!\nلنجرّب معًا لوحة المفاتيح التي اخترتها",
                color = Color.LightGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            // Test Input Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF222226))
                    .border(1.dp, Color(0xFF333338), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (testText.isEmpty()) {
                    Text(
                        text = "جرّب اختبار لوحة المفاتيح.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = testText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Live interactive keyboard preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            KeyboardScreen(
                colorScheme = colorScheme,
                currentLanguage = currentLanguage,
                imeOptions = EditorInfo.IME_ACTION_DONE,
                isIncognito = false,
                keyboardHeight = prefs.keyboardHeight,
                showNumberRow = prefs.showNumberRow,
                hapticEnabled = prefs.hapticFeedback != "Off",
                soundEnabled = prefs.keySound != "Off",
                oneHandedMode = prefs.oneHandedMode,
                suggestions = listOf("السلام", "شكراً", "تمام", "أهلاً"),
                clipboardList = emptyList(),
                isVoiceListening = false,
                voiceStatusText = "جاهز للاستماع",
                voicePartialText = "",
                onTextInput = { text ->
                    testText += text
                },
                onDelete = {
                    if (testText.isNotEmpty()) {
                        testText = testText.dropLast(1)
                    }
                },
                onEnter = {
                    testText += "\n"
                },
                onSpace = {
                    testText += " "
                },
                onSwitchLanguage = {
                    currentLanguage = if (currentLanguage == "ar") "en" else "ar"
                },
                onMoveCursor = {},
                onSelectSuggestion = { sug ->
                    testText += "$sug "
                },
                onTogglePinClip = { _, _ -> },
                onDeleteClip = {},
                onClearUnpinnedClips = {},
                onStartVoice = {},
                onStopVoice = {},
                onSelectAll = {},
                onCut = { testText = "" },
                onCopy = {},
                onPaste = {},
                onUndo = { if (testText.isNotEmpty()) testText = testText.dropLast(1) },
                onRedo = {},
                onOpenSettings = onNavigateToSettings,
                onToggleOneHanded = {},
                currentDraftText = testText
            )
        }
    }
}
