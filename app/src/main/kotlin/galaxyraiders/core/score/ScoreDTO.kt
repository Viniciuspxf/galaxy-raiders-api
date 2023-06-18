package galaxyraiders.core.score

import java.util.Date

data class ScoreDTO (
  val startTime: Date,
  var numberOfDestroyedAsteroids: Int,
  var points: Double
) {
  constructor() : this(Date(), 0, 0.0)
}