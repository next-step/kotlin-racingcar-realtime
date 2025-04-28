package view

import model.Car

object InputRacingCar {
    fun getCars(): List<Car> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        val input = readln()
        val carsName = input.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
        return carsName.map { Car(it, 0) }
    }

    fun getDistance(): Int {
        println("목표 거리를 입력하세요.")
        return readln().toInt()
    }
}