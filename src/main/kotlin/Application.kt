import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import service.RealtimeRacingCarService
import view.InputView

fun main() {
    val cars = InputView.getCars()
    val distance = InputView.getDistance()

    val racingCarService = RealtimeRacingCarService()
    try {
        runBlocking {
            cars.forEach {
                launch(Dispatchers.IO) {
                    racingCarService.start(it, distance)
                }
            }
        }
    } catch(e: IllegalArgumentException) {

    }
}