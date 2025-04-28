package racingcar.controller

import kotlinx.coroutines.runBlocking
import racingcar.model.RaceGame
import racingcar.model.RacingCar
import racingcar.view.InputView
import racingcar.view.OutputView

object RacingGameController {

    fun start() = runBlocking {
        val inputView = InputView()
        val outputView = OutputView()

        val carNames = inputView.getCarNames()
        val goalDistance = inputView.getRoundCount()

        val cars = carNames.map { RacingCar(it) }
        val raceGame = RaceGame(cars, goalDistance)

        raceGame.startRace(outputView)
    }
}