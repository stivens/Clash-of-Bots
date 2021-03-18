package com.codingame.game.core


import io.vavr.control.Try
import io.vavr.kotlin.*
import java.lang.IllegalArgumentException

sealed class Action {
    companion object {
        fun of(userOutput: String): Try<Action> = Try {
            val tokens = userOutput.split(" ")
            val majorKeyword = tokens.first()

            when (majorKeyword) {
                "move" -> Move(parseDirection(tokens[1]))
                "attack" -> Attack(parseDirection(tokens[1]))
                "guard" -> GUARD
                "suicide" -> SUICIDE
                else -> throw IllegalArgumentException("Invalid sequence: $userOutput")
            }
        }

        private fun parseDirection(raw: String): Direction = when (raw) {
            "up" -> Direction.UP
            "down" -> Direction.DOWN
            "left" -> Direction.LEFT
            "right" -> Direction.RIGHT
            else -> throw IllegalArgumentException()
        }
    }
}

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class Move(val direction: Direction) : Action()
data class Attack(val direction: Direction) : Action()
object GUARD : Action()
object SUICIDE : Action()