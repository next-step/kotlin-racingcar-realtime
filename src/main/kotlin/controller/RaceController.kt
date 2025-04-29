package controller

import kotlinx.coroutines.runBlocking
import model.Car
import model.Race
import view.InputView
import kotlin.collections.map

class RaceController {
    val inputView = InputView()

    fun run() =
        runBlocking {
            val nameLists = inputView.getCarNames()
            val goalDistance = inputView.getGoalDistance()

            val carLists = nameLists.map(::Car)
            val race = Race(carLists, goalDistance)

            race.start()
        }
}
