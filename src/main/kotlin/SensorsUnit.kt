import CommandsReader.CommandsReaderInterface
import models.*

class SensorsReader(val reader: CommandsReaderInterface) {

    fun worldSize(): World? {
        val instruction = reader.readInput()
        if (instruction != ";WORLD") {
            return null
        }

        val dimensions = readCoordinates()

        if (dimensions.size != 3 || dimensions.any { it <= 0 }) {
            return null
        }

        return World(
            width = dimensions[0].toUInt(),
            depth = dimensions[1].toUInt(),
            height = dimensions[2].toUInt()
        )
    }

    fun dronePosition(): Point? {
        val instruction = reader.readInput()
        if (instruction != ";DRONE") {
            return null
        }

        val position = readCoordinates()

        if (position.size != 3) {
            return null
        }

        return Point(
            x = position[0],
            y = position[1],
            z = position[2]
        )
    }

    private fun readCoordinates(): List<Int> {
        val values = try {
            reader.readInput()
                .split(" ")
                .map { it.toInt() }
        } catch (e: Exception) {
            return listOf()
        }

        return values
    }
}