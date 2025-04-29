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
        raceController.startGameWithAddCar()

        //delay(100000L)
        //모바일 환경 처럼 계속 프로세스가 살아있지 않으므로 현재 프로젝트에서는 코루틴 동작에 있어 join 처리 필요
    }
}
