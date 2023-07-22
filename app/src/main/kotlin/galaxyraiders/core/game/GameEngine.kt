package galaxyraiders.core.game

import galaxyraiders.Config
import galaxyraiders.ports.persistence.ScoreManager
import galaxyraiders.ports.RandomGenerator
import galaxyraiders.ports.ui.Controller
import galaxyraiders.ports.ui.Controller.PlayerCommand
import galaxyraiders.ports.ui.Visualizer
import kotlin.system.measureTimeMillis

const val MILLISECONDS_PER_SECOND: Int = 1000

object GameEngineConfig {
  private val config = Config(prefix = "GR__CORE__GAME__GAME_ENGINE__")

  val frameRate = config.get<Int>("FRAME_RATE")
  val spaceFieldWidth = config.get<Int>("SPACEFIELD_WIDTH")
  val spaceFieldHeight = config.get<Int>("SPACEFIELD_HEIGHT")
  val asteroidProbability = config.get<Double>("ASTEROID_PROBABILITY")
  val coefficientRestitution = config.get<Double>("COEFFICIENT_RESTITUTION")

  val msPerFrame: Int = MILLISECONDS_PER_SECOND / this.frameRate
}

@Suppress("TooManyFunctions")
class GameEngine(
  val generator: RandomGenerator,
  val controller: Controller,
  val visualizer: Visualizer,
  val scoreManager: ScoreManager,
) {
  val field = SpaceField(
    width = GameEngineConfig.spaceFieldWidth,
    height = GameEngineConfig.spaceFieldHeight,
    generator = generator
  )

  var playing = false

  fun execute() {
    while (true) {
      val duration = measureTimeMillis { this.tick() }

      Thread.sleep(
        maxOf(0, GameEngineConfig.msPerFrame - duration)
      )
    }
  }

  fun execute(maxIterations: Int) {
    repeat(maxIterations) {
      this.tick()
    }
  }

  fun tick() {
    this.processPlayerInput()
    this.updateSpaceObjects()
    this.renderSpaceField()
    this.updateLeaderboard()
  }

  fun processPlayerInput() {
    this.controller.nextPlayerCommand()?.also {
      when (it) {
        PlayerCommand.MOVE_SHIP_UP ->
          this.field.ship.boostUp()
        PlayerCommand.MOVE_SHIP_DOWN ->
          this.field.ship.boostDown()
        PlayerCommand.MOVE_SHIP_LEFT ->
          this.field.ship.boostLeft()
        PlayerCommand.MOVE_SHIP_RIGHT ->
          this.field.ship.boostRight()
        PlayerCommand.LAUNCH_MISSILE ->
          this.field.generateMissile()
        PlayerCommand.PAUSE_GAME ->
          this.playing = !this.playing
        PlayerCommand.START_GAME ->
          this.startGame()
        PlayerCommand.EXIT_GAME ->
          this.endGame()
      }
    }
  }

  private fun startGame() {
    this.playing = true
    this.scoreManager.addScoreBoard()
  }

  private fun endGame() {
    this.playing = false
  }

  fun updateSpaceObjects() {
    if (!this.playing) return
    this.cleanExplosions()
    this.handleCollisions()
    this.moveSpaceObjects()
    this.trimSpaceObjects()
    this.generateAsteroids()
  }

  fun handleCollisions() {
    this.field.spaceObjects.forEachPair {
        (first, second) ->
      if (first.impacts(second)) {
        first.collideWith(second, GameEngineConfig.coefficientRestitution)
        this.handleAsteroidDestruction(first, second)
      }
    }
  }

  fun handleAsteroidDestruction(first: SpaceObject, second: SpaceObject) {
    var asteroid: Asteroid? = null

    if (first.type == "Asteroid" && second.type == "Missile")
      asteroid = first as Asteroid
    else if (first.type ==  "Missile" && second.type == "Asteroid")
      asteroid = second as Asteroid

    if (asteroid != null) {
      this.scoreManager.addAsteroidHitPoints(asteroid)
      this.field.generateExplosion(asteroid)
      first.destroy()
      second.destroy()
    }
  }

  fun moveSpaceObjects() {
    this.field.moveShip()
    this.field.moveAsteroids()
    this.field.moveMissiles()
    this.field.moveExplosions()
  }

  fun cleanExplosions() {
    this.field.cleanExplosions()
  }

  fun trimSpaceObjects() {
    this.field.trimAsteroids()
    this.field.trimMissiles()
  }

  fun generateAsteroids() {
    val probability = generator.generateProbability()

    if (probability <= GameEngineConfig.asteroidProbability) {
      this.field.generateAsteroid()
    }
  }

  fun renderSpaceField() {
    this.visualizer.renderSpaceField(this.field)
  }

  fun updateLeaderboard() {
    this.visualizer.setLeaderboard(this.scoreManager.getLeaderBoard())
  }
}

fun <T> List<T>.forEachPair(action: (Pair<T, T>) -> Unit) {
  for (i in 0 until this.size) {
    for (j in i + 1 until this.size) {
      action(Pair(this[i], this[j]))
    }
  }
}
