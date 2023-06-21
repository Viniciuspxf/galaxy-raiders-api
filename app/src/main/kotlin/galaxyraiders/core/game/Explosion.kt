package galaxyraiders.core.game

import com.fasterxml.jackson.annotation.JsonIgnore
import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D
import java.time.Duration
import java.time.LocalDateTime

class Explosion(
  initialPosition: Point2D,
  initialVelocity: Vector2D,
  radius: Double,
) :
  SpaceObject("Explosion", '*', initialPosition,  initialVelocity, radius, 0.0) {

  @JsonIgnore
  var startDateTime: LocalDateTime = LocalDateTime.now()
  val expired: Boolean
    get() {
      return Duration.between(startDateTime, LocalDateTime.now()).seconds < 10
    }
}