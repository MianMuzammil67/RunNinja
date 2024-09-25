package com.example.runningtrakerapp.ui.fragments

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.runningtrakerapp.R
import com.example.runningtrakerapp.R.layout
import com.example.runningtrakerapp.databinding.FragmentTrackingBinding
import com.example.runningtrakerapp.databinding.FragmentTrakingBinding
import com.example.runningtrakerapp.db.Run
import com.example.runningtrakerapp.services.Polyline
import com.example.runningtrakerapp.services.TrackingServices
import com.example.runningtrakerapp.ui.viewModel.MainViewModel
import com.example.runningtrakerapp.utill.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtrakerapp.utill.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtrakerapp.utill.Constants.ACTION_STOP_SERVICE
import com.example.runningtrakerapp.utill.Constants.CANCEL_TRACKING_DIALOG_TAG
import com.example.runningtrakerapp.utill.Constants.MAP_ZOOM
import com.example.runningtrakerapp.utill.Constants.POLYLINE_COLOR
import com.example.runningtrakerapp.utill.Constants.POLYLINE_WIDTH
import com.example.runningtrakerapp.utill.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(layout.fragment_traking) {
    private lateinit var binding: FragmentTrakingBinding

    val viewModel: MainViewModel by viewModels()

    @set:Inject
    var weight = 80f

    private var isTracking = false
    private var curTimeInMillis = 0L

    private var pathPoints = mutableListOf<Polyline>()
    private var map: GoogleMap? = null
    private var menu: Menu? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrakingBinding.bind(view)
        binding.mapView.onCreate(savedInstanceState)


        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }
        binding.btnFinishRun.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }

        binding.mapView.getMapAsync { map ->
            this.map = map
            addAllPolyline()
        }

        subscribeToObservers()

        if (savedInstanceState != null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingFragment?
            cancelTrackingDialog?.setYesListener { stopRun() }


        }

    }

    private fun subscribeToObservers() {
        TrackingServices.isTracking.observe(viewLifecycleOwner) {
            updateTracking(it)
        }
        TrackingServices.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        }

        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner) {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            binding.tvTimer.text = formattedTime

        }
    }

    private fun sendCommandToService(string: String) {
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action = string
            requireContext().startService(it)
        }
    }

    private fun toggleRun() {
        if (isTracking) {
            this.menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking && curTimeInMillis > 0L) {
//            binding.btnToggleRun.text = "Start"
            binding.btnToggleRun.text = "Pause"
            binding.btnFinishRun.visibility = View.VISIBLE
        } else if (!isTracking) {
            this.menu?.getItem(0)?.isVisible = true
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

//    when we change mobile rotation previous polyline will be lost.
//    with this fun we will be able to draw line again

    private fun addAllPolyline() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }

    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)

        }
    }

/*
//    private fun zoomToSeeWholeTrack() {
//        val bonds = LatLngBounds.Builder()
//        for (polyline in pathPoints) {
//            for (pol in polyline) {
//                bonds.include(pol)
//            }
//        }
//        map?.moveCamera(
//            CameraUpdateFactory
//                .newLatLngBounds(
//                    bonds.build(),
//                    binding.mapView.width,
//                    binding.mapView.height,
//                    (binding.mapView.height * 0.05).toInt()
//                )
//        )
//    }

 */


    private fun zoomToSeeWholeTrack() {
        if (map == null || pathPoints.isEmpty()) return

        val boundsBuilder = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (point in polyline) {
                boundsBuilder.include(point)
            }
        }

        // Set padding to fit the bounds with some margin (e.g., 10% of map height)
        val bounds = boundsBuilder.build()
        val padding = (binding.mapView.height * 0.05).toInt() // 5% padding

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                binding.mapView.width,
                binding.mapView.height,
                padding
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bitmap ->
            var distanceInMeter = 0
            for (polyline in pathPoints) {
                distanceInMeter += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeter / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val timeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeter / 1000f) * weight).toInt()

            val run = Run(
                bitmap,
                timeStamp,
                curTimeInMillis,
                avgSpeed.toInt(),
                distanceInMeter,
                caloriesBurned
            )
            viewModel.saveRun(run)

            Snackbar.make(
                requireActivity().findViewById(R.id.main),
                "Run saved successfully", Snackbar.LENGTH_SHORT
            ).show()
            stopRun()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.tool_bar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cancel_run -> {
                showCancelTrackingDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCancelTrackingDialog() {
        CancelTrackingFragment().apply {
            setYesListener {
                stopRun()
            }

        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }

    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }


    override fun onStart() {
        binding.tvTimer.text = "00:00:00:00"
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}