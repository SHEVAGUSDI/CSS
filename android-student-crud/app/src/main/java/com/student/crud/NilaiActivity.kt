package com.student.crud

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.NilaiAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Mahasiswa
import com.student.crud.models.MataKuliah
import com.student.crud.models.Nilai

class NilaiActivity : AppCompatActivity() {
    
    private lateinit var spinnerMahasiswa: Spinner
    private lateinit var spinnerMataKuliah: Spinner
    private lateinit var etNilai: TextInputEditText
    private lateinit var etGrade: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var nilaiAdapter: NilaiAdapter
    private var currentNilai: Nilai? = null
    private var mahasiswaList = mutableListOf<Mahasiswa>()
    private var mataKuliahList = mutableListOf<MataKuliah>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nilai)
        
        supportActionBar?.title = "Data Nilai"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        setupDatabase()
        setupSpinners()
        setupRecyclerView()
        setupClickListeners()
        setupNilaiTextWatcher()
        loadData()
    }
    
    private fun initViews() {
        spinnerMahasiswa = findViewById(R.id.spinnerMahasiswa)
        spinnerMataKuliah = findViewById(R.id.spinnerMataKuliah)
        etNilai = findViewById(R.id.etNilai)
        etGrade = findViewById(R.id.etGrade)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
    }
    
    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }
    
    private fun setupSpinners() {
        loadMahasiswaData()
        loadMataKuliahData()
    }
    
    private fun loadMahasiswaData() {
        mahasiswaList.clear()
        mahasiswaList.add(Mahasiswa(0, "", "Pilih Mahasiswa", "", "", "", 0))
        mahasiswaList.addAll(databaseHelper.getAllMahasiswa())
        
        val spinnerItems = mahasiswaList.map { "${it.nama} (${it.nim})" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMahasiswa.adapter = adapter
    }
    
    private fun loadMataKuliahData() {
        mataKuliahList.clear()
        mataKuliahList.add(MataKuliah(0, "", "Pilih Mata Kuliah", 0, 0, 0))
        mataKuliahList.addAll(databaseHelper.getAllMataKuliah())
        
        val spinnerItems = mataKuliahList.map { "${it.namaMk} (${it.kodeMk})" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMataKuliah.adapter = adapter
    }
    
    private fun setupRecyclerView() {
        nilaiAdapter = NilaiAdapter(
            mutableListOf(),
            onEditClick = { nilai ->
                currentNilai = nilai
                populateForm(nilai)
            },
            onDeleteClick = { nilai ->
                showDeleteConfirmation(nilai)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@NilaiActivity)
            adapter = nilaiAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveNilai()
            }
        }
        
        btnClear.setOnClickListener {
            clearForm()
        }
    }
    
    private fun setupNilaiTextWatcher() {
        etNilai.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val nilaiText = s.toString().trim()
                if (nilaiText.isNotEmpty()) {
                    try {
                        val nilai = nilaiText.toDouble()
                        if (nilai >= 0 && nilai <= 100) {
                            val grade = Nilai.calculateGrade(nilai)
                            etGrade.setText(grade)
                        } else {
                            etGrade.setText("")
                        }
                    } catch (e: NumberFormatException) {
                        etGrade.setText("")
                    }
                } else {
                    etGrade.setText("")
                }
            }
        })
    }
    
    private fun validateInput(): Boolean {
        val nilaiText = etNilai.text.toString().trim()
        val selectedMahasiswa = spinnerMahasiswa.selectedItemPosition
        val selectedMataKuliah = spinnerMataKuliah.selectedItemPosition
        
        when {
            selectedMahasiswa == 0 -> {
                Toast.makeText(this, "Pilih mahasiswa terlebih dahulu", Toast.LENGTH_SHORT).show()
                return false
            }
            selectedMataKuliah == 0 -> {
                Toast.makeText(this, "Pilih mata kuliah terlebih dahulu", Toast.LENGTH_SHORT).show()
                return false
            }
            nilaiText.isEmpty() -> {
                etNilai.error = "Nilai tidak boleh kosong"
                etNilai.requestFocus()
                return false
            }
        }
        
        // Validate numeric value
        try {
            val nilai = nilaiText.toDouble()
            if (nilai < 0 || nilai > 100) {
                etNilai.error = "Nilai harus antara 0-100"
                etNilai.requestFocus()
                return false
            }
        } catch (e: NumberFormatException) {
            etNilai.error = "Nilai harus berupa angka"
            etNilai.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun saveNilai() {
        val nilaiValue = etNilai.text.toString().trim().toDouble()
        val grade = etGrade.text.toString().trim()
        val selectedMahasiswa = mahasiswaList[spinnerMahasiswa.selectedItemPosition]
        val selectedMataKuliah = mataKuliahList[spinnerMataKuliah.selectedItemPosition]
        val tanggalInput = databaseHelper.getCurrentDate()
        
        val nilai = if (currentNilai != null) {
            currentNilai!!.copy(
                mahasiswaId = selectedMahasiswa.id,
                mataKuliahId = selectedMataKuliah.id,
                nilai = nilaiValue,
                grade = grade,
                tanggalInput = tanggalInput
            )
        } else {
            Nilai(
                mahasiswaId = selectedMahasiswa.id,
                mataKuliahId = selectedMataKuliah.id,
                nilai = nilaiValue,
                grade = grade,
                tanggalInput = tanggalInput
            )
        }
        
        val result = if (currentNilai != null) {
            databaseHelper.updateNilai(nilai)
        } else {
            databaseHelper.insertNilai(nilai)
        }
        
        if (result > 0) {
            val message = if (currentNilai != null) "Data berhasil diupdate" else "Data berhasil disimpan"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            clearForm()
            loadData()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun populateForm(nilai: Nilai) {
        // Set spinner selections
        val mahasiswaIndex = mahasiswaList.indexOfFirst { it.id == nilai.mahasiswaId }
        val mataKuliahIndex = mataKuliahList.indexOfFirst { it.id == nilai.mataKuliahId }
        
        if (mahasiswaIndex > 0) {
            spinnerMahasiswa.setSelection(mahasiswaIndex)
        }
        
        if (mataKuliahIndex > 0) {
            spinnerMataKuliah.setSelection(mataKuliahIndex)
        }
        
        etNilai.setText(nilai.nilai.toString())
        etGrade.setText(nilai.grade)
        
        btnSave.text = "Update"
    }
    
    private fun clearForm() {
        spinnerMahasiswa.setSelection(0)
        spinnerMataKuliah.setSelection(0)
        etNilai.text?.clear()
        etGrade.text?.clear()
        currentNilai = null
        btnSave.text = "Simpan"
        
        // Clear any errors
        etNilai.error = null
    }
    
    private fun showDeleteConfirmation(nilai: Nilai) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus data nilai ini?")
            .setPositiveButton("Ya") { _, _ ->
                deleteNilai(nilai)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun deleteNilai(nilai: Nilai) {
        val result = databaseHelper.deleteNilai(nilai.id)
        if (result > 0) {
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            loadData()
            if (currentNilai?.id == nilai.id) {
                clearForm()
            }
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadData() {
        val nilaiList = databaseHelper.getAllNilai()
        nilaiAdapter.updateData(nilaiList)
    }
    
    override fun onResume() {
        super.onResume()
        loadMahasiswaData() // Refresh data when returning from other activities
        loadMataKuliahData()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}