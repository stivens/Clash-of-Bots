package com.codingame.game.core

import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

import com.codingame.game.core.Action.*
import com.codingame.game.core.Action.Direction.*

class ActionTest : FeatureSpec({
    feature("Action.tryParse") {
        scenario("should return proper actions when player delivered valid output") {
            forAll(
                row("move up", Move(UP)),
                row("move down", Move(DOWN)),
                row("move left", Move(LEFT)),
                row("move right", Move(RIGHT)),
                row("attack up", Attack(UP)),
                row("attack down", Attack(DOWN)),
                row("attack left", Attack(LEFT)),
                row("attack right", Attack(RIGHT)),
                row("guard", Guard()),
                row("selfdestruction", Selfdestruction()),
                row("guard some debug msg", Guard(debugMsg = "some debug msg")),
                row("attack down hello world", Attack(DOWN, debugMsg = "hello world"))
            ) { playerOutput, expectedAction ->
                val result = Action.tryParse(playerOutput)

                result.isSuccess shouldBe true
                result.get() shouldBe expectedAction
            }
        }

        scenario("should fail when player delivered invalid output") {
            forAll(
                row("move", -1),
                row("attack", -1),
                row("attack north", -1),
                row("attack him", -1),
                row("42", -1),
                row("foobar", -1),
                row("Do you know the definition of insanity?", -1)
            ) { playerOutput, _ ->
                Action.tryParse(playerOutput).isFailure shouldBe true
            }
        }
    }
})