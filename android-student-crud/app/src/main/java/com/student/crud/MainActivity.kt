package com.student.crud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    
    private lateinit var btnMahasiswa: Button
    private lateinit var btnJurusan: Button
    private lateinit var btnMataKuliah: Button
    private lateinit var btnNilai: Button
    private lateinit var btnDosen: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupClickListeners()
    }
    
    private fun initViews() {
        btnMahasiswa = findViewById(R.id.btnMahasiswa)
        btnJurusan = findViewById(R.id.btnJurusan)
        btnMataKuliah = findViewById(R.id.btnMataKuliah)
        btnNilai = findViewById(R.id.btnNilai)
        btnDosen = findViewById(R.id.btnDosen)
    }
    
    private fun setupClickListeners() {
        btnMahasiswa.setOnClickListener {
            startActivity(Intent(this, MahasiswaActivity::class.java))
        }
        
        btnJurusan.setOnClickListener {
            startActivity(Intent(this, JurusanActivity::class.java))
        }
        
        btnMataKuliah.setOnClickListener {
            startActivity(Intent(this, MataKuliahActivity::class.java))
        }
        
        btnNilai.setOnClickListener {
            startActivity(Intent(this, NilaiActivity::class.java))
        }
        
        btnDosen.setOnClickListener {
            startActivity(Intent(this, DosenActivity::class.java))
        }
    }
}