package study

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun main() {
    runBlocking {
        val quickResult =
//            withTimeout(500.milliseconds) { // 예외처리를 별도로 해줘야함, OrNull 을 쓰는게 더 좋다
            withTimeoutOrNull(500.milliseconds) {
                calculateSomething()
            }
        println(quickResult) // 타임아웃 발생 시 null 반환
    }
}

suspend fun calculateSomething(): Int {
    delay(3.seconds)
    return 2 + 2
}
