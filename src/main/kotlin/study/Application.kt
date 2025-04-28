package study

import kotlinx.coroutines.runBlocking
import study.domain.Car
import study.service.Race
import study.view.InputView
import kotlin.collections.map

fun main() =
    runBlocking {
        val carNames = InputView.readCars()
        val cars = carNames.map { Car(it) }.toMutableList()
        val goal = InputView.readGoal()
        val race = Race(cars, goal)
        race.start()
    }
