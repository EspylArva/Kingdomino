package com.iteration.kingdomino

import com.iteration.kingdomino.game.model.Field
import com.iteration.kingdomino.game.model.Tile
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TypeConverterUnitTest {
    @Test
    fun marshall_field() {
        val content = mutableListOf(
            mutableListOf(Tile(Tile.Terrain.FIELD, Tile.Crown.ONE), Tile(Tile.Terrain.MINE, Tile.Crown.THREE), Tile(Tile.Terrain.NULL, Tile.Crown.ZERO)),
            mutableListOf(Tile(Tile.Terrain.FOREST, Tile.Crown.TWO), Tile(Tile.Terrain.CASTLE, Tile.Crown.ZERO), Tile(Tile.Terrain.FOREST, Tile.Crown.ONE)),
            mutableListOf(Tile(Tile.Terrain.MOUNTAIN, Tile.Crown.TWO), Tile(Tile.Terrain.PLAIN, Tile.Crown.TWO), Tile(Tile.Terrain.SEA, Tile.Crown.ONE))
        )
        val field = Field(content)

        val marshalled = Field.fromField(field)

        val expected = """
             [
                [{"type":"FIELD","crown":"ONE"},{"type":"MINE","crown":"THREE"},{"type":"NULL","crown":"ZERO"}],
                [{"type":"FOREST","crown":"TWO"},{"type":"CASTLE","crown":"ZERO"},{"type":"FOREST","crown":"ONE"}],
                [{"type":"MOUNTAIN","crown":"TWO"},{"type":"PLAIN","crown":"TWO"},{"type":"SEA","crown":"ONE"}]
            ]
        """.trimIndent().replace("\n", "").replace(" ", "")

        assertEquals(expected, marshalled)
    }

    @Test
    fun unmarshall_field() {
        val content = mutableListOf(
            mutableListOf(Tile(Tile.Terrain.FIELD, Tile.Crown.ONE), Tile(Tile.Terrain.MINE, Tile.Crown.THREE), Tile(Tile.Terrain.NULL, Tile.Crown.ZERO)),
            mutableListOf(Tile(Tile.Terrain.FOREST, Tile.Crown.TWO), Tile(Tile.Terrain.CASTLE, Tile.Crown.ZERO), Tile(Tile.Terrain.FOREST, Tile.Crown.ONE)),
            mutableListOf(Tile(Tile.Terrain.MOUNTAIN, Tile.Crown.TWO), Tile(Tile.Terrain.PLAIN, Tile.Crown.TWO), Tile(Tile.Terrain.SEA, Tile.Crown.ONE))
        )
        val field = Field(content)
        val expected = """
             [
                [{"type":"FIELD","crown":"ONE"},{"type":"MINE","crown":"THREE"},{"type":"NULL","crown":"ZERO"}],
                [{"type":"FOREST","crown":"TWO"},{"type":"CASTLE","crown":"ZERO"},{"type":"FOREST","crown":"ONE"}],
                [{"type":"MOUNTAIN","crown":"TWO"},{"type":"PLAIN","crown":"TWO"},{"type":"SEA","crown":"ONE"}]
            ]
        """.trimIndent().replace("\n", "").replace(" ", "")
        val unmarshalled = Field.fromString(expected)

        assertEquals(field, unmarshalled)
    }
}