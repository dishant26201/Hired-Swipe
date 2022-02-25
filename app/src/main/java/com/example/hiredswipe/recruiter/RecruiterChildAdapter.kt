package com.example.hiredswipe.recruiter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R

class RecruiterChildAdapter(private val exampleList: List<CandidateItem>) : RecyclerView.Adapter<RecruiterChildAdapter.ExampleViewHolder2>(){

    private lateinit var mListener2: OnClickListener2

    interface OnClickListener2 {
        fun onCardLeftClick()
        fun onCardRightClick()
    }

    fun setOnClickListener(listener2: OnClickListener2) {
        mListener2 = listener2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder2 {
        val itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.candidate_item, parent, false)
        return ExampleViewHolder2(itemView2, mListener2)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder2, position: Int) {
        val currentItem = exampleList[position]

        holder.textView1.text = currentItem.text1
        holder.textView2.text = currentItem.text2

    }

    override fun getItemCount(): Int {
        return exampleList.size
    }

    class ExampleViewHolder2(itemView2: View, listener2: OnClickListener2) : RecyclerView.ViewHolder(itemView2) {

        val textView1: TextView = itemView2.findViewById(R.id.candidate_tv_1)
        val textView2: TextView = itemView2.findViewById(R.id.candidate_tv_2)

        val leftSide: View = itemView.findViewById(R.id.vCardLeftClick)
        val rightSide: View = itemView.findViewById(R.id.vCardRightClick)



        init {

            //onclick listener for the cardView (left and right sides)
            leftSide.setOnClickListener{
                listener2.onCardLeftClick()
            }

            rightSide.setOnClickListener {
                listener2.onCardRightClick()
            }
        }

    }
}