package com.iteration.kingdomino.game

import timber.log.Timber

public class Field {
    // FIXME : should castle have value 0 or 1?
    private val field : MutableList<MutableList<Tile>> = MutableList(9) { MutableList(9) { Tile(Tile.Terrain.NULL, Tile.Crown.ZERO) } }
    init {
        field[4][4] = Tile(Tile.Terrain.CASTLE, Tile.Crown.ZERO)
    }

    fun calculateScore() : Int
    {
        val setOfDomains = HashSet<Set<Tile>>()
        val trimmedDomain = trimmedField(field)
        for(rowIndex in 0 until trimmedDomain.size)
        {
            for(colIndex in 0 until trimmedDomain[0].size)
            {
                if(trimmedDomain[rowIndex][colIndex].type != Tile.Terrain.NULL && trimmedDomain[rowIndex][colIndex].type != Tile.Terrain.CASTLE)
                {
                    if(setOfDomains.all { domain -> !domain.contains(trimmedDomain[rowIndex][colIndex]) }) // tile is not referenced in domains
                    {
                        setOfDomains.add( getDomain(rowIndex, colIndex, trimmedDomain[rowIndex][colIndex].type, trimmedDomain) )
                    }
                }
            }
        }

        var score = 0
        for(domain in setOfDomains)
        {
            Timber.d(domain.toString())
            var multiplier = 0;
            domain.forEach { tile -> multiplier += tile.crown.value }
            score += multiplier * domain.size
        }
        Timber.d("Computed score: $score")
        return score
    }

    private fun getDomain(rowIndex: Int, colIndex: Int, terrainType : Tile.Terrain, field : MutableList<MutableList<Tile>>): Set<Tile> {
        val domain = HashSet<Tile>()
        if(field[rowIndex][colIndex].type == terrainType)
        {
            domain.add(field[rowIndex][colIndex])
        }
        if(rowIndex < field.size - 1) { domain.addAll(getDomain(rowIndex+1, colIndex, terrainType, field)) } // propagate downward
        if(colIndex < field[0].size - 1) { domain.addAll(getDomain(rowIndex, colIndex+1, terrainType, field)) } // propagate to the right
        return domain
    }

    fun addTile(t : Tile, posXY : Pair<Int, Int>)
    {
        val x = posXY.first; val y = posXY.second

        if(x < 0 || x > 8){ throw PlayerFieldException("Impossible to add this tile to the player field: given x index was $x; should be between 0 and 8") }
        if(y < 0 || y > 8){ throw PlayerFieldException("Impossible to add this tile to the player field: given y index was $y; should be between 0 and 8") }
        if(field[y][x].type != Tile.Terrain.NULL) throw PlayerFieldException("Impossible to add this tile: target is not an empty tile")
        else {
            if(fieldSmallEnough(x, y))
            {
                Timber.d("Valid neighbour: ${validNeighbour(x, y, t.type)}")
                if(validNeighbour(x, y, t.type)) // at least one neighbour of XY is valid
                {
                    field[y][x] = t

                }
                else { throw PlayerFieldException("Invalid movement: no valid neighbour has been found near this position.") }
            }
            else { throw PlayerFieldException("Invalid movement: field already reaches limits of size for a field.") }
        }
    }

    private fun trimmedField(field : MutableList<MutableList<Tile>>) : MutableList<MutableList<Tile>>
    {
        val trimmedField = fieldClone() // clone the field
        for(i in 8 downTo 0)
        {
//            Timber.d("Scanning row $i || ${field[i]} : ${field[i].all { tile -> tile.type == Tile.Terrain.NULL }}")
            if(field[i].all { tile -> tile.type == Tile.Terrain.NULL })
            {
                trimmedField.removeAt(i)
            }
//            Timber.d("Scanning col $i || ${List(trimmedField[0].size) {it -> field[it][i]}} : ${field.all { row -> row[i].type == Tile.Terrain.NULL }}")
            if(field.all { row -> row[i].type == Tile.Terrain.NULL })
            {
                trimmedField.forEach { row -> row.removeAt(i) }
            }
        }
        return trimmedField
    }

    private fun fieldClone(): MutableList<MutableList<Tile>> {
//        val clone = MutableList(9) { MutableList(9) { Tile(Tile.Terrain.NULL, 0) } }
//        for(i in 0..8)
//        {
//            for(j in 0..8)
//            {
//                val t = field[j][i]
//                clone[j][i] = t.copy()
//            }
//        }
//        return clone
        return field.map { it.map {tile -> tile.copy() }.toMutableList()}.toMutableList()
    }

    private fun fieldSmallEnough(x : Int, y : Int) : Boolean {
        val futureField = fieldClone()
        val trimmed = trimmedField(futureField)
        return (trimmed.size < 6) and (trimmed[0].size < 6)
    }

    private fun validNeighbour(x : Int, y : Int, type : Tile.Terrain) : Boolean {
        val neighbours = HashSet<Tile.Terrain>()
        if(y > 0) neighbours.add(field[y-1][x].type)
        if(y < 8) neighbours.add(field[y+1][x].type)
        if(x > 0) neighbours.add(field[y][x-1].type)
        if(x < 8) neighbours.add(field[y][x+1].type)

        return neighbours.any { terrainType -> terrainType == type || terrainType == Tile.Terrain.CASTLE }
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