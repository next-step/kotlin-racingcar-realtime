package racingcar.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import racingcar.view.InputView
import racingcar.view.OutputView
import racingcar.model.RacingCar
import racingcar.model.RaceGame

object RacingGameController {

    fun start() {
        val inputView = InputView()
        val outputView = OutputView()

        // 사용자로부터 입력 받기
        val carNames = inputView.getCarNames()
        val goalDistance = inputView.getRoundCount()

        val cars = carNames.map { RacingCar(it) }
        val raceGame = RaceGame(cars)

        runBlocking {
            runRace(raceGame, goalDistance, outputView)
        }
    }

    // 경주 실행 및 출력
    private suspend fun runRace(raceGame: RaceGame, goalDistance: Int, outputView: OutputView) =
        CoroutineScope(Dispatchers.Default).launch {
            repeat(goalDistance) {
                val jobs = launch {
                    raceGame.runOneRound()
                }

                jobs.join()

                val winners = raceGame.findWinners(goalDistance)
                if (winners.isNotEmpty()) {
                    jobs.cancel()

                    // 우승자 출력
                    outputView.announceWinners(winners)
                }
            }

        }.join()
}
