package com.grace.sdiary.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a glassmorphism card effect: translucent background, subtle border, and soft shadow.
 *
 * @param shape  corner shape for the card (default 12.dp rounded)
 * @param elevation  shadow elevation depth
 */
fun Modifier.glassCard(
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    elevation: Dp = 4.dp,
): Modifier {
    val glassColors = GraceTheme.glassColors
    return this
        .shadow(elevation, shape, ambientColor = Color.Black.copy(alpha = 0.15f), spotColor = Color.Black.copy(alpha = 0.15f))
        .background(glassColors.glassBackground, shape)
        .glassBorder(shape)
}

/**
 * Draws a subtle glass-style border matching the current theme.
 *
 * @param shape  corner shape to follow
 */
fun Modifier.glassBorder(
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
): Modifier {
    val glassColors = GraceTheme.glassColors
    return this.drawBehind {
        drawRoundRect(
            color = glassColors.glassBorder,
            cornerRadius = CornerRadius(
                x = shape.topStart.x.toPx(),
                y = shape.topStart.y.toPx(),
            ),
            size = size,
            style = Stroke(width = 1.dp.toPx()),
        )
    }
}

/**
 * A shimmer loading overlay that sweeps across the composable from left to right.
 * Use on placeholder / skeleton content.
 */
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerProgress",
    )
    return this.drawBehind {
        val shimmerWidth = size.width * 0.4f
        val total = size.width + shimmerWidth
        val center = (progress + 2f) / 4f * total - shimmerWidth
        val startX = center.coerceIn(-shimmerWidth, size.width)
        val endX = (startX + shimmerWidth).coerceAtMost(size.width + shimmerWidth)

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = 0.06f),
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.06f),
                    Color.Transparent,
                ),
                start = Offset(startX, 0f),
                end = Offset(endX, size.height),
            ),
            size = size,
        )
    }
}

/**
 * An animated gradient border glow effect. The gradient rotates around the composable's border
 * continuously, creating a "breathing" neon glow.
 *
 * @param colors  gradient colors (default [Gold, Teal])
 * @param shape  corner shape to follow
 * @param strokeWidth  width of the glowing border
 */
fun Modifier.glowBorder(
    colors: List<Color> = listOf(Gold, Teal),
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    strokeWidth: Dp = 2.dp,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "glowBorder")
    val angleDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "glowAngle",
    )
    return this.drawBehind {
        val w = size.width
        val h = size.height
        val diagonal = kotlin.math.sqrt((w * w + h * h).toDouble()).toFloat()
        val center = Offset(w / 2f, h / 2f)
        val rad = angleDeg * (kotlin.math.PI.toFloat() / 180f)
        val cosA = kotlin.math.cos(rad.toDouble()).toFloat()
        val sinA = kotlin.math.sin(rad.toDouble()).toFloat()
        val dx = diagonal * cosA
        val dy = diagonal * sinA

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(center.x + dx, center.y + dy),
                end = Offset(center.x - dx, center.y - dy),
            ),
            cornerRadius = CornerRadius(
                x = shape.topStart.x.toPx(),
                y = shape.topStart.y.toPx(),
            ),
            size = size,
            style = Stroke(width = strokeWidth.toPx()),
        )
    }
}
