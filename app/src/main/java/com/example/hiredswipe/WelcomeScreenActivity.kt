package com.example.hiredswipe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hiredswipe.databinding.ActivityRegisterBinding
import com.example.hiredswipe.databinding.ActivityWelcomeScreenBinding

class WelcomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding // implementing view binding pt.1

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ibRoundArrow.setOnClickListener {
            val intent = Intent(this@WelcomeScreenActivity, SelectUserTypeActivity::class.java)
            startActivity(intent)
//            overridePendingTransition(
//                R.anim.slide_in_right,
//                R.anim.slide_out_left
//            )
        }
    }
}