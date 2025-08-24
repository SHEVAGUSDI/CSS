package com.mahasiswa.crud.activities

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahasiswa.crud.R
import com.mahasiswa.crud.adapters.JurusanAdapter
import com.mahasiswa.crud.database.DatabaseHelper
import com.mahasiswa.crud.databinding.ActivityJurusanBinding
import com.mahasiswa.crud.databinding.DialogAddEditJurusanBinding
import com.mahasiswa.crud.models.Jurusan

class JurusanActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityJurusanBinding
    private lateinit var adapter: JurusanAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private var jurusanList = mutableListOf<Jurusan>()
    private var filteredList = mutableListOf<Jurusan>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJurusanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        databaseHelper = DatabaseHelper(this)
        
        setupToolbar()
        setupRecyclerView()
        setupSearchFunctionality()
        setupClickListeners()
        
        loadJurusan()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = JurusanAdapter(
            jurusanList = filteredList,
            onEditClick = { jurusan -> showAddEditDialog(jurusan) },
            onDeleteClick = { jurusan -> showDeleteConfirmation(jurusan) }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@JurusanActivity)
            adapter = this@JurusanActivity.adapter
        }
    }
    
    private fun setupSearchFunctionality() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterJurusan(s.toString())
            }
        })
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            showAddEditDialog()
        }
    }
    
    private fun loadJurusan() {
        jurusanList.clear()
        jurusanList.addAll(databaseHelper.getAllJurusan())
        filteredList.clear()
        filteredList.addAll(jurusanList)
        
        updateUI()
    }
    
    private fun filterJurusan(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(jurusanList)
        } else {
            filteredList.addAll(adapter.filter(query))
        }
        adapter.updateData(filteredList)
        updateUI()
    }
    
    private fun updateUI() {
        if (filteredList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }
    
    private fun showAddEditDialog(jurusan: Jurusan? = null) {
        val dialogBinding = DialogAddEditJurusanBinding.inflate(layoutInflater)
        val isEdit = jurusan != null
        
        // Fill fields if editing
        jurusan?.let {
            dialogBinding.apply {
                etKodeJurusan.setText(it.kodeJurusan)
                etNamaJurusan.setText(it.namaJurusan)
                etFakultas.setText(it.fakultas)
            }
        }
        
        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isEdit) getString(R.string.edit_jurusan) else getString(R.string.add_jurusan))
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialogBinding.btnSave.setOnClickListener {
            saveJurusan(dialogBinding, jurusan, dialog)
        }
        
        dialog.show()
    }
    
    private fun saveJurusan(dialogBinding: DialogAddEditJurusanBinding, existingJurusan: Jurusan?, dialog: AlertDialog) {
        val kodeJurusan = dialogBinding.etKodeJurusan.text.toString().trim().uppercase()
        val namaJurusan = dialogBinding.etNamaJurusan.text.toString().trim()
        val fakultas = dialogBinding.etFakultas.text.toString().trim()
        
        // Validation
        var isValid = true
        
        if (kodeJurusan.isEmpty()) {
            dialogBinding.tilKodeJurusan.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilKodeJurusan.error = null
            // Check duplicate kode jurusan
            if (databaseHelper.isKodeJurusanExists(kodeJurusan, existingJurusan?.id ?: -1)) {
                dialogBinding.tilKodeJurusan.error = getString(R.string.error_duplicate_kode)
                isValid = false
            }
        }
        
        if (namaJurusan.isEmpty()) {
            dialogBinding.tilNamaJurusan.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilNamaJurusan.error = null
        }
        
        if (fakultas.isEmpty()) {
            dialogBinding.tilFakultas.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilFakultas.error = null
        }
        
        if (!isValid) return
        
        val jurusan = Jurusan(
            id = existingJurusan?.id ?: 0,
            kodeJurusan = kodeJurusan,
            namaJurusan = namaJurusan,
            fakultas = fakultas
        )
        
        val result = if (existingJurusan == null) {
            databaseHelper.addJurusan(jurusan)
        } else {
            databaseHelper.updateJurusan(jurusan).toLong()
        }
        
        if (result > 0) {
            Toast.makeText(this, 
                if (existingJurusan == null) getString(R.string.success_add) else getString(R.string.success_update), 
                Toast.LENGTH_SHORT).show()
            loadJurusan()
            dialog.dismiss()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showDeleteConfirmation(jurusan: Jurusan) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteJurusan(jurusan)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
    
    private fun deleteJurusan(jurusan: Jurusan) {
        val result = databaseHelper.deleteJurusan(jurusan.id)
        if (result > 0) {
            Toast.makeText(this, getString(R.string.success_delete), Toast.LENGTH_SHORT).show()
            loadJurusan()
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }
}