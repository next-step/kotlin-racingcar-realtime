package controller

import model.Car
import model.Race
import view.InputView

class RaceController {
    val inputView = InputView()

    suspend fun run() {
        val nameLists = inputView.getCarNames()
        val goalDistance = inputView.getGoalDistance()

        val carLists = nameLists.map(::Car)
        val race = Race(carLists, goalDistance)
        race.start()
    }
}
