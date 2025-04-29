package racingcar.model

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    val cars: List<Car>,
    val goal: Int,
    val channel: Channel<String> = Channel(UNLIMITED),
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var isPaused: AtomicBoolean = AtomicBoolean(false)

    
    suspend fun start() {
        cars.forEach {
            scope.launch {
                move(it)
            }
        }

        launchInput() //race 내부에서 사용자의 인풋을 감지하는 함수
    }

    private fun launchInput() {
        scope.launch {
            while (isActive) {
                val input = readlnOrNull()
                if (input != null) {
                    isPaused.set(true)
                }
            }
        }
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
