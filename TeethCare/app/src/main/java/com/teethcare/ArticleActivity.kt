package com.teethcare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teethcare.artikel.ArtikelAdapter
import com.teethcare.artikel.DataArtikel.artikelList
import com.teethcare.detail.DetailArtikelActivity
import com.teethcare.profil.UserProfileActivity

class ArticleActivity : AppCompatActivity() {
    companion object{
        val INTENT_ARTIKEL = "INTENT_ARTIKEL"
    }

    private lateinit var rvArtikel: RecyclerView
    private lateinit var navigateBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)


        supportActionBar?.apply {
            title = "Artikel"
            setDisplayHomeAsUpEnabled(true)
        }

        listItemArtikel()
    }

    private fun listItemArtikel(){
        val recyclerView = findViewById<RecyclerView>(R.id.rv_article)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ArtikelAdapter(this, artikelList){
            val intent = Intent(this, DetailArtikelActivity::class.java)
            intent.putExtra(INTENT_ARTIKEL, it)
            startActivity(intent)
        }
    }
}