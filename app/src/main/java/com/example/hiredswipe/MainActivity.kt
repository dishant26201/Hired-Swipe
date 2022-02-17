package com.example.hiredswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.hiredswipe.candidate.CandidateHomeActivity
import com.example.hiredswipe.databinding.ActivityMainBinding
import com.example.hiredswipe.databinding.SplashScreenBinding
import com.example.hiredswipe.recruiter.RecruiterHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private val db = Firebase.firestore // cloud firestore

    //implementing view biendings for MainActivity screen and Splash screen
    private lateinit var splashScreenBinding: SplashScreenBinding
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        splashScreenBinding = SplashScreenBinding.inflate(layoutInflater)
        var view = splashScreenBinding.root
        setContentView(view)

        //if user is logged in, we move to the next Activity
        if(currentUser != null){

            checkUserType(currentUser)
        }
        else{
            // implementing view binding pt.2
            binding = ActivityMainBinding.inflate(layoutInflater)
            view = binding.root
            setContentView(view)
            // when the register/join-now button is clicked
            binding.btnJoinNow.setOnClickListener {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            // when the sign-in button is clicked
            binding.btnSignIn.setOnClickListener {
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun checkUserType(user: FirebaseUser?){
        db.collection("Candidates").document(user!!.uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    updateUICandidate(user)
                }
                else {
                    db.collection("Recruiters").document(user.uid)
                        .get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    updateUIRecruiter(user)
                                }
                                else {
                                    // We come in this else block when user exists, but is not a candidate or a recruiter
                                    Log.w(TAG, "I AM HERE")
                                    val intent = Intent(this@MainActivity, SelectUserTypeActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // get rid of extra layers/info from previous activities
                                    startActivity(intent) // start next activity
                                    finish() // finish current activity
                                }
                            } else {
//                                                    Log.d("TAG", "Error: ", task.exception)
                                Log.d(TAG, "Error: HERE")
                            }
                        }
                }
            }
            else {
                Log.d(TAG, "Error: ", task.exception)
            }
        }
    }

    private fun updateUIRecruiter(user: FirebaseUser?) {
        // move to the next activity and kill this one
        val intent = Intent(this@MainActivity, RecruiterHomeActivity::class.java)
        startActivity(intent) // start next activity
        finish() // finish current activity
    }

    private fun updateUICandidate(user: FirebaseUser?) {
        // move to the next activity and kill this one
        val intent = Intent(this@MainActivity, CandidateHomeActivity::class.java)
        startActivity(intent) // start next activity
        finish() // finish current activity
    }
}