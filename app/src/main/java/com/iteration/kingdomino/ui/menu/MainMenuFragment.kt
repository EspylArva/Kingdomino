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
import com.iteration.kingdomino.R
import timber.log.Timber

class MainMenuFragment : Fragment() {

    private lateinit var mainMenuViewModel: MainMenuViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mainMenuViewModel =
                ViewModelProvider(this).get(MainMenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_menu, container, false)

        val btn_appendix = root.findViewById<Button>(R.id.btn_rules)

        btn_appendix.setOnClickListener {
            Timber.d("Navigating to appendix")
            root.findNavController().navigate(R.id.nav_appendix)
        }

        return root
    }
}