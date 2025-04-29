package service

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import model.Car
import org.junit.jupiter.api.Test

class RealtimeRaceTest {
    @Test
    fun race() = runTest {
        // given
        val testScope = CoroutineScope(StandardTestDispatcher(testScheduler) + SupervisorJob())
        val testChannel = Channel<Car>(Channel.UNLIMITED)
        val realtimeRace = RealtimeRace(testScope, testChannel, usingPause = false)
        val cars = listOf(
            Car("car1", 0, false),
            Car("car2", 0, false),
            Car("car3", 9, false),
        )

        // when
        realtimeRace.start(cars, 10)

        // then
        realtimeRace.cars.find { it.name == "car1" }?.isWinner shouldBe false
        realtimeRace.cars.find { it.name == "car2" }?.isWinner shouldBe false
        realtimeRace.cars.find { it.name == "car3" }?.isWinner shouldBe true
    }

    @Test
    fun addCar() = runTest {
        // given
        val testScope = CoroutineScope(StandardTestDispatcher(testScheduler) + SupervisorJob())
        val testChannel = Channel<Car>(Channel.UNLIMITED)
        val readInputChannel = Channel<String>(Channel.UNLIMITED)
        readInputChannel.send("add car4")
        val readInput: suspend () -> String = { readInputChannel.receive() }
        val realtimeRace = RealtimeRace(testScope, testChannel, readInput, true)
        val cars = listOf(
            Car("car1", 0, false),
            Car("car2", 0, false),
            Car("car3", 0, false),
        )

        // when
        realtimeRace.start(cars, 3)

        // then
        realtimeRace.cars.find { it.name == "car4" }?.name shouldBe "car4"
    }
}