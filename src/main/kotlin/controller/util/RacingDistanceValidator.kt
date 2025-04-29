package controller.util

object RacingDistanceValidator {
    fun validateAndReturnDistance(inputDistance: String): Int {
        return try {
            val distance = inputDistance.toInt()

            if (distance <= 0) throw IllegalStateException("시도할 횟수는 1 이상이어야 합니다.")

            distance
        } catch (exception: NumberFormatException) {
            throw NumberFormatException("시도할 횟수에 숫자가 아닌 값이 입력되었습니다.")
        }
    }
}