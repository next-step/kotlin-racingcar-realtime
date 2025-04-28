package racingcar.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import racingcar.view.OutputView

class RaceGame(
    private val cars: List<RacingCar>,
    private val goalDistance: Int
) {
    private var winner: RacingCar? = null
    private val raceScope = CoroutineScope(Dispatchers.Default)

    suspend fun startRace() {
        val jobs = cars.map { car ->
            raceScope.launch {
                while (isActive && car.getPosition() < goalDistance) {
                    car.moveForward()
                    println("${car.name} : ${"-".repeat(car.getPosition())}")

                    if (car.getPosition() >= goalDistance && winner == null) {
                        winner = car
                        raceScope.cancel() // 다른 차들 중단
                    }
                }
            }
        }

        // 모든 job이 끝날 때까지 기다리기
        jobs.joinAll()

        // 우승자 발표
        winner?.let {
            OutputView().announceWinners(listOf(it))
        } ?: println("우승자가 없습니다.")
    }
}
