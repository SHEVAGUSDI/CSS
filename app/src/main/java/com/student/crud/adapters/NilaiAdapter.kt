package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.student.crud.R
import com.student.crud.models.Nilai

class NilaiAdapter(
    private var nilaiList: List<Nilai>,
    private val onEditClick: (Nilai) -> Unit,
    private val onDeleteClick: (Nilai) -> Unit
) : RecyclerView.Adapter<NilaiAdapter.NilaiViewHolder>() {

    class NilaiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NilaiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return NilaiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NilaiViewHolder, position: Int) {
        val nilai = nilaiList[position]
        
        holder.tvTitle.text = "${nilai.mahasiswaNama} - ${nilai.mataKuliahNama}"
        holder.tvSubtitle.text = "Nilai: ${nilai.nilai} | Grade: ${nilai.grade}"
        holder.tvDescription.text = "Tanggal: ${nilai.tanggalInput}"
        
        holder.btnEdit.setOnClickListener { onEditClick(nilai) }
        holder.btnDelete.setOnClickListener { onDeleteClick(nilai) }
    }

    override fun getItemCount(): Int = nilaiList.size

    fun updateData(newList: List<Nilai>) {
        nilaiList = newList
        notifyDataSetChanged()
    }
}