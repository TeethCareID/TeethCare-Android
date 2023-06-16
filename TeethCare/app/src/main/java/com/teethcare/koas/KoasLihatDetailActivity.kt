package com.teethcare.koas

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.teethcare.databinding.ActivityKoasLihatDetailBinding

class KoasLihatDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKoasLihatDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setViews()
        setupAction()
    }

    private fun setupView(){
        binding = ActivityKoasLihatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = "Detail Keluhan"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setViews(){
        binding.apply {
            tVEmail.text = intent.getStringExtra("email")
            edAddDesc.text = intent.getStringExtra("deskripsiKeluhan")
            tVKunci.text = intent.getStringExtra("kataKunci")
            tvTanggal.text = intent.getStringExtra("date")
            tvKoasKaries.text = intent.getStringExtra("persentaseCaries")
            tvKoasDiscoloration.text = intent.getStringExtra("persentaseDiscoloration")
            Glide.with(this@KoasLihatDetailActivity).load(intent.getStringExtra("avatar")).into(imageViewGigi)

        }
    }

    private fun setupAction(){
        val recipientEmail = intent.getStringExtra("email")
        binding.btnTangani.setOnClickListener {
            val emailIntent = Intent().apply {
                action = Intent.ACTION_SEND
                data = Uri.parse("mailto:")
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
                putExtra(Intent.EXTRA_SUBJECT, intent.getStringExtra("kataKunci"))
                putExtra(Intent.EXTRA_TEXT, intent.getStringExtra("deskripsiKeluhan"))
            }
            if (emailIntent.resolveActivity(this.packageManager) != null){
                emailIntent.setPackage("com.google.android.gm")
                startActivity(emailIntent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}