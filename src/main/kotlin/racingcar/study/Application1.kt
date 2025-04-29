package racingcar.study

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val channel = Channel<Int>()

    launch {
        for (x in 1..5) {
            channel.send(x)
        }
        channel.close()
    }

    for (y in channel) {
        println(y)
    }

    for (z in channel) {
        println(z)
    }
}