package study

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.time.Duration.Companion.milliseconds

fun main() =
    runBlocking {
        val semaphore = Semaphore(3) // 동시에 3개까지만 허용
        repeat(10) {
            launch {
                semaphore.withPermit {
                    println("Running $it")
                    delay(500.milliseconds)
                }
            }
        }
    }
