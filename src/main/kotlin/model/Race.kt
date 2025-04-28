package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class Race(
    val cars: List<Car>,
    val goalDistance: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    suspend fun start() {
        val jobs =
            cars.map {
                scope.launch { move(it) }
            }

        jobs.joinAll()
    }

    private suspend fun move(car: Car) {
        while (isActive && car.position < goalDistance) {
            yield()
            car.move()
            if (car.position == goalDistance) {
                println("${car.carName}가 최종 우승했습니다.")
                scope.cancel()
            }
        }
    }
}
