package com.example.runningtrakerapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningtrakerapp.R
import com.example.runningtrakerapp.databinding.FragmentStatisticsBinding
import com.example.runningtrakerapp.ui.viewModel.StatisticsViewModel
import com.example.runningtrakerapp.utill.CustomMarkerView
import com.example.runningtrakerapp.utill.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment() : Fragment(R.layout.fragment_statistics) {
    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStatisticsBinding.bind(view)
        subscribeToObserver()
        setupBarChart()

    }

    private fun setupBarChart() {
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            setDrawGridLines(false)
            axisLineColor = Color.BLACK
            textColor = Color.BLACK

        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        binding.barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }


    }

    private fun subscribeToObserver() {

        viewModel.totalTimeRun.observe(viewLifecycleOwner) {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        }
        viewModel.totalDistance.observe(viewLifecycleOwner) {
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km * 10f) / 10f
                val totalDistanceString = "${totalDistance}km"
                binding.tvTotalDistance.text = totalDistanceString
            }
        }

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner) {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        }
        viewModel.totalCalories.observe(viewLifecycleOwner) {
            it?.let {
                val totalCalories = "${it}kcal"
                binding.tvTotalCalories.text = totalCalories
            }
        }

        viewModel.runsSortedByDate.observe(viewLifecycleOwner) { data ->
            data?.let {
                val allAvgSpeeds =
                    it.indices.map { i ->
                        BarEntry(
                            i.toFloat(),
                            it[i].avgSpeedInKMH.toFloat()
                        )
                    }

                val bardataSet = BarDataSet(allAvgSpeeds, "Avg Speed Over Time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                binding.apply {
                    barChart.data = BarData(bardataSet)
                    barChart.marker =
                        CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                    barChart.invalidate()
                }
            }
        }
    }

}


