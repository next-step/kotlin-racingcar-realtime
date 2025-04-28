package model

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RaceTest {
    @Test
    fun start() =
        runTest {
            val cars: List<Car> =
                listOf(
                    Car("car1", 0),
                    Car("car2", 0),
                    Car("car3", 8),
                )
            val goalDistance = 10
            val race = Race(cars, goalDistance, StandardTestDispatcher(testScheduler))

            race.start()
            val winner = race.cars.firstOrNull { it.position == goalDistance }
            winner?.carName.shouldBe("car3")
        }
}
