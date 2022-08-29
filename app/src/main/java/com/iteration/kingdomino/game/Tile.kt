package com.iteration.kingdomino.game

import com.iteration.kingdomino.R


data class Tile(val type : Terrain, val crown : Crown) {

    companion object
    {
        val CASTLES = listOf(R.drawable.ic_castle_red, R.drawable.ic_castle_blue, R.drawable.ic_castle_orange, R.drawable.ic_castle_green)
    }
    override fun toString() = "[$type (${crown.value})]"


    enum class Terrain(val drawableId: Int) {
        CASTLE(0),
        FIELD(R.drawable.ic_terrain_field),
        FOREST(R.drawable.ic_terrain_forest),
        MINE(R.drawable.ic_terrain_mine),
        MOUNTAIN(R.drawable.ic_terrain_mountain),
        NULL(R.drawable.ic_terrain_blank),
        PLAIN(R.drawable.ic_terrain_plain),
        SEA(R.drawable.ic_terrain_sea)
    }

    enum class Crown(val drawableId : Int, val value : Int) {
        ZERO(0, 0),
        ONE(R.drawable.ic_one_crown, 1),
        TWO(R.drawable.ic_two_crown, 2),
        THREE(R.drawable.ic_three_crown, 3)
    }
}

fun nullTile() : Tile = Tile(Tile.Terrain.NULL, Tile.Crown.ZERO)
