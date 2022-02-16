package com.example.hiredswipe.recruiter

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
import com.example.hiredswipe.databinding.FragmentRecruiterChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class RecruiterChatFragment : Fragment(R.layout.fragment_recruiter_chat) {

    private val TAG = "RecruiterChatFragment"

    private var _binding: FragmentRecruiterChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var candidateChatList: ArrayList<CandidateChatObject>
    private lateinit var adapter: RecruiterChatAdapter

    private lateinit var mDbRef: DatabaseReference // Real-time database
    private val currUser = Firebase.auth.currentUser
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRecruiterChatBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth // initialise auth
        val uid = auth.currentUser!!.uid // uid of current user

        mDbRef = FirebaseDatabase.getInstance().getReference()

        candidateChatList = ArrayList()
        adapter = RecruiterChatAdapter(requireContext(), candidateChatList)

        userRecyclerView = binding.rvMain

        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.adapter = adapter

        userRecyclerView.apply {
            addItemDecoration(DividerItemDecoration(this@RecruiterChatFragment.context, DividerItemDecoration.VERTICAL))
        }

        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Inner code started  on Thread : " + Thread.currentThread().name + " making outer code suspend");
            var matched : List<String>? = null
            matched = db.collection("Recruiters").document(uid)
                .get().await()
                .toObject(CandidateChatObject::class.java)!!.matched
            Log.d(TAG, "Matched: " + matched.toString())
            if (matched != null && matched.isNotEmpty()) {
                matched.forEach {
                    Log.d(TAG, "Matched id: " + matched.toString())
                    val candidate = db.collection("Candidates").document(it)
                        .get().await()
                        .toObject(CandidateChatObject::class.java)
                    candidateChatList.add(candidate!!)
                }
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "candidateChatList: " + candidateChatList.toString())
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