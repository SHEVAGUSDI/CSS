package com.mahasiswa.crud.activities

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahasiswa.crud.R
import com.mahasiswa.crud.adapters.MahasiswaAdapter
import com.mahasiswa.crud.database.DatabaseHelper
import com.mahasiswa.crud.databinding.ActivityMahasiswaBinding
import com.mahasiswa.crud.databinding.DialogAddEditMahasiswaBinding
import com.mahasiswa.crud.models.Jurusan
import com.mahasiswa.crud.models.Mahasiswa
import java.util.regex.Pattern

class MahasiswaActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMahasiswaBinding
    private lateinit var adapter: MahasiswaAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private var mahasiswaList = mutableListOf<Mahasiswa>()
    private var jurusanList = mutableListOf<Jurusan>()
    private var filteredList = mutableListOf<Mahasiswa>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        databaseHelper = DatabaseHelper(this)
        
        setupToolbar()
        setupRecyclerView()
        setupSearchFunctionality()
        setupClickListeners()
        
        loadJurusan()
        loadMahasiswa()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = MahasiswaAdapter(
            mahasiswaList = filteredList,
            onEditClick = { mahasiswa -> showAddEditDialog(mahasiswa) },
            onDeleteClick = { mahasiswa -> showDeleteConfirmation(mahasiswa) }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MahasiswaActivity)
            adapter = this@MahasiswaActivity.adapter
        }
    }
    
    private fun setupSearchFunctionality() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterMahasiswa(s.toString())
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
    }
    
    private fun loadMahasiswa() {
        mahasiswaList.clear()
        mahasiswaList.addAll(databaseHelper.getAllMahasiswa())
        filteredList.clear()
        filteredList.addAll(mahasiswaList)
        
        updateUI()
    }
    
    private fun filterMahasiswa(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(mahasiswaList)
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
    
    private fun showAddEditDialog(mahasiswa: Mahasiswa? = null) {
        val dialogBinding = DialogAddEditMahasiswaBinding.inflate(layoutInflater)
        val isEdit = mahasiswa != null
        
        // Setup dropdown for Jurusan
        val jurusanNames = jurusanList.map { "${it.kodeJurusan} - ${it.namaJurusan}" }
        val jurusanAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jurusanNames)
        dialogBinding.actvJurusan.setAdapter(jurusanAdapter)
        
        // Fill fields if editing
        mahasiswa?.let {
            dialogBinding.apply {
                etNim.setText(it.nim)
                etNama.setText(it.nama)
                etAlamat.setText(it.alamat)
                etTelepon.setText(it.telepon)
                etEmail.setText(it.email)
                
                val selectedJurusan = jurusanList.find { j -> j.id == it.jurusanId }
                selectedJurusan?.let { j ->
                    actvJurusan.setText("${j.kodeJurusan} - ${j.namaJurusan}", false)
                }
            }
        }
        
        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isEdit) getString(R.string.edit_mahasiswa) else getString(R.string.add_mahasiswa))
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialogBinding.btnSave.setOnClickListener {
            saveMahasiswa(dialogBinding, mahasiswa, dialog)
        }
        
        dialog.show()
    }
    
    private fun saveMahasiswa(dialogBinding: DialogAddEditMahasiswaBinding, existingMahasiswa: Mahasiswa?, dialog: AlertDialog) {
        val nim = dialogBinding.etNim.text.toString().trim()
        val nama = dialogBinding.etNama.text.toString().trim()
        val alamat = dialogBinding.etAlamat.text.toString().trim()
        val telepon = dialogBinding.etTelepon.text.toString().trim()
        val email = dialogBinding.etEmail.text.toString().trim()
        val jurusanText = dialogBinding.actvJurusan.text.toString().trim()
        
        // Validation
        var isValid = true
        
        if (nim.isEmpty()) {
            dialogBinding.tilNim.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilNim.error = null
            // Check duplicate NIM
            if (databaseHelper.isNimExists(nim, existingMahasiswa?.id ?: -1)) {
                dialogBinding.tilNim.error = getString(R.string.error_duplicate_nim)
                isValid = false
            }
        }
        
        if (nama.isEmpty()) {
            dialogBinding.tilNama.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilNama.error = null
        }
        
        if (alamat.isEmpty()) {
            dialogBinding.tilAlamat.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilAlamat.error = null
        }
        
        if (telepon.isEmpty()) {
            dialogBinding.tilTelepon.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (!isValidPhoneNumber(telepon)) {
            dialogBinding.tilTelepon.error = getString(R.string.error_invalid_phone)
            isValid = false
        } else {
            dialogBinding.tilTelepon.error = null
        }
        
        if (email.isEmpty()) {
            dialogBinding.tilEmail.error = getString(R.string.error_empty_field)
            isValid = false
        } else if (!isValidEmail(email)) {
            dialogBinding.tilEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            dialogBinding.tilEmail.error = null
        }
        
        if (jurusanText.isEmpty()) {
            dialogBinding.tilJurusan.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilJurusan.error = null
        }
        
        if (!isValid) return
        
        // Find selected jurusan
        val selectedJurusan = jurusanList.find { jurusanText.contains(it.kodeJurusan) }
        if (selectedJurusan == null) {
            dialogBinding.tilJurusan.error = "Jurusan tidak valid"
            return
        }
        
        val mahasiswa = Mahasiswa(
            id = existingMahasiswa?.id ?: 0,
            nim = nim,
            nama = nama,
            alamat = alamat,
            telepon = telepon,
            email = email,
            jurusanId = selectedJurusan.id
        )
        
        val result = if (existingMahasiswa == null) {
            databaseHelper.addMahasiswa(mahasiswa)
        } else {
            databaseHelper.updateMahasiswa(mahasiswa).toLong()
        }
        
        if (result > 0) {
            Toast.makeText(this, 
                if (existingMahasiswa == null) getString(R.string.success_add) else getString(R.string.success_update), 
                Toast.LENGTH_SHORT).show()
            loadMahasiswa()
            dialog.dismiss()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showDeleteConfirmation(mahasiswa: Mahasiswa) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteMahasiswa(mahasiswa)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
    
    private fun deleteMahasiswa(mahasiswa: Mahasiswa) {
        val result = databaseHelper.deleteMahasiswa(mahasiswa.id)
        if (result > 0) {
            Toast.makeText(this, getString(R.string.success_delete), Toast.LENGTH_SHORT).show()
            loadMahasiswa()
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private fun isValidPhoneNumber(phone: String): Boolean {
        // Indonesian phone number pattern
        val pattern = Pattern.compile("^(\\+62|62|0)[0-9]{9,12}$")
        return pattern.matcher(phone).matches()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }
}