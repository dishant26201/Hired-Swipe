package com.example.hiredswipe.recruiter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.Candidate
import com.example.hiredswipe.R
import com.example.hiredswipe.Recruiter
import com.example.hiredswipe.databinding.FragmentRecruiterSwipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList


class RecruiterSwipeFragment : Fragment(R.layout.fragment_recruiter_swipe) {

    private val TAG = "RecruiterSwipeFragment"

    // implementing view binding pt.1
    private var _binding: FragmentRecruiterSwipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var candidateArrayList : ArrayList<Candidate> // arraylist to store candidates
    private lateinit var recruiterSwipeAdapter: RecruiterSwipeAdapter // adapter
    private var mLayoutManager: LinearLayoutManager? = null // layoutManager
    private lateinit var recyclerView: RecyclerView // RecyclerView

    // firebase references
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // implementing view binding pt.2
        _binding = FragmentRecruiterSwipeBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth // initialise auth
        val uid = auth.currentUser!!.uid // uid of current user


        mLayoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView = binding.recyclerView2
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setHasFixedSize(true) // setHasFixedSize can be used for optimization purposes if we know that the list/rv is constant in size, and is not affected by the adapters size
        candidateArrayList = arrayListOf()
        recruiterSwipeAdapter = RecruiterSwipeAdapter(candidateArrayList)
        recyclerView.adapter = recruiterSwipeAdapter


        GlobalScope.launch(Dispatchers.IO) {
            var swipedLeft = db.collection("Recruiters").document(uid)
                .get().await()
                .toObject(Recruiter::class.java)!!.swipedLeft
            withContext(Dispatchers.Main) {
                EventChangeListener(uid, swipedLeft!!)
            }
        }

        val btnYes = binding.btnYes
        val btnNo = binding.btnNo

        btnYes.setOnClickListener { swipeYes() }
        btnNo.setOnClickListener { swipeNo() }

        // Initializing swipeGesture and passing it to itemTouchHelper
        // then we attach the itemTouchHelper to the recyclerView
        val swipeGesture = object : RecruiterSwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedCandidate = candidateArrayList[viewHolder.position]
                when (direction){
                    ItemTouchHelper.LEFT ->{
                        db.collection("Recruiters").document(uid)
                            .update("swipedLeft", FieldValue.arrayUnion(swipedCandidate.id.toString()))
                        swipeYes()
                    }
                    ItemTouchHelper.RIGHT->{
                        Log.d(TAG, candidateArrayList[viewHolder.position].id.toString())
                        db.collection("Recruiters").document(uid)
                            .update("swipedRight", FieldValue.arrayUnion(swipedCandidate.id.toString()))
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

    private fun EventChangeListener(uid : String, swipedLeft : List<String>) {
        db.collection("Candidates").orderBy("firstName", Query.Direction.ASCENDING).
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.d(TAG, "Firestore error: ${error.message.toString()}")
                    return
                }
                else {
                    for (dc : DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val documentCandidate = dc.document
                            if (swipedLeft.contains(documentCandidate.id)) {
                                Log.d(TAG, "document not shown")
                            }
                            else {
                                val candidate = Candidate(documentCandidate["firstName"].toString(), documentCandidate["lastName"].toString(), documentCandidate.id)
                                // candidateArrayList.add(dc.document.toObject(Candidate::class.java))
                                candidateArrayList.add(candidate)
                            }
                        }
                    }
                    recruiterSwipeAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getSwipedLeftArray(uid: String) : List<String> {
        var swipedLeft = listOf<String>()
        db.collection("Recruiters").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    swipedLeft = document.toObject(Recruiter::class.java)!!.swipedLeft!!
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return swipedLeft
    }

    fun swipeYes(){
        Log.i("MainAct", "Yes Clicked!")
        val index = mLayoutManager?.findFirstVisibleItemPosition()
        if (index != null) {
            //like the profile/job Logic

            //Removing the card and updating the adapter
            candidateArrayList.removeAt(index)
            recruiterSwipeAdapter.notifyItemRemoved(index)
        }
    }

    fun swipeNo(){
        Log.i("MainAct", "No Clicked")

        val index = mLayoutManager?.findFirstVisibleItemPosition()
        if (index != null) {
            //Dislike the profile/job Logic

            //Removing the card and updating the adapter
            candidateArrayList.removeAt(index)
            recruiterSwipeAdapter.notifyItemRemoved(index)
        }
    }
}