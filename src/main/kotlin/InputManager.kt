import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class InputManager {
    val ioScope = CoroutineScope(Dispatchers.IO)
    lateinit var iCheckingCar: checkingInterface

    fun initCar(): List<String> {
        var ret: List<String> = listOf()
        var success = false
        while (!success) {
            try {
                ret = inputCar()
                success = true
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
        return ret
    }

    fun initLoopCount(): Int {
        var ret = 0
        var success = false
        while (!success) {
            try {
                ret = inputLoopCount()
                success = true
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
        return ret
    }

    fun runInputScope() {
        ioScope.launch {
            realInput()
        }
    }

    fun realInput() {
        while (true) {
            val input = readLine()
            if (input != null && input == "") {
                println("(사용자 엔터 입력)")
                runBlocking {
                    msgChannel.send(DefMessage(DefMessage.MessageID.LoopStop, ""))
                }
                inputAddCar()
            }
        }
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
        if (stringList.size != 2 && input != "") {
            throw IllegalArgumentException("[ERROR] 입력이 잘못되었습니다. add {차량이름}, boost {차량이름}, slow {차량이름}, stop {차량이름}")
        }
        runBlocking {
            when (stringList[0]) {
                "add" -> {
                    if (iCheckingCar.findCar(stringList[1])) {
                        throw IllegalArgumentException("[ERROR] 해당 자동차가 이미 존재합니다.")
                    } else if (stringList[1].length > 5) {
                        throw IllegalArgumentException("[ERROR] 자동차 이름을 5글자 이하로 입력해주세요.")
                    }
                    msgChannel.send(DefMessage(DefMessage.MessageID.Add, stringList[1]))
                }
                "boost" -> {
                    if (!iCheckingCar.findCar(stringList[1])) {
                        throw IllegalArgumentException("[ERROR] 해당 자동차가 존재하지 않습니다.")
                    }
                    msgChannel.send(DefMessage(DefMessage.MessageID.Boost, stringList[1]))
                }
                "slow" -> {
                    if (!iCheckingCar.findCar(stringList[1])) {
                        throw IllegalArgumentException("[ERROR] 해당 자동차가 존재하지 않습니다.")
                    }
                    msgChannel.send(DefMessage(DefMessage.MessageID.Slow, stringList[1]))
                }
                "stop" -> {
                    if (!iCheckingCar.findCar(stringList[1])) {
                        throw IllegalArgumentException("[ERROR] 해당 자동차가 존재하지 않습니다.")
                    }
                    msgChannel.send(DefMessage(DefMessage.MessageID.Stop, stringList[1]))
                }
                "" -> msgChannel.send(DefMessage(DefMessage.MessageID.LoopStart, ""))
                else -> throw IllegalArgumentException("[ERROR] 입력이 잘못되었습니다. add {차량이름}, boost {차량이름}, slow {차량이름}, stop {차량이름}")
            }
        }
    }

    fun inputCar(): List<String> {
        println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")
        return readLine()?.split(",") ?: throw IllegalArgumentException("[ERROR] 입력을 넣어주세요")
    }

    fun inputLoopCount(): Int {
        println("목표 거리를 입력하세요.")
        try {
            return readLine()?.toInt() ?: throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
        } catch (_: NumberFormatException) {
            throw IllegalArgumentException("[ERROR] 숫자로 입력해 주세요")
        }
    }
}
