package com.codingame.game

import com.codingame.gameengine.core.AbstractReferee
import com.codingame.gameengine.core.MultiplayerGameManager
import com.google.inject.Inject

class Referee : AbstractReferee() {
    @Inject
    private lateinit var gameManager: MultiplayerGameManager<Player>

    override fun init() {
        TODO("Not yet implemented")
    }

    override fun gameTurn(turn: Int) {
        TODO("Not yet implemented")
    }
}