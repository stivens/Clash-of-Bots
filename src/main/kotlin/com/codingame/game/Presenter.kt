package com.codingame.game

import java.lang.Object.*
import com.codingame.game.core.*
import com.codingame.game.core.Action.*
import com.codingame.game.core.Arena
import com.codingame.game.core.Position
import com.codingame.game.core.Robot
import com.codingame.gameengine.module.entities.Curve
import com.codingame.gameengine.module.entities.GraphicEntityModule
import com.codingame.gameengine.module.entities.Sprite
import com.codingame.gameengine.module.entities.*
import com.codingame.game.core.Action.Direction.*
import com.google.inject.Inject
//import jdk.nashorn.internal.runtime.Debug.id




class Presenter(private val arena: Arena, private val graphicEntityModule: GraphicEntityModule, private val player1: Player, private val player2: Player) {
    private val robots: List<Pair<Robot, Position>> = arena.getAllRobots()
    private val robotsGroups: MutableMap<Robot, Group> = mutableMapOf()
    private val robotsHP: MutableMap<Robot, Text> = mutableMapOf()
    private val robotsShields: MutableMap<Robot, Sprite> = mutableMapOf()
    private val robotsFists: MutableMap<Robot, Sprite> = mutableMapOf()
    private val fieldHight : Int = 824 / arena.height
    private val fieldWidth : Int = 1664 / arena.width
    private var playerRobotsNum : MutableMap<Player, Text> = mutableMapOf()
    private var playerHP : MutableMap<Player, Text> = mutableMapOf()



    init {
        drawArena()
    }

    //fun triggerAction(robotAction: RobotAction) {
    //  TODO("Not yet implemented")
    //}

    private fun drawArena() {
        //graphicEntityModule.createSprite().image = Constants.BACKGROUND_SPRITE
        graphicEntityModule.createSprite().setImage(Constants.FRAME).setZIndex(100)


        for (x in 0..arena.width - 1) {
            for(y in 0..arena.height - 1) {
                graphicEntityModule.createSprite().setImage(Constants.BRICK_SPRITE)
                    .setBaseHeight(fieldHight)
                    .setBaseWidth(fieldWidth)
                    .setX(x * fieldWidth + fieldWidth/2 + 129)
                    .setY(y * fieldHight + fieldHight/2 + 128)
                    .setAnchor(.5).setZIndex(0)
            }
        }
        //player1
        graphicEntityModule.createSprite().setImage(player1.avatarToken)
            .setX(200)
            .setY(10)
            .setZIndex(101)

        graphicEntityModule.createText(player1.nicknameToken)
            .setX(300)
            .setY(10)
            .setZIndex(101)
            .setFillColor(0x0000ff)
            .setFontSize(40)

        playerRobotsNum.put(player1, graphicEntityModule.createText("Robots: " + arena.getAllRobotsOwnedBy(player1).count().toString())
            .setX(350)
            .setY(60)
            .setZIndex(101)
            .setFillColor(0x0000ff)
            .setFontSize(50))

        playerHP.put(player1, graphicEntityModule.createText("HP: " + countHP(player1))
            .setX(600)
            .setY(60)
            .setZIndex(101)
            .setFillColor(0x0000ff)
            .setFontSize(50))


        //player2
        graphicEntityModule.createSprite().setImage(player2.avatarToken)
            .setX(1300)
            .setY(10)
            .setZIndex(101)
        graphicEntityModule.createText(player2.nicknameToken)
            .setX(1400)
            .setY(10)
            .setZIndex(101)
            .setFillColor(0xff0000)
            .setFontSize(40)

        playerRobotsNum.put(player2, graphicEntityModule.createText("Robots: " + arena.getAllRobotsOwnedBy(player2).count().toString())
            .setX(1450)
            .setY(60)
            .setZIndex(101)
            .setFillColor(0xff0000)
            .setFontSize(50))

        playerHP.put(player2, graphicEntityModule.createText("HP: " + countHP(player2))
            .setX(1700)
            .setY(60)
            .setZIndex(101)
            .setFillColor(0xff0000)
            .setFontSize(50))


        // przydatne do testow jesli zaczynasz z jakimiś robotami
        //for(robot in robots) {
          //  addRobot(robot.first)
        //}

    }

