import models.Point
import models.World
import io.mockk.*
import models.Direction
import models.DirectionCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class VoloDroneTest {
    var blackbox: Blackbox = mockk<Blackbox>(relaxed = true)
    var sensorsReader: SensorsReader = mockk<SensorsReader>(relaxed = true)
    var commandsReceiver: CommandsReceiver = mockk<CommandsReceiver>(relaxed = true)
    var movementUnit: MovementUnit = mockk<MovementUnit>(relaxed = true)
    lateinit var drone: VoloDrone

    @BeforeEach
    fun setUp() {
        drone = VoloDrone(blackbox, sensorsReader, commandsReceiver, movementUnit)
    }

    @Test
    fun testInit() {
        // then
        assertEquals("0.0.1", drone.firmwareVersion)
        verify(exactly = 0) {
            blackbox.log(any())
        }
    }

    @Test
    fun testBoot() {
        // given
        every { sensorsReader.worldSize() } returns World(10u,10u,10u)
        every { sensorsReader.dronePosition() } returns Point(5,5,5)
        every { commandsReceiver.hasNewCommand() } returns false

        // when
        val bootResult = drone.boot()

        // then
        assertTrue(bootResult)
        verify {
            blackbox.log("=== Volodrone Initialising")
            blackbox.log("=== Volodrone Sensor Data read.")
            blackbox.log("World: (x=range(0, 10),y=range(0, 10),z=range(0, 10))")
            blackbox.log("Drone starts at: (5,5,5)")
        }
    }

    @Test
    fun testBootFailedDueToMissingWorld() {
        // given
        every { sensorsReader.worldSize() } returns null

        // when
        drone.boot()

        // then
        verify(exactly = 1) {
            sensorsReader.worldSize()
        }

        verify {
            blackbox.log("=== Volodrone failed to get world size, booting aborted")
        }

        verify(exactly = 0) {
            sensorsReader.dronePosition()
        }
    }

    @Test
    fun testBootFailedDueToUnkownDronePosition() {
        // given
        every { sensorsReader.worldSize() } returns World(10u,10u,10u)
        every { sensorsReader.dronePosition() } returns null

        // when
        drone.boot()

        // then
        verify(exactly = 1) {
            sensorsReader.worldSize()
            sensorsReader.dronePosition()
        }
        verify {
            blackbox.log("=== Volodrone failed to get drone's position, booting aborted")
        }
    }

    @Test
    fun testBootFailedDueToInvalidDronePosition() {
        // given
        every { sensorsReader.worldSize() } returns World(10u,10u,10u)
        every { sensorsReader.dronePosition() } returns Point(5,5,-5)

        // when
        drone.boot()

        // then
        verify(exactly = 1) {
            sensorsReader.worldSize()
            sensorsReader.dronePosition()
        }
        verify {
            blackbox.log("=== Volodrone drone's position is out of world bounce, booting aborted")
        }
    }

    @Test
    fun testProcessingLoopWithoutAnyCommands() {
        // given
        every { sensorsReader.worldSize() } returns World(10u,10u,10u)
        every { sensorsReader.dronePosition() } returns Point(5,5,5)
        every { commandsReceiver.hasNewCommand() } returns false

        // when
        val bootResult = drone.boot()
        drone.runProcessingLoop()

        verify {

            blackbox.log("=== Volodrone Take Off")
            blackbox.log("=== Volodrone Landing")
        }
    }

    @Test
    fun testProcessingLoopWithCommands() {
        // given
        every { sensorsReader.worldSize() } returns World(10u,10u,10u)
        every { sensorsReader.dronePosition() } returns Point(5,5,5)
        every { commandsReceiver.hasNewCommand() } returnsMany listOf(true, true, true, false)
        every { commandsReceiver.readNextCommand() } returnsMany listOf(
            DirectionCommand(1u, Direction.UP, 2),
            DirectionCommand(2u, Direction.LEFT, 3),
            DirectionCommand(3u, Direction.BACKWARD, 10)
        )

        // when
        val bootResult = drone.boot()
        drone.runProcessingLoop()

        verify {
            blackbox.log("=== Volodrone Take Off")
            commandsReceiver.hasNewCommand()
            commandsReceiver.readNextCommand()
            movementUnit.flyTo(Point(5,5,7))
            commandsReceiver.hasNewCommand()
            commandsReceiver.readNextCommand()
            movementUnit.flyTo(Point(2,5,7))
            commandsReceiver.hasNewCommand()
            commandsReceiver.readNextCommand()
            blackbox.log("(0,-10,0) -> CRASH IMMINENT - AUTOMATIC COURSE CORRECTION")
            blackbox.log("(0,-5,0) -> (2,0,7)")
            movementUnit.flyTo(Point(2,0,7))
            blackbox.log("=== Volodrone Landing")
        }
    }
}