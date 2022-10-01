package com.iteration.kingdomino.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.ViewholderScorePlayerBinding
import com.iteration.kingdomino.game.model.Player
import com.iteration.kingdomino.game.model.Tile

class ScoreAdapter(val players: List<Pair<Player, Int>>) : RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderScorePlayerBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = players[position].first
        val playerPosition = players[position].second
        holder.setTextContent(player)
        holder.setCastleColor(playerPosition)
    }

    override fun getItemCount() = players.size

    class ViewHolder(private val binding: ViewholderScorePlayerBinding) : RecyclerView.ViewHolder(binding.root)  {

        init {
            val metrics = itemView.context.getSystemService(WindowManager::class.java).currentWindowMetrics.bounds
            val size = (metrics.width() * 0.2).toInt()
            binding.imgScorePlayerCrown.layoutParams.height = size
            binding.imgScorePlayerCrown.layoutParams.width = size
            binding.lblScorePlayerName.textSize = 36f // FIXME relative size ?
            binding.lblScoreValue.textSize = 24f
        }

        fun setTextContent(player: Player) {
            val res = itemView.resources
            val castleCentered = if(player.map.castleCentered) res.getString(R.string.yes) else res.getString(R.string.no)
            binding.lblScorePlayerName.text = player.name
            binding.lblScoreCenteredCastle.text = res.getString(R.string.score_castle_centered, castleCentered)
            binding.lblScoreDomainSize.text = res.getString(R.string.score_domain_size, player.map.domainSize)
            binding.lblScoreCrownCount.text = res.getString(R.string.score_crown_count, player.map.crownCount)
            binding.lblScoreValue.text = res.getString(R.string.score_value, player.score.toString())
        }

        fun setCastleColor(position: Int) {
            binding.imgScorePlayerCrown.background = ResourcesCompat.getDrawable(itemView.resources, Tile.CASTLES[position], null)
        }

    }
}