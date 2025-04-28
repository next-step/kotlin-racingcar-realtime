package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield


class Race(
    val cars: ArrayList<Car>,
    val goal: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private var isPaused = false
    private val channel = Channel<String>()

    suspend fun start() {
        scope.launch {

//            for (car in cars) {
//                scope.launch {
//                    move(car)
//                }
//            }
            val jobs = cars.map {
                delay(100)
                    scope.launch {
                        move(it)
                    } // 직접만든 scope로 동작되게
            }

            scope.launch {
                println("scope.isActive: ${scope.isActive}")
                while (scope.isActive) {
                    val input = readLine()
                    if (input != null) {
                        println("(사용자 엔터 입력)")
                        isPaused = true
                        readLine()?.let {
//                            println("input2: $input")
                            channel.send(it)
                        }
                    }
                    delay(100)
                }
            }

            scope.launch {
                for (message in channel) {
                    println("received : $message")

                    var command: String?
                    var car: String?

                    message.split(" ").let {
                        command = it.getOrNull(0)
                        car = it.getOrNull(1)
                    }

                    val commandType = if (command == ("add")) {
                        CommandType.ADD
                    } else {
                        CommandType.NONE
                    }

                    when (commandType) {
                        CommandType.ADD -> {
                            // 자동차 add!
                            car?.let {
                                addCar(it)
                            }
                        }
                        else -> {}
                    }
                    isPaused = false
                }
            }

            jobs.joinAll()
        // 단일 스레드에서 여러 스레드로 분리 시켰기 때문에 JoinAll 필요
        }.join()
    }

    private suspend fun addCar(carName: String) {
        val jobs = scope.launch {
            cars.add(Car(carName))
        }
        jobs.join()
    }

    private suspend fun move(car: Car) {
        while (car.position < goal) {
            yield() // 스코프 취소됐을때 while문 벗어나기 위함
            if (!isPaused) {
                car.move()
                if (car.position == goal) {
                    println("${car.name}가 최종 우승했습니다.")
                    scope.cancel()
                }
            }
        }
    }
}