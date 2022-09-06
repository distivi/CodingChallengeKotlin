package CommandsReader

class CommandsReaderFromTerminal: CommandsReaderInterface {
    override fun readInput(): String {
        return readln()
    }
}
