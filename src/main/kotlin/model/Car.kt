package model

data class Car(val name: String, var positon: Int) {
    fun move() {
        positon++
    }
}