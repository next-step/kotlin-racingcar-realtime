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

    fun isValidateAddRacingCarName(inputRacingCarNames: String): Boolean {
        val racingCars = if (inputRacingCarNames.length < 4) "" else inputRacingCarNames.substring(3)

        return when {
            !inputRacingCarNames.startsWith("add ") -> throw IllegalStateException("추가할 경주용 차량 이름은 add 로 시작해야 합니다.")
            //racingCars.any { it.isEmpty() } -> throw IllegalStateException("경주용 차량 이름에 빈 문자열이 입력되었습니다.")
            inputRacingCarNames.isEmpty() -> throw IllegalStateException("경주용 차량 이름에 빈 문자열이 입력되었습니다.")
            racingCars.isEmpty() -> throw IllegalStateException("경주용 차량 이름에 빈 문자열이 입력되었습니다.")
            racingCars.length > 5 -> throw IllegalStateException("경주용 차량 이름은 5자 이하여야 합니다.")
            else -> true
        }
    }
}