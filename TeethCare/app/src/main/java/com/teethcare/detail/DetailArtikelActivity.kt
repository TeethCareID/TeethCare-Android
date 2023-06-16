package com.teethcare.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.teethcare.ArticleActivity
import com.teethcare.R
import com.teethcare.artikel.Artikel

class DetailArtikelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_artikel)

        val artikel = intent.getParcelableExtra<Artikel>(ArticleActivity.INTENT_ARTIKEL)

        val imageArtikel = findViewById<ImageView>(R.id.imageIv)
        val judulArtikel = findViewById<TextView>(R.id.judulTv)
        val kategoriArtikel = findViewById<TextView>(R.id.kategoriTv)
        val tanggalArtikel = findViewById<TextView>(R.id.tanggalTv)
        val kontenArtikel = findViewById<TextView>(R.id.contentTv)

        imageArtikel.setImageResource(artikel?.imageArtikel!!)
        judulArtikel.text = artikel.judulArtikel
        kategoriArtikel.text = artikel.kategoriArtikel
        tanggalArtikel.text = artikel.tanggalArtikel
        kontenArtikel.text = artikel.kontenArtikel
    }
}