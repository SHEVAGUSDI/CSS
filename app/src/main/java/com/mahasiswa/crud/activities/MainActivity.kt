package com.mahasiswa.crud.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mahasiswa.crud.R
import com.mahasiswa.crud.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.cardMahasiswa.setOnClickListener {
            startActivity(Intent(this, MahasiswaActivity::class.java))
        }
        
        binding.cardJurusan.setOnClickListener {
            startActivity(Intent(this, JurusanActivity::class.java))
        }
        
        binding.cardMataKuliah.setOnClickListener {
            startActivity(Intent(this, MataKuliahActivity::class.java))
        }
        
        binding.cardDosen.setOnClickListener {
            startActivity(Intent(this, DosenActivity::class.java))
        }
        
        binding.cardNilai.setOnClickListener {
            startActivity(Intent(this, NilaiActivity::class.java))
        }
    }
}