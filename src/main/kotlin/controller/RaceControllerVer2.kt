package controller

import controller.util.RacingCarMapper
import controller.util.RacingCarNameValidator
import controller.util.RacingDistanceValidator
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import model.Distance
import model.RacingCar
import view.RaceView
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class RacingControllerVer2(
    val raceView: RaceView,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val channel: Channel<RacingCar> = Channel(Channel.UNLIMITED)
) {
    var racingCars = mutableListOf<RacingCar>()
    var distance = Distance()
    var racingJobs = mutableListOf<Job>()
    var isGamePaused = AtomicBoolean(false)
    private var scope = CoroutineScope(dispatcher + SupervisorJob())

    fun printCarListInputMessage() {
        raceView.printContent("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    }

    fun printDistanceInputMesage() {
        raceView.printContent("목표 거리를 입력하세요.")
    }

    fun printStartGameMeesage() {
        raceView.printContent("\n실행 결과")
    }

    fun printRacingCarStatus(racingCar: RacingCar) {
        raceView.printContent("${racingCar.name} : ${racingCar.getPositionStateString()}")
    }

    fun printWinner(carName: String) {
        raceView.printContent("${carName}가 최종 우승했습니다.")
    }

    fun inputAndValidateRacingCarNames() {
        var isValidationPassed = false

        while (isValidationPassed.not()) {
            try {
                raceView.inputContent().also { inputRacingCarNames ->
                    isValidationPassed = RacingCarNameValidator.isValidateRacingCarName(inputRacingCarNames)
                    racingCars = RacingCarMapper.mapToRacingCars(inputRacingCarNames).toMutableList()
                }
            } catch (exception: IllegalStateException) {
                raceView.printError(exception.message ?: "Error!")
            }
        }
    }

    fun inputAndValidateDistance() {
        while (distance.totalDistance <= 0) {
            try {
                distance =
                    distance.copy(totalDistance = RacingDistanceValidator.validateAndReturnDistance(raceView.inputContent()))

            } catch (exception: Exception) {
                raceView.printError(exception.message ?: "Error!")
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun startGameWithAddCar() {
        scope.launch {
            launch {
                startGameVer5()
            }

            launch {
                addRacingCarVer2()
            }

            /** 차량 추가 channel */
            while (isActive) {
                while (!channel.isEmpty) {
                    val newRacingCar = channel.receive()
                    println("${newRacingCar.name} 참가 완료 !!")

                    scope.launch {
                        move(newRacingCar)
                    }
                }
            }
        }.join()
    }

    suspend fun startGameVer5() {
        racingJobs = racingCars.map {
            scope.launch {
                move(it)
            }
        }.toMutableList()

        racingJobs.joinAll()
    }

    private suspend fun move(racingCar: RacingCar) {
        while (coroutineContext.isActive && racingCar.position < distance.totalDistance) {
            if (isGamePaused.get().not()) {
                racingCar.move()
                printRacingCarStatus(racingCar)
                checkWinner(racingCar)
            }
        }
    }

    private suspend fun checkWinner(racingCar: RacingCar) {
        if (racingCar.position == distance.totalDistance) {
            printWinner(racingCar.name)

            scope.cancel()
        }
    }

    fun addRacingCarVer2() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                val input = readlnOrNull()
                if (input != null) {
                    isGamePaused.set(true)

                    println("[channel ver] new 차량 이름 입력!")
                    val command = readlnOrNull()
                    channel.send(RacingCar(command ?: "")) // Todo 빈 차량 이름 예외 처리 하기!

                    isGamePaused.set(false)
                }
            }
        }
    }
}