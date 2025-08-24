package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.crud.R
import com.student.crud.models.Nilai

class NilaiAdapter(
    private var nilaiList: MutableList<Nilai>,
    private val onEditClick: (Nilai) -> Unit,
    private val onDeleteClick: (Nilai) -> Unit
) : RecyclerView.Adapter<NilaiAdapter.NilaiViewHolder>() {

    class NilaiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NilaiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return NilaiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NilaiViewHolder, position: Int) {
        val nilai = nilaiList[position]
        
        holder.tvTitle.text = "Nilai: ${nilai.nilai} (${nilai.grade})"
        holder.tvSubtitle.text = "Mahasiswa ID: ${nilai.mahasiswaId} | MK ID: ${nilai.mataKuliahId}"
        holder.tvDetails.text = "Tanggal: ${nilai.tanggalInput}"
        
        holder.btnEdit.setOnClickListener {
            onEditClick(nilai)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(nilai)
        }
    }

    override fun getItemCount(): Int = nilaiList.size

    fun updateData(newList: List<Nilai>) {
        nilaiList.clear()
        nilaiList.addAll(newList)
        notifyDataSetChanged()
    }
}