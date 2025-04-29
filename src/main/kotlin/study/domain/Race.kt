package study.domain

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
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

class Race(
    cars: List<Car>,
    val goal: Int,
    val channelRequirement: Channel<Requirement> = Channel(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private var _cars: MutableList<Car> = cars.toMutableList()
    val cars: List<Car>
        get() = _cars.toList()

    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private lateinit var jobs: List<Job>
    private var isPaused = AtomicBoolean(false)

    suspend fun start() {
        println("실행 결과")
        jobs = _cars.map(::signUpCar)

        scope.launch(Dispatchers.IO) {
            while (isActive) {
                for (requirement in channelRequirement) {
                    runRequirement(requirement)
                }
            }
        }

        jobs.joinAll()
    }

    fun pauseRace() {
        isPaused.set(true)
    }

    fun resumeRace() {
        isPaused.set(false)
    }

    private fun runRequirement(requirement: Requirement) {
        if ("add" == requirement.command) {
            val car = Car(requirement.target)
            _cars.add(car)
            jobs += signUpCar(car)
            println("${requirement.target} 참가 완료!\n")
            return
        }

        _cars.firstOrNull { it.name == requirement.target }?.let {
            when (requirement.command) {
                "boost" -> it.speedUp()
                "slow" -> it.speedDown()
                "stop" -> it.pause()
                "resume" -> it.resume()
            }
        }
    }

    private fun signUpCar(car: Car): Job =
        scope.launch {
            while (kotlin.coroutines.coroutineContext.isActive) {
                moveCar(car)
            }
        }

    private suspend fun moveCar(car: Car) {
        val duration = (0..(500 / car.speed).toInt()).random()
        delay(duration.milliseconds)
        if (isPaused.get()) return
        car.move()
        if (car.position >= goal) {
            println("\n${car.name} 최종 우승했습니다.")
            scope.cancel()
        }
    }
}
