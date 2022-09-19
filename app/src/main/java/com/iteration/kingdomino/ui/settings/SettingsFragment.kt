package com.iteration.kingdomino.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        binding.vmStatic = Companion
        return binding.root
    }

    companion object {
        var confirmOnPlay: Boolean = false

        val settingsList = """
            |confirm on play: $confirmOnPlay
        """.trimIndent()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}