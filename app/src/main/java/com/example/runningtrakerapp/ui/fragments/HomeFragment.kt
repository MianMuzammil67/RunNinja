package com.example.runningtrakerapp.ui.fragments

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtrakerapp.R
import com.example.runningtrakerapp.adapters.RunAdapter
import com.example.runningtrakerapp.databinding.FragmentHomeBinding
import com.example.runningtrakerapp.ui.viewModel.MainViewModel
import com.example.runningtrakerapp.ui.viewModel.WeekGoalViewModel
import com.example.runningtrakerapp.utill.Constants.KEY_NAME
import com.example.runningtrakerapp.utill.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runningtrakerapp.utill.SortType
import com.example.runningtrakerapp.utill.TrackingUtility
import com.example.runningtrakerapp.utill.TrackingUtility.showSettingsAlert
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
//    private lateinit var sharedPreferences: SharedPreferences

    @Inject
    @Named("defaultPreferences")
    lateinit var sharedPre: SharedPreferences

//    @Inject
//    @Named("weeklyGoalPreferences")
//    lateinit var weekPreferences: SharedPreferences

    private lateinit var binding: FragmentHomeBinding
    private lateinit var runAdapter: RunAdapter
    private val mainViewModel: MainViewModel by viewModels()
    private val weeklyGoalViewModel: WeekGoalViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        requestPermissions()
        setupRecyclerView()
        loadFilesFromSharedPref()

        if (!TrackingUtility.canGetLocation(requireContext())) {
            (activity as? AppCompatActivity)?.showSettingsAlert()
        }

        when (mainViewModel.sortType) {
            SortType.DATE -> binding.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortType.DISTANCE -> binding.spFilter.setSelection(2)
            SortType.AVG_SPEED -> binding.spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> mainViewModel.sortRun(SortType.DATE)
                    1 -> mainViewModel.sortRun(SortType.RUNNING_TIME)
                    2 -> mainViewModel.sortRun(SortType.DISTANCE)
                    3 -> mainViewModel.sortRun(SortType.AVG_SPEED)
                    4 -> mainViewModel.sortRun(SortType.CALORIES_BURNED)
                }
            }
        }
        mainViewModel.runs.observe(viewLifecycleOwner) { run ->
            runAdapter.submitList(run)
        }

        binding.btnEditGaol.setOnClickListener {
            showWeekGoalDialog()
        }

//       weak goal

        weeklyGoalViewModel.weeklyGoalLiveData.observe(viewLifecycleOwner) { weeklyGoal ->
            Log.d("RunGoal", "weeklyGoalLiveData updated: $weeklyGoal")
            binding.weekGoal.text = "Week Goal: $weeklyGoal KM"

            if (weeklyGoal == 0) {
                showWeekGoalDialog()
            }
        }
        weeklyGoalViewModel.getTotalDistanceForWeek.observe(viewLifecycleOwner) { distance ->
            val distanceInKm = (distance / 1000)
            val weeklyGoal = weeklyGoalViewModel.getWeeklyGoal()

            binding.progressBar.progress = distanceInKm
            binding.disDone.text = "$distanceInKm KM done"
            binding.disLeft.text = "${weeklyGoal - distanceInKm} KM left"

            // Optional Toast for testing/debugging
//            Toast.makeText(requireContext(), "Distance: $distanceInKm KM", Toast.LENGTH_LONG).show()
        }


/*
        // Optional Toast for testing/debugging
//            Toast.makeText(requireContext(), "Week Goal: $weeklyGoal KM", Toast.LENGTH_SHORT).show()
//        }
//        lifecycleScope.launch {
//            weeklyGoalViewModel.weeklyGoalLiveData.observe(viewLifecycleOwner) { weeklyGoal ->
//                binding.weekGoal.setText(weeklyGoal.toString())
//                binding.weekGoal.text = "Week Goal: $weeklyGoal KM"
//                if (weeklyGoal == 0) {
//                    showWeekGoalDialog()
//                }
//            }
//        }


//        weeklyGoalViewModel.weeklyGoalLiveData.observe(viewLifecycleOwner) { weeklyGoal ->
//
//            Log.d("getWeeklyGoal", " fragment Weekly goal =  $weeklyGoal")
//
//            binding.weekGoal.text = "Week Goal: $weeklyGoal KM"
//
//            if (weeklyGoal == 0) {
//                showWeekGoalDialog()
//            }
//                Toast.makeText(requireContext(), "Week Goal: $weeklyGoal KM", Toast.LENGTH_SHORT).show()
//
//        }

//
//        val distance = weeklyGoalViewModel.getTotalDistanceForWeek
//
//        val distanceInt = (distance / 1000)
//        val weeklyGoal = weeklyGoalViewModel.getWeeklyGoal()
//        binding.progressBar.progress = distanceInt
//        binding.disDone.text = "${distanceInt}KM done "
//        binding.disLeft.text = "${weeklyGoal - distanceInt}KM left"

//        Toast.makeText(requireContext(), distance.toString(), Toast.LENGTH_LONG).show()

//            weeklyGoalViewModel.getTotalDistanceForWeek.observe(viewLifecycleOwner) { distance ->
//                val distanceInt = (distance / 1000)
//                val weeklyGoal = weeklyGoalViewModel.getWeeklyGoal()
//                binding.progressBar.progress = distanceInt
//                binding.disDone.text = "${distanceInt}KM done "
//                binding.disLeft.text = "${weeklyGoal - distanceInt}KM left"
//
//                Toast.makeText(requireContext(), distance.toString(), Toast.LENGTH_LONG).show()
//            }
//        }
*/

        runAdapter.itemclickedlistener {run ->
            val action = HomeFragmentDirections.actionRunFragmentToRunDetailFragment(run)
            findNavController().navigate(action)
        }

        binding.fab.setOnClickListener {
            if (!TrackingUtility.canGetLocation(requireContext())) {
                (activity as? AppCompatActivity)?.showSettingsAlert()
            } else
                findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    private fun showWeekGoalDialog() {
        val dialog = WeeklyGoalDialog()
        dialog.show(childFragmentManager, "SetWeeklyGoalDialog")
    }

    private fun loadFilesFromSharedPref() {
        val name = sharedPre.getString(KEY_NAME, "")
        binding.tvGreeting.text = name
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }

    /*
//    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        Log.d("RunFragment", "Permissions granted: $perms")
//
//    }
//    @RequiresApi(Build.VERSION_CODES.Q)
//    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            Log.d("RunFragment", "Permissions Denied permanently: $perms")
//            AppSettingsDialog.Builder(this).build().show()
//        } else {
//            requestPermissions()
//        }
//
//
//    }*/


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(
                    "RunFragment",
                    "Location permission granted.",
                )
            } else {
                Log.d(
                    "RunFragment",
                    "Location permission denied. Cannot start tracking.",
                )
                Toast.makeText(
                    context,
                    "Location permission denied. Cannot start tracking.",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }


    }

    private fun setupRecyclerView() {
        runAdapter = RunAdapter()

        binding.rvRuns.apply {
            adapter = runAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }


}

