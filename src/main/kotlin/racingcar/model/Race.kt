package racingcar.model

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    val cars: List<Car>,
    val goal: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val isPaused: AtomicBoolean = AtomicBoolean(false)

    
    suspend fun start() {
        val jobs = cars.map {
            scope.launch {
                move(it)
            }
        }
        jobs.joinAll()
    }
    private suspend fun move(car: Car) {
        while(coroutineContext.isActive && car.position < goal) {
            if(!isPaused.get()) {
                car.move()
                checkWinner(car)
            }
        }
    }

    private fun checkWinner(car: Car) {
        if (car.position == goal) {
            println("${car.name}가 최종 우승했습니다.")
            scope.cancel()
        }
    }
}
