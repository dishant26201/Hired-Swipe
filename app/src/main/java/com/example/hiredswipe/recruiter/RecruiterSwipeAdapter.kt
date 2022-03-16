package com.example.hiredswipe.recruiter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R
import com.example.hiredswipe.Candidate
import java.util.*
import kotlin.collections.ArrayList


class RecruiterSwipeAdapter(private val context: Context, private val candidateList: ArrayList<Candidate>) :
    RecyclerView.Adapter<RecruiterSwipeAdapter.ExampleViewHolder>() {

    private val TAG = "RecruiterSwipeAdapter"

    private lateinit var mListener: OnClickListener

    interface OnClickListener {
        fun onYesClick()
        fun onNoClick()
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    // this methods only gets called a few times, creates the viewHolders (does not populate them?)
    // that fit on the screen + a few extra
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_secondary_recruiter, parent, false)
        return ExampleViewHolder(itemView, mListener)
    }

    // method which gets called when we want to fill/change the data in the viewHolders
    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = candidateList[position]

        //declaring the variables for DataSet, Adapter, LayoutManager, RecyclerView (of the Secondary RecyclerView)
        val childRvList = generateDummyList(currentItem)            // arraylist to store information about job postings
        val childRvAdapter = RecruiterChildAdapter(childRvList)                                // adapter

        val childLayoutManager = object : LinearLayoutManager(context, HORIZONTAL, false){      //layoutManager
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        holder.rv.layoutManager = childLayoutManager
        holder.rv.adapter = childRvAdapter
        holder.rv.setHasFixedSize(true)

        childRvAdapter.setOnClickListener(object : RecruiterChildAdapter.OnClickListener2 {

            override fun onCardLeftClick() {
                val childRvPos = childLayoutManager.findFirstVisibleItemPosition()
                Log.d(TAG, "leftSide Clicked!\n\tBEFORE\tsecondaryRvPos: $childRvPos")
                //move to next cards until end of list
                if (childRvPos > 0){
                    holder.rv.scrollToPosition(childRvPos - 1)
                }
//                holder.rv.smoothScrollToPosition(secondaryRvAdapter.itemCount - 1)
                Log.d(TAG, "\n\tAFTER\tsecondaryRvPos: $childRvPos")
            }

            override fun onCardRightClick() {
                val secondaryRvPos = childLayoutManager.findFirstVisibleItemPosition()
                Log.d(TAG, "rightSide Clicked!\n\tBEFORE\tsecondaryRvPos: $secondaryRvPos")
                //move to previous cards until start of list
                if (secondaryRvPos < childRvAdapter.itemCount - 1){
                    holder.rv.scrollToPosition(secondaryRvPos + 1)
                }
                Log.d(TAG, "\n\tAFTER\tsecondaryRvPos: $secondaryRvPos")
            }

        })

    }

    // method which returns the size of the arraylist
    override fun getItemCount() : Int {
        return candidateList.size
    }

    class ExampleViewHolder(itemView: View, listener: OnClickListener) : RecyclerView.ViewHolder(itemView) {
        val rv = itemView.findViewById<RecyclerView>(R.id.rvSecondaryRecruiter)

        private val btnYes: ImageButton = itemView.findViewById(R.id.btnYesRecruiter)
        private val btnNo: ImageButton = itemView.findViewById(R.id.btnNoRecruiter)

        init {
            btnYes.setOnClickListener {
                listener.onYesClick()
            }
            btnNo.setOnClickListener {
                listener.onNoClick()
            }
        }
    }

    // Generating ArrayList for the Secondary RecyclerView Items
    private fun generateDummyList(current: Candidate): List<Candidate> {
        val candidateDataList = ArrayList<Candidate>()

        /*
            Here we segregate the candidate data into multiple data classes
                1. The name and email of the Candidate (first card, required)
                2. The Candidate's Education (second card, optional)
                3. The Candidate's Work Experience (third card, optional)

                Right now I'm going to send the Candidate object to all cards and display data accordingly
         */

        for(i in 0 until 3) {
            candidateDataList.add(current)
        }
        return candidateDataList
    }

}