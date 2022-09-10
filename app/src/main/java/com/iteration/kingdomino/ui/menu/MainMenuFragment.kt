package com.iteration.kingdomino.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iteration.kingdomino.R

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
        val textView: TextView = root.findViewById(R.id.text_gallery)
        mainMenuViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}