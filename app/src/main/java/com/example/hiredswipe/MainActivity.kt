package com.example.hiredswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hiredswipe.databinding.ActivityMainBinding
import com.example.hiredswipe.databinding.ActivitySignInBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // implementing view binding pt.1

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // when the register/join-now button is clicked
        binding.btnJoinNow.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        // when the sign-in button is clicked
        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}