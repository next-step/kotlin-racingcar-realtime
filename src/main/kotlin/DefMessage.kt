data class DefMessage(
    val messageId: MessageID,
    val arg1: Any,
) {
    enum class MessageID {
        LoopStart,
        LoopStop,
        Add,
        Boost,
        Slow,
        Stop,
        PrintWinner,
        FinishAll,
    }
}
