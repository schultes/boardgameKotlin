package de.thm.mow.boardgame.model.checkers

import de.thm.mow.boardgame.model.*
import de.thm.mow.boardgame.model.support.*

class CheckersGameLogic : GameLogic<CheckersPiece> {
    override fun getInitialBoard() : Board<CheckersPiece> {
        val board = Board<CheckersPiece>(CheckersPiece.Empty, CheckersPiece.Invalid)
        for (x in 0 until board.columns) {
            for (y in 0 until 3) {
                if ((x + y) % 2 == 1) {
                    board[x, y] = CheckersPiece.BlackMan
                }
            }

            for (y in board.rows - 3 until board.rows) {
                if ((x + y) % 2 == 1) {
                    board[x, y] = CheckersPiece.WhiteMan
                }
            }
        }

        return board
    }

    override fun getMoves(@argLabel("onBoard") board: Board<CheckersPiece>, @argLabel("forPlayer") player: Player, @argLabel("forSourceCoords") sc: Coords) : MutableList<Move<CheckersPiece>> {
        val result = mutableListOf<Move<CheckersPiece>>()
        val allMoves = getMoves(board, player)
        for (move in allMoves) {
            if (move.source.x == sc.x && move.source.y == sc.y) {
                result.add(move)
            }
        }

        return result
    }

    override fun getMoves(@argLabel("onBoard") board: Board<CheckersPiece>, @argLabel("forPlayer") player: Player) : MutableList<Move<CheckersPiece>> {
        return getMoves(board, player, false)
    }

    private fun getMoves(@argLabel("onBoard") board: Board<CheckersPiece>, @argLabel("forPlayer") player: Player, ignoreCaptureObligation: Boolean) : MutableList<Move<CheckersPiece>> {
        val normalMoves = mutableListOf<Move<CheckersPiece>>()
        val captureMoves = mutableListOf<Move<CheckersPiece>>()
        val forwardDirection = if (player == Player.white) -1 else 1
        for (x in 0 until board.columns) {
            for (y in 0 until board.rows) {
                val sc: Coords = Coords(x, y)
                val sourcePiece = board[sc.x, sc.y]
                if (sourcePiece.belongs(player)) {
                    val sourcePieceIsMan = (sourcePiece == CheckersPiece.getMan(player))
                    val range = if (sourcePieceIsMan) 1..1 else 1..board.rows - 1
                    val yDirections = if (sourcePieceIsMan) intArrayOf(forwardDirection) else intArrayOf(-1, 1)
                    for (dy in yDirections) {
                        for (dx in intArrayOf(-1, 1)) {
                            for (i in range) {
                                val tx = sc.x + i * dx
                                val ty = sc.y + i * dy
                                val tc: Coords = Coords(tx, ty)
                                if (board[tx, ty] != CheckersPiece.Empty) {
                                    break
                                }

                                val targetPiece = getTargetPiece(board, player, tc, sourcePiece)
                                normalMoves.add(Move<CheckersPiece>(sc, mutableListOf(Step(tc, mutableListOf(Effect(sc, CheckersPiece.Empty), Effect(tc, targetPiece))))))
                            }
                        }
                    }

                    // capture
                    val arrayOfSteps = recursiveCapture(board, player, sc, range, yDirections)
                    for (steps in arrayOfSteps) {
                        captureMoves.add(Move<CheckersPiece>(sc, steps))
                    }
                }
            }
        }

        if ((ignoreCaptureObligation)) {
            return captureMoves + normalMoves
        }

        return if (!captureMoves.isEmpty()) captureMoves else normalMoves
    }

    private fun recursiveCapture(@argLabel("onBoard") board: Board<CheckersPiece>, @argLabel("forPlayer") player: Player, @argLabel("forCurrentCoords") cc: Coords, @argLabel("withRange") range: IntRange, @argLabel("inYdirections") yDirections: IntArray) : MutableList<MutableList<Step<CheckersPiece>>> {
        val result = mutableListOf<MutableList<Step<CheckersPiece>>>()
        for (dy in yDirections) {
            for (dx in intArrayOf(-1, 1)) {
                for (i in range) {
                    val tx = cc.x + i * dx
                    val ty = cc.y + i * dy
                    val tc: Coords = Coords(tx, ty)
                    if (board[tx, ty].belongs(player)) {
                        break
                    }

                    if (board[tx, ty].belongs(player.opponent)) {
                        val t2x = tx + dx
                        val t2y = ty + dy
                        val t2c: Coords = Coords(t2x, t2y)
                        if (board[t2x, t2y] == CheckersPiece.Empty) {
                            val sourcePiece = board[cc.x, cc.y]
                            val targetPiece = getTargetPiece(board, player, t2c, sourcePiece)
                            val effects: MutableList<Effect<CheckersPiece>> = mutableListOf(Effect(cc, CheckersPiece.Empty), Effect(tc, CheckersPiece.Empty), Effect(t2c, targetPiece))
                            val thisSteps = arrayOf(Step(t2c, effects))
                            if (sourcePiece != targetPiece) {
                                // promotion took place (man -> king): stop recursion!
                                result.add(thisSteps)
                            } else {
                                val newBoard = board.changedCopy(effects)
                                val arrayOfSubsequentSteps = recursiveCapture(newBoard, player, t2c, range, yDirections)
                                if (arrayOfSubsequentSteps.isEmpty()) {
                                    result.add(thisSteps)
                                } else {
                                    for (subsequentSteps in arrayOfSubsequentSteps) {
                                        val concatenatedSteps = thisSteps + subsequentSteps
                                        result.add(concatenatedSteps)
                                    }
                                }
                            }
                        }

                        break
                    }
                }
            }
        }

        return result
    }

    private fun getTargetPiece(@argLabel("onBoard") board: Board<CheckersPiece>, @argLabel("forPlayer") player: Player, @argLabel("atCoords") coords: Coords, @argLabel("forSourcePiece") sourcePiece: CheckersPiece) : CheckersPiece {
        val finalRow = if (player == Player.white) 0 else board.rows - 1
        if ((coords.y == finalRow)) {
            return CheckersPiece.getKing(player)
        }

        return sourcePiece
    }

    override fun evaluateBoard(@argLabel("_") board: Board<CheckersPiece>, @argLabel("forPlayer") player: Player) : Double {
        var result = 0.0
        for (x in 0 until board.columns) {
            for (y in 0 until board.rows) {
                if (board[x, y].belongs(Player.white)) {
                    result++
                }

                if (board[x, y] == CheckersPiece.WhiteKing) {
                    result += 3
                }

                if (board[x, y].belongs(Player.black)) {
                    result--
                }

                if (board[x, y] == CheckersPiece.BlackKing) {
                    result -= 3
                }
            }
        }

        // mobility
        val whiteMoves = getMoves(board, Player.white, true)
        val blackMoves = getMoves(board, Player.black, true)
        result += Double(whiteMoves.size)
        result -= Double(blackMoves.size)
        return result
    }

    override fun getResult(@argLabel("onBoard") board: Board<CheckersPiece>, @argLabel("forPlayer") player: Player) : GameResult {
        val movesOfCurrentPlayer = getMoves(board, player)
        if (movesOfCurrentPlayer.isEmpty()) {
            return GameResult(true, player.opponent)
        }

        return GameResult(false, null)
    }
}
