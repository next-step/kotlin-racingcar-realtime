package study.domain

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

object Contest {
    class Requirement(
        val command: String,
        val target: String,
    )

    class Car(
        val name: String,
        var position: Int = 0,
        var speed: Int = 1,
        var isPause: Boolean = false,
    ) {
        fun move() {
            position++
            println("$name : ${"-".repeat(position)}")
        }

        fun speedUp() {
            speed++
            println("$name 속도 2배 증가!\n")
        }

        fun speedDown() {
            speed--
            println("$name 속도 2배 감소!\n")
        }

        fun pause() {
            isPause = true
            println("$name 일시 정지\n")
        }

        fun resume() {
            isPause = true
            println("$name 재개\n")
        }
    }

    class Race(
        cars: List<Car>,
        val goal: Int,
        val channelRequirement: Channel<Requirement>,
    ) {
        private var _cars: MutableList<Car> = cars.toMutableList()
        val cars: List<Car>
            get() = _cars.toList()

        private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        private lateinit var jobs: List<Job>
        private var isPaused = AtomicBoolean(false)

        suspend fun start() {
            println("실행 결과")
            jobs =
                _cars.map {
                    scope.launch {
                        while (kotlin.coroutines.coroutineContext.isActive) {
                            move(it)
                        }
                    }
                }
            scope.launch(Dispatchers.IO) {
                while (isActive) {
                    for (requirement in channelRequirement) {
                        runRequirement(requirement)
                    }
                }
            }

            jobs.joinAll()
        }

        fun pause() {
            isPaused.set(true)
        }

        fun resume() {
            isPaused.set(false)
        }

        private fun runRequirement(requirement: Requirement) {
            if ("add" == requirement.command) {
                val car = Car(requirement.target)
                _cars.add(car)
                jobs +=
                    scope.launch {
                        while (kotlin.coroutines.coroutineContext.isActive) {
                            move(car)
                        }
                    }
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

        private suspend fun move(car: Car) {
            delay((0..(50 * 10 / car.speed)).random().milliseconds)
            if (isPaused.get()) return
            car.move()
            if (car.position >= goal) {
                println("\n${car.name} 최종 우승했습니다.")
                scope.cancel()
            }
        }
    }
}
