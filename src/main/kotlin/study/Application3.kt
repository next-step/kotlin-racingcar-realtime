package study

import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    runBlocking {
        withTimeoutOrNull(2.seconds) {
            while (true) {
                try {
                    doWork()
//                } catch (e: Exception) { // 코루틴이 제대로 종료되지 않음, CancellationException 도 잡아버려서 코루틴이 종료가되지 않음
                } catch (e: UnsupportedOperationException) {
                    println("Oops: ${e.message}")
                }
            }
        }
    }
}

suspend fun doWork(): Int {
    println("${Thread.currentThread().name} : ${coroutineContext.job}")
    delay(500.milliseconds)
    throw UnsupportedOperationException("Didn't work!")
}
