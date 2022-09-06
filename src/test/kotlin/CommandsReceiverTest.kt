import CommandsReader.CommandsReaderInterface
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import models.Direction
import models.DirectionCommand
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertFails

internal class CommandsReceiverTest {
    var commandsReader: CommandsReaderInterface = mockk<CommandsReaderInterface>(relaxed = true)
    lateinit var commandsReceiver: CommandsReceiver

    @BeforeEach
    fun setUp() {
        commandsReceiver = CommandsReceiver(commandsReader)
    }

    @Test
    fun start() {
        // given
        every { commandsReader.readInput() } returns ";COMMAND"
        // when
        commandsReceiver.start()
        // then
        verify { commandsReader.readInput() }
    }

    @Test
    fun startFailed() {
        // given
        every { commandsReader.readInput() } returns ";NA"
        // when
        assertFails { commandsReceiver.start() }
        // then
        verify { commandsReader.readInput() }
    }

    @Test
    fun hasNewCommand() {
        // given
        every { commandsReader.readInput() } returns ";COMMAND"
        // when
        commandsReceiver.start()
        assertTrue(commandsReceiver.hasNewCommand())
        // then
        verify { commandsReader.readInput() }
    }


    @Test
    fun readNextCommand() {
        // given
        every { commandsReader.readInput() } returnsMany listOf(";COMMAND", "01 LEFT 2", "02 RIGHT -5", ";LAND")
        // when
        commandsReceiver.start()
        assertTrue(commandsReceiver.hasNewCommand())

        assertEquals(DirectionCommand(1u,Direction.LEFT,2), commandsReceiver.readNextCommand())
        assertEquals(DirectionCommand(2u,Direction.RIGHT,-5), commandsReceiver.readNextCommand())
        assertNull(commandsReceiver.readNextCommand())
        assertFalse(commandsReceiver.hasNewCommand())
        // then
        verify { commandsReader.readInput() }
    }

    @Test
    fun readNextCommandOutOfOrder() {
        // this test simulate situation when commands come in different order
        // given
        every { commandsReader.readInput() } returnsMany listOf(";COMMAND", "02 LEFT 2", "01 RIGHT -5", ";LAND")
        // when
        commandsReceiver.start()
        assertTrue(commandsReceiver.hasNewCommand())
        assertNull(commandsReceiver.readNextCommand()) // it reads 02 left 2
        assertEquals(DirectionCommand(1u,Direction.RIGHT,-5), commandsReceiver.readNextCommand())
        assertEquals(DirectionCommand(2u,Direction.LEFT,2), commandsReceiver.readNextCommand())
        assertNull(commandsReceiver.readNextCommand())
        assertFalse(commandsReceiver.hasNewCommand())
        // then
        verify { commandsReader.readInput() }
    }

    @Test
    fun readNextCommandInvalidCommand() {
        // this test simulate situation when commands come in different order
        // given
        every { commandsReader.readInput() } returnsMany listOf(";COMMAND", "01 NA 2", ";LAND")
        // when
        commandsReceiver.start()
        assertTrue(commandsReceiver.hasNewCommand())
        assertNull(commandsReceiver.readNextCommand())
        assertNull(commandsReceiver.readNextCommand())
        assertFalse(commandsReceiver.hasNewCommand())
        // then
        verify { commandsReader.readInput() }
    }
}