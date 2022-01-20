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

        auth = Firebase.auth

        val uid = auth.currentUser!!.uid

        val userType = intent.getStringExtra("userType").toString()

        binding.ibRoundArrow.setOnClickListener {
            binding.ibRoundArrow.isEnabled = false
            binding.ibRoundArrow.isClickable = false

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
            else {
                addBasicInfoToDatabase(firstName, lastName, email, location, userType, uid)
            }
        }
    }

    private fun addBasicInfoToDatabase(firstName: String, lastName: String, email: String, location: String, userType: String, uid: String) {
        val candidate = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "location" to location,
            "userType" to userType
        )

        db.collection("Candidates").document(uid)
            .set(candidate)
            .addOnSuccessListener {
                val intent = Intent(this@BasicInfoActivity, EducationSetupActivity::class.java)
                finish()
                startActivity(intent)
                binding.ibRoundArrow.isEnabled = true
                binding.ibRoundArrow.isClickable = true
            }.addOnFailureListener {
                binding.ibRoundArrow.isEnabled = true
                binding.ibRoundArrow.isClickable = true
                Toast.makeText(this, "Failed to upload to the database", Toast.LENGTH_SHORT).show()
            }
    }
}