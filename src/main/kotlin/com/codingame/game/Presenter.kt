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
import com.codingame.gameengine.module.tooltip.TooltipModule
import com.google.inject.Inject
//import jdk.nashorn.internal.runtime.Debug.id

class Presenter(
    private val arena: Arena,
    private val player1: Player,
    private val player2: Player,
    private val graphicEntityModule: GraphicEntityModule,
    private val tooltipModule: TooltipModule
) {
    private val robots: List<Pair<Robot, Position>> = arena.getAllRobots()
    private val robotsGroups: MutableMap<Robot, Group> = mutableMapOf()
    private val robotsHP: MutableMap<Robot, Text> = mutableMapOf()
    private val robotsDirect: MutableMap<Robot, Int> = mutableMapOf()
    private val robotsSprite: MutableMap<Robot, SpriteAnimation> = mutableMapOf()
    private val robotsShields: MutableMap<Robot, Sprite> = mutableMapOf()
    private val fieldHight : Int = 1000 / arena.height
    private val fieldWidth : Int = 1000 / arena.width
    private var playerRobotsNum : MutableMap<Player, Text> = mutableMapOf()
    private var playerHP : MutableMap<Player, Text> = mutableMapOf()
    var robotActions: Map<Robot, Action> = mapOf()

    private val blueSheet = graphicEntityModule.createSpriteSheetSplitter()
        .setSourceImage("blue.png")
        .setImageCount(16)
        .setWidth(150)
        .setHeight(150)
        .setOrigRow(0)
        .setOrigCol(0)
        .setImagesPerRow(4)
        .setName("blue")
        .split()
    private val redSheet = graphicEntityModule.createSpriteSheetSplitter()
        .setSourceImage("red.png")
        .setImageCount(16)
        .setWidth(150)
        .setHeight(150)
        .setOrigRow(0)
        .setOrigCol(0)
        .setImagesPerRow(4)
        .setName("red")
        .split()

    init {
        drawArena()
    }

    fun updateTooltips() {
        robotsGroups.forEach { (robot, group) ->
            val description = """
                ${arena.getPositionOf(robot)}
                ${robotActions[robot] ?: ""}
            """.trimIndent()

            tooltipModule.setTooltipText(group, description)
        }
    }

    //fun triggerAction(robotAction: RobotAction) {
    //  TODO("Not yet implemented")
    //}

    private fun drawArena() {
        graphicEntityModule.createSprite().setImage(Config.Presenter.FRAME_SPRITE).setZIndex(100)

        for (x in 0..arena.width - 1) {
            for(y in 0..arena.height - 1) {
                var stone = "stone0.png"
                if ((0..3).random() == 3) { stone = "stone" + (0..12).random().toString() + ".png"}
                graphicEntityModule.createSprite().setImage(stone)
                    .setBaseHeight(fieldHight)
                    .setBaseWidth(fieldWidth)
                    .setX(x * fieldWidth + fieldWidth/2 + 460)
                    .setY(y * fieldHight + fieldHight/2 + 40)
                    .setAnchor(.5).setZIndex(0)
                graphicEntityModule.createSprite().setImage("brick6.png")
                    .setBaseHeight(fieldHight)
                    .setBaseWidth(fieldWidth)
                    .setX(x * fieldWidth + fieldWidth/2 + 460)
                    .setY(y * fieldHight + fieldHight/2 + 40)
                    .setAnchor(.5).setZIndex(1)
            }
        }
        //player1
        graphicEntityModule.createSprite().setImage(player1.avatarToken)
            .setX(50) //50 //10
            .setY(100)
            .setZIndex(101)

        graphicEntityModule.createText(player1.nicknameToken)
            .setX(200) //200 //120
            .setY(120)
            .setZIndex(101)
            .setFillColor(Config.Presenter.COLOR_BLUE)
            .setFontFamily("Impact")
            .setFontSize(70)

        playerRobotsNum.put(player1, graphicEntityModule.createText("Robots: " + arena.getAllRobotsOwnedBy(player1).count().toString())
            .setX(100)
            .setY(250)
            .setZIndex(101)
            .setFillColor(0xffffff)
            .setFontFamily("Comic Sans MS")
            .setFontSize(60))

        playerHP.put(player1, graphicEntityModule.createText("HP: " + countHP(player1))
            .setX(100)
            .setY(350)
            .setZIndex(101)
            .setFillColor(0xffffff)
            .setFontFamily("Comic Sans MS")
            .setFontSize(60))

        //player2
        graphicEntityModule.createSprite().setImage(player2.avatarToken)
            .setX(1510) //1510 //1800
            .setY(100)
            .setZIndex(101)
        graphicEntityModule.createText(player2.nicknameToken)
            .setX(1660) //1660 //1470
            .setY(120)
            .setZIndex(101)
            .setFillColor(Config.Presenter.COLOR_RED)
            .setFontFamily("Impact")
            .setFontSize(70)

        playerRobotsNum.put(player2, graphicEntityModule.createText("Robots: " + arena.getAllRobotsOwnedBy(player2).count().toString())
            .setX(1560)
            .setY(250)
            .setZIndex(101)
            .setFillColor(0xffffff)
            .setFontFamily("Comic Sans MS")
            .setFontSize(60))

        playerHP.put(player2, graphicEntityModule.createText("HP: " + countHP(player2))
            .setX(1560)
            .setY(350)
            .setZIndex(101)
            .setFillColor(0xffffff)
            .setFontFamily("Comic Sans MS")
            .setFontSize(60))
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
        robotsShields[robot]!!.setScale(0.1).setVisible(true)
        graphicEntityModule.commitWorldState(0.0)
        robotsShields[robot]!!.setScale(0.05, Curve.LINEAR)
    }

    fun triggerGuardDisable(robot: Robot) {
        robotsShields[robot]!!.setVisible(false)
    }
    private fun setDirection(robot: Robot, move: Move)
    {
        if(move == Move(UP)) { robotsDirect[robot] = 0
            if (robot.owner.index == 0)
                robotsSprite[robot]!!.setImages(blueSheet[0], blueSheet[4])
            else robotsSprite[robot]!!.setImages(redSheet[0], redSheet[4]) }
        else if(move == Move(DOWN)) { robotsDirect[robot] = 2
            if (robot.owner.index == 0)
                robotsSprite[robot]!!.setImages(blueSheet[2], blueSheet[6])
            else robotsSprite[robot]!!.setImages(redSheet[2], redSheet[6]) }
        else if(move == Move(RIGHT)) { robotsDirect[robot] = 1
            if (robot.owner.index == 0)
                robotsSprite[robot]!!.setImages(blueSheet[1], blueSheet[5])
            else robotsSprite[robot]!!.setImages(redSheet[1], redSheet[5]) }
        else if(move == Move(LEFT)) { robotsDirect[robot] = 3
            if (robot.owner.index == 0)
                robotsSprite[robot]!!.setImages(blueSheet[3], blueSheet[7])
            else robotsSprite[robot]!!.setImages(redSheet[3], redSheet[7]) }
    }

    fun triggerMove(robot: Robot, move: Move) {
        val robotGroup = robotsGroups[robot] !!
        val robotPosition = arena.getPositionOf(robot) !!
        setDirection(robot, move)
        robotsSprite[robot]!!.setLoop(true)
        graphicEntityModule.commitEntityState(0.0, robotGroup, robotsSprite[robot])

        if((move == Move(UP)) and (robotPosition.y == arena.height - 1))
        {
            copyGroup(robot)
            val robotGroupCopy = robotsGroups[robot] !!
            setDirection(robot, move)
            robotGroupCopy.setY((robotPosition.y + 1)  * fieldHight + 50, Curve.NONE)
                .setX(robotPosition.x * fieldWidth + 470, Curve.NONE)
            graphicEntityModule.commitEntityState(0.0, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setY(-1 * fieldHight + 50, Curve.LINEAR)
            robotGroupCopy.setY(robotPosition.y * fieldHight + 50, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.5, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setVisible(false)
        }

        else if((move == Move(DOWN)) and (robotPosition.y == 0))
        {
            copyGroup(robot)
            val robotGroupCopy = robotsGroups[robot] !!
            setDirection(robot, move)
            robotGroupCopy.setY((robotPosition.y - 1)  * fieldHight + 50, Curve.NONE)
                .setX(robotPosition.x * fieldWidth + 470, Curve.NONE)
            graphicEntityModule.commitEntityState(0.0, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setY(arena.height * fieldHight + 50, Curve.LINEAR)
            robotGroupCopy.setY(robotPosition.y * fieldHight + 50, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.5, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setVisible(false)
        }

        else if((move == Move(RIGHT)) and (robotPosition.x == 0))
        {
            copyGroup(robot)
            val robotGroupCopy = robotsGroups[robot] !!
            setDirection(robot, move)
            robotGroupCopy.setY(robotPosition.y  * fieldHight + 50, Curve.NONE)
                .setX( - 1 * fieldWidth + 470, Curve.NONE)
            graphicEntityModule.commitEntityState(0.0, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setX(arena.width * fieldWidth + 470, Curve.LINEAR)
            robotGroupCopy.setX(robotPosition.x * fieldWidth + 470, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.5, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setVisible(false)
        }

        else if((move == Move(LEFT)) and (robotPosition.x == arena.width - 1))
        {
            copyGroup(robot)
            val robotGroupCopy = robotsGroups[robot] !!
            setDirection(robot, move)
            robotGroupCopy.setY(robotPosition.y  * fieldHight + 50, Curve.NONE)
                .setX( arena.width * fieldWidth + 470, Curve.NONE)
            graphicEntityModule.commitEntityState(0.0, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setX(-1 * fieldWidth + 470, Curve.LINEAR)
            robotGroupCopy.setX(robotPosition.x * fieldWidth + 470, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.5, robotGroup,robotsSprite[robot], robotGroupCopy)
            robotGroup.setVisible(false)
        }

        else {
            robotGroup.setY(robotPosition.y * fieldHight + 50, Curve.LINEAR)
                .setX(robotPosition.x * fieldWidth + 470, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.5, robotGroup, robotsSprite[robot])
        }
        robotsSprite[robot]!!.setLoop(false)
    }


    fun triggerCollision(robot: Robot, move: Move) {

        val robotGroup = robotsGroups[robot] !!
        val robotPosition = arena.getPositionOf(robot) !!
        setDirection(robot, move)
        robotsSprite[robot]!!.setLoop(true)
        graphicEntityModule.commitEntityState(0.0, robotGroup, robotsSprite[robot])

        val colisionAnimation = graphicEntityModule.createSpriteAnimation()
            .setImages("col1.png", "col2.png", "col3.png", "col4.png", "col5.png", "col6.png")
            .setScale(0.15)
            .setZIndex(5)
            .setDuration(200)
            .setLoop(true)
            .setVisible(false)

        if(move == Move(UP))
        {
            robotGroup.setY(robotPosition.y * fieldHight + 49 - fieldHight/2, Curve.LINEAR)

            colisionAnimation.setX(robotPosition.x * fieldWidth + 470 - fieldWidth/2)
                .setY(robotPosition.y * fieldHight - fieldHight/2 )
                .setVisible(true)
        }
        else if(move == Move(DOWN))
        {
            robotGroup.setY(robotPosition.y * fieldHight + 51 + fieldHight/2, Curve.LINEAR)

            colisionAnimation.setX(robotPosition.x * fieldWidth + 470 - fieldWidth/2)
                .setY(robotPosition.y * fieldHight + 40 + fieldHight/2 )
                .setVisible(true)
        }
        else if(move == Move(RIGHT))
        {
            robotGroup.setX(robotPosition.x * fieldWidth + 470 + fieldWidth/2, Curve.LINEAR)

            colisionAnimation.setImages("col1b.png", "col2b.png", "col3b.png", "col4b.png", "col5b.png", "col6b.png")
                .setScale(0.15)
                .setX(robotPosition.x * fieldWidth + 470 + fieldWidth/2)
                .setY(robotPosition.y * fieldHight + fieldHight/2 )
                .setVisible(true)
        }
        else if(move == Move(LEFT))
        {
            robotGroup.setX(robotPosition.x * fieldWidth + 470 - fieldWidth/2, Curve.LINEAR)

            colisionAnimation.setImages("col1b.png", "col2b.png", "col3b.png", "col4b.png", "col5b.png", "col6b.png")
                .setScale(0.15)
                .setX(robotPosition.x * fieldWidth + 470 - 3*fieldWidth/2)
                .setY(robotPosition.y * fieldHight  + fieldHight/2 )
                .setVisible(true)
        }
        graphicEntityModule.commitWorldState(0.3)
        robotGroup.setY(robotPosition.y * fieldHight + 50, Curve.LINEAR)
            .setX(robotPosition.x * fieldWidth + 470, Curve.LINEAR)
        graphicEntityModule.commitWorldState(0.4)

        colisionAnimation.setVisible(false)
        graphicEntityModule.commitWorldState(0.5)
    }

    fun triggerAttack(robot: Robot, attack: Attack) {
        val robotPosition = arena.getPositionOf(robot) !!
        val bullet = graphicEntityModule.createSpriteAnimation()
            .setImages("shot0.png", "shot1.png", "shot2.png", "shot3.png")
            .setZIndex(10)
            //.setScale(0.1)
            .setX((robotPosition.x) * fieldWidth + 470)
            .setY((robotPosition.y) * fieldHight + 70)
            .setLoop(true)
            .setVisible(false)
            .setDuration(10) //tutaj

        if(attack == Attack(RIGHT))
        {
            bullet.setRotation(1.6).setVisible(true).setX((robotPosition.x + 1) * fieldWidth + 470)
            graphicEntityModule.commitEntityState(0.5, bullet)
            bullet.setX((robotPosition.x + 2) * fieldWidth + 430, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.9, bullet)
            bullet.setVisible(false)
        }

        if(attack == Attack(DOWN))
        {
            bullet.setRotation(3.1).setVisible(true).setX(robotPosition.x * fieldWidth + 500)
                .setY(robotPosition.y * fieldHight + 90)
            graphicEntityModule.commitEntityState(0.5, bullet)
            bullet.setY((robotPosition.y + 1) * fieldHight + 80, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.9, bullet)
            bullet.setVisible(false)
        }
        if(attack == Attack(UP))
        {
            bullet.setVisible(true)
                .setX((robotPosition.x) * fieldWidth + 480)
                .setY((robotPosition.y) * fieldHight + 60)
            graphicEntityModule.commitEntityState(0.5, bullet)
            bullet.setY((robotPosition.y - 1) * fieldHight + 70, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.9, bullet)
            bullet.setVisible(false)
        }
        if(attack == Attack(LEFT))
        {
            bullet.setRotation(4.7).setVisible(true)
                .setX((robotPosition.x) * fieldWidth + 470)
                .setY((robotPosition.y) * fieldHight + 85)
            graphicEntityModule.commitEntityState(0.5, bullet)
            bullet.setX((robotPosition.x - 1) * fieldHight + 470, Curve.LINEAR)
            graphicEntityModule.commitEntityState(0.9, bullet)
            bullet.setVisible(false)
        }


    }


    fun triggerSelfdestruction(robot: Robot, selfdestruction: Selfdestruction) {
        val robotPosition = arena.getPositionOf(robot) !!
        var  boom: ArrayList<SpriteAnimation> = ArrayList()
        for (i in -1..1)
        {
            for (j in -1..1)
            {
                boom.add(graphicEntityModule.createSpriteAnimation()
                    .setImages("explosion1.png", "explosion2.png", "explosion3.png", "explosion4.png", "explosion5.png")
                    .setScale(fieldHight/64.0)
                    .setZIndex(10)
                    .setX((robotPosition.x + i) * fieldWidth + 470)
                    .setY((robotPosition.y + j) * fieldHight + 50)
                    //.setDuration(200)
                    .setLoop(true)
                    .setVisible(true))
            }
        }
        for (i in 0..8) {
            graphicEntityModule.commitEntityState(0.5, boom[i])
            graphicEntityModule.commitEntityState(0.9, boom[i])
            boom[i].setVisible(false)
            graphicEntityModule.commitEntityState(1.0, boom[i])

        }
    }

    fun triggerDamage(robot: Robot) {

        val rd = robotsDirect[robot]!!

        val sp = robotsSprite[robot]!!.setImages(blueSheet[rd+8],blueSheet[rd+12])
        if (robot.owner.index == 1) { sp.setImages(redSheet[rd+8],redSheet[rd+12]) }

        graphicEntityModule.commitEntityState(0.5, sp)
        sp.setVisible(true).setLoop(true)
        graphicEntityModule.commitEntityState(0.8, sp)
        sp.setImages(blueSheet[rd],blueSheet[rd+4])
        if (robot.owner.index == 1) { sp.setImages(redSheet[rd],redSheet[rd+4]) }
        playerHP[robot.owner]!!.setText("HP: " + countHP(robot.owner))
        robotsHP[robot]!!.setText(robot.health.toString())
        graphicEntityModule.commitEntityState(1.0, sp)

        playerHP[robot.owner]!!.setText("HP: " + countHP(robot.owner))
        robotsHP[robot]!!.setText(robot.health.toString())
    }

    fun triggerDeath(robot: Robot) {
        robotsGroups[robot]!!.setScale(1.0, Curve.LINEAR)
        graphicEntityModule.commitEntityState(0.8,  robotsGroups[robot]!!)
        robotsGroups[robot]!!.setScale(0.1, Curve.LINEAR)
        graphicEntityModule.commitEntityState(0.99,  robotsGroups[robot]!!)
        robotsGroups[robot]!!.setVisible(false)
        playerRobotsNum[robot.owner]!!.setText("Robots: " + arena.getAllRobotsOwnedBy(robot.owner).count().toString())

    }

    fun copyGroup(robot: Robot){

        val robotSprite = graphicEntityModule.createSpriteAnimation().setImages(blueSheet[1], blueSheet[5])
            .setLoop(false)
            .setScale((fieldHight * 0.8)/150.0)
            .setZIndex(1)
            .setDuration(1)

        if(robot.owner.index == 1)
        {
            robotSprite.setImages(redSheet[3], redSheet[7])
        }
        robotsSprite.replace(robot, robotSprite)

        val robotHP = graphicEntityModule.createText(robot.health.toString())
            .setFontSize(25)
            .setFillColor(0x00ff00)
            .setAnchorY(0.5)
            .setAnchorX(-0.8)
            .setZIndex(3)
            .setStrokeThickness(3.0) // Adding an outline
            .setStrokeColor(0x000000)
            .setFontFamily("Impact")
            //.setVisible(true)
        robotsHP.replace(robot, robotHP)

        val robotShield = graphicEntityModule.createSprite()
            .setImage(Config.Presenter.SHIELD_SPRITE)
            .setAnchor(.5)
            .setZIndex(2)
            .setScale(0.1)
            .setVisible(false)

        robotsShields.replace(robot, robotShield)

        val robotGroup = graphicEntityModule.createGroup(robotSprite, robotHP, robotShield)
            .setZIndex(3)
        robotsGroups.replace(robot, robotGroup)
    }

    fun addRobot(robot: Robot) {

        val robotSprite = graphicEntityModule.createSpriteAnimation().setImages(blueSheet[1], blueSheet[5])
            .setLoop(false)
            .setScale((fieldHight * 0.8)/150.0)
            .setZIndex(1)
            .setDuration(1)
        var rd = 1

        if(robot.owner.index == 1)
        {
            robotSprite.setImages(redSheet[3], redSheet[7])
            rd = 3
        }
        robotsSprite.put(robot, robotSprite)

        val robotPosition = arena.getPositionOf(robot)

        val robotHP = graphicEntityModule.createText(robot.health.toString())
            .setFontSize(25)
            .setFillColor(0x00ff00)
            .setAnchorY(0.5)
            .setAnchorX(-0.8)
            .setZIndex(3)
            .setStrokeThickness(3.0) // Adding an outline
            .setStrokeColor(0x000000)
            .setFontFamily("Impact")

        robotsHP.put(robot, robotHP)

        val robotShield = graphicEntityModule.createSprite()
            .setImage(Config.Presenter.SHIELD_SPRITE)
            .setAnchor(.5)
            .setZIndex(2)
            .setScale(0.1)
            .setVisible(false)

        robotsShields.put(robot, robotShield)

        val robotGroup = graphicEntityModule.createGroup(robotSprite, robotHP, robotShield)
            .setZIndex(3)
            .setX(robotPosition!!.x * fieldWidth + 470)
            .setY(robotPosition!!.y * fieldHight + 50)
        robotsGroups.put(robot, robotGroup)

        robotsDirect.put(robot, rd)
        playerRobotsNum[robot.owner]!!.setText("Robots: " + arena.getAllRobotsOwnedBy(robot.owner).count().toString())
        playerHP[robot.owner]!!.setText("HP: " + countHP(robot.owner))
        graphicEntityModule.commitEntityState(0.0,  robotGroup)
    }
}
