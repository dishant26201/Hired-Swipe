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

        auth = Firebase.auth

        val uid = auth.currentUser!!.uid

        db.collection("Candidates").document(uid)
            .collection("Work")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val jobTitle = document["jobTitle"].toString()
                    val companyName = document["companyName"].toString()
                    val companyLocation = document["companyLocation"].toString()
                    val employmentType = document["employmentType"].toString()
                    val startYear = document["startYear"].toString()
                    val endYear = document["endYear"].toString()
                    val description = document["description"].toString()
                    val documentId = document.id.toString()
                    addView(jobTitle, companyName, companyLocation, employmentType, startYear, endYear, description, uid, documentId)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        binding.tvSkip.setOnClickListener {
            val intent = Intent(this@WorkSetupActivity, CandidateHomeActivity::class.java)
            finish()
            intent.putExtra("isUpdate", "no")
            startActivity(intent)
        }

        binding.ibAddEntry.setOnClickListener {
            val intent = Intent(this@WorkSetupActivity, WorkEntryFormActivity::class.java)
//            finish()
            intent.putExtra("isUpdate", "no")
            startActivity(intent)
        }
    }

    private fun addView(jobTitle: String, companyName: String, companyLocation: String, employmentType: String, startYear: String, endYear: String, description: String, uid: String, documentId: String) {
        val workEntry : View = layoutInflater.inflate(R.layout.work_entry, null, false)

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        workEntry.findViewById<TextView>(R.id.tvJobTitleEntry).setText(jobTitle)
        workEntry.findViewById<TextView>(R.id.tvCompanyLocation).setText("$companyName, $companyLocation")
        workEntry.findViewById<TextView>(R.id.tvEmploymentType).setText(employmentType)
        workEntry.findViewById<TextView>(R.id.tvDates).setText("$startYear - $endYear")

        if (description.isEmpty() || description.isBlank()) {
            workEntry.findViewById<TextView>(R.id.tvDescription).setText("")
        }
        else {
            workEntry.findViewById<TextView>(R.id.tvDescription).setText(description)
        }

        params.setMargins(0, 48, 0, 0)
        workEntry.layoutParams = params

        binding.llWorkHolder.addView(workEntry)

        workEntry.setOnClickListener {
            val intent = Intent(this@WorkSetupActivity, WorkEntryFormActivity::class.java)
            intent.putExtra("jobTitle", jobTitle)
            intent.putExtra("companyName", companyName)
            intent.putExtra("companyLocation", companyLocation)
            intent.putExtra("employmentType", employmentType)
            intent.putExtra("startYear", startYear)
            intent.putExtra("endYear", endYear)
            intent.putExtra("description", description)
            intent.putExtra("documentId", documentId)
            intent.putExtra("isUpdate", "yes")
            startActivity(intent)
        }

        workEntry.findViewById<ImageButton>(R.id.ibDeleteEntry).setOnClickListener {
            it.isEnabled = false
            it.isClickable = false
            removeView(workEntry, uid, documentId)
        }
    }

    private fun removeView(v: View?, uid: String, documentId: String) {
        db.collection("Candidates").document(uid)
            .collection("Work").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Document deleted from the database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                v!!.findViewById<ImageButton>(R.id.ibDeleteEntry).isEnabled = true
                v.findViewById<ImageButton>(R.id.ibDeleteEntry).isClickable = true
                Toast.makeText(this, "Couldn't delete document from the database", Toast.LENGTH_SHORT).show()
            }
        binding.llWorkHolder.removeView(v)
    }
}