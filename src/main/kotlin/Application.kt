import controller.RaceController
import kotlinx.coroutines.runBlocking

fun main() =
    runBlocking {
        val raceController = RaceController()
        raceController.run()
    }
