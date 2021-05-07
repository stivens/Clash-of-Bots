package com.codingame.game

import com.codingame.game.core.Arena
import com.codingame.game.core.Interpreter
import com.codingame.game.core.Position
import com.codingame.game.core.Robot
import com.codingame.game.util.Symmetry
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject

class Referee : AbstractReferee() {
    @Inject private lateinit var gameManager: MultiplayerGameManager<Player>
    @Inject private lateinit var graphicEntityModule: GraphicEntityModule

    private lateinit var presenter: Presenter
    private lateinit var arena: Arena
    private lateinit var interpreter: Interpreter

    override fun init() {
        arena = loadArena()
//        presenter = Presenter(arena, graphicEntityModule)
//        interpreter = Interpreter(arena, presenter)
        interpreter = Interpreter(arena, presenter = null)
    }

    override fun gameTurn(turn: Int) {
        TODO("Not yet implemented")
    }

    private fun loadArena(): Arena = Arena(20 ,20)

    private fun spawnRobots(player1: Player, player2: Player): Boolean {
        val emptyPositions = arena.getEmptyPositions().minus(Config.Referee.SPAWN_SYMMETRY_CENTER)
        if (emptyPositions.size < 4) return false

        fun attemptToAssignSymmetricalPositions(): Boolean {
            for (attempt in 1..Config.Referee.SPAWN_ATTEMPTS_LIMIT) {
                val randomEmptyPos = emptyPositions.random()
                val symmetricalPos = Symmetry.centerSymmetry(center = Config.Referee.SPAWN_SYMMETRY_CENTER, a = randomEmptyPos)
                    .normalizeOverflow(arena.width, arena.height)

                val complementPos1 = randomEmptyPos.allNeighborsInRange(Config.Referee.SPAWN_COMPLEMENT_RANGE)
                        .flatten()
                        .shuffled()
                        .firstOrNull()
                        ?.normalizeOverflow(arena.width, arena.height) ?: continue

                val complementPos2 =
                    Symmetry.centerSymmetry(center = Config.Referee.SPAWN_SYMMETRY_CENTER, a = complementPos1)
                        .normalizeOverflow(arena.width, arena.height)

                val positions = listOf(randomEmptyPos, symmetricalPos, complementPos1, complementPos2)
                if (positions.distinct() != positions || !positions.all { emptyPositions.contains(it) }) continue

                randomEmptyPos.let { arena.emplace( Robot(owner = player1), it ) }
                complementPos1.let { arena.emplace( Robot(owner = player2), it ) }

                symmetricalPos.let { arena.emplace( Robot(owner = player2), it ) }
                complementPos2.let { arena.emplace( Robot(owner = player1), it ) }

                return true
            }

            return false
        }

        return attemptToAssignSymmetricalPositions()
    }
}