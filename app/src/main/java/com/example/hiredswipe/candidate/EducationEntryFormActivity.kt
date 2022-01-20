package com.example.hiredswipe.candidate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hiredswipe.databinding.ActivityEducationEntryFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EducationEntryFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEducationEntryFormBinding // implementing view binding pt.1
    private val db = Firebase.firestore // Cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityEducationEntryFormBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth // initialise auth

        val uid = auth.currentUser!!.uid // uid of current user

        val isUpdate = intent.getStringExtra("isUpdate").toString() // retrieve isUpdate flag from the previous activity. If "yes" then we are just updating an existing entry, else we are creating a new entry

        when (isUpdate) {
            "yes" -> { // an existing entry shall be updated
                // display existing data from the database (sent from the previous activity) in the input fields
                binding.etSchoolName.setText(intent.getStringExtra("schoolName"))
                binding.etDegreeType.setText(intent.getStringExtra("degreeType"))
                binding.etFieldOfStudy.setText(intent.getStringExtra("fieldOfStudy"))
                binding.etStartYear.setText(intent.getStringExtra("startYear"))
                binding.etEndYear.setText(intent.getStringExtra("endYear"))
                binding.etGpa.setText(intent.getStringExtra("gpa"))
                binding.etDescription.setText(intent.getStringExtra("description"))

                // when the save button is clicked
                binding.btnSave.setOnClickListener {
                    // disable the "save" button
                    binding.btnSave.isEnabled = false
                    binding.btnSave.isClickable = false

                    // capture all the data from the input fields/EditTexts
                    val schoolName = binding.etSchoolName.text.toString()
                    val degreeType = binding.etDegreeType.text.toString()
                    val fieldOfStudy = binding.etFieldOfStudy.text.toString()
                    val startYear = binding.etStartYear.text.toString()
                    val endYear = binding.etEndYear.text.toString()
                    val gpa = binding.etGpa.text.toString()
                    val description = binding.etDescription.text.toString()
                    val documentId = intent.getStringExtra("documentId").toString() // pass the documentId so that the specific document can be accessed from a collection of documents/educationEntries

                    // schoolName, degreeType, fieldOfStudy, startYear, and endYear fields cannot be empty. GPA and description is optional
                    if (binding.etSchoolName.text.isEmpty()) {
                        binding.etSchoolName.error = "You cannot leave this field empty"
                    }
                    if (binding.etDegreeType.text.isEmpty()) {
                        binding.etDegreeType.error = "You cannot leave this field empty"
                    }
                    if (binding.etFieldOfStudy.text.isEmpty()) {
                        binding.etFieldOfStudy.error = "You cannot leave this field empty"
                    }
                    if (binding.etStartYear.text.isEmpty()) {
                        binding.etStartYear.error = "You cannot leave this field empty"
                    }
                    if (binding.etEndYear.text.isEmpty()) {
                        binding.etEndYear.error = "You cannot leave this field empty"
                    } else {
                        // call function to update an entry/document in the database
                        updateDocument(
                            schoolName,
                            degreeType,
                            fieldOfStudy,
                            startYear,
                            endYear,
                            gpa,
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
                    val schoolName = binding.etSchoolName.text.toString()
                    val degreeType = binding.etDegreeType.text.toString()
                    val fieldOfStudy = binding.etFieldOfStudy.text.toString()
                    val startYear = binding.etStartYear.text.toString()
                    val endYear = binding.etEndYear.text.toString()
                    val gpa = binding.etGpa.text.toString()
                    val description = binding.etDescription.text.toString()

                    // schoolName, degreeType, fieldOfStudy, startYear, and endYear fields cannot be empty. GPA and description is optional
                    if (binding.etSchoolName.text.isEmpty()) {
                        binding.etSchoolName.error = "You cannot leave this field empty"
                    }
                    if (binding.etDegreeType.text.isEmpty()) {
                        binding.etDegreeType.error = "You cannot leave this field empty"
                    }
                    if (binding.etFieldOfStudy.text.isEmpty()) {
                        binding.etFieldOfStudy.error = "You cannot leave this field empty"
                    }
                    if (binding.etStartYear.text.isEmpty()) {
                        binding.etStartYear.error = "You cannot leave this field empty"
                    }
                    if (binding.etEndYear.text.isEmpty()) {
                        binding.etEndYear.error = "You cannot leave this field empty"
                    }
                    else {
                        // call function to update an entry/document in the database
                        addEducationToDatabase(schoolName, degreeType, fieldOfStudy, startYear, endYear, gpa, description, uid)
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
    private fun addEducationToDatabase(schoolName: String, degreeType: String, fieldOfStudy: String, startYear: String, endYear: String, gpa: String, description: String, uid: String) {
        // create an "education entry" object sort of thing
        val educationEntry = hashMapOf(
            "schoolName" to schoolName,
            "degreeType" to degreeType,
            "fieldOfStudy" to fieldOfStudy,
            "startYear" to startYear,
            "endYear" to endYear,
            "gpa" to gpa,
            "description" to description,
        )

        // add educationEntry to the database
        db.collection("Candidates").document(uid)
            .collection("Education") // sub-collection "Education"
            .add(educationEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Data entered in the database", Toast.LENGTH_SHORT).show()
                // move to previous activity and kill this activity
                val intent = Intent(this@EducationEntryFormActivity, EducationSetupActivity::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener { // if couldn't add to the database
                // enable the "save" button
                binding.btnSave.isEnabled = true
                binding.btnSave.isClickable = true
                Toast.makeText(this, "Failed to upload to the database", Toast.LENGTH_SHORT).show()
            }

        // possible regex for custom document id. DO NOT DELETE
//            .document(schoolName.replace("\\s".toRegex(), "")
//                    + degreeType.replace("\\s".toRegex(), "")
//                    + fieldOfStudy.replace("\\s".toRegex(), ""))
    }

    // update the educationEntry in the database
    private fun updateDocument(schoolName: String, degreeType: String, fieldOfStudy: String, startYear: String, endYear: String, gpa: String, description: String, uid: String, documentId: String) {
        // create an "education entry" object sort of thing
        val educationEntry = hashMapOf(
            "schoolName" to schoolName,
            "degreeType" to degreeType,
            "fieldOfStudy" to fieldOfStudy,
            "startYear" to startYear,
            "endYear" to endYear,
            "gpa" to gpa,
            "description" to description,
        )

        // update(set) educationEntry in the database
        db.collection("Candidates").document(uid)
            .collection("Education").document(documentId) // particular document/educationEntry, accessed by the documentId
            .set(educationEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show()
                // move to previous activity and kill this activity
                val intent = Intent(this@EducationEntryFormActivity, EducationSetupActivity::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener { // if couldn't update in the database
                // enable the "save" button
                binding.btnSave.isEnabled = true
                binding.btnSave.isClickable = true
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
            }
    }
}