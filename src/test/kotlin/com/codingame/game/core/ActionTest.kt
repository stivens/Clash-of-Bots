package com.codingame.game.core

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ActionTest : FeatureSpec({
    feature("Action.of (a.k.a parser)") {
        scenario("should return proper actions when player delivered valid output") {
            forAll(
                row("move up",      Move(Direction.UP)),
                row("move down",    Move(Direction.DOWN)),
                row("move left",    Move(Direction.LEFT)),
                row("move right",   Move(Direction.RIGHT)),
                row("attack up",    Attack(Direction.UP)),
                row("attack down",  Attack(Direction.DOWN)),
                row("attack left",  Attack(Direction.LEFT)),
                row("attack right", Attack(Direction.RIGHT)),
                row("guard",        GUARD),
                row("suicide",      SUICIDE)
            ) { playerOutput, expectedAction ->
                val result = Action.of(playerOutput)

                result.isSuccess shouldBe true
                result.get() shouldBe expectedAction
            }
        }

        scenario("should fail when player delivered invalid output") {
            forAll(
                row("move", -1),
                row("attack", -1),
                row("42", -1),
                row("foobar", -1),
                row("Do you know the definition of insanity?", -1)
            ) { playerOutput, _ ->
                Action.of(playerOutput).isFailure shouldBe true
            }
        }
    }
})