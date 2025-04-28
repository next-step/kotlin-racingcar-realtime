import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import model.Car
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CarTest {
    @Test
    fun moveTest() = runTest {
        val car = Car("car1")
        car.move()
        car.move()
        car.nowPosition.shouldBe(2)
    }

}