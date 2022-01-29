package com.example.hiredswipe.recruiter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
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
import kotlin.system.exitProcess


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

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        val btnYes = binding.btnYes
        val btnNo = binding.btnNo

        btnYes.setOnClickListener {

            val cardPos = getCardPos()  //getting the correct position for the card which is swiped
            swipeYes(cardPos)  // calling swipeYes with the position
        }
        btnNo.setOnClickListener {

            val cardPos = getCardPos()  //getting the correct position for the card which is swiped
            swipeNo(cardPos)  // calling swipeNo with the position
        }

        //As we want both the buttons to be disabled when we are scrolling, we add a scrollListener
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
        val swipeGesture = object : RecruiterSwipeGesture(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedCandidate = candidateArrayList[viewHolder.position]
                val itemPos = viewHolder.position
                when (direction){
                    ItemTouchHelper.LEFT ->{
                        db.collection("Recruiters").document(uid)
                            .update("swipedLeft", FieldValue.arrayUnion(swipedCandidate.id.toString()))
                        swipeYes(itemPos)
                    }
                    ItemTouchHelper.RIGHT->{
                        Log.d(TAG, candidateArrayList[viewHolder.position].id.toString())
                        db.collection("Recruiters").document(uid)
                            .update("swipedRight", FieldValue.arrayUnion(swipedCandidate.id.toString()))
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

    fun swipeYes(index: Int){
        Log.i("MainAct", "Yes Clicked!")

        if (index >= 0) {
            //like the profile/job Logic

            //Removing the card and updating the adapter
            candidateArrayList.removeAt(index)
            recruiterSwipeAdapter.notifyItemRemoved(index)
        }
        else{
            Log.i("MainAct", "Error invalid pos: $index")
        }
    }

    fun swipeNo(index: Int){
        Log.i("MainAct", "No Clicked")


        if (index >= 0) {
            //Dislike the profile/job Logic

            //Removing the card and updating the adapter
            candidateArrayList.removeAt(index)
            recruiterSwipeAdapter.notifyItemRemoved(index)
        }
        else{
            Log.i("MainAct", "Error invalid pos: $index")
        }
    }
}