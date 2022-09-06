
fun main(args: Array<String>) {
    // given
    val inputReader = CommandsReader.CommandsReaderFromTerminal()
    val drone = VoloDrone(
        Blackbox(),
        sensorsReader = SensorsReader(reader = inputReader),
        commandsReceiver = CommandsReceiver(commandsReader = inputReader),
        movementUnit = MovementUnit()
    )

    // then
    if (drone.boot()) {
        drone.runProcessingLoop()
    }
}