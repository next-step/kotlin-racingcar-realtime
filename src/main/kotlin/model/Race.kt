package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class Race(
    val cars: List<Car>,
    val goal: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    suspend fun start() {
        val jobs = cars.map {
            scope.launch { move(it) } // 직접만든 scope로 동작되게
        }
        jobs.joinAll()
        // 단일 스레드에서 여러 스레드로 분리 시켰기 때문에 JoinAll 필요
    }

    private suspend fun move(car: Car) {
        while (car.position < goal) {
            yield() // 스코프 취소됐을때 while문 벗어나기 위함
            car.move()
            if (car.position == goal) {
                println("${car.name}가 최종 우승했습니다.")
                scope.cancel()
            }
        }
    }
}