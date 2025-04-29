package game2

import game.util.printlnWithTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
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
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random


fun main() {

    runBlocking {
        val carNames = listOf("car1", "car2", "car3", "car4", "car5")
        val goal = 10
        val cars = carNames.map { Car(it) }
        val channel = Channel<Car>(Channel.UNLIMITED) // 채널을 왜 밖에서 만들어줘야 하는가?
        val race = Race(cars, goal, channel)

        race.start()

    }
}

class Race(
    cars: List<Car>,
    val goal: Int,
    private val channel: Channel<Car> = Channel(Channel.UNLIMITED),
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val cars = cars.toMutableList()

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    val isPaused = AtomicBoolean(false)

    suspend fun start() {

        val jobs = moveCars()
        jobs.add(inputLaunch())
        jobs.add(monitoringRace())

        jobs.forEach { it ->
            it.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> printlnWithTime("코루틴 취소로 인해 종료됨 ${it}")
                }
            }
        }

        delay(1000)

        jobs.joinAll()

    }

    private fun moveCars(): MutableList<Job> {
        return cars.mapIndexed { index, car ->
            scope.launch (CoroutineName("Car-${car.name}")) {
                move(car)
            }
        }.toMutableList()
    }

    private fun inputLaunch(): Job {
        return scope.launch(Dispatchers.IO) {
            val reader = BufferedReader(InputStreamReader(System.`in`))

            while (isActive) {
                printlnWithTime("입력을 해주세요:")
                if (reader.ready()) {
                    val input = reader.readLine()
                    if (input != null) {
                        isPaused.set(true)
                        channel.send(Car("Car4"))
                        isPaused.set(false)
                    }
                }

                delay(1000)
            }
        }
    }

    private fun monitoringRace(): Job {
        return scope.launch {
            while (scope.isActive) {
                val car = channel.receive()
                printlnWithTime("${car.name} 참가 완료")

                cars.add(car)
                launch {
                    move(car)
                }
            }
        }
    }

    private suspend fun move(car: Car) {
        while (scope.isActive && car.position < goal) {
            if (!isPaused.get()) {
                car.move()
                if (car.position == goal) {
                    printlnWithTime("${car.name} 이 최종우승하였습니다,")

                    scope.cancel()
                }
            }
        }
    }
}

data class Car(
    val name: String,
    var position: Int = 0
) {
    suspend fun move() {
        val delayMillis = Random.nextInt(0, 1000).toLong()
        delay(delayMillis)
        position++
        printlnWithTime("${name} : ${"-".repeat(position)}")
    }
}
