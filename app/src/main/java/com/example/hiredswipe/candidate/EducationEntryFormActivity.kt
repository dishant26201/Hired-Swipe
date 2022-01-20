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

        auth = Firebase.auth

        val uid = auth.currentUser!!.uid

        val isUpdate = intent.getStringExtra("isUpdate").toString()

        when (isUpdate) {
            "yes" -> {
                binding.etSchoolName.setText(intent.getStringExtra("schoolName"))
                binding.etDegreeType.setText(intent.getStringExtra("degreeType"))
                binding.etFieldOfStudy.setText(intent.getStringExtra("fieldOfStudy"))
                binding.etStartYear.setText(intent.getStringExtra("startYear"))
                binding.etEndYear.setText(intent.getStringExtra("endYear"))
                binding.etGpa.setText(intent.getStringExtra("gpa"))
                binding.etDescription.setText(intent.getStringExtra("description"))

                binding.btnSave.setOnClickListener {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.isClickable = false

                    val schoolName = binding.etSchoolName.text.toString()
                    val degreeType = binding.etDegreeType.text.toString()
                    val fieldOfStudy = binding.etFieldOfStudy.text.toString()
                    val startYear = binding.etStartYear.text.toString()
                    val endYear = binding.etEndYear.text.toString()
                    val gpa = binding.etGpa.text.toString()
                    val description = binding.etDescription.text.toString()
                    val documentId = intent.getStringExtra("documentId").toString()

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
            "no" -> {
                binding.btnSave.setOnClickListener {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.isClickable = false

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
                        addEducationToDatabase(schoolName, degreeType, fieldOfStudy, startYear, endYear, gpa, description, uid)
                    }
                }
            }
        }

        binding.ibCloseForm.setOnClickListener {
            finish();
        }
    }

    private fun addEducationToDatabase(schoolName: String, degreeType: String, fieldOfStudy: String, startYear: String, endYear: String, gpa: String, description: String, uid: String) {
        val educationEntry = hashMapOf(
            "schoolName" to schoolName,
            "degreeType" to degreeType,
            "fieldOfStudy" to fieldOfStudy,
            "startYear" to startYear,
            "endYear" to endYear,
            "gpa" to gpa,
            "description" to description,
        )
        db.collection("Candidates").document(uid)
            .collection("Education")
            .add(educationEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Data entered in the database", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EducationEntryFormActivity, EducationSetupActivity::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener {
                binding.btnSave.isEnabled = true
                binding.btnSave.isClickable = true
                Toast.makeText(this, "Failed to upload to the database", Toast.LENGTH_SHORT).show()
            }

        // possible regex for custom document id
//            .document(schoolName.replace("\\s".toRegex(), "")
//                    + degreeType.replace("\\s".toRegex(), "")
//                    + fieldOfStudy.replace("\\s".toRegex(), ""))
    }

    private fun updateDocument(schoolName: String, degreeType: String, fieldOfStudy: String, startYear: String, endYear: String, gpa: String, description: String, uid: String, documentId: String) {
        val educationEntry = hashMapOf(
            "schoolName" to schoolName,
            "degreeType" to degreeType,
            "fieldOfStudy" to fieldOfStudy,
            "startYear" to startYear,
            "endYear" to endYear,
            "gpa" to gpa,
            "description" to description,
        )
        db.collection("Candidates").document(uid)
            .collection("Education").document(documentId)
            .set(educationEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EducationEntryFormActivity, EducationSetupActivity::class.java)
                finish()
                startActivity(intent)
            }.addOnFailureListener {
                binding.btnSave.isEnabled = true
                binding.btnSave.isClickable = true
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
            }
    }
}