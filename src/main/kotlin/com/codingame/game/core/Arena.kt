package com.codingame.game.core

import com.codingame.game.Player
import com.codingame.game.util.Vector

class Arena (val width: Int, val height: Int) {
    private val board: Array<Array<Robot?>> = Array(height) { Array(width) { null } }
    private val robots: MutableMap<Robot, Position> = mutableMapOf()

    private val emptyPositions: MutableSet<Position> =
        (0 until height).flatMap { y ->
            (0 until width).mapNotNull { x ->
                Position(x, y)
            }
        }.toMutableSet()

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

        emptyPositions.remove(normalizedPosition)
    }

    fun remove(robot: Robot) {
        robots[robot]?.let { pos ->
            board[pos.y][pos.x] = null
            emptyPositions.add(pos)
        }

        robots.remove(robot)
    }

    fun getEmptyPositions(): Set<Position> = emptyPositions.toSet()

    override fun toString(): String =
        (0 until height).map { y ->
            (0 until width).map { x ->

                get(x, y)?.owner?.hashCode()?.toString()?.take(2) ?: "--"

            }.joinToString(separator = " ")
        }.joinToString(separator = "\n")
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