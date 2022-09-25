package com.iteration.kingdomino.csvreader

import android.content.Context
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.model.Card
import com.iteration.kingdomino.game.model.Tile
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStreamReader
import java.util.*

class CSVReader
{
    fun readCsv(context : Context) : Stack<Card>
    {
        val inputStream = context.resources.openRawResource(R.raw.dominos);
        val listCards = Stack<Card>()
        val csvParser = CSVParserBuilder().withSeparator(',').build()
        val csvReader = CSVReaderBuilder(InputStreamReader(inputStream)).withCSVParser(csvParser).build()

        var line: Array<String>? = csvReader.readNext()
        while (line != null) {
            // Do something with the data
            val domino = Card(line[4].toInt(), Tile(Tile.Terrain.valueOf(line[1]), Tile.Crown.valueOf(line[0])), Tile(Tile.Terrain.valueOf(line[3]), Tile.Crown.valueOf(line[2])))
            listCards.push(domino)
            line = csvReader.readNext()
        }

        return listCards
    }
}