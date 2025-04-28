import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun simpleFlow(): Flow<Int> =
    flow {
        for (i in 1..3) {
            delay(100)
            emit(i)
        }
    }

fun main() =
    runBlocking {
        val channel = Channel<Int>()

        launch {
            for (x in 1..5) {
                println("SEND!")
                channel.send(x)
            }
            channel.close()
        }

        for (y in channel) {
            println("FOR 문")
            println(y)
        }
    }
