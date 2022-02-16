package com.example.hiredswipe.candidate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.FragmentCandidateChatBinding
import com.example.hiredswipe.recruiter.RecruiterChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CandidateChatFragment : Fragment(R.layout.fragment_recruiter_chat) {

    private val TAG = "CandidateChatFragment"

    private var _binding: FragmentCandidateChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var recruiterChatList: ArrayList<RecruiterChatObject>
    private lateinit var adapter: CandidateChatAdapter

    private lateinit var mDbRef: DatabaseReference // Real-time database
    private val currUser = Firebase.auth.currentUser
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCandidateChatBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth // initialise auth
        val uid = auth.currentUser!!.uid // uid of current user

        mDbRef = FirebaseDatabase.getInstance().getReference()

        recruiterChatList = ArrayList()
        adapter = CandidateChatAdapter(requireContext(), recruiterChatList)

        userRecyclerView = binding.rvMain

        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter

        userRecyclerView.apply {
            addItemDecoration(DividerItemDecoration(this@CandidateChatFragment.context, DividerItemDecoration.VERTICAL))
        }

        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Inner code started  on Thread : " + Thread.currentThread().name + " making outer code suspend");
            var matched : List<String>? = null
            matched = db.collection("Candidates").document(uid)
                .get().await()
                .toObject(RecruiterChatObject::class.java)!!.matched
            Log.d(TAG, "Matched: " + matched.toString())
            if (matched != null && matched.isNotEmpty()) {
                matched.forEach {
                    Log.d(TAG, "Matched id: " + matched.toString())
                    val recruiter = db.collection("Recruiters").document(it)
                        .get().await()
                        .toObject(RecruiterChatObject::class.java)
                    recruiterChatList.add(recruiter!!)
                }
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "recruiterChatList: " + recruiterChatList.toString())
                    adapter.notifyDataSetChanged()
                }

            }
        }
        return view
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

//    private fun addView(userId : String) {
//        val userIdEntry : View = layoutInflater.inflate(R.layout.username_chat_entry, null, false)
//        userIdEntry.findViewById<TextView>(R.id.tvUserId).text = userId
//        binding.llChatHolder.addView(userIdEntry)
//    }

}