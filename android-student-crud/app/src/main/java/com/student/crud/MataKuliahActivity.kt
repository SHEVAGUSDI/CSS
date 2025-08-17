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
import com.student.crud.adapters.MataKuliahAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Dosen
import com.student.crud.models.MataKuliah

class MataKuliahActivity : AppCompatActivity() {
    
    private lateinit var etKodeMk: TextInputEditText
    private lateinit var etNamaMk: TextInputEditText
    private lateinit var etSks: TextInputEditText
    private lateinit var etSemester: TextInputEditText
    private lateinit var spinnerDosen: Spinner
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var mataKuliahAdapter: MataKuliahAdapter
    private var currentMataKuliah: MataKuliah? = null
    private var dosenList = mutableListOf<Dosen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mata_kuliah)
        
        supportActionBar?.title = "Data Mata Kuliah"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        setupDatabase()
        setupSpinner()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }
    
    private fun initViews() {
        etKodeMk = findViewById(R.id.etKodeMk)
        etNamaMk = findViewById(R.id.etNamaMk)
        etSks = findViewById(R.id.etSks)
        etSemester = findViewById(R.id.etSemester)
        spinnerDosen = findViewById(R.id.spinnerDosen)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
    }
    
    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }
    
    private fun setupSpinner() {
        loadDosenData()
    }
    
    private fun loadDosenData() {
        dosenList.clear()
        dosenList.add(Dosen(0, "", "Pilih Dosen", "", "", ""))
        dosenList.addAll(databaseHelper.getAllDosen())
        
        val spinnerItems = dosenList.map { it.namaDosen }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDosen.adapter = adapter
    }
    
    private fun setupRecyclerView() {
        mataKuliahAdapter = MataKuliahAdapter(
            mutableListOf(),
            onEditClick = { mataKuliah ->
                currentMataKuliah = mataKuliah
                populateForm(mataKuliah)
            },
            onDeleteClick = { mataKuliah ->
                showDeleteConfirmation(mataKuliah)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MataKuliahActivity)
            adapter = mataKuliahAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveMataKuliah()
            }
        }
        
        btnClear.setOnClickListener {
            clearForm()
        }
    }
    
    private fun validateInput(): Boolean {
        val kodeMk = etKodeMk.text.toString().trim()
        val namaMk = etNamaMk.text.toString().trim()
        val sksText = etSks.text.toString().trim()
        val semesterText = etSemester.text.toString().trim()
        val selectedDosen = spinnerDosen.selectedItemPosition
        
        when {
            kodeMk.isEmpty() -> {
                etKodeMk.error = "Kode mata kuliah tidak boleh kosong"
                etKodeMk.requestFocus()
                return false
            }
            namaMk.isEmpty() -> {
                etNamaMk.error = "Nama mata kuliah tidak boleh kosong"
                etNamaMk.requestFocus()
                return false
            }
            sksText.isEmpty() -> {
                etSks.error = "SKS tidak boleh kosong"
                etSks.requestFocus()
                return false
            }
            semesterText.isEmpty() -> {
                etSemester.error = "Semester tidak boleh kosong"
                etSemester.requestFocus()
                return false
            }
            selectedDosen == 0 -> {
                Toast.makeText(this, "Pilih dosen terlebih dahulu", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        
        // Validate numeric values
        try {
            val sks = sksText.toInt()
            val semester = semesterText.toInt()
            
            if (sks <= 0) {
                etSks.error = "SKS harus lebih dari 0"
                etSks.requestFocus()
                return false
            }
            
            if (semester <= 0) {
                etSemester.error = "Semester harus lebih dari 0"
                etSemester.requestFocus()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "SKS dan Semester harus berupa angka", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun saveMataKuliah() {
        val kodeMk = etKodeMk.text.toString().trim()
        val namaMk = etNamaMk.text.toString().trim()
        val sks = etSks.text.toString().trim().toInt()
        val semester = etSemester.text.toString().trim().toInt()
        val selectedDosen = dosenList[spinnerDosen.selectedItemPosition]
        
        val mataKuliah = if (currentMataKuliah != null) {
            currentMataKuliah!!.copy(
                kodeMk = kodeMk,
                namaMk = namaMk,
                sks = sks,
                semester = semester,
                dosenId = selectedDosen.id
            )
        } else {
            MataKuliah(
                kodeMk = kodeMk,
                namaMk = namaMk,
                sks = sks,
                semester = semester,
                dosenId = selectedDosen.id
            )
        }
        
        val result = if (currentMataKuliah != null) {
            databaseHelper.updateMataKuliah(mataKuliah)
        } else {
            databaseHelper.insertMataKuliah(mataKuliah)
        }
        
        if (result > 0) {
            val message = if (currentMataKuliah != null) "Data berhasil diupdate" else "Data berhasil disimpan"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            clearForm()
            loadData()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun populateForm(mataKuliah: MataKuliah) {
        etKodeMk.setText(mataKuliah.kodeMk)
        etNamaMk.setText(mataKuliah.namaMk)
        etSks.setText(mataKuliah.sks.toString())
        etSemester.setText(mataKuliah.semester.toString())
        
        // Set spinner selection
        val dosenIndex = dosenList.indexOfFirst { it.id == mataKuliah.dosenId }
        if (dosenIndex > 0) {
            spinnerDosen.setSelection(dosenIndex)
        }
        
        btnSave.text = "Update"
    }
    
    private fun clearForm() {
        etKodeMk.text?.clear()
        etNamaMk.text?.clear()
        etSks.text?.clear()
        etSemester.text?.clear()
        spinnerDosen.setSelection(0)
        currentMataKuliah = null
        btnSave.text = "Simpan"
        
        // Clear any errors
        etKodeMk.error = null
        etNamaMk.error = null
        etSks.error = null
        etSemester.error = null
    }
    
    private fun showDeleteConfirmation(mataKuliah: MataKuliah) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus mata kuliah \"${mataKuliah.namaMk}\"?")
            .setPositiveButton("Ya") { _, _ ->
                deleteMataKuliah(mataKuliah)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun deleteMataKuliah(mataKuliah: MataKuliah) {
        val result = databaseHelper.deleteMataKuliah(mataKuliah.id)
        if (result > 0) {
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            loadData()
            if (currentMataKuliah?.id == mataKuliah.id) {
                clearForm()
            }
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadData() {
        val mataKuliahList = databaseHelper.getAllMataKuliah()
        mataKuliahAdapter.updateData(mataKuliahList)
    }
    
    override fun onResume() {
        super.onResume()
        loadDosenData() // Refresh dosen data when returning from other activities
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}