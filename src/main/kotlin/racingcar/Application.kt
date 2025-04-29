package racingcar

import kotlinx.coroutines.runBlocking
import racingcar.controller.RacingController
import racingcar.model.Car
import racingcar.view.RacingView

fun main() =
    runBlocking {
        val racingView = RacingView()
        val cars = racingView.nameInputView().map { Car(it) }.toList()
        val goal = racingView.distanceInputView()
        val racingController = RacingController(cars, goal, racingView)
        racingController.start()
    }
