package com.example.hiredswipe.recruiter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hiredswipe.candidate.EducationSetupActivity
import com.example.hiredswipe.databinding.ActivityRecruiterBasicInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RecruiterBasicInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecruiterBasicInfoBinding // implementing view binding pt.1
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityRecruiterBasicInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth // initialise auth
        val uid = auth.currentUser!!.uid // uid of current user

        val userType = intent.getStringExtra("userType").toString() // get userType from last activity (candidate or recruiter)

        // when the "next" button is clicked
        binding.ibRoundArrow.setOnClickListener {
            // disable "next" button
            binding.ibRoundArrow.isEnabled = false
            binding.ibRoundArrow.isClickable = false

            // get data from input text fields (firstName, lastName, email, and location)
            val name = binding.etNameRecruiter.text.toString()

            // firstName, lastName, email, and location fields cannot be empty
            if (binding.etNameRecruiter.text.isEmpty()) {
                binding.etNameRecruiter.error = "You cannot leave this field empty"
            }
            else { // if the fields are not empty, add info to database
                addBasicInfoToDatabase(name, userType, uid)
            }
        }
    }

    // function to add basic info i.e. candidate entry, to the database
    private fun addBasicInfoToDatabase(name: String, userType: String, uid: String) {
        // create a "recruiter" object sort of thing
        val recruiter = hashMapOf(
            "name" to name,
            "userType" to userType
        )

        // adding the "candidate" to the database
        db.collection("Recruiters").document(uid)
            .set(recruiter)
            .addOnSuccessListener {
                // move to next activity and kill this activity
                val intent = Intent(this@RecruiterBasicInfoActivity, RecruiterHomeActivity::class.java)
                finish()
                startActivity(intent)
                // enable next button (can be omitted I guess)
                binding.ibRoundArrow.isEnabled = true
                binding.ibRoundArrow.isClickable = true
            }.addOnFailureListener {
                // enable next button
                binding.ibRoundArrow.isEnabled = true
                binding.ibRoundArrow.isClickable = true
                Toast.makeText(this, "Failed to upload to the database", Toast.LENGTH_SHORT).show()
            }
    }
}