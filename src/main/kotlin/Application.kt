import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import model.Car
import service.RealtimeRace
import view.InputView

fun main() {
    val cars = InputView.getCars()
    val distance = InputView.getDistance()
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val channel: Channel<Car> = Channel(Channel.UNLIMITED)

    runBlocking {
        RealtimeRace(scope, channel).start(cars, distance)
    }
}