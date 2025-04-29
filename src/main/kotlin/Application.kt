import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

val msgChannel = Channel<DefMessage>()

fun main() {
    runBlocking {
        Controller().startMainLoop()
    }
}
