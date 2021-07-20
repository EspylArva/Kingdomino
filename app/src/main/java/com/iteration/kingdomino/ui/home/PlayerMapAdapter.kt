package com.iteration.kingdomino.ui.home

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Player
import com.iteration.kingdomino.game.Tile
import timber.log.Timber


class PlayerMapAdapter(private val players : List<Player>) : RecyclerView.Adapter<PlayerMapAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_player_map, parent, false))
    }

    override fun getItemCount(): Int {
        return players.size;
    }

    override fun onBindViewHolder(holder: PlayerMapAdapter.ViewHolder, position: Int) {
        val field = players[position].map.field
//        val field = players[position].map.trimmedField()

        holder.playerMap.removeAllViews()
        val rows = field.size
        val columns = field[0].size

        holder.playerMap.rowCount = rows
        holder.playerMap.columnCount = columns

        val metrics = DisplayMetrics()
        holder.itemView.context.display?.getRealMetrics(metrics)

        val size = (metrics.widthPixels * 0.1).toInt()


        for(row in 0 until field.size)
        {
            for(col in 0 until field[row].size)
            {
                val iv = ImageView(holder.itemView.context)
                var drawableId = field[row][col].type.drawableId
                if(drawableId == 0) { drawableId = Tile.CASTLES[position] }

                iv.setImageResource(drawableId)
                iv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                val gridParam = GridLayout.LayoutParams()
                gridParam.height = size
                gridParam.width = size
                gridParam.columnSpec = GridLayout.spec(col)
                gridParam.rowSpec = GridLayout.spec(row)
                gridParam.setMargins(1,1,1,1)

                holder.playerMap.addView(iv, gridParam)

                iv.setOnClickListener { Timber.d("Clicked on $row x $col: ${field[row][col]}") }

            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val playerMap : GridLayout = itemView.findViewById(R.id.player_map)
    }

}
