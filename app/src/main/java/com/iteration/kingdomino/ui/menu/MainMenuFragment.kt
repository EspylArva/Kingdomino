package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.FragmentMenuBinding

class MainMenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMenuBinding.inflate(inflater)
        initListeners()
        return binding.root
    }

    private fun initListeners() {

        // TODO: buttonNewGame.setOnClickListener

        binding.continueCurrentGameButton.setOnClickListener {
            findNavController().navigate(R.id.nav_game)
        }

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.nav_settings)
        }

        binding.rulesButton.setOnClickListener {
            val action = MainMenuFragmentDirections.actionMenuToAppendix()
            action.pageContent = R.raw.rules
            findNavController().navigate(action)
        }

        binding.creditsButton.setOnClickListener {
            val action = MainMenuFragmentDirections.actionMenuToAppendix()
            action.pageContent = R.raw.readme
            findNavController().navigate(action)
        }
    }
}