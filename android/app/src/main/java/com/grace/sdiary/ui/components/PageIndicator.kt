package com.grace.sdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme

@Composable
fun PageIndicator(
    pages: List<String>,
    currentPage: String,
    onChangePage: (String) -> Unit
) {
    val glass = GraceTheme.glassColors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        pages.forEachIndexed { index, page ->
            val isActive = page == currentPage
            Box(
                modifier = Modifier
                    .size(if (isActive) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) Gold else glass.textLow.copy(alpha = 0.4f)
                    )
                    .clickable { onChangePage(page) }
            )
            if (index < pages.lastIndex) {
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
