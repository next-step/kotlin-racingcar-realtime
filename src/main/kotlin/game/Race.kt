package game

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

class Race(
    private val participants: List<Car>,
    private val destinationDistance: Int,
) {
    private val gameEndFlag = AtomicBoolean(false)

    suspend fun start() = coroutineScope {
        val jobs = participants.map {
            launch(Dispatchers.Default) {
                while (!gameEndFlag.get()) {
                    it.move()
                    it.printDistance()

                    if (it.isArrived(destinationDistance)) {
                        gameEndFlag.set(true)
                        break
                    }
                }
            }
        }

        checkGameEnd(jobs)
        jobs.joinAll()
    }

    private suspend fun checkGameEnd(jobs: List<Job>) = coroutineScope {
        launch {
            while (isActive) {
                if (gameEndFlag.get()) {
                    jobs.forEach { it.cancel() }
                    break
                }
            }
            delay(100.milliseconds)
        }
    }

    fun getWinners(): List<String> {
        return participants.filter { it.isArrived(destinationDistance) }.map { it.name }
    }
}