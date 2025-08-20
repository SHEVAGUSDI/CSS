package com.student.crud

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.NilaiAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Mahasiswa
import com.student.crud.models.MataKuliah
import com.student.crud.models.Nilai

class NilaiActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: NilaiAdapter
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var spinnerMahasiswa: AppCompatAutoCompleteTextView
    private lateinit var spinnerMataKuliah: AppCompatAutoCompleteTextView
    private lateinit var etNilai: TextInputEditText
    private lateinit var tvGradePreview: TextView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var fabAdd: FloatingActionButton
    
    private var editingNilai: Nilai? = null
    private var isFormVisible = false
    private var mahasiswaList: List<Mahasiswa> = emptyList()
    private var mataKuliahList: List<MataKuliah> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nilai)

        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupListeners()
        loadMahasiswaData()
        loadMataKuliahData()
        loadData()
        hideForm()
    }

    private fun initializeViews() {
        spinnerMahasiswa = findViewById(R.id.spinnerMahasiswa)
        spinnerMataKuliah = findViewById(R.id.spinnerMataKuliah)
        etNilai = findViewById(R.id.etNilai)
        tvGradePreview = findViewById(R.id.tvGradePreview)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        adapter = NilaiAdapter(
            nilaiList = emptyList(),
            onEditClick = { nilai -> editNilai(nilai) },
            onDeleteClick = { nilai -> confirmDelete(nilai) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener { showForm() }
        btnSave.setOnClickListener { saveNilai() }
        btnCancel.setOnClickListener { hideForm() }
        
        // Add text watcher for automatic grade calculation
        etNilai.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateGradePreview()
            }
        })
    }

    private fun loadMahasiswaData() {
        mahasiswaList = dbHelper.getAllMahasiswa()
        val mahasiswaNames = mahasiswaList.map { "${it.nama} (${it.nim})" }
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mahasiswaNames)
        spinnerMahasiswa.setAdapter(arrayAdapter)
    }

    private fun loadMataKuliahData() {
        mataKuliahList = dbHelper.getAllMataKuliah()
        val mataKuliahNames = mataKuliahList.map { "${it.namaMk} (${it.kodeMk})" }
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mataKuliahNames)
        spinnerMataKuliah.setAdapter(arrayAdapter)
    }

    private fun showForm() {
        if (mahasiswaList.isEmpty()) {
            Toast.makeText(this, "Please add Mahasiswa data first", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (mataKuliahList.isEmpty()) {
            Toast.makeText(this, "Please add Mata Kuliah data first", Toast.LENGTH_SHORT).show()
            return
        }
        
        isFormVisible = true
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.VISIBLE
        clearForm()
        editingNilai = null
    }

    private fun hideForm() {
        isFormVisible = false
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.GONE
        clearForm()
        editingNilai = null
    }

    private fun clearForm() {
        spinnerMahasiswa.setText("")
        spinnerMataKuliah.setText("")
        etNilai.setText("")
        tvGradePreview.text = "Grade: -"
    }

    private fun editNilai(nilai: Nilai) {
        editingNilai = nilai
        
        // Set spinner selections
        val selectedMahasiswa = mahasiswaList.find { it.id == nilai.mahasiswaId }
        selectedMahasiswa?.let {
            spinnerMahasiswa.setText("${it.nama} (${it.nim})", false)
        }
        
        val selectedMataKuliah = mataKuliahList.find { it.id == nilai.mataKuliahId }
        selectedMataKuliah?.let {
            spinnerMataKuliah.setText("${it.namaMk} (${it.kodeMk})", false)
        }
        
        etNilai.setText(nilai.nilai.toString())
        updateGradePreview()
        
        showForm()
    }

    private fun updateGradePreview() {
        val nilaiText = etNilai.text.toString().trim()
        val nilaiValue = nilaiText.toDoubleOrNull()
        
        if (nilaiValue != null && nilaiValue >= 0 && nilaiValue <= 100) {
            val grade = Nilai.calculateGrade(nilaiValue)
            tvGradePreview.text = "Grade: $grade"
        } else {
            tvGradePreview.text = "Grade: -"
        }
    }

    private fun saveNilai() {
        if (!validateInput()) return

        val selectedMahasiswaText = spinnerMahasiswa.text.toString()
        val selectedMahasiswa = mahasiswaList.find { "${it.nama} (${it.nim})" == selectedMahasiswaText }
        
        val selectedMataKuliahText = spinnerMataKuliah.text.toString()
        val selectedMataKuliah = mataKuliahList.find { "${it.namaMk} (${it.kodeMk})" == selectedMataKuliahText }
        
        if (selectedMahasiswa == null) {
            Toast.makeText(this, "Please select a valid Mahasiswa", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (selectedMataKuliah == null) {
            Toast.makeText(this, "Please select a valid Mata Kuliah", Toast.LENGTH_SHORT).show()
            return
        }

        val nilaiValue = etNilai.text.toString().trim().toDouble()

        val nilai = Nilai(
            id = editingNilai?.id ?: 0,
            mahasiswaId = selectedMahasiswa.id,
            mataKuliahId = selectedMataKuliah.id,
            nilai = nilaiValue,
            grade = Nilai.calculateGrade(nilaiValue),
            tanggalInput = "" // Will be set by database helper
        )

        try {
            if (editingNilai == null) {
                // Add new nilai
                val result = dbHelper.addNilai(nilai)
                if (result != -1L) {
                    Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_SHORT).show()
                    hideForm()
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update existing nilai
                val result = dbHelper.updateNilai(nilai)
                if (result > 0) {
                    Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_SHORT).show()
                    hideForm()
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(): Boolean {
        val selectedMahasiswaText = spinnerMahasiswa.text.toString()
        val selectedMataKuliahText = spinnerMataKuliah.text.toString()
        val nilaiText = etNilai.text.toString().trim()

        if (selectedMahasiswaText.isEmpty()) {
            Toast.makeText(this, "Please select a Mahasiswa", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedMataKuliahText.isEmpty()) {
            Toast.makeText(this, "Please select a Mata Kuliah", Toast.LENGTH_SHORT).show()
            return false
        }

        if (nilaiText.isEmpty()) {
            etNilai.error = getString(R.string.field_required)
            return false
        }

        val nilaiValue = nilaiText.toDoubleOrNull()
        if (nilaiValue == null || nilaiValue < 0 || nilaiValue > 100) {
            etNilai.error = getString(R.string.invalid_nilai)
            return false
        }

        return true
    }

    private fun confirmDelete(nilai: Nilai) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteNilai(nilai)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun deleteNilai(nilai: Nilai) {
        try {
            val result = dbHelper.deleteNilai(nilai.id)
            if (result > 0) {
                Toast.makeText(this, getString(R.string.success_deleted), Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        val nilaiList = dbHelper.getAllNilai()
        adapter.updateData(nilaiList)
    }
}