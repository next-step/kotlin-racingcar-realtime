import com.kmc.Car
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RaceTest {
    @Test
    fun start() =
        runTest {
            val cars =
                listOf(
                    "car1",
                    "car2",
                    "car3",
                )
            makeCar(cars)
            val positionList =
                listOf(
                    0,
                    0,
                    9,
                )
            Car.setPosition(positionList)
            val loopCount = 10
            val dispatcher = StandardTestDispatcher(testScheduler)
            val race = Race
            race.loopCount = loopCount
            race.context = dispatcher
            race.start()

            val winner = Car.findWinner().firstOrNull()
            winner shouldBe "car3"
        }
}
