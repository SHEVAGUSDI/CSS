package com.student.crud

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.btnJurusan).setOnClickListener {
            startActivity(Intent(this, JurusanActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnDosen).setOnClickListener {
            startActivity(Intent(this, DosenActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnMahasiswa).setOnClickListener {
            startActivity(Intent(this, MahasiswaActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnMataKuliah).setOnClickListener {
            startActivity(Intent(this, MataKuliahActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnNilai).setOnClickListener {
            startActivity(Intent(this, NilaiActivity::class.java))
        }
    }
}