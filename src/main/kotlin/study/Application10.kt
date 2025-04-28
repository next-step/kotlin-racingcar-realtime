package study

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun main() =
    runBlocking {
        flow {
            repeat(10) {
                println("Emitted $it")
                emit(it)
            }
        }.collect { value ->
            delay(500)
            println("Collected $value")
        }
    }
