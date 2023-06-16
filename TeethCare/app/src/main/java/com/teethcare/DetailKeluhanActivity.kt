package com.teethcare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.teethcare.databinding.ActivityDetailKeluhanBinding

class DetailKeluhanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailKeluhanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setViews()
    }

    private fun setupView(){
        binding = ActivityDetailKeluhanBinding.inflate(layoutInflater)
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
            tvKaries.text = intent.getStringExtra("persentaseCaries")
            tvDiscolor.text = intent.getStringExtra("persentaseDiscoloration")
            Glide.with(this@DetailKeluhanActivity).load(intent.getStringExtra("avatar")).into(imageViewGigi)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}