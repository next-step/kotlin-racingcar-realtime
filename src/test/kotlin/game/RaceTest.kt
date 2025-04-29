package game

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RaceTest {
    @Test
    fun start() = runTest {
        // given
        val cars = listOf(
            Car("car1"),
            Car("car2"),
            Car("car3"),
        )
        val goal = 5
        val dispatcher = StandardTestDispatcher(testScheduler)
        val race = Race(cars, goal, dispatcher)

        // when
        race.start()
        advanceUntilIdle()

        // then
        race.cars.firstOrNull { it.position == goal }.shouldNotBeNull()
    }

    @Test
    fun start1() = runTest {
        // given
        val cars = listOf(
            Car("car1", 0),
            Car("car2", 0),
            Car("car3", 9),
        )
        val goal = 10
        val dispatcher = StandardTestDispatcher(testScheduler)
        val race = Race(cars, goal, dispatcher)

        // when
        race.start()
        advanceUntilIdle()

        // then
        race.cars.firstOrNull { it.position == goal }.shouldNotBeNull()
        race.getWinners().size shouldBe 1
        race.getWinners().first() shouldBe "car3"
    }
}