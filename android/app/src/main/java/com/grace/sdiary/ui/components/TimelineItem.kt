package com.grace.sdiary.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Teal

@Composable
fun TimelineItem(
    time: String,
    title: String,
    subtitle: String? = null,
    color: Color = Teal,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    val glass = GraceTheme.glassColors
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it / 4 }) + fadeIn()
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = time,
                style = AppTypography.labelSmall,
                color = glass.textMid,
                modifier = Modifier
                    .width(48.dp)
                    .align(Alignment.Top)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .shadow(4.dp, CircleShape, spotColor = color.copy(alpha = 0.4f))
                        .background(color, CircleShape)
                )

                if (!isLast) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(40.dp)
                            .background(glass.textLow.copy(alpha = 0.25f))
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = AppTypography.bodySmall,
                        color = glass.textMid
                    )
                }
            }
        }
    }
}
