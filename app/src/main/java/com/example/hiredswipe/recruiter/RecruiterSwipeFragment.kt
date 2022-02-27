package com.example.hiredswipe.recruiter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.FragmentRecruiterSwipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList
import kotlin.system.exitProcess
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.Candidate
import com.example.hiredswipe.Recruiter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecruiterSwipeFragment : Fragment(R.layout.fragment_recruiter_swipe) {

    private val TAG = "RecruiterSwipeFragment"

    // implementing view binding pt.1
    private var _binding: FragmentRecruiterSwipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var candidateArrayList : ArrayList<Candidate> // arraylist to store job postings
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
        recyclerView = binding.rvMainRecruiter
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setHasFixedSize(true) // setHasFixedSize can be used for optimization purposes if we know that the list/rv is constant in size, and is not affected by the adapters size
        candidateArrayList = arrayListOf()
        recruiterSwipeAdapter = RecruiterSwipeAdapter(requireActivity().applicationContext, candidateArrayList)
        recyclerView.adapter = recruiterSwipeAdapter

        GlobalScope.launch(Dispatchers.IO) {
            var swipedLeft : List<String>? = null
            var swipedRight : List<String>? = null
            swipedLeft = db.collection("Recruiters").document(uid)
                .get().await()
                .toObject(Recruiter::class.java)!!.swipedLeft
            swipedRight = db.collection("Recruiters").document(uid)
                .get().await()
                .toObject(Recruiter::class.java)!!.swipedRight
            EventChangeListener(swipedLeft!!, swipedRight!!)
        }

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        recruiterSwipeAdapter.setOnClickListener(object : RecruiterSwipeAdapter.OnClickListener {
            override fun onYesClick() {
                swipeYes()
                Log.d(TAG, "swipeYes in $TAG")
            }

            override fun onNoClick() {
                swipeNo()
                Log.d(TAG, "swipeNo in $TAG")
            }
        })

//        // initializing swipeGesture and passing it to itemTouchHelper
//        // then we attach the itemTouchHelper to the recyclerView
        val swipeGesture = object : RecruiterSwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemPos = viewHolder.position
                val swipedCandidate = candidateArrayList[itemPos]
                when (direction){
                    ItemTouchHelper.LEFT -> {
                        swipeNo()
                    }
                    ItemTouchHelper.RIGHT-> {
                        Log.d(TAG, candidateArrayList[viewHolder.position].id.toString())
                        swipeYes()
                    }
                }
                super.onSwiped(viewHolder, direction)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        return view
    }

    private fun EventChangeListener(swipedLeft : List<String>, swipedRight : List<String>) {
        db.collection("Candidates").orderBy("firstName", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.d(TAG, "Firestore error: ${error.message.toString()}")
                        return
                    }
                    else {
                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val documentCandidate = dc.document
                                if (swipedLeft.contains(documentCandidate.id) || swipedRight.contains(documentCandidate.id)) {
                                    Log.d(TAG, "document not shown")
                                }
                                else {
                                    val candidate = documentCandidate.toObject(Candidate::class.java)
                                    candidate.id = documentCandidate.id
                                    Log.d(TAG, "Candidate added to candidateArrayList: ${candidate}")
                                    candidateArrayList.add(candidate)
                                }
                            }
                        }
                        recruiterSwipeAdapter.notifyDataSetChanged()
                    }
                }
            })
    }

    private fun getCardPos() : Int {
        // if layoutManager is not defined yet, we exit with status code -1
        if (mLayoutManager == null) {
            Log.i(TAG, "Error, mLayoutManager is not defined")
            exitProcess(-1)
        }
        var cardPos = mLayoutManager!!.findFirstCompletelyVisibleItemPosition()
        //if cardPos = -1, this mean no view is completely visible and we have to align them first
        if (cardPos == -1) {
            Log.i(TAG, "Error, could not find a completelyVisibleItem")
            Toast.makeText(context, "Error, could not find a completelyVisibleItem", Toast.LENGTH_SHORT).show()
            cardPos = 0
        }
        return cardPos
    }

    private fun swipeYes() {
        Log.i(TAG, "Yes Clicked!")

        val uid = auth.currentUser!!.uid
        val index = getCardPos()
        val swipedCandidate = candidateArrayList[index]

        if (index >= 0) {
            db.collection("Recruiters").document(uid)
                .update("swipedRight", FieldValue.arrayUnion(swipedCandidate.id.toString()))
                .addOnSuccessListener {
                    candidateArrayList.removeAt(index) // removing the card and updating the adapter
                    recruiterSwipeAdapter.notifyItemRemoved(index)
                    if (swipedCandidate.swipedRight!!.contains(uid.toString())) {
                        Toast.makeText(context, "It's a match!", Toast.LENGTH_SHORT).show()
                        db.collection("Recruiters").document(uid)
                            .update("matched", FieldValue.arrayUnion(swipedCandidate.id.toString()))
                            .addOnSuccessListener {
                                db.collection("Candidates").document(swipedCandidate.id.toString())
                                    .update("matched", FieldValue.arrayUnion(uid))
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "swipeRightGesture: failed")
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "swipeRightGesture: failed")
                }
        }
        else {
            Log.i(TAG, "Error invalid pos: $index")
        }
    }

    private fun swipeNo() {
        Log.i(TAG, "No Clicked")

        val uid = auth.currentUser!!.uid
        val index = getCardPos()
        val swipedCandidate = candidateArrayList[index]

        if (index >= 0) {
            db.collection("Recruiters").document(uid)
                .update("swipedLeft", FieldValue.arrayUnion(swipedCandidate.id.toString()))
                .addOnSuccessListener {
                    candidateArrayList.removeAt(index) // removing the card and updating the adapter
                    recruiterSwipeAdapter.notifyItemRemoved(index)
                }
                .addOnFailureListener {
                    Log.d(TAG, "swipeLeftGesture: failed")
                }
        }
        // if pos is null or invalid
        else{
            Log.i(TAG, "Error invalid pos: $index")
        }
    }
}