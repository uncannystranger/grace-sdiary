package com.grace.sdiary.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.AppTypography

@Composable
fun StatRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val glass = GraceTheme.glassColors

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = glass.textMid
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = glass.textMid
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = AppTypography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = glass.textHigh
        )
    }
}
