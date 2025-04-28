import com.kmc.Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// lateinit var inputJob: Job
val channel = Channel<String>()

lateinit var inputTherad: Thread

fun main() =
    runBlocking {
        var loopCount = 0
        var success = false
        while (!success) {
            try {
                val inputList = inputCar()
                makeCar(inputList)
                success = true
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
        success = false
        while (!success) {
            try {
                loopCount = inputLoopCount()
                success = true
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }

//    inputJob = launch {
//        realInput()
//    }

        inputTherad =
            Thread {
                realInput()
            }

        launch {
            for (name in channel) {
                Car.addCar(name)
                Race.reStart()
            }
        }
        asyncInput(loopCount)
    }

suspend fun asyncInput(loopCount: Int) {
    CoroutineScope(Dispatchers.Default).launch {
        Race.loopCount = loopCount
        Race.start()
        while (!Race.raceDone) {
            delay(1)
        }
        Car.printWinner()
//        inputJob.cancel()
        inputTherad.interrupt()
        channel.close()
    }.join()
}

fun realInput() {
    while (!Race.raceDone) {
//        delay(1)
        val input = readLine()
        println("(사용자 엔터 입력)")
//        delay(1)
        if (input != null) {
            Race.cancelAllJob()
            runBlocking {
                channel.send(inputAddCar())
            }
        }
    }
}

fun makeCar(carList: List<String>) {
    Car.clearCar()
    Car.createCar(carList)
}

fun inputCar(): List<String> {
    println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
    return readLine()?.split(",") ?: throw IllegalArgumentException("[ERROR] 입력을 넣어주세요")
}

fun inputAddCar(): String {
    var ret = ""
    try {
        ret = readLine()!!.split("add ")[1]
    } catch (e: Exception) {
        throw IllegalArgumentException("[ERROR] 입력이 잘못되었습니다.")
    }
    return ret
}

fun inputLoopCount(): Int {
    println("목표 거리를 입력하세요.")
    try {
        return readLine()?.toInt() ?: throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
    } catch (_: NumberFormatException) {
        throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
    }
}
