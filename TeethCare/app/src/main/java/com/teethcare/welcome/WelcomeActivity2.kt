package com.teethcare.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teethcare.R
import com.teethcare.databinding.ActivityWelcome2Binding

class WelcomeActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityWelcome2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView(){
        binding = ActivityWelcome2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupAction(){
        binding.btnNextWelcome2.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity3::class.java))
            finish()
        }
    }
}