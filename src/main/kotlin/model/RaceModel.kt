package model

import entity.Car

class RaceModel {
    fun initCarList(input: String): List<Car> {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
        return input.split(",").map {
            if (it.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
            if (it.length > 5) throw IllegalArgumentException("자동차 이름은 5자 이하만 가능합니다.")
            Car(it, 0)
        }
    }

    fun initGoal(input: String): Int {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 횟수가 없습니다.")
        try {
            val goal = input.toInt()
            if (goal < 1) {
                throw IllegalArgumentException("1 이상의 숫자만 입력 가능합니다.")
            }
            return goal
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("횟수는 숫자만 입력 가능합니다.")
        }
    }

    fun runRound(car: Car): Car {
        val random = (0..9).random()
        if (random >= 4) {
            car.move()
        }
        return car
    }

    fun getWinners(carList: List<Car>): List<String> {
        val maxDist = carList.maxOfOrNull { it.distance } ?: throw IllegalStateException("자동차가 없습니다.")
        return carList.filter { it.distance == maxDist }.map { it.name }
    }
}
