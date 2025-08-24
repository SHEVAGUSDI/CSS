package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.crud.R
import com.student.crud.models.MataKuliah

class MataKuliahAdapter(
    private var mataKuliahList: MutableList<MataKuliah>,
    private val onEditClick: (MataKuliah) -> Unit,
    private val onDeleteClick: (MataKuliah) -> Unit
) : RecyclerView.Adapter<MataKuliahAdapter.MataKuliahViewHolder>() {

    class MataKuliahViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MataKuliahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return MataKuliahViewHolder(view)
    }

    override fun onBindViewHolder(holder: MataKuliahViewHolder, position: Int) {
        val mataKuliah = mataKuliahList[position]
        
        holder.tvTitle.text = mataKuliah.namaMk
        holder.tvSubtitle.text = "Kode: ${mataKuliah.kodeMk}"
        holder.tvDetails.text = "SKS: ${mataKuliah.sks} | Semester: ${mataKuliah.semester}"
        
        holder.btnEdit.setOnClickListener {
            onEditClick(mataKuliah)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(mataKuliah)
        }
    }

    override fun getItemCount(): Int = mataKuliahList.size

    fun updateData(newList: List<MataKuliah>) {
        mataKuliahList.clear()
        mataKuliahList.addAll(newList)
        notifyDataSetChanged()
    }
}