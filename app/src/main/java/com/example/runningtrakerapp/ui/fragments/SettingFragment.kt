package com.example.runningtrakerapp.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.runningtrakerapp.R
import com.example.runningtrakerapp.databinding.FragmentSettingsBinding
import com.example.runningtrakerapp.utill.Constants.KEY_NAME
import com.example.runningtrakerapp.utill.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SettingFragment() : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding

    @Inject
    @Named("defaultPreferences")
    lateinit var sharedPref: SharedPreferences

//    private lateinit var sharedPreferences: SharedPreferences


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        loadFilesFromSharedPref()

//        sharedPreferences =
//            requireContext().getSharedPreferences("weekly_goal_prefs", Context.MODE_PRIVATE)
//        setWeeklyGoal(30)


        binding.btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPref()
            if (success) {
                Snackbar.make(view, "Saved Changes ", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "Please fill out the fields", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

//    fun setWeeklyGoal(goal: Int) {
//        sharedPreferences.edit().putInt("weekly_goal", goal).apply()
//    }

    private fun loadFilesFromSharedPref() {
        val name = sharedPref.getString(KEY_NAME, "")
        val weight = sharedPref.getFloat(KEY_WEIGHT, 80f)
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }

    private fun applyChangesToSharedPref(): Boolean {

        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        } else {
            sharedPref.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight.toFloat())
                .apply()
        }
        return true

    }


}