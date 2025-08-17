package com.student.crud.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.student.crud.R
import com.student.crud.models.Dosen

class DosenAdapter(
    private var dosenList: MutableList<Dosen>,
    private val onEditClick: (Dosen) -> Unit,
    private val onDeleteClick: (Dosen) -> Unit
) : RecyclerView.Adapter<DosenAdapter.DosenViewHolder>() {

    class DosenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DosenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return DosenViewHolder(view)
    }

    override fun onBindViewHolder(holder: DosenViewHolder, position: Int) {
        val dosen = dosenList[position]
        
        holder.tvTitle.text = dosen.namaDosen
        holder.tvSubtitle.text = "NIP: ${dosen.nip}"
        holder.tvDetails.text = "Email: ${dosen.email} | Telepon: ${dosen.telepon}"
        
        holder.btnEdit.setOnClickListener {
            onEditClick(dosen)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(dosen)
        }
    }

    override fun getItemCount(): Int = dosenList.size

    fun updateData(newList: List<Dosen>) {
        dosenList.clear()
        dosenList.addAll(newList)
        notifyDataSetChanged()
    }
}