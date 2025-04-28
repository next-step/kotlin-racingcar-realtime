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
        val sb = StringBuilder()
        sb.append(car.name)
        sb.append(" : ")
        repeat(car.distance) {
            sb.append("-")
        }
        println(sb)
    }

    fun showWinners(winners: List<String>) {
        val sb = StringBuilder()
        sb.append(winners.joinToString(", "))
        sb.append("가 최종 우승했습니다.")
        println(sb)
    }

    fun showErrorMsg(msg: String) {
        println("[Error] $msg")
    }
}
