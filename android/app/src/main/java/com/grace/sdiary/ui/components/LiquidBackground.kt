package com.grace.sdiary.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.Coral
import com.grace.sdiary.ui.theme.Forest
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.Teal

private data class Blob(
    val color: Color,
    val baseRadius: Float,
    val offsetRangeX: Float,
    val offsetRangeY: Float
)

@Composable
fun LiquidBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquidBg")

    val blobs = remember {
        listOf(
            Blob(Forest.copy(alpha = 0.22f), 120f, 60f, 50f),
            Blob(Teal.copy(alpha = 0.2f), 100f, 70f, 40f),
            Blob(Gold.copy(alpha = 0.17f), 140f, 50f, 60f),
            Blob(Coral.copy(alpha = 0.17f), 90f, 55f, 45f)
        )
    }

    val blobAnims = blobs.mapIndexed { index, blob ->
        val xOffset by infiniteTransition.animateFloat(
            initialValue = -blob.offsetRangeX,
            targetValue = blob.offsetRangeX,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 6000 + index * 1500,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blobX_$index"
        )
        val yOffset by infiniteTransition.animateFloat(
            initialValue = -blob.offsetRangeY,
            targetValue = blob.offsetRangeY,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 7000 + index * 2000,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blobY_$index"
        )
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000 + index * 2000,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "blobScale_$index"
        )
        Triple(xOffset, yOffset, scale)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .blur(radius = 45.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            blobs.forEachIndexed { index, blob ->
                val (xOff, yOff, scale) = blobAnims[index]
                val cx = size.width / 2f + xOff
                val cy = size.height / 2f + yOff
                val r = blob.baseRadius * scale
                drawCircle(
                    color = blob.color,
                    radius = r,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}
