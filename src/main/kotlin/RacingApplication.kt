import controller.RacingController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import view.RaceView

fun main() {
    runBlocking {
        val raceController = RacingController(raceView = RaceView())

        initRacingCars(raceController = raceController)
        initRound(raceController = raceController)

        startGame(raceController)
    }
}

private fun initRacingCars(raceController: RacingController) {
    raceController.printCarListInputMessage()
    raceController.inputAndValidateRacingCarNames()
}

private fun initRound(raceController: RacingController) {
    raceController.printDistanceInputMesage()
    raceController.inputAndValidateDistance()
}

private suspend fun startGame(raceController: RacingController) {
    coroutineScope {
        raceController.printStartGameMeesage()
        //raceController.startGame()
        //raceController.startGameVer2()
        launch {
            raceController.startGameVer3()
        }

        launch {
            raceController.addRacingCar()
        }
    }
}