package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.student.crud.R
import com.student.crud.models.Jurusan

class JurusanAdapter(
    private var jurusanList: List<Jurusan>,
    private val onEditClick: (Jurusan) -> Unit,
    private val onDeleteClick: (Jurusan) -> Unit
) : RecyclerView.Adapter<JurusanAdapter.JurusanViewHolder>() {

    class JurusanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JurusanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return JurusanViewHolder(view)
    }

    override fun onBindViewHolder(holder: JurusanViewHolder, position: Int) {
        val jurusan = jurusanList[position]
        
        holder.tvTitle.text = jurusan.namaJurusan
        holder.tvSubtitle.text = "Kode: ${jurusan.kodeJurusan}"
        holder.tvDescription.text = "Fakultas: ${jurusan.fakultas}"
        
        holder.btnEdit.setOnClickListener { onEditClick(jurusan) }
        holder.btnDelete.setOnClickListener { onDeleteClick(jurusan) }
    }

    override fun getItemCount(): Int = jurusanList.size

    fun updateData(newList: List<Jurusan>) {
        jurusanList = newList
        notifyDataSetChanged()
    }
}