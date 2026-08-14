package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.WinResult

@Composable
fun GameBoard(
    board: List<Player?>,
    winResult: WinResult?,
    onCellClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.aspectRatio(1f)
    ) {
        val lineColor = MaterialTheme.colorScheme.surfaceVariant
        
        // Draw Grid Lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.width * 0.02f
            val oneThird = size.width / 3f
            val twoThirds = size.width * 2f / 3f

            // Verticals
            drawLine(lineColor, Offset(oneThird, 0f), Offset(oneThird, size.height), strokeWidth, StrokeCap.Round)
            drawLine(lineColor, Offset(twoThirds, 0f), Offset(twoThirds, size.height), strokeWidth, StrokeCap.Round)

            // Horizontals
            drawLine(lineColor, Offset(0f, oneThird), Offset(size.width, oneThird), strokeWidth, StrokeCap.Round)
            drawLine(lineColor, Offset(0f, twoThirds), Offset(size.width, twoThirds), strokeWidth, StrokeCap.Round)
        }

        // Draw Cells
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until 3) {
                Row(modifier = Modifier.weight(1f)) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        GameCell(
                            player = board[index],
                            onClick = { onCellClicked(index) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Draw Winning Line if applicable
        if (winResult != null) {
            WinningLine(winResult)
        }
    }
}

@Composable
fun WinningLine(winResult: WinResult) {
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(winResult) {
        progress.animateTo(1f, animationSpec = tween(500))
    }

    val color = MaterialTheme.colorScheme.tertiary

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellSize = size.width / 3f
        val halfCell = cellSize / 2f

        val startIdx = winResult.winLine.first()
        val endIdx = winResult.winLine.last()

        val startX = (startIdx % 3) * cellSize + halfCell
        val startY = (startIdx / 3) * cellSize + halfCell
        
        val endX = (endIdx % 3) * cellSize + halfCell
        val endY = (endIdx / 3) * cellSize + halfCell

        // Calculate current end based on animation progress
        val currentX = startX + (endX - startX) * progress.value
        val currentY = startY + (endY - startY) * progress.value

        // Draw glowing thick line
        drawLine(
            color = color.copy(alpha = 0.5f),
            start = Offset(startX, startY),
            end = Offset(currentX, currentY),
            strokeWidth = size.width * 0.08f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(currentX, currentY),
            strokeWidth = size.width * 0.03f,
            cap = StrokeCap.Round
        )
    }
}
