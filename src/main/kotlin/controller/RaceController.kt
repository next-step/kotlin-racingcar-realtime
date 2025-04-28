package controller

import entity.Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import model.RaceModel
import view.RaceView

class RaceController(
    val raceModel: RaceModel,
    val raceView: RaceView,
) {
    fun initCarList(): List<Car> {
        while (true) {
            try {
                raceView.showCarInitMsg()
                return raceModel.initCarList(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    fun initGoal(): Int {
        while (true) {
            try {
                raceView.showGoalInitMsg()
                return raceModel.initGoal(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    suspend fun runRound(
        carList: List<Car>,
        goal: Int,
        scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    ) {
        raceView.showRoundResult()
        carList.map {
            scope.launch {
                while (isActive) {
                    ensureActive()
                    raceModel.runRound(it)
                    raceView.showCarStatus(it)
                    if (it.isFinished(goal)) {
                        scope.cancel()
                    }
                }
            }
        }.joinAll()
    }

    fun runAward(
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
