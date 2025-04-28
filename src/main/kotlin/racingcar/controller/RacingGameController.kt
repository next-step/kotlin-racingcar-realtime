package racingcar.controller

import kotlinx.coroutines.runBlocking
import racingcar.model.RaceGame
import racingcar.model.RacingCar
import racingcar.view.InputView

object RacingGameController {

    fun start() = runBlocking {
        val inputView = InputView()

        val carNames = inputView.getCarNames()
        val goalDistance = inputView.getGoalDistance()

        val cars = carNames.map { RacingCar(it) }
        val raceGame = RaceGame(cars, goalDistance)

        raceGame.startRace()
    }
}