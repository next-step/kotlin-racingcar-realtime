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
    cars: List<Car>, // 방어적 복사 + 불변 객체 유지
    private val goal: Int,
    private val channel: Channel<Car> = Channel(Channel.UNLIMITED),
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val _cars: MutableList<Car> = cars.toMutableList()
    val cars: List<Car>
        get() = _cars.toList() // 외부에 링크할 때도 컬렉션의 헤드를 끊어버리기 위해 toList()

    private val raceScope = CoroutineScope(dispatcher + SupervisorJob())
    private val isPaused = AtomicBoolean(false)

    suspend fun startRace() {
        launchRace()
        launchInput()
        readyForNewCar()
    }

    private fun launchRace() {
        _cars.map { car ->
            raceScope.launch {
                goCar(car)
            }
        }
    }

    private fun launchInput() {
        raceScope.launch(Dispatchers.IO) {
            while (coroutineContext.isActive) {
                val input = readlnOrNull()
                if (input != null && input.isNotBlank()) {
                    isPaused.set(true)  // 입력받기 전에 멈춤

                    val newCar = Car(input)
                    channel.send(newCar)
                }
                isPaused.set(false)  // 입력 끝나고 다시 달림
            }
        }
    }

    private suspend fun readyForNewCar() {
        while (coroutineContext.isActive) {
            while (!channel.isEmpty) {
                val newCar = channel.receive()
                println("${newCar.name} 참가 완료!")
                _cars.add(newCar)
                raceScope.launch { goCar(newCar) }
            }
        }
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


}
