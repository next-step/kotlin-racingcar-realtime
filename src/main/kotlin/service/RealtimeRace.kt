package service

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import model.Car
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.coroutineContext

class RealtimeRace(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    private val channel: Channel<Car> = Channel(Channel.UNLIMITED),
    private val readInput: suspend () -> String = { readln() },
    private val usingPause: Boolean = true,
    private val onRaceFinish: () -> Unit = { scope.cancel() }
) {
    private val isPaused: AtomicBoolean = AtomicBoolean(false)
    private var _cars: MutableList<Car> = mutableListOf()
    val cars: List<Car> get() = _cars.toList()
    private val jobs: MutableList<Job> = mutableListOf()

    suspend fun start(cars: List<Car>, distance: Int) {
        // mutableList에 모두 삽입
        _cars.addAll(cars)

        val raceJob = scope.launch {
            race(_cars, distance)
        }
        jobs.add(raceJob)

        if (usingPause) {
            val pauseJob = scope.launch {
                pause()
            }
            jobs.add(pauseJob)
        }
        println()
        println(">> 레이스 시작")
        joinAll(*jobs.toTypedArray())
    }

    private suspend fun race(cars: List<Car>, distance: Int) {
        cars.map {
            scope.launch {
                channel.send(it)
            }
        }.joinAll()

        for (i in channel) {
            scope.launch {
                move(i, distance)
            }
        }
    }

    private suspend fun move(car: Car, distance: Int) {
        while (coroutineContext.isActive && car.position < distance) {
            waitIfPaused(isPaused)      // 1차 상태값 체크
            car.waitRandomTime()
            waitIfPaused(isPaused)      // 2차 상태값 체크 (waitRandomTime이 suspend fun이므로)
            car.move()
            checkWinner(car, distance)
        }
    }

    private fun pause() {
        scope.launch {
            while (isActive) {
                val input = readInput()
                when {
                    input.isEmpty() -> {
                        pauseRace()
                    }
                    input.startsWith("add ") -> {
                        handleAddCommand(input)
                        resumeRace()
                    }
                    else -> {
                        println("잘못 입력했습니다.")
                        resumeRace()
                    }
                }
            }
        }
    }

    private fun pauseRace() {
        println(">> 레이스 일시 정지")
        isPaused.set(true)
    }

    fun resumeRace() {
        println(">> 레이스 재개")
        isPaused.set(false)
    }

    suspend fun handleAddCommand(input: String) {
        val newCarName = input.split(" ", limit = 2).getOrNull(1)?.takeIf { it.isNotBlank() }

        if (newCarName == null) {
            println("잘못된 추가 명령입니다.")
        } else if (_cars.any { it.name == newCarName }) {
            println("이미 있는 자동차 이름입니다.")
        } else {
            println("새로운 자동차가 추가되었습니다: $newCarName")
            val newCar = Car(newCarName, 0, false)
            _cars.add(newCar)
            channel.send(newCar)
        }
    }

    private suspend fun waitIfPaused(isPaused: AtomicBoolean) {
        while(isPaused.get()) {
            delay(100L)
        }
    }

    private fun checkWinner(car: Car, distance: Int) {
        if (car.position == distance) {
            car.printWinner()
            car.isWinner = true
            onRaceFinish()
        }
    }
}