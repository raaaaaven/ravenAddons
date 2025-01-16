package at.raven.ravenAddons.commands

class CommandBuilder(
    val name: String,
) {
    var description = ""
    var aliases: List<String> = emptyList()
    private var autoComplete: ((Array<String>) -> List<String>) = { listOf() }
    private var callback: (Array<String>) -> Unit = {}

    fun callback(callback: (Array<String>) -> Unit) {
        this.callback = callback
    }

    fun autoComplete(autoComplete: (Array<String>) -> List<String>) {
        this.autoComplete = autoComplete
    }

    fun toSimpleCommand() = SimpleCommand(name.lowercase(), aliases, callback, autoComplete)
}
