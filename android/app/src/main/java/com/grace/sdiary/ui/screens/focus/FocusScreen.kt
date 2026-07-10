package com.grace.sdiary.ui.screens.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.FocusCenter
import androidx.compose.material.icons.outlined.FreeBreakfast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.*

private val focusDurations = listOf(15, 25, 30, 45, 60)
private val breakDurations = listOf(5, 10, 15, 20)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    onNavigate: (String) -> Unit,
    viewModel: FocusViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val isRunning by viewModel.isRunning.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val pomodoroCount by viewModel.pomodoroCount.collectAsState()
    val time by viewModel.formattedTime.collectAsState()
    val progress by viewModel.progress.collectAsState()

    var showSettings by remember { mutableStateOf(false) }
    var selectedFocusDuration by remember { mutableIntStateOf(25) }
    var selectedBreakDuration by remember { mutableIntStateOf(5) }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = glass.textHigh
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(0.5f))

                TimerRing(
                    time = time,
                    progress = progress,
                    mode = mode,
                    modifier = Modifier.size(280.dp)
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (mode == "focus") Icons.Outlined.FocusCenter else Icons.Outlined.FreeBreakfast,
                        contentDescription = null,
                        tint = if (mode == "focus") Gold else Teal,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (mode == "focus") "FOCUS" else "BREAK",
                        style = AppTypography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (mode == "focus") Gold else Teal,
                        letterSpacing = 4.sp
                    )
                }

                Spacer(Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (isRunning) viewModel.pauseTimer()
                            else viewModel.startTimer()
                        },
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        containerColor = Gold,
                        contentColor = Color.Black,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Start",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = viewModel::resetTimer,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(glass.glassBackground)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Reset",
                            tint = glass.textMid
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Coffee,
                        contentDescription = null,
                        tint = glass.textMid,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Pomodoros completed: $pomodoroCount",
                        style = AppTypography.bodyMedium,
                        color = glass.textMid
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Completed $pomodoroCount pomodoros today",
                    style = AppTypography.labelSmall,
                    color = glass.textLow
                )

                Spacer(Modifier.weight(0.5f))

                if (showSettings) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Duration Settings",
                                style = AppTypography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = glass.textHigh
                            )

                            Text(
                                text = "Focus",
                                style = AppTypography.labelLarge,
                                color = glass.textMid
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(focusDurations.size) { index ->
                                    val dur = focusDurations[index]
                                    val isSel = selectedFocusDuration == dur
                                    FilterChip(
                                        selected = isSel,
                                        onClick = {
                                            selectedFocusDuration = dur
                                            viewModel.setFocusDuration(dur)
                                        },
                                        label = { Text("${dur}m") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Gold.copy(alpha = 0.2f),
                                            selectedLabelColor = Gold
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = glass.glassBorder,
                                            selectedBorderColor = Gold,
                                            enabled = true,
                                            selected = isSel
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Break",
                                style = AppTypography.labelLarge,
                                color = glass.textMid
                            )
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(breakDurations.size) { index ->
                                    val dur = breakDurations[index]
                                    val isSel = selectedBreakDuration == dur
                                    FilterChip(
                                        selected = isSel,
                                        onClick = {
                                            selectedBreakDuration = dur
                                            viewModel.setBreakDuration(dur)
                                        },
                                        label = { Text("${dur}m") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Teal.copy(alpha = 0.2f),
                                            selectedLabelColor = Teal
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            borderColor = glass.glassBorder,
                                            selectedBorderColor = Teal,
                                            enabled = true,
                                            selected = isSel
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                IconButton(
                    onClick = { showSettings = !showSettings },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(glass.glassBackground)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Timer settings",
                        tint = glass.textMid,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            LiquidBackground(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun TimerRing(
    time: String,
    progress: Float,
    mode: String,
    modifier: Modifier = Modifier
) {
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "timerProgress"
    )

    val strokeWidth = 10.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.size(280.dp)) {
            val canvasSize = this.size.minDimension
            val stroke = strokeWidth.toPx()
            val radius = (canvasSize - stroke) / 2f
            val topLeft = Offset(
                (this.size.width - canvasSize) / 2f + stroke / 2f,
                (this.size.height - canvasSize) / 2f + stroke / 2f
            )
            val arcSize = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)

            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            val sweepAngle = animProgress * 360f
            val gradientBrush = Brush.sweepGradient(
                colors = listOf(Gold, Teal),
                center = Offset(this.size.width / 2f, this.size.height / 2f)
            )

            drawArc(
                brush = gradientBrush,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            if (animProgress > 0f) {
                val dotAngle = -90f + sweepAngle
                val dotRad = Math.toRadians(dotAngle.toDouble())
                val dotX = (this.size.width / 2f) + radius * kotlin.math.cos(dotRad).toFloat()
                val dotY = (this.size.height / 2f) + radius * kotlin.math.sin(dotRad).toFloat()
                drawCircle(
                    color = if (mode == "focus") Gold else Teal,
                    radius = stroke / 2.2f,
                    center = Offset(dotX, dotY)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = time,
                transitionSpec = {
                    (fadeIn(spring(Spring.DampingRatioMediumBouncy)) togetherWith
                            fadeOut(spring(Spring.DampingRatioMediumBouncy)))
                },
                label = "timerText"
            ) { targetTime ->
                Text(
                    text = targetTime,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp,
                    color = Gold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
