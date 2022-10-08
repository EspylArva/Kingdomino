package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.FragmentMenuBinding
import com.iteration.kingdomino.game.data.DaggerProvider


class MainMenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMenuBinding.inflate(inflater)
        initListeners()
        return binding.root
    }

    private fun initListeners() {
        binding.createNewGameButton.setOnClickListener {
            findNavController().navigate(R.id.nav_new_game)
        }

        binding.continueCurrentGameButton.setOnClickListener {
            findNavController().navigate(R.id.nav_load_game)
        }

        binding.createNewGameButton.setOnLongClickListener {
            // FIXME This is for debugging purposes. Remove
            Toast.makeText(requireContext(), "Clearing shared Preferences", Toast.LENGTH_SHORT).show()
            DaggerProvider.create().dataManager().clearSharedPreferences(requireContext())
            return@setOnLongClickListener true
        }

        binding.continueCurrentGameButton.setOnLongClickListener {
            // FIXME This is for debugging purposes. Remove
            findNavController().navigate(R.id.nav_game)
            return@setOnLongClickListener true
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