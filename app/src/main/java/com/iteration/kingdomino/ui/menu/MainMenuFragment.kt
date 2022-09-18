package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.iteration.kingdomino.R
import timber.log.Timber

class MainMenuFragment : Fragment() {

    private lateinit var mainMenuViewModel: MainMenuViewModel
    private lateinit var buttonNewGame : Button
    private lateinit var buttonContinueGame : Button
    private lateinit var buttonSettings : Button
    private lateinit var buttonRules : Button
    private lateinit var buttonCredits : Button

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mainMenuViewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
        val root = initView(inflater, container)
        initListeners()
        initObservers()

        return root
    }

    private fun initListeners() {

        // TODO: buttonNewGame.setOnClickListener

        buttonContinueGame.setOnClickListener {
            findNavController().navigate(R.id.nav_game)
        }

        // TODO: buttonSettings.setOnClickListener

        buttonRules.setOnClickListener {
            val action = MainMenuFragmentDirections.actionMenuToAppendix()
            action.pageContent = R.raw.rules
            findNavController().navigate(action)
        }

        buttonCredits.setOnClickListener {
            val action = MainMenuFragmentDirections.actionMenuToAppendix()
            action.pageContent = R.raw.readme
            findNavController().navigate(action)
        }

    }

    private fun initObservers() {}

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View? {
        val root = inflater.inflate(R.layout.fragment_menu, container, false)

        buttonNewGame = root.findViewById(R.id.btn_new_game)
        buttonContinueGame = root.findViewById(R.id.btn_continue_game)
        buttonSettings = root.findViewById(R.id.btn_settings)
        buttonRules = root.findViewById(R.id.btn_rules)
        buttonCredits = root.findViewById(R.id.btn_credits)

        return root
    }

}