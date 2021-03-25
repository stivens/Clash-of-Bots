package com.codingame.game.core

data class Position(val x: Int, val y: Int)

class Arena private constructor(
    val width: Int,
    val height: Int,
    val board: Array<ArenaObject>
) {
    fun get(position: Position): ArenaObject = board[normalizedIndex(position.x, position.y)]

    private fun normalizedIndex(x: Int, y: Int): Int {
        val xWithOverflow = if (x < 0) width + x else x
        val yWithOverflow = if (y < 0) height + y else y

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