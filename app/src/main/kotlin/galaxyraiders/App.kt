@file:Suppress("MatchingDeclarationName")
package galaxyraiders

import galaxyraiders.adapters.BasicRandomGenerator
import galaxyraiders.adapters.tui.TextUserInterface
import galaxyraiders.adapters.web.WebUserInterface
import galaxyraiders.core.game.GameEngine
import galaxyraiders.adapters.persistence.FilesScoreManager
import kotlin.concurrent.thread
import kotlin.random.Random

object AppConfig {
  val config = Config("GR__APP__")

  val randomSeed = config.get<Int>("RANDOM_SEED")
  val operationMode = config.get<OperationMode>("OPERATION_MODE")
}

fun main() {
  val generator = BasicRandomGenerator(
    rng = Random(seed = AppConfig.randomSeed)
  )

  val ui = when (AppConfig.operationMode) {
    OperationMode.Text -> TextUserInterface()
    OperationMode.Web -> WebUserInterface()
  }

  val (controller, visualizer) = ui.build()

  val filesScoreManager = FilesScoreManager()

  val gameEngine = GameEngine(
    generator, controller, visualizer, filesScoreManager
  )

  thread { gameEngine.execute() }

  ui.start()
}
