package com.example.hiredswipe.candidate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.FragmentCandidateSwipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class CandidateSwipeFragment : Fragment(R.layout.fragment_candidate_swipe) {

    private val TAG = "CandidateSwipeFragment"

    // implementing view binding pt.1
    private var _binding: FragmentCandidateSwipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var jobArrayList : ArrayList<JobItem> // arraylist to store job postings
    private lateinit var candidateSwipeAdapter: CandidateSwipeAdapter // adapter
    private var mLayoutManager: LinearLayoutManager? = null // layoutManager
    private lateinit var recyclerView: RecyclerView // RecyclerView

    // firebase references
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // implementing view binding pt.2
        _binding = FragmentCandidateSwipeBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth // initialise auth
        val uid = auth.currentUser!!.uid // uid of current user

        mLayoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setHasFixedSize(true) // setHasFixedSize can be used for optimization purposes if we know that the list/rv is constant in size, and is not affected by the adapters size
        jobArrayList = arrayListOf()
        candidateSwipeAdapter = CandidateSwipeAdapter(jobArrayList)
        recyclerView.adapter = candidateSwipeAdapter

        EventChangeListener()

        val btnYes = binding.btnYes
        val btnNo = binding.btnNo

        btnYes.setOnClickListener { swipeYes() }
        btnNo.setOnClickListener { swipeNo() }

        // Initializing swipeGesture and passing it to itemTouchHelper
        // then we attach the itemTouchHelper to the recyclerView
        val swipeGesture = object : CandidateSwipeGesture(){
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

    private fun EventChangeListener() {
        db.collection("Recruiters").orderBy("name", Query.Direction.ASCENDING).
            addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.d(TAG, "Firestore error: ${error.message.toString()}")
                        return
                    }
                    else {
                        for (dc : DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                jobArrayList.add(dc.document.toObject(JobItem::class.java))
                            }
                        }
                        candidateSwipeAdapter.notifyDataSetChanged()
                    }
                }
            })
    }

    fun swipeYes(){
        Log.i("MainAct", "Yes Clicked!")
        val index = mLayoutManager?.findFirstVisibleItemPosition()
        if (index != null) {
            //like the profile/job Logic

            //Removing the card and updating the adapter
            jobArrayList.removeAt(index)
            candidateSwipeAdapter.notifyItemRemoved(index)
        }
    }

    fun swipeNo(){
        Log.i("MainAct", "No Clicked")

        val index = mLayoutManager?.findFirstVisibleItemPosition()
        if (index != null) {
            //Dislike the profile/job Logic

            //Removing the card and updating the adapter
            jobArrayList.removeAt(index)
            candidateSwipeAdapter.notifyItemRemoved(index)
        }
    }
}