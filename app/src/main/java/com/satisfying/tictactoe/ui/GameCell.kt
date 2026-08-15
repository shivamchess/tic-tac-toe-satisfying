package com.satisfying.tictactoe.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.satisfying.tictactoe.Player
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCoralGlow
import com.satisfying.tictactoe.theme.NeonCyan
import com.satisfying.tictactoe.theme.NeonCyanGlow
import com.satisfying.tictactoe.theme.SurfaceDark
import com.satisfying.tictactoe.theme.SurfaceElevated

fun Modifier.satisfyingBounce(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cellScale"
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
    isWinningCell: Boolean,
    onClick: () -> Unit,
    onPositioned: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val context = LocalContext.current

    // Spring pulse when marked
    val markScale = remember { Animatable(if (player != null) 1f else 0.5f) }
    LaunchedEffect(player) {
        if (player != null) {
            markScale.snapTo(0.3f)
            markScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp)
            .shadow(
                elevation = if (isWinningCell) 12.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = if (isWinningCell) NeonCyan else Color.Black,
                spotColor = if (isWinningCell) NeonCoral else Color.Black
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isWinningCell) listOf(SurfaceElevated, SurfaceDark)
                    else listOf(SurfaceElevated.copy(alpha = 0.8f), SurfaceDark.copy(alpha = 0.9f))
                )
            )
            .border(
                width = if (isWinningCell) 2.dp else 1.dp,
                brush = if (isWinningCell) Brush.linearGradient(listOf(NeonCyan, NeonCoral))
                else Brush.linearGradient(listOf(Color(0x334ECDC4), Color(0x11FFFFFF))),
                shape = RoundedCornerShape(20.dp)
            )
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                val center = Offset(pos.x + coordinates.size.width / 2f, pos.y + coordinates.size.height / 2f)
                onPositioned(center)
            }
            .satisfyingBounce(enabled = player == null) {
                // Rich tactile haptic
                try {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator != null) {
                        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }
                } catch (_: Exception) {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                }
                onClick()
            }
            .padding(14.dp)
    ) {
        if (player != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = markScale.value
                        scaleY = markScale.value
                    }
            ) {
                GlowingAnimatedMark(player = player, isWinning = isWinningCell)
            }
        }
    }
}

@Composable
fun GlowingAnimatedMark(player: Player, isWinning: Boolean) {
    val drawProgress = remember { Animatable(0f) }

    LaunchedEffect(player) {
        drawProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
        )
    }

    val primaryColor = if (player == Player.X) NeonCyan else NeonCoral
    val glowColor = if (player == Player.X) NeonCyanGlow else NeonCoralGlow

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = size.width * 0.16f
        val glowStrokeWidth = strokeWidth * 2.2f
        val progress = drawProgress.value

        if (player == Player.X) {
            val padding = size.width * 0.12f
            val left = padding
            val top = padding
            val right = size.width - padding
            val bottom = size.height - padding

            val path1 = Path().apply {
                moveTo(left, top)
                lineTo(right, bottom)
            }
            val path2 = Path().apply {
                moveTo(right, top)
                lineTo(left, bottom)
            }

            val pm1 = PathMeasure().apply { setPath(path1, false) }
            val pm2 = PathMeasure().apply { setPath(path2, false) }

            val p1Progress = (progress * 2f).coerceIn(0f, 1f)
            val p2Progress = ((progress - 0.5f) * 2f).coerceIn(0f, 1f)

            val dPath1 = Path()
            val dPath2 = Path()
            pm1.getSegment(0f, pm1.length * p1Progress, dPath1, true)
            pm2.getSegment(0f, pm2.length * p2Progress, dPath2, true)

            // Neon Outer Glow
            drawPath(dPath1, color = glowColor, style = Stroke(width = glowStrokeWidth, cap = StrokeCap.Round))
            drawPath(dPath2, color = glowColor, style = Stroke(width = glowStrokeWidth, cap = StrokeCap.Round))

            // Neon Core
            drawPath(dPath1, color = primaryColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            drawPath(dPath2, color = primaryColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

            // Hot White Center highlight
            drawPath(dPath1, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = strokeWidth * 0.35f, cap = StrokeCap.Round))
            drawPath(dPath2, color = Color.White.copy(alpha = 0.7f), style = Stroke(width = strokeWidth * 0.35f, cap = StrokeCap.Round))
        } else {
            val padding = size.width * 0.12f
            val arcSize = Size(size.width - (padding * 2f), size.height - (padding * 2f))
            val arcOffset = Offset(padding, padding)

            // Neon Outer Glow
            drawArc(
                color = glowColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = glowStrokeWidth, cap = StrokeCap.Round)
            )

            // Neon Core
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Hot White Center highlight
            drawArc(
                color = Color.White.copy(alpha = 0.7f),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth * 0.35f, cap = StrokeCap.Round)
            )
        }
    }
}
