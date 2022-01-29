package com.example.hiredswipe.recruiter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.Candidate
import com.example.hiredswipe.R

class RecruiterSwipeAdapter(private val candidateList: ArrayList<Candidate>) :
    RecyclerView.Adapter<RecruiterSwipeAdapter.ExampleViewHolder>() {

    // this methods only gets called a few times, creates the viewHolders (does not populate them?)
    // that fit on the screen + a few extra
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.candidate_item, parent, false)
        return ExampleViewHolder(itemView)
    }

    // method which gets called when we want to fill/change the data in the viewHolders
    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = candidateList[position]
        //filling or updating the data into viewHolder from correct position in the exampleList
        holder.textView1.text = currentItem.firstName.toString() + " " + currentItem.lastName.toString()
    }

    // method which returns the size of the arraylist
    override fun getItemCount() : Int {
        return candidateList.size
    }

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.text_view_2)
    }
}