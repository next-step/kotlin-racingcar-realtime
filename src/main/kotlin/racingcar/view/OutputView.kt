package racingcar.view

import racingcar.model.RacingCar

class OutputView {

    // 경주의 상태를 출력
    fun printRaceStatus(racingCars: List<RacingCar>) {
        for (car in racingCars) {
            println("${car.name} : ${"-".repeat(car.getPosition())}")
        }
    }

    // 우승자 발표
    fun announceWinners(winners: List<RacingCar>) {
        val winnerNames = winners.joinToString(", ") { it.name }
        println("${winnerNames}가 최종 우승했습니다.")
    }
}
