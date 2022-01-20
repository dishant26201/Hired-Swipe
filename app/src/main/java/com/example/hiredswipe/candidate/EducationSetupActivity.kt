package com.example.hiredswipe.candidate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.ActivityEducationSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EducationSetupActivity : AppCompatActivity() {

    private val TAG = "EducationSetupActivity"

    private lateinit var binding: ActivityEducationSetupBinding // implementing view binding pt.1
    private val db = Firebase.firestore // Cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityEducationSetupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val uid = auth.currentUser!!.uid

        binding.ibRoundArrow.isClickable = false
        binding.ibRoundArrow.isEnabled = false
        binding.ibRoundArrow.isVisible = false

        db.collection("Candidates").document(uid)
            .collection("Education")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val schoolName = document["schoolName"].toString()
                    val degreeType = document["degreeType"].toString()
                    val fieldOfStudy = document["fieldOfStudy"].toString()
                    val startYear = document["startYear"].toString()
                    val endYear = document["endYear"].toString()
                    val gpa = document["gpa"].toString()
                    val description = document["description"].toString()
                    val documentId = document.id.toString()
                    addView(schoolName, degreeType, fieldOfStudy, startYear, endYear, gpa, description, uid, documentId)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        if (binding.llEducationHolder.childCount > 0) {
            binding.ibRoundArrow.isClickable = true
            binding.ibRoundArrow.isEnabled = true
            binding.ibRoundArrow.isVisible = true

            binding.ibRoundArrow.setOnClickListener {
                val intent = Intent(this@EducationSetupActivity, WorkSetupActivity::class.java)
//            finish()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // get rid of extra layers/info from previous activities
                intent.putExtra("isUpdate", "no")
                startActivity(intent)
            }
        }
        else {
            binding.ibRoundArrow.isClickable = false
            binding.ibRoundArrow.isEnabled = false
            binding.ibRoundArrow.isVisible = false
        }

        binding.tvSkip.setOnClickListener {
            val intent = Intent(this@EducationSetupActivity, WorkSetupActivity::class.java)
//            finish()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // get rid of extra layers/info from previous activities
            intent.putExtra("isUpdate", "no")
            startActivity(intent)
        }

        binding.ibAddEntry.setOnClickListener {
            val intent = Intent(this@EducationSetupActivity, EducationEntryFormActivity::class.java)
//            finish()
            intent.putExtra("isUpdate", "no")
            startActivity(intent)
        }
    }

    private fun addView(schoolName: String, degreeType: String, fieldOfStudy: String, startYear: String, endYear: String, gpa: String, description: String, uid: String, documentId: String) {
        val educationEntry : View = layoutInflater.inflate(R.layout.education_entry, null, false)

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        educationEntry.findViewById<TextView>(R.id.tvSchoolNameEntry).setText(schoolName)
        educationEntry.findViewById<TextView>(R.id.tvDegreeFos).setText("$degreeType, $fieldOfStudy")
        educationEntry.findViewById<TextView>(R.id.tvDates).setText("$startYear - $endYear")

        if (gpa.isEmpty() || gpa.isBlank()) {
            educationEntry.findViewById<TextView>(R.id.tvGrade).setText("")
        }
        else {
            educationEntry.findViewById<TextView>(R.id.tvGrade).setText(gpa)
        }
        if (description.isEmpty() || description.isBlank()) {
            educationEntry.findViewById<TextView>(R.id.tvDescription).setText("")
        }
        else {
            educationEntry.findViewById<TextView>(R.id.tvDescription).setText(description)
        }

        params.setMargins(0, 48, 0, 0)
        educationEntry.layoutParams = params

        binding.llEducationHolder.addView(educationEntry)

        educationEntry.setOnClickListener {
            val intent = Intent(this@EducationSetupActivity, EducationEntryFormActivity::class.java)
            intent.putExtra("schoolName", schoolName)
            intent.putExtra("degreeType", degreeType)
            intent.putExtra("fieldOfStudy", fieldOfStudy)
            intent.putExtra("startYear", startYear)
            intent.putExtra("endYear", endYear)
            intent.putExtra("gpa", gpa)
            intent.putExtra("description", description)
            intent.putExtra("documentId", documentId)
            intent.putExtra("isUpdate", "yes")
            startActivity(intent)
        }

        educationEntry.findViewById<ImageButton>(R.id.ibDeleteEntry).setOnClickListener {
            it.isEnabled = false
            it.isClickable = false
            removeView(educationEntry, uid, documentId)
        }
    }

    private fun removeView(v: View?, uid: String, documentId: String) {
        db.collection("Candidates").document(uid)
            .collection("Education").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Document deleted from the database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                v!!.findViewById<ImageButton>(R.id.ibDeleteEntry).isEnabled = true
                v.findViewById<ImageButton>(R.id.ibDeleteEntry).isClickable = true
                Toast.makeText(this, "Couldn't delete document from the database", Toast.LENGTH_SHORT).show()
            }

        binding.llEducationHolder.removeView(v)
    }
}