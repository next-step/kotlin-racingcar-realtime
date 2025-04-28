package service

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import model.Car

class RealtimeRacingCarService {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val channel: Channel<Car> = Channel(Channel.UNLIMITED)
    private var isPaused = false

    suspend fun start(cars: List<Car>, distance: Int) {
        coroutineScope {
            scope.launch {
                first(cars, distance)
            }
            scope.launch {
                pause()
            }
        }.join()
    }

    private suspend fun first(cars: List<Car>, distance: Int) {
        coroutineScope {
            cars.map {
                scope.launch {
                    channel.send(it)
                }
            }.joinAll()

            while (true) {
                val car = channel.receive()
                scope.launch {
                    move(car, distance)
                }
            }
        }
    }

    private suspend fun pause() {
        coroutineScope {
            scope.launch {
                while (isActive) {
                    val input = readln()
                    if (input.isEmpty()) {
                        isPaused = !isPaused
                    } else if (input.startsWith("add") && input.split(" ").size == 2 && input.split(" ")[1].isNotBlank()) {
                        val newCarName = input.split(" ")[1]
                        println("새로운 자동차가 추가되었습니다: $newCarName")
                        channel.send(Car(newCarName, 0))

                        isPaused = false
                    }
                }
            }
        }
    }

    private suspend fun move(car: Car, distance: Int) {
        while (car.position < distance) {
            if(isPaused) {
                delay(100L)
            } else {
                delay((1000..5000).random().toLong())
                car.move()
                car.printCurrentPosition()
                if (car.position == distance) {
                    car.printWinner()
                    scope.cancel()
                }
            }
        }
    }
}