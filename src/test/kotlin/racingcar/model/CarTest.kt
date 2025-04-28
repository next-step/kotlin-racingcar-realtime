package racingcar.model

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CarTest {
    @Test
    fun carTest() =
        runTest {
            val car = Car("name")
            car.forward()
            assertEquals("name", car.name)
            assertEquals(1, car.position)
            assertEquals("name : -", car.toString())
        }
}
