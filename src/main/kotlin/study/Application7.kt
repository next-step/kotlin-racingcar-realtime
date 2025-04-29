package study

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

fun main() =
    runBlocking {
        val map = ConcurrentHashMap<String, Int>()
        val jobs =
            List(100) {
                launch {
                    repeat(100) {
                        map.compute("key") { _, v -> (v ?: 0) + 1 }
                    }
                }
            }
//        jobs.forEach { it.join() }
        jobs.joinAll() // delay 대신 사용 가능
        println(map["key"]) // 예상 결과: 10,000
    }
