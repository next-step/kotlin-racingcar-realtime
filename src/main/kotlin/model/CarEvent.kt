package model

sealed class CarEvent {
    data class Add(val car: Car) : CarEvent()
    data class Boost(val car: Car) : CarEvent()
}