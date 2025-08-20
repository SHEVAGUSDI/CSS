package com.student.crud

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.MataKuliahAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Dosen
import com.student.crud.models.MataKuliah

class MataKuliahActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: MataKuliahAdapter
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var etKodeMk: TextInputEditText
    private lateinit var etNamaMk: TextInputEditText
    private lateinit var etSks: TextInputEditText
    private lateinit var etSemester: TextInputEditText
    private lateinit var spinnerDosen: AppCompatAutoCompleteTextView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var fabAdd: FloatingActionButton
    
    private var editingMataKuliah: MataKuliah? = null
    private var isFormVisible = false
    private var dosenList: List<Dosen> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mata_kuliah)

        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupListeners()
        loadDosenData()
        loadData()
        hideForm()
    }

    private fun initializeViews() {
        etKodeMk = findViewById(R.id.etKodeMk)
        etNamaMk = findViewById(R.id.etNamaMk)
        etSks = findViewById(R.id.etSks)
        etSemester = findViewById(R.id.etSemester)
        spinnerDosen = findViewById(R.id.spinnerDosen)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        adapter = MataKuliahAdapter(
            mataKuliahList = emptyList(),
            onEditClick = { mataKuliah -> editMataKuliah(mataKuliah) },
            onDeleteClick = { mataKuliah -> confirmDelete(mataKuliah) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener { showForm() }
        btnSave.setOnClickListener { saveMataKuliah() }
        btnCancel.setOnClickListener { hideForm() }
    }

    private fun loadDosenData() {
        dosenList = dbHelper.getAllDosen()
        val dosenNames = dosenList.map { "${it.namaDosen} (${it.nip})" }
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dosenNames)
        spinnerDosen.setAdapter(arrayAdapter)
    }

    private fun showForm() {
        if (dosenList.isEmpty()) {
            Toast.makeText(this, "Please add Dosen data first", Toast.LENGTH_SHORT).show()
            return
        }
        
        isFormVisible = true
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.VISIBLE
        clearForm()
        editingMataKuliah = null
    }

    private fun hideForm() {
        isFormVisible = false
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.GONE
        clearForm()
        editingMataKuliah = null
    }

    private fun clearForm() {
        etKodeMk.setText("")
        etNamaMk.setText("")
        etSks.setText("")
        etSemester.setText("")
        spinnerDosen.setText("")
    }

    private fun editMataKuliah(mataKuliah: MataKuliah) {
        editingMataKuliah = mataKuliah
        etKodeMk.setText(mataKuliah.kodeMk)
        etNamaMk.setText(mataKuliah.namaMk)
        etSks.setText(mataKuliah.sks.toString())
        etSemester.setText(mataKuliah.semester.toString())
        
        // Set spinner selection
        val selectedDosen = dosenList.find { it.id == mataKuliah.dosenId }
        selectedDosen?.let {
            spinnerDosen.setText("${it.namaDosen} (${it.nip})", false)
        }
        
        showForm()
    }

    private fun saveMataKuliah() {
        if (!validateInput()) return

        val kodeMk = etKodeMk.text.toString().trim()
        val namaMk = etNamaMk.text.toString().trim()
        val sks = etSks.text.toString().trim().toIntOrNull() ?: 0
        val semester = etSemester.text.toString().trim().toIntOrNull() ?: 0
        
        val selectedDosenText = spinnerDosen.text.toString()
        val selectedDosen = dosenList.find { "${it.namaDosen} (${it.nip})" == selectedDosenText }
        
        if (selectedDosen == null) {
            Toast.makeText(this, "Please select a valid Dosen", Toast.LENGTH_SHORT).show()
            return
        }

        val mataKuliah = MataKuliah(
            id = editingMataKuliah?.id ?: 0,
            kodeMk = kodeMk,
            namaMk = namaMk,
            sks = sks,
            semester = semester,
            dosenId = selectedDosen.id
        )

        try {
            if (editingMataKuliah == null) {
                // Add new mata kuliah
                val result = dbHelper.addMataKuliah(mataKuliah)
                if (result != -1L) {
                    Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_SHORT).show()
                    hideForm()
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update existing mata kuliah
                val result = dbHelper.updateMataKuliah(mataKuliah)
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
        val kodeMk = etKodeMk.text.toString().trim()
        val namaMk = etNamaMk.text.toString().trim()
        val sksText = etSks.text.toString().trim()
        val semesterText = etSemester.text.toString().trim()
        val selectedDosenText = spinnerDosen.text.toString()

        if (kodeMk.isEmpty()) {
            etKodeMk.error = getString(R.string.field_required)
            return false
        }

        if (namaMk.isEmpty()) {
            etNamaMk.error = getString(R.string.field_required)
            return false
        }

        if (sksText.isEmpty()) {
            etSks.error = getString(R.string.field_required)
            return false
        }

        val sks = sksText.toIntOrNull()
        if (sks == null || sks <= 0) {
            etSks.error = "SKS must be a positive number"
            return false
        }

        if (semesterText.isEmpty()) {
            etSemester.error = getString(R.string.field_required)
            return false
        }

        val semester = semesterText.toIntOrNull()
        if (semester == null || semester <= 0 || semester > 8) {
            etSemester.error = "Semester must be between 1 and 8"
            return false
        }

        if (selectedDosenText.isEmpty()) {
            Toast.makeText(this, "Please select a Dosen", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if Kode MK already exists
        val excludeId = editingMataKuliah?.id ?: -1
        if (dbHelper.isKodeMkExists(kodeMk, excludeId)) {
            etKodeMk.error = getString(R.string.unique_constraint)
            return false
        }

        return true
    }

    private fun confirmDelete(mataKuliah: MataKuliah) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteMataKuliah(mataKuliah)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun deleteMataKuliah(mataKuliah: MataKuliah) {
        try {
            val result = dbHelper.deleteMataKuliah(mataKuliah.id)
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
        val mataKuliahList = dbHelper.getAllMataKuliah()
        adapter.updateData(mataKuliahList)
    }
}