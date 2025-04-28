package study

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import study.domain.Car
import study.service.Race

class RaceTest {
    @Test
    fun start() =
        runTest {
            // given
            val cars =
                listOf(
                    Car("car1"),
                    Car("car2"),
                    Car("car3"),
                )
            val goal = 10
            val dispatcher = StandardTestDispatcher(testScheduler)
            val race = Race(cars, goal, dispatcher)

            // when
            race.start()

            // then
            race.cars.firstOrNull { it.isWinner(goal) }.shouldNotBeNull()
        }

    @Test
    fun start2() =
        runTest {
            // given
            val cars =
                listOf(
                    Car("car1", 0),
                    Car("car2", 0),
                    Car("car3", 9),
                )
            val goal = 10
            val dispatcher = StandardTestDispatcher(testScheduler)
            val race = Race(cars, goal, dispatcher)

            // when
            race.start()

            // then
            val winner = race.cars.firstOrNull { it.isWinner(goal) }
            winner?.name shouldBe "car3"
        }
}
