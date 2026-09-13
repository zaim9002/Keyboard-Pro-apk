package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

data class KeyboardSizePreset(
    val id: String,
    val nameAr: String,
    val heightDp: Int,
    val description: String
)

@Composable
fun ResizePanel(
    modifier: Modifier = Modifier,
    currentHeight: String,
    colorScheme: KeyboardColorScheme,
    onSelectHeight: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    val presets = listOf(
        KeyboardSizePreset("Small", "صغير (42dp)", 42, "حجم مدمج لشاشات إضافية"),
        KeyboardSizePreset("Medium", "افتراضي (48dp)", 48, "الحجم القياسي المتوازن كـ Gboard"),
        KeyboardSizePreset("Large", "كبير (54dp)", 54, "أزرار أوسع وأسهل للإبهام"),
        KeyboardSizePreset("ExtraLarge", "ضخم (60dp)", 60, "أقصى حجم لكتابة بدون أخطاء")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Height,
                    contentDescription = "تغيير حجم الكيبورد",
                    tint = colorScheme.accent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "تغيير حجم وارتفاع الكيبورد",
                    color = colorScheme.keyText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = {
                    HapticHelper.performKeyHaptic(context, view)
                    onClose()
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Preset cards grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                val isSelected = currentHeight.equals(preset.id, ignoreCase = true) ||
                        (preset.id == "Medium" && currentHeight != "Small" && currentHeight != "Large" && currentHeight != "ExtraLarge")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) colorScheme.accent.copy(alpha = 0.22f)
                            else colorScheme.keyBackground
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) colorScheme.accent else colorScheme.keyBackground.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            HapticHelper.performKeyHaptic(context, view)
                            onSelectHeight(preset.id)
                        }
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "محدد",
                                tint = colorScheme.accent,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AspectRatio,
                                contentDescription = null,
                                tint = colorScheme.keyText.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = preset.nameAr,
                            color = if (isSelected) colorScheme.accent else colorScheme.keyText,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Footer quick note
        Text(
            text = "✓ يتم حفظ الارتفاع وتطبيقه فورياً على كافة الأزرار بدون أي مساحات فارغة سفلية",
            color = colorScheme.keyText.copy(alpha = 0.6f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
        )
    }
}
