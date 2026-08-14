package com.satisfying.tictactoe

enum class Player {
    X, O
}

data class WinResult(
    val winner: Player,
    val winLine: List<Int> // Indices of winning cells (0-8)
)

sealed class GameState {
    object Playing : GameState()
    data class Won(val result: WinResult) : GameState()
    object Draw : GameState()
}

data class GameUiState(
    val board: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.X,
    val gameState: GameState = GameState.Playing,
    val scoreX: Int = 0,
    val scoreO: Int = 0
)
