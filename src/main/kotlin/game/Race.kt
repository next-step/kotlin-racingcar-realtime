package game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

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

        jobs.joinAll()
    }

    fun getWinners(): List<String> {
        return participants.filter { it.isArrived(destinationDistance) }.map { it.name }
    }
}