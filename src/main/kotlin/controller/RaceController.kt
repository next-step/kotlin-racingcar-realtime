package controller

import controller.util.RacingCarMapper
import controller.util.RacingCarNameValidator
import controller.util.RacingDistanceValidator
import controller.util.RandomNumberGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import model.Distance
import model.RacingCar
import view.RaceView
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

class RacingController(
    val raceView: RaceView,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val mutextForRacingCars = Mutex()

    var racingCars = mutableListOf<RacingCar>()
    var distance = Distance()

    var racingJobs1 = mutableListOf<Job>()

    var isGamePaused = AtomicBoolean(false)

    private var scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var addScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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

    suspend fun startGame() {
        try {
            coroutineScope {
                racingCars.forEachIndexed { index, racingCar ->
                    launch {
                        while (racingCars[index].position < distance.totalDistance
                            || !racingCars.any { it.position >= distance.totalDistance }
                        ) {
                            delay(RandomNumberGenerator.generateRandomNumber().toLong())
                            racingCars[index] = racingCars[index].copy(position = racingCars[index].position + 1)
                            printRacingCarStatus(racingCars[index])
                        }
                        this@coroutineScope.cancel()

                        // TODO
                        /** 현재 scope를 취소시키는 경우에는 예외가 발생 ?!?!?! */
                    }
                }
            }
        } catch (exception: Exception) {
            println(exception.message)
        }
    }

    suspend fun startGameVer2() {
        coroutineScope { // startGameVer2 호출한 곳의 CoroutineContext 받아온다
            racingJobs1 = racingCars.map {
                launch {
                    //while (it.position < distance.totalDistance) {
                    while (isActive && it.position < distance.totalDistance) {
                        val duration = RandomNumberGenerator.generateRandomNumber().milliseconds
                        delay(duration)
                        it.position++
                        printRacingCarStatus(it)

                        // 우승자 출력
                        if (it.position == distance.totalDistance) {
                            printWinner(it.name)
                            racingJobs1.map { job -> job.cancel() }
                        }
                    }
                }
            }.toMutableList()
        }
    }

    /** ver2 에서 잠재적 문제점은 ?? */
    // 1. while 조건에 isActive와 같이 확인 코드 필요
    // 2. 지금은 메인 스레드 하나에서 수행되어서 join 필요 X

    suspend fun startGameVer3() {
        racingJobs1 = racingCars.map {
            scope.launch(dispatcher) {
                while (isActive && it.position < distance.totalDistance) {
                    if (isGamePaused.get().not()) {
                        val duration = RandomNumberGenerator.generateRandomNumber().milliseconds
                        delay(duration)
                        it.position++
                        printRacingCarStatus(it)
                        //println("current size : ${racingCars.size}")

                        // 우승자 출력
                        if (it.position == distance.totalDistance) {
                            printWinner(it.name)
                            scope.cancel() // 현재 scope는 SupervisorJob 존재 !!
                            addScope.cancel()
                        }
                    } else {
                        yield()
                    }
                }
            }
        }.toMutableList()

        racingJobs1.joinAll()
    }

    suspend fun startGameForNewCar(newCarIndex: Int) {
        println("new car start !! car name : ${racingCars[newCarIndex].name}")

        coroutineScope {
            val newRacingCarJob = launch(dispatcher) {
                while (isActive && racingCars.any { it.position >= distance.totalDistance }.not()) {
                    if (isGamePaused.get().not()) {
                        val duration = RandomNumberGenerator.generateRandomNumber().milliseconds
                        delay(duration)
                        racingCars[newCarIndex].position++

                        printRacingCarStatus(racingCars[newCarIndex])
                    } else {
                        yield()
                    }
                }
            }

            newRacingCarJob.join()
        }
    }

    fun addRacingCar() {
        addScope.launch {
            println("addRacingCar called !!!!!")

            while (isActive && racingCars.any { it.position >= distance.totalDistance }.not()) {
                val enterInput = raceView.inputContent()

                if (enterInput.isEmpty()) {
                    isGamePaused.set(true)

                    println("새로운 차량 이름을 입력하세요.")

                    val newCar = raceView.inputContent()
                    println("$newCar 참가 완료 !")

                    mutextForRacingCars.withLock {
                        racingCars.add(RacingCar(newCar))
                    }

                    val newScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
                    newScope.launch {
                        startGameForNewCar(racingCars.size - 1)
                    }

                    isGamePaused.set(false)
                }
            }
        }
    }
}

// <Todo>
// 현재 로직 = 새로운 차량 추가 시 새로운 스코프와 job 추가
// 기존 job 리스트를 활용하는 방법은 없는지 ?!

// channel
// send 와 receive 스레드를 다르게 ??
// channel을 어디에 사용? >> 차량 새로 등록 시?