import controller.RaceController
import model.RaceModel
import view.RaceView

fun main() {
    val model = RaceModel()
    val view = RaceView()
    val controller = RaceController(model, view)
    controller.runGame()
}
