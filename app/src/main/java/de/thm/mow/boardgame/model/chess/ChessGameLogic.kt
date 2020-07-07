package de.thm.mow.boardgame.model.chess

import de.thm.mow.boardgame.model.*
import de.thm.mow.boardgame.model.support.*

class ChessGameLogic : GameLogic<ChessPiece> {
    private fun firstRank(@argLabel("forPlayer") player: Player) : Int {
        return if (player == Player.white) 7 else 0
    }

    private fun secondRank(@argLabel("forPlayer") player: Player) : Int {
        return if (player == Player.white) 6 else 1
    }

    private fun kingCoords(board: Board<ChessPiece>, player: Player) : Coords {
        return if (player == Player.white) (board as ChessBoard).whiteKing else (board as ChessBoard).blackKing
    }

    private fun isThreatened(board: Board<ChessPiece>, player: Player, c: Coords) : Boolean {
        val yDir = if (player == Player.white) -1 else +1
        for (x in intArrayOf(-1, +1)) {
            if (board[c.x + x, c.y + yDir] == ChessPiece.pawn(player.opponent)) return true
        }

        for (y in intArrayOf(-2, -1, +1, +2)) {
            for (x in intArrayOf(-1, +1)) {
                val xFactor = if (y == 2 || y == -2) 1 else 2
                if (board[c.x + x * xFactor, c.y + y] == ChessPiece.knight(player.opponent)) return true
            }
        }

        for (y in -1..+1) {
            for (x in -1..+1) {
                if (x == 0 && y == 0) continue
                val isStraight = x == 0 || y == 0
                val isDiagonal = !isStraight
                for (d in 1..7) {
                    val currentPiece = board[c.x + x * d, c.y + y * d]
                    if (currentPiece == ChessPiece.king(player.opponent) && d == 1) return true
                    if (currentPiece == ChessPiece.queen(player.opponent)) return true
                    if (currentPiece == ChessPiece.rook(player.opponent) && isStraight) return true
                    if (currentPiece == ChessPiece.bishop(player.opponent) && isDiagonal) return true
                    if (currentPiece != ChessPiece.Empty) break
                }
            }
        }

        return false
    }

    private fun isInCheck(board: Board<ChessPiece>, player: Player) : Boolean {
        return isThreatened(board, player, kingCoords(board, player))
    }

    override fun getInitialBoard(): Board<ChessPiece> {
        val board = ChessBoard()
        for (p in arrayOf(Player.white, Player.black)) {
            var rank = firstRank(p)
            board[0, rank] = ChessPiece.rook(p)
            board[1, rank] = ChessPiece.knight(p)
            board[2, rank] = ChessPiece.bishop(p)
            board[3, rank] = ChessPiece.queen(p)
            board[4, rank] = ChessPiece.king(p)
            board[5, rank] = ChessPiece.bishop(p)
            board[6, rank] = ChessPiece.knight(p)
            board[7, rank] = ChessPiece.rook(p)

            rank = secondRank(p)
            for (x in 0..7) board[x, rank] = ChessPiece.pawn(p)
        }
        return board
    }

    override fun getMoves(@argLabel("onBoard") board: Board<ChessPiece>, @argLabel("forPlayer") player: Player, @argLabel("forSourceCoords") sc: Coords): MutableList<Move<ChessPiece>> {
        val moves = mutableListOf<Move<ChessPiece>>()
        addMoves(moves, board, player, sc)
        return moves
    }

