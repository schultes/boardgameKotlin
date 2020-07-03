package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.support.*

@tuple data class GameResult(var finished: Boolean, var winner: Player?) {}

interface GameLogic<P> {
    fun getInitialBoard() : Board<P>
    fun getMoves(@argLabel("onBoard") board: Board<P>, forPlayer: Player, forSourceCoords: Coords) : MutableList<Move<P>>
    fun getMoves(@argLabel("onBoard") board: Board<P>, @argLabel("forPlayer") player: Player) : MutableList<Move<P>>
    fun evaluateBoard(@argLabel("_") board: Board<P>) : Double
    fun getResult(@argLabel("onBoard") board: Board<P>, forPlayer: Player) : GameResult
}
