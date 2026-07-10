package com.grace.sdiary.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.GraceTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val glass = GraceTheme.glassColors
    val cardColors = CardDefaults.cardColors(containerColor = glass.glassBackground)
    val cardElevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    val border = BorderStroke(1.dp, glass.glassBorder)

    val cardModifier = if (onClick != null) {
        modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else modifier

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = cardColors,
        border = border,
        elevation = cardElevation
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}
