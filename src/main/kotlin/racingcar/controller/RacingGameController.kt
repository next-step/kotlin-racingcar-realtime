package racingcar.controller

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import racingcar.model.Race
import racingcar.model.Car
import racingcar.view.InputView

object RacingGameController {
    fun start() = runBlocking {
        val inputView = InputView()

        val carNames = inputView.getCarNames()
        val goalDistance = inputView.getGoalDistance()

        val cars = carNames.map { Car(it) }.toMutableList()
        val channel = Channel<Car>(Channel.UNLIMITED)
        val race = Race(cars, goalDistance, channel)

        race.startRace()
    }
}