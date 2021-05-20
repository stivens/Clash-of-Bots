package com.codingame.game.core

import io.vavr.control.Try
import io.vavr.kotlin.*
import java.lang.IllegalArgumentException

import com.codingame.game.core.Action.Direction.*

sealed class Action {
    abstract val debugMsg: String?

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    data class Move  (val direction: Direction, override val debugMsg: String? = null) : Action()
    data class Attack(val direction: Direction, override val debugMsg: String? = null) : Action()
    data class Guard                           (override val debugMsg: String? = null) : Action()
    data class Selfdestruction                 (override val debugMsg: String? = null) : Action()

    companion object {
        fun tryParse(userOutput: String): Try<Action> = Try {
            val tokens = userOutput.split(" ")
            val majorKeyword = tokens.first()

            when (majorKeyword.toLowerCase()) {
                "move" ->
                    Move( parseDirection(tokens[1]), parseDebugMsg(tokens.drop(2)) )

                "attack" ->
                    Attack( parseDirection(tokens[1]), parseDebugMsg(tokens.drop(2)) )

                "guard" ->
                    Guard( parseDebugMsg(tokens.drop(1)) )

                "selfdestruction" ->
                    Selfdestruction( parseDebugMsg(tokens.drop(1)) )

                else ->
                    throw IllegalArgumentException("Invalid sequence: $userOutput")
            }
        }

        private fun parseDirection(raw: String): Direction = when (raw.toLowerCase()) {
            "up"    -> UP
            "down"  -> DOWN
            "left"  -> LEFT
            "right" -> RIGHT
            else    -> throw IllegalArgumentException("Invalid direction $raw")
        }

        private fun parseDebugMsg(tokens: List<String>): String? =
            if (tokens.isNotEmpty()) { tokens.joinToString(" ") } else { null }
    }
}
