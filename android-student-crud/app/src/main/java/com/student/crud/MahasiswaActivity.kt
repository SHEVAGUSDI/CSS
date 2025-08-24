package com.student.crud

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.MahasiswaAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Jurusan
import com.student.crud.models.Mahasiswa

class MahasiswaActivity : AppCompatActivity() {
    
    private lateinit var etNim: TextInputEditText
    private lateinit var etNama: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etTelepon: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var spinnerJurusan: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var mahasiswaAdapter: MahasiswaAdapter
    private var currentMahasiswa: Mahasiswa? = null
    private var jurusanList = mutableListOf<Jurusan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mahasiswa)
        
        supportActionBar?.title = "Data Mahasiswa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        setupDatabase()
        setupSpinner()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }
    
    private fun initViews() {
        etNim = findViewById(R.id.etNim)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etTelepon = findViewById(R.id.etTelepon)
        etEmail = findViewById(R.id.etEmail)
        spinnerJurusan = findViewById(R.id.spinnerJurusan)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
    }
    
    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }
    
    private fun setupSpinner() {
        loadJurusanData()
    }
    
    private fun loadJurusanData() {
        jurusanList.clear()
        jurusanList.add(Jurusan(0, "", "Pilih Jurusan", ""))
        jurusanList.addAll(databaseHelper.getAllJurusan())
        
        val spinnerItems = jurusanList.map { it.namaJurusan }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJurusan.adapter = adapter
    }
    
    private fun setupRecyclerView() {
        mahasiswaAdapter = MahasiswaAdapter(
            mutableListOf(),
            onEditClick = { mahasiswa ->
                currentMahasiswa = mahasiswa
                populateForm(mahasiswa)
            },
            onDeleteClick = { mahasiswa ->
                showDeleteConfirmation(mahasiswa)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MahasiswaActivity)
            adapter = mahasiswaAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveMahasiswa()
            }
        }
        
        btnClear.setOnClickListener {
            clearForm()
        }
    }
    
    private fun validateInput(): Boolean {
        val nim = etNim.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val selectedJurusan = spinnerJurusan.selectedItemPosition
        
        when {
            nim.isEmpty() -> {
                etNim.error = "NIM tidak boleh kosong"
                etNim.requestFocus()
                return false
            }
            nama.isEmpty() -> {
                etNama.error = "Nama tidak boleh kosong"
                etNama.requestFocus()
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
            selectedJurusan == 0 -> {
                Toast.makeText(this, "Pilih jurusan terlebih dahulu", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }
    
    private fun saveMahasiswa() {
        val nim = etNim.text.toString().trim()
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val selectedJurusan = jurusanList[spinnerJurusan.selectedItemPosition]
        
        val mahasiswa = if (currentMahasiswa != null) {
            currentMahasiswa!!.copy(
                nim = nim,
                nama = nama,
                alamat = alamat,
                telepon = telepon,
                email = email,
                jurusanId = selectedJurusan.id
            )
        } else {
            Mahasiswa(
                nim = nim,
                nama = nama,
                alamat = alamat,
                telepon = telepon,
                email = email,
                jurusanId = selectedJurusan.id
            )
        }
        
        val result = if (currentMahasiswa != null) {
            databaseHelper.updateMahasiswa(mahasiswa)
        } else {
            databaseHelper.insertMahasiswa(mahasiswa)
        }
        
        if (result > 0) {
            val message = if (currentMahasiswa != null) "Data berhasil diupdate" else "Data berhasil disimpan"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            clearForm()
            loadData()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun populateForm(mahasiswa: Mahasiswa) {
        etNim.setText(mahasiswa.nim)
        etNama.setText(mahasiswa.nama)
        etAlamat.setText(mahasiswa.alamat)
        etTelepon.setText(mahasiswa.telepon)
        etEmail.setText(mahasiswa.email)
        
        // Set spinner selection
        val jurusanIndex = jurusanList.indexOfFirst { it.id == mahasiswa.jurusanId }
        if (jurusanIndex > 0) {
            spinnerJurusan.setSelection(jurusanIndex)
        }
        
        btnSave.text = "Update"
    }
    
    private fun clearForm() {
        etNim.text?.clear()
        etNama.text?.clear()
        etAlamat.text?.clear()
        etTelepon.text?.clear()
        etEmail.text?.clear()
        spinnerJurusan.setSelection(0)
        currentMahasiswa = null
        btnSave.text = "Simpan"
        
        // Clear any errors
        etNim.error = null
        etNama.error = null
        etAlamat.error = null
        etTelepon.error = null
        etEmail.error = null
    }
    
    private fun showDeleteConfirmation(mahasiswa: Mahasiswa) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus data mahasiswa \"${mahasiswa.nama}\"?")
            .setPositiveButton("Ya") { _, _ ->
                deleteMahasiswa(mahasiswa)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun deleteMahasiswa(mahasiswa: Mahasiswa) {
        val result = databaseHelper.deleteMahasiswa(mahasiswa.id)
        if (result > 0) {
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            loadData()
            if (currentMahasiswa?.id == mahasiswa.id) {
                clearForm()
            }
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadData() {
        val mahasiswaList = databaseHelper.getAllMahasiswa()
        mahasiswaAdapter.updateData(mahasiswaList)
    }
    
    override fun onResume() {
        super.onResume()
        loadJurusanData() // Refresh jurusan data when returning from other activities
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}