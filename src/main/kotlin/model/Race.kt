package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    val cars: List<Car>,
    val goal: Int,
    val channel: Channel<Car> = Channel(Channel.UNLIMITED),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    val isPaused: AtomicBoolean = AtomicBoolean(false)
    private val scope: CoroutineScope = CoroutineScope(dispatcher + SupervisorJob()) // 이걸 왜 사용해야하더라...

    suspend fun start() {
        val startJob = scope.launch { launchStart() } // 경기 시작
        val inputJob = scope.launch { launchInput() }
        val monitorJob = scope.launch { monitorRace() }

        startJob.join()
        inputJob.join()
        monitorJob.join()
    }

    suspend fun launchStart() {
        val jobs =
            cars.map {
                scope.launch { move(it) }
            }
        jobs.joinAll()
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

    private suspend fun monitorRace() {
        while (coroutineContext.isActive) {
            for (car in channel) {
                println("${car.name} 참가 완료!")
                scope.launch { move(car) }
            }
        }
    }

    fun pauseRace() {
        isPaused.set(true)
    }

    fun resumeRace() {
        isPaused.set(false)
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
        if (inputList.size < 2) {
            return true
        }
        if (inputList[0] != "add") {
            return true
        }
        if (inputList[1].length > 5) {
            return true
        }
        return false
    }

    suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goal) {
            if (!isPaused.get()) {
                car.move()
                checkWinner(car)
            }
        }
    }

    fun checkWinner(car: Car) {
        if (car.position == goal) {
            println("${car.name}가 최종 우승했습니다.")
            scope.cancel()
            channel.close()
        }
    }
}
