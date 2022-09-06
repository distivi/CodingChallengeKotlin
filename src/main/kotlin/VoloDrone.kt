import models.Direction
import models.DirectionCommand
import models.Point
import models.World

class VoloDrone(
    val blackbox: Blackbox,
    val sensorsReader: SensorsReader,
    val commandsReceiver: CommandsReceiver,
    val movementUnit: MovementUnit
    ) {
    val firmwareVersion = "0.0.1"
    val name = "Volodrone"

    // drone Memory
    private lateinit var world: World
    private lateinit var dronePosition: Point

    fun boot(): Boolean {
        blackbox.log("=== $name Initialising")
        blackbox.log("=== $name Sensor Data read.")

        val world = sensorsReader.worldSize()

        if (world == null) {
            blackbox.log("=== $name failed to get world size, booting aborted")
            return false
        }
        blackbox.log("$world")

        val dronePosition = sensorsReader.dronePosition()
        if (dronePosition == null) {
            blackbox.log("=== $name failed to get drone's position, booting aborted")
            return false
        }
        blackbox.log("Drone starts at: ($dronePosition)")

        if (!world.contains(dronePosition)) {
            blackbox.log("=== $name drone's position is out of world bounce, booting aborted")
            return false
        }

        this.world = world
        this.dronePosition = dronePosition

        return true
    }

    fun runProcessingLoop() {
        try {
            commandsReceiver.start()
        } catch (e: Exception) {
            blackbox.log("=== $name failed to start receiving commands")
            return
        }

        // we are good to go
        blackbox.log("=== $name Take Off")

        while (commandsReceiver.hasNewCommand()) {
            // read command
            // check if command is fine
            // calculate next fly position
            // check if destination position in range of world bounds
            // make corrections if need to not hit the wall
            // move the drone
            val command = commandsReceiver.readNextCommand()

            command?.let {
                val directionPoint = command.getDirectionPoint()
                val destinationPoint = dronePosition.addPoint(directionPoint)
                if (world.contains(destinationPoint)) {
                    flyTo(directionPoint, destinationPoint)
                } else {
                    // need course correction
                    blackbox.log("($directionPoint) -> CRASH IMMINENT - AUTOMATIC COURSE CORRECTION")
                    val autoCorrectedDestinationPoint = autocorrectDestinationPoint(destinationPoint)
                    val autoCorrectedDirectionPoint = destinationPoint.minusPoint(autoCorrectedDestinationPoint)
                    flyTo(autoCorrectedDirectionPoint, autoCorrectedDestinationPoint)
                }
            }
        }

        blackbox.log("=== $name Landing")
    }


    fun autocorrectDestinationPoint(point: Point): Point {
        val x = Math.min(world.width.toInt(), Math.max(0, point.x))
        val y = Math.min(world.depth.toInt(), Math.max(0, point.y))
        val z = Math.min(world.height.toInt(), Math.max(0, point.z))
        return Point(x,y,z)
    }

    fun flyTo(directionPoint: Point, destinationPoint: Point) {
        blackbox.log("($directionPoint) -> ($destinationPoint)")
        movementUnit.flyTo(destinationPoint)
        dronePosition = destinationPoint
    }

}