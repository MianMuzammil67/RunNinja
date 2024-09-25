package com.example.runningtrakerapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.runningtrakerapp.R
import com.example.runningtrakerapp.databinding.RunDetailFragmentBinding
import com.example.runningtrakerapp.utill.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class RunDetailFragment : Fragment(R.layout.run_detail_fragment) {
    private lateinit var binding: RunDetailFragmentBinding
    private val arg: RunDetailFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RunDetailFragmentBinding.bind(view)

        arg.let { run ->
            val runData = run.runData

            binding.apply {
                val cal = runData.caloriesBurned
                val timeRun = TrackingUtility.getFormattedStopWatchTime(runData.timeInMillis)

                tvTotalCalories.text = "$cal kcal"
                tvAverageSpeed.text = getFormattedSpeed(runData.avgSpeedInKMH)
                tvTotalTime.text = timeRun
                tvTotalDistance.text = getFormattedDistance(runData.distanceInMeters)
            }
            Glide.with(requireContext()).load(runData.image).into(binding.imageView)

        }
    }

    private fun getFormattedDistance(distanceInMeters: Int): String {

        val km = distanceInMeters / 1000f
        val totalDistance = round(km * 10f) / 10f
        val distanceString = "${totalDistance}km"
        return distanceString
    }

    private fun getFormattedSpeed(speed: Int): String {

        val avgSpeed = round(speed * 10f) / 10f
        val avgSpeedString = "${avgSpeed}km/h"
        return avgSpeedString
    }


}