package com.example.hiredswipe.candidate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hiredswipe.databinding.ActivityWorkEntryFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WorkEntryFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkEntryFormBinding // implementing view binding pt.1
    private val db = Firebase.firestore // Cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityWorkEntryFormBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth // initialise auth

        val uid = auth.currentUser!!.uid // uid of current user

        val isUpdate = intent.getStringExtra("isUpdate").toString() // retrieve isUpdate flag from the previous activity. If "yes" then we are just updating an existing entry, else we are creating a new entry

        when (isUpdate) {
            "yes" -> { // an existing entry shall be updated
                // display existing data from the database (sent from the previous activity) in the input fields
                binding.etJobTitle.setText(intent.getStringExtra("jobTitle"))
                binding.etCompany.setText(intent.getStringExtra("companyName"))
                binding.etLocation.setText(intent.getStringExtra("companyLocation"))
                binding.etEmploymentType.setText(intent.getStringExtra("employmentType"))
                binding.etStartYear.setText(intent.getStringExtra("startYear"))
                binding.etEndYear.setText(intent.getStringExtra("endYear"))
                binding.etDescription.setText(intent.getStringExtra("description"))

                // when the save button is clicked
                binding.btnSave.setOnClickListener {
                    // disable the "save" button
                    binding.btnSave.isEnabled = false
                    binding.btnSave.isClickable = false

                    // capture all the data from the input fields/EditTexts
                    val jobTitle = binding.etJobTitle.text.toString()
                    val companyName = binding.etCompany.text.toString()
                    val companyLocation = binding.etLocation.text.toString()
                    val employmentType = binding.etEmploymentType.text.toString()
                    val startYear = binding.etStartYear.text.toString()
                    val endYear = binding.etEndYear.text.toString()
                    val description = binding.etDescription.text.toString()
                    val documentId = intent.getStringExtra("documentId").toString() // pass the documentId so that the specific document can be accessed from a collection of documents/workEntries

                    // jobTitle, companyName, companyLocation, employmentType, startYear, and endYear fields cannot be empty. Description is optional
                    if (binding.etJobTitle.text.isEmpty()) {
                        binding.etJobTitle.error = "You cannot leave this field empty"
                    }
                    if (binding.etCompany.text.isEmpty()) {
                        binding.etCompany.error = "You cannot leave this field empty"
                    }
                    if (binding.etLocation.text.isEmpty()) {
                        binding.etLocation.error = "You cannot leave this field empty"
                    }
                    if (binding.etEmploymentType.text.isEmpty()) {
                        binding.etEmploymentType.error = "You cannot leave this field empty"
                    }
                    if (binding.etStartYear.text.isEmpty()) {
                        binding.etStartYear.error = "You cannot leave this field empty"
                    }
                    if (binding.etEndYear.text.isEmpty()) {
                        binding.etEndYear.error = "You cannot leave this field empty"
                    }
                    else {
                        // call function to update an entry/document in the database
                        updateDocument(
                            jobTitle,
                            companyName,
                            companyLocation,
                            employmentType,
                            startYear,
                            endYear,
                            description,
                            uid,
                            documentId
                        )
                    }
                }
            }
            "no" -> { // a new entry shall be created
                // when the save button is clicked
                binding.btnSave.setOnClickListener {
                    // disable the "save" button
                    binding.btnSave.isEnabled = false
                    binding.btnSave.isClickable = false

                    // capture all the data from the input fields/EditTexts
                    val jobTitle = binding.etJobTitle.text.toString()
                    val companyName = binding.etCompany.text.toString()
                    val companyLocation = binding.etLocation.text.toString()
                    val employmentType = binding.etEmploymentType.text.toString()
                    val startYear = binding.etStartYear.text.toString()
                    val endYear = binding.etEndYear.text.toString()
                    val description = binding.etDescription.text.toString()

                    // jobTitle, companyName, companyLocation, employmentType, startYear, and endYear fields cannot be empty. Description is optional
                    if (binding.etJobTitle.text.isEmpty()) {
                        binding.etJobTitle.error = "You cannot leave this field empty"
                    }
                    if (binding.etCompany.text.isEmpty()) {
                        binding.etCompany.error = "You cannot leave this field empty"
                    }
                    if (binding.etLocation.text.isEmpty()) {
                        binding.etLocation.error = "You cannot leave this field empty"
                    }
                    if (binding.etEmploymentType.text.isEmpty()) {
                        binding.etEmploymentType.error = "You cannot leave this field empty"
                    }
                    if (binding.etStartYear.text.isEmpty()) {
                        binding.etStartYear.error = "You cannot leave this field empty"
                    }
                    if (binding.etEndYear.text.isEmpty()) {
                        binding.etEndYear.error = "You cannot leave this field empty"
                    }
                    else {
                        // call function to update an entry/document in the database
                        addWorkToDatabase(jobTitle, companyName, companyLocation, employmentType, startYear, endYear, description, uid)
                    }
                }
            }
        }

        // when the "close" button is clicked
        binding.ibCloseForm.setOnClickListener {
            finish() // kill the activity. No changes saved.
        }
    }

    // function to update an entry/document in the database
    private fun addWorkToDatabase(jobTitle: String, companyName: String, companyLocation: String, employmentType: String, startYear: String, endYear: String, description: String, uid: String) {
        // create a "work entry" object sort of thing
        val workEntry = hashMapOf(
            "jobTitle" to jobTitle,
            "companyName" to companyName,
            "companyLocation" to companyLocation,
            "employmentType" to employmentType,
            "startYear" to startYear,
            "endYear" to endYear,
            "description" to description,
        )

        // add workEntry to the database
        db.collection("Candidates").document(uid)
            .collection("Work") // sub-collection "Work"
            .add(workEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Data entered in the database", Toast.LENGTH_SHORT).show()
                // move to previous activity and kill this activity
                val intent = Intent(this@WorkEntryFormActivity, WorkSetupActivity::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener { // if couldn't add to the database
                // enable the "save" button
                binding.btnSave.isEnabled = true
                binding.btnSave.isClickable = true
                Toast.makeText(this, "Failed to upload to the database", Toast.LENGTH_SHORT).show()
            }
    }

    // update the workEntry in the database
    private fun updateDocument(jobTitle: String, companyName: String, companyLocation: String, employmentType: String, startYear: String, endYear: String, description: String, uid: String, documentId: String) {
        // create a "work entry" object sort of thing
        val workEntry = hashMapOf(
            "jobTitle" to jobTitle,
            "companyName" to companyName,
            "companyLocation" to companyLocation,
            "employmentType" to employmentType,
            "startYear" to startYear,
            "endYear" to endYear,
            "description" to description,
        )

        // update(set) workEntry in the database
        db.collection("Candidates").document(uid)
            .collection("Work").document(documentId) // particular document/workEntry, accessed by the documentId
            .set(workEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show()
                // move to previous activity and kill this activity
                val intent = Intent(this@WorkEntryFormActivity, WorkSetupActivity::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener {
                // enable the "save" button
                binding.btnSave.isEnabled = true
                binding.btnSave.isClickable = true
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
            }
    }
}