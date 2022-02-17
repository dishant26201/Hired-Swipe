package com.example.hiredswipe.candidate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R
import com.example.hiredswipe.Recruiter

class CandidateSwipeAdapter(private val jobList: ArrayList<Recruiter>) :
    RecyclerView.Adapter<CandidateSwipeAdapter.ExampleViewHolder>() {

    private lateinit var mListener: onButtonClickListener

    interface onButtonClickListener {
        fun onYesClick()
        fun onNoClick()
    }

    fun setOnButtonClickListener(listener: onButtonClickListener) {
        mListener = listener
    }

    // this methods only gets called a few times, creates the viewHolders (does not populate them?)
    // that fit on the screen + a few extra
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.job_item, parent, false)
        return ExampleViewHolder(itemView, mListener)
    }

    // method which gets called when we want to fill/change the data in the viewHolders
    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = jobList[position]
        //filling or updating the data into viewHolder from correct position in the exampleList
        holder.textView1.text = currentItem.name.toString()
    }

    // method which returns the size of the arraylist
    override fun getItemCount() : Int {
        return jobList.size
    }


    class ExampleViewHolder(itemView: View, listener: onButtonClickListener) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.text_view_1)
        val btnYes: ImageButton = itemView.findViewById(R.id.btnYesCandidate)
        val btnNo: ImageButton = itemView.findViewById(R.id.btnNoCandidate)

        init {
            btnYes.setOnClickListener {
                listener.onYesClick()
            }
            btnNo.setOnClickListener {
                listener.onNoClick()
            }
        }
    }

}