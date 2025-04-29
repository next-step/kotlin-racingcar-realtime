import com.kmc.Car
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val addChannel = Channel<String>()
val boostChannel = Channel<String>()
val slowChannel = Channel<String>()
val stopChannel = Channel<String>()

var restart = false

fun main() {
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

    CoroutineScope(Dispatchers.Default).launch {
        realInput()
    }

    runBlocking {
        launch {
            for (name in addChannel) {
                synchronized(Car) {
                    Car.addCar(name)
                    restart = true
                }
            }
        }
        launch {
            for (name in boostChannel) {
                synchronized(Car) {
                    Car.boostCar(name)
                    restart = true
                }
            }
        }
        launch {
            for (name in slowChannel) {
                synchronized(Car) {
                    Car.slowCar(name)
                    restart = true
                }
            }
        }
        launch {
            for (name in stopChannel) {
                synchronized(Car) {
                    Car.stopCar(name)
                    restart = true
                }
            }
        }
        runRace(loopCount)
    }
}

suspend fun runRace(loopCount: Int) {
    CoroutineScope(Dispatchers.Default).launch {
        Race.loopCount = loopCount
        Race.start()
        while (!Race.raceDone.get()) {
            if (restart) {
                synchronized(Car) {
                    Race.reStart()
                    restart = false
                }
            }
        }
        Car.printWinner()
        addChannel.close()
        boostChannel.close()
        slowChannel.close()
        stopChannel.close()
    }.join()
}

fun realInput() {
    while (!Race.raceDone.get()) {
        val input = readLine()
        println("(사용자 엔터 입력)")
        if (input != null && input == "" && !Race.raceDone.get()) {
            Race.cancelAllJob()
            inputAddCar()
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

fun inputAddCar() {
    var success = false
    while (!success) {
        try {
            parseInput(readLine()!!)
            success = true
        } catch (e: Exception) {
            println(e.message)
        }
    }
}

fun parseInput(input: String) {
    val stringList = input.split(" ")
    if (stringList.size < 2 && input != "") {
        throw IllegalArgumentException("[ERROR] 입력이 잘못되었습니다. add {차량이름}, boost {차량이름}, slow {차량이름}, stop {차량이름}")
    }
    runBlocking {
        when (stringList[0]) {
            "add" -> addChannel.send(stringList[1])
            "boost" -> boostChannel.send(stringList[1])
            "slow" -> slowChannel.send(stringList[1])
            "stop" -> stopChannel.send(stringList[1])
            "" -> restart = true
            else -> throw IllegalArgumentException("[ERROR] 입력이 잘못되었습니다. add {차량이름}, boost {차량이름}, slow {차량이름}, stop {차량이름}")
        }
    }
}

fun inputLoopCount(): Int {
    println("목표 거리를 입력하세요.")
    try {
        return readLine()?.toInt() ?: throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
    } catch (_: NumberFormatException) {
        throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
    }
}
