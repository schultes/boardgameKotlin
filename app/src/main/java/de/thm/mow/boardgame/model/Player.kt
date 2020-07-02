package de.thm.mow.boardgame.model

enum class Player {
    white, black;
    val opponent: Player
        get() = if (this == white) black else white
}