package controller

import model.Car
import model.Race

class RaceController {
    fun run() {
        // inputView로 구현
        val car = listOf(Car("car1", 0), Car("car2", 0))
        val race = Race(car)

        // 경기 시작
        race.start()
    }
}
