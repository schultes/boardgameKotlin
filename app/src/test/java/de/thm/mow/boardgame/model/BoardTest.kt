package de.thm.mow.boardgame.model

import de.thm.mow.boardgame.model.checkers.CheckersPiece
import org.junit.Assert
import org.junit.Test

class BoardTest {
    @Test
    fun boardTest() {
        val board = Board(CheckersPiece.Empty, CheckersPiece.Invalid)
        board[2, 3] = CheckersPiece.BlackKing
        board[4, 7] = CheckersPiece.WhiteMan

        Assert.assertTrue(board[0, 0] == CheckersPiece.Empty)
        Assert.assertTrue(board[7, 7] == CheckersPiece.Empty)
        Assert.assertTrue(board[8, 7] == CheckersPiece.Invalid)
        Assert.assertTrue(board[7, 8] == CheckersPiece.Invalid)
        Assert.assertTrue(board[2, 3] == CheckersPiece.BlackKing)
        Assert.assertTrue(board[4, 7] == CheckersPiece.WhiteMan)

        val effects = mutableListOf(Effect(Coords(1,1), CheckersPiece.BlackMan), Effect(Coords(2,2), CheckersPiece.BlackMan))
        val steps = mutableListOf(Step(Coords(1,1), effects))
        val move = Move(Coords(0,0), steps)
        board.applyChanges(move.steps[0].effects)

        Assert.assertTrue(board[0, 0] == CheckersPiece.Empty)
        Assert.assertTrue(board[7, 7] == CheckersPiece.Empty)
        Assert.assertTrue(board[8, 7] == CheckersPiece.Invalid)
        Assert.assertTrue(board[7, 8] == CheckersPiece.Invalid)
        Assert.assertTrue(board[2, 3] == CheckersPiece.BlackKing)
        Assert.assertTrue(board[4, 7] == CheckersPiece.WhiteMan)

        Assert.assertTrue(board[1, 1] == CheckersPiece.BlackMan)
        Assert.assertTrue(board[2, 2] == CheckersPiece.BlackMan)
    }
}