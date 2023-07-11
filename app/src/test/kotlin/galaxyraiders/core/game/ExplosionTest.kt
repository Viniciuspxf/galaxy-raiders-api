package galaxyraiders.core.game

import galaxyraiders.core.physics.Point2D
import galaxyraiders.core.physics.Vector2D
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("Given an explosion")
class ExplosionTest {
  private val explosion = Explosion(
    initialPosition = Point2D(1.0, 1.0),
    initialVelocity = Vector2D(1.0, 0.0),
    radius = 1.0
  )

  @Test
  fun `it has a type Explosion `() {
    assertEquals("Explosion", explosion.type)
  }

  @Test
  fun `it has a symbol asterisk `() {
    assertEquals('*', explosion.symbol)
  }

  @Test
  fun `it shows the type Explosion when converted to String `() {
    assertTrue(explosion.toString().contains("Explosion"))
  }

  @Test
  fun `it has mass zero `() {
    assertEquals(0.0, explosion.mass)
  }


  @Test
  fun `it begins not expired `() {
    assertFalse(explosion.expired)
  }

  @Test
  fun `it expires after six seconds `() {
    explosion.startDateTime = explosion.startDateTime.minusSeconds(6)
    assertTrue(explosion.expired)
  }
}