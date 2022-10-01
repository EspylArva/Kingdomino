package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.FragmentMenuBinding


class MainMenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMenuBinding.inflate(inflater)
        initListeners()
        return binding.root
    }

    private fun initListeners() {

        // TODO: buttonNewGame.setOnClickListener
        binding.createNewGameButton.setOnClickListener {
            val bottomSheet = NewGameBottomSheet()
            bottomSheet.show(requireActivity().supportFragmentManager, NewGameBottomSheet.TAG)
        }

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}