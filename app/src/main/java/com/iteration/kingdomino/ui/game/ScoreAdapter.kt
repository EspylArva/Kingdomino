package com.iteration.kingdomino.ui.game

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.Player
import com.iteration.kingdomino.game.Tile

class ScoreAdapter(val players: List<Pair<Player, Int>>) : RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.viewholder_score_player, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position].first
        val playerPosition = players[position].second
        holder.setTextContent(player)
        holder.setCastleColor(playerPosition)
    }

    override fun getItemCount() = players.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        private val imgScoreCastle : ImageView = itemView.findViewById(R.id.img_score_player_crown)
        private val lblPlayer : TextView = itemView.findViewById(R.id.lbl_score_player_name)
        private val lblCastleCentered : TextView = itemView.findViewById(R.id.lbl_score_centered_castle)
        private val lblDomainSize : TextView = itemView.findViewById(R.id.lbl_score_domain_size)
        private val lblScore : TextView = itemView.findViewById(R.id.lbl_score_value)

        init {
            val metrics = DisplayMetrics()
            itemView.context.display?.getRealMetrics(metrics)
            val size = (metrics.widthPixels * 0.2).toInt()
            imgScoreCastle.layoutParams.height = size
            imgScoreCastle.layoutParams.width = size
            lblPlayer.textSize = 36f // FIXME relative size ?
            lblScore.textSize = 24f
        }

        fun setTextContent(player: Player) {
            val res = itemView.resources
            val castleCentered = if(player.map.castleCentered) res.getString(R.string.yes) else res.getString(R.string.no)
            lblPlayer.text = player.name
            lblCastleCentered.text = res.getString(R.string.score_castle_centered, castleCentered)
            lblDomainSize.text = res.getString(R.string.score_domain_size, player.map.domainSize)
            lblScore.text = res.getString(R.string.score_value, player.score.toString())
        }

        fun setCastleColor(position: Int) {
            imgScoreCastle.background = ResourcesCompat.getDrawable(itemView.resources, Tile.CASTLES[position], null)
        }

    }
}