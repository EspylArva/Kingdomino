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
import timber.log.Timber


class PlayerMapAdapter(private val players : List<Player>) : RecyclerView.Adapter<PlayerMapAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerMapAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_player_map, parent, false))
    }

    override fun getItemCount(): Int {
        return players.size;
    }

    override fun onBindViewHolder(holder: PlayerMapAdapter.ViewHolder, position: Int) {

        /**
         * gridLayout = (GridLayout) findViewById(R.id.gridview);

        gridLayout.removeAllViews();

        int total = 10;
        int column = 3;
        int row = total / column;
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row + 1);
        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
        if (c == column) {
        c = 0;
        r++;
        }
        ImageView oImageView = new ImageView(this);
        oImageView.setImageResource(R.drawable.ic_launcher);

        oImageView.setLayoutParams(new LayoutParams(100, 100));

        Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        Spec colspan = GridLayout.spec(GridLayout.UNDEFINED, 1);
        if (r == 0 && c == 0) {
        Log.e("", "spec");
        colspan = GridLayout.spec(GridLayout.UNDEFINED, 2);
        rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
        }
        GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
        rowSpan, colspan);
        gridLayout.addView(oImageView, gridParam);


        }
         */
        val field = players[position].map.field//.trimmedField()

        holder.playerMap.removeAllViews()
        val rows = field.size
        val columns = field[0].size

        holder.playerMap.rowCount = rows
        holder.playerMap.columnCount = columns

        val metrics = DisplayMetrics()
        holder.itemView.context.display?.getRealMetrics(metrics)

        val size = (metrics.widthPixels * 0.2).toInt()
        Timber.d("Tile size for map: $size")


        for(row in 0 until field.size)
        {
            for(col in 0 until field[row].size)
            {
                val iv = ImageView(holder.itemView.context)
                var drawableId = field[row][col].type.drawableId
                if(drawableId == 0) { drawableId = R.drawable.ic_castle_blue }
                iv.setImageResource(drawableId)

                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.width = size; lp.height = size
                iv.layoutParams = lp

                val gridParam = GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f))
                holder.playerMap.addView(iv, gridParam)

//                iv.requestLayout()
//                holder.playerMap.requestLayout()
//                holder.itemView.requestLayout()
            }
        }
    }

    private fun setCardSize(holder: ViewHolder, size: Int) {
//        holder.clFirst.layoutParams.width  = size; holder.clFirst.layoutParams.height  = size
//        holder.clSecond.layoutParams.width = size; holder.clSecond.layoutParams.height = size
//        holder.clSecond.requestLayout()
    }

    private fun setBackground(iv : ImageView, drawableId : Int) {
        if (drawableId != 0) {
            iv.background = ResourcesCompat.getDrawable(iv.resources, drawableId, null)
        }
    }




    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val playerMap : GridLayout = itemView.findViewById(R.id.player_map)
    }

}
