package com.mahasiswa.crud.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mahasiswa.crud.databinding.ItemJurusanBinding
import com.mahasiswa.crud.models.Jurusan

class JurusanAdapter(
    private var jurusanList: List<Jurusan>,
    private val onEditClick: (Jurusan) -> Unit,
    private val onDeleteClick: (Jurusan) -> Unit
) : RecyclerView.Adapter<JurusanAdapter.JurusanViewHolder>() {

    inner class JurusanViewHolder(private val binding: ItemJurusanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(jurusan: Jurusan) {
            binding.apply {
                tvNamaJurusan.text = jurusan.namaJurusan
                tvKodeJurusan.text = "Kode: ${jurusan.kodeJurusan}"
                tvFakultas.text = jurusan.fakultas

                btnEdit.setOnClickListener { onEditClick(jurusan) }
                btnDelete.setOnClickListener { onDeleteClick(jurusan) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JurusanViewHolder {
        val binding = ItemJurusanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JurusanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JurusanViewHolder, position: Int) {
        holder.bind(jurusanList[position])
    }

    override fun getItemCount(): Int = jurusanList.size

    fun updateData(newList: List<Jurusan>) {
        jurusanList = newList
        notifyDataSetChanged()
    }

    fun filter(query: String): List<Jurusan> {
        return if (query.isEmpty()) {
            jurusanList
        } else {
            jurusanList.filter { jurusan ->
                jurusan.namaJurusan.contains(query, ignoreCase = true) ||
                        jurusan.kodeJurusan.contains(query, ignoreCase = true) ||
                        jurusan.fakultas.contains(query, ignoreCase = true)
            }
        }
    }
}