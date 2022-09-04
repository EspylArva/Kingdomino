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
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Card
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
        return vm.playerOrder.size
    }

    @SuppressLint("ShowToast")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.v("onBindViewHolder of PlayerMapAdapter. position=$position")
        val field = vm.playerOrder.keys.toList()[position].map.field

        displayPlayerMap(field, position, holder)
    }

    private fun displayPlayerMap(field: MutableList<MutableList<Tile>>, position: Int, holder: ViewHolder) {
        holder.playerMap.removeAllViews()
        val rows = field.size
        val columns = field[0].size
        holder.playerMap.rowCount = rows
        holder.playerMap.columnCount = columns

        for(row in 0 until field.size)
        {
            for(col in 0 until field[row].size)
            {
                // Get the drawables
                var typeDrawableId = field[row][col].type.drawableId
                if(typeDrawableId == 0) { typeDrawableId = Tile.CASTLES[position] }
                val crownDrawableId = field[row][col].crown.drawableId

                val clTile = generateTileConstraintLayout(holder, typeDrawableId, crownDrawableId)
                holder.add(clTile, row, col)
                clTile.setOnClickListener {
                    val currentPlayer = vm.players.value!![0]
                    Timber.v("Clicked on $row x $col: ${field[row][col]} of ${vm.playerOrder.values.toList()[position]}. Current player=$currentPlayer")
                    if(currentPlayer == vm.playerOrder.keys.toList()[position]){
                        vm.addPosition(row, col)
                    } else {
                        Timber.w("Cannot play on another player's field. Get back to yours! (currently @ $position, should be ${vm.playerOrder.keys.toList().indexOf(currentPlayer)}")
                        Toast.makeText(holder.itemView.context, holder.itemView.resources.getText(R.string.error_wrong_field), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Displays ghost drawable of the [Card] selected by the current [Player] at selected XY coordinates.
     *
     * @param player the [Player] on whom [Field] the card ghost will be displayed
     * @param holder the [ViewHolder] containing the field.
     */
    fun showGhost(player: Player, holder: ViewHolder) {
        Timber.d("Display ghost")
        val positions = vm.playerPickedPositions.value ?: return
        val card = vm.playerCardSelection.value ?: return

        Timber.d("Resetting player map ghosts")
        displayPlayerMap(player.map.field, vm.currentPlayerIndex, holder)

        Timber.d("Displaying ghost of card=$card at positions=$positions")
        if(isValidTilePlacement(player, positions, card)){
            if(positions.size == 1) {
                setNewDrawable(card.tile1, positions[0], 0.7f, holder)
            } else if (positions.size == 2) {
                setNewDrawable(card.tile1, positions[0], 0.7f, holder)
                setNewDrawable(card.tile2, positions[1], 0.7f, holder)
            }
        } else { vm.playerPickedPositions.value = mutableListOf() }
    }

    /**
     * Sets a new drawable based on a [Tile] for the tile at given coordinates.
     * The GUI tile is a [ConstraintLayout] containing two [ImageView].
     *
     * @param tile tile to display. Contains the drawable information to display.
     * @param position XY coordinates to populate. X and Y should be contained between 0 and 8.
     * @param alpha transparency level. Should be between 0 and 1. Default value is 1.
     * @param holder the ViewHolder containing the cell to change.
     */
    private fun setNewDrawable(tile: Tile, position: Pair<Int, Int>, alpha: Float = 1f, holder: ViewHolder) {
        val index = position.first*9 + position.second
        Timber.v("Trying to display ghost @$position. Shooting for index=$index")
        val clTile = holder.playerMap[index] as ConstraintLayout

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
     * Whether the ghost placement at given coordinates is valid or not.
     * Ghost placement has to fulfill following conditions:
     * - No [Tile] have been played at coordinates XY
     * - Player's field does not exceed size 5x5 after the tile has been played at given coordinates
     * - One of the [Card]'s two tile must be adjacent to a tile of same type, or to a [Tile.Terrain.CASTLE]
     *
     * @param player the player on whom field the card will be placed
     * @param positions the XY coordinates at which the card will be played
     * @param card the [Card] to be played
     * @return [Boolean] indicating whether the ghost placement is valid or not
     */
    private fun isValidTilePlacement(player: Player, positions: MutableList<Pair<Int, Int>>, card: Card) : Boolean {
        for(index in 0 until vm.playerPickedPositions.value!!.size) {
            val tilePosValid = player.map.isTileLocationFree(positions[index])
            val smallEnough = player.map.isFieldSmallEnough(positions[index])
            if (!tilePosValid || !smallEnough){
                return false
            }
        }
        return if(vm.playerPickedPositions.value!!.size == 1) true else player.map.isCardLocationValid(positions[0], positions[1], card)
    }

    /**
     * Generates a tile at GUI level with given drawables.
     * The tile is represented by a [ConstraintLayout] containing two [ImageView] for each drawable component.
     *
     * @param holder the ViewHolder containing the tiles.
     * @param typeDrawableId the tile type drawable id. [Tile] type represents a [Tile.Terrain].
     * @param crownDrawableId the tile crown drawable id. [Tile] crown represents a [Tile.Crown].
     * @return the GUI tile component.
     */
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

        fun add(clTile: ConstraintLayout, row: Int, col: Int) {
            val metrics = DisplayMetrics()
            itemView.context.display?.getRealMetrics(metrics)
            val size = (metrics.widthPixels * 0.1).toInt()

            // Grid cell layout parameters
            val gridParam = GridLayout.LayoutParams()
            gridParam.height = size; gridParam.width = size // Square dimensions
            gridParam.columnSpec = GridLayout.spec(col); gridParam.rowSpec = GridLayout.spec(row) // Set position in the grid; no weight attributed, because we don't want the cell to fill the space
            gridParam.setMargins(1,1,1,1) // Margins for display purposes

            playerMap.addView(clTile, gridParam)
        }
    }

}
