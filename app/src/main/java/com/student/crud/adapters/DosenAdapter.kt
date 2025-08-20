package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.student.crud.R
import com.student.crud.models.Dosen

class DosenAdapter(
    private var dosenList: List<Dosen>,
    private val onEditClick: (Dosen) -> Unit,
    private val onDeleteClick: (Dosen) -> Unit
) : RecyclerView.Adapter<DosenAdapter.DosenViewHolder>() {

    class DosenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSubtitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DosenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return DosenViewHolder(view)
    }

    override fun onBindViewHolder(holder: DosenViewHolder, position: Int) {
        val dosen = dosenList[position]
        
        holder.tvTitle.text = dosen.namaDosen
        holder.tvSubtitle.text = "NIP: ${dosen.nip}"
        holder.tvDescription.text = "${dosen.email} • ${dosen.telepon}"
        
        holder.btnEdit.setOnClickListener { onEditClick(dosen) }
        holder.btnDelete.setOnClickListener { onDeleteClick(dosen) }
    }

    override fun getItemCount(): Int = dosenList.size

    fun updateData(newList: List<Dosen>) {
        dosenList = newList
        notifyDataSetChanged()
    }
}