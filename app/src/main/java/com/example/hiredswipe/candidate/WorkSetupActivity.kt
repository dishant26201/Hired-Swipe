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
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.ActivityWorkSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WorkSetupActivity : AppCompatActivity() {

    private val TAG = "WorkSetupActivity"

    private lateinit var binding: ActivityWorkSetupBinding // implementing view binding pt.1
    private val db = Firebase.firestore // Cloud firestore
    private lateinit var auth: FirebaseAuth // declare auth

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityWorkSetupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth // initialise auth

        val uid = auth.currentUser!!.uid // uid of current user

        // display the current work entries of the user from the database
        db.collection("Candidates").document(uid)
            .collection("Work") // sub-collection "Work"
            .get()
            .addOnSuccessListener { result ->
                // loop through all the work entries and display them on the screen
                for (document in result) {
                    val jobTitle = document["jobTitle"].toString()
                    val companyName = document["companyName"].toString()
                    val companyLocation = document["companyLocation"].toString()
                    val employmentType = document["employmentType"].toString()
                    val startYear = document["startYear"].toString()
                    val endYear = document["endYear"].toString()
                    val description = document["description"].toString()
                    val documentId = document.id
                    addView(jobTitle, companyName, companyLocation, employmentType, startYear, endYear, description, uid, documentId) // function to display a work entry on the screen
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception -> // if failed throw exception
                Log.d(TAG, "Error getting documents: ", exception)
            }

        // if "skip" button is clicked
        binding.tvSkip.setOnClickListener {
            // move to the next activity and kill this one
            val intent = Intent(this@WorkSetupActivity, CandidateHomeActivity::class.java)
            finish()
            intent.putExtra("isUpdate", "no")
            startActivity(intent)
        }

        // if "add entry" button is clicked
        binding.ibAddEntry.setOnClickListener {
            // move to the work entry form, but don't kill this activity
            val intent = Intent(this@WorkSetupActivity, WorkEntryFormActivity::class.java)
            intent.putExtra("isUpdate", "no") // isUpdate flag is to indicated whether we are updating an existing entry or creating a new one. Here we create a new entry
            startActivity(intent)
        }
    }

    // function to display a work entry on the screen
    private fun addView(jobTitle: String, companyName: String, companyLocation: String, employmentType: String, startYear: String, endYear: String, description: String, uid: String, documentId: String) {
        val workEntry : View = layoutInflater.inflate(R.layout.work_entry, null, false) // inflate workEntry and store it in a variable

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT) // to set margins to the entry

        // set data to be displayed
        workEntry.findViewById<TextView>(R.id.tvJobTitleEntry).setText(jobTitle)
        workEntry.findViewById<TextView>(R.id.tvCompanyLocation).setText("$companyName, $companyLocation")
        workEntry.findViewById<TextView>(R.id.tvEmploymentType).setText(employmentType)
        workEntry.findViewById<TextView>(R.id.tvDates).setText("$startYear - $endYear")

        // if there is no data for any of the optional fields just show an empty string instead
        if (description.isEmpty() || description.isBlank()) {
            workEntry.findViewById<TextView>(R.id.tvDescription).setText("")
        }
        else {
            workEntry.findViewById<TextView>(R.id.tvDescription).setText(description)
        }

        params.setMargins(0, 48, 0, 0) // marginTop for the entry
        workEntry.layoutParams = params

        binding.llWorkHolder.addView(workEntry) // add entry in the linear layout list/holder

        // if the entry is clicked
        workEntry.setOnClickListener {
            // move to the work form to update the current entry, but don't kill this activity
            val intent = Intent(this@WorkSetupActivity, WorkEntryFormActivity::class.java)
            // send all the current data from the entry to next activity i.e. the form
            intent.putExtra("jobTitle", jobTitle)
            intent.putExtra("companyName", companyName)
            intent.putExtra("companyLocation", companyLocation)
            intent.putExtra("employmentType", employmentType)
            intent.putExtra("startYear", startYear)
            intent.putExtra("endYear", endYear)
            intent.putExtra("description", description)
            intent.putExtra("documentId", documentId)
            intent.putExtra("isUpdate", "yes") // isUpdate flag to indicate that we are updating an existing entry
            startActivity(intent)
        }

        // if the "delete" icon is clicked
        workEntry.findViewById<ImageButton>(R.id.ibDeleteEntry).setOnClickListener {
            // disable the "delete" icon/button
            it.isEnabled = false
            it.isClickable = false
            // function to remove the entry/view from the screen and the database
            removeView(workEntry, uid, documentId)
        }
    }

    // function to remove a work entry from the screen and the database
    private fun removeView(v: View?, uid: String, documentId: String) {
        db.collection("Candidates").document(uid)
            .collection("Work").document(documentId) // particular document/workEntry, accessed by the documentId
            .delete() // delete the document/entry from the database
            .addOnSuccessListener {
                Toast.makeText(this, "Document deleted from the database", Toast.LENGTH_SHORT).show()
                binding.llWorkHolder.removeView(v) // delete the view/entry from the screen
            }
            .addOnFailureListener { // if the document/entry could not be deleted
                // enable the "delete" button/icon
                v!!.findViewById<ImageButton>(R.id.ibDeleteEntry).isEnabled = true
                v.findViewById<ImageButton>(R.id.ibDeleteEntry).isClickable = true
                Toast.makeText(this, "Couldn't delete document from the database", Toast.LENGTH_SHORT).show()
            }
    }
}