package com.iteration.kingdomino.game

public class Card(val tile1 : Tile, val tile2 : Tile)
{
    override fun toString() = "$tile1 || $tile2"

}