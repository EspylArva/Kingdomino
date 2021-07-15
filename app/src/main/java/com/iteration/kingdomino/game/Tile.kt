package com.iteration.kingdomino.game


data class Tile(val type : Terrain, val crown : Int) {

    override fun toString() = "[$type ($crown)]"


    public enum class Terrain {
        CASTLE,
        FIELD,
        FOREST,
        MINE,
        MOUNTAIN,
        NULL,
        PLAIN,
        SEA
    }
}