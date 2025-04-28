package game

import kotlinx.coroutines.*

class Race(
    val participants: List<Car>,
    val destinationDistance: Int,
    val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private lateinit var jobs: List<Job>

    suspend fun start() {
        jobs = participants.map {
            scope.launch {
                while (isActive && !it.isArrived(destinationDistance)) {
                    it.move()
                    it.printPosition()

                    if (it.isArrived(destinationDistance)) {
                        scope.cancel() // scope 내 모든 코루틴 취소
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