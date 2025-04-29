package racingcar

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import racingcar.model.Car
import racingcar.model.Race

class RaceTest {
    @Test
    fun start1() = runTest {
        val cars = listOf(
            Car("car1"),
            Car("car2"),
            Car("car3"),
        )
        val goal = 10
        val dispatcher = StandardTestDispatcher(testScheduler)
        val race = Race(cars, goal, dispatcher)

        race.start()

        val winner = race.cars.first() { it.position == goal }
        winner.name shouldBe "car3"
    }

    @Test
    fun start2() = runTest {
        val cars = listOf(
            Car("car1"),
            Car("car2"),
            Car("car3", 9),
        )
        val goal = 10
        val dispatcher = StandardTestDispatcher(testScheduler)
        val race = Race(cars, goal, dispatcher)

        race.start()

        val winner = race.cars.first() { it.position == goal }
        winner.name shouldBe "car3"
    }

}