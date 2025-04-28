package racingcar.view

import racingcar.model.RacingCar

class OutputView {
    // 우승자 발표
    fun announceWinners(winners: List<RacingCar>) {
        val winnerNames = winners.joinToString(", ") { it.name }
        println("${winnerNames}가 최종 우승했습니다.")
    }
}
