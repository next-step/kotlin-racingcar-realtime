import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import model.Car
import model.Race
import org.junit.jupiter.api.Test

class RaceAppKtTest {
    @Test
    fun start() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)

            val cars =
                listOf(
                    Car("car1"),
                    Car("car2"),
                    Car("car3", 9),
                )
            val goal = 10
            val race = Race(cars, goal, dispatcher = dispatcher)

            race.start()

            val winner =
                race.cars.firstOrNull {
                    println("car - $it")
                    it.position == goal
                }

            println("winner : $winner")
            winner?.name shouldBe "car3"
        }
}
