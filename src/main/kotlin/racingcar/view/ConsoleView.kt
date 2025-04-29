package racing.view

import racingcar.model.Car

class ConsoleView {
    fun printResults(winners: List<Car>) {
        println("최종 우승자 : ${winners.joinToString(", ") { it.name }}")
    }

    fun printRoundResult(cars: List<Car>) {
        cars.forEach { car ->
            println("${car.name}: ${"-".repeat(car.position)}")
        }
        println()
    }

    fun inputCarNames():  List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        val input1 = readLine()
        if (input1.isNullOrBlank()) {
            throw IllegalArgumentException("[ERROR] 입력이 없습니다.")
        }
        return input1.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    fun inputGoal(): Int {
        println("목표 거리를 입력하세요.")
        val input = readLine()
        if(input.isNullOrBlank()) {
            throw IllegalArgumentException("[ERROR] 입력이 없습니다.")
        }
        return input.toIntOrNull()
            ?: throw IllegalArgumentException("[ERROR] 목표거리는 정수로만 입력하세요.")
    }
}