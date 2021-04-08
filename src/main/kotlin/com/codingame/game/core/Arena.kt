package com.codingame.game.core

import com.codingame.game.Player
import com.codingame.game.util.Vector

data class Arena constructor(
    val width: Int,
    val height: Int,
) {
    private val board: Array<Array<Robot?>> = Array(height) { Array(width) { null } }
    private val robots: MutableMap<Robot, Position> = mutableMapOf()

    fun get(position: Position): Robot? {
        val (x, y) = position.normalizeOverflow(width, height)
        return get(x, y)
    }

    fun get(x: Int, y: Int): Robot? = board[y][x]

    fun getAllRobots(): List<Pair<Robot, Position>> = robots.toList()

    fun getAllRobotsOwnedBy(player: Player): List<Pair<Robot, Position>> =
        getAllRobots()
            .filter { (robot, _) -> robot.owner == player }

    fun getPositionOf(robot: Robot): Position? = robots[robot]

    fun emplace(robot: Robot, position: Position) {
        val normalizedPosition = position.normalizeOverflow(width, height)
        val (x, y) = normalizedPosition

        remove(robot)
        board[y][x]?.let { remove(it) }

        board[y][x] = robot
        robots[robot] = normalizedPosition
    }

    fun remove(robot: Robot) {
        robots[robot]?.let { (x, y) ->
            board[y][x] = null
        }

        robots.remove(robot)
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

    fun apply(v: Vector): Position =
        Position(x + v.dx, y + v.dy)
}