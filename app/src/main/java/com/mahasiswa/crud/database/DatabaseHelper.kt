package com.mahasiswa.crud.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mahasiswa.crud.models.*
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    companion object {
        private const val DATABASE_NAME = "mahasiswa_db"
        private const val DATABASE_VERSION = 1
        
        // Table names
        private const val TABLE_JURUSAN = "jurusan"
        private const val TABLE_DOSEN = "dosen"
        private const val TABLE_MAHASISWA = "mahasiswa"
        private const val TABLE_MATA_KULIAH = "mata_kuliah"
        private const val TABLE_NILAI = "nilai"
        
        // Common columns
        private const val KEY_ID = "id"
        
        // Jurusan table columns
        private const val KEY_KODE_JURUSAN = "kode_jurusan"
        private const val KEY_NAMA_JURUSAN = "nama_jurusan"
        private const val KEY_FAKULTAS = "fakultas"
        
        // Dosen table columns
        private const val KEY_NIP = "nip"
        private const val KEY_NAMA_DOSEN = "nama_dosen"
        private const val KEY_ALAMAT_DOSEN = "alamat"
        private const val KEY_TELEPON_DOSEN = "telepon"
        private const val KEY_EMAIL_DOSEN = "email"
        
        // Mahasiswa table columns
        private const val KEY_NIM = "nim"
        private const val KEY_NAMA = "nama"
        private const val KEY_ALAMAT = "alamat"
        private const val KEY_TELEPON = "telepon"
        private const val KEY_EMAIL = "email"
        private const val KEY_JURUSAN_ID = "jurusan_id"
        
        // Mata Kuliah table columns
        private const val KEY_KODE_MK = "kode_mk"
        private const val KEY_NAMA_MK = "nama_mk"
        private const val KEY_SKS = "sks"
        private const val KEY_SEMESTER = "semester"
        private const val KEY_DOSEN_ID = "dosen_id"
        
        // Nilai table columns
        private const val KEY_MAHASISWA_ID = "mahasiswa_id"
        private const val KEY_MATA_KULIAH_ID = "mata_kuliah_id"
        private const val KEY_NILAI = "nilai"
        private const val KEY_GRADE = "grade"
        private const val KEY_TANGGAL_INPUT = "tanggal_input"
    }
    
    override fun onCreate(db: SQLiteDatabase?) {
        // Create Jurusan table
        val createJurusanTable = """
            CREATE TABLE $TABLE_JURUSAN (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_KODE_JURUSAN TEXT UNIQUE NOT NULL,
                $KEY_NAMA_JURUSAN TEXT NOT NULL,
                $KEY_FAKULTAS TEXT NOT NULL
            )
        """.trimIndent()
        
        // Create Dosen table
        val createDosenTable = """
            CREATE TABLE $TABLE_DOSEN (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NIP TEXT UNIQUE NOT NULL,
                $KEY_NAMA_DOSEN TEXT NOT NULL,
                $KEY_ALAMAT_DOSEN TEXT NOT NULL,
                $KEY_TELEPON_DOSEN TEXT NOT NULL,
                $KEY_EMAIL_DOSEN TEXT NOT NULL
            )
        """.trimIndent()
        
        // Create Mahasiswa table
        val createMahasiswaTable = """
            CREATE TABLE $TABLE_MAHASISWA (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NIM TEXT UNIQUE NOT NULL,
                $KEY_NAMA TEXT NOT NULL,
                $KEY_ALAMAT TEXT NOT NULL,
                $KEY_TELEPON TEXT NOT NULL,
                $KEY_EMAIL TEXT NOT NULL,
                $KEY_JURUSAN_ID INTEGER,
                FOREIGN KEY($KEY_JURUSAN_ID) REFERENCES $TABLE_JURUSAN($KEY_ID)
            )
        """.trimIndent()
        
        // Create Mata Kuliah table
        val createMataKuliahTable = """
            CREATE TABLE $TABLE_MATA_KULIAH (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_KODE_MK TEXT UNIQUE NOT NULL,
                $KEY_NAMA_MK TEXT NOT NULL,
                $KEY_SKS INTEGER NOT NULL,
                $KEY_SEMESTER INTEGER NOT NULL,
                $KEY_DOSEN_ID INTEGER,
                FOREIGN KEY($KEY_DOSEN_ID) REFERENCES $TABLE_DOSEN($KEY_ID)
            )
        """.trimIndent()
        
        // Create Nilai table
        val createNilaiTable = """
            CREATE TABLE $TABLE_NILAI (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_MAHASISWA_ID INTEGER,
                $KEY_MATA_KULIAH_ID INTEGER,
                $KEY_NILAI REAL NOT NULL,
                $KEY_GRADE TEXT NOT NULL,
                $KEY_TANGGAL_INPUT TEXT NOT NULL,
                FOREIGN KEY($KEY_MAHASISWA_ID) REFERENCES $TABLE_MAHASISWA($KEY_ID),
                FOREIGN KEY($KEY_MATA_KULIAH_ID) REFERENCES $TABLE_MATA_KULIAH($KEY_ID)
            )
        """.trimIndent()
        
        db?.execSQL(createJurusanTable)
        db?.execSQL(createDosenTable)
        db?.execSQL(createMahasiswaTable)
        db?.execSQL(createMataKuliahTable)
        db?.execSQL(createNilaiTable)
        
        // Enable foreign key constraints
        db?.execSQL("PRAGMA foreign_keys = ON")
    }
    
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NILAI")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MATA_KULIAH")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MAHASISWA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DOSEN")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_JURUSAN")
        onCreate(db)
    }
    
    // Jurusan CRUD operations
    fun addJurusan(jurusan: Jurusan): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_KODE_JURUSAN, jurusan.kodeJurusan)
            put(KEY_NAMA_JURUSAN, jurusan.namaJurusan)
            put(KEY_FAKULTAS, jurusan.fakultas)
        }
        val result = db.insert(TABLE_JURUSAN, null, values)
        db.close()
        return result
    }
    
    fun getAllJurusan(): List<Jurusan> {
        val jurusanList = mutableListOf<Jurusan>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_JURUSAN ORDER BY $KEY_NAMA_JURUSAN", null)
        
        if (cursor.moveToFirst()) {
            do {
                val jurusan = Jurusan(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    kodeJurusan = cursor.getString(cursor.getColumnIndexOrThrow(KEY_KODE_JURUSAN)),
                    namaJurusan = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA_JURUSAN)),
                    fakultas = cursor.getString(cursor.getColumnIndexOrThrow(KEY_FAKULTAS))
                )
                jurusanList.add(jurusan)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return jurusanList
    }
    
    fun updateJurusan(jurusan: Jurusan): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_KODE_JURUSAN, jurusan.kodeJurusan)
            put(KEY_NAMA_JURUSAN, jurusan.namaJurusan)
            put(KEY_FAKULTAS, jurusan.fakultas)
        }
        val result = db.update(TABLE_JURUSAN, values, "$KEY_ID = ?", arrayOf(jurusan.id.toString()))
        db.close()
        return result
    }
    
    fun deleteJurusan(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_JURUSAN, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
    
    // Dosen CRUD operations
    fun addDosen(dosen: Dosen): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NIP, dosen.nip)
            put(KEY_NAMA_DOSEN, dosen.namaDosen)
            put(KEY_ALAMAT_DOSEN, dosen.alamat)
            put(KEY_TELEPON_DOSEN, dosen.telepon)
            put(KEY_EMAIL_DOSEN, dosen.email)
        }
        val result = db.insert(TABLE_DOSEN, null, values)
        db.close()
        return result
    }
    
    fun getAllDosen(): List<Dosen> {
        val dosenList = mutableListOf<Dosen>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_DOSEN ORDER BY $KEY_NAMA_DOSEN", null)
        
        if (cursor.moveToFirst()) {
            do {
                val dosen = Dosen(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    nip = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIP)),
                    namaDosen = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA_DOSEN)),
                    alamat = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ALAMAT_DOSEN)),
                    telepon = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TELEPON_DOSEN)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL_DOSEN))
                )
                dosenList.add(dosen)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return dosenList
    }
    
    fun updateDosen(dosen: Dosen): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NIP, dosen.nip)
            put(KEY_NAMA_DOSEN, dosen.namaDosen)
            put(KEY_ALAMAT_DOSEN, dosen.alamat)
            put(KEY_TELEPON_DOSEN, dosen.telepon)
            put(KEY_EMAIL_DOSEN, dosen.email)
        }
        val result = db.update(TABLE_DOSEN, values, "$KEY_ID = ?", arrayOf(dosen.id.toString()))
        db.close()
        return result
    }
    
    fun deleteDosen(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_DOSEN, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
    
    // Mahasiswa CRUD operations
    fun addMahasiswa(mahasiswa: Mahasiswa): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NIM, mahasiswa.nim)
            put(KEY_NAMA, mahasiswa.nama)
            put(KEY_ALAMAT, mahasiswa.alamat)
            put(KEY_TELEPON, mahasiswa.telepon)
            put(KEY_EMAIL, mahasiswa.email)
            put(KEY_JURUSAN_ID, mahasiswa.jurusanId)
        }
        val result = db.insert(TABLE_MAHASISWA, null, values)
        db.close()
        return result
    }
    
    fun getAllMahasiswa(): List<Mahasiswa> {
        val mahasiswaList = mutableListOf<Mahasiswa>()
        val db = this.readableDatabase
        val query = """
            SELECT m.*, j.$KEY_NAMA_JURUSAN 
            FROM $TABLE_MAHASISWA m 
            LEFT JOIN $TABLE_JURUSAN j ON m.$KEY_JURUSAN_ID = j.$KEY_ID 
            ORDER BY m.$KEY_NAMA
        """
        val cursor = db.rawQuery(query, null)
        
        if (cursor.moveToFirst()) {
            do {
                val mahasiswa = Mahasiswa(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    nim = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NIM)),
                    nama = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA)),
                    alamat = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ALAMAT)),
                    telepon = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TELEPON)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL)),
                    jurusanId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_JURUSAN_ID))
                )
                mahasiswa.jurusanNama = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA_JURUSAN)) ?: ""
                mahasiswaList.add(mahasiswa)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return mahasiswaList
    }
    
    fun updateMahasiswa(mahasiswa: Mahasiswa): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NIM, mahasiswa.nim)
            put(KEY_NAMA, mahasiswa.nama)
            put(KEY_ALAMAT, mahasiswa.alamat)
            put(KEY_TELEPON, mahasiswa.telepon)
            put(KEY_EMAIL, mahasiswa.email)
            put(KEY_JURUSAN_ID, mahasiswa.jurusanId)
        }
        val result = db.update(TABLE_MAHASISWA, values, "$KEY_ID = ?", arrayOf(mahasiswa.id.toString()))
        db.close()
        return result
    }
    
    fun deleteMahasiswa(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_MAHASISWA, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
    
    // Mata Kuliah CRUD operations
    fun addMataKuliah(mataKuliah: MataKuliah): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_KODE_MK, mataKuliah.kodeMk)
            put(KEY_NAMA_MK, mataKuliah.namaMk)
            put(KEY_SKS, mataKuliah.sks)
            put(KEY_SEMESTER, mataKuliah.semester)
            put(KEY_DOSEN_ID, mataKuliah.dosenId)
        }
        val result = db.insert(TABLE_MATA_KULIAH, null, values)
        db.close()
        return result
    }
    
    fun getAllMataKuliah(): List<MataKuliah> {
        val mataKuliahList = mutableListOf<MataKuliah>()
        val db = this.readableDatabase
        val query = """
            SELECT mk.*, d.$KEY_NAMA_DOSEN 
            FROM $TABLE_MATA_KULIAH mk 
            LEFT JOIN $TABLE_DOSEN d ON mk.$KEY_DOSEN_ID = d.$KEY_ID 
            ORDER BY mk.$KEY_NAMA_MK
        """
        val cursor = db.rawQuery(query, null)
        
        if (cursor.moveToFirst()) {
            do {
                val mataKuliah = MataKuliah(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    kodeMk = cursor.getString(cursor.getColumnIndexOrThrow(KEY_KODE_MK)),
                    namaMk = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA_MK)),
                    sks = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SKS)),
                    semester = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SEMESTER)),
                    dosenId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DOSEN_ID))
                )
                mataKuliah.dosenNama = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAMA_DOSEN)) ?: ""
                mataKuliahList.add(mataKuliah)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return mataKuliahList
    }
    
    fun updateMataKuliah(mataKuliah: MataKuliah): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_KODE_MK, mataKuliah.kodeMk)
            put(KEY_NAMA_MK, mataKuliah.namaMk)
            put(KEY_SKS, mataKuliah.sks)
            put(KEY_SEMESTER, mataKuliah.semester)
            put(KEY_DOSEN_ID, mataKuliah.dosenId)
        }
        val result = db.update(TABLE_MATA_KULIAH, values, "$KEY_ID = ?", arrayOf(mataKuliah.id.toString()))
        db.close()
        return result
    }
    
    fun deleteMataKuliah(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_MATA_KULIAH, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
    
    // Nilai CRUD operations
    fun addNilai(nilai: Nilai): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_MAHASISWA_ID, nilai.mahasiswaId)
            put(KEY_MATA_KULIAH_ID, nilai.mataKuliahId)
            put(KEY_NILAI, nilai.nilai)
            put(KEY_GRADE, nilai.grade)
            put(KEY_TANGGAL_INPUT, nilai.tanggalInput)
        }
        val result = db.insert(TABLE_NILAI, null, values)
        db.close()
        return result
    }
    
    fun getAllNilai(): List<Nilai> {
        val nilaiList = mutableListOf<Nilai>()
        val db = this.readableDatabase
        val query = """
            SELECT n.*, m.$KEY_NAMA as mahasiswa_nama, mk.$KEY_NAMA_MK as mata_kuliah_nama 
            FROM $TABLE_NILAI n 
            LEFT JOIN $TABLE_MAHASISWA m ON n.$KEY_MAHASISWA_ID = m.$KEY_ID 
            LEFT JOIN $TABLE_MATA_KULIAH mk ON n.$KEY_MATA_KULIAH_ID = mk.$KEY_ID 
            ORDER BY n.$KEY_TANGGAL_INPUT DESC
        """
        val cursor = db.rawQuery(query, null)
        
        if (cursor.moveToFirst()) {
            do {
                val nilai = Nilai(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                    mahasiswaId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MAHASISWA_ID)),
                    mataKuliahId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MATA_KULIAH_ID)),
                    nilai = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_NILAI)),
                    grade = cursor.getString(cursor.getColumnIndexOrThrow(KEY_GRADE)),
                    tanggalInput = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TANGGAL_INPUT))
                )
                nilai.mahasiswaNama = cursor.getString(cursor.getColumnIndexOrThrow("mahasiswa_nama")) ?: ""
                nilai.mataKuliahNama = cursor.getString(cursor.getColumnIndexOrThrow("mata_kuliah_nama")) ?: ""
                nilaiList.add(nilai)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return nilaiList
    }
    
    fun updateNilai(nilai: Nilai): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_MAHASISWA_ID, nilai.mahasiswaId)
            put(KEY_MATA_KULIAH_ID, nilai.mataKuliahId)
            put(KEY_NILAI, nilai.nilai)
            put(KEY_GRADE, nilai.grade)
            put(KEY_TANGGAL_INPUT, nilai.tanggalInput)
        }
        val result = db.update(TABLE_NILAI, values, "$KEY_ID = ?", arrayOf(nilai.id.toString()))
        db.close()
        return result
    }
    
    fun deleteNilai(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NILAI, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
    
    // Helper functions
    fun isNimExists(nim: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val query = if (excludeId == -1) {
            "SELECT * FROM $TABLE_MAHASISWA WHERE $KEY_NIM = ?"
        } else {
            "SELECT * FROM $TABLE_MAHASISWA WHERE $KEY_NIM = ? AND $KEY_ID != ?"
        }
        val cursor = if (excludeId == -1) {
            db.rawQuery(query, arrayOf(nim))
        } else {
            db.rawQuery(query, arrayOf(nim, excludeId.toString()))
        }
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
    
    fun isKodeJurusanExists(kode: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val query = if (excludeId == -1) {
            "SELECT * FROM $TABLE_JURUSAN WHERE $KEY_KODE_JURUSAN = ?"
        } else {
            "SELECT * FROM $TABLE_JURUSAN WHERE $KEY_KODE_JURUSAN = ? AND $KEY_ID != ?"
        }
        val cursor = if (excludeId == -1) {
            db.rawQuery(query, arrayOf(kode))
        } else {
            db.rawQuery(query, arrayOf(kode, excludeId.toString()))
        }
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
    
    fun isNipExists(nip: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val query = if (excludeId == -1) {
            "SELECT * FROM $TABLE_DOSEN WHERE $KEY_NIP = ?"
        } else {
            "SELECT * FROM $TABLE_DOSEN WHERE $KEY_NIP = ? AND $KEY_ID != ?"
        }
        val cursor = if (excludeId == -1) {
            db.rawQuery(query, arrayOf(nip))
        } else {
            db.rawQuery(query, arrayOf(nip, excludeId.toString()))
        }
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
    
    fun isKodeMkExists(kode: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val query = if (excludeId == -1) {
            "SELECT * FROM $TABLE_MATA_KULIAH WHERE $KEY_KODE_MK = ?"
        } else {
            "SELECT * FROM $TABLE_MATA_KULIAH WHERE $KEY_KODE_MK = ? AND $KEY_ID != ?"
        }
        val cursor = if (excludeId == -1) {
            db.rawQuery(query, arrayOf(kode))
        } else {
            db.rawQuery(query, arrayOf(kode, excludeId.toString()))
        }
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
}