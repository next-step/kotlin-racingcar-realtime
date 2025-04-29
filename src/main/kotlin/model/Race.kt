package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class Race(
    cars: List<Car>,
    private val goal: Int,
    private val channel: Channel<Car> = Channel(Channel.UNLIMITED),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val _cars = cars.toMutableList()
    val cars: List<Car> get() = _cars
    private var errorMsg: String = ""

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val isPaused = AtomicBoolean(false)

    suspend fun start() {
        val startJob = scope.launch { launchStart() }
        val inputJob = scope.launch { launchInput() }
        val monitorJob = scope.launch { monitorRace() }

        startJob.join()
        inputJob.join()
        monitorJob.join()
    }

    private suspend fun launchStart() {
        val jobs =
            _cars.map { car ->
                scope.launch { move(car) }
            }
        jobs.joinAll()
    }

    private fun launchInput() {
        scope.launch {
            while (isActive) {
                val input = readlnOrNull()
                if (input != null) {
                    pauseRace()
                    processInput()
                    resumeRace()
                }
            }
        }
    }

    private suspend fun monitorRace() {
        for (car in channel) {
            println("${car.name} 참가 완료!")
            _cars.add(car)
            scope.launch { move(car) }
        }
    }

    private fun pauseRace() = isPaused.set(true)

    private fun resumeRace() = isPaused.set(false)

    private suspend fun reProcessInput() {
        println(errorMsg)
        processInput()
    }

    private suspend fun processInput() {
        val input = readln()

        if (input.isEmpty()) return

        val parts = input.split(' ')
        if (checkInput(parts)) {
            reProcessInput()
            return
        }

        val (commandStr, carName) = parts
        val car = _cars.find { it.name == carName }

        val command = RaceCommand.entries.find { it.name.equals(commandStr, ignoreCase = true) }
        if (command == null) {
            errorMsg = "알 수 없는 명령입니다."
            reProcessInput()
            return
        }

        when (command) {
            RaceCommand.ADD -> channel.send(Car(carName))
            RaceCommand.BOOST, RaceCommand.SLOW, RaceCommand.STOP -> {
                if (car != null) {
                    car.moveType = RaceMode.valueOf(command.name)
                } else {
                    errorMsg = "해당 이름의 자동차가 없습니다."
                    reProcessInput()
                    return
                }
            }
        }
    }

    private fun checkInput(parts: List<String>): Boolean {
        if (parts.size != 2) {
            errorMsg = "잘못된 입력입니다. 다시 입력해주세요."
            return true
        }
        return false
    }

    private suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goal) {
            if (!isPaused.get()) {
                car.move()
                checkWinner(car)
            }
        }
    }

    private fun checkWinner(car: Car) {
        if (car.position == goal) {
            println("${car.name}가 최종 우승했습니다.")
            scope.cancel()
            channel.close()
        }
    }
}
