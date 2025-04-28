import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// fun main() = runBlocking{
//    flow {
//        repeat(10) {
//            emit(it)
//            delay(700)
//            println("Emitted $it")
//        }
//    }.collect { value ->
//        delay(500)
//        println("Collected $value")
//    }

// }

fun main() =
    runBlocking {
        val channel = Channel<Int>()

        launch {
            for (x in 1..5) {
                channel.send(x)
                delay(500)
            }
            channel.close()
        }

        for (y in channel) {
            delay(1200)
            println(y)
        }
    }
