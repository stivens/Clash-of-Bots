package com.codingame.game

import com.codingame.game.core.Arena
import com.codingame.game.core.Position
import com.codingame.game.core.Robot
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class InputGeneratorTest : ShouldSpec({
    context("generateInputFor") {
        val arena = this.javaClass.getResource("/arenas/testarena.txt").readText().let { Arena.of(it) }

        val player1 = Player()
        val player2 = Player()

        val player1robots = listOf(
            Robot(id = 1, owner = player1, health = 100, position = Position(3, 3)),
            Robot(id = 2, owner = player1, health = 100, position = Position(5, 8)),
            Robot(id = 3, owner = player1, health =  50, position = Position(0, 0)),
        ).onEach { robot -> arena.putNewRobot(robot, robot.position) }

        val player2robots = listOf(
            Robot(id = 4, owner = player2, health = 100, position = Position(3, 4)),
            Robot(id = 5, owner = player2, health =  42, position = Position(9, 9)),
        ).onEach { robot -> arena.putNewRobot(robot, robot.position) }

        should("return proper number of robots") {
            val player1input = InputGenerator.generateInputFor(player1, arena)
            val player2input = InputGenerator.generateInputFor(player2, arena)

            player1input.split("\n")[1] shouldBe "nrobots: ${player1robots.size}"
            player2input.split("\n")[1] shouldBe "nrobots: ${player2robots.size}"
        }

        should("return proper vision range") {
            val input1 = InputGenerator.generateInputFor(player1, arena, visionRange = 1)
            val input2 = InputGenerator.generateInputFor(player1, arena, visionRange = 3)

            input1.split("\n").first() shouldBe "vision: 1"
            input2.split("\n").first() shouldBe "vision: 3"
        }

        should("return proper minimap for given robot") {
            val player1input = InputGenerator.generateInputFor(player1, arena)
                .split("\n").drop(3).joinToString(separator = "\n")
            val player2input = InputGenerator.generateInputFor(player2, arena, visionRange = 3)
                .split("\n").drop(3).joinToString(separator = "\n")

            player1input shouldBe """
                . . .
                . 100 .
                . -100 .
                
                . . .
                . 100 .
                . . .
                
                . . .
                . 50 .
                . . .
            """.trimIndent()

            player2input shouldBe """
                . . . . . . .
                . . . . . . .
                . . . -100 . . .
                . . . 100 . . .
                . . . . . . .
                . . . . . . .
                . . . . . . .
                
                . . . . . . .
                . . . . . . .
                . . . . . . .
                . . . 42 . . .
                . . . . . . .
                . . . . . . .
                . . . . . . -50
            """.trimIndent()
        }
    }
})