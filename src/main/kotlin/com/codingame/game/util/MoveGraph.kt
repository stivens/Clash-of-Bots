package com.codingame.game.util

import com.codingame.game.core.Position
import java.lang.IllegalStateException

class MoveGraph {
    fun registerPositionOccupancy(position: Position) {
        checkConsistency()

        pos2vertex.getOrPut(
            key = position,
            defaultValue = { Vertex() }
        ).counter += 1
    }

    fun registerMoveAttempt(from: Position, to: Position) {
        checkConsistency()

        pos2vertex.getOrPut(
            key = to,
            defaultValue = { Vertex() }
        ).let {
            it.counter += 1
            it.incomingEdges.add(from)
        }
    }

    fun resolve() {
        checkConsistency()

        val q = ArrayDeque<Position>()

        pos2vertex.forEach { (_, vertex) ->
            if (vertex.counter > 1) {
                vertex.incomingEdges.forEach { q.add(it) }
            }
        }

        while (q.isNotEmpty()) {
            val pos = q.removeFirst()

            pos2vertex.getOrPut(
                key = pos,
                defaultValue = { Vertex() }
            ).run {
                counter += 1
                if (counter > 1) {
                    incomingEdges.forEach { q.add(it) }
                }
            }
        }

        alreadyResolved = true
    }

    fun checkCollision(departure: Position, destination: Position): Boolean {
        val depVertex = pos2vertex.getOrPut(
            key = departure,
            defaultValue = { Vertex() }
        )

        val destVertex = pos2vertex.getOrPut(
            key = destination,
            defaultValue = { Vertex() }
        )

        return destVertex.counter > 1 || depVertex.incomingEdges.contains(destination)
    }


    private class Vertex(val incomingEdges: MutableSet<Position> = mutableSetOf(), var counter: Int = 0)

    private val pos2vertex = mutableMapOf<Position, Vertex>()

    private var alreadyResolved = false

    private fun checkConsistency() {
        if (alreadyResolved) {
            throw IllegalStateException("MoveGraph cannot be updated after resolution.")
        }
    }
}