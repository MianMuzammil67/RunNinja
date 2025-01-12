package com.example.runningtrakerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runningtrakerapp.databinding.RunLayoutBinding
import com.example.runningtrakerapp.db.Run
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter : RecyclerView.Adapter<RunAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RunLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRun = diffUtil.currentList[position]
        holder.itemView.apply {
            Glide.with(context).load(currentRun.image).into(holder.binding.ivRunImage)
            setOnClickListener {
                _itemClicked?.let { it(currentRun) }
            }
        }

        val calender = Calendar.getInstance().apply {
            timeInMillis = currentRun.timestamp
        }

        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        holder.binding.apply {
            tvDate.text = dateFormat.format(calender.time)

            tvAvgSpeed.text = "${currentRun.avgSpeedInKMH}km/h"
            tvCalories.text = "${currentRun.caloriesBurned}kcal"
            tvDistance.text = "${currentRun.distanceInMeters}m"
        }
    }
    private var _itemClicked: ((Run) -> Unit)? = null
    fun itemclickedlistener(listener: (Run) -> Unit) {
        _itemClicked = listener
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem == newItem
        }
    }
    private val diffUtil = AsyncListDiffer(this, diffCallBack)
    fun submitList(list: List<Run>) = diffUtil.submitList(list)


    class ViewHolder(var binding: RunLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
