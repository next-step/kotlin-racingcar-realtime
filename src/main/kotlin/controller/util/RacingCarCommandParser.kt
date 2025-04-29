package controller.util

object RacingCarCommandParser {
    fun parseCommand(commandString: String): CommandEvent {
        val commands = commandString.split(" ")

        return if (commands.size == 2) {
            when (commands[0]) {
                "add" -> CommandEvent.Add(addCarName = commands[1])
                "stop" -> CommandEvent.Stop(stopCarName = commands[1])
                else -> CommandEvent.Error
            }
        } else {
            CommandEvent.Error
        }
    }
}

sealed class CommandEvent {
    data class Add(val addCarName: String = "") : CommandEvent()
    data class Stop(val stopCarName: String = "") : CommandEvent()
    object Error : CommandEvent()
}