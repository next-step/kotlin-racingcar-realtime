package racingcar.model

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
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

    private val raceScope = CoroutineScope(dispatcher)
    private val isPaused = AtomicBoolean(false)

    private var jobs = mutableMapOf<String, Job>()
    suspend fun startRace() {
        raceScope.launch {
            launchRace()
            launchInput()
            readyForNewCar()
        }.join()
    }

    private suspend fun launchRace() = raceScope.launch{
      jobs = _cars.map { car ->
                val job = launch {
                    goCar(car)
                }
                car.name to job
            }.toMap().toMutableMap()
    }

    private fun launchInput() {
        raceScope.launch(Dispatchers.IO) {
            while (coroutineContext.isActive) {
                val input = readln()

                if (input.isEmpty()) {
                    isPaused.set(true)  // 입력받기 전에 멈춤
                    val inputCarName  = readln()

                    if (inputCarName.startsWith("add ")) {
                        val newCarName = inputCarName.removePrefix("add ").trim()
                        val newCar = Car(newCarName)
                        channel.send(newCar)
                    }

                    if (inputCarName.startsWith("boost ")) {
                        val boostCarName = inputCarName.removePrefix("boost ").trim()
                        val boostCar =  cars.first { it.name == boostCarName }
                        boostCar.boost()
                        println("${boostCar.name} 부스트!(x${boostCar.rate})" )
                    }

                    if (inputCarName.startsWith("slow ")) {
                        val slowCarName = inputCarName.removePrefix("slow ").trim()
                        val slowCar =  cars.first { it.name == slowCarName }
                        slowCar.slow()
                        println("${slowCar.name} 슬로우~~(x${slowCar.rate})" )
                    }

                    if (inputCarName.startsWith("stop ")) {
                        val stopCarName = inputCarName.removePrefix("stop ").trim()
                        val stopCar =  cars.first { it.name == stopCarName}
                        _cars.remove(stopCar)
                        jobs.get(stopCar.name)?.cancel()
                        jobs.remove(stopCar.name)
                        println("${stopCar.name} 차 정지!! " )
                    }

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
