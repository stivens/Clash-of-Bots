package com.codingame.game.core

import com.codingame.game.Player

data class Position(val x: Int, val y: Int) {
    fun allNeighborsInRange(range: Int): List<List<Position>> =
        (y - range .. y + range).map { neighborY ->
            (x - range .. x + range).map { neighborX ->
                Position(neighborX, neighborY)
            }
        }
}

class Arena private constructor(
    val width: Int,
    val height: Int,
    val board: Array<ArenaObject>
) {
    fun get(position: Position): ArenaObject = board[normalizedIndex(position.x, position.y)]

    fun getAllRobotsOwnedBy(player: Player): List<Robot> =
        board
            .mapNotNull {
                when (it) {
                    is Robot ->
                        if (it.owner == player)
                            it
                        else null
                    else -> null
                }
            }
            .sortedBy { it.id }

    fun putNewRobot(robot: Robot, position: Position) {
        board[normalizedIndex(position.x, position.y)] = robot
    }

    private fun normalizedIndex(x: Int, y: Int): Int {
        val xWithOverflow =
            when {
                x < 0 -> width + x
                x >= width -> x % width
                else -> x
            }
        val yWithOverflow = when {
            y < 0 -> height + y
            y >= height -> y % height
            else -> y
        }

        return yWithOverflow * width + xWithOverflow
    }

    companion object {
        fun of(shape: String): Arena {
            val rows = shape.split("\n")

            require(rows.isNotEmpty()) { "Arena cannot be empty" }

            val height = rows.size
            val width = rows.first().length

            require(rows.all { it.length == width }) { "Invalid arena shape" }

            val board = Array<ArenaObject>(height * width) { VOID }

            rows.withIndex().forEach { indexedRow ->
                val (y, row) = indexedRow

                row.withIndex().forEach { indexedCol ->
                    val (x, value) = indexedCol
                    val index = y * width + x

                    board[index] = when (value) {
                        '.' -> EMPTY
                        '#' -> WALL
                        'E' -> ENTRY
                        else -> VOID
                    }
                }
            }

            return Arena(width, height, board)
        }
    }
}