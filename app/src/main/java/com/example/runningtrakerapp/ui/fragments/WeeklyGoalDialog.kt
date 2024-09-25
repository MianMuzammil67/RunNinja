package com.example.runningtrakerapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.runningtrakerapp.databinding.WeaklyGaolDialougeBinding
import com.example.runningtrakerapp.ui.viewModel.WeekGoalViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WeeklyGoalDialog : DialogFragment() {

    private var _binding: WeaklyGaolDialougeBinding? = null
    private val binding get() = _binding!!
    private val weekGoalViewModel: WeekGoalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WeaklyGaolDialougeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        weekGoalViewModel.weeklyGoalLiveData.observe(viewLifecycleOwner) { goal ->
            binding.etWeeklyGoal.setText(goal.toString())
        }


        binding.button.setOnClickListener {
            val goalText = binding.etWeeklyGoal.text.toString()
            if (goalText.isNotEmpty()) {
                val weeklyGoal = goalText.toIntOrNull()
                if (weeklyGoal != null) {
                    weekGoalViewModel.setWeeklyGoal(weeklyGoal)
                    Toast.makeText(
                        requireContext(),
                        "Weekly goal set to $weeklyGoal kilometers",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter a weekly goal", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    } override fun onStart() {
        super.onStart()
        // Set dialog to full width
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



//@AndroidEntryPoint
//class WeeklyGoalDialog() : DialogFragment() {
//
//    private lateinit var binding: WeaklyGaolDialougeBinding
//    private val weekGoalViewModel: WeekGoalViewModel by viewModels()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = WeaklyGaolDialougeBinding.bind(view)
//
//        binding.button.setOnClickListener {
//
//            val goalText = binding.etWeeklyGoal.text.toString()
//            if (goalText.isNotEmpty()) {
//                val weeklyGoal = goalText.toIntOrNull()
//                if (weeklyGoal != null) {
//                    weekGoalViewModel.setWeeklyGoal(weeklyGoal)
//                    Toast.makeText(
//                        requireContext(),
//                        "Weekly goal set to $weeklyGoal KioMeters",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    dismiss()
//                } else {
//                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(requireContext(), "Please enter a weekly goal", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//    }
//
//
//}


