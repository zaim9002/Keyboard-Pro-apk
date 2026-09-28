package com.example.ime.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import kotlinx.coroutines.delay

@Composable
fun KeyboardTickerBar(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onOpenMenu: () -> Unit,
    onHeadlineClick: (String) -> Unit = {}
) {
    val headlines = listOf(
        "«كفاية إنهم كلموني».. سماح أنور تعتذر عن مغادرتها مهرجان الإسكندرية السينمائي",
        "تحديث جديد: إطلاق ميزات الذكاء الاصطناعي وصور GIF المباشرة بلوحة المفاتيح",
        "حالة الطقس اليوم: أجواء معتدلة نهاراً ومائلة للبرودة ليلاً في أغلب المناطق",
        "مباراة القمة اليوم في الدوري: متابعة حية وتغطية خاصة لكافة الأحداث الرياضية",
        "اكتشف أجمل ثيمات لوحة المفاتيح وتخصيص الألوان والصور من المعرض"
    )

    var currentHeadlineIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentHeadlineIndex = (currentHeadlineIndex + 1) % headlines.size
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .background(colorScheme.background.copy(alpha = 0.95f))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Headline text
        val currentHeadline = headlines[currentHeadlineIndex]
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { onHeadlineClick(currentHeadline) }
                .padding(end = 6.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            AnimatedContent(
                targetState = currentHeadline,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "headline_anim"
            ) { text ->
                Text(
                    text = text,
                    color = colorScheme.keyText.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Grid Menu Button (⊞)
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colorScheme.keyBackground.copy(alpha = 0.7f))
                .clickable { onOpenMenu() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = "قائمة لوحة المفاتيح",
                tint = colorScheme.keyText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
