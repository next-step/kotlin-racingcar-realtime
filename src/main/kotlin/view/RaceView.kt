package view

import entity.Car

class RaceView {
    fun showCarInitMsg() {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    }

    fun showGoalInitMsg() {
        println("목표 거리를 입력하세요.")
    }

    fun showRoundResult() {
        println("\n실행 결과")
    }

    fun showCarStatus(car: Car) {
        println(
            StringBuilder().apply {
                append(car.name)
                append(" : ")
                append("-".repeat(car.distance))
            },
        )
    }

    fun showWinners(winners: List<String>) {
        println(
            StringBuilder().apply {
                append(winners.joinToString(", "))
                append("가 최종 우승했습니다.")
            },
        )
    }

    fun showErrorMsg(msg: String) {
        println("[Error] $msg")
    }
}
