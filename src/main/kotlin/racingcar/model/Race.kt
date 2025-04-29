package racingcar.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    private val cars: List<Car>,
    private val goal: Int,
    private val channel: Channel<String> = Channel(Channel.UNLIMITED),
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val raceScope = CoroutineScope(dispatcher + SupervisorJob())
    val isPaused = AtomicBoolean(false)
    private val pauseMutex = Mutex(locked = false)

    suspend fun startRace() {
        val jobs = cars.map { car ->
            raceScope.launch {
                goCar(car)
            }
        }

        val inputJob = readyForNewCarIn()
        (jobs + inputJob).joinAll()
    }

    private suspend fun goCar(car: Car) {
        while (coroutineContext.isActive && car.position < goal) {
            if (!isPaused.get()) {
                car.moveForward()
                checkWinner(car)
            }
        }
    }

    private fun checkWinner(car: Car) {
        if (car.position == goal) {
            println("${car.name}가 최종 우승했습니다.")
            raceScope.cancel() // 다른 차들 중단
        }
    }

    fun readyForNewCarIn(): Job = raceScope.launch(Dispatchers.IO) {
        while (coroutineContext.isActive) {
            isPaused.set(true)  // 입력받기 전에 멈춤
            val input = readlnOrNull()
            if (input != null && input.isNotBlank()) {
                val newCar = Car(input)
                raceScope.launch {
                    goCar(newCar)
                }
                println("새 차 추가됨: ${newCar.name}")
            }
            isPaused.set(false)  // 입력 끝나고 다시 달림
        }
    }

}
