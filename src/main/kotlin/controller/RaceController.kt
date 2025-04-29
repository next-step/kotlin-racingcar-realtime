package controller

import model.Car
import model.Race
import view.InputView

class RaceController {
    val inputView: InputView = InputView()

    suspend fun run() {
        // inputView로 구현
        val names = inputView.getCarNames()
        val cars = names.map(::Car)
        val goal = inputView.getGoal()

        val race = Race(cars, goal)

        // 경기 시작
        race.start()
    }
}