    private fun updateView(){

    }

    private fun countHP(player: Player): String
    {
        var hp = 0
        for(robot in arena.getAllRobotsOwnedBy(player))
        {
            hp += robot.first.health
        }
        return hp.toString()
    }

    fun triggerGuard(robot: Robot, guard: Guard) {
        robotsShields[robot]!!.setScale(0.2).setVisible(true)
        graphicEntityModule.commitWorldState(0.0)
        robotsShields[robot]!!.setScale(0.1, Curve.LINEAR)

    }

    fun triggerGuardDisable(robot: Robot) {
        robotsShields[robot]!!.setVisible(false)
    }

    fun triggerMove(robot: Robot, move: Move) {
        val robotSprite = robotsGroups[robot] !!
        val robotPosition = arena.getPositionOf(robot) !!


        if((move == Move(UP)) and (robotPosition.y == arena.height - 1))
        {
            copyGroup(robot)
            val robotSpriteCopy = robotsGroups[robot] !!
            robotSpriteCopy.setY((robotPosition.y + 1)  * fieldHight + 140, Curve.NONE)
                .setX(robotPosition.x * fieldWidth + 140, Curve.NONE)
            graphicEntityModule.commitWorldState(0.0)
            robotSprite.setY(-1 * fieldHight + 140, Curve.LINEAR)
            robotSpriteCopy.setY(robotPosition.y * fieldHight + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(1.0)
            robotSprite.setVisible(false)
        }

        else if((move == Move(DOWN)) and (robotPosition.y == 0))
        {
            copyGroup(robot)
            val robotSpriteCopy = robotsGroups[robot] !!
            robotSpriteCopy.setY((robotPosition.y - 1)  * fieldHight + 140, Curve.NONE)
                .setX(robotPosition.x * fieldWidth + 140, Curve.NONE)
            graphicEntityModule.commitWorldState(0.0)
            robotSprite.setY(arena.height * fieldHight + 140, Curve.LINEAR)
            robotSpriteCopy.setY(robotPosition.y * fieldHight + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(1.0)
            robotSprite.setVisible(false)
        }

        else if((move == Move(RIGHT)) and (robotPosition.x == 0))
        {
            copyGroup(robot)
            val robotSpriteCopy = robotsGroups[robot] !!
            robotSpriteCopy.setY(robotPosition.y  * fieldHight + 140, Curve.NONE)
                .setX( - 1 * fieldWidth + 140, Curve.NONE)
            graphicEntityModule.commitWorldState(0.0)
            robotSprite.setX(arena.width * fieldWidth + 140, Curve.LINEAR)
            robotSpriteCopy.setX(robotPosition.x * fieldWidth + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(1.0)
            robotSprite.setVisible(false)
        }

        else if((move == Move(LEFT)) and (robotPosition.x == arena.width - 1))
        {
            copyGroup(robot)
            val robotSpriteCopy = robotsGroups[robot] !!
            robotSpriteCopy.setY(robotPosition.y  * fieldHight + 140, Curve.NONE)
                .setX( arena.width * fieldWidth + 140, Curve.NONE)
            graphicEntityModule.commitWorldState(0.0)
            robotSprite.setX(-1 * fieldWidth + 140, Curve.LINEAR)
            robotSpriteCopy.setX(robotPosition.x * fieldWidth + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(1.0)
            robotSprite.setVisible(false)
        }

        else {
            robotSprite.setY(robotPosition.y * fieldHight + 140, Curve.LINEAR)
                .setX(robotPosition.x * fieldWidth + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(1.0)
        }
    }

    fun triggerCollision(robot: Robot, move: Move) {
        val robotSprite = robotsGroups[robot] !!
        val robotPosition = arena.getPositionOf(robot) !!
        if(move == Move(UP))
        {
            robotSprite.setY(robotPosition.y * fieldHight + 120, Curve.LINEAR)
        }
        else if(move == Move(DOWN))
        {
            robotSprite.setY(robotPosition.y * fieldHight + 160, Curve.LINEAR)
        }
        else if(move == Move(RIGHT))
        {
            robotSprite.setX(robotPosition.x * fieldWidth + 120, Curve.LINEAR)
        }
        else if(move == Move(LEFT))
        {
            robotSprite.setX(robotPosition.x * fieldWidth + 160, Curve.LINEAR)
        }
        graphicEntityModule.commitWorldState(0.3)
        robotSprite.setY(robotPosition.y * fieldHight + 140, Curve.LINEAR)
            .setX(robotPosition.x * fieldWidth + 140, Curve.LINEAR)
        graphicEntityModule.commitWorldState(0.4)

        robotSprite.setRotation(0.5)
        graphicEntityModule.commitWorldState(0.5)
        robotSprite.setRotation(-0.5)
        graphicEntityModule.commitWorldState(0.6)
        robotSprite.setRotation(0.5)
        graphicEntityModule.commitWorldState(0.7)
        robotSprite.setRotation(-0.5)
        graphicEntityModule.commitWorldState(0.8)
        robotSprite.setRotation(0.5)
        graphicEntityModule.commitWorldState(0.9)
        robotSprite.setRotation(0.0)
        graphicEntityModule.commitWorldState(1.0)
    }

    fun triggerAttack(robot: Robot, attack: Attack) {
        val robotPosition = arena.getPositionOf(robot) !!
        val robotFist = graphicEntityModule.createSprite()
            .setImage(Constants.ROBOT_FIST)
            .setAnchor(-0.1)
            .setZIndex(3)
            .setScale(0.1)
            .setX((robotPosition.x) * fieldWidth + 140)
            .setY((robotPosition.y) * fieldHight + 140)
        graphicEntityModule.commitWorldState(0.0)

        if(attack == Attack(RIGHT))
        {
            robotFist.setRotation(1.6).setX(robotPosition.x * fieldWidth + 220)
            graphicEntityModule.commitWorldState(0.0)
            robotFist.setX((robotPosition.x + 1) * fieldWidth + 220, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.5)
            robotFist.setX((robotPosition.x) * fieldWidth + 220, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.99)
            robotFist.setVisible(false)
        }
        if(attack == Attack(DOWN))
        {
            robotFist.setRotation(3.1).setX(robotPosition.x * fieldWidth + 220)
                .setY(robotPosition.y * fieldHight + 220)
            graphicEntityModule.commitWorldState(0.0)
            robotFist.setY((robotPosition.y + 1) * fieldHight + 220, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.5)
            robotFist.setY((robotPosition.y) * fieldHight + 220, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.99)
            robotFist.setVisible(false)
        }
        if(attack == Attack(UP))
        {
            //robotFist.setRotation(1.6).setX(robotPosition.x * fieldWidth + 220)
            //graphicEntityModule.commitWorldState(0.0)
            robotFist.setY((robotPosition.y - 1) * fieldHight + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.5)
            robotFist.setY((robotPosition.y) * fieldHight + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.99)
            robotFist.setVisible(false)
        }
        if(attack == Attack(LEFT))
        {
            robotFist.setRotation(-1.6).setX(robotPosition.x * fieldWidth + 140)
                .setY(robotPosition.y * fieldHight + 230)
            graphicEntityModule.commitWorldState(0.0)
            robotFist.setX((robotPosition.x - 1) * fieldWidth + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.5)
            robotFist.setX((robotPosition.x) * fieldWidth + 140, Curve.LINEAR)
            graphicEntityModule.commitWorldState(0.99)
            robotFist.setVisible(false)
        }
    }

    fun triggerSelfdestruction(robot: Robot, selfdestruction: Selfdestruction) {
        robotsGroups[robot]!!.setScale(5.0, Curve.LINEAR)
        graphicEntityModule.commitWorldState(0.9)
        robotsGroups[robot]!!.setVisible(false)
    }

    fun triggerDamage(robot: Robot) {
        robotsHP[robot]!!.setText(robot.health.toString())
        playerHP[robot.owner]!!.setText("HP: " + countHP(robot.owner))

    }

    fun triggerDeath(robot: Robot) {
        robotsGroups[robot]!!.setScale(0.1, Curve.LINEAR)
        graphicEntityModule.commitWorldState(0.9)
        robotsGroups[robot]!!.setVisible(false)
        playerRobotsNum[robot.owner]!!.setText("Robots: " + arena.getAllRobotsOwnedBy(robot.owner).count().toString())

    }

    fun copyGroup(robot: Robot){
        val robotSprite = graphicEntityModule.createRectangle()
            .setHeight((fieldHight * 0.8).toInt())
            .setWidth((fieldWidth * 0.8).toInt())
            .setLineWidth(5.0)
            .setZIndex(1)
        if(robot.owner.index == 0)
        {
            robotSprite.setFillColor(0x0000FF)
        }
        else if(robot.owner.index == 1)
        {
            robotSprite.setFillColor(0xFF0000)
        }

        val robotHP = graphicEntityModule.createText(robot.health.toString())
            .setFontSize(30)
            .setFillColor(0x00ff00)
            .setAnchor(-0.5)
            .setZIndex(3)
        robotsHP.replace(robot, robotHP)

        val robotShield = graphicEntityModule.createSprite()
            .setImage(Constants.ROBOT_SHIELD)
            .setAnchor(.5)
            .setZIndex(2)
            .setScale(0.1)
            .setVisible(false)

        robotsShields.replace(robot, robotShield)

        val robotFist = graphicEntityModule.createSprite()
            .setImage(Constants.ROBOT_FIST)
            .setAnchor(.5)
            .setZIndex(3)
            .setScale(0.1)
            .setVisible(false)
        robotsFists.replace(robot, robotFist)


        val robotGroup = graphicEntityModule.createGroup(robotSprite, robotHP, robotShield, robotFist)
            .setZIndex(3)
        robotsGroups.replace(robot, robotGroup)
    }

    fun addRobot(robot: Robot) {
        val robotSprite = graphicEntityModule.createRectangle()
            .setHeight((fieldHight * 0.8).toInt())
            .setWidth((fieldWidth * 0.8).toInt())
            .setLineWidth(5.0)
            .setZIndex(1)
        if(robot.owner.index == 0)
        {
            robotSprite.setFillColor(0x0000FF)
        }
        else if(robot.owner.index == 1)
        {
            robotSprite.setFillColor(0xFF0000)
        }

        val robotPosition = arena.getPositionOf(robot)

        val robotHP = graphicEntityModule.createText(robot.health.toString())
            .setFontSize(30)
            .setFillColor(0x00ff00)
            .setAnchor(-0.5)
            .setZIndex(3)

        robotsHP.put(robot, robotHP)

        val robotShield = graphicEntityModule.createSprite()
            .setImage(Constants.ROBOT_SHIELD)
            .setAnchor(.5)
            .setZIndex(2)
            .setScale(0.1)
            .setVisible(false)

        robotsShields.put(robot, robotShield)

        val robotFist = graphicEntityModule.createSprite()
            .setImage(Constants.ROBOT_FIST)
            .setAnchor(-0.1)
            .setZIndex(3)
            .setScale(0.1)
            .setVisible(false)
        robotsFists.put(robot, robotFist)


        val robotGroup = graphicEntityModule.createGroup(robotSprite, robotHP, robotShield, robotFist)
            .setZIndex(3)
            .setX(robotPosition!!.x * fieldWidth + 140)
            .setY(robotPosition!!.y * fieldHight + 140)
        robotsGroups.put(robot, robotGroup)

        playerRobotsNum[robot.owner]!!.setText("Robots: " + arena.getAllRobotsOwnedBy(robot.owner).count().toString())
        playerHP[robot.owner]!!.setText("HP: " + countHP(robot.owner))
    }


}
