package galaxyraiders.ports.ui

import galaxyraiders.core.game.SpaceField
import galaxyraiders.core.score.ScoreDTO

interface Visualizer {
  fun renderSpaceField(field: SpaceField)
  fun setLeaderboard(leaderboard: List<ScoreDTO>)
}
