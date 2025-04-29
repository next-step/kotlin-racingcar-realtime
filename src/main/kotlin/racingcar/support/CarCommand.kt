package racingcar.support

enum class CarCommand(val value: String) {
    add("차량 추가"),
    boost("속도 증가"),
    slow("속도 감소"),
    stop("차량 정지")
}