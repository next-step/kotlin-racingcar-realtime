package racingcar.view

import racingcar.model.Car

class RacingView {
    fun nameInputView(): List<String> {
        while (true) {
            try {
                println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
                val names = readln()
                return names.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            } catch (e: IllegalArgumentException) {
                println("[ERROR] ${e.message}")
            } catch (e: IllegalStateException) {
                println("[ERROR] ${e.message}")
            }
        }
    }

    fun distanceInputView(): Int {
        while (true) {
            try {
                println("목표 거리를 입력하세요.")
                val distance = readln().toInt()
                if (distance <= 0) {
                    throw IllegalArgumentException("시도 횟수는 양수만 가능")
                }
                return distance
            } catch (e: IllegalArgumentException) {
                println("[ERROR] ${e.message}")
            } catch (e: IllegalStateException) {
                println("[ERROR] ${e.message}")
            }
        }
    }

    fun positionView(car: Car) = println(car)

    fun resultView(car: Car) = println("${car.name}가 최종 우승했습니다.")
}
