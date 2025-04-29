package study.view

import study.domain.Contest.Requirement

object InputHandler {
    fun readAnySignal() {
        readlnOrNull()
    }

    fun readRequirement(): Requirement {
        val input = readlnOrNull() ?: ""
        val parts = input.split(" ", limit = 2) // 최대 2개의 부분으로 분리 (명령어, 나머지)
        val command = parts.getOrNull(0) ?: ""
        val name = parts.getOrNull(1) ?: ""
        return Requirement(command, name)
    }

    fun readCarNames(): List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        val input = readlnOrNull() ?: ""
        return input.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun readGoal(): Int {
        println("목표 거리를 입력하세요.")
        while (true) {
            val input = readlnOrNull()
            input?.toIntOrNull()?.let { goal ->
                if (goal > 0) {
                    return goal
                } else {
                    println("목표 거리는 0보다 큰 숫자로 입력해주세요.")
                }
            } ?: run {
                println("유효한 숫자를 입력해주세요.")
            }
        }
    }
}
