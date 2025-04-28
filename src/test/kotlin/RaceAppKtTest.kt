import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import model.Car
import model.Race
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RaceAppKtTest {
    @Test
    fun start() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)

        val cars = listOf(
            Car("car1", 0),
            Car("car2", 0),
            Car("car3", 9),
        )
        val goal = 10
        val race = Race(cars, goal, dispatcher = dispatcher)

        race.start()

        val winner = race.cars.first() {it.position == goal}.shouldNotBeNull()

        winner.name shouldBe "car3"
    }
}