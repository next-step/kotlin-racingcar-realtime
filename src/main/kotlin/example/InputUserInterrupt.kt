package example

class InputUserInterrupt {
    companion object {
        fun carInitialize(): List<String> {
            var carList: List<String> = emptyList()
            println("경주할 자동차 이름을 입력하세요.(이름은 쉼표(,) 기준으로 구분)")

            while(carList.isEmpty()) {
                val input = readln()
                try {
                    carList = input.split(",").map { it.trim() }
                    if (carList.any { it.isEmpty() }) {
                        throw IllegalStateException("경주용 차량 이름에 빈 문자열이 입력되었습니다.")
                    }
                    if (carList.any { it.length > 5 }) {
                        throw IllegalStateException("경주용 차량 이름은 5자 이하여야 합니다.")
                    }
                } catch (e: IllegalStateException) {
                    println("${e.message}")
                    carList = emptyList()
                }
            }

            println("$carList")
            return carList
        }

        fun goalInitialize(): Int {
            var goal = 0
            println("목표 거리를 입력하세요.")

            while(0 == goal) {
                val input = readln()
                try {
                    goal = input.toInt()
                    if (goal <= 0) throw IllegalStateException("시도할 횟수는 1 이상이어야 합니다.")
                } catch (e: IllegalStateException) {
                    println("${e.message}")
                    goal = 0
                } catch (e: NumberFormatException){
                    println("시도할 횟수에 숫자가 아닌 값이 입력되었습니다.")
                    goal = 0
                }
            }

            return goal
        }

        fun command(): Pair<CarState, String> {
            var command: Pair<CarState, String> = Pair(CarState.NORMAL, "")
            println("(사용자 엔터 입력)")

            while(command == Pair(CarState.NORMAL, "")) {
                val input = readln()
                if(input == ""){
                    break
                }

                try {
                    var cmdCarStatus = CarState.NORMAL
                    var cmdCarName = ""// = if (input.length < 4) "" else input.substring(4)

                    if (input.startsWith("add ")) {
                        cmdCarStatus = CarState.ADDED
                        cmdCarName = input.substring(4)
                    } else if (input.startsWith("boost ")) {
                        cmdCarStatus = CarState.BOOST
                        cmdCarName = input.substring(6)
                    } else if (input.startsWith("slow ")) {
                        cmdCarStatus = CarState.SLOW
                        cmdCarName = input.substring(5)
                    } else if (input.startsWith("stop ")) {
                        cmdCarStatus = CarState.STOP
                        cmdCarName = input.substring(5)
                    } else {
                        throw IllegalStateException("사용 가능한 명령어 [add 자동차이름], [boost 자동차이름], [slow 자동차이름], [stop 자동차이름]")
                    }

                    if ("" == cmdCarName) {
                        throw IllegalStateException("경주용 차량 이름에 빈 문자열이 입력되었습니다.")
                    }

                    if (5 < cmdCarName.length) {
                        throw IllegalStateException("경주용 차량 이름은 5자 이하여야 합니다.")
                    }

                    println("command: $cmdCarStatus, car name: $cmdCarName")
                    command = Pair(cmdCarStatus, cmdCarName)
                } catch (e: IllegalStateException) {
                    println("${e.message}")
                }
            }

            return command
        }
    }
}