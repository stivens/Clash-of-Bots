package com.codingame.game

import com.codingame.game.core.*

internal object InputGenerator {
    internal fun generateInputFor(player: Player, arena: Arena, visionRange: Int = 1): String =
        arena
            .getAllRobotsOwnedBy(player)
            .map { robot -> robot.position.allNeighborsInRange(visionRange) }
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

    private fun translateToStringRepr(arenaObject: ArenaObject, perspectiveOf: Player): String =
        when (arenaObject) {
            is VOID  -> "-"
            is EMPTY -> "."
            is ENTRY -> "."
            is WALL  -> "#"
            is Robot -> {
                if (arenaObject.owner == perspectiveOf) {
                    arenaObject.health.toString()
                } else {
                    (-arenaObject.health).toString()
                }
            }
        }
}