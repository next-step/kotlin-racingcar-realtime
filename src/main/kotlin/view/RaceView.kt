package view

import entity.Car

class RaceView {
    fun showCarInitMsg() {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    }

    fun showRoundInitMsg() {
        println("시도할 회수는 몇회인가요?")
    }

    fun showRoundResult() {
        println("\n실행 결과")
    }

    fun showEachRoundResult(carList: List<Car>) {
        carList.forEach {
            val sb = StringBuilder()
            sb.append(it.name)
            sb.append(" : ")
            repeat(it.distance) {
                sb.append("-")
            }
            println(sb)
        }
        println()
    }

    fun showWinners(winners: List<String>) {
        val sb = StringBuilder()
        sb.append("최종 우승자 : ")
        sb.append(winners.joinToString(", "))
        println(sb)
    }

    fun showErrorMsg(msg: String) {
        println("[Error] $msg")
    }
}
