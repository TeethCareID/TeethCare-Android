package com.teethcare.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teethcare.LoginActivity
import com.teethcare.databinding.ActivityWelcome3Binding
import com.teethcare.register.PilihanRegisterActivity

class WelcomeActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityWelcome3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView(){
        binding = ActivityWelcome3Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupAction(){
        binding.apply {
            btnMasukWelcome3.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity3, LoginActivity::class.java))
                finish()
            }
        }
    }
}