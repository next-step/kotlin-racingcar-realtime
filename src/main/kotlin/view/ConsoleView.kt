package view

object ConsoleView {
    const val ERROR_TAG = "[ERROR]"

    fun tryInputCarName(): List<String> {
        val list = ArrayList<String>()
        while (true) {
            try {
                val inputCars = inputCarName()

                for (name in inputCars) {
                    if (name.length <= 5) {
                        list.add(name)
                    } else {
                        throw IllegalArgumentException("각 자동차 이름은 5자 이하만 가능합니다.")
                    }
                }
                break
            } catch (e: Exception) {
                println("$ERROR_TAG ${e.message}")
            }
        }
        return list
    }

    private fun inputCarName(): List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        val input = readLine()
        if (input?.isBlank() == true) {
            throw IllegalArgumentException("유효한 값을 입력 하십시오.")
        }

        return input!!.split(",")
            .map { it.trim() }
    }

    fun tryInputGoalDistance(): Int {
        while (true) {
            try {
                return inputGoalDistance()
            } catch (e: Exception) {
                println("$ERROR_TAG ${e.message}")
            }
        }
    }

    private fun inputGoalDistance(): Int {
        println("목표 거리를 입력하세요.")
        val count = readLine()
        return count?.toIntOrNull() ?: throw IllegalArgumentException("숫자만 입력 가능합니다.")
    }
}
