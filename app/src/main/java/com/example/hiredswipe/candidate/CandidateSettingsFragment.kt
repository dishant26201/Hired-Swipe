package com.example.hiredswipe.candidate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hiredswipe.R
import com.example.hiredswipe.SignInActivity
import com.example.hiredswipe.databinding.FragmentCandidateSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CandidateSettingsFragment : Fragment(R.layout.fragment_candidate_settings) {

    private val TAG = "CandidateSettingsFragment"

    private var _binding: FragmentCandidateSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mDbRef: DatabaseReference // Real-time database
    private val currUser = Firebase.auth.currentUser
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCandidateSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth // initialise auth
        val uid = auth.currentUser!!.uid // uid of current user

        binding.btnLogOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this.activity, SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish();
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}