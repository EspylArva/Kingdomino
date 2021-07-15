package com.iteration.kingdomino.game

data class Card(val id : Int, val tile1 : Tile, val tile2 : Tile)
{
    override fun toString() = "$id - $tile1 || $tile2"

}