package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.student.crud.R
import com.student.crud.models.Mahasiswa

class MahasiswaAdapter(
    private var mahasiswaList: List<Mahasiswa>,
    private val onEditClick: (Mahasiswa) -> Unit,
    private val onDeleteClick: (Mahasiswa) -> Unit
) : RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder>() {

    class MahasiswaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MahasiswaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MahasiswaViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        
        holder.tvTitle.text = mahasiswa.nama
        holder.tvSubtitle.text = "NIM: ${mahasiswa.nim}"
        holder.tvDescription.text = "${mahasiswa.jurusanNama} • ${mahasiswa.email}"
        
        holder.btnEdit.setOnClickListener { onEditClick(mahasiswa) }
        holder.btnDelete.setOnClickListener { onDeleteClick(mahasiswa) }
    }

    override fun getItemCount(): Int = mahasiswaList.size

    fun updateData(newList: List<Mahasiswa>) {
        mahasiswaList = newList
        notifyDataSetChanged()
    }
}