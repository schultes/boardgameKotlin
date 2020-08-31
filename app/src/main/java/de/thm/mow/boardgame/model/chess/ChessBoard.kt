package de.thm.mow.boardgame.model.chess

import de.thm.mow.boardgame.model.*
import de.thm.mow.boardgame.model.support.*

class ChessBoard(pieces: MutableList<ChessPiece>, var evaluation: Double = 0.0, var whiteKing: Coords = Coords(4, 7), var blackKing: Coords = Coords(4, 0), var twoStepsPawn: Coords? = null) : Board<ChessPiece>(ChessPiece.Invalid, pieces) {
    companion object {
        fun yIndex(@argLabel("ofRank") rank: Int, @argLabel("forPlayer") player: Player) : Int {
            return if (player == Player.white) 8 - rank else -1 + rank
        }
    }

    constructor() : this(MutableList<ChessPiece>(ChessPiece.Empty, 64))
    override fun clone() : Board<ChessPiece> {
        return ChessBoard(pieces.copy(), evaluation, whiteKing, blackKing, twoStepsPawn)
    }

    override fun applyChanges(@argLabel("_") changes: MutableList<Effect<ChessPiece>>) {
        // changes.size == 2 -> normal move or capture (incl. promotion); == 3 -> en passant; == 4 -> castling
        val source = changes.first()
        var target = changes.last()
        val capturedOpponent = changes.last()
        if (changes.size == 3) {
            // en passant
            target = changes[1]
        }

        val sourcePiece = this[source.coords]
        val targetPiece = target.newPiece
        val capturedPiece = this[capturedOpponent.coords]
        val p = targetPiece.player!!
        if (capturedPiece.belongs(p.opponent)) {
            evaluation -= capturedPiece.value(capturedOpponent.coords)
        }

        evaluation += targetPiece.value(target.coords) - sourcePiece.value(source.coords)
        if (targetPiece == ChessPiece.king(p)) {
            if (p == Player.white) {
                whiteKing = target.coords
            } else {
                blackKing = target.coords
            }
        }

        if (targetPiece == ChessPiece.pawn(p) && target.coords.y == ChessBoard.yIndex(4, p) && source.coords.y == ChessBoard.yIndex(2, p)) {
            twoStepsPawn = target.coords
        } else {
            twoStepsPawn = null
        }

        super.applyChanges(changes)
    }
}
