package study

import kotlinx.coroutines.runBlocking
import study.domain.Car
import study.service.Race
import study.view.InputView
import kotlin.apply
import kotlin.collections.map

fun main() =
    runBlocking {
        val carNames = InputView.readCarNames()
        val cars = carNames.map { Car(it) }
        val goal = InputView.readGoal()
        val race =
            Race(cars, goal).apply {
                start()
            }
    }
