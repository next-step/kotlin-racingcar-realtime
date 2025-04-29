import controller.RaceController
import kotlinx.coroutines.runBlocking
import model.RaceModel
import view.RaceView

fun main() =
    runBlocking {
        val model = RaceModel()
        val view = RaceView()
        val controller = RaceController(model, view)
        controller.runGame()
    }
