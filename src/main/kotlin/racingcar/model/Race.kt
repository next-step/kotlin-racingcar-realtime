package racingcar.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import racingcar.support.CarCommand
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

                if (input != null && input.isBlank()) {
                    // 엔터만 쳤으면 -> 일시정지
                    isPaused.set(true)

                    println("명령어와 자동차 이름을 입력하세요. (add/boost/slow/stop), 아무 입력 없이 엔터치면 재개합니다.")
                    val command = readlnOrNull()?.trim()

                    if (command.isNullOrBlank()) {
                        // 다시 재개
                        println("경주를 다시 시작합니다.")
                        isPaused.set(false)
                        continue
                    }

                    val (carCommand, carName) = command.split(" ")
//                    handleCommand(CarCommand.valueOf(carCommand), cars.get(carName))

                    println("$carCommand 명령어 처리 완료. 경주를 다시 시작합니다.")
                    isPaused.set(false)
                }
            }
        }
    }

    private suspend fun handleCommand(carCommand: CarCommand, car: String) {

        when (carCommand) {
            CarCommand.add -> readyForNewCar()
            CarCommand.boost -> TODO()
            CarCommand.slow -> TODO()
            CarCommand.stop -> TODO()
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
