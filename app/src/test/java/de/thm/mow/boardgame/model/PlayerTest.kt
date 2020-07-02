package de.thm.mow.boardgame.model

import org.junit.Assert
import org.junit.Test

class PlayerTest {
    @Test
    fun testOpponent() {
        val playerWhite = Player.white
        val playerBlack = Player.black
        Assert.assertTrue(playerWhite.opponent == playerBlack)
        Assert.assertTrue(playerBlack.opponent == playerWhite)
    }
}