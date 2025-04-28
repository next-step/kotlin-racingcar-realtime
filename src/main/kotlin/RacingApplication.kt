import controller.RacingController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        CoroutineScope(Dispatchers.Default).launch {
            waitAddCar(raceController)
        }.join()
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
    CoroutineScope(Dispatchers.Default).launch {
        //coroutineScope {
            raceController.printStartGameMeesage()
            val job1 = launch {
                raceController.startGame()
            }

            //waitAddCar(raceController)
            //job1.join()
        //}
    }
}

private suspend fun waitAddCar(raceController: RacingController) {
        while(!raceController.ended()) {
            println("wait Enter")
            raceController.inputWaiting()
            println("wait car name")
            raceController.inputAndValidateAddRacingCarNames()
            //raceController.startGame()
            startGame(raceController)
        }
}