package com.example.hiredswipe.candidate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hiredswipe.R
import com.example.hiredswipe.WelcomeScreenActivity
import com.example.hiredswipe.databinding.ActivityBasicInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BasicInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicInfoBinding // implementing view binding pt.1
    private val db = Firebase.firestore // cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityBasicInfoBinding.inflate(layoutInflater)
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
            val firstName = binding.etFirstNameCandidate.text.toString()
            val lastName = binding.etLastNameCandidate.text.toString()
            val email = binding.etEmailCandidate.text.toString().trim { it <= ' ' }
            val location = binding.etLocationCandidate.text.toString()

            // firstName, lastName, email, and location fields cannot be empty
            if (binding.etFirstNameCandidate.text.isEmpty()) {
                binding.etFirstNameCandidate.error = "You cannot leave this field empty"
            }
            if (binding.etLastNameCandidate.text.isEmpty()) {
                binding.etLastNameCandidate.error = "You cannot leave this field empty"
            }
            if (binding.etEmailCandidate.text.isEmpty()) {
                binding.etEmailCandidate.error = "You cannot leave this field empty"
            }
            if (binding.etLocationCandidate.text.isEmpty()) {
                binding.etLocationCandidate.error = "You cannot leave this field empty"
            }
            else { // if the fields are not empty, add info to database
                addBasicInfoToDatabase(firstName, lastName, email, location, userType, uid)
            }
        }
    }

    // function to add basic info i.e. candidate entry, to the database
    private fun addBasicInfoToDatabase(firstName: String, lastName: String, email: String, location: String, userType: String, uid: String) {
        // create a "candidate" object sort of thing
        val candidate = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "location" to location,
            "userType" to userType
        )

        // adding the "candidate" to the database
        db.collection("Candidates").document(uid)
            .set(candidate)
            .addOnSuccessListener {
                // move to next activity and kill this activity
                val intent = Intent(this@BasicInfoActivity, EducationSetupActivity::class.java)
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