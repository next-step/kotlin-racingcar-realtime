import kotlinx.coroutines.runBlocking
import service.RealtimeRacingCarService
import view.InputView

fun main() {
    val cars = InputView.getCars()
    val distance = InputView.getDistance()

    val racingCarService = RealtimeRacingCarService()

    runBlocking {
        racingCarService.start(cars, distance)
    }
}