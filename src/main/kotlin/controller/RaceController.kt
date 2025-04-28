package controller

import model.RaceModel
import view.RaceView

class RaceController (
    val raceModel: RaceModel,
    val raceView: RaceView,
) {
    fun runGame() {
        initCarList()
        initRound()
        runRound()
        runAward()
    }

    private fun initCarList() {
        while (raceModel.carList.isEmpty()) {
            try {
                raceView.showCarInitMsg()
                raceModel.initCarList(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    private fun initRound() {
        while (raceModel.round < 1) {
            try {
                raceView.showRoundInitMsg()
                raceModel.initRound(readln())
            } catch (e: IllegalArgumentException) {
                handleError(e)
            }
        }
    }

    private fun runRound() {
        raceView.showRoundResult()
        repeat(raceModel.round) {
            raceModel.runRound()
            raceView.showEachRoundResult(raceModel.carList)
        }
    }

    private fun runAward() {
        val winners = raceModel.getWinners()
        raceView.showWinners(winners)
    }

    private fun handleError(e: Exception) {
        raceView.showErrorMsg(e.message.toString())
    }
}