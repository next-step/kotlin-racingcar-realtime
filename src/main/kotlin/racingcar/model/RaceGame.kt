package racingcar.model

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class RaceGame(
    private val cars: List<RacingCar>
) {
    fun getCars(): List<RacingCar> = cars

    suspend fun runOneRound() {
        cars.forEach { car ->
            coroutineScope {
                launch {
                    val delayTime = RandomMovingRule.getDelayTime()
                    car.moveForward(delayTime)
                    println("${car.name} : ${"-".repeat(car.getPosition())}")
                }
            }
        }
    }

    fun findWinners(goalDistance: Int): List<RacingCar> {
        return cars.filter { it.getPosition() == goalDistance }
    }
}
