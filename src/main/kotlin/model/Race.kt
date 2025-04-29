package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class Race(
    val cars: List<Car>,
    val goal: Int,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope: CoroutineScope = CoroutineScope(dispatcher + SupervisorJob()) // 이걸 왜 사용해야하더라...

    suspend fun start() {
        // 경기 시작
        val startJob = scope.launch { launchStart() }
        startJob.join()
    }

    suspend fun launchStart() {
        val jobs =
            cars.map {
                scope.launch { move(it) }
            }
        jobs.joinAll()
    }

    suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goal) {
            car.move()

            if (car.position == goal) {
                println("${car.name}가 최종 우승했습니다.")
                scope.cancel()
            }
        }
    }
}
