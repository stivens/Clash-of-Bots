package com.codingame.game

import com.codingame.game.core.*

internal object InputGenerator {
    internal fun minimapFor(robot: Robot, arena: Arena, visionRange: Int = Config.Robots.VISION_RANGE): String {
        val position = arena.getPositionOf(robot)

        require(position != null) { "Robot must be present on the board." }

        return position.allNeighborsInRange(visionRange)
            .let { rows ->
                rows.map { cols ->
                    cols.map { pos ->
                        translateToStringRepr(arena.get(pos), perspectiveOf = robot.owner)
                    }
                }
            }
            .let { rows ->
                rows.joinToString(separator = "\n") { cols -> cols.joinToString(separator = " ") }
            }
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