package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicBoolean

class Race(
    val cars: List<Car>,
    val goalDistance: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    var inputChannel = Channel<String>()
    var isPaused = AtomicBoolean(false)
    var jobs: MutableList<Job> = mutableListOf()

    suspend fun start() {
        val nowJob =
            cars.map {
                scope.launch { move(it) }
            }

        jobs.addAll(nowJob)

        scope.launch {
            while (true) {
                if (isPaused.get()) {
                    break
                }

                readLine() ?: break
                println("1. 엔터 입력, isPause:${isPaused.get()} ")
                isPaused.set(true)
                println("1. 엔터 입력, isPause:${isPaused.get()} ")

//                jobs.forEach { it.cancel() }

                println("(사용자 엔터 입력)")
                var input = readLine() ?: break

                if (input.isEmpty()) {
                    isPaused.set(false)
                } else if (input.startsWith("add ")) {
                    println("2. input send, isPause:${isPaused.get()} ")
                    inputChannel.send(input.replace("add ", ""))
                } else {
                    isPaused.set(false)
                }
            }
        }

        // 채널을 consume하는 역할
        scope.launch {
            for (carName in inputChannel) {
                val newCar = Car(carName)
                println("새 참가자: ${newCar.carName}")

                val newJob = scope.launch { move(newCar) }
                jobs.add(newJob)

                isPaused.set(false)
            }
        }

        jobs.joinAll()
    }

    private suspend fun move(car: Car) {
        while (isActive && car.position < goalDistance) {
            while (isPaused.get()) {
                yield()
            }
            yield()
            car.move()
            if (car.position == goalDistance) {
                println("${car.carName}가 최종 우승했습니다.")
                inputChannel.close()
                scope.cancel()
            }
        }
    }
}
