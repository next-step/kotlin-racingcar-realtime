package racingcar.support

object InputValidator {

    // 자동차 이름 유효성 검증
    fun validateCarNames(input: String?): List<String> {
        if (input.isNullOrBlank()) {
            throw IllegalArgumentException("[ERROR] 이름 입력이 필요합니다.")
        }

        val carNames = input.split(",").map { it.trim() }

        if (carNames.isEmpty() || carNames.any { it.isBlank() }) {
            throw IllegalArgumentException("[ERROR] 최소 1개의 자동차 이름을 입력해야 합니다.")
        }

        if (carNames.any { it.length > 5 }) {
            throw IllegalArgumentException("[ERROR] 자동차 이름은 5자 이하만 가능합니다.")
        }

        return carNames
    }

    // 목표 거리 유효성 검증
    fun validateGoalDistance(input: String?): Int {
        if (input.isNullOrBlank()) {
            throw IllegalArgumentException("[ERROR] 목표 거리 입력이 필요합니다.")
        }

        // 숫자로 변환이 안 될 경우 예외 던지기
        val goalDistance = input.toIntOrNull()
            ?: throw IllegalStateException("[ERROR] 숫자를 입력해야 합니다.")

        // 0 이하의 값이 들어올 경우 예외 던지기
        if (goalDistance <= 0) {
            throw IllegalArgumentException("[ERROR] 목표 거리는 1 이상이어야 합니다.")
        }

        return goalDistance
    }
}
