package com.codingame.game

import com.codingame.game.core.Action
import com.codingame.game.core.Arena
import com.codingame.game.core.Robot
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.google.inject.Inject

class Presenter(private val arena: Arena) {
    @Inject private lateinit var graphicEntityModule: GraphicEntityModule

    init {
        // draw arena
        TODO("Not yet implemented")
    }

    fun triggerAction(action: Action, robot: Robot) {
        TODO("Not yet implemented")
    }
}