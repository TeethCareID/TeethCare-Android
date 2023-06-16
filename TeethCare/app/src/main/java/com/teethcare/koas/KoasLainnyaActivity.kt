package com.teethcare.koas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teethcare.*
import com.teethcare.databinding.ActivityKoasLainnyaBinding
import com.teethcare.profil.UserProfileActivity

class KoasLainnyaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKoasLainnyaBinding
    private lateinit var navigateBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupAction()
    }

    private fun setupView() {
        binding = ActivityKoasLainnyaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigation.selectedItemId = R.id.itemKoasLainnya
    }

    private fun setupAction() {
        binding.apply {
            RLArtikel.setOnClickListener {
                startActivity(Intent(this@KoasLainnyaActivity, ArticleActivity::class.java))
            }
            RLPertanyaan.setOnClickListener {
                startActivity(Intent(this@KoasLainnyaActivity, PertanyaanActivity::class.java))
            }
            RLAbout.setOnClickListener {
                startActivity(Intent(this@KoasLainnyaActivity, AboutActivity::class.java))
            }

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.itemKoasHome -> {
                        startActivity(Intent(this@KoasLainnyaActivity, KoasMainActivity::class.java))
                    }
                    R.id.itemKoasUser -> {
                        startActivity(Intent(this@KoasLainnyaActivity, KoasUserProfileActivity::class.java))
                    }
                }
                false
            }
        }
    }


}