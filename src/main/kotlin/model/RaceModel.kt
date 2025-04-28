package model

import entity.Car

class RaceModel {
    var carList: List<Car> = emptyList()
    var round: Int = 0

    fun initCarList(input: String) {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
        carList = input.split(",").map {
            if (it.isEmpty()) throw IllegalArgumentException("입력된 이름이 없습니다.")
            if (it.length > 5) throw IllegalArgumentException("자동차 이름은 5자 이하만 가능합니다.")
            Car(it, 0)
        }
    }

    fun initRound(input: String) {
        if (input.isEmpty()) throw IllegalArgumentException("입력된 횟수가 없습니다.")
        try {
            round = input.toInt()
            if (round < 1) {
                throw IllegalArgumentException("1 이상의 숫자만 입력 가능합니다.")
            }
        } catch(_: NumberFormatException) {
            throw IllegalArgumentException("횟수는 숫자만 입력 가능합니다.")
        }
    }

    fun runRound() {
        carList.forEach {
            val random = (0..9).random()
            if (random >= 4) {
                it.move()
            }
        }
    }

    fun getWinners(): List<String> {
        val maxDist = carList.maxOfOrNull { it.distance } ?: throw IllegalStateException("자동차가 없습니다.")
        return carList.filter{ it.distance == maxDist }.map{ it.name }
    }
}