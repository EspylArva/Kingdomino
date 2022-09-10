package com.iteration.kingdomino.ui.appendix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iteration.kingdomino.R

class AppendixFragment : Fragment() {

    private lateinit var appendixViewModel: AppendixViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        appendixViewModel =
                ViewModelProvider(this).get(AppendixViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_appendix, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        appendixViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}