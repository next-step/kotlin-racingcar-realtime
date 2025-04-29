
// fun main() = runBlocking{
//    flow {
//        repeat(10) {
//            emit(it)
//            delay(700)
//            println("Emitted $it")
//        }
//    }.collect { value ->
//        delay(500)
//        println("Collected $value")
//    }

// }

// fun main() =
//    runBlocking {
//        val channel = Channel<Int>()
//
//        val testJob = launch {
//            while (true) {
//                //test
//                delay(100)
//            }
//        }
//        testJob.join()
//
//        launch {
//            for (x in 1..5) {
//                channel.send(x)
//                delay(500)
//            }
//            channel.close()
//        }
//        testJob.join()
//
//        for (y in channel) {
//            delay(1200)
//            println(y)
//        }
//
//        val testThread = Thread {
//            while (true) {
//                Thread.sleep(1)
//            }
//        }
//        testThread.start()
//    }

fun main() {
    var testInt = 99
    val testList = mutableListOf(1, 2, 3, 4, 5, 6, testInt)
    val copyList = testList.toMutableList()
    Thread {
        while (true) {
            for (i in copyList) {
                println(i)
                Thread.sleep(10)
            }
        }
    }.start()

    Thread {
        while (true) {
            if (testInt == 10) {
                testInt = 11
            } else {
                testInt = 10
            }
            Thread.sleep(10)
        }
    }.start()
}
