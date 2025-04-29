package racingcar.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import racing.view.ConsoleView
import racingcar.model.Car
import racingcar.model.Race


class RaceController {
    suspend fun run() = runBlocking() {
        val carNames = validateCarNames()
        val goal = validateGoal()
        val cars = carNames.map { Car(it) }
        val channel = Channel<String>(UNLIMITED)

        val race = Race(cars, goal, channel)

        race.start()
        val job = launch(Dispatchers.IO) {
            while(isActive) {
                val input = readlnOrNull()
                if (input != null) {
                    race.isPaused.set(true)
                }
            }
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