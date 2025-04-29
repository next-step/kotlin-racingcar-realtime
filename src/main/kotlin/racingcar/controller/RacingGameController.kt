package racingcar.controller

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import racingcar.model.Race
import racingcar.model.Car
import racingcar.view.InputView

object RacingGameController {
    fun start() = runBlocking {
        val inputView = InputView()

//        val carNames = inputView.getCarNames()
//        val goalDistance = inputView.getGoalDistance()

        val carNames = listOf("car1", "car2", "car3")
        val goalDistance = 10

        val cars = carNames.map { Car(it) }
        val channel = Channel<String>(Channel.UNLIMITED)
        val race = Race(cars, goalDistance, channel)

        race.startRace()
    }
}