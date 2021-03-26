package com.codingame.game.core

import com.codingame.game.Player

data class Arena constructor(
    val width: Int,
    val height: Int,
) {
    private val board: Array<Array<Robot?>> = Array(height) { Array(width) { null } }

    fun get(position: Position): Robot? {
        val (x, y) = position.normalizeOverflow(width, height)
        return get(x, y)
    }

    fun get(x: Int, y: Int): Robot? = board[y][x]

    fun getAllRobotsOwnedBy(player: Player): List<Pair<Robot, Position>> =
        (0 until height).flatMap { y ->
            (0 until width).mapNotNull { x ->
                get(x, y)?.let { Pair(it, Position(x, y)) }
            }
        }.filter { (robot, _) -> robot.owner == player }

    fun putNewRobot(robot: Robot, position: Position) {
        val (x, y) = position.normalizeOverflow(width, height)
        board[y][x] = robot
    }
}

data class Position(val x: Int, val y: Int) {
    fun allNeighborsInRange(range: Int): List<List<Position>> =
        (y - range .. y + range).map { neighborY ->
            (x - range .. x + range).map { neighborX ->
                Position(neighborX, neighborY)
            }
        }

    fun normalizeOverflow(width: Int, height: Int): Position {
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

        return Position(xWithOverflow, yWithOverflow)
    }
}