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
import androidx.core.view.allViews
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Card
import com.iteration.kingdomino.game.Field
import com.iteration.kingdomino.game.Player
import com.iteration.kingdomino.game.Tile
import timber.log.Timber
import java.lang.Exception


class PlayerMapAdapter(private val vm : GameViewModel) : RecyclerView.Adapter<PlayerMapAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_player_map, parent, false))
    }

    override fun getItemCount(): Int {
        return vm.playerOrder.size
    }

    @SuppressLint("ShowToast")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.v("onBindViewHolder of PlayerMapAdapter. position=$position")
        val field = vm.playerOrder.keys.toList()[position].map.field


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
                // Get the drawables
                var typeDrawableId = field[row][col].type.drawableId;
                if(typeDrawableId == 0) { typeDrawableId = Tile.CASTLES[position] }
                val crownDrawableId = field[row][col].crown.drawableId
                val clTile = generateTileConstraintLayout(holder, typeDrawableId, crownDrawableId)

                // Grid cell layout parameters
                val gridParam = GridLayout.LayoutParams()
                gridParam.height = size; gridParam.width = size // Square dimensions
                gridParam.columnSpec = GridLayout.spec(col); gridParam.rowSpec = GridLayout.spec(row) // Set position in the grid; no weight attributed, because we don't want the cell to fill the space
                gridParam.setMargins(1,1,1,1) // Margins for display purposes

                holder.playerMap.addView(clTile, gridParam)

                // OnClickListener
                clTile.setOnClickListener {
                    val currentPlayer = vm.players.value!![0]
                    Timber.d("Clicked on $row x $col: ${field[row][col]} of ${vm.playerOrder.values.toList()[position]}. Current player=$currentPlayer")
                    if(currentPlayer == vm.playerOrder.keys.toList()[position]){
                        vm.addPosition(row, col)
                        showGhost(currentPlayer, clTile)
                    } else {
                        Timber.w("Cannot play on another player's field. Get back to yours! (currently @ $position, should be ${vm.playerOrder.keys.toList().indexOf(currentPlayer)}")
                        Toast.makeText(holder.itemView.context, holder.itemView.resources.getText(R.string.wrong_field), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showGhost(player: Player, clTile: ConstraintLayout) {
        val positions = vm.playerPickedPositions.value ?: return
        val card = vm.playerCardSelection.value ?: return

        if(positions.size == 1 && isValidTilePlacement(player, positions, card, 0)) {
            setNewDrawable(card.tile1, positions[0], 0.7f, clTile)
        } else if (positions.size == 2 && isValidTilePlacement(player, positions, card, 0, 1)) {
            setNewDrawable(card.tile1, positions[0], 0.7f, clTile)
            setNewDrawable(card.tile2, positions[1], 0.7f, clTile)
        }
    }
    private fun setNewDrawable(tile: Tile, position: Pair<Int, Int>, alpha: Float = 1f, clTile: ConstraintLayout) {
        Timber.v("Setting new drawable over tile @$position. tile=$clTile")

        clTile.removeAllViews()

        val ivCrown = ImageView(clTile.context)
        ivCrown.setImageResource(tile.crown.drawableId)
        ivCrown.alpha = 1f.coerceAtMost(alpha * 1.5f)
        val ivType = ImageView(clTile.context)
        ivType.setImageResource(tile.type.drawableId)
        ivType.alpha = alpha

        ivCrown.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        ivType.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        clTile.addView(ivCrown)
        clTile.addView(ivType)
    }

    /**
     * @param tileIndex should be 0 or 1 only
     */
    private fun isValidTilePlacement(player: Player, positions: MutableList<Pair<Int, Int>>, card: Card, vararg tileIndex: Int) : Boolean{
        for(index in tileIndex){
            if(index != 0 && index != 1) throw Exception("Unexpected argument value: tileIndex=$tileIndex, should be either 0 or 1")
            val tilePosValid = player.map.isTileLocationFree(positions[index])
            val smallEnough = player.map.isFieldSmallEnough(positions[index])
            if (!tilePosValid || !smallEnough){
                return false
            }
        }

        return if(tileIndex.size == 2) {
            if (player.map.isCardLocationValid(positions[0], positions[1], card)) {
                true
            } else {
                vm.playerPickedPositions.value!!.clear()
                vm.playerPickedPositions.postValue(vm.playerPickedPositions.value)
                false
            }
        } else {
            true
        }
    }

    private fun generateTileConstraintLayout(holder: ViewHolder, typeDrawableId: Int, crownDrawableId: Int) : ConstraintLayout {
        val clTile = ConstraintLayout(holder.itemView.context)
        val ivType = ImageView(holder.itemView.context); val ivCrown = ImageView(holder.itemView.context)

        // Set the drawables (if needed)
        if(crownDrawableId != 0) { ivCrown.setImageResource(crownDrawableId) }
        ivType.setImageResource(typeDrawableId)

        // Set layout parameters: parent layout should wrap content, as it will be resized later anyways
        // ImageViews should match parent to fill the map tile
        clTile.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ivType.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        ivCrown.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        // Add the ImageViews to the parent layout, and add it to the grid layout
        clTile.addView(ivType)
        clTile.addView(ivCrown)

        return clTile
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val playerMap : GridLayout = itemView.findViewById(R.id.player_map)
    }

}
