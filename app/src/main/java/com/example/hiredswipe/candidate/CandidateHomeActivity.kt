package com.example.hiredswipe.candidate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hiredswipe.databinding.ActivityCandidateHomeBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hiredswipe.R

class CandidateHomeActivity : AppCompatActivity() {

    private val TAG = "CandidateHomeActivity"

    private lateinit var binding: ActivityCandidateHomeBinding // implementing view binding pt.1
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivityCandidateHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // NAVIGATION COMMENTS BY DHAIRY
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.bottomNavigationView.setupWithNavController(navController)
    }
}