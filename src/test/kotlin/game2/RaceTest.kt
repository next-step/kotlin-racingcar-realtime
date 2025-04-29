package game2

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RaceTest {

    @Test
    fun start() = runTest {
        val cars:List<Car> = listOf(
            Car("car1"),
            Car("car2"),
            Car("car3"),
        )

        val goal = 10
        val race = Race(cars, goal, dispatcher = StandardTestDispatcher(testScheduler))

        race.start()

        advanceUntilIdle()

        race.cars.firstOrNull { it.position == goal}.shouldNotBeNull()
    }

    @Test
    fun start2() = runTest {
        val cars:List<Car> = listOf(
            Car("car1", 0),
            Car("car2", 0),
            Car("car3", 9),
        )

        val goal = 10
        val race = Race(cars, goal, dispatcher = StandardTestDispatcher(testScheduler))

        race.start()

        val first = race.cars.first { it.position == goal }
        first.name shouldBe "car3"
    }
}