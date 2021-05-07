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

    private fun spawnRobots(player1: Player, player2: Player) {
        val emptyPositions = arena.getEmptyPositions()
        if (emptyPositions.size < 4) return

        fun attemptToAssignSymmetricalPositions(): Boolean {
            for (attempt in 1..Config.Referee.SPAWN_ATTEMPTS_LIMIT) {
                val randomEmptyPos = emptyPositions.random()
                val symmetricalPos = Symmetry.centerSymmetry(center = Config.Referee.SPAWN_SYMMETRY_CENTER, a = randomEmptyPos)

                if (arena.get(symmetricalPos) is Robot) continue

                val complementPos1 =
                    randomEmptyPos.allNeighborsInRange(Config.Referee.SPAWN_COMPLEMENT_RANGE).flatten()
                        .filter { arena.get(it) == null && it !in listOf(randomEmptyPos, symmetricalPos) }
                        .shuffled()
                        .firstOrNull()

                val complementPos2 =
                    Symmetry.centerSymmetry(center = Config.Referee.SPAWN_SYMMETRY_CENTER, a = complementPos1!!)

                if (arena.get(complementPos2) is Robot) continue

                randomEmptyPos.let { arena.emplace( Robot(owner = player1), it ) }
                complementPos1?.let { arena.emplace( Robot(owner = player2), it ) }

                symmetricalPos.let { arena.emplace( Robot(owner = player2), it ) }
                complementPos2?.let { arena.emplace( Robot(owner = player1), it ) }

                return true
            }

            return false
        }

        attemptToAssignSymmetricalPositions()
    }
}