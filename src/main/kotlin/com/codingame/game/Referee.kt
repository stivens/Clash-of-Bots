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

import kotlinx.serialization.*
import kotlinx.serialization.json.*

import kotlin.random.Random

class Referee : AbstractReferee() {
    @Inject private lateinit var gameManager: MultiplayerGameManager<Player>
    @Inject private lateinit var graphicEntityModule: GraphicEntityModule

    private lateinit var presenter: Presenter
    private lateinit var arena: Arena
    private lateinit var interpreter: Interpreter

    private lateinit var rng: Random
    private lateinit var initialSpawnPoints: SpawnPoints

    private val player1 = Player()
    private val player2 = Player()

    override fun init() {
        arena = loadArena()
//        presenter = Presenter(arena, graphicEntityModule)
//        interpreter = Interpreter(arena, presenter)
        interpreter = Interpreter(arena, presenter = null)
        rng = Random(gameManager.seed)
        setupParams()
        initialSpawnPoints.spawn(arena, player1, player2)
    }

    override fun gameTurn(turn: Int) {
        pickSpawnPoints()?.spawn(arena, player1, player2)
        println(arena.toString())
        println("\n\n\n")
    }

    private fun loadArena(): Arena = Arena(20 ,20)

    @Serializable
    private data class SpawnPoints(val forPlayer1: Set<Position>, val forPlayer2: Set<Position>) {
        fun spawn(arena: Arena, player1: Player, player2: Player) {
            forPlayer1.spawn(arena, player1)
            forPlayer2.spawn(arena, player2)
        }

        private fun Set<Position>.spawn(arena: Arena, player: Player) {
            this.forEach { position -> arena.emplace(Robot(owner = player), position) }
        }
    }

    private fun pickSpawnPoints(): SpawnPoints? {
        val emptyPositions = arena.getEmptyPositions().minus(Config.Referee.SPAWN_SYMMETRY_CENTER)
        if (emptyPositions.size < 4) return null

        fun attemptToPickSymmetricalPositions(): SpawnPoints? {
            for (attempt in 1..Config.Referee.SPAWN_ATTEMPTS_LIMIT) {
                val randomEmptyPos = emptyPositions.random(rng)
                val symmetricalPos = Symmetry.centerSymmetry(center = Config.Referee.SPAWN_SYMMETRY_CENTER, a = randomEmptyPos)
                    .normalizeOverflow(arena.width, arena.height)

                val complementPos1 = randomEmptyPos.allNeighborsInRange(Config.Referee.SPAWN_COMPLEMENT_RANGE)
                    .flatten()
                    .shuffled(rng)
                    .firstOrNull()
                    ?.normalizeOverflow(arena.width, arena.height) ?: continue

                val complementPos2 =
                    Symmetry.centerSymmetry(center = Config.Referee.SPAWN_SYMMETRY_CENTER, a = complementPos1)
                        .normalizeOverflow(arena.width, arena.height)

                val positions = listOf(randomEmptyPos, symmetricalPos, complementPos1, complementPos2)

                if (positions.distinct() != positions || !positions.all { emptyPositions.contains(it) })
                    continue

                return SpawnPoints(
                    forPlayer1 = setOf(randomEmptyPos, complementPos2),
                    forPlayer2 = setOf(symmetricalPos, complementPos1)
                )
            }

            return null
        }

        return attemptToPickSymmetricalPositions()
    }

    private fun setupParams() {
        val params = gameManager.gameParameters

        initialSpawnPoints = "initialSpawnPoints".let {
            try {
                Json.decodeFromString(params.getProperty(it))
            } catch (e: Exception) {
                pickSpawnPoints()!!
                    .also { randomized -> params.setProperty(it, Json.encodeToString(randomized)) }
            }
        }
    }
}