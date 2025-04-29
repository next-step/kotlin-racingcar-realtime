package study

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        launch {
            buildList {
                add(1)
                add(2)
                delay(1000)
                add(3)
                add(4)
                delay(1000)
                add(5)
            }
        }
    }
}
