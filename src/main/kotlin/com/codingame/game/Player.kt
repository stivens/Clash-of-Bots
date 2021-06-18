package com.codingame.game

import com.codingame.game.core.Action
import com.codingame.game.core.Robot
import com.codingame.gameengine.core.AbstractMultiplayerPlayer

class Player : AbstractMultiplayerPlayer() {
    var robots: List<Robot> = listOf()
    var actions: List<Action> = listOf()
    var bonusScore: Int = 0

    override fun getExpectedOutputLines(): Int  = robots.size

    fun getScoreSummary(): String = if (score >= 0) {
        "$score robots alive, $bonusScore total health"
    } else {
        "disqualified"
    }
}