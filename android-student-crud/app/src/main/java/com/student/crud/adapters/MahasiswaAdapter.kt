package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.crud.R
import com.student.crud.models.Mahasiswa

class MahasiswaAdapter(
    private var mahasiswaList: MutableList<Mahasiswa>,
    private val onEditClick: (Mahasiswa) -> Unit,
    private val onDeleteClick: (Mahasiswa) -> Unit
) : RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder>() {

    class MahasiswaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MahasiswaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MahasiswaViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        
        holder.tvTitle.text = mahasiswa.nama
        holder.tvSubtitle.text = "NIM: ${mahasiswa.nim}"
        holder.tvDetails.text = "Email: ${mahasiswa.email} | Telepon: ${mahasiswa.telepon}"
        
        holder.btnEdit.setOnClickListener {
            onEditClick(mahasiswa)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(mahasiswa)
        }
    }

    override fun getItemCount(): Int = mahasiswaList.size

    fun updateData(newList: List<Mahasiswa>) {
        mahasiswaList.clear()
        mahasiswaList.addAll(newList)
        notifyDataSetChanged()
    }
}