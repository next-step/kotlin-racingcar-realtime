import controller.RaceController
import kotlinx.coroutines.runBlocking
import model.RaceModel
import view.RaceView

fun main() =
    runBlocking {
        val model = RaceModel()
        val view = RaceView()
        val controller = RaceController(model, view)
        val carList = controller.initCarList()
        val goal = controller.initGoal()
        controller.runRound(carList, goal)
        controller.runAward(carList, goal)
    }
