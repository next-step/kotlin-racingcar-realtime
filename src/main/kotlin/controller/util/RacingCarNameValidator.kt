package controller.util

object RacingCarNameValidator {
    fun isValidateRacingCarName(inputRacingCarNames: String): Boolean {
        val racingCars = inputRacingCarNames.split(",").map { it.trim() }

        return when {
            racingCars.any { it.isEmpty() } -> throw IllegalStateException("경주용 차량 이름에 빈 문자열이 입력되었습니다.")
            racingCars.any { it.length > 5 } -> throw IllegalStateException("경주용 차량 이름은 5자 이하여야 합니다.")
            else -> true
        }
    }
}