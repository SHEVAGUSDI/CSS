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
import com.mahasiswa.crud.adapters.DosenAdapter
import com.mahasiswa.crud.database.DatabaseHelper
import com.mahasiswa.crud.databinding.ActivityDosenBinding
import com.mahasiswa.crud.databinding.DialogAddEditDosenBinding
import com.mahasiswa.crud.models.Dosen
import java.util.regex.Pattern

class DosenActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDosenBinding
    private lateinit var adapter: DosenAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private var dosenList = mutableListOf<Dosen>()
    private var filteredList = mutableListOf<Dosen>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        databaseHelper = DatabaseHelper(this)
        
        setupToolbar()
        setupRecyclerView()
        setupSearchFunctionality()
        setupClickListeners()
        
        loadDosen()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = DosenAdapter(
            dosenList = filteredList,
            onEditClick = { dosen -> showAddEditDialog(dosen) },
            onDeleteClick = { dosen -> showDeleteConfirmation(dosen) }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DosenActivity)
            adapter = this@DosenActivity.adapter
        }
    }
    
    private fun setupSearchFunctionality() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterDosen(s.toString())
            }
        })
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            showAddEditDialog()
        }
    }
    
    private fun loadDosen() {
        dosenList.clear()
        dosenList.addAll(databaseHelper.getAllDosen())
        filteredList.clear()
        filteredList.addAll(dosenList)
        
        updateUI()
    }
    
    private fun filterDosen(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(dosenList)
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
    
    private fun showAddEditDialog(dosen: Dosen? = null) {
        val dialogBinding = DialogAddEditDosenBinding.inflate(layoutInflater)
        val isEdit = dosen != null
        
        // Fill fields if editing
        dosen?.let {
            dialogBinding.apply {
                etNip.setText(it.nip)
                etNamaDosen.setText(it.namaDosen)
                etAlamat.setText(it.alamat)
                etTelepon.setText(it.telepon)
                etEmail.setText(it.email)
            }
        }
        
        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isEdit) getString(R.string.edit_dosen) else getString(R.string.add_dosen))
            .setView(dialogBinding.root)
            .create()
        
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        
        dialogBinding.btnSave.setOnClickListener {
            saveDosen(dialogBinding, dosen, dialog)
        }
        
        dialog.show()
    }
    
    private fun saveDosen(dialogBinding: DialogAddEditDosenBinding, existingDosen: Dosen?, dialog: AlertDialog) {
        val nip = dialogBinding.etNip.text.toString().trim()
        val namaDosen = dialogBinding.etNamaDosen.text.toString().trim()
        val alamat = dialogBinding.etAlamat.text.toString().trim()
        val telepon = dialogBinding.etTelepon.text.toString().trim()
        val email = dialogBinding.etEmail.text.toString().trim()
        
        // Validation
        var isValid = true
        
        if (nip.isEmpty()) {
            dialogBinding.tilNip.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilNip.error = null
            // Check duplicate NIP
            if (databaseHelper.isNipExists(nip, existingDosen?.id ?: -1)) {
                dialogBinding.tilNip.error = "NIP sudah digunakan"
                isValid = false
            }
        }
        
        if (namaDosen.isEmpty()) {
            dialogBinding.tilNamaDosen.error = getString(R.string.error_empty_field)
            isValid = false
        } else {
            dialogBinding.tilNamaDosen.error = null
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
        
        if (!isValid) return
        
        val dosen = Dosen(
            id = existingDosen?.id ?: 0,
            nip = nip,
            namaDosen = namaDosen,
            alamat = alamat,
            telepon = telepon,
            email = email
        )
        
        val result = if (existingDosen == null) {
            databaseHelper.addDosen(dosen)
        } else {
            databaseHelper.updateDosen(dosen).toLong()
        }
        
        if (result > 0) {
            Toast.makeText(this, 
                if (existingDosen == null) getString(R.string.success_add) else getString(R.string.success_update), 
                Toast.LENGTH_SHORT).show()
            loadDosen()
            dialog.dismiss()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showDeleteConfirmation(dosen: Dosen) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteDosen(dosen)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
    
    private fun deleteDosen(dosen: Dosen) {
        val result = databaseHelper.deleteDosen(dosen.id)
        if (result > 0) {
            Toast.makeText(this, getString(R.string.success_delete), Toast.LENGTH_SHORT).show()
            loadDosen()
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