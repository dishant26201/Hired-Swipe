package com.example.hiredswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hiredswipe.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    val TAG = "RegisterActivity"

    private lateinit var binding: ActivityRegisterBinding // implementing view binding pt.1
    private lateinit var auth: FirebaseAuth // declare auth
    private lateinit var googleSignInClient: GoogleSignInClient // declare GoogleSignInClient
    private val RC_SIGN_IN = 0
    private val defaultWebClientId = "464702421423-cd37rq6c0th0ipjur4pjfi84t4h3n0t9.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth // initialise auth

        // configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(defaultWebClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.btnNormalCreateAccount.setOnClickListener {
            binding.btnNormalCreateAccount.isEnabled = false
            binding.btnNormalCreateAccount.isClickable = false
            formValidate()
        }

        // when the google sign in button is clicked
        binding.signInButton.setOnClickListener{
            signIn()
        }

        // when user presses on "Sign In" textView
        binding.tvSignIn.setOnClickListener{

            // move to the SignInActivity and kill this one
            val intent = Intent(this@RegisterActivity, SignInActivity::class.java)
            startActivity(intent) // start next activity
            finish() // finish current activity
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            // if the registration is successful
            if (task.isSuccessful) {
                val firebaseUser: FirebaseUser = task.result!!.user!! // firebase registered user

                // success message
                Log.d(TAG, "createUserWithEmail:success")
                Toast.makeText(
                    this@RegisterActivity,
                    "Authentication Successful",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@RegisterActivity, SelectUserTypeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // get rid of extra layers/info from previous activities
                startActivity(intent) // start next activity
                finish() // finish currenrt activity
            }
            // if the registration is unsuccessful
            else {
                binding.btnNormalCreateAccount.isEnabled = true
                binding.btnNormalCreateAccount.isClickable = true
                // failure message
                Log.d(TAG, "createUserWithEmail:failure")
                Toast.makeText(
                    this@RegisterActivity,
                    "Authentication failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun formValidate() {
        val email = binding.etEmailRegister
        val password = binding.etPasswordRegister
        val reEnterPassword = binding.etReEnterPasswordRegister

        // email, password, and re-enter password fields cannot be empty
        if (email.text.isEmpty()) {
            binding.btnNormalCreateAccount.isEnabled = true
            binding.btnNormalCreateAccount.isClickable = true
            email.error = "Email field cannot be blank"
        }
        if (password.text.isEmpty()) {
            binding.btnNormalCreateAccount.isEnabled = true
            binding.btnNormalCreateAccount.isClickable = true
            password.error = "Password field cannot be blank"
        }
        if (reEnterPassword.text.isEmpty()) {
            binding.btnNormalCreateAccount.isEnabled = true
            binding.btnNormalCreateAccount.isClickable = true
            reEnterPassword.error = "Please enter password again"
        }
        else{
            // verifying that the entered passwords are the same
            if (password.text.toString().equals(reEnterPassword.text.toString())) {
                val email: String = binding.etEmailRegister.text.toString().trim { it <= ' ' } // remove accidental spaces from email
                val password: String = binding.etPasswordRegister.text.toString().trim { it <= ' ' } // remove accidental spaces from password

                createAccount(email, password) // create account and move to next activity
            }
            // if entered passwords do not match
            else {
                binding.btnNormalCreateAccount.isEnabled = true
                binding.btnNormalCreateAccount.isClickable = true
                reEnterPassword.error = "Passwords do not match"
            }
        }
    }

    // google sign-in function
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this@RegisterActivity, SelectUserTypeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // get rid of extra layers/info from previous activities
        startActivity(intent) // start next activity
        finish() // finish currenrt activity
    }
}