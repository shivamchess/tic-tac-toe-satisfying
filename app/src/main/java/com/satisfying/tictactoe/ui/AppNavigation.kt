package com.satisfying.tictactoe.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class Screen {
    HOME,
    GAME,
    SETTINGS
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 400),
        label = "Screen Transition"
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                onNavigateToGame = { currentScreen = Screen.GAME },
                onNavigateToSettings = { currentScreen = Screen.SETTINGS }
            )
            Screen.GAME -> GameScreen(
                onNavigateHome = { currentScreen = Screen.HOME }
            )
            Screen.SETTINGS -> SettingsScreen(
                onNavigateBack = { currentScreen = Screen.HOME }
            )
        }
    }
}
