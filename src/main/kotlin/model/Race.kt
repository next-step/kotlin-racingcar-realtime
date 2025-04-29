package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    cars: List<Car>,
    val goalDistance: Int,
    private val channel: Channel<CarEvent> = Channel(Channel.UNLIMITED),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val _cars: MutableList<Car> = cars.toMutableList()
    val cars: List<Car>
        get() = _cars.toList()

    private val scope: CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())
    val isPaused: AtomicBoolean = AtomicBoolean(false)

    var jobs: MutableList<Job> = mutableListOf()

    suspend fun start() {
        val raceJob = scope.launch { launchRace() }
        val inputJob = scope.launch { launchInput() }
        val monitorJob = scope.launch { monitorRace() }

        raceJob.join()
        inputJob.join()
        monitorJob.join()
    }

    private fun launchRace() {
        _cars.forEach {
            scope.launch {
                move(it)
            }
        }
    }

    private fun boost(car: Car) {
        _cars.forEach {
            if (it.equals(car.carName)) {
                println(car.carName)
                it.boostSpeed()
            }
        }
    }

    private fun slow(car: Car) {
        _cars.forEach {
            if (it.equals(car.carName)) {
                println(car.carName)
                it.slowSpeed()
            }
        }
    }

    private suspend fun monitorRace() {
        while (coroutineContext.isActive) { // 제어권을 넘겨주기 위한 용도
            while (!channel.isEmpty) {
                val carEvent = channel.receive()

                when(carEvent) {
                    is CarEvent.Add -> {
                        println("${carEvent.car} 참가 완료!")
                        _cars.add(carEvent.car) // 뭘까
                        scope.launch {
                            move(carEvent.car)
                        }
                    }
                    is CarEvent.Boost -> {
                        println("${carEvent.car} 속도가 상승됩니다!")
                        boost(carEvent.car)
                    }
                    is CarEvent.Slow -> {
                        println("${carEvent.car} 속도가 하락합니다!")
                        slow(carEvent.car)
                    }
                }





            }
        }
    }

    private fun launchInput() {
        scope.launch {
            while (isActive) {
                val input = readlnOrNull()
                if (input != null) {
                    pauseRace()
                    enterInput()
                    resumeRace()
                }
            }
        }
    }

    private suspend fun enterInput() {
        val input = readln()

        if (input.isEmpty()) {
            return
        }

        if (input.isNotEmpty()) {
            val inputList = input.split(' ')
            if (isInputAvailable(inputList)) {
                val command = inputList[0]
                val carName = inputList[1]

                if (command.equals("add", ignoreCase = true)) channel.send(CarEvent.Add(Car(carName)))
                else if (command.equals("boost", ignoreCase = true)) channel.send(CarEvent.Boost(Car(carName)))
                else if (command.equals("slow", ignoreCase = true)) channel.send(CarEvent.Slow(Car(carName)))

            } else {
                println("다시 입력해주세요.")
                enterInput()
                return
            }
        }
    }

    private fun isInputAvailable(inputList: List<String>): Boolean {
        if (inputList.size < 2) { // 스페이스 유무 확인
            return false
        }
        if (inputList[1].length > 5) {
            return false
        }
        if (inputList[0] != "add" && inputList[0] != "boost" && inputList[0] != "slow") {
            return false
        }
        if (inputList[0] == "add") {
            _cars.forEach {
                if (it.equals(inputList[1])) {
                    return false
                }
            }
        } else {

        }

        return true
    }

    private fun pauseRace() {
        isPaused.set(true)
    }

    private fun resumeRace() {
        isPaused.set(false)
    }

    private suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goalDistance) {
            if (!isPaused.get()) {
                car.move()
                car.checkWinner()
            }
        }
    }

    private fun Car.checkWinner() {
        if (position == goalDistance) {
            println("${carName}가 최종 우승했습니다.")
            scope.cancel()
        }
    }
}
