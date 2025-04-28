package study

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

fun main() {
//    runBlocking {
//        launch(Dispatchers.Default) {
//            var x = 0
//            repeat(10_000) {
//                x++
//            }
//            println(x) //  항상 10,000이 출력됨
//        }
//    }
//    runBlocking {
//        launch {
//            var x = 0
//            repeat(10_000) {
//                launch { x++ }
//            }
//            println(x) // 0이 출력됨, 메인쓰레드가 내부 코루틴 에 제어권을 넘겨주지 않고 끝나버림
//        }
//    }
    runBlocking {
//        launch {
        // 쓰레드 수를 1개로 줄이면 해결가능, 그렇지만 blocking 되면 다른 코드도 다 중단됨
        launch(Dispatchers.Default) {
//            var x = 0
            val x = AtomicInteger(0)
            repeat(10_000) {
                // 여러개 쓰레드가 있는 상황에서 race condition 을 유발
//                println("launch: ${Thread.currentThread().name}")
                launch {
//                    x++
                    x.incrementAndGet()
                }
            }
//            println("main: ${Thread.currentThread().name}")
            delay(1.seconds)
            println(x) // 10,000 보다 적은 숫자가 실행할때마다 달라짐, 10,000개의 코루틴이 도는시간보다 출력문이 먼저 실행됨
        }
    }
}
