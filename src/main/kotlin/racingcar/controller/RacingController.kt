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
    private var channel = Channel<String>(Channel.UNLIMITED)

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
        if (this.position == goal) {
            racingView.resultView(this)
            scope.cancel()
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
                isPaused.set(!isPaused.get())
                if (isPaused.get()) {
                    println("(사용자 엔터 입력)")
                }
            } else {
                try {
                    val command = c.split(" ")
                    if (command[0] != "add" || command.size > 2) {
                        throw IllegalArgumentException("invalid command: $c")
                    }
                    val name = command[1]
                    if (_cars.map { it.name }.toList().contains(name)) {
                        throw IllegalArgumentException("duplicate name: $name")
                    }
                    val car = Car(name)
                    _cars.add(car)
                    scope.launch { move(car) }
                    println("$name 참가 완료!")
                } catch (e: IllegalArgumentException) {
                    println("[ERROR] ${e.message}")
                } catch (e: IllegalStateException) {
                    println("[ERROR] ${e.message}")
                }
            }
        }
    }
}
