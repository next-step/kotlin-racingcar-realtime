package study

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

// 자동차 데이터 클래스

fun main() = runBlocking {
    println("경주할 자동차 이름을 입력하세요.(쉼표(,) 기준으로 구분)")
    val initialCars = readln().split(",").map { it.trim() }.filter { it.isNotEmpty() }

    if (initialCars.isEmpty()) throw IllegalArgumentException("자동차 이름을 하나 이상 입력해야 합니다.")

    println("목표 거리를 입력하세요.")
    val target = readln().toIntOrNull() ?: throw IllegalArgumentException("목표 거리는 숫자여야 합니다.")
    if (target <= 0) throw IllegalArgumentException("목표 거리는 1 이상이어야 합니다.")

    val carList = mutableListOf<Car>()
    val carJobs = mutableMapOf<String, Job>()
    val carMutex = Mutex()

    val raceStateChannel = Channel<Boolean>(Channel.CONFLATED) // 경주 상태 Channel(true=재개, false=정지)
    val supervisor = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + supervisor)

    val winner = CompletableDeferred<String>() // 우승자
    var raceFinished = false                   // 경주 종료 여부

    lateinit var inputJob: Job

    // 자동차를 독립적으로 움직이는 함수
    suspend fun launchCar(car: Car) {
        val job = scope.launch {
            var isRunning = true

            val raceStateReceiver = launch {
                for (state in raceStateChannel) {
                    isRunning = state
                }
            }

            try {
                while (isActive && car.position < target && !raceFinished) {
                    if (!isRunning) {
                        delay(100)
                        continue
                    }

                    delay(Random.nextLong(200, 500))

                    carMutex.withLock {
                        car.position++
                        println("${car.name} : ${"-".repeat(car.position)}")

                        if (car.position >= target && !raceFinished) {
                            raceFinished = true
                            winner.complete(car.name)
                        }
                    }
                }
            } catch (e: CancellationException) {
                // 정상 종료
            } finally {
                raceStateReceiver.cancel() // 내부 수신 코루틴도 정리
            }
        }
        carJobs[car.name] = job
    }

    // 초기 자동차들 출발
    carMutex.withLock {
        initialCars.forEach { name ->
            val car = Car(name)
            carList.add(car)
            launchCar(car)
        }
    }

    // 최초 상태는 '경주 중'으로 설정
    raceStateChannel.trySend(true)

    // 사용자 입력을 받는 코루틴
    inputJob = launch {
        while (isActive && !raceFinished) {
            val input = readlnOrNull()?.trim() ?: break

            if (input.isEmpty()) {
                raceStateChannel.trySend(false) // 멈추기
                println("[SYSTEM] 경주를 일시 정지합니다.")

                // 다시 엔터 치면 재개
                while (true) {
                    val resumeInput = readlnOrNull()?.trim() ?: break
                    if (resumeInput.isEmpty()) {
                        raceStateChannel.trySend(true) // 재개
                        println("[SYSTEM] 경주를 재개합니다.")
                        break
                    } else if (resumeInput.startsWith("add ")) {
                        val newCarName = resumeInput.removePrefix("add ").trim()
                        if (newCarName.isEmpty()) {
                            println("[ERROR] 추가할 자동차 이름이 비어있습니다.")
                        } else {
                            carMutex.withLock {
                                if (carList.any { it.name == newCarName }) {
                                    println("[ERROR] 이미 존재하는 자동차 이름입니다: $newCarName")
                                } else {
                                    println("[SYSTEM] 새로운 자동차 추가: $newCarName")
                                    val newCar = Car(newCarName)
                                    carList.add(newCar)
                                    launchCar(newCar)
                                    println("[SYSTEM] $newCarName 참가 완료!")
                                }
                            }
                        }
                    } else {
                        println("[ERROR] 올바르지 않은 입력입니다. (add 자동차이름)")
                    }
                }
            } else if (input.startsWith("add ")) {
                val newCarName = input.removePrefix("add ").trim()

                if (newCarName.isEmpty()) {
                    println("[ERROR] 추가할 자동차 이름이 비어있습니다.")
                } else {
                    carMutex.withLock {
                        if (carList.any { it.name == newCarName }) {
                            println("[ERROR] 이미 존재하는 자동차 이름입니다: $newCarName")
                        } else {
                            println("[SYSTEM] 새로운 자동차 추가: $newCarName")
                            val newCar = Car(newCarName)
                            carList.add(newCar)
                            launchCar(newCar)
                            println("[SYSTEM] $newCarName 참가 완료!")
                        }
                    }
                }
            } else {
                println("[ERROR] 올바르지 않은 입력입니다. (add 자동차이름)")
            }
        }
    }

    // 우승자 나오면
    val winningCar = winner.await()

    println("\n${winningCar}가 최종 우승했습니다!\n")

    raceFinished = true

    // 코루틴 전부 정리
    inputJob.cancelAndJoin()
    carJobs.values.forEach { it.cancelAndJoin() }
    raceStateChannel.close()

    // 최종 상태 출력
    println("현재까지 자동차들의 위치")
    carMutex.withLock {
        carList.forEach { car ->
            println("${car.name} : ${"-".repeat(car.position)} (${car.position}칸)")
        }
    }
}
