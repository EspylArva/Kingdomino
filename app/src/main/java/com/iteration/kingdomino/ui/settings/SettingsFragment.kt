package com.iteration.kingdomino.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import com.iteration.kingdomino.R
import com.iteration.kingdomino.databinding.FragmentSettingsBinding
import com.iteration.kingdomino.ui.menu.MainMenuFragmentDirections

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater, container)
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View? {
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(inflater, R.layout.fragment_settings, container, false)
        binding.vmStatic = SettingsFragment.Companion
        return binding.root
    }

    companion object {
        var confirmOnPlay: Boolean = false

        val settingsList = """
            |confirm on play: $confirmOnPlay
        """.trimIndent()
    }

}