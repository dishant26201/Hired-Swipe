package com.example.hiredswipe.candidate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.FragmentCandidateSwipeBinding
import com.google.firebase.auth.FirebaseAuth

class CandidateSwipeFragment : Fragment(R.layout.fragment_candidate_swipe) {

    //test comment
    // implementing view binding pt.1
    private var _binding: FragmentCandidateSwipeBinding? = null
    private val binding get() = _binding!!

    // template list for recyclerView
    private val jobList = generateDummyList(150)

    // adapter for recyclerView
    private val adapter = CandidateSwipeAdapter(jobList)

    //layoutManager and RecyclerView
    private var mLayoutManager: LinearLayoutManager? = null
    private lateinit var recyclerView: RecyclerView;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // implementing view binding pt.2
        _binding = FragmentCandidateSwipeBinding.inflate(inflater, container, false)
        val view = binding.root

        mLayoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = mLayoutManager

        //setHasFixedSize can be used for optimization purposes if we know that the list is constant in size
        recyclerView.setHasFixedSize (true)

        val btnYes = binding.btnYes
        val btnNo = binding.btnNo

        btnYes.setOnClickListener { swipeYes() }
        btnNo.setOnClickListener { swipeNo() }

        // Initializing swipeGesture and passing it to itemTouchHelper
        // then we attach the itemTouchHelper to the recyclerView
        val swipeGesture = object : SwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT ->{
                        swipeYes()
                    }
                    ItemTouchHelper.RIGHT->{
                        swipeNo()
                    }
                }

                super.onSwiped(viewHolder, direction)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }



    private fun generateDummyList(size: Int): ArrayList<JobItem> {
        val list = ArrayList<JobItem>()
        for (i in 0 until size) {
            val drawable = when (i % 4) {
                0 -> R.drawable.ic_person_wave
                1 -> R.drawable.ic_person_elderly
                2 -> R.drawable.ic_person_sports
                else -> R.drawable.ic_person_sus
            }
            val item = JobItem(drawable, "Item $i", "Line 2")
            list += item
        }
        return list
    }

    fun swipeYes(){
        Log.i("MainAct", "Yes Clicked!")
        val index = mLayoutManager?.findFirstVisibleItemPosition()
        if (index != null) {
            //like the profile/job Logic

            //Removing the card and updating the adapter
            jobList.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    fun swipeNo(){
        Log.i("MainAct", "No Clicked")

        val index = mLayoutManager?.findFirstVisibleItemPosition()
        if (index != null) {
            //Dislike the profile/job Logic

            //Removing the card and updating the adapter
            jobList.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }


}