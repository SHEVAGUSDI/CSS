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
import com.student.crud.adapters.DosenAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Dosen

class DosenActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: DosenAdapter
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var etNip: TextInputEditText
    private lateinit var etNamaDosen: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etTelepon: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var fabAdd: FloatingActionButton
    
    private var editingDosen: Dosen? = null
    private var isFormVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dosen)

        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupListeners()
        loadData()
        hideForm()
    }

    private fun initializeViews() {
        etNip = findViewById(R.id.etNip)
        etNamaDosen = findViewById(R.id.etNamaDosen)
        etAlamat = findViewById(R.id.etAlamat)
        etTelepon = findViewById(R.id.etTelepon)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        adapter = DosenAdapter(
            dosenList = emptyList(),
            onEditClick = { dosen -> editDosen(dosen) },
            onDeleteClick = { dosen -> confirmDelete(dosen) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener { showForm() }
        btnSave.setOnClickListener { saveDosen() }
        btnCancel.setOnClickListener { hideForm() }
    }

    private fun showForm() {
        isFormVisible = true
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.VISIBLE
        clearForm()
        editingDosen = null
    }

    private fun hideForm() {
        isFormVisible = false
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.GONE
        clearForm()
        editingDosen = null
    }

    private fun clearForm() {
        etNip.setText("")
        etNamaDosen.setText("")
        etAlamat.setText("")
        etTelepon.setText("")
        etEmail.setText("")
    }

    private fun editDosen(dosen: Dosen) {
        editingDosen = dosen
        etNip.setText(dosen.nip)
        etNamaDosen.setText(dosen.namaDosen)
        etAlamat.setText(dosen.alamat)
        etTelepon.setText(dosen.telepon)
        etEmail.setText(dosen.email)
        showForm()
    }

    private fun saveDosen() {
        if (!validateInput()) return

        val nip = etNip.text.toString().trim()
        val namaDosen = etNamaDosen.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()

        val dosen = Dosen(
            id = editingDosen?.id ?: 0,
            nip = nip,
            namaDosen = namaDosen,
            alamat = alamat,
            telepon = telepon,
            email = email
        )

        try {
            if (editingDosen == null) {
                // Add new dosen
                val result = dbHelper.addDosen(dosen)
                if (result != -1L) {
                    Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_SHORT).show()
                    hideForm()
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update existing dosen
                val result = dbHelper.updateDosen(dosen)
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
        val nip = etNip.text.toString().trim()
        val namaDosen = etNamaDosen.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (nip.isEmpty()) {
            etNip.error = getString(R.string.field_required)
            return false
        }

        if (namaDosen.isEmpty()) {
            etNamaDosen.error = getString(R.string.field_required)
            return false
        }

        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.field_required)
            return false
        }

        if (telepon.isEmpty()) {
            etTelepon.error = getString(R.string.field_required)
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = getString(R.string.field_required)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.invalid_email)
            return false
        }

        // Check if NIP already exists
        val excludeId = editingDosen?.id ?: -1
        if (dbHelper.isNipExists(nip, excludeId)) {
            etNip.error = getString(R.string.unique_constraint)
            return false
        }

        return true
    }

    private fun confirmDelete(dosen: Dosen) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteDosen(dosen)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun deleteDosen(dosen: Dosen) {
        try {
            val result = dbHelper.deleteDosen(dosen.id)
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
        val dosenList = dbHelper.getAllDosen()
        adapter.updateData(dosenList)
    }
}