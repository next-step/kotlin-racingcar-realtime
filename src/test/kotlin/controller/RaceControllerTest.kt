package controller

import entity.Car
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import model.RaceModel
import org.junit.jupiter.api.Test
import view.RaceView

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
            val controller = RaceController(model, view)

            controller.runRound(
                cars,
                goal,
                CoroutineScope(Dispatchers.Unconfined),
            )

            cars.firstOrNull { it.distance == goal }?.name shouldBe "car3"
        }
}
