package com.iteration.kingdomino.game.model

data class Card(val id : Int, val tile1 : Tile, val tile2 : Tile) : Comparable<Card>
{
    var hasBeenDrawn = false
    var hasBeenPlayed = false

    override fun toString() = "[Card#$id {$tile1 | $tile2}]"
    override fun equals(other: Any?): Boolean {
        return (other as Card).id == this.id
    }

    override fun compareTo(other: Card) = this.id - other.id
}