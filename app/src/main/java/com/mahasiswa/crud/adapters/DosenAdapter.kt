package com.mahasiswa.crud.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahasiswa.crud.databinding.ItemDosenBinding
import com.mahasiswa.crud.models.Dosen

class DosenAdapter(
    private var dosenList: List<Dosen>,
    private val onEditClick: (Dosen) -> Unit,
    private val onDeleteClick: (Dosen) -> Unit
) : RecyclerView.Adapter<DosenAdapter.DosenViewHolder>() {

    inner class DosenViewHolder(private val binding: ItemDosenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dosen: Dosen) {
            binding.apply {
                tvNamaDosen.text = dosen.namaDosen
                tvNip.text = "NIP: ${dosen.nip}"
                tvEmail.text = dosen.email
                tvTelepon.text = dosen.telepon

                btnEdit.setOnClickListener { onEditClick(dosen) }
                btnDelete.setOnClickListener { onDeleteClick(dosen) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DosenViewHolder {
        val binding = ItemDosenBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DosenViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DosenViewHolder, position: Int) {
        holder.bind(dosenList[position])
    }

    override fun getItemCount(): Int = dosenList.size

    fun updateData(newList: List<Dosen>) {
        dosenList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String): List<Dosen> {
        return if (query.isEmpty()) {
            dosenList
        } else {
            dosenList.filter { dosen ->
                dosen.namaDosen.contains(query, ignoreCase = true) ||
                        dosen.nip.contains(query, ignoreCase = true) ||
                        dosen.email.contains(query, ignoreCase = true)
            }
        }
    }
}