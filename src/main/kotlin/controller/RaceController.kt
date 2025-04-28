package controller

import Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import view.InputView

class raceController {
    val inputView = InputView()
    suspend fun run() {

        val carLists = inputView.getCarNames()

        val goalDistance = inputView.getGoalDistance()

        carLists.forEach {
            Car(it, goalDistance)
        } //



            CoroutineScope.launch{

            }


    }


}