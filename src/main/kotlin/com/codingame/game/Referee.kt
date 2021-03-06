package com.codingame.game

import com.codingame.game.core.*
import com.codingame.game.util.Symmetry
import com.codingame.gameengine.core.AbstractPlayer
import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.GameManager
import com.codingame.gameengine.core.MultiplayerGameManager
import com.codingame.gameengine.module.endscreen.EndScreenModule
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.tooltip.TooltipModule

import com.google.inject.Inject

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.IllegalArgumentException

import kotlin.random.Random

class Referee : AbstractReferee() {
    @Inject private lateinit var gameManager: MultiplayerGameManager<Player>
    @Inject private lateinit var graphicEntityModule: GraphicEntityModule
    @Inject private lateinit var tooltipModule: TooltipModule
    @Inject private lateinit var endScreenModule: EndScreenModule

    private lateinit var presenter: Presenter
    private lateinit var interpreter: Interpreter

    private lateinit var arena: Arena

    private lateinit var rng: Random

    private lateinit var player1: Player
    private lateinit var player2: Player

    override fun init() {
        gameManager.apply {
            firstTurnMaxTime = Config.Referee.FIRST_TURN_MAX_TIME
                 turnMaxTime = Config.Referee.TURN_MAX_TIME
               frameDuration = Config.Referee.FRAME_DURATION
                    maxTurns = Config.Referee.MAX_TURNS
        }

        rng = Random(gameManager.seed)

        player1 = gameManager.getPlayer(0)
        player2 = gameManager.getPlayer(1)

        arena = Arena(Config.Arena.WIDTH, Config.Arena.HEIGHT)

        presenter = Presenter(arena, player1, player2, graphicEntityModule, tooltipModule)
        interpreter = Interpreter(arena, presenter)

        loadInitialSpawnPoints().doSpawn(arena, player1, player2, presenter)
    }






    override fun gameTurn(turn: Int) {
        performPlayersIO()

        if (isGameover()) {
            gameManager.endGame()
        } else {
            val player1Actions = (player1.robots zip player1.actions).toMap()
            val player2Actions = (player2.robots zip player2.actions).toMap()

            interpreter.execute(player1Actions + player2Actions)
        }

        if (shouldSpawnNewRobots(turn)) {
            repeat(Config.Referee.SPAWN_SIZE_MULTIPLIER) {
                pickSpawnPoints()?.doSpawn(arena, player1, player2, presenter)
            }
        }
    }

    private fun shouldSpawnNewRobots(turn: Int) =
        (turn % Config.Referee.SPAWN_TURN_DELAY == 0) && (turn < Config.Referee.MAX_TURNS - 1)

    private fun isGameover() =
        gameManager.activePlayers.size < gameManager.playerCount

    override fun onEnd() {
        gameManager.activePlayers.forEach { player ->
            val robots = arena.getAllRobotsOwnedBy(player).map { (robot, _) -> robot }
            player.score = robots.size
            player.bonusScore = robots.map { it.health }.sum()
        }

        val scores = gameManager.players.map { it.score * 100000 + it.bonusScore }.toIntArray()
        val summaries = gameManager.players.map { it.getScoreSummary() }.toTypedArray()

        endScreenModule.setScores(scores, summaries)
    }






    private fun performPlayersIO() {
        val players = listOf(player1, player2)

        players.forEach(::sendInputs)
        players.forEach(Player::execute)
        players.forEach(::parseOutputs)
    }

    private fun sendInputs(player: Player) {
        val robots = arena.getAllRobotsOwnedBy(player)
            .map { (robot, _) -> robot }
            .shuffled(rng)

        player.robots = robots

        player.sendInputLine("${robots.size}")

        robots.forEach { robot ->
            InputGenerator.minimapFor(robot, arena)
                .split("\n")
                .forEach(player::sendInputLine)
        }
    }

    private fun parseOutputs(player: Player) {
        try {
            val outputs = player.getOutputs()

            val actions = outputs.map {
                Action
                    .tryParse(it)
                    .getOrElseThrow { _ -> IllegalArgumentException("Invalid action: $it.") } !!
            }

            player.actions = actions
        }
        catch (e: AbstractPlayer.TimeoutException) {
            disqualify(player, "Timeout occurred. Make sure you provide output for every robot you own.")
        }
        catch (e: Exception) {
            disqualify(player, e.message ?: "")
        }
    }

    private fun disqualify(player: Player, reason: String) {
        player.score = -1
        player.deactivate(reason)
        gameManager.addToGameSummary(
            GameManager.formatErrorMessage("${player.nicknameToken} was disqualified. $reason")
        )
    }






    @Serializable
    private data class SpawnPoints(
        val forPlayer1: Set<Position>,
        val forPlayer2: Set<Position>
    ) {
        fun doSpawn(arena: Arena, player1: Player, player2: Player, presenter: Presenter?) {
            forPlayer1.doSpawn(arena, player1, presenter)
            forPlayer2.doSpawn(arena, player2, presenter)
        }

        private fun Set<Position>.doSpawn(arena: Arena, player: Player, presenter: Presenter?) {
            this.forEach { position ->
                val robot = Robot(owner = player)
                arena.emplace(robot, position)
                presenter?.addRobot(robot)
            }
        }
    }

    private fun pickSpawnPoints(): SpawnPoints? {
        val emptyPositions = arena.getEmptyPositions().minus(Config.Referee.SPAWN_SYMMETRY_CENTER)
        if (emptyPositions.size < 4) return null

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

    private fun loadInitialSpawnPoints(): SpawnPoints {
        val params = gameManager.gameParameters

        return "initialSpawnPoints".let {
            try {
                Json.decodeFromString(params.getProperty(it))
            } catch (e: Exception) {
                pickSpawnPoints()!!
                    .also { randomized -> params.setProperty(it, Json.encodeToString(randomized)) }
            }
        }
    }
}