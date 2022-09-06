package models

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class WorldTest {

    @ParameterizedTest
    @CsvSource(
        "0,0,0",
        "10,0,0",
        "0,10,0",
        "0,0,10",
    )
    fun testContainsPoint(x: Int, y: Int, z:Int) {
        // given
        val world = World(10u,10u,10u)

        // when
        val point = Point(x,y,z)

        // then
        assertTrue(world.contains(point))
    }

    @ParameterizedTest
    @CsvSource(
        "-1,0,0",
        "0,-1,0",
        "0,0,-1",
        "11,0,0",
        "0,11,0",
        "0,0,11",
        "6,-2,4",
    )
    fun testDoesNotContainPoint(x: Int, y: Int, z:Int) {
        // given
        val world = World(10u,10u,10u)

        // when
        val point = Point(x,y,z)

        // then
        assertFalse(world.contains(point))
    }
}