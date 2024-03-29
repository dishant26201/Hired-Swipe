package com.example.hiredswipe.candidate

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R
import com.example.hiredswipe.Recruiter


class CandidateSwipeAdapter(private val context: Context, private val jobList: ArrayList<Recruiter>) :
    RecyclerView.Adapter<CandidateSwipeAdapter.ExampleViewHolder>() {

    private val TAG = "CandidateSwipeAdapter"

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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rv_secondary_candidate, parent, false)
        return ExampleViewHolder(itemView, mListener)
    }

    // method which gets called when we want to fill/change the data in the viewHolders
    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = jobList[position]

        //declaring the variables for DataSet, Adapter, LayoutManager, RecyclerView (of the Secondary RecyclerView)
        val childRvList = generateDummyList(currentItem.name.toString(), 4)            // arraylist to store information about job postings
        val childRvAdapter = CandidateChildAdapter(childRvList)                                // adapter

        val childLayoutManager = object : LinearLayoutManager(context, HORIZONTAL, false){      //layoutManager
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }

        holder.rv.layoutManager = childLayoutManager
        holder.rv.adapter = childRvAdapter
        holder.rv.setHasFixedSize(true)

        childRvAdapter.setOnClickListener(object : CandidateChildAdapter.OnClickListener2 {

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
        return jobList.size
    }


    class ExampleViewHolder(itemView: View, listener: OnClickListener) : RecyclerView.ViewHolder(itemView) {
        val rv = itemView.findViewById<RecyclerView>(R.id.rvSecondaryCandidate)

        private val btnYes: ImageButton = itemView.findViewById(R.id.btnYesCandidate)
        private val btnNo: ImageButton = itemView.findViewById(R.id.btnNoCandidate)


        init {
            btnYes.setOnClickListener {
                listener.onYesClick()
            }
            btnNo.setOnClickListener {
                listener.onNoClick()
            }
        }
    }


    // Generating DummyList for the Secondary RecyclerView Items
    private fun generateDummyList(name: String, size: Int): List<JobItem> {
        val list = ArrayList<JobItem>()

        for(i in 0 until size) {
           val item = JobItem("Name: $name", "Item: $i")
            list += item
        }
        return list
    }


}