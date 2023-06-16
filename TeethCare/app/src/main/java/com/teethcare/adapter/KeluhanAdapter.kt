package com.teethcare.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teethcare.R
import com.teethcare.model.kotlinKeluhan

class KeluhanAdapter(private val kelList: ArrayList<kotlinKeluhan>): RecyclerView.Adapter<KeluhanAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.keluhan_user, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: KeluhanAdapter.ViewHolder, position: Int) {
        val currentKeluhan = kelList[position]
        holder.kataKunci.text = currentKeluhan.kataKunci
        holder.deskripsiKeluhan.text = currentKeluhan.deskripsiKeluhan
        holder.date.text = currentKeluhan.date
    }

    override fun getItemCount(): Int {
        return kelList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val kataKunci : TextView = itemView.findViewById(R.id.kataKunci_keluhan)
        val deskripsiKeluhan: TextView = itemView.findViewById(R.id.deskripsi_keluhan)
        val date: TextView = itemView.findViewById(R.id.tanggal_keluhan)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }

    }
}