package controller

import entity.Car
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import model.RaceModel
import org.junit.jupiter.api.Test
import view.RaceView
import kotlin.time.Duration.Companion.milliseconds

class RaceControllerTest {
    @Test
    fun runRoundTest() =
        runTest {
            val cars: List<Car> =
                listOf(
                    Car("car1", 0),
                    Car("car2", 0),
                    Car("car3", 9),
                )
            val goal = 10
            val model = RaceModel()
            val view = RaceView()
            val controller =
                RaceController(
                    model,
                    view,
                    Dispatchers.Unconfined,
                )
            cars.map { controller.runRound(it, goal) }
            while (controller.scope.isActive) {
                delay(100.milliseconds)
            }
            cars.firstOrNull { it.distance == goal }?.name shouldBe "car3"
        }
}
