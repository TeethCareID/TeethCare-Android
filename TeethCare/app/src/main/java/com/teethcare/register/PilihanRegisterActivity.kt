package com.teethcare.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teethcare.R
import com.teethcare.databinding.ActivityPilihanRegisterBinding
import com.teethcare.koas.KoasRegisterActivity

class PilihanRegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPilihanRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupAction(){
        binding.apply {
            btnKoas.setOnClickListener {
                startActivity(Intent(this@PilihanRegisterActivity, KoasRegisterActivity::class.java))
            }
            btnPasien.setOnClickListener {
                startActivity(Intent(this@PilihanRegisterActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun setupView(){
        binding = ActivityPilihanRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = getString(R.string.pilih_user)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}