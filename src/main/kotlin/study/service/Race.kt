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
import study.view.InputView
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.filter
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.ranges.random
import kotlin.text.repeat
import kotlin.time.Duration.Companion.milliseconds

class Race(
    var cars: MutableList<Car>,
    val goal: Int = 0,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private lateinit var jobs: List<Job>
    private val pauseState = AtomicBoolean(false)

    private val channel = Channel<Car>(Channel.UNLIMITED)

    suspend fun start() {
        jobs =
            cars.map {
                makeCarJob(it)
            }

        scope.launch(Dispatchers.IO) {
            while (isActive) {
                InputView.readyAddCar()
                pauseState.set(true)
                val addCar = Car(InputView.readAddCar())
                channel.send(addCar)
                pauseState.set(false)
            }
        }

        scope.launch {
            for (car in channel) {
                makeAddCarJob(car)
                println("${car.name} 참가 완료!\n")
            }
        }

        jobs.joinAll()
        printWinner()
    }

    private suspend fun makeAddCarJob(car: Car) {
        cars.add(car)
        jobs += makeCarJob(car)
        channel.send(car)
    }

    private fun makeCarJob(car: Car): Job =
        scope.launch {
            while (car.isRunning(goal) && isActive) {
                move(car)
            }
        }

    private suspend fun move(car: Car) {
        val duration = (0..500).random().milliseconds
        delay(duration)
        if (pauseState.get()) return
        car.position++
        println("${car.name}: ${"-".repeat(car.position)}")
        if (car.isWinner(goal)) {
            scope.cancel()
        }
    }

    private suspend fun printWinner() {
        val winners = cars.filter { it.isWinner(goal) }.map { it.name }
        withContext(Dispatchers.IO) {
            println("\n${winners.joinToString(separator = ",", postfix = "가 최종 우승했습니다.")}")
        }
    }
}
