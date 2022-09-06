import CommandsReader.CommandsReaderInterface
import models.Direction
import models.DirectionCommand

class CommandsReceiver(val commandsReader: CommandsReaderInterface) {
    val commands: MutableMap<UInt, DirectionCommand> = mutableMapOf()
    var currentCommand: UInt = 1u
    var dataStreamFinished = false

    fun start() {
        if (commandsReader.readInput() != ";COMMAND") {
            throw Exception("Data stream is not ready")
        }
    }

    fun hasNewCommand(): Boolean {
        return !dataStreamFinished
    }

    fun readNextCommand(): DirectionCommand? {
        val commandFromQueue = getNextDirectionCommandInQueue()
        if (commandFromQueue != null) {
            return commandFromQueue
        }

        val command = commandsReader.readInput()
        if (command == ";LAND") {
            dataStreamFinished = true
            return null
        }

        try {
            val (order, direction, distance) = command.split(" ")
            val command = DirectionCommand(order.toUInt(), Direction.valueOf(direction), distance.toInt())

            commands.put(command.order, command)
            val nextCommandInQueue = commands.get(currentCommand)

            if (nextCommandInQueue != null) {
                currentCommand += 1u
                return nextCommandInQueue
            } else {
                // waiting for our command to come
                return null
            }
        } catch (e: Exception) {
            return null
        }

        return null
    }

    private fun getNextDirectionCommandInQueue(): DirectionCommand? {
        val nextCommandInQueue = commands.get(currentCommand)

        if (nextCommandInQueue != null) {
            currentCommand += 1u
            return nextCommandInQueue
        } else {
            // waiting for our command to come
            return null
        }
    }
}