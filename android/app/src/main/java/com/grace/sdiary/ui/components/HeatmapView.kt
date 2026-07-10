package com.grace.sdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GoldLight
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Teal

@Composable
fun HeatmapView(
    months: List<Pair<String, List<Int>>>,
    modifier: Modifier = Modifier
) {
    val cellSize = 12.dp
    val cellGap = 3.dp
    val dayLabelWidth = 28.dp
    val dayLabels = listOf(0 to "Mon", 2 to "Wed", 4 to "Fri")
    val glass = GraceTheme.glassColors

    data class MonthGrid(val name: String, val weeks: List<List<Int>>)

    val monthGrids = months.map { (name, values) ->
        val weeks = values.chunked(7).map { week ->
            week + List(7 - week.size) { 0 }
        }
        MonthGrid(name, weeks)
    }

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState())
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = dayLabelWidth),
                verticalAlignment = Alignment.Bottom
            ) {
                monthGrids.forEach { grid ->
                    val totalWeeks = grid.weeks.size
                    val labelWidth = if (totalWeeks > 0) {
                        (cellSize + cellGap) * totalWeeks - cellGap
                    } else 0.dp

                    Spacer(Modifier.width(cellGap / 2f))
                    Text(
                        text = grid.name,
                        style = AppTypography.labelSmall,
                        color = glass.textMid,
                        modifier = Modifier.width(labelWidth.coerceAtLeast(0.dp))
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            for (row in 0..6) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dayLabel = dayLabels.firstOrNull { it.first == row }?.second
                    if (dayLabel != null) {
                        Text(
                            text = dayLabel,
                            style = AppTypography.labelSmall,
                            color = glass.textLow,
                            modifier = Modifier.width(dayLabelWidth)
                        )
                    } else {
                        Spacer(Modifier.width(dayLabelWidth))
                    }

                    monthGrids.forEach { grid ->
                        grid.weeks.forEach { week ->
                            val value = week.getOrElse(row) { 0 }
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(
                                        color = when {
                                            value >= 4 -> Teal
                                            value >= 3 -> Gold
                                            value >= 2 -> GoldLight
                                            value >= 1 -> GoldLight.copy(alpha = 0.4f)
                                            else -> glass.textLow.copy(alpha = 0.08f)
                                        },
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                            Spacer(Modifier.width(cellGap))
                        }
                    }
                }
                Spacer(Modifier.height(cellGap))
            }
        }
    }
}
