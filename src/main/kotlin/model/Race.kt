package model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext


class Race(
    cars: List<Car>,
    val goal: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val _cars: MutableList<Car> = cars.toMutableList()
    val cars: List<Car>
        get() = _cars.toList()

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val carChannel: Channel<Pair<CommandType, Car>> = Channel(Channel.UNLIMITED)
    val isPaused: AtomicBoolean = AtomicBoolean(false)
    private val jobMap = mutableMapOf<String, Job>()


    suspend fun start() {
        launchRace()
        launchInput()
        monitorRace()
    }

    private fun launchRace() {
        _cars.forEach {
            val job = scope.launch {
                println("move: ${it.name}")
                move(it)
            } // 직접만든 scope로 동작되게

            jobMap[it.name] = job
        }
    }

    private fun launchInput() {
        scope.launch {
            while (isActive) {
                yield()
                val input = withContext(Dispatchers.IO) { readlnOrNull() }

                if (input != null) {
                    println("(사용자 엔터 입력)")
                    pauseRace()
                    readln()?.let {
                        val pair = commandTypePair(it)
                        if (pair != null) {
                            carChannel.send(pair)
                        }
                        resumeRace()
                    }
                }
            }
        }
    }

    private suspend fun monitorRace() {
        while (coroutineContext.isActive) {
            while (!carChannel.isEmpty) {
                if (!coroutineContext.isActive) break

                val commandCar = carChannel.receive()
                val commandType = commandCar.first
                val car = commandCar.second

                println("$commandCar")
                when (commandType) {
                    CommandType.ADD -> {
                        println("${car.name} 참가완료!")
                        addCar(car)
                    }
                    CommandType.BOOST -> car.speedUp()
                    CommandType.SLOW -> car.slowDown()
                    CommandType.STOP -> {
                        jobMap[car.name]?.cancel()
                    }
                    CommandType.NONE -> {

                    }

                }
            }
        }
    }

    private fun pauseRace() {
        isPaused.set(true)
    }

    private fun commandTypePair(message: String): Pair<CommandType, Car>? {
        var command: String?
        var name: String?

        message.split(" ").let {
            command = it.getOrNull(0)
            name = it.getOrNull(1)
        }

        if (command.isNullOrBlank() || name.isNullOrBlank()) return null

        val commandType = CommandType.fromCommand(command!!)

        return when (commandType) {
            CommandType.ADD -> {
                Pair(commandType, Car(name!!))
            }
            CommandType.BOOST,
            CommandType.SLOW,
            CommandType.STOP -> {
                _cars.firstOrNull { it.name == name }?.let { Pair(commandType, it) }
            }
            CommandType.NONE -> null
        }
    }

    private fun resumeRace() {
        isPaused.set(false)
    }

    private suspend fun addCar(car: Car) {
        val jobs = scope.launch {
            _cars.add(car)
            move(car)
        }
        jobs.join()
    }

    private suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goal) {
//            yield() // 스코프 취소됐을때 while문 벗어나기 위함 <-- 강사님은 왜 이거 없지?

            if (!isPaused.get()) {
                car.move()
                car.checkWinner()
            } else {
                delay(100)
            }
        }
    }

    private fun Car.checkWinner() {
        if (position == goal) {
            println("${name}가 최종 우승했습니다.")
            scope.cancel()
        }
    }
}

