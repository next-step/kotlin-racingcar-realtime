import controller.RacingController
import controller.RacingControllerVer2
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import model.Distance
import model.RacingCar
import org.junit.jupiter.api.Test
import view.RaceView

class RaceTest {

    @Test
    fun start() = runTest {
        val raceController = RacingControllerVer2(
            raceView = RaceView(),
            dispatcher = StandardTestDispatcher(testScheduler)
        )

        raceController.racingCars = listOf(
            RacingCar("c1"),
            RacingCar("c2", 4)  // c2 winner test !
        ).toMutableList()
        raceController.distance = Distance(5)

        // when
        raceController.startGame()

        // then
        val winner = raceController.racingCars.firstOrNull { it.position == raceController.distance.totalDistance }?.name
        winner shouldBe "c2"
    }
}