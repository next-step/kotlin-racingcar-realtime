package study

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import study.domain.Car
import study.domain.Race
import study.domain.Requirement
import study.view.InputHandler

fun main() {
    runBlocking {
        val names = InputHandler.readCarNames()
        val goal = InputHandler.readGoal()
        val cars = names.map(::Car)
        val channelRequirement: Channel<Requirement> = Channel()
        val race = Race(cars, goal, channelRequirement)

        val requirementScope = CoroutineScope(Dispatchers.IO)
        requirementScope.launch {
            while (isActive) {
                InputHandler.readAnySignal()
                race.pauseRace()

                val requirement = InputHandler.readRequirement()
                channelRequirement.send(requirement)
                race.resumeRace()
            }
        }

        race.start()
        requirementScope.cancel()
    }
}
