package racingcar.view

import racingcar.support.InputValidator

class InputView {

    // 사용자로부터 자동차 이름을 입력받고 유효성 검증
    fun getCarNames(): List<String> {
        while (true) {
            println("경주할 자동차 이름을 입력하세요. (이름은 쉼표(,)로 구분)")
            val input = readlnOrNull()

            try {
                return InputValidator.validateCarNames(input)
            } catch (e: IllegalArgumentException) {
                println(e.message)
            } catch (e: IllegalStateException) {
                println(e.message)
            }
        }
    }

    // 사용자로부터 목표 거리를 입력받고 유효성 검증
    fun getRoundCount(): Int {
        while (true) {
            println("목표 거리를 입력하세요.")
            val input = readlnOrNull()

            try {
                return InputValidator.validateGoalDistance(input)
            } catch (e: IllegalArgumentException) {
                println(e.message)
            } catch (e: IllegalStateException) {
                println(e.message)
            }
        }
    }
}
