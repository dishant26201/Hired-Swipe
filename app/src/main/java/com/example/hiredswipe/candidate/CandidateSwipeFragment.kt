package com.example.hiredswipe.candidate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.FragmentCandidateSwipeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList
import kotlin.system.exitProcess
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.Recruiter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CandidateSwipeFragment : Fragment(R.layout.fragment_candidate_swipe) {

    private val TAG = "CandidateSwipeFragment"
  
    // implementing view binding pt.1
    private var _binding: FragmentCandidateSwipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var jobArrayList : ArrayList<Recruiter> // arraylist to store job postings
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

        GlobalScope.launch(Dispatchers.IO) {
            val swipedLeft = db.collection("Candidates").document(uid)
                .get().await()
                .toObject(Recruiter::class.java)!!.swipedLeft
            val swipedRight = db.collection("Candidates").document(uid)
                .get().await()
                .toObject(Recruiter::class.java)!!.swipedRight
            EventChangeListener(uid, swipedLeft!!, swipedRight!!)
        }

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        val btnYes = binding.btnYes
        val btnNo = binding.btnNo

        // onClick listeners for both Yes, No butttons
        btnYes.setOnClickListener {
            val cardPos = getCardPos()  //getting the correct position for the card which is swiped
            swipeYes(cardPos, uid)  // calling swipeYes with the position
        }

        btnNo.setOnClickListener {
            val cardPos = getCardPos()  //getting the correct position for the card which is swiped
            swipeNo(cardPos)  // calling swipeNo with the position
        }

        // As we want both the buttons to be disabled when we are scrolling, we add a scrollListener
        // to our recyclerView object
        // scrollListener enables the buttons when were are in SCROLL_STATE_IDLE (not scrolling)
        // and disabled the buttons when we are in any other state
        val scrollListener = object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    Log.i(TAG, "In Scroll_State_Idle")
                    btnYes.isEnabled = true
                    btnYes.isClickable = true
                    btnYes.setImageResource(R.drawable.ic_thumb_up)

                    btnNo.isClickable = true
                    btnNo.isEnabled = true
                    btnNo.setImageResource(R.drawable.ic_thumb_down)
                }
                //if we are in any state other than idle (scrolling, slowing down) then we disable the buttons
                else{
                    btnYes.isEnabled = false
                    btnYes.isClickable = false
                    btnYes.setImageResource(R.drawable.ic_thumb_up_disabled)

                    btnNo.isClickable = false
                    btnNo.isEnabled = false
                    btnNo.setImageResource(R.drawable.ic_thumb_down_disabled)
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)

        // initializing swipeGesture and passing it to itemTouchHelper
        // then we attach the itemTouchHelper to the recyclerView
        val swipeGesture = object : CandidateSwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemPos = viewHolder.position
                val swipeRecruiter = jobArrayList[itemPos]
                when (direction){
                    ItemTouchHelper.LEFT -> {
                        db.collection("Candidates").document(uid)
                            .update("swipedLeft", FieldValue.arrayUnion(swipeRecruiter.id.toString()))
                            .addOnSuccessListener {
                                swipeNo(itemPos)
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "swipeLeftGesture: failed")
                            }
                    }
                    ItemTouchHelper.RIGHT-> {
                        Log.d(TAG, jobArrayList[viewHolder.position].id.toString())
                        db.collection("Candidates").document(uid)
                            .update("swipedRight", FieldValue.arrayUnion(swipeRecruiter.id.toString()))
                            .addOnSuccessListener {
                                swipeYes(itemPos, uid)
                            }
                            .addOnFailureListener {
                                Log.d(TAG, "swipeRightGesture: failed")
                            }
                    }
                }
                super.onSwiped(viewHolder, direction)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    private fun EventChangeListener(uid : String, swipedLeft : List<String>, swipedRight : List<String>) {
        db.collection("Recruiters").orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.d(TAG, "Firestore error: ${error.message.toString()}")
                        return
                    } else {
                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val documentRecruiter = dc.document
                                if (swipedLeft.contains(documentRecruiter.id)) {
                                    Log.d(TAG, "document not shown")
                                }
                                else {
                                    val recruiter = documentRecruiter.toObject(Recruiter::class.java)
                                    recruiter.id = documentRecruiter.id
                                    Log.d(TAG, "Candidate added to candidateArrayList: ${recruiter}")
                                    jobArrayList.add(recruiter)
                                }
                            }
                        }
                        candidateSwipeAdapter.notifyDataSetChanged()
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
            cardPos = 0
        }
        return cardPos
    }

    private fun swipeYes(index : Int, uid : String) {
        Log.i(TAG, "Yes Clicked!")
        if (index >= 0) {
            val currentRecruiter = jobArrayList[index]
            jobArrayList.removeAt(index) // removing the card and updating the adapter
            candidateSwipeAdapter.notifyItemRemoved(index)
            if (currentRecruiter.swipedRight!!.contains(uid.toString())) {
                Toast.makeText(context, "It's a match!", Toast.LENGTH_SHORT).show()
                db.collection("Candidates").document(uid)
                    .update("matched", FieldValue.arrayUnion(currentRecruiter.id.toString()))
                    .addOnSuccessListener {
                        db.collection("Recruiters").document(currentRecruiter.id.toString())
                            .update("matched", FieldValue.arrayUnion(uid))
                    }
            }
//            db.collection("Recruiters").document(currentRecruiter.id!!)
//                .get()
//                .addOnSuccessListener { document ->
//                    Log.d(TAG, "currentRecruiter ID: ${document.id}")
//                    Log.d(TAG, "currentRecruiter from DB: ${document.data}")
//                    val swipedRight = document.data!!["swipedRight"] as List<*>?
//                    Log.d(TAG, "currentRecruiter swipedRight: $swipedRight")
//                    Toast.makeText(context, "It's a match!", Toast.LENGTH_SHORT).show()
//
//                    if (swipedRight!!.contains(uid.toString())) {
//                        Toast.makeText(context, "It's a match!", Toast.LENGTH_SHORT).show()
//                    }
//                    jobArrayList.removeAt(index) // removing the card and updating the adapter
//                    candidateSwipeAdapter.notifyItemRemoved(index)
//
//                    Log.d(TAG, currentRecruiter.name.toString())
//                    Log.d(TAG, currentRecruiter.id.toString())
//
//                }
        }
        else {
            Log.i(TAG, "Error invalid pos: $index")
        }
    }

    private fun swipeNo(index : Int) {
        Log.i(TAG, "No Clicked")
        if (index >= 0) {
            jobArrayList.removeAt(index) // removing the card and updating the adapter
            candidateSwipeAdapter.notifyItemRemoved(index)
        }
        // if pos is null or invalid
        else{
           Log.i(TAG, "Error invalid pos: $index")
        }
    }
}