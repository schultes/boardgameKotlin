package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.support.*

class AI<P, GL : GameLogic<P>>(val logic: GL) {
    var maxSearchDepth = 2
    fun performMove(@argLabel("onBoard") board: Board<P>, @argLabel("forPlayer") player: Player, finished: () -> Unit) {
        val allMoves = logic.getMoves(board, player)
        allMoves.asyncMap({getValue(board.changedCopy(it), player.opponent,maxSearchDepth - 2)}, {
            var bestValue = 0.0
            val bestMoves = mutableListOf<Move<P>>()
            allMoves.zip(it).forEach {
                val currentValue = it.second
                val almostTheSame = bestMoves.isNotEmpty() && (bestValue - currentValue).absoluteValue < 0.02
                val improvement = bestMoves.isEmpty() || (currentValue > bestValue) == player.isMaximizing
                if (improvement && !almostTheSame) {
                    bestMoves.clear()
                }

                if (improvement || almostTheSame) {
                    bestValue = currentValue
                    bestMoves.add(it.first)
                }
            }

            if (bestMoves.isNotEmpty()) {
                board.applyChanges(bestMoves.random())
            }

            finished()
        })
    }

    private fun getValue(@argLabel("onBoard") board: Board<P>, @argLabel("forPlayer") player: Player, @argLabel("withDepth") depth: Int) : Double {
        val allMoves = if (depth < 0) mutableListOf() else logic.getMoves(board, player)
        var bestValue: Double? = null
        allMoves.forEach {
            val value = getValue(board.changedCopy(it), player.opponent, depth - 1)
            if (bestValue == null || (value > bestValue!!) == player.isMaximizing) {
                bestValue = value
            }
        }

        return bestValue ?: logic.evaluateBoard(board, player)
    }
}
