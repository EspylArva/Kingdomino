package com.iteration.kingdomino.game.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.annotations.TestOnly
import timber.log.Timber
import java.lang.reflect.Type
import java.util.stream.Collectors.toList

class Field {
    private val fieldSize: Int
    private val maxFieldSize: Int
    val field : MutableList<MutableList<Tile>>
    private val trimmedField
        get() = field.trimmed()

    val width: Int get() = this.field[0].size
    val height: Int get() = this.field.size

    /**
     * Map representing the field. The field's [Tile]s are grouped by "domains".
     * Each domain regroups all [Tile]s orthogonally adjacent and of the same [Tile.Terrain] type.
     * Key: Set<Int>        . Set of [Tile] positions within the field. The position is an integer between 0 and 99. Digit of tens represent X coordinate, digit of units represent Y coordinate.
     * Value: Tile.Terrain  . [Tile.Terrain] type of the domain.
     */
    private var domains = HashMap<MutableSet<Int>, Tile.Terrain>()

    val domainSize: Int get() = trimmedField.field.stream().flatMap { row -> row.stream() }.filter { it.type != Tile.Terrain.NULL }.collect(toList()).size
    val crownCount: Int get() = trimmedField.field.stream().flatMap { row -> row.stream() }.filter { it.type != Tile.Terrain.NULL }.mapToInt { it.crown.value }.sum()

    val castleCentered: Boolean get() {
        val middle = trimmedField.field.size.floorDiv(2)
        return trimmedField.field[middle][middle].type == Tile.Terrain.CASTLE
    }

    init {
    }

    constructor(fieldSize: Int) {
        this.fieldSize = fieldSize
        maxFieldSize = fieldSize*2-1

        field = MutableList(fieldSize*2-1) { MutableList(fieldSize*2-1) { Tile.nullTile() } }
        val center = fieldSize-1
        // Sets the middle of the field to be a castle.
        field[center][center] = Tile(Tile.Terrain.CASTLE, Tile.Crown.ZERO)

    }
    constructor(argField: MutableList<MutableList<Tile>>) {
        this.fieldSize = argField.size
        maxFieldSize = fieldSize*2-1
        field = argField

        Timber.d("Field from argField=$argField:\n${field.mapAsString()}")
        Timber.d("width=$width, height=$height")
        for (x in 0 until width) {
            for (y in 0 until height) {
                Timber.d("addToDomains(x=$x, y=$y)")
                addToDomains(x, y)
            }
        }
    }

