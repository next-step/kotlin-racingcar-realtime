import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import service.RealtimeRacingCarService
import view.InputView

fun main() {
    val cars = InputView.getCars()
    val distance = InputView.getDistance()

    val racingCarService = RealtimeRacingCarService()
    runBlocking(Dispatchers.IO) {
        cars.forEach {
            launch {
                racingCarService.start(it, distance)
            }
        }
    }
}