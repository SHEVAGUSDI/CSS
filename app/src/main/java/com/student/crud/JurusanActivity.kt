package com.student.crud

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.JurusanAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Jurusan

class JurusanActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: JurusanAdapter
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var etKodeJurusan: TextInputEditText
    private lateinit var etNamaJurusan: TextInputEditText
    private lateinit var etFakultas: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var fabAdd: FloatingActionButton
    
    private var editingJurusan: Jurusan? = null
    private var isFormVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jurusan)

        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupListeners()
        loadData()
        hideForm()
    }

    private fun initializeViews() {
        etKodeJurusan = findViewById(R.id.etKodeJurusan)
        etNamaJurusan = findViewById(R.id.etNamaJurusan)
        etFakultas = findViewById(R.id.etFakultas)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        adapter = JurusanAdapter(
            jurusanList = emptyList(),
            onEditClick = { jurusan -> editJurusan(jurusan) },
            onDeleteClick = { jurusan -> confirmDelete(jurusan) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener { showForm() }
        btnSave.setOnClickListener { saveJurusan() }
        btnCancel.setOnClickListener { hideForm() }
    }

    private fun showForm() {
        isFormVisible = true
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.VISIBLE
        clearForm()
        editingJurusan = null
    }

    private fun hideForm() {
        isFormVisible = false
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.GONE
        clearForm()
        editingJurusan = null
    }

    private fun clearForm() {
        etKodeJurusan.setText("")
        etNamaJurusan.setText("")
        etFakultas.setText("")
    }

    private fun editJurusan(jurusan: Jurusan) {
        editingJurusan = jurusan
        etKodeJurusan.setText(jurusan.kodeJurusan)
        etNamaJurusan.setText(jurusan.namaJurusan)
        etFakultas.setText(jurusan.fakultas)
        showForm()
    }

    private fun saveJurusan() {
        if (!validateInput()) return

        val kodeJurusan = etKodeJurusan.text.toString().trim()
        val namaJurusan = etNamaJurusan.text.toString().trim()
        val fakultas = etFakultas.text.toString().trim()

        val jurusan = Jurusan(
            id = editingJurusan?.id ?: 0,
            kodeJurusan = kodeJurusan,
            namaJurusan = namaJurusan,
            fakultas = fakultas
        )

        try {
            if (editingJurusan == null) {
                // Add new jurusan
                val result = dbHelper.addJurusan(jurusan)
                if (result != -1L) {
                    Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_SHORT).show()
                    hideForm()
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update existing jurusan
                val result = dbHelper.updateJurusan(jurusan)
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
        val kodeJurusan = etKodeJurusan.text.toString().trim()
        val namaJurusan = etNamaJurusan.text.toString().trim()
        val fakultas = etFakultas.text.toString().trim()

        if (kodeJurusan.isEmpty()) {
            etKodeJurusan.error = getString(R.string.field_required)
            return false
        }

        if (namaJurusan.isEmpty()) {
            etNamaJurusan.error = getString(R.string.field_required)
            return false
        }

        if (fakultas.isEmpty()) {
            etFakultas.error = getString(R.string.field_required)
            return false
        }

        // Check if kode jurusan already exists
        val excludeId = editingJurusan?.id ?: -1
        if (dbHelper.isKodeJurusanExists(kodeJurusan, excludeId)) {
            etKodeJurusan.error = getString(R.string.unique_constraint)
            return false
        }

        return true
    }

    private fun confirmDelete(jurusan: Jurusan) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteJurusan(jurusan)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun deleteJurusan(jurusan: Jurusan) {
        try {
            val result = dbHelper.deleteJurusan(jurusan.id)
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
        val jurusanList = dbHelper.getAllJurusan()
        adapter.updateData(jurusanList)
    }
}