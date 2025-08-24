package com.mahasiswa.crud.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahasiswa.crud.databinding.ItemMahasiswaBinding
import com.mahasiswa.crud.models.Mahasiswa

class MahasiswaAdapter(
    private var mahasiswaList: List<Mahasiswa>,
    private val onEditClick: (Mahasiswa) -> Unit,
    private val onDeleteClick: (Mahasiswa) -> Unit
) : RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder>() {

    inner class MahasiswaViewHolder(private val binding: ItemMahasiswaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mahasiswa: Mahasiswa) {
            binding.apply {
                tvNama.text = mahasiswa.nama
                tvNim.text = "NIM: ${mahasiswa.nim}"
                tvJurusan.text = mahasiswa.jurusanNama.ifEmpty { "Jurusan tidak ditemukan" }
                tvEmail.text = mahasiswa.email
                tvTelepon.text = mahasiswa.telepon

                btnEdit.setOnClickListener { onEditClick(mahasiswa) }
                btnDelete.setOnClickListener { onDeleteClick(mahasiswa) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaViewHolder {
        val binding = ItemMahasiswaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MahasiswaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MahasiswaViewHolder, position: Int) {
        holder.bind(mahasiswaList[position])
    }

    override fun getItemCount(): Int = mahasiswaList.size

    fun updateData(newList: List<Mahasiswa>) {
        mahasiswaList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String): List<Mahasiswa> {
        return if (query.isEmpty()) {
            mahasiswaList
        } else {
            mahasiswaList.filter { mahasiswa ->
                mahasiswa.nama.contains(query, ignoreCase = true) ||
                        mahasiswa.nim.contains(query, ignoreCase = true) ||
                        mahasiswa.jurusanNama.contains(query, ignoreCase = true) ||
                        mahasiswa.email.contains(query, ignoreCase = true)
            }
        }
    }
}