package model

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RaceTest {
    val cars: List<Car> =
        listOf(
            Car("car1", 0),
            Car("car2", 0),
            Car("car3", 8),
        )
    val goalDistance = 10

    @Test
    fun start() =
        runTest {
            val race = Race(cars, goalDistance)

            race.start()
            val winner = race.cars.firstOrNull { it.position == goalDistance }
            winner?.carName.shouldBe("car3")
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun moveTest() =
        runTest {
            // given
            val car = Car("testCar")
            val race = Race(listOf(car), 3)

            // when
            launch { race.start() }

            // then
            advanceUntilIdle() // 코루틴 모두 실행
            assertTrue(car.position > 0) // 이동했는지 체크
        }
}
