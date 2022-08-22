package com.iteration.kingdomino.ui.home

import android.annotation.SuppressLint
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Field
import com.iteration.kingdomino.game.Player
import com.iteration.kingdomino.game.Tile
import timber.log.Timber


class PlayerMapAdapter(private val vm : GameViewModel) : RecyclerView.Adapter<PlayerMapAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_player_map, parent, false))
    }

    override fun getItemCount(): Int {
        return vm.players.value!!.size
    }

    @SuppressLint("ShowToast")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.d("onBindViewHolder of PlayerMapAdapter. position=$position")
        val field = vm.immutablePlayers[position].map.field


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
                val clTile = ConstraintLayout(holder.itemView.context)
                val ivType = ImageView(holder.itemView.context); val ivCrown = ImageView(holder.itemView.context)

                // Get the drawables
                var typeDrawableId = field[row][col].type.drawableId; val crownDrawableId = field[row][col].crown.drawableId
                if(typeDrawableId == 0) { typeDrawableId = Tile.CASTLES[position] }
                // Set the drawables (if needed)
                if(crownDrawableId != 0) { ivCrown.setImageResource(crownDrawableId) }
                ivType.setImageResource(typeDrawableId)

                // Set layout parameters: parent layout should wrap content, as it will be resized later anyways
                // ImageViews should match parent to fill the map tile
                clTile.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                ivType.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                ivCrown.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

                // Grid cell layout parameters
                val gridParam = GridLayout.LayoutParams()
                gridParam.height = size; gridParam.width = size // Square dimensions
                gridParam.columnSpec = GridLayout.spec(col); gridParam.rowSpec = GridLayout.spec(row) // Set position in the grid; no weight attributed, because we don't want the cell to fill the space
                gridParam.setMargins(1,1,1,1) // Margins for display purposes

                // Add the ImageViews to the parent layout, and add it to the grid layout
                clTile.addView(ivType)
                clTile.addView(ivCrown)
                holder.playerMap.addView(clTile, gridParam)


                // OnClickListener
                clTile.setOnClickListener {
                    val currentPlayer = vm.players.value!![0]
                    Timber.d("Clicked on $row x $col: ${field[row][col]} of ${vm.immutablePlayers[position]}. Current player=$currentPlayer")
                    if(currentPlayer == vm.immutablePlayers[position]){
                        try {
                            vm.addPosition(row, col)
//                            currentPlayer.playCard(vm.playerCardSelection.value!!, Pair(row, col), Pair(row, col)) // FIXME : should be two different pairs of coordinates
                        } catch (e : Field.PlayerFieldException) {
                            Timber.e("Error when playing ${vm.playerCardSelection.value}: $e")
                            Toast.makeText(holder.itemView.context, holder.itemView.resources.getString(R.string.error_play_tile), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Timber.d("Cannot play on another player's field. Get back to yours! (currently @ $position, should be ${vm.immutablePlayers.indexOf(currentPlayer)}")
                        Toast.makeText(holder.itemView.context, holder.itemView.resources.getText(R.string.wrong_field), Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val playerMap : GridLayout = itemView.findViewById(R.id.player_map)
    }

}
