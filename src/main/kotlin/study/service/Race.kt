package study.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import study.domain.Car
import study.domain.Command
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.filter
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.ranges.random
import kotlin.time.Duration.Companion.milliseconds

class Race(
    cars: List<Car>,
    val goal: Int = 0,
    private val channel: Channel<Command> = Channel(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val _cars: MutableList<Car> = cars.toMutableList()
    val cars: List<Car>
        get() = _cars.toList()

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private lateinit var jobs: List<Job>
    private val isPaused = AtomicBoolean(false)

    suspend fun startRace() {
        launchRace()
        monitorCommand()

        jobs.joinAll()
        evaluateWinner()
    }

    private fun launchRace() {
        jobs = _cars.map(::makeMovingCarJob)
    }

    private fun monitorCommand() {
        scope.launch(Dispatchers.IO) {
            for (command in channel)
                analysisCommand(command)
        }
    }

    fun pauseRace() {
        isPaused.set(true)
    }

    fun resumeRace() {
        isPaused.set(false)
    }

    private fun endRaceIfReachedGoal(car: Car) {
        if (car.isReachToGoal(goal)) {
            scope.cancel()
        }
    }

    private fun analysisCommand(command: Command) {
        if ("add" == command.command) {
            addCar(Car(command.name))
            return
        }

        cars.firstOrNull { it.name == command.name }?.let {
            when (command.command) {
                "boost" -> it.boost()
                "slow" -> it.slow()
                "stop" -> it.pause()
                "resume" -> it.resume()
            }
        }
    }

    private fun addCar(car: Car) {
        _cars.add(car)
        jobs += makeMovingCarJob(car)
        println("${car.name} 참가 완료!\n")
    }

    private fun makeMovingCarJob(car: Car): Job =
        scope.launch {
            while (kotlin.coroutines.coroutineContext.isActive && !car.isReachToGoal(goal)) {
                moveCar(car)
            }
        }

    private suspend fun moveCar(car: Car) {
        val duration = (0..(5 * car.acceleration.toInt())).random().milliseconds
        delay(duration)
        if (car.isPause || isPaused.get()) return
        car.moveForward()
        endRaceIfReachedGoal(car)
    }

    private fun evaluateWinner() {
        val winners = _cars.filter { it.isReachToGoal(goal) }.map { it.name }
        println("\n${winners.joinToString(separator = ",", postfix = "가 최종 우승했습니다.")}")
    }
}
