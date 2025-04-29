package view

import java.util.Scanner

class InputView {
    private val scanner = Scanner(System.`in`)

    fun getCarNames(): List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        val input = scanner.nextLine()

        return input.split(",")
    }

    fun getGoalDistance(): Int {
        println("목표 거리를 입력하세요.")
        val input = scanner.nextInt()

        return input
    }
}
