package com.example.hiredswipe.recruiter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.Candidate
import com.example.hiredswipe.CandidateEducation
import com.example.hiredswipe.CandidateWorkExp
import com.example.hiredswipe.R

class RecruiterChildAdapter(private val candidateDataList: List<Candidate>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var mListener2: OnClickListener2

    interface OnClickListener2 {
        fun onCardLeftClick()
        fun onCardRightClick()
    }

    fun setOnClickListener(listener2: OnClickListener2) {
        mListener2 = listener2
    }

    //view holders for all the types of items (check if we can export these as separate files?)
    class CandidateCard1(itemView: View, listener2: OnClickListener2) : RecyclerView.ViewHolder(itemView){

        fun bind(candidate: Candidate){
            val textView1: TextView = itemView.findViewById(R.id.candidate_tv_1)
            val textView2: TextView = itemView.findViewById(R.id.candidate_tv_2)
            val textView3: TextView = itemView.findViewById(R.id.candidate_tv_3)

            textView1.text = (candidate.firstName + " " + candidate.lastName)
            textView2.text = candidate.email
            textView3.text = candidate.location

        }
        private val leftSide: View = itemView.findViewById(R.id.vCardLeftClick)
        private val rightSide: View = itemView.findViewById(R.id.vCardRightClick)

        init {
            //onclick listener for the cardView (left and right sides)
            leftSide.setOnClickListener{ listener2.onCardLeftClick() }
            rightSide.setOnClickListener { listener2.onCardRightClick() }
        }
    }
    class CandidateCard2(itemView: View, listener2: OnClickListener2): RecyclerView.ViewHolder(itemView){

        fun bind(candidate: Candidate){
            val textView1: TextView = itemView.findViewById(R.id.candidate_tv_1)
            val textView2: TextView = itemView.findViewById(R.id.candidate_tv_2)
            val textView3: TextView = itemView.findViewById(R.id.candidate_tv_3)
            val education = candidate.educationList[0]

            textView1.text = education.schoolName
            textView2.text = education.degreeType
            textView3.text = education.description
        }
        private val leftSide: View = itemView.findViewById(R.id.vCardLeftClick)
        private val rightSide: View = itemView.findViewById(R.id.vCardRightClick)

        init {
            //onclick listener for the cardView (left and right sides)
            leftSide.setOnClickListener{ listener2.onCardLeftClick() }
            rightSide.setOnClickListener { listener2.onCardRightClick() }
        }
    }
    class CandidateCard3(itemView: View, listener2: OnClickListener2): RecyclerView.ViewHolder(itemView){

        fun bind(candidate: Candidate){
            val textView1: TextView = itemView.findViewById(R.id.candidate_tv_1)
            val textView2: TextView = itemView.findViewById(R.id.candidate_tv_2)
            val textView3: TextView = itemView.findViewById(R.id.candidate_tv_3)
            val education = candidate.workExpList[0]

            textView1.text = education.companyName
            textView2.text = education.jobTitle
            textView3.text = education.description
        }

        val leftSide: View = itemView.findViewById(R.id.vCardLeftClick)
        val rightSide: View = itemView.findViewById(R.id.vCardRightClick)

        init {
            //onclick listener for the cardView (left and right sides)
            leftSide.setOnClickListener{ listener2.onCardLeftClick() }
            rightSide.setOnClickListener { listener2.onCardRightClick() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        //inflating different layouts based on the index
        when (viewType) {
            0 -> {
                val itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.candidate_item_1, parent, false)
                return CandidateCard1(itemView2, mListener2)
            }
            1 -> {
                val itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.candidate_item_2, parent, false)
                return CandidateCard2(itemView2, mListener2)
            }
            2 -> {
                val itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.candidate_item_3, parent, false)
                return CandidateCard3(itemView2, mListener2)
            }
            else -> {
                val itemView2 = LayoutInflater.from(parent.context).inflate(R.layout.candidate_item_1, parent, false)
                return CandidateCard1(itemView2, mListener2)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(position){
            0 -> {
                (holder as CandidateCard1).bind(candidateDataList[position])
            }
            1 -> {
                (holder as CandidateCard2).bind(candidateDataList[position])
            }
            2 -> {
                (holder as CandidateCard3).bind(candidateDataList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return candidateDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


//    class ExampleViewHolder2(itemView2: View, listener2: OnClickListener2) : RecyclerView.ViewHolder(itemView2) {
//
//        val textView1: TextView = itemView2.findViewById(R.id.candidate_tv_1)
//        val textView2: TextView = itemView2.findViewById(R.id.candidate_tv_2)
//
//        val leftSide: View = itemView.findViewById(R.id.vCardLeftClick)
//        val rightSide: View = itemView.findViewById(R.id.vCardRightClick)
//
//
//        init {
//
//            //onclick listener for the cardView (left and right sides)
//            leftSide.setOnClickListener{
//                listener2.onCardLeftClick()
//            }
//
//            rightSide.setOnClickListener {
//                listener2.onCardRightClick()
//            }
//        }
//    }
}