package com.example.listtodoapp.home

import android.content.ClipData.Item
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.listtodoapp.Model.Task
import com.example.listtodoapp.databinding.ItemToDoBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapter() :
    ListAdapter<Task, HomeAdapter.HomeViewHolder>(Companion) {

    private var context: Context? = null

    inner class HomeViewHolder(val binding: ItemToDoBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean =
            oldItem.uid == newItem.uid

        override fun areContentsTheSame(
            oldItem: Task,
            newItem: Task
        ): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemToDoBinding.inflate(layoutInflater, parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.txtShowTitle.text = currentItem.title
        holder.binding.txtShowTask.text = currentItem.description
        holder.binding.txtShowDate.text = currentItem.date.toString()
        holder.binding.txtShowTime.text = currentItem.time.toString()

        val myformat = "h:mm a"
        val sdf = SimpleDateFormat(myformat)
        holder.binding.txtShowTime.text = sdf.format(Date(currentItem.time))

        val myformat1 = "EEE, d MMM yyyy"
        val sdf1 = SimpleDateFormat(myformat1)
        holder.binding.txtShowDate.text = sdf1.format(Date(currentItem.date))




    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }




}
