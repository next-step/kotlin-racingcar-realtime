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
    private val channel: Channel<Car> = Channel(Channel.UNLIMITED),
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
            scope.launch { move(it) }
        }
    }

    private suspend fun monitorRace() {
        while (coroutineContext.isActive) { // 제어권을 넘겨주기 위한 용도
            while (!channel.isEmpty) {
                val car = channel.receive()
                println("${car.carName} 참가 완료!")
                _cars.add(car)
                scope.launch {
                    move(car)
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
            if (checkInput(inputList)) {
                println("다시 입력해주세요.")
                enterInput()
                return
            }
            val command = inputList[0]
            val carName = inputList[1]

            if (command == "add") {
                channel.send(Car(carName))
            }
        }
    }

    private fun checkInput(inputList: List<String>): Boolean {
        if (inputList.size < 2) { // 스페이스 유무 확인
            return true
        }
        if (inputList[1].length > 5) {
            return true
        }
        if (inputList[0] != "add") {
            return true
        }
        return false
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
