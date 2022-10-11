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
class FieldUnitTest {
    @Test
    fun trim_field() {
        val tile = Tile(Tile.Terrain.MINE, Tile.Crown.THREE)
        val field = Field(5)

        field.addTile(tile, Pair(4,5))
        field.addTile(tile, Pair(3,5))
        field.addTile(tile, Pair(2,5))
        field.addTile(tile, Pair(1,5))
        field.addTile(tile, Pair(0,5))

        field.trimmedField

//        assertEquals(expected, marshalled)
    }
}