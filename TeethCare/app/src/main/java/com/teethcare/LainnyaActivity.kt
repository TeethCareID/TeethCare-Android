package com.teethcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teethcare.databinding.ActivityLainnyaBinding
import com.teethcare.profil.UserProfileActivity

class LainnyaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLainnyaBinding
    private lateinit var navigateBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            RLArtikel.setOnClickListener {
                startActivity(Intent(this@LainnyaActivity, ArticleActivity::class.java))
            }
            RLPertanyaan.setOnClickListener {
                startActivity(Intent(this@LainnyaActivity, PertanyaanActivity::class.java))
            }
            RLAbout.setOnClickListener {
                startActivity(Intent(this@LainnyaActivity, AboutActivity::class.java))
            }

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.itemHome -> {
                        startActivity(Intent(this@LainnyaActivity, MainActivity::class.java))
                    }
                    R.id.itemHistory -> {
                        startActivity(Intent(this@LainnyaActivity, RiwayatUserActivity::class.java))
                    }
                    R.id.itemUser -> {
                        startActivity(Intent(this@LainnyaActivity, UserProfileActivity::class.java))
                    }
                }
                false
            }
        }
    }

    private fun setupView() {
        binding = ActivityLainnyaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.selectedItemId = R.id.itemLainnya
    }


}