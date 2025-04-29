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
import kotlinx.coroutines.withContext
import study.domain.Car
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.filter
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.ranges.random
import kotlin.time.Duration.Companion.milliseconds

class Race(
    var cars: MutableList<Car>,
    val goal: Int = 0,
    private val channel: Channel<Car>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private lateinit var jobs: List<Job>
    private val isPaused = AtomicBoolean(false)

    fun readyRace() {
        jobs =
            cars.map {
                makeCarJob(it)
            }

        scope.launch(Dispatchers.IO) {
            for (car in channel) {
                addCar(car)
            }
        }
    }

    suspend fun startRace() {
        jobs.joinAll()
        printWinner()
        channel.close()
    }

    fun pauseRace() {
        isPaused.set(true)
    }

    fun resumeRace() {
        isPaused.set(false)
    }

    private fun finishRace() {
        scope.cancel()
    }

    private fun addCar(car: Car) {
        cars.add(car)
        jobs += makeCarJob(car)
        println("${car.name} 참가 완료!\n")
    }

    private fun makeCarJob(car: Car): Job =
        scope.launch {
            while (!car.isReachToGoal(goal) && coroutineContext.isActive) {
                move(car)
            }
        }

    private suspend fun move(car: Car) {
        val duration = (0..500).random().milliseconds
        delay(duration)
        if (isPaused.get()) return
        car.moveForward()
        if (car.isReachToGoal(goal)) {
            finishRace()
        }
    }

    private suspend fun printWinner() {
        val winners = cars.filter { it.isReachToGoal(goal) }.map { it.name }
        withContext(Dispatchers.IO) {
            println("\n${winners.joinToString(separator = ",", postfix = "가 최종 우승했습니다.")}")
        }
    }
}
