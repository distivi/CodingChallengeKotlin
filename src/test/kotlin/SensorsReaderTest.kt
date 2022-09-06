import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import io.mockk.*
import CommandsReader.*
import models.Point
import models.World

internal class SensorsReaderTest {

    var commandsReader: CommandsReaderInterface = mockk<CommandsReaderInterface>(relaxed = true)

    lateinit var sensorsReader: SensorsReader

    @BeforeEach
    fun setUp() {
        sensorsReader = SensorsReader(reader = commandsReader)
    }

    @Test
    fun worldSizeSuccessfull() {
        // when
        every { commandsReader.readInput() } returns ";WORLD" andThen "10 10 10"

        // then
        assertEquals(World(10u,10u,10u), sensorsReader.worldSize())
    }

    @Test
    fun worldSizeInvalidCommand() {
        // when
        every { commandsReader.readInput() } returns ";ROOM"

        // then
        assertNull(sensorsReader.worldSize())
    }

    @Test
    fun worldSizeInvalidDimensions() {
        // when
        every { commandsReader.readInput() } returns ";WORLD" andThen "10 ten five"

        // then
        assertNull(sensorsReader.worldSize())
    }

    @Test
    fun worldSizeCanNotBeNegative() {
        // when
        every { commandsReader.readInput() } returns ";WORLD" andThen "10 -10 10"

        // then
        assertNull(sensorsReader.worldSize())
    }

    @Test
    fun dronePosition() {
        // when
        every { commandsReader.readInput() } returns ";DRONE" andThen "5 5 5"

        // then
        assertEquals(Point(5,5,5), sensorsReader.dronePosition())
    }

    @Test
    fun dronePositionInvalidCommand() {
        // when
        every { commandsReader.readInput() } returns ";BOX" andThen "5 5 5"

        // then
        assertNull(sensorsReader.dronePosition())
    }

    @Test
    fun dronePositionInvalidCoordinates() {
        // when
        every { commandsReader.readInput() } returns ";DRONE" andThen "five five five"

        // then
        assertNull(sensorsReader.dronePosition())
    }
}
