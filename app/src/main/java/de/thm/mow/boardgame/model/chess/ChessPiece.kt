package de.thm.mow.boardgame.model.chess

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
            if (belongs(Player.white)) return Player.white
            if (belongs(Player.black)) return Player.black
            return null
        }

    val value: Double
        get() {
            player?.let { p ->
                val sign = p.sign
                when (this) {
                    pawn(p) -> return sign * 1.0
                    knight(p) -> return sign * 3.0
                    bishop(p) -> return sign * 3.0
                    rook(p) -> return sign * 5.0
                    queen(p) -> return sign * 9.0
                    else -> return 0.0
                }
            }
            return 0.0
        }
}
