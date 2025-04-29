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
        println("${car.name} : ${"-".repeat(car.distance)}")
    }

    fun addCarMsg(name: String) {
        println("$name 참가 완료!\n")
    }

    fun boostCarMsg(name: String) {
        println("$name 속도 2배 증가!\n")
    }

    fun slowCarMsg(name: String) {
        println("$name 속도 2배 감소!\n")
    }

    fun stopCarMsg(name: String) {
        println("$name 정지!\n")
    }

    fun showWinner(winner: Car) {
        println("\n${winner.name}가 최종 우승했습니다.")
    }

    fun showErrorMsg(msg: String) {
        println("[Error] $msg")
    }
}
