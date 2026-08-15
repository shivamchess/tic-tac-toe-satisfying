package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.WinResult
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan
import com.satisfying.tictactoe.theme.SurfaceDark

@Composable
fun GameBoard(
    board: List<Player?>,
    winResult: WinResult?,
    onCellClicked: (Int) -> Unit,
    onCellPositioned: (Int, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    // Screen shake animation on winning
    val shakeX = remember { Animatable(0f) }
    val shakeY = remember { Animatable(0f) }

    LaunchedEffect(winResult) {
        if (winResult != null) {
            shakeX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -12f at 50
                    14f at 100
                    -8f at 150
                    10f at 200
                    -4f at 250
                    0f at 400
                }
            )
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = shakeX.value
                translationY = shakeY.value
            }
            .aspectRatio(1f)
            .shadow(
                elevation = 32.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = NeonCyan.copy(alpha = 0.4f),
                spotColor = NeonCoral.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF05070B)) // Very dark base
            .drawBehind {
                // Outer Thick Metallic Bevel (simulating depth)
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF334155), Color(0xFF0F172A)),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(32.dp.toPx())
                )
                // Inner Recessed Board Area (creating a cavity for the cells)
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF111827), Color(0xFF030712)),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.width / 1.2f
                    ),
                    topLeft = Offset(12.dp.toPx(), 12.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width - 24.dp.toPx(), size.height - 24.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                )
                // Glowing Inner Trim
                drawRoundRect(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            NeonCyan.copy(alpha = 0.6f),
                            NeonCoral.copy(alpha = 0.6f),
                            ElectricGold.copy(alpha = 0.6f),
                            NeonCyan.copy(alpha = 0.6f)
                        ),
                        center = Offset(size.width / 2f, size.height / 2f)
                    ),
                    topLeft = Offset(12.dp.toPx(), 12.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(size.width - 24.dp.toPx(), size.height - 24.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }
            .padding(20.dp)
    ) {
        // Draw Cells Grid
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until 3) {
                Row(modifier = Modifier.weight(1f)) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        val isWinCell = winResult?.winLine?.contains(index) == true
                        GameCell(
                            player = board[index],
                            isWinningCell = isWinCell,
                            onClick = { onCellClicked(index) },
                            onPositioned = { offset -> onCellPositioned(index, offset) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Draw Laser Winning Line
        if (winResult != null) {
            JuicyWinningLine(winResult)
        }
    }
}

@Composable
fun JuicyWinningLine(winResult: WinResult) {
    val progress = remember { Animatable(0f) }
    val glowPulse = remember { Animatable(1f) }

    LaunchedEffect(winResult) {
        progress.animateTo(1f, animationSpec = tween(380, easing = FastOutSlowInEasing))
        glowPulse.animateTo(
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(400),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val lineColor = if (winResult.winner == Player.X) NeonCyan else NeonCoral

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellSize = size.width / 3f
        val halfCell = cellSize / 2f

        val startIdx = winResult.winLine.first()
        val endIdx = winResult.winLine.last()

        val startX = (startIdx % 3) * cellSize + halfCell
        val startY = (startIdx / 3) * cellSize + halfCell

        val endX = (endIdx % 3) * cellSize + halfCell
        val endY = (endIdx / 3) * cellSize + halfCell

        val currentX = startX + (endX - startX) * progress.value
        val currentY = startY + (endY - startY) * progress.value

        // Massive Outer Laser Glow
        drawLine(
            color = lineColor.copy(alpha = 0.35f),
            start = Offset(startX, startY),
            end = Offset(currentX, currentY),
            strokeWidth = size.width * 0.12f * glowPulse.value,
            cap = StrokeCap.Round
        )

        // Mid Laser Core
        drawLine(
            color = lineColor,
            start = Offset(startX, startY),
            end = Offset(currentX, currentY),
            strokeWidth = size.width * 0.045f,
            cap = StrokeCap.Round
        )

        // Ultra Bright Laser Center
        drawLine(
            color = Color.White,
            start = Offset(startX, startY),
            end = Offset(currentX, currentY),
            strokeWidth = size.width * 0.015f,
            cap = StrokeCap.Round
        )
    }
}
