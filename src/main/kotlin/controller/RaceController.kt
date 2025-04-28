package controller

import entity.Car
import model.RaceModel
import view.RaceView

class RaceController(
    val raceModel: RaceModel,
    val raceView: RaceView,
) {
    fun runGame() {
        val carList = initCarList()
        val goal = initGoal()
        runRound(carList, goal)
        runAward(carList)
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
                raceView.showRoundInitMsg()
                return raceModel.initGoal(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    private fun runRound(carList: List<Car>, goal: Int) {
        raceView.showRoundResult()
        repeat(goal) {
            carList.forEach {
                raceModel.runRound(it)
            }
            raceView.showEachRoundResult(carList)
        }
    }

    private fun runAward(carList: List<Car>) {
        val winners = raceModel.getWinners(carList)
        raceView.showWinners(winners)
    }

    private fun handleError(e: Exception) {
        raceView.showErrorMsg(e.message.toString())
    }
}
