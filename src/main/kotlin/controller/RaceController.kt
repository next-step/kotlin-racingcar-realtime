package controller

import entity.Car
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import model.RaceModel
import view.RaceView

class RaceController(
    val raceModel: RaceModel,
    val raceView: RaceView,
) {
    suspend fun runGame() {
        val carList = initCarList()
        val goal = initGoal()
        try {
            runRound(carList, goal)
        } catch (_: CancellationException) {
            runAward(carList, goal)
        }
    }

    private fun initCarList(): List<Car> {
        while (true) {
            try {
                raceView.showCarInitMsg()
                return raceModel.initCarList(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    private fun initGoal(): Int {
        while (true) {
            try {
                raceView.showGoalInitMsg()
                return raceModel.initGoal(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    private suspend fun runRound(
        carList: List<Car>,
        goal: Int,
    ) = coroutineScope {
        raceView.showRoundResult()
        launch(Dispatchers.Default) {
            carList.forEach {
                launch {
                    while (isActive) {
                        ensureActive()
                        raceModel.runRound(it)
                        raceView.showCarStatus(it)
                        if (it.checkWinner(goal)) {
                            this@coroutineScope.cancel()
                        }
                    }
                }
            }
        }
    }

    private fun runAward(
        carList: List<Car>,
        goal: Int,
    ) {
        val winners = raceModel.getWinners(carList, goal)
        raceView.showWinners(winners)
    }

    private fun handleError(e: Exception) {
        raceView.showErrorMsg(e.message.toString())
    }
}
