package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.student.crud.R
import com.student.crud.models.MataKuliah

class MataKuliahAdapter(
    private var mataKuliahList: List<MataKuliah>,
    private val onEditClick: (MataKuliah) -> Unit,
    private val onDeleteClick: (MataKuliah) -> Unit
) : RecyclerView.Adapter<MataKuliahAdapter.MataKuliahViewHolder>() {

    class MataKuliahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MataKuliahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MataKuliahViewHolder(view)
    }

    override fun onBindViewHolder(holder: MataKuliahViewHolder, position: Int) {
        val mataKuliah = mataKuliahList[position]
        
        holder.tvTitle.text = mataKuliah.namaMk
        holder.tvSubtitle.text = "Kode: ${mataKuliah.kodeMk}"
        holder.tvDescription.text = "${mataKuliah.sks} SKS • Semester ${mataKuliah.semester} • ${mataKuliah.dosenNama}"
        
        holder.btnEdit.setOnClickListener { onEditClick(mataKuliah) }
        holder.btnDelete.setOnClickListener { onDeleteClick(mataKuliah) }
    }

    override fun getItemCount(): Int = mataKuliahList.size

    fun updateData(newList: List<MataKuliah>) {
        mataKuliahList = newList
        notifyDataSetChanged()
    }
}