package com.example.runningtrakerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtrakerapp.R
import com.example.runningtrakerapp.databinding.FragmentSetupBinding
import com.example.runningtrakerapp.utill.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningtrakerapp.utill.Constants.KEY_NAME
import com.example.runningtrakerapp.utill.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SetupFragment() : Fragment(R.layout.fragment_setup) {
    @Inject
    @Named("defaultPreferences")
    lateinit var sharedPre: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    private lateinit var binding: FragmentSetupBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupBinding.bind(view)

        if (!isFirstAppOpen) {
            val navOption = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()

            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOption
            )
        }
        binding.btnContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }else{
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun writePersonalDataToSharedPref(): Boolean {

        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
//        val height = binding.etWeight.text.toString()


        if (name.isEmpty() || weight.isEmpty() ) {
            return false
        }
        sharedPre.edit()
            .putString(KEY_NAME, name)
//            .putString(KEY_Height, height)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

        val toolbarText = "Let's go, $name!"
        requireActivity().actionBar?.title = toolbarText
        return true

    }

}