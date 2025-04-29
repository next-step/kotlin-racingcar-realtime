package controller

import controller.util.CommandEvent
import controller.util.RacingCarCommandParser
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
    val channel: Channel<CommandEvent> = Channel(Channel.UNLIMITED),
) {
    var racingCars = mutableListOf<RacingCar>()
    var distance = Distance()
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
            observeUserInput()
            observeNewRacingCar()

            startGame()
            // startGame에서 우승자 감지 전까지 joinAll >> 우승자를 찾아야 끝이므로 다른 코루틴들 미리 돌려놓고 joinAll을 하는 현재 로직을 마지막에 배치
        }.join()
        // main 스레드에서 돌아가는 runBlocking 종료 방지 위해서 join ?
    }

    suspend fun startGame() {
        racingCars.map {
            scope.launch(dispatcher) {
                move(it)
            }
        }.joinAll()
        // 별도 코루틴으로 동작 수행 시, 동작 완료를 기다려야 한다면 여기서도 join 처리를 해줘야 동작 완료전까지 프로그램 종료 x
        // 현재 콘솔 환경은 모바일 어플리케이션처럼 계속 프로그램이 살아서 돌아가는 것이 아니기 때문이다 ?!
    }

    private suspend fun move(racingCar: RacingCar) {
        while (coroutineContext.isActive && racingCar.position < distance.totalDistance && racingCar.isAvailable) {
            if (isGamePaused.get().not()) {
                racingCar.move()
                printRacingCarStatus(racingCar)
                checkWinner(racingCar)
            }
        }
    }

    private fun checkWinner(racingCar: RacingCar) {
        if (racingCar.position == distance.totalDistance) {
            printWinner(racingCar.name)

            scope.cancel()
        }
    }

    private fun observeUserInput() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                val input = readlnOrNull()
                if (input != null) {
                    isGamePaused.set(true)

                    println("새로운 차량 이름 입력해주세요!")
                    val command = readlnOrNull()

                    val commandEvent = RacingCarCommandParser.parseCommand(command ?: "")

                    channel.send(commandEvent)

                    isGamePaused.set(false)
                }
            }
        }
    }

    /** channel을 통해서 새로운 차량 정보 받기 */
    private fun observeNewRacingCar() {
        scope.launch {
            while (isActive) {
                while (!channel.isEmpty) {
                    val commandEvent = channel.receive()

                    when (commandEvent) {
                        is CommandEvent.Add -> {
                            println("${commandEvent.addCarName} 참가 완료 !!")
                            scope.launch {
                                val newRacingCar = RacingCar(commandEvent.addCarName)
                                racingCars.add(newRacingCar)
                                move(newRacingCar)
                            }
                        }
                        is CommandEvent.Stop -> {
                            val stoppedCar = racingCars.find { it.name == commandEvent.stopCarName }
                            if (stoppedCar != null) {
                                stoppedCar.isAvailable = false
                                println("${stoppedCar.name} 차량 정지 !")
                            }
                        }
                        else -> {
                            println("차량명 입력 오류 .. !")
                        }
                    }
                }
            }
        }
    }
}
