package galaxyraiders.adapters.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import galaxyraiders.core.game.Asteroid
import galaxyraiders.core.score.ScoreDTO
import galaxyraiders.ports.persistence.ScoreManager
import java.io.File
import java.io.FileNotFoundException

class FilesScoreManager : ScoreManager {

  lateinit var currentGameScore: ScoreDTO

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
      this.scoreboard = emptyList()
    }

    saveScoreboard()
  }

  override fun addScoreBoard() {
    currentGameScore = ScoreDTO()
    this.scoreboard += currentGameScore
    saveScoreboard()
  }

  override fun getLeaderBoard(): List<ScoreDTO> {
    val sortedScoreboard = scoreboard.sortedByDescending { it.points }
    return if (sortedScoreboard.size > 2) sortedScoreboard.slice(0..2) else sortedScoreboard
  }

  override fun addAsteroidHitPoints(asteroid: Asteroid) {
    this.currentGameScore.points += asteroid.mass / asteroid.radius
    this.currentGameScore.numberOfDestroyedAsteroids++

    saveScoreboard()
    saveLeaderboard()
  }

  private fun saveLeaderboard() {
    val objectMapper = ObjectMapper()
    objectMapper.writeValue(leaderboardFile, getLeaderBoard())
  }

  private fun saveScoreboard() {
    val objectMapper = ObjectMapper()
    objectMapper.writeValue(scoreboardFile, scoreboard)
  }

}

