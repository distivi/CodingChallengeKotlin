package models

enum class Direction {
    LEFT, RIGHT, UP, DOWN, FORWARD, BACKWARD
}

data class DirectionCommand(val order: UInt, val direction: Direction, val distance: Int) {
    fun getDirectionPoint(): Point {
        return when (direction) {
            Direction.LEFT -> Point(-distance, 0,0)
            Direction.RIGHT -> Point(distance, 0,0)
            Direction.UP -> Point(0,0, distance)
            Direction.DOWN -> Point(0,0, -distance)
            Direction.FORWARD -> Point(0,distance, 0)
            Direction.BACKWARD -> Point(0,-distance, 0)
        }
    }
}

