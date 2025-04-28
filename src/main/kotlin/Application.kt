import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import service.RealtimeRacingCarService
import view.InputRacingCar

fun main() {
    val cars = InputRacingCar.getCars()
    val distance = InputRacingCar.getDistance()

    val racingCarService = RealtimeRacingCarService()
    runBlocking(Dispatchers.IO) {
        cars.forEach {
            launch {
                racingCarService.start(it, distance)
            }
        }
    }
}