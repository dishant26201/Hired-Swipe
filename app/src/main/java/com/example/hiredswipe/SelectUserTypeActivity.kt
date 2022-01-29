package com.example.hiredswipe

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.hiredswipe.candidate.CandidateBasicInfoActivity
import com.example.hiredswipe.databinding.ActivitySelectUserTypeBinding
import com.example.hiredswipe.recruiter.RecruiterBasicInfoActivity

class SelectUserTypeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySelectUserTypeBinding // implementing view binding pt.1
    private var mSelectedOptionPosition: Int = 0
    private var userType : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        // implementing view binding pt.2
        super.onCreate(savedInstanceState)
        binding = ActivitySelectUserTypeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // when these views are clicked
        binding.tvCandidateOption.setOnClickListener(this)
        binding.tvRecruiterOption.setOnClickListener(this)
        binding.ibRoundArrow.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCandidateOption -> {
                selectedOptionLook(binding.tvCandidateOption, 1) // function to highlight the "candidate" option
                userType = "candidate"
            }
            R.id.tvRecruiterOption -> {
                selectedOptionLook(binding.tvRecruiterOption, 2) // function to highlight the "recruiter" option
                userType = "recruiter"
            }
            R.id.ibRoundArrow -> {
                // move to next activity, but don't kill this one
                if (userType.lowercase().equals("candidate")) {
                    val intent = Intent(this@SelectUserTypeActivity, CandidateBasicInfoActivity::class.java)
                    intent.putExtra("userType", userType)
                    finish()
                    startActivity(intent)
                }
                if (userType.lowercase().equals("recruiter")) {
                    val intent = Intent(this@SelectUserTypeActivity, RecruiterBasicInfoActivity::class.java)
                    intent.putExtra("userType", userType)
                    finish()
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this@SelectUserTypeActivity, "Please select a valid option", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // default look of an option
    private fun defaultOptionLook() {
        // set default look to both options
        val options = ArrayList<TextView>()
        options.add(0, binding.tvCandidateOption)
        options.add(1, binding.tvRecruiterOption)

        for (option in options) {
            option.setTextColor(Color.parseColor("#FF000000"))
            option.typeface = Typeface.DEFAULT_BOLD
            option.background = ContextCompat.getDrawable(this, R.drawable.classic_white_button)
        }
    }

    // look of an option when selected
    private fun selectedOptionLook(tv: TextView, selectedOptionNumber: Int) {
        // highlight the selected option
        defaultOptionLook()
        mSelectedOptionPosition = selectedOptionNumber

        tv.setTextColor(Color.parseColor("#0073e6"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_background)

    }
}