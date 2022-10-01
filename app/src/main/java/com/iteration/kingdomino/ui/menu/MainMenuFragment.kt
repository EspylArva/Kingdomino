package com.iteration.kingdomino.ui.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.FragmentMenuBinding
import timber.log.Timber


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
            Timber.d("Testing bottomsheet")
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