package controller

import controller.util.RacingCarMapper
import controller.util.RacingCarNameValidator
import controller.util.RacingDistanceValidator
import controller.util.RandomNumberGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
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
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.milliseconds

class RacingController(
    val raceView: RaceView,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    val channel: Channel<RacingCar> = Channel(Channel.UNLIMITED)
) {
    val mutextForRacingCars = Mutex()

    var racingCars = mutableListOf<RacingCar>()

    var distance = Distance()

    var racingJobs1 = mutableListOf<Job>()
    var addCarRacing2Job: Job? = null

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
        racingJobs1 = racingCars.map { // Todo - 이 곳 내부 코드를 밖으로 분리해서 아래 scope 에서도 이어서 수행 가능하도록 수정해보기
            scope.launch(dispatcher) {
                while (isActive && it.position < distance.totalDistance) {
                    if (isGamePaused.get().not()) {
//                        val duration = RandomNumberGenerator.generateRandomNumber().milliseconds
//                        delay(duration)
//                        it.position++
//                        printRacingCarStatus(it)

                        it.move()
                        printRacingCarStatus(it)

                        // 우승자 출력
                        if (it.position == distance.totalDistance) {
                            printWinner(it.name)
                            scope.cancel() // 현재 scope는 SupervisorJob 존재 !!
                        }
                    } else {
                        yield()
                    }
                }
            }
        }.toMutableList()

        racingJobs1.joinAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun startGameWithAddCar() {
        scope.launch {
            launch {
                //startGameVer4()
                startGameVer5()
            }

            launch {
                //addRacingCar()
                addRacingCarVer2()
            }

            /** channel test */
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

    suspend fun startGameVer4() {
        racingJobs1 = racingCars.map { // Todo - 이 곳 내부 코드를 밖으로 분리해서 아래 scope 에서도 이어서 수행 가능하도록 수정해보기
            scope.launch {
                while (isActive && it.position < distance.totalDistance) {
                    //while (coroutineContext.isActive && it.position < distance.totalDistance) { // 위의 isActive랑 비교해보기 !!
//                    val channelResult = channel.tryReceive()
//                    if (channelResult.isSuccess) {
//                        println("[${it.name}] receive succes : ${channelResult.getOrNull()}")
//                    } else {
//                        println("[${it.name}] receive fail")
//                    }

                    if (isGamePaused.get().not()) {
                        it.move()
                        printRacingCarStatus(it)
                    } else {
                        yield()
                    }

                    if (it.position == distance.totalDistance) {
                        printWinner(it.name)
                        scope.cancel() // 현재 scope는 SupervisorJob 존재 !!
                    }
                }
            }
        }.toMutableList()

        racingJobs1.joinAll()
        // Todo - joinAll이 없다면 ..?
    }

    suspend fun startGameVer5() {
        racingJobs1 = racingCars.map {
            scope.launch {
                move(it)
            }
        }.toMutableList()

        racingJobs1.joinAll()
    }

    private suspend fun move(racingCar: RacingCar) {
        while (coroutineContext.isActive && racingCar.position < distance.totalDistance) {
            if (isGamePaused.get().not()) {
                racingCar.move()
                printRacingCarStatus(racingCar)
                checkWinner(racingCar)
            }
            //else { yield() }
        }
    }

    private suspend fun checkWinner(racingCar: RacingCar) {
        if (racingCar.position == distance.totalDistance) {
            printWinner(racingCar.name)

            addCarRacing2Job?.cancel()
            scope.cancel()
        }
    }

    suspend fun startGameForNewCar(newCarIndex: Int) {
        println("new car start !! car name : ${racingCars[newCarIndex].name}")

        coroutineScope {
            while (isActive && racingCars.any { it.position >= distance.totalDistance }.not()) {
                if (isGamePaused.get().not()) {
                    racingCars[newCarIndex].move()
                    printRacingCarStatus(racingCars[newCarIndex])
                } else {
                    yield()
                }
            }
        }
    }

    suspend fun startGameForNewCarVer2(newCarIndex: Int) {
        println("new car start !! car name : ${racingCars[newCarIndex].name}")

        move(racingCars[newCarIndex])
    }

    suspend fun addRacingCar() {
        //coroutineScope {
        scope.launch {
            /** Todo - 명시적으로 공용으로 사용하는 scope 사용을 선언해야 scope.cancel 시 동작함!, 그냥 launch는 별도의 스코프 ?! */

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

                    mutextForRacingCars.withLock { // 없어도 exception 발생함
                        racingJobs1.add(
                            scope.launch {
                                //startGameForNewCar(racingCars.size - 1)
                                startGameForNewCarVer2(racingCars.size - 1)
                            }
                        )
                    }

                    isGamePaused.set(false)
                }
            }
        }.join()
    }

    suspend fun addRacingCarVer2() {
        scope.launch(Dispatchers.IO) {
        //coroutineScope {
            //addCarRacing2Job = launch(Dispatchers.IO) {
                // Todo - job 형태로 만들어서 launch 하고 우승자 나오는 상황에서 해당 job cancel 하는 경우에는 바로 종료되지 않은 이유 ?!
                //while (addCarRacing2Job?.isActive == true) {
                while (isActive) {
                    println("hihi!!!!!")
                    val input = readlnOrNull()
                    if (input != null) {
                        isGamePaused.set(true)

                        println("[channel ver] new 차량 이름 입력!")
                        val command = readlnOrNull()
                        channel.send(RacingCar(command ?: "")) // Todo 빈 차량 이름 예외 처리 하기!

                        isGamePaused.set(false)
                    }
                }
            //}
        }
    }
    // 입력 받은 차량 문자열 검증하고 send
    // vs
    // 우선 RacingCar 형태로 만들고 send & receive 단(클라이언트)에서 검증 및 처리
}

// <Todo>
// 현재 로직 = 새로운 차량 추가 시 새로운 스코프와 job 추가
// 기존 job 리스트를 활용하는 방법은 없는지 ?!

// channel
// send 와 receive 스레드를 다르게 ??
// channel을 어디에 사용? >> 차량 새로 등록 시?

// join 사용 이유 더 명확하게 파악하기