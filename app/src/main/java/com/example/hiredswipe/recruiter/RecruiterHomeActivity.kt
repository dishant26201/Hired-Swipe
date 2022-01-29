package com.example.hiredswipe.recruiter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hiredswipe.R
import com.example.hiredswipe.databinding.ActivityRecruiterHomeBinding

class RecruiterHomeActivity : AppCompatActivity() {

    private val TAG = "RecruiterHomeActivity"

    private lateinit var binding: ActivityRecruiterHomeBinding // implementing view binding pt.1
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityRecruiterHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // NAVIGATION COMMENTS BY DHAIRY
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_recruiter) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.bottomNavigationView.setupWithNavController(navController)
    }
}