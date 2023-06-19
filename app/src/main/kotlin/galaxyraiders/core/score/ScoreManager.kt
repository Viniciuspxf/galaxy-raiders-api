package galaxyraiders.core.score

import com.fasterxml.jackson.databind.ObjectMapper
import galaxyraiders.core.game.Asteroid
import java.io.File
import java.io.FileNotFoundException

class ScoreManager() {

  var currentGameScore: ScoreDTO = ScoreDTO()

  var scoreboard: List<ScoreDTO>

  var scoreboardFile: File

  var leaderboardFile: File

  init {
    val scoreFolder = File("core/score/")

    scoreFolder.mkdirs()

    this.scoreboardFile = File(scoreFolder, "Scoreboard.json")
    this.leaderboardFile = File(scoreFolder, "Leaderboard.json")


    val objectMapper = ObjectMapper()

    try {
      this.scoreboard = objectMapper.readValue(scoreboardFile, objectMapper.typeFactory.constructCollectionType(
        MutableList::class.java, ScoreDTO::class.java))
    }
    catch (exception: FileNotFoundException) {
      scoreboard = emptyList()
    }

    this.scoreboard += currentGameScore
    saveScoreboard()
  }

  fun addAsteroidHitPoints(asteroid: Asteroid) {
    this.currentGameScore.points += asteroid.mass / asteroid.radius
    this.currentGameScore.numberOfDestroyedAsteroids++

    saveScoreboard()
    saveLeaderboard()
  }

  private fun saveLeaderboard() {
    val objectMapper = ObjectMapper()
    val sortedScoreboard = scoreboard.sortedByDescending { it.points }
    val leaderboard = if (scoreboard.size > 2) sortedScoreboard.slice(0..2) else scoreboard
    objectMapper.writeValue(leaderboardFile, leaderboard)
  }

  private fun saveScoreboard() {
    val objectMapper = ObjectMapper()
    objectMapper.writeValue(scoreboardFile, scoreboard)
  }

}

