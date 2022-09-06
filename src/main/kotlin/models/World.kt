package models

data class World(val width: UInt, val depth: UInt, val height: UInt) {
    override fun toString(): String {
        return "World: (x=range(0, $width),y=range(0, $depth),z=range(0, $height))"
    }

    fun contains(point: Point): Boolean {
        return point.x in 0..width.toInt() &&
                point.y in 0..depth.toInt() &&
                point.z in 0..height.toInt()
    }
}