package com.example.hiredswipe.candidate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.FragmentCandidateSwipeBinding
import kotlin.system.exitProcess
import androidx.recyclerview.widget.RecyclerView


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

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)


        //setHasFixedSize can be used for optimization purposes if we know that the list is constant in size
        recyclerView.setHasFixedSize (true)

        val btnYes = binding.btnYes
        val btnNo = binding.btnNo

        // onClick listeners for both Yes, No butttons
        btnYes.setOnClickListener {
            //getting the correct position for the card which is swiped
            val cardPos = getCardPos()

            // calling swipeYes with the position
            swipeYes(cardPos)
        }

        btnNo.setOnClickListener {
            //getting the correct position for the card which is swiped
            val cardPos = getCardPos()

            // calling swipeNo with the position
            swipeNo(cardPos)
        }

        //As we want both the buttons to be disabled when we are scrolling, we add a scrollListner
        //to our recyclerView object
        //scrollListener enables the buttons when were are in SCROLL_STATE_IDLE (not scrolling)
        //and disabled the buttons when we are in any other state
        val scrollListener = object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    Log.i("MainAct", "In Scroll_State_Idle")
                    btnYes.isEnabled = true
                    btnYes.isClickable = true

                    btnNo.isClickable = true
                    btnNo.isEnabled = true
                }
                //if we are in any state other than idle (scrolling, slowing down) then we disable the buttons
                else{
                    btnYes.isEnabled = false
                    btnYes.isClickable = false

                    btnNo.isClickable = false
                    btnNo.isEnabled = false
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        // Initializing swipeGesture and passing it to itemTouchHelper
        // then we attach the itemTouchHelper to the recyclerView
        val swipeGesture = object : SwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemPos = viewHolder.position
                when(direction){
                    ItemTouchHelper.LEFT ->{
                        Log.i("Card Swiped: ", viewHolder.toString())
                        swipeYes(itemPos)
                    }
                    ItemTouchHelper.RIGHT->{
                        Log.i("Card Swiped: ", viewHolder.toString())
                        swipeNo(itemPos)
                    }
                }

                super.onSwiped(viewHolder, direction)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    private fun getCardPos(): Int {
        // if layoutManager is not defined yet, we exit with status code -1
        if(mLayoutManager == null){
            Log.i("MainAct", "Error, mLayoutManager is not defined")
            exitProcess(-1)
        }

        var cardPos = mLayoutManager!!.findFirstCompletelyVisibleItemPosition()
        //if cardPos = -1, this mean no view is completely visible and we have to align them first
        if(cardPos == -1){
            Log.i("MainAct", "Error, could not find a completelyVisibleItem")
            cardPos = 0
        }
        return cardPos
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

    fun swipeYes(pos: Int){
        Log.i("MainAct", "Yes Clicked!")
        if (pos >= 0) {
            //like the profile/job Logic

            //Removing the card and updating the adapter
            jobList.removeAt(pos)
            adapter.notifyItemRemoved(pos)
        }
        // if pos is null or invalid
        else{
            Log.i("MainAct", "Error invalid pos: $pos")
        }
    }

    fun swipeNo(pos: Int){
        Log.i("MainAct", "No Clicked")
        if (pos >= 0) {
            //Dislike the profile/job Logic

            //Removing the card and updating the adapter
            jobList.removeAt(pos)
            adapter.notifyItemRemoved(pos)
        }
        // if pos is null or invalid
        else{
           Log.i("MainAct", "Error invalid pos: $pos")
        }
    }


}