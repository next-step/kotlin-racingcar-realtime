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

    fun getUserInputEnter() {
        // 무한 루프에서 사용자의 입력을 기다림
        while (true) {
            val input = readLine()

            // 엔터만 입력되었을 때
            if (input.isNullOrEmpty()) {
                println("엔터를 눌렀습니다! 특정 행동을 수행합니다.")
                break // 원하는 행동을 수행한 후 종료
            } else {
                println("입력값: $input")
            }
        }
    }
}
