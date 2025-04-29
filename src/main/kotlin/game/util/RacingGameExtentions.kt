package game.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun printlnWithTime(message: Any? = "") {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")
    val timePrefix = "[${now.format(formatter)}]"
    val threadNamePrefix = "[${Thread.currentThread().name}]"

    println("$timePrefix $threadNamePrefix $message")
}