    /**
     * Computes the [Player]'s score.
     * Score is computed following this logic:
     * - Each domain yields an integer value, calculated by multiplying the size of the domain by the number of crowns contained by the domain.
     * - Each score yielded by a domain is added. The total score is the sum of all yielded domains score.
     *
     * @return the [Player]'s total score.
     */
    fun calculateScore() : Int {
        Timber.v("Player's domains=$domains")
        val score = domains.keys.sumOf total@{ domain ->
            val crowns = domain.sumOf {
                val x = it % 10
                val y = (it - x) / 10
                return@sumOf field[y][x].crown.value
            }
            return@total crowns * domain.size
        }

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
    fun addTile(tile : Tile, posXY : Pair<Int, Int>)
    {
        val x = posXY.first; val y = posXY.second

        if(x < 0 || x > maxFieldSize){ throw PlayerFieldException("Given x index was $x; should be between 0 and $maxFieldSize") }
        if(y < 0 || y > maxFieldSize){ throw PlayerFieldException("Given y index was $y; should be between 0 and $maxFieldSize") }
        if(!isTileLocationFree(posXY)) throw PlayerFieldException("Tile at $posXY is not free: currently occupied by ${field[y][x]}")
        if(!isFieldSmallEnough(posXY)) throw PlayerFieldException("Playing tile at $posXY would make it exceed max. size 5x5")

        field[y][x] = tile
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
        val neighboursId = getNeighboursPosition(x, y)
        // No similar terrain, means only a castle is nearby
        if(neighboursId.isEmpty()) {
            val newSet = HashSet<Int>(); newSet.add(y*10 + x)
            domains[newSet] = field[y][x].type
        }
        // Similar terrain found, adding to the first domain
        else {
            neighboursId.forEach {
                domains.filterKeys { key -> key.contains(it) }.forEach { entry -> entry.key.add(y*10 + x) }
            }
        }
        // Checking if two domains should be merged
        val domainsContainingXY = domains.filter { entry -> entry.key.contains(y*10 + x) }
        if(domainsContainingXY.size > 1) {
            for(i in 1 until domainsContainingXY.size) {
                (domains.keys.find { domain -> domain == domainsContainingXY.keys.elementAt(0) } as HashSet).addAll(domainsContainingXY.keys.elementAt(i))
                domains = domains.filter { entry -> entry.key != domainsContainingXY.keys.elementAt(i) } as HashMap
            }
        }
    }

    fun isFieldSmallEnough(position: Pair<Int, Int>) : Boolean {
        val x = position.first
        val y = position.second
        val future = field.clone().addAt(x, y, Tile(Tile.Terrain.CASTLE, Tile.Crown.THREE))
        val trimmedFuture = future.trimmed()
        Timber.v("Checking if the field does not exceed 5x5 even after adding a tile at ${x}x$y: currentSize=${trimmedField.width}x${trimmedField.height}. futureSize=${trimmedFuture.width}x${trimmedFuture.height}")
        return (trimmedFuture.width < fieldSize) and (trimmedFuture.height < fieldSize)
    }

    fun tileHasValidNeighbour(position: Pair<Int, Int>, tile: Tile) : Boolean {
        val x = position.first
        val y = position.second
        val anyValidNeighbour = getNeighboursTypes(x, y).any { terrainType -> terrainType == tile.type || terrainType == Tile.Terrain.CASTLE }
        Timber.v("Checking if any neighbour of ${x}x$y is a valid neighbour: neighbours=${getNeighboursTypes(x, y)} anyValidNeighbour=$anyValidNeighbour")
        return anyValidNeighbour
    }

    fun isTileLocationFree(tileLocation: Pair<Int, Int>) : Boolean {
        val x = tileLocation.first
        val y = tileLocation.second
        val locationFree = field[y][x].type == Tile.Terrain.NULL
        Timber.v("Checking if ${x}x$y cell is free: XYCell.type=${field[x][y].type}. locationFree=$locationFree")
        return locationFree
    }

    fun isCardTilesAdjacent(tile1Location: Pair<Int, Int>, tile2Location: Pair<Int, Int>) : Boolean {
        val sameX = (tile1Location.first == tile2Location.first) && (tile1Location.second == tile2Location.second + 1 || tile1Location.second == tile2Location.second - 1)
        val sameY = (tile1Location.second == tile2Location.second) && (tile1Location.first == tile2Location.first + 1 || tile1Location.first == tile2Location.first - 1)
        Timber.v("Checking if tile1 and tile2 are adjacent cell is free: tile1Location=$tile1Location tile2Location=$tile2Location. sameX=$sameX sameY=$sameY")
        return sameX || sameY
    }

    fun cardHasValidNeighbour(tile1Location: Pair<Int, Int>, tile2Location: Pair<Int, Int>, card: Card) : Boolean {
        val tile1ValidNeighbour = tileHasValidNeighbour(tile1Location, card.tile1)
        val tile2ValidNeighbour = tileHasValidNeighbour(tile2Location, card.tile2)
        Timber.v("Checking if any tile has valid neighbour: tile1ValidNeighbour=$tile1ValidNeighbour tile2ValidNeighbour=$tile2ValidNeighbour")
        return tile1ValidNeighbour || tile2ValidNeighbour
    }
    
    fun isCardLocationValid(tile1Location: Pair<Int, Int>, tile2Location: Pair<Int, Int>, card: Card) = isCardTilesAdjacent(tile1Location, tile2Location) && cardHasValidNeighbour(tile1Location, tile2Location, card)

    /**
     * Gets the list of the positions of [Tile] neighbouring the given XY coordinates.
     * The position is an integer between 0 and 99. Digit of tens represent X coordinate, digit of units represent Y coordinate.
     *
     * @param x the X coordinate.
     * @param y the Y coordinate.
     * @return the list of neighbour positions.
     */
    private fun getNeighboursPosition(x: Int, y: Int): List<Int> {
        val neighbours = mutableListOf<Int>()
        if(field[y][x].type != Tile.Terrain.NULL) {
            if(y > 0 && field[y-1][x].type == field[y][x].type) neighbours.add((y-1)*10+(x))
            if(y < height-1 && field[y+1][x].type == field[y][x].type) neighbours.add((y+1)*10+(x))
            if(x > 0 && field[y][x-1].type == field[y][x].type) neighbours.add(y*10+x-1)
            if(x < width-1 && field[y][x+1].type == field[y][x].type) neighbours.add(y*10+x+1)
        }
        return neighbours
    }

    /**
     * Gets a set of [Tile] [Tile.Terrain] type neighbouring the given XY coordinates.
     * As the return type is a [Set], amount of neighbours of the same type will not be reflected.
     *
     * @param x the X coordinate.
     * @param y the Y coordinate.
     * @return the set of neighbouring [Tile.Terrain] type.
     */
    private fun getNeighboursTypes(x : Int, y : Int) : Set<Tile.Terrain>
    {
        val neighbours = mutableSetOf<Tile.Terrain>()
        if(y > 0) neighbours.add(field[y-1][x].type)
        if(y < maxFieldSize) neighbours.add(field[y+1][x].type)
        if(x > 0) neighbours.add(field[y][x-1].type)
        if(x < maxFieldSize) neighbours.add(field[y][x+1].type)

        return neighbours
    }

    override fun toString(): String {
        return field.mapAsString()
    }

    fun clone() : Field = Field(field.clone())

    companion object FieldConverter {
        fun fromField(field: Field) : String = Gson().toJson(field.field)

        fun fromString(value: String?): Field {
            val listType: Type = object : TypeToken<MutableList<MutableList<Tile>>>() {}.type
            val field : MutableList<MutableList<Tile>> = Gson().fromJson(value, listType)
            return Field(field)
        }
    }
}

class PlayerFieldException(message : String) : Exception(message) {
    private val playerFieldExceptionPrefix = "Invalid action: "
    override val message: String
        get() = playerFieldExceptionPrefix + super.message
}

private fun MutableList<MutableList<Tile>>.addAt(x: Int, y: Int, tile: Tile) : MutableList<MutableList<Tile>> {
    this[y][x] = tile
    return this
}

private fun MutableList<MutableList<Tile>>.clone(): MutableList<MutableList<Tile>> {
    return this.map { it.map {tile -> tile.copy() }.toMutableList()}.toMutableList()
}

private fun MutableList<MutableList<Tile>>.trimmed(): Field {
    val trimmedField = this.clone()// fieldClone() // clone the field
    for(i in 8 downTo 0) {
        if(this[i].all { tile -> tile.type == Tile.Terrain.NULL }) {
            trimmedField.removeAt(i)
        }
        if(this.all { row -> row[i].type == Tile.Terrain.NULL }) {
            trimmedField.forEach { row -> row.removeAt(i) }
        }
    }
    return Field(trimmedField)
}

@TestOnly
fun MutableList<MutableList<Tile>>.mapAsString() : String = this.joinToString ("\n") {
    it.joinToString { tile -> "[${tile.type.name.elementAt(0).uppercaseChar()}${tile.crown.value}]" }
}