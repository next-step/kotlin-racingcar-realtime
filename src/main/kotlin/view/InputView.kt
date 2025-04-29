package view

import java.util.Scanner

class InputView {
    val scanner: Scanner = Scanner(System.`in`)

    fun getCarNames(): List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        val names = scanner.nextLine()
        return names.split(',')
    }

    fun getGoal(): Int {
        println("목표 거리를 입력하세요.")
        val goal = scanner.nextInt()
        return goal
    }
}
