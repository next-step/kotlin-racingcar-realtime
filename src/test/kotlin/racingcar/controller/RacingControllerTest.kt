package racingcar.controller

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import racingcar.model.Car
import racingcar.view.RacingView

class RacingControllerTest {
    @Test
    fun startRacingTest() =
        runTest {
            // given
            val race = RacingController(RacingView(), StandardTestDispatcher(testScheduler))
            race.goal = 10
            race.cars =
                listOf(
                    Car("car1"),
                    Car("car2"),
                    Car("car3"),
                )

            // when
            race.startRacing()

            // then
            race.cars.firstOrNull { it.position == race.goal }.shouldNotBeNull()
        }

    @Test
    fun startRacingTest2() =
        runTest {
            // given
            val race = RacingController(RacingView(), StandardTestDispatcher(testScheduler))
            race.goal = 10
            race.cars =
                listOf(
                    Car("car1", 0),
                    Car("car2", 0),
                    Car("car3", 9),
                )

            // when
            race.startRacing()

            // then
            val winner = race.cars.firstOrNull { it.position == race.goal }
            winner?.name shouldBe "car3"
        }
}
