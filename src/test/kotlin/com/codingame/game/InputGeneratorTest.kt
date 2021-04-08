package com.codingame.game

import com.codingame.game.core.Arena
import com.codingame.game.core.Position
import com.codingame.game.core.Robot
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class InputGeneratorTest : ShouldSpec({
    context("generateInputFor") {
        val arena = Arena(width = 12, height = 12)

        val player1 = Player()
        val player2 = Player()

        val player1robots = listOf(
            Pair( Robot(owner = player1, health = 100), Position(3, 3) ),
            Pair( Robot(owner = player1, health = 100), Position(5, 8) ),
            Pair( Robot(owner = player1, health =  50), Position(0, 0) ),
        ).onEach { (robot, position) -> arena.emplace(robot, position) }

        val player2robots = listOf(
            Pair( Robot(owner = player2, health = 100), Position(3, 4) ),
            Pair( Robot(owner = player2, health =  42), Position(9, 9) ),
        ).onEach { (robot, position) -> arena.emplace(robot, position) }

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
                0 0 0
                0 100 0
                0 -100 0
                
                0 0 0
                0 100 0
                0 0 0
                
                0 0 0
                0 50 0
                0 0 0
            """.trimIndent()

            player2input shouldBe """
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 -100 0 0 0
                0 0 0 100 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 42 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 0
                0 0 0 0 0 0 -50
            """.trimIndent()
        }
    }
})