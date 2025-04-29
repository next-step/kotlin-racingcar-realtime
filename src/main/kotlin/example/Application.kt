package example

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val carNames = InputUserInterrupt.carInitialize()
        val goal = InputUserInterrupt.goalInitialize()
        val cars = carNames.map(::Car)
        val channel = Channel<Car>(Channel.UNLIMITED)
        val race = Race(cars, goal, channel)
        race.start()
    }
}