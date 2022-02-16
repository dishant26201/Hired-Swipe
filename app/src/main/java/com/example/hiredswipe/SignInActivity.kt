package com.example.hiredswipe

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hiredswipe.candidate.CandidateHomeActivity
import com.example.hiredswipe.databinding.ActivitySignInBinding
import com.example.hiredswipe.recruiter.RecruiterHomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    val TAG = "SignInActivity"

    private lateinit var binding: ActivitySignInBinding // implementing view binding pt.1
    private lateinit var auth: FirebaseAuth // declare auth
    private val db = Firebase.firestore // cloud firestore
    private lateinit var googleSignInClient: GoogleSignInClient // declare GoogleSignInClient
    private val RC_SIGN_IN = 0
    private val defaultWebClientId = "464702421423-cd37rq6c0th0ipjur4pjfi84t4h3n0t9.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestIdToken(getString(R.string.default_web_client_id))
            .requestIdToken(defaultWebClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // validating form and login user
        binding.btnNormalSignIn.setOnClickListener{
            formValidate()
        }

        // when the google sign in button is clicked
        binding.signInButton.setOnClickListener{
            signIn()
        }

        // when user presses on "Join Now" textView
        binding.tvJoinNow.setOnClickListener{

            // move to the RegisterActivity and kill this one
            val intent = Intent(this@SignInActivity, RegisterActivity::class.java)
            startActivity(intent) // start next activity
            finish() // finish current activity
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }

    private fun formValidate() {
        val email = binding.etEmailSignIn
        val password = binding.etPasswordSignIn

        // email, password, and re-enter password fields cannot be empty
        if (email.text.isEmpty()) {
            binding.btnNormalSignIn.isEnabled = true
            binding.btnNormalSignIn.isClickable = true
            email.error = "Email field cannot be blank"
        }
        if (password.text.isEmpty()) {
            binding.btnNormalSignIn.isEnabled = true
            binding.btnNormalSignIn.isClickable = true
            password.error = "Password field cannot be blank"
        }
        else{
            // verifying entered details are the correct, then sign-in and move to next activity
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        //Here we need to check if user is a candidate, or a recruiter, otherwise we move them to SelectUserTypeActivity



                        db.collection("Candidates").document(user!!.uid).get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    Log.d(TAG, "I AM IN IF line 113")
                                    updateUICandidate(user)
                                }
                                else {
                                    db.collection("Recruiters").document(user.uid)
                                        .get().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val document = task.result
                                                if (document.exists()) {
                                                    updateUIRecruiter(user)
                                                }
                                                else {
                                                    // We come in this else block when user exists, but is not a candidate or a recruiter
                                                    Log.w(TAG, "I AM HERE")
                                                    val intent = Intent(this@SignInActivity, SelectUserTypeActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // get rid of extra layers/info from previous activities
                                                    startActivity(intent) // start next activity
                                                    finish() // finish current activity
                                                }
                                            } else {
//                                                    Log.d("TAG", "Error: ", task.exception)
                                                Log.d(TAG, "Error: HERE")
                                            }
                                        }
                                }
                            }
                            else {
                                Log.d(TAG, "Error: ", task.exception)
                            }
                        }
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "HERE\n\nsignInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    db.collection("Candidates").document(user!!.uid)
                        .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if(document != null) {
                                if (document.exists()) {
                                    updateUICandidate(user)
                                } else {
                                    db.collection("Recruiters").document(user!!.uid)
                                        .get().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val document = task.result
                                                if(document != null) {
                                                    if (document.exists()) {
                                                        updateUIRecruiter(user)
                                                    } else {
                                                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                                                    }
                                                }
                                            } else {
                                                Log.d("TAG", "Error: ", task.exception)
                                            }
                                        }
                                }
                            }
                        }
                        else {
                            Log.d("TAG", "Error: ", task.exception)
                        }
                    }
                } else {
                    // if sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    // updateUI(null)
                }
            }
    }

    private fun reload() {
    }

    private fun updateUIRecruiter(user: FirebaseUser?) {
        // move to the next activity and kill this one
        val intent = Intent(this@SignInActivity, RecruiterHomeActivity::class.java)
        startActivity(intent) // start next activity
        finish() // finish current activity
    }

    private fun updateUICandidate(user: FirebaseUser?) {
        // move to the next activity and kill this one
        val intent = Intent(this@SignInActivity, CandidateHomeActivity::class.java)
        startActivity(intent) // start next activity
        finish() // finish current activity
    }
}