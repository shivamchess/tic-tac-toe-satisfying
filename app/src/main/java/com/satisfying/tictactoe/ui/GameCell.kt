package com.satisfying.tictactoe.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.satisfying.tictactoe.Player

fun Modifier.bouncingClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

@Composable
fun GameCell(
    player: Player?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .bouncingClickable(enabled = player == null) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            }
            .padding(16.dp)
    ) {
        if (player != null) {
            AnimatedMark(player)
        }
    }
}

@Composable
fun AnimatedMark(player: Player) {
    var animationProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(player) {
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        ) { value, _ ->
            animationProgress = value
        }
    }

    val color = if (player == Player.X) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = size.width * 0.15f
        
        if (player == Player.X) {
            // Draw X
            val path1 = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, size.height)
            }
            val path2 = Path().apply {
                moveTo(size.width, 0f)
                lineTo(0f, size.height)
            }
            
            val pm1 = PathMeasure().apply { setPath(path1, false) }
            val pm2 = PathMeasure().apply { setPath(path2, false) }
            
            val drawnPath1 = Path()
            val drawnPath2 = Path()
            
            // First line draws from 0 to 0.5 progress, second from 0.5 to 1.0
            val p1Progress = (animationProgress * 2f).coerceIn(0f, 1f)
            val p2Progress = ((animationProgress - 0.5f) * 2f).coerceIn(0f, 1f)
            
            pm1.getSegment(0f, pm1.length * p1Progress, drawnPath1, true)
            pm2.getSegment(0f, pm2.length * p2Progress, drawnPath2, true)
            
            drawPath(drawnPath1, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            drawPath(drawnPath2, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            
        } else {
            // Draw O
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animationProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}
