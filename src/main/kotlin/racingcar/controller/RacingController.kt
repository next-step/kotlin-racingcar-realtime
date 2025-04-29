package racingcar.controller

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import racingcar.model.Car
import racingcar.view.RacingView
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class RacingController(
    cars: List<Car>,
    val goal: Int,
    private val racingView: RacingView,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val _cars: MutableList<Car> = cars.toMutableList()
    val cars: List<Car>
        get() = _cars

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val isPaused = AtomicBoolean(false)
    private val channel = Channel<String>(Channel.UNLIMITED)

    suspend fun start() {
        launchRace()
        waitCommand()
        runCommand()
    }

    private fun launchRace() {
        _cars.forEach {
            scope.launch { move(it) }
        }
    }

    private suspend fun move(car: Car) {
        while (coroutineContext.isActive && car.position < goal) {
            if (!isPaused.get()) {
                yield() // while 이 종료되는 시점이 car.forward 내부의 delay 에 걸리기 전에 종료
                car.forward()
                racingView.positionView(car)
                car.getTheGoal()
            }
        }
    }

    private fun Car.getTheGoal() {
        if (this.position >= goal) {
            racingView.resultView(this)
            scope.cancel()
            channel.close()
        }
    }

    private fun waitCommand() =
        scope.launch {
            while (isActive) {
                val input = readln()
                channel.send(input)
            }
        }

    private suspend fun runCommand() {
        for (c in channel) {
            if (c.isEmpty()) {
                pauseRacing()
            } else {
                try {
                    val input = c.split(" ")
                    if (input.size != 2) {
                        throw IllegalArgumentException("invalid input: $c")
                    }
                    controlCar(input[0], input[1])
                } catch (e: IllegalArgumentException) {
                    println("[ERROR] ${e.message}")
                } catch (e: IllegalStateException) {
                    println("[ERROR] ${e.message}")
                }
            }
        }
    }

    private fun pauseRacing() {
        isPaused.set(!isPaused.get())
        if (isPaused.get()) {
            println("(사용자 엔터 입력)")
        }
    }

    private fun controlCar(
        command: String,
        name: String,
    ) {
        when (command) {
            "add" -> {
                if (_cars.map { it.name }.toList().contains(name)) {
                    throw IllegalArgumentException("duplicate name: $name")
                }
                addCar(name)
            }

            "boost", "slow", "stop", "start" -> {
                var car: Car
                _cars
                    .find { it.name == name }
                    .apply {
                        if (this == null) {
                            throw IllegalArgumentException("no car named $name")
                        }
                        car = this
                    }
                speedControlCar(car, command)
            }

            else -> throw IllegalArgumentException("invalid command: $command")
        }
    }

    private fun addCar(name: String) {
        val car = Car(name)
        _cars.add(car)
        scope.launch { move(car) }
        println("$name 참가 완료!")
    }

    private fun speedControlCar(
        car: Car,
        command: String,
    ) {
        when (command) {
            "boost" -> {
                car.boost()
                println("${car.name} 속도 2배 증가!")
            }
            "slow" -> {
                car.slow()
                println("${car.name} 속도 2배 감소!")
            }
            "stop" -> {
                car.stop()
                println("${car.name} 정지!")
            }
            "start" -> {
                car.start()
                println("${car.name} 출발!")
            }
        }
    }
}
