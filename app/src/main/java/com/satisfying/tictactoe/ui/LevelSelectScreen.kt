package com.satisfying.tictactoe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.satisfying.tictactoe.GameMode
import com.satisfying.tictactoe.theme.ElectricGold
import com.satisfying.tictactoe.theme.NeonCoral
import com.satisfying.tictactoe.theme.NeonCyan

data class LevelCard(
    val mode: GameMode,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val color: Color,
    val difficulty: String
)

@Composable
fun LevelSelectScreen(
    onModeSelected: (GameMode) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val levels = listOf(
        LevelCard(
            mode = GameMode.TWO_PLAYER,
            emoji = "👥",
            title = "VS FRIEND",
            subtitle = "2 Players · Local",
            description = "Pass the phone & battle your friend face to face. No mercy.",
            color = NeonCyan,
            difficulty = "HUMAN"
        ),
        LevelCard(
            mode = GameMode.AI_EASY,
            emoji = "🤖",
            title = "VS AI · EASY",
            subtitle = "1 Player · Beginner",
            description = "The AI is still learning. Perfect for warming up or crushing.",
            color = ElectricGold,
            difficulty = "EASY"
        ),
        LevelCard(
            mode = GameMode.AI_IMPOSSIBLE,
            emoji = "💀",
            title = "VS AI · BOSS",
            subtitle = "1 Player · Impossible",
            description = "Unbeatable Minimax AI. You CANNOT win. Challenge accepted?",
            color = NeonCoral,
            difficulty = "IMPOSSIBLE"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02000A))
    ) {
        AnimatedGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "< BACK",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = NeonCoral,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.clickable {
                        HapticEngine.click(context)
                        onNavigateBack()
                    }
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "SELECT MODE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = "CHOOSE YOUR DESTINY",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = NeonCyan,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Level cards
            levels.forEach { level ->
                LevelCardItem(
                    card = level,
                    onSelect = {
                        HapticEngine.heavyImpact(context)
                        onModeSelected(level.mode)
                    }
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun LevelCardItem(
    card: LevelCard,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "cardGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(
                elevation = 16.dp,
                shape = CutCornerShape(16.dp),
                ambientColor = card.color,
                spotColor = card.color
            )
            .clip(CutCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        card.color.copy(alpha = 0.12f),
                        Color(0xFF0D1117)
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .drawBehind {
                // Bright top-left bevel highlight
                drawRoundRect(
                    brush = Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                )
                // Glowing border
                drawRoundRect(
                    color = card.color.copy(alpha = glowAlpha),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.8f)
                )
            }
            .clickable(interactionSource = interactionSource, indication = null, onClick = onSelect)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Giant emoji
            Text(
                text = card.emoji,
                fontSize = 52.sp,
                modifier = Modifier.size(72.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Difficulty badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(card.color.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = card.difficulty,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = card.color,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = card.title,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = card.subtitle,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = card.color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = card.description,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 16.sp
                )
            }

            // Arrow indicator
            Text(
                text = "▶",
                fontSize = 18.sp,
                color = card.color.copy(alpha = glowAlpha)
            )
        }
    }
}
