package study

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds

fun main() {
    runBlocking {
        val mutex = Mutex()
        var x = 0
        launch(Dispatchers.Default) {
            repeat(10_000) {
                launch {
                    mutex.withLock {
                        x++
                    }
                }
            }
        }
        delay(1.seconds)
        println(x) // 항상 10,000 출력
    }
}
