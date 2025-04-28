package study

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() =
    runBlocking {
        val channel = Channel<Int>()

        launch {
            for (x in 1..5) {
                channel.send(x)
            }
            channel.close()
        }

        for (z in channel) { // `in` 절이 suspend function 임
            println("z : $z")
        }

        for (y in channel) { // 밑에 print 문은 출력되지 않음
            println("y: $y")
        }
    }
