//package study
//
//import kotlinx.coroutines.*
//import kotlin.random.Random
//
//fun main() = runBlocking {
//    println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
//    val cars = readln().split(",").map { it.trim() }
//
//    println("목표 거리를 입력하세요.")
//    val target = readln().toInt()
//
//    //각 자동차의 현재 위치를 저장할 일반 Map (ConcurrentHashMap 아님!)
//    val positions = mutableMapOf<String, Int>().apply {
//        cars.forEach { this[it] = 0 }
//    }
//
//    val winner = CompletableDeferred<String>() // 우승자 기록용
//
//    val jobs = mutableListOf<Job>()
//
//    // 자동차별 코루틴 병렬 실행
//    cars.forEach { car ->
//        val job = launch(Dispatchers.Default) { // 병렬 실행
//            while (isActive && positions[car]!! < target && !winner.isCompleted) {
//                delay(Random.nextLong(0, 500)) // 0~500ms 랜덤 대기
//
//                // 자기 위치만 업데이트하니까 동시 접근 문제 없음
//                positions[car] = positions[car]!! + 1
//
//                println("$car : ${"-".repeat(positions[car]!!)}")
//
//                if (positions[car]!! >= target && !winner.isCompleted) {
//                    winner.complete(car)
//
//
//                }
//            }
//        }
//        jobs.add(job)
//    }
//
//    // 우승자 대기
//    val winningCar = winner.await()
//    println("\n${winningCar}가 최종 우승했습니다.")
//
//    // 모든 코루틴 정리
//    jobs.forEach { it.cancelAndJoin() }
//}
package study

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class Car(val name: String, var position: Int = 0)

val carList = mutableListOf<Car>()
val carJobs = mutableListOf<Job>()
val carMutex = Mutex()

val raceCommandChannel = Channel<String>(Channel.UNLIMITED) // MOVE/STOP 명령 채널

var raceFinished = false
var winnerCar: String? = null

val supervisor = SupervisorJob()
val scope = CoroutineScope(Dispatchers.Default + supervisor)


fun main() = runBlocking {
    println("경주할 자동차 이름을 입력하세요.(쉼표(,) 기준으로 구분)")
    val initialCars = readln().split(",").map { it.trim() }.filter { it.isNotEmpty() }

    println("목표 거리를 입력하세요.")
    val target = readln().toInt()


    lateinit var inputJob: Job

    carMutex.withLock {
        initialCars.forEach { name ->
            val car = Car(name)
            carList.add(car)
            launchCar(car,target)
        }
    }

    // 자동차 개수만큼 MOVE 명령 보내서 경주 시작
    repeat(carList.size) {
        raceCommandChannel.send("MOVE")
    }

    // 사용자 입력 받기
    inputJob = launch {
        while (isActive && !raceFinished) {
            val input = readlnOrNull()?.trim() ?: break

            if (input.isEmpty()) {
                println("[SYSTEM] 경주를 일시 정지합니다.")
                println(carList.size)
                repeat(carList.size) {
                    println("====================>")
                    raceCommandChannel.send("STOP")
                }

                // 다시 재개 대기
                while (true) {
                    val resumeInput = readlnOrNull()?.trim() ?: break
                    if (resumeInput.isEmpty()) {
                        println("[SYSTEM] 경주를 재개합니다.")
                        repeat(carList.size) {
                            raceCommandChannel.send("MOVE")
                        }
                        break
                    }
                }
            } else if (input.startsWith("add ")) {
                val newCarName = input.removePrefix("add ").trim()
                if (newCarName.isNotEmpty()) {
                    val newCar = Car(newCarName)
                    carList.add(newCar)
                    launchCar(newCar,target)
                    println("[SYSTEM] 새로운 자동차 추가 완료: $newCarName")
                    raceCommandChannel.send("MOVE") // 새 차에도 MOVE 신호 보내야 함
                } else {
                    println("[ERROR] 자동차 이름이 없습니다.")
                }
            } else {
                println("[ERROR] (add 자동차이름) 형식으로 입력하세요.")
            }
        }
    }

    while (!raceFinished) {
        delay(100)
    }

    println("\n${winnerCar}가 최종 우승했습니다!\n")

    inputJob.cancelAndJoin()
    carJobs.forEach { it.cancelAndJoin() }
    raceCommandChannel.close()

    println("최종 자동차 위치")
    carMutex.withLock {
        carList.forEach { car ->
            println("${car.name} : ${"-".repeat(car.position)} (${car.position}칸)")
        }
    }
}


 fun launchCar(car: Car, target: Int) {
    val job = scope.launch {
        try {
            while (isActive && car.position < target && !raceFinished) {
                val command = raceCommandChannel.receive()  // 명령 수신

                if (command == "MOVE") {
                    delay(300) // 이동 딜레이
                    carMutex.withLock {
                        car.position++
                        println("${car.name} : ${"-".repeat(car.position)}")

                        if (car.position >= target) {
                            raceFinished = true
                            winnerCar = car.name
                        }
                    }
                } else if (command == "STOP") {
                   yield()
                    // STOP이면 아무것도 하지 않고 다시 명령 대기
                }
            }
        } catch (e: CancellationException) {
            // 정상 종료
        } catch (e: ClosedReceiveChannelException) {
            // 채널 닫힘
        }
    }
    carJobs.add(job)
}
