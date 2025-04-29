package racingcar.controller

import kotlinx.coroutines.coroutineScope
import racing.view.ConsoleView
import racingcar.model.Car
import racingcar.model.Race


class RaceController{
    suspend fun run() {
        val carNames = validateCarNames()
        val goal = validateGoal()
        val cars = carNames.map { Car(it) }

        val race = Race(cars, goal)
        coroutineScope {
            race.start()
        }
    }

    fun validateCarNames(): List<String> {
        while(true) {
            try {
                val cars = ConsoleView().inputCarNames()
                val invalid = cars.firstOrNull { it.length > 5 }
                if (invalid != null) {
                    throw IllegalArgumentException("[ERROR] 5자리를 초과한 이름이 있습니다: $invalid")
                }
                return cars.toSet().toList()
            } catch(e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    fun validateGoal(): Int {
        while(true) {
            try {
                return ConsoleView().inputGoal()
            } catch(e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }
}