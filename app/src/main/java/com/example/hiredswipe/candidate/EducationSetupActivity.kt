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

        auth = Firebase.auth // initialise auth

        val uid = auth.currentUser!!.uid // uid of current user

        // NEXT BUTTON TO BE IMPLEMENTED
        binding.ibRoundArrow.isClickable = false
        binding.ibRoundArrow.isEnabled = false
        binding.ibRoundArrow.isVisible = false

        // display the current education entries of the user from the database
        db.collection("Candidates").document(uid)
            .collection("Education") // sub-collection "Education"
            .get()
            .addOnSuccessListener { result ->
                // loop through all the education entries and display them on the screen
                for (document in result) {
                    val schoolName = document["schoolName"].toString()
                    val degreeType = document["degreeType"].toString()
                    val fieldOfStudy = document["fieldOfStudy"].toString()
                    val startYear = document["startYear"].toString()
                    val endYear = document["endYear"].toString()
                    val gpa = document["gpa"].toString()
                    val description = document["description"].toString()
                    val documentId = document.id
                    addView(schoolName, degreeType, fieldOfStudy, startYear, endYear, gpa, description, uid, documentId) // function to display an education entry on the screen
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception -> // if failed throw exception
                Log.d(TAG, "Error getting documents: ", exception)
            }

        // NEXT BUTTON FUNCTIONALITY TO BE IMPLEMENTED
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

        // if "skip" button is clicked
        binding.tvSkip.setOnClickListener {
            // move to the next activity, but don't kill this one
            val intent = Intent(this@EducationSetupActivity, WorkSetupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // get rid of extra layers/info from previous activities
            intent.putExtra("isUpdate", "no")
            startActivity(intent)
        }

        // if "add entry" button is clicked
        binding.ibAddEntry.setOnClickListener {
            // move to the education entry form, but don't kill this activity
            val intent = Intent(this@EducationSetupActivity, EducationEntryFormActivity::class.java)
            intent.putExtra("isUpdate", "no") // isUpdate flag is to indicated whether we are updating an existing entry or creating a new one. Here we create a new entry
            startActivity(intent)
        }
    }

    // function to display an education entry on the screen
    private fun addView(schoolName: String, degreeType: String, fieldOfStudy: String, startYear: String, endYear: String, gpa: String, description: String, uid: String, documentId: String) {
        val educationEntry : View = layoutInflater.inflate(R.layout.education_entry, null, false) // inflate educationEntry and store it in a variable

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT) // to set margins to the entry

        // set data to be displayed
        educationEntry.findViewById<TextView>(R.id.tvSchoolNameEntry).setText(schoolName)
        educationEntry.findViewById<TextView>(R.id.tvDegreeFos).setText("$degreeType, $fieldOfStudy")
        educationEntry.findViewById<TextView>(R.id.tvDates).setText("$startYear - $endYear")

        // if there is no data for any of the optional fields just show an empty string instead
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

        params.setMargins(0, 48, 0, 0) // marginTop for the entry
        educationEntry.layoutParams = params

        binding.llEducationHolder.addView(educationEntry) // add entry in the linear layout list/holder

        // if the entry is clicked
        educationEntry.setOnClickListener {
            // move to the education form to update the current entry, but don't kill this activity
            val intent = Intent(this@EducationSetupActivity, EducationEntryFormActivity::class.java)
            // send all the current data from the entry to next activity i.e. the form
            intent.putExtra("schoolName", schoolName)
            intent.putExtra("degreeType", degreeType)
            intent.putExtra("fieldOfStudy", fieldOfStudy)
            intent.putExtra("startYear", startYear)
            intent.putExtra("endYear", endYear)
            intent.putExtra("gpa", gpa)
            intent.putExtra("description", description)
            intent.putExtra("documentId", documentId)
            intent.putExtra("isUpdate", "yes") // isUpdate flag to indicate that we are updating an existing entry
            startActivity(intent)
        }

        // if the "delete" icon is clicked
        educationEntry.findViewById<ImageButton>(R.id.ibDeleteEntry).setOnClickListener {
            // disable the "delete" icon/button
            it.isEnabled = false
            it.isClickable = false
            // function to remove the entry/view from the screen and the database
            removeView(educationEntry, uid, documentId)
        }
    }

    // function to remove an education entry from the screen and the database
    private fun removeView(v: View?, uid: String, documentId: String) {
        db.collection("Candidates").document(uid)
            .collection("Education").document(documentId) // particular document/educationEntry, accessed by the documentId
            .delete() // delete the document/entry from the database
            .addOnSuccessListener {
                Toast.makeText(this, "Document deleted from the database", Toast.LENGTH_SHORT).show()
                binding.llEducationHolder.removeView(v) // delete the view/entry from the screen
            }
            .addOnFailureListener { // if the document/entry could not be deleted
                // enable the "delete" button/icon
                v!!.findViewById<ImageButton>(R.id.ibDeleteEntry).isEnabled = true
                v.findViewById<ImageButton>(R.id.ibDeleteEntry).isClickable = true
                Toast.makeText(this, "Couldn't delete document from the database", Toast.LENGTH_SHORT).show()
            }
    }
}