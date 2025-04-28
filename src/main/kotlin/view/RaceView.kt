package view

class RaceView {
    companion object {
        private const val ERROR_TAG = "[ERROR]"
    }

    fun printContent(message: String) {
        println(message)
    }

    fun printError(errorMessage: String) {
        println("$ERROR_TAG $errorMessage")
    }

    fun inputContent(): String = readLine() ?: ""
}