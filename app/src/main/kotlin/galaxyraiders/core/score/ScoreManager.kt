package galaxyraiders.core.score

import com.fasterxml.jackson.databind.ObjectMapper
import galaxyraiders.core.game.Asteroid
import java.io.File

class ScoreManager() {

  var currentGameScore: ScoreDTO = ScoreDTO()

  var scoreboard: List<ScoreDTO>

  var scoreboardFile = File("app/src/main/kotlin/galaxyraiders/core/score/Scoreboard.json")

  var leaderboardFile = File("app/src/main/kotlin/galaxyraiders/core/score/Leaderboard.json")

  init {
    val objectMapper = ObjectMapper()
    this.scoreboard = objectMapper.readValue(scoreboardFile, objectMapper.typeFactory.constructCollectionType(
      MutableList::class.java, ScoreDTO::class.java))
    this.scoreboard += currentGameScore
  }

  fun addAsteroidHitPoints(asteroid: Asteroid) {
    this.currentGameScore.points += asteroid.mass / asteroid.radius
    this.currentGameScore.numberOfDestroyedAsteroids++

    saveScoreboard()
    saveLeaderboard()
  }

  private fun saveLeaderboard() {
    val objectMapper = ObjectMapper()

    objectMapper.writeValue(leaderboardFile, scoreboard.sortedByDescending { it.points }.slice(0..2))
  }

  private fun saveScoreboard() {
    val objectMapper = ObjectMapper()
    objectMapper.writeValue(scoreboardFile, scoreboard)
  }

}

