package com.teethcare.artikel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teethcare.R

class ArtikelAdapter(private val context: Context, private val artikel: List<Artikel>, private val listener: (Artikel) -> Unit)
    : RecyclerView.Adapter<ArtikelAdapter.ArtikelViewHolder>() {
    class ArtikelViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val imageArtikel = view.findViewById<ImageView>(R.id.imageIv)
        val judulArtikel = view.findViewById<TextView>(R.id.judulTv)
        val kategoriArtikel = view.findViewById<TextView>(R.id.kategoriTv)
        val tanggalArtikel = view.findViewById<TextView>(R.id.tanggalTv)
        val kontenArtikel = view.findViewById<TextView>(R.id.contentTv)

        fun bindView(artikel: Artikel, listener: (Artikel) -> Unit){
            imageArtikel.setImageResource(artikel.imageArtikel)
            judulArtikel.text = artikel.judulArtikel
            kategoriArtikel.text = artikel.kategoriArtikel
            tanggalArtikel.text = artikel.tanggalArtikel
            kontenArtikel.text = artikel.kontenArtikel
            itemView.setOnClickListener {
                listener(artikel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtikelViewHolder {
        return ArtikelViewHolder(
            LayoutInflater.from(context).inflate(R.layout.article_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ArtikelViewHolder, position: Int) {
        holder.bindView(artikel[position], listener)
    }

    override fun getItemCount(): Int = artikel.size
}