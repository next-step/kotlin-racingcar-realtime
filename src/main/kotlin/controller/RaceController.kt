package controller

import kotlinx.coroutines.runBlocking
import model.Car
import model.Race
import view.InputView

class RaceController {
    val inputView = InputView()

    fun run() =
        runBlocking {
            val nameLists = inputView.getCarNames()
            val goalDistance = inputView.getGoalDistance()

            val carLists = nameLists.map(::Car)
            val race = Race(carLists, goalDistance)

//            launch(Dispatchers.IO) {
//                while (true) {
//                    val input = readLine()
//
//                    // 엔터만 입력되었을 때
//                    if (input.isNullOrEmpty()) {
//                        println("(사용자 엔터 입력)")
//                    } else {
//                        println("입력값: $input")
//                    }
//                }
//            }
            race.start()
        }
}
