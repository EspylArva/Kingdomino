package com.iteration.kingdomino.game

import timber.log.Timber

class Field {

    constructor()

    constructor(argField: MutableList<MutableList<Tile>>) {
        field.clear()
        field.addAll(argField)
    }

    val field : MutableList<MutableList<Tile>> = MutableList(9) { MutableList(9) { nullTile() } }
    val width: Int get() = this.field.size
    val height: Int get() = this.field[0].size

    /**
     * Map representing the field. The field's [Tile]s are grouped by "domains".
     * Each domain regroups all [Tile]s orthogonally adjacent and of the same [Tile.Terrain] type.
     * Key: Set<Int>        . Set of [Tile] positions within the field. The position is an integer between 0 and 99. Digit of tens represent X coordinate, digit of units represent Y coordinate.
     * Value: Tile.Terrain  . [Tile.Terrain] type of the domain.
     */
    private var domains = HashMap<MutableSet<Int>, Tile.Terrain>()

    init {
        // Sets the middle of the field to be a castle.
        // FIXME : should castle have value 0 or 1?
        field[4][4] = Tile(Tile.Terrain.CASTLE, Tile.Crown.ZERO)
    }

    /**
     * Computes the [Player]'s score.
     * Score is computed following this logic:
     * - Each domain yields an integer value, calculated by multiplying the size of the domain by the number of crowns contained by the domain.
     * - Each score yielded by a domain is added. The total score is the sum of all yielded domains score.
     *
     * @return the [Player]'s total score.
     */
    fun calculateScore() : Int
    {
        Timber.v("Player's domains=$domains")
        val score = domains.keys.map { domain ->
            val crowns = domain.map {
                val y = it % 10
                val x = (it - y)/10
                field[x][y].crown.value
            }.toList().sum()
            crowns * domain.size
        }.toList().sum()

        Timber.d("Computing score. score=$score")
        return score
    }

    /**
     * Adds the given [Card] to the field, at given XY coordinates
     *
     * @param card the [Card] to be added to the field.
     * @param tile1Location the XY coordinates of the [Card]'s first tile.
     * @param tile2Location the XY coordinates of the [Card]'s second tile.
     */
    fun addCard(card: Card, tile1Location: Pair<Int, Int>, tile2Location: Pair<Int, Int>) {
        if (isCardLocationValid(tile1Location, tile2Location, card)) {
            addTile(card.tile1, tile1Location)
            addTile(card.tile2, tile2Location)
            Timber.i("Success playing card=$card at pos1=$tile1Location pos2=$tile2Location")
        } else {
            throw PlayerFieldException("Neither tile of card=$card has valid neighbour at positions [$tile1Location|$tile2Location] ")
        }
    }

    /**
     * Adds the given [Tile] to the field, at given XY coordinates
     *
     * @param tile the [Tile] to be added to the field.
     * @param posXY the XY coordinates of the [Tile].
     */
    private fun addTile(tile : Tile, posXY : Pair<Int, Int>)
    {
        val x = posXY.first; val y = posXY.second

        if(x < 0 || x > 8){ throw PlayerFieldException("Given x index was $x; should be between 0 and 8") }
        if(y < 0 || y > 8){ throw PlayerFieldException("Given y index was $y; should be between 0 and 8") }
        if(!isTileLocationFree(posXY)) throw PlayerFieldException("Tile at $posXY is not free: currently occupied by ${field[x][y]}")
        if(!isFieldSmallEnough(posXY)) throw PlayerFieldException("Playing tile at $posXY would make it exceed max. size 5x5")

        field[x][y] = tile
        addToDomains(x, y)

        Timber.v("Success playing tile $tile at $posXY")
    }

    /**
     * Adds the tile at given coordinates the map of [domains].
     *
     * @param x the X coordinate of the tile to add to the map of [domains].
     * @param y the Y coordinate of the tile to add to the map of [domains].
     */
    private fun addToDomains(x: Int, y: Int) {
        // Checking which domain to add to...
        val neighboursId = getNeighboursId(x, y)
        // No similar terrain, means only a castle is nearby
        if(neighboursId.isEmpty()) {
            val newSet = HashSet<Int>(); newSet.add(x*10 + y)
            domains[newSet] = field[x][y].type
        }
        // Similar terrain found, adding to the first domain
        else {
            domains.filterKeys { key -> key.containsAll(neighboursId) }.forEach { entry -> entry.key.add(x*10 + y) }
        }
        // Checking if two domains should be merged
        val domainsContainingXY = domains.filter { entry -> entry.key.contains(x*10 + y) }
        if(domainsContainingXY.size > 1) {
            for(i in 1 until domainsContainingXY.size) {
                (domains.keys.find { domain -> domain == domainsContainingXY.keys.elementAt(0) } as HashSet).addAll(domainsContainingXY.keys.elementAt(i))
                domains = domains.filter { entry -> entry.key != domainsContainingXY.keys.elementAt(i) } as HashMap
            }
        }
    }


