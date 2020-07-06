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
        fun getPawn(@argLabel("forPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhitePawn else BlackPawn
        }

        fun getKnight(@argLabel("forPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteKnight else BlackKnight
        }

        fun getBishop(@argLabel("forPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteBishop else BlackBishop
        }

        fun getRook(@argLabel("forPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteRook else BlackRook
        }

        fun getQueen(@argLabel("forPlayer") player: Player) : ChessPiece {
            return if (player == Player.white) WhiteQueen else BlackQueen
        }

        fun getKing(@argLabel("forPlayer") player: Player) : ChessPiece {
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
}
