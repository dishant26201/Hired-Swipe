package com.example.hiredswipe.candidate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hiredswipe.databinding.ActivityCandidateHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hiredswipe.R


class CandidateHomeActivity : AppCompatActivity() {

    private val TAG = "CandidateHomeActivity"

    private lateinit var binding: ActivityCandidateHomeBinding // implementing view binding pt.1
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityCandidateHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth // initialise auth

        val uid = auth.currentUser!!.uid // uid of current user

        // NAVIGATION COMMENTS BY DHAIRY
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.bottomNavigationView.setupWithNavController(navController)




//        db.collection("Candidates").document(uid)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null) {
//                    binding.tvHello.setText("Hello " + document["firstName"].toString() + " " + document["lastName"].toString())
//                } else {
//                    Log.d(TAG, "No such document")
//                }
//
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting documents: ", exception)
//            }
    }
}