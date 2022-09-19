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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false)
        binding.vmStatic = Companion
        return binding.root
    }

    companion object {
        var confirmOnPlay: Boolean = false

        val settingsList = """
            |confirm on play: $confirmOnPlay
        """.trimIndent()
    }

}