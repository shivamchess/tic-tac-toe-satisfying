package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.GameMode
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan

private data class LevelInfo(
    val mode: GameMode,
    val emoji: String,
    val title: String,
    val tag: String,
    val description: String,
    val color: Color
)

@Composable
fun LevelSelectScreen(
    onModeSelected: (GameMode) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val levels = listOf(
        LevelInfo(
            mode = GameMode.TWO_PLAYER,
            emoji = "👥",
            title = "2 Players",
            tag = "Pass & Play",
            description = "Take turns on the same phone. Perfect for playing with friends.",
            color = NeonCyan
        ),
        LevelInfo(
            mode = GameMode.AI_EASY,
            emoji = "🤖",
            title = "vs AI  ·  Easy",
            tag = "Beginner",
            description = "The AI makes some mistakes. Good for learning or casual play.",
            color = ElectricGold
        ),
        LevelInfo(
            mode = GameMode.AI_IMPOSSIBLE,
            emoji = "💀",
            title = "vs AI  ·  Boss",
            tag = "Unbeatable",
            description = "Minimax algorithm. Perfect play. You literally cannot win.",
            color = NeonCoral
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080F))
    ) {
        AnimatedGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 24.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.clickable {
                        HapticEngine.click(context)
                        onNavigateBack()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Choose Mode",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "How do you want to play?",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            levels.forEach { level ->
                LevelCard(level = level) {
                    HapticEngine.heavyImpact(context)
                    onModeSelected(level.mode)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun LevelCard(level: LevelInfo, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "card")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "g"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(12.dp, RoundedCornerShape(22.dp), ambientColor = level.color, spotColor = level.color)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF10111A))
            .drawBehind {
                // Subtle left accent bar
                drawRoundRect(
                    color = level.color.copy(alpha = glowAlpha),
                    topLeft = Offset(0f, size.height * 0.15f),
                    size = androidx.compose.ui.geometry.Size(5.dp.toPx(), size.height * 0.7f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
                )
                // Faint top gradient wash
                drawRoundRect(
                    brush = Brush.linearGradient(
                        listOf(level.color.copy(alpha = 0.06f), Color.Transparent),
                        start = Offset.Zero,
                        end = Offset(size.width * 0.5f, size.height)
                    ),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(22.dp.toPx())
                )
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji in a tinted pill
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(level.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = level.emoji, fontSize = 30.sp)
        }

        Spacer(modifier = Modifier.width(18.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Title + tag on same line
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = level.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(level.color.copy(alpha = 0.18f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = level.tag,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = level.color,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = level.description,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.45f),
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Chevron
        Text(
            text = "›",
            fontSize = 26.sp,
            color = level.color.copy(alpha = glowAlpha)
        )
    }
}
