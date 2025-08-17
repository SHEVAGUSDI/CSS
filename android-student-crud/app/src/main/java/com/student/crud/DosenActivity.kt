package com.student.crud

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.DosenAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Dosen

class DosenActivity : AppCompatActivity() {
    
    private lateinit var etNip: TextInputEditText
    private lateinit var etNamaDosen: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etTelepon: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var dosenAdapter: DosenAdapter
    private var currentDosen: Dosen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dosen)
        
        supportActionBar?.title = "Data Dosen"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        setupDatabase()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }
    
    private fun initViews() {
        etNip = findViewById(R.id.etNip)
        etNamaDosen = findViewById(R.id.etNamaDosen)
        etAlamat = findViewById(R.id.etAlamat)
        etTelepon = findViewById(R.id.etTelepon)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
    }
    
    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }
    
    private fun setupRecyclerView() {
        dosenAdapter = DosenAdapter(
            mutableListOf(),
            onEditClick = { dosen ->
                currentDosen = dosen
                populateForm(dosen)
            },
            onDeleteClick = { dosen ->
                showDeleteConfirmation(dosen)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DosenActivity)
            adapter = dosenAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveDosen()
            }
        }
        
        btnClear.setOnClickListener {
            clearForm()
        }
    }
    
    private fun validateInput(): Boolean {
        val nip = etNip.text.toString().trim()
        val namaDosen = etNamaDosen.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()
        
        when {
            nip.isEmpty() -> {
                etNip.error = "NIP tidak boleh kosong"
                etNip.requestFocus()
                return false
            }
            namaDosen.isEmpty() -> {
                etNamaDosen.error = "Nama dosen tidak boleh kosong"
                etNamaDosen.requestFocus()
                return false
            }
            alamat.isEmpty() -> {
                etAlamat.error = "Alamat tidak boleh kosong"
                etAlamat.requestFocus()
                return false
            }
            telepon.isEmpty() -> {
                etTelepon.error = "Telepon tidak boleh kosong"
                etTelepon.requestFocus()
                return false
            }
            email.isEmpty() -> {
                etEmail.error = "Email tidak boleh kosong"
                etEmail.requestFocus()
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Format email tidak valid"
                etEmail.requestFocus()
                return false
            }
        }
        return true
    }
    
    private fun saveDosen() {
        val nip = etNip.text.toString().trim()
        val namaDosen = etNamaDosen.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()
        
        val dosen = if (currentDosen != null) {
            currentDosen!!.copy(
                nip = nip,
                namaDosen = namaDosen,
                alamat = alamat,
                telepon = telepon,
                email = email
            )
        } else {
            Dosen(
                nip = nip,
                namaDosen = namaDosen,
                alamat = alamat,
                telepon = telepon,
                email = email
            )
        }
        
        val result = if (currentDosen != null) {
            databaseHelper.updateDosen(dosen)
        } else {
            databaseHelper.insertDosen(dosen)
        }
        
        if (result > 0) {
            val message = if (currentDosen != null) "Data berhasil diupdate" else "Data berhasil disimpan"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            clearForm()
            loadData()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun populateForm(dosen: Dosen) {
        etNip.setText(dosen.nip)
        etNamaDosen.setText(dosen.namaDosen)
        etAlamat.setText(dosen.alamat)
        etTelepon.setText(dosen.telepon)
        etEmail.setText(dosen.email)
        btnSave.text = "Update"
    }
    
    private fun clearForm() {
        etNip.text?.clear()
        etNamaDosen.text?.clear()
        etAlamat.text?.clear()
        etTelepon.text?.clear()
        etEmail.text?.clear()
        currentDosen = null
        btnSave.text = "Simpan"
        
        // Clear any errors
        etNip.error = null
        etNamaDosen.error = null
        etAlamat.error = null
        etTelepon.error = null
        etEmail.error = null
    }
    
    private fun showDeleteConfirmation(dosen: Dosen) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus data dosen \"${dosen.namaDosen}\"?")
            .setPositiveButton("Ya") { _, _ ->
                deleteDosen(dosen)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun deleteDosen(dosen: Dosen) {
        val result = databaseHelper.deleteDosen(dosen.id)
        if (result > 0) {
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            loadData()
            if (currentDosen?.id == dosen.id) {
                clearForm()
            }
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadData() {
        val dosenList = databaseHelper.getAllDosen()
        dosenAdapter.updateData(dosenList)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}