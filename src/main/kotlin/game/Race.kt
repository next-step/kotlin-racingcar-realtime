package game

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicBoolean

class Race(
    participants: List<Car>,
    private val goal: Int,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val _cars: MutableList<Car> = participants.toMutableList()
    val cars: List<Car>
        get() = _cars.toList()

    private val scope = CoroutineScope(dispatcher + SupervisorJob())

    private val isPaused = AtomicBoolean(false)
    private val channel: Channel<Pair<Operation, Car>> = Channel(Channel.UNLIMITED)

    suspend fun start() {
        launchRace()
        launchInput()
        monitorRace()
    }

    private fun launchRace() {
        _cars.forEach { move(it) }
    }

    private fun move(car: Car) {
        scope.launch {
            while (scope.isActive && !car.isArrived(goal)) {
                // 경기가 중단되지 않았다면
                if (!isPaused.get()) {
                    car.move()
                    car.printPosition()

                    // 목표거리에 도달한 경우 다른 코루틴을 취소하여 경기 종료
                    if (car.isArrived(goal)) {
                        scope.cancel() // scope 내 모든 코루틴 취소
                        // channel.close()
                        break
                    }
                }
            }
        }
    }

    private fun launchInput() {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                val input = readlnOrNull()
                if (input.isNullOrBlank()) {
                    togglePause()
                } else {
                    val i = input.split(" ")
                    controlRace(i[0], i[1])
                    resumeRace()
                }
            }
        }
    }

    private suspend fun monitorRace() {
        while (scope.isActive) {
            if (!channel.isEmpty) {
                val event = channel.receive()
                val car = event.second
                when (event.first) {
                    Operation.ADD -> {
                        println("${car.name} 참가 완료!")
                        println()
                        move(car)
                    }
                    Operation.BOOST -> {
                        car.boost()
                        println("${car.name} 속도 2배 증가! \n")
                    }
                    else -> {}
                }
            }
//            try {
//                val car = channel.receive()
//                println("${car.name} 참가 완료!")
//                println()
//                move(car)
//            } catch (e: ClosedReceiveChannelException) {
//                println("=== channel is closed. ===")
//            }
        }
    }

    fun togglePause() {
        isPaused.set(!isPaused.get())
    }

    fun pauseRace() {
        isPaused.set(true)
    }

    fun resumeRace() {
        isPaused.set(false)
    }

    suspend fun controlRace(operation: String, carName: String) {
        when (operation) {
            Operation.ADD.operation -> channel.send(Pair(Operation.ADD, Car(carName)))
            Operation.BOOST.operation -> channel.send(Pair(Operation.BOOST, Car(carName)))
            Operation.SLOW.operation -> channel.send(Pair(Operation.SLOW, Car(carName)))
            Operation.STOP.operation -> channel.send(Pair(Operation.STOP, Car(carName)))
            else -> { println("Unknown operation") }
        }
    }

    fun getWinners(): List<String> {
        return _cars.filter { it.isArrived(goal) }.map { it.name }
    }
}