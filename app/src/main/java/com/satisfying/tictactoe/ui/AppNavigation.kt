package com.satisfying.tictactoe.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.satisfying.tictactoe.GameMode

enum class Screen {
    HOME,
    LEVEL_SELECT,
    GAME,
    SETTINGS
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedMode by remember { mutableStateOf(GameMode.TWO_PLAYER) }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 350),
        label = "Screen Transition"
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                onNavigateToGame = { currentScreen = Screen.LEVEL_SELECT },
                onNavigateToSettings = { currentScreen = Screen.SETTINGS }
            )
            Screen.LEVEL_SELECT -> LevelSelectScreen(
                onModeSelected = { mode ->
                    selectedMode = mode
                    currentScreen = Screen.GAME
                },
                onNavigateBack = { currentScreen = Screen.HOME }
            )
            Screen.GAME -> GameScreen(
                initialGameMode = selectedMode,
                onNavigateHome = { currentScreen = Screen.HOME },
                onNavigateLevelSelect = { currentScreen = Screen.LEVEL_SELECT }
            )
            Screen.SETTINGS -> SettingsScreen(
                onNavigateBack = { currentScreen = Screen.HOME }
            )
        }
    }
}
