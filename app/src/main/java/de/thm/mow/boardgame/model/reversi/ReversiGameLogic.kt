package de.thm.mow.boardgame.model.reversi

import de.thm.mow.boardgame.model.*
import de.thm.mow.boardgame.model.support.*

class ReversiGameLogic :
    GameLogic<ReversiPiece> {
    override fun getInitialBoard() : Board<ReversiPiece> {
        val board =
            Board<ReversiPiece>(
                ReversiPiece.Empty,
                ReversiPiece.Invalid
            )
        val x = board.columns / 2 - 1
        val y = board.rows / 2 - 1
        board[x, y] = ReversiPiece.White
        board[x, y + 1] = ReversiPiece.Black
        board[x + 1, y] = ReversiPiece.Black
        board[x + 1, y + 1] = ReversiPiece.White
        return board
    }

    override fun getMoves(@argLabel("onBoard") board: Board<ReversiPiece>, @argLabel("forPlayer") player: Player, @argLabel("forSourceCoords") sourceCoords: Coords) : MutableList<Move<ReversiPiece>> {
        val result = mutableListOf<Move<ReversiPiece>>()
        val playersPiece =
            ReversiPiece.getPiece(player)
        if (board[sourceCoords.x, sourceCoords.y] == ReversiPiece.Empty) {
            val opponent = player.opponent
            val allChanges = mutableListOf<Effect<ReversiPiece>>()
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) {
                        continue
                    }

                    val tmp = mutableListOf<Effect<ReversiPiece>>()
                    var x = sourceCoords.x + dx
                    var y = sourceCoords.y + dy
                    while (board[x, y].belongs(opponent)) {
                        val newElement = arrayOf(
                            Effect(
                                Coords(x, y),
                                playersPiece
                            )
                        )
                        tmp += newElement
                        x += dx
                        y += dy
                    }

                    if ((tmp.isEmpty())) {
                        continue
                    }

                    if ((board[x, y].belongs(player))) {
                        allChanges += tmp
                    }
                }
            }

            if ((!allChanges.isEmpty())) {
                val newElement = arrayOf(
                    Effect(
                        sourceCoords,
                        playersPiece
                    )
                )
                allChanges += newElement
                val move =
                    Move<ReversiPiece>(
                        sourceCoords,
                        mutableListOf(
                            Step(
                                sourceCoords,
                                allChanges
                            )
                        ),
                        null
                    )
                result.add(move)
            }
        }

        return result
    }

    override fun getMoves(@argLabel("onBoard") board: Board<ReversiPiece>, @argLabel("forPlayer") player: Player) : MutableList<Move<ReversiPiece>> {
        val result = mutableListOf<Move<ReversiPiece>>()
        for (x in 0 until board.columns) {
            for (y in 0 until board.rows) {
                result += getMoves(board, player,
                    Coords(x, y)
                )
            }
        }

        return result
    }

    override fun evaluateBoard(@argLabel("_") board: Board<ReversiPiece>) : Double {
        var result = 0.0
        for (x in 0 until board.columns) {
            for (y in 0 until board.rows) {
                if ((board[x, y].belongs(Player.white))) {
                    result++
                }

                if ((board[x, y].belongs(Player.black))) {
                    result--
                }
            }
        }

        return result
    }

    override fun getResult(@argLabel("onBoard") board: Board<ReversiPiece>, @argLabel("forPlayer") Ꮻ_: Player) : GameResult {
        var finished = true
        var winner: Player? = null
        val movesOfBothPlayers = arrayOf(getMoves(board,
            Player.white
        ), getMoves(board, Player.black))
        for (movesOfOnePlayer in movesOfBothPlayers) {
            if (!movesOfOnePlayer.isEmpty()) {
                finished = false
            }
        }

        if ((finished)) {
            if ((evaluateBoard(board) > 0)) {
                winner = Player.white
            }

            if ((evaluateBoard(board) < 0)) {
                winner = Player.black
            }
        }

        return GameResult(finished, winner)
    }
}
