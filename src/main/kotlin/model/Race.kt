package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    val cars: List<Car>,
    val goalDistance: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    val inputChannel = Channel<String>()
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
                readLine() ?: break
                isPaused.set(true)

                println("(사용자 엔터 입력)")
                var input = readLine() ?: break

                if (input.isEmpty()) {
                    isPaused.set(false)
                } else if (input.startsWith("add ")) {
                    inputChannel.send(input.replace("add ", ""))
                } else {
                    isPaused.set(false)
                }
            }
        }

        while (coroutineContext.isActive) {
            watingChannel()
        }
    }

    private suspend fun watingChannel() {

        val carName = inputChannel.receive() // 사용자가 입력을 완료할 때까지 대기
        println("$carName 참가 완료!")
        val inputCar = Car(carName)
        val nowJob = scope.launch { move(inputCar) }
        jobs.add(nowJob)

        isPaused.set(false)
    }

    private suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goalDistance) {

            if (!isPaused.get()) {
                car.move()
            }
            isWinner(car)
        }
    }

    private suspend fun isWinner(car: Car) {
        if (car.position == goalDistance) {
            println("${car.carName}가 최종 우승했습니다.")
            scope.cancel()
        }
    }
}
