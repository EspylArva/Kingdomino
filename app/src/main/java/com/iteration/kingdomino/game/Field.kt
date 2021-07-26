package com.iteration.kingdomino.game

import timber.log.Timber

public class Field {
    // FIXME : should castle have value 0 or 1?
    val field : MutableList<MutableList<Tile>> = MutableList(9) { MutableList(9) { Tile(Tile.Terrain.NULL, Tile.Crown.ZERO) } }
    private var domains = HashMap<HashSet<Int>, Tile.Terrain>()
//    private var domains : Map<HashSet<Int>, Tile.Terrain> = HashMap<HashSet<Int>, Tile.Terrain>()
    init {
        field[4][4] = Tile(Tile.Terrain.CASTLE, Tile.Crown.ZERO)
    }

    fun calculateScore() : Int
    {
        var score = 0

        Timber.d(domains.toString())

        for(domain in domains.keys)
        {
            var crowns = 0
            for(tileId in domain)
            {
                val y = tileId % 10
                val x = (tileId - y)/10
                crowns += field[x][y].crown.value
            }
            score += crowns * domain.size
        }
        return score
    }

    fun addTile(t : Tile, posXY : Pair<Int, Int>)
    {
        val x = posXY.first; val y = posXY.second

        if(x < 0 || x > 8){ throw PlayerFieldException("Impossible to add this tile to the player field: given x index was $x; should be between 0 and 8") }
        if(y < 0 || y > 8){ throw PlayerFieldException("Impossible to add this tile to the player field: given y index was $y; should be between 0 and 8") }
        if(field[x][y].type != Tile.Terrain.NULL) throw PlayerFieldException("Impossible to add this tile: target is not an empty tile")
        else {
            if(fieldSmallEnough(x, y))
            {
                if(validNeighbour(x, y, t.type)) {// at least one neighbour of XY is valid
                    field[x][y] = t
                    addToDomains(x, y, t)

                    Timber.d("Success playing tile $t at $posXY")
                }
                else { throw PlayerFieldException("Invalid movement: no valid neighbour has been found near this position.") }
            }
            else { throw PlayerFieldException("Invalid movement: field already reaches limits of size for a field.") }
        }
    }

    private fun addToDomains(x: Int, y: Int, t: Tile) {
        // Checking which domain to add to...
        val neighboursId = getNeighboursId(x, y)
        // No similar terrain, means only a castle is nearby
        if(neighboursId.isEmpty()) {
            val newSet = HashSet<Int>(); newSet.add(x*10 + y)
            domains[newSet] = field[x][y].type
        }
        // Similar terrain found, adding to the first domain
        else {
            for(neighbourId in neighboursId)
            {
                domains.forEach { entry ->
                    if(entry.key.contains(neighbourId)) {
                        entry.key.add(x*10 + y)
                    }
                }
            }
        }

        // Checking if two domains should be merged

        val domainsContainingXY = domains.filter { entry -> entry.key.contains(x*10 + y) }
        if(domainsContainingXY.size > 1)
        {
            for(i in 1 until domainsContainingXY.size)
            {
                (domains.keys.find { domain -> domain == domainsContainingXY.keys.elementAt(0) } as HashSet).addAll(domainsContainingXY.keys.elementAt(i))
                domains = domains.filter { entry -> entry.key != domainsContainingXY.keys.elementAt(i) } as HashMap
            }
        }
    }


    private fun trimmedField(field : MutableList<MutableList<Tile>>) : MutableList<MutableList<Tile>>
    {
        val trimmedField = fieldClone() // clone the field
        for(i in 8 downTo 0) {
            if(field[i].all { tile -> tile.type == Tile.Terrain.NULL }) {
                trimmedField.removeAt(i)
            }
            if(field.all { row -> row[i].type == Tile.Terrain.NULL }) {
                trimmedField.forEach { row -> row.removeAt(i) }
            }
        }
        return trimmedField
    }

    fun trimmedField() = trimmedField(field)

    private fun fieldClone(): MutableList<MutableList<Tile>> {
        return field.map { it.map {tile -> tile.copy() }.toMutableList()}.toMutableList()
    }

    private fun fieldSmallEnough(x : Int, y : Int) : Boolean {
        val futureField = fieldClone()
        futureField[x][y] = Tile(Tile.Terrain.CASTLE, Tile.Crown.THREE)
        val trimmed = trimmedField(futureField)
        return (trimmed.size < 6) and (trimmed[0].size < 6)
    }

    private fun validNeighbour(x : Int, y : Int, type : Tile.Terrain) : Boolean {
        return getNeighboursTypes(x, y).any { terrainType -> terrainType == type || terrainType == Tile.Terrain.CASTLE }
    }

    private fun getNeighboursId(x: Int, y: Int): List<Int> {
        val neighbours = mutableListOf<Int>()
        if(field[x][y].type != Tile.Terrain.NULL)
        {
            if(y > 0 && field[x][y-1].type == field[x][y].type) neighbours.add(x*10+(y-1))
            if(y < 8 && field[x][y+1].type == field[x][y].type) neighbours.add(x*10+(y+1))
            if(x > 0 && field[x-1][y].type == field[x][y].type) neighbours.add((x-1)*10+y)
            if(x < 8 && field[x+1][y].type == field[x][y].type) neighbours.add((x+1)*10+y)
        }
        return neighbours
    }

    private fun getNeighboursTypes(x : Int, y : Int) : Set<Tile.Terrain>
    {
        val neighbours = HashSet<Tile.Terrain>()
        if(y > 0) neighbours.add(field[x][y-1].type)
        if(y < 8) neighbours.add(field[x][y+1].type)
        if(x > 0) neighbours.add(field[x-1][y].type)
        if(x < 8) neighbours.add(field[x+1][y].type)

        return neighbours
    }




    private fun mapAsString(field : MutableList<MutableList<Tile>>) : String
    {
        var mapRepresentation = ""
        for(row in 0 until field.size)
        {
            for(col in 0 until field[row].size)
            {
                mapRepresentation += field[row][col].type.toString().elementAt(0).toUpperCase()
            }
            mapRepresentation += '\n'
        }
        return mapRepresentation
    }

    override fun toString(): String {
        return mapAsString(trimmedField(field))
    }

    class PlayerFieldException(message : String) : Exception(message)
}