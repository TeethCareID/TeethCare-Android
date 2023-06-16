package com.teethcare.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teethcare.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView(){
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupAction(){
        binding.btnNextWelcome1.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity2::class.java))
            finish()
        }
    }
}