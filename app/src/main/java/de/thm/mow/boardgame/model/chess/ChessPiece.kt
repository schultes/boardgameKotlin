package de.thm.mow.boardgame.model.chess

import de.thm.mow.boardgame.model.Coords
import de.thm.mow.boardgame.model.Player
import de.thm.mow.boardgame.model.support.*

enum class ChessPiece(val rawValue: String) {
    Empty("  "),
    Invalid("x"),
    WhitePawn("♙"),
    BlackPawn("♟"),
    WhiteKnight("♘"),
    BlackKnight("♞"),
    WhiteBishop("♗"),
    BlackBishop("♝"),
    WhiteRook("♖"),
    BlackRook("♜"),
    WhiteQueen("♕"),
    BlackQueen("♛"),
    WhiteKing("♔"),
    BlackKing("♚");
    companion object {
        fun pawn(@argLabel("ofPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhitePawn else BlackPawn
        }

        fun knight(@argLabel("ofPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteKnight else BlackKnight
        }

        fun bishop(@argLabel("ofPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteBishop else BlackBishop
        }

        fun rook(@argLabel("ofPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteRook else BlackRook
        }

        fun queen(@argLabel("ofPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteQueen else BlackQueen
        }

        fun king(@argLabel("ofPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteKing else BlackKing
        }

        fun pawnValue(@argLabel("at") c: Coords, @argLabel("forPlayer") p: Player) : Double {
            val borderRankValues = doubleArrayOf(0.0, -0.01, -0.05, 0.0, 0.1, 0.2)
            var result = 1.0
            val yDelta = (c.y - ChessBoard.yIndex(2, p)).absoluteValue
            when (c.x) {
                3, 4 -> result += yDelta * 0.08
                else -> result += borderRankValues[yDelta]
            }
            return result
        }

        fun knightValue(@argLabel("at") c: Coords) : Double {
            return 3.0 + centerPreferringValue(c, 0.05)
        }

        fun bishopValue(@argLabel("at") c: Coords) : Double {
            return 3.0 + centerPreferringValue(c, 0.02)
        }

        fun rookValue(@argLabel("at") c: Coords) : Double {
            return 5.0 + centerPreferringValue(c, 0.01)
        }

        fun queenValue(@argLabel("at") c: Coords) : Double {
            return 9.0 + centerPreferringValue(c, 0.02)
        }

        fun kingValue(@argLabel("at") c: Coords, @argLabel("forPlayer") p: Player) : Double {
            if (c.y != ChessBoard.yIndex(1, p)) return -0.05
            val xValues = doubleArrayOf(0.1, 0.2, 0.1, 0.0, 0.0, 0.1, 0.2, 0.1)
            return xValues[c.x]
        }

        private fun centerPreferringValue(@argLabel("at") c: Coords, @argLabel("withWeight") w: Double) : Double {
            var result = 0.0
            val xFromCenter = (3.5 - c.x).absoluteValue - 0.5
            val yFromCenter = (3.5 - c.y).absoluteValue - 0.5
            result -= xFromCenter * w
            result -= yFromCenter * w
            return result
        }
    }

    override fun toString(): String {
        return rawValue
    }

    fun belongs(@argLabel("toPlayer") player: Player) : Boolean {
        if (((this == WhitePawn) || (this == WhiteKnight) || (this == WhiteBishop) || (this == WhiteRook) || (this == WhiteQueen) || (this == WhiteKing)) && (player == Player.white)) {
            return true
        }

        if (((this == BlackPawn) || (this == BlackKnight) || (this == BlackBishop) || (this == BlackRook) || (this == BlackQueen) || (this == BlackKing)) && (player == Player.black)) {
            return true
        }

        return false
    }

    val player: Player?
        get() {
            if (belongs(Player.white)) {
                return Player.white
            }

            if (belongs(Player.black)) {
                return Player.black
            }

            return null
        }

    fun value(c: Coords) : Double {
        player?.let { p ->
            val sign = p.sign
            when (this) {
                ChessPiece.pawn(p) -> return sign * ChessPiece.pawnValue(c, p)
                ChessPiece.knight(p) -> return sign * ChessPiece.knightValue(c)
                ChessPiece.bishop(p) -> return sign * ChessPiece.bishopValue(c)
                ChessPiece.rook(p) -> return sign * ChessPiece.rookValue(c)
                ChessPiece.queen(p) -> return sign * ChessPiece.queenValue(c)
                ChessPiece.king(p) -> return sign * ChessPiece.kingValue(c, p)
                else -> return 0.0
            }
        }

        return 0.0
    }
}
