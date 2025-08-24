package com.mahasiswa.crud.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mahasiswa.crud.R
import com.mahasiswa.crud.database.DatabaseHelper
import com.mahasiswa.crud.databinding.ActivityNilaiBinding

class NilaiActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityNilaiBinding
    private lateinit var databaseHelper: DatabaseHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        databaseHelper = DatabaseHelper(this)
        
        setupToolbar()
        showComingSoonMessage()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun showComingSoonMessage() {
        binding.tvEmpty.text = "Nilai module - Coming Soon!"
        binding.tvEmpty.visibility = android.view.View.VISIBLE
        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Nilai CRUD akan segera tersedia", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }
}