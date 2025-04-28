package model

data class Car(val name: String, var position: Int) {
    fun move() {
        position++
    }

    fun printCurrentPosition() {
        println("${name} : ${"-".repeat(position)}")
    }

    fun printWinner() {
        println()
        println("최종 우승자 : ${name}")
    }
}