    private fun addMoves(moves: MutableList<Move<ChessPiece>>, board: Board<ChessPiece>, player: Player, sc: Coords) {
        val srcPiece = board[sc.x, sc.y]

        if (srcPiece == ChessPiece.pawn(player)) {
            val yDir = if (player == Player.white) -1 else +1
            if (sc.y == secondRank(player) && board[sc.x, sc.y + yDir] == ChessPiece.Empty) {
                addMove(moves, board, player, sc, 0, 2 * yDir, true, false)
            }
            addMove(moves, board, player, sc, 0, yDir, true, false)
            for (x in intArrayOf(-1, +1)) {
                addMove(moves, board, player, sc, x, yDir, false, true)
            }
        }

        if (srcPiece == ChessPiece.knight(player)) {
            for (y in intArrayOf(-2, -1, +1, +2)) {
                for (x in intArrayOf(-1, +1)) {
                    val xFactor = if (y == 2 || y == -2) 1 else 2
                    addMove(moves, board, player, sc, x * xFactor, y)
                }
            }
        }

        if (srcPiece == ChessPiece.king(player) || srcPiece == ChessPiece.queen(player) || srcPiece == ChessPiece.bishop(player) || srcPiece == ChessPiece.rook(player)) {
            val maxDistance = if (srcPiece == ChessPiece.king(player)) 1 else 7
            val straight = if (srcPiece == ChessPiece.bishop(player)) false else true
            val diagonal = if (srcPiece == ChessPiece.rook(player)) false else true
            for (y in -1..+1) {
                for (x in -1..+1) {
                    if (x == 0 && y == 0) continue
                    if (!straight && (x == 0 || y == 0)) continue
                    if (!diagonal && x != 0 && y != 0) continue
                    for (d in 1..maxDistance) {
                        addMove(moves, board, player, sc, x * d, y * d)
                        if (board[sc.x + x * d, sc.y + y * d] != ChessPiece.Empty) break
                    }
                }
            }
        }

        // Castling
        // (At the moment, we don't check whether King or Rook have been moved before.)
        if (srcPiece == ChessPiece.king(player) && !isInCheck(board, player)) {
            for (x in intArrayOf(-1, +1)) {
                val queenside = x == -1
                val rookTarget = Coords(sc.x + 1 * x, sc.y)
                val kingTarget = Coords(sc.x + 2 * x, sc.y)
                val rookSource = if (queenside) Coords(sc.x + 4 * x, sc.y) else Coords(sc.x + 3 * x, sc.y)
                var allowed = true
                for (c in arrayOf(rookTarget, kingTarget)) {
                    if (board[c] != ChessPiece.Empty || isThreatened(board, player, c)) allowed = false
                }
                if (board[rookSource] != ChessPiece.rook(player)) allowed = false
                if (queenside && board[sc.x - 3, sc.y] != ChessPiece.Empty) allowed = false
                if (allowed) {
                    val effects = mutableListOf(Effect(sc, ChessPiece.Empty), Effect(rookTarget, ChessPiece.rook(player)), Effect(rookSource, ChessPiece.Empty), Effect(kingTarget, ChessPiece.king(player)))
                    moves += Move<ChessPiece>(sc, mutableListOf(Step(kingTarget, effects)), null)
                }
            }
        }
    }

    private fun addMove(moves: MutableList<Move<ChessPiece>>, board: Board<ChessPiece>, player: Player, sc: Coords, deltaX: Int, deltaY: Int, moveAllowed: Boolean = true, captureAllowed: Boolean = true) {
        val tc = Coords(sc.x + deltaX, sc.y + deltaY)
        if (moveAllowed && board[tc.x, tc.y] == ChessPiece.Empty || captureAllowed && board[tc.x, tc.y].belongs(player.opponent)) {
            var targetPiece = board[sc.x, sc.y]
            if (targetPiece == ChessPiece.pawn(player) && tc.y == firstRank(player.opponent)) {
                targetPiece = ChessPiece.queen(player) // promotion
            }
            val effects = mutableListOf(Effect(sc, ChessPiece.Empty), Effect(tc, targetPiece))
            val newMove = Move<ChessPiece>(sc, mutableListOf(Step(tc, effects)), null)
            val newBoard = board.clone()
            newBoard.applyChanges(effects)
            if (!isInCheck(newBoard, player)) {
                moves += newMove
            }
        }
    }

    override fun getMoves(board: Board<ChessPiece>, player: Player): MutableList<Move<ChessPiece>> {
        val allMoves = mutableListOf<Move<ChessPiece>>()
        for (x in 0..7) {
            for (y in 0..7) {
                addMoves(allMoves, board, player, Coords(x, y))
            }
        }
        return allMoves
    }

    override fun evaluateBoard(board: Board<ChessPiece>): Double {
        var value = 0.0
        for (p in arrayOf(Player.white, Player.black)) {
            val sign = if (p == Player.white) +1 else -1
            if (isInCheck(board, p) && getMoves(board, p).isEmpty()) value -= sign * 100.0
        }
        value += (board as ChessBoard).evaluation

        return value
    }

    override fun getResult(@argLabel("onBoard") board: Board<ChessPiece>, @argLabel("forPlayer") player: Player): GameResult {
        var finished = false
        var winner: Player? = null
        if (getMoves(board, player).isEmpty()) {
            finished = true
            winner = if (isInCheck(board, player)) player.opponent else null
        }

        return GameResult(finished, winner)
    }
}