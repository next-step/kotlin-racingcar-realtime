package model

enum class CommandType(val command: String) {
    ADD("add"),
    BOOST("boost"),
    SLOW("slow"),
    STOP("stop"),
    NONE(""),
    ;

    companion object {
        fun fromCommand(command: String): CommandType {
            return values().find { it.command.equals(command, ignoreCase = true) } ?: NONE
        }
    }
}
