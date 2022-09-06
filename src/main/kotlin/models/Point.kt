package models

data class Point(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String {
        return "$x,$y,$z"
    }

    fun addPoint(point: Point): Point {
        return Point(
            x + point.x,
            y + point.y,
            z + point.z
        )
    }

    fun minusPoint(point: Point): Point {
        return Point(
            x - point.x,
            y - point.y,
            z - point.z
        )
    }
}