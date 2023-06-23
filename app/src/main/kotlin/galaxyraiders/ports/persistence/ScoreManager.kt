package galaxyraiders.ports.persistence

import galaxyraiders.core.game.Asteroid

interface ScoreManager {
  fun addAsteroidHitPoints(asteroid: Asteroid)
}