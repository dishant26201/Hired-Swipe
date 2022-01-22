package com.example.hiredswipe.candidate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R


class CandidateSwipeAdapter(private val jobList: List<JobItem>) :
    RecyclerView.Adapter<CandidateSwipeAdapter.ExampleViewHolder>() {

    //this methods only gets called a few times, creates the viewHolders (does not populate them?)
    // that fit on the screen + a few extra
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.job_item,
            parent,
            false
        )
        return ExampleViewHolder(itemView)
    }

    //method which gets called when we want to fill/change the data in the viewHolders
    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = jobList[position]

        //filling or updating the data into viewHolder from correct position in the exampleList
        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView1.text = currentItem.text1
        holder.textView2.text = currentItem.text2
    }

    override fun getItemCount() = jobList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val textView1: TextView = itemView.findViewById(R.id.text_view_1)
        val textView2: TextView = itemView.findViewById(R.id.text_view_2)
    }


}