package study

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    runBlocking {
        val job =
            launch {
//                while (isActive) {
                while (true) {
                    delay(100.milliseconds)
                    println("$isActive")
//                    ensureActive()
                    yield() // 제어권을 넘겨줄지 말지 파악하는 로직이 들어있음
                }
            }
        delay(1.seconds)
        job.cancel()
    }
}
