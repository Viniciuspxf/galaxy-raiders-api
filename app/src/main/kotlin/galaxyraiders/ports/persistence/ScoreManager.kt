package galaxyraiders.ports.persistence

import galaxyraiders.core.game.Asteroid
import galaxyraiders.core.score.ScoreDTO

interface ScoreManager {
  fun addAsteroidHitPoints(asteroid: Asteroid)
  fun addScoreBoard()
  fun getLeaderBoard(): List<ScoreDTO>
}