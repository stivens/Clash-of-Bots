package com.codingame.game

import com.codingame.game.core.*

internal object InputGenerator {
    internal fun generateInputFor(player: Player, arena: Arena, visionRange: Int = 1): String =
        arena
            .getAllRobotsOwnedBy(player)
            .map { (_, position) -> position.allNeighborsInRange(visionRange) }
            .map { rows ->
                rows.map { cols ->
                    cols.map { pos ->
                        translateToStringRepr(arena.get(pos), perspectiveOf = player)
                    }
                }
            }
            .map { rows -> rows.joinToString(separator = "\n") { cols -> cols.joinToString(separator = " ") } }
            .let {
                val numberOfRobots = it.size

                """
                |vision: $visionRange
                |nrobots: $numberOfRobots
                |   
                |${it.joinToString(separator = "\n\n")}
                """.trimMargin()
            }

    private fun translateToStringRepr(maybeRobot: Robot?, perspectiveOf: Player): String =
        when (maybeRobot) {
            null -> "0"
            else -> {
                if (maybeRobot.owner == perspectiveOf) {
                    maybeRobot.health.toString()
                } else {
                    maybeRobot.health.unaryMinus().toString()
                }
            }
        }
}