    private fun trimmedField(field : MutableList<MutableList<Tile>>) : Field
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
        return Field(trimmedField)
    }

    fun trimmedField() = trimmedField(field)

    private fun fieldClone(): MutableList<MutableList<Tile>> {
        return field.map { it.map {tile -> tile.copy() }.toMutableList()}.toMutableList()
    }

    fun isFieldSmallEnough(position: Pair<Int, Int>) : Boolean {
        val x = position.first
        val y = position.second
        val futureField = fieldClone()
        futureField[x][y] = Tile(Tile.Terrain.CASTLE, Tile.Crown.THREE)
        val trimmed = trimmedField(futureField)
        Timber.v("Checking if the field does not exceed 5x5 even after adding a tile at ${x}x$y: currentSize=${trimmedField().width}x${trimmedField().height}. futureSize=${trimmed.width}x${trimmed.height}")
        return (trimmed.width < 6) and (trimmed.height < 6)
    }

    fun isValidNeighbour(position: Pair<Int, Int>, tile: Tile) : Boolean {
        val x = position.first
        val y = position.second
        val anyValidNeighbour = getNeighboursTypes(x, y).any { terrainType -> terrainType == tile.type || terrainType == Tile.Terrain.CASTLE }
        Timber.v("Checking if any neighbour of ${x}x$y is a valid neighbour: neighbours=${getNeighboursTypes(x, y)} anyValidNeighbour=$anyValidNeighbour")
        return anyValidNeighbour
    }

    fun isTileLocationFree(tileLocation: Pair<Int, Int>) : Boolean {
        val x = tileLocation.first
        val y = tileLocation.second
        val locationFree = field[x][y].type == Tile.Terrain.NULL
        Timber.v("Checking if ${x}x$y cell is free: XYCell.type=${field[x][y].type}. locationFree=$locationFree")
        return locationFree
    }

    fun isCardTilesAdjacent(tile1Location: Pair<Int, Int>, tile2Location: Pair<Int, Int>) : Boolean {
        val sameX = (tile1Location.first == tile2Location.first) && (tile1Location.second == tile2Location.second + 1 || tile1Location.second == tile2Location.second - 1)
        val sameY = (tile1Location.second == tile2Location.second) && (tile1Location.first == tile2Location.first + 1 || tile1Location.first == tile2Location.first - 1)
        Timber.v("Checking if tile1 and tile2 are adjacent cell is free: tile1Location=$tile1Location tile2Location=$tile2Location. sameX=$sameX sameY=$sameY")
        return sameX || sameY
    }

    fun isNeighbourValid(tile1Location: Pair<Int, Int>, tile2Location: Pair<Int, Int>, card: Card) : Boolean {
        val tile1ValidNeighbour = isValidNeighbour(tile1Location, card.tile1)
        val tile2ValidNeighbour = isValidNeighbour(tile2Location, card.tile2)
        Timber.v("Checking if any tile has valid neighbour: tile1ValidNeighbour=$tile1ValidNeighbour tile2ValidNeighbour=$tile2ValidNeighbour")
        return tile1ValidNeighbour || tile2ValidNeighbour
    }
    
    fun isCardLocationValid(tile1Location: Pair<Int, Int>, tile2Location: Pair<Int, Int>, card: Card) = isCardTilesAdjacent(tile1Location, tile2Location) && isNeighbourValid(tile1Location, tile2Location, card)

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
        return mapAsString(trimmedField(field).field)
    }

    class PlayerFieldException(message : String) : Exception(message) {
        private val PLAYER_FIELD_EXCEPTION_HEADER = "Invalid action: "
        override val message: String?
            get() = PLAYER_FIELD_EXCEPTION_HEADER + super.message
    }
}