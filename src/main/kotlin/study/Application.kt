package study

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import study.domain.Car
import study.domain.Command
import study.service.Race
import study.view.InputView
import kotlin.collections.map

fun main() {
    runBlocking {
        val cars = InputView.readCars().map(::Car)
        val goal = InputView.readGoal()

        val channel = Channel<Command>()
        val race = Race(cars, goal, channel)
        val commandScope = listenCommand(race, channel)

        race.startRace()
        commandScope.cancel()
    }
}

private fun listenCommand(
    race: Race,
    channel: Channel<Command>,
): CoroutineScope =
    CoroutineScope(Dispatchers.IO).apply {
        this.launch {
            while (isActive) {
                InputView.readyCommand()
                race.pauseRace()
                val (command, name) = InputView.readCommand()
                channel.send(Command(command, name))
                race.resumeRace()
            }
        }
    }
