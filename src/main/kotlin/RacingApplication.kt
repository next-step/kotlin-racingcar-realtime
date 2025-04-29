import controller.RacingController
import controller.RacingControllerVer2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import view.RaceView

fun main() {
    runBlocking {
        val raceController = RacingControllerVer2(raceView = RaceView())

        initRacingCars(raceController = raceController)
        initRound(raceController = raceController)

        startGame(raceController)
    }

    //joinTest()
}

private fun initRacingCars(raceController: RacingControllerVer2) {
    raceController.printCarListInputMessage()
    raceController.inputAndValidateRacingCarNames()
}

private fun initRound(raceController: RacingControllerVer2) {
    raceController.printDistanceInputMesage()
    raceController.inputAndValidateDistance()
}

private suspend fun startGame(raceController: RacingControllerVer2) {
    coroutineScope {
        raceController.printStartGameMeesage()
        //raceController.startGame()
        //raceController.startGameVer2()

//        launch {
//            //raceController.startGameVer3()
//            raceController.startGameVer4()
//        }
//
//        launch {
//            raceController.addRacingCar()
//        }

        raceController.startGameWithAddCar()
    }
}

fun joinTest() {
    runBlocking {
        var jobs = mutableListOf<Job>()
        var count = 0
        val mutex = Mutex()

        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope.launch {
            jobs.add(
                launch(Dispatchers.IO) {
                    while (isActive && count < 5) {
                        println("count! : $count")
                        delay(1000L)
                        count++

                        if (count == 3) {

                            jobs.add(
                                launch(Dispatchers.IO) {
                                    println("add job start !")
                                    delay(5000L)
                                    println("add job finished")
                                }
                            )

                        }
                    }
                }
            )

            //jobs.joinAll()
        }.join()

        //jobs.joinAll()
//        jobs.forEach {
//            mutex.withLock {
//                it.join()
//            }
//        }
        // SupervisorJob 없으면 error 이유 ?
    }
}