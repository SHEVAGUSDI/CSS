package com.student.crud.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.student.crud.models.*
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "student_crud.db"
        private const val DATABASE_VERSION = 1

        // Table names
        private const val TABLE_JURUSAN = "jurusan"
        private const val TABLE_DOSEN = "dosen"
        private const val TABLE_MAHASISWA = "mahasiswa"
        private const val TABLE_MATA_KULIAH = "mata_kuliah"
        private const val TABLE_NILAI = "nilai"

        // Common columns
        private const val COLUMN_ID = "id"

        // Jurusan table columns
        private const val COLUMN_KODE_JURUSAN = "kode_jurusan"
        private const val COLUMN_NAMA_JURUSAN = "nama_jurusan"
        private const val COLUMN_FAKULTAS = "fakultas"

        // Dosen table columns
        private const val COLUMN_NIP = "nip"
        private const val COLUMN_NAMA_DOSEN = "nama_dosen"
        private const val COLUMN_ALAMAT = "alamat"
        private const val COLUMN_TELEPON = "telepon"
        private const val COLUMN_EMAIL = "email"

        // Mahasiswa table columns
        private const val COLUMN_NIM = "nim"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_JURUSAN_ID = "jurusan_id"

        // Mata Kuliah table columns
        private const val COLUMN_KODE_MK = "kode_mk"
        private const val COLUMN_NAMA_MK = "nama_mk"
        private const val COLUMN_SKS = "sks"
        private const val COLUMN_SEMESTER = "semester"
        private const val COLUMN_DOSEN_ID = "dosen_id"

        // Nilai table columns
        private const val COLUMN_MAHASISWA_ID = "mahasiswa_id"
        private const val COLUMN_MATA_KULIAH_ID = "mata_kuliah_id"
        private const val COLUMN_NILAI = "nilai"
        private const val COLUMN_GRADE = "grade"
        private const val COLUMN_TANGGAL_INPUT = "tanggal_input"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Jurusan table
        val createJurusanTable = """
            CREATE TABLE $TABLE_JURUSAN (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_KODE_JURUSAN TEXT UNIQUE NOT NULL,
                $COLUMN_NAMA_JURUSAN TEXT NOT NULL,
                $COLUMN_FAKULTAS TEXT NOT NULL
            )
        """.trimIndent()

        // Create Dosen table
        val createDosenTable = """
            CREATE TABLE $TABLE_DOSEN (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NIP TEXT UNIQUE NOT NULL,
                $COLUMN_NAMA_DOSEN TEXT NOT NULL,
                $COLUMN_ALAMAT TEXT NOT NULL,
                $COLUMN_TELEPON TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL
            )
        """.trimIndent()

        // Create Mahasiswa table
        val createMahasiswaTable = """
            CREATE TABLE $TABLE_MAHASISWA (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NIM TEXT UNIQUE NOT NULL,
                $COLUMN_NAMA TEXT NOT NULL,
                $COLUMN_ALAMAT TEXT NOT NULL,
                $COLUMN_TELEPON TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL,
                $COLUMN_JURUSAN_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_JURUSAN_ID) REFERENCES $TABLE_JURUSAN($COLUMN_ID)
            )
        """.trimIndent()

        // Create Mata Kuliah table
        val createMataKuliahTable = """
            CREATE TABLE $TABLE_MATA_KULIAH (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_KODE_MK TEXT UNIQUE NOT NULL,
                $COLUMN_NAMA_MK TEXT NOT NULL,
                $COLUMN_SKS INTEGER NOT NULL,
                $COLUMN_SEMESTER INTEGER NOT NULL,
                $COLUMN_DOSEN_ID INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_DOSEN_ID) REFERENCES $TABLE_DOSEN($COLUMN_ID)
            )
        """.trimIndent()

        // Create Nilai table
        val createNilaiTable = """
            CREATE TABLE $TABLE_NILAI (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MAHASISWA_ID INTEGER NOT NULL,
                $COLUMN_MATA_KULIAH_ID INTEGER NOT NULL,
                $COLUMN_NILAI REAL NOT NULL,
                $COLUMN_GRADE TEXT NOT NULL,
                $COLUMN_TANGGAL_INPUT TEXT NOT NULL,
                FOREIGN KEY($COLUMN_MAHASISWA_ID) REFERENCES $TABLE_MAHASISWA($COLUMN_ID),
                FOREIGN KEY($COLUMN_MATA_KULIAH_ID) REFERENCES $TABLE_MATA_KULIAH($COLUMN_ID)
            )
        """.trimIndent()

        db.execSQL(createJurusanTable)
        db.execSQL(createDosenTable)
        db.execSQL(createMahasiswaTable)
        db.execSQL(createMataKuliahTable)
        db.execSQL(createNilaiTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NILAI")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MATA_KULIAH")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAHASISWA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOSEN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_JURUSAN")
        onCreate(db)
    }

    // Jurusan CRUD operations
    fun addJurusan(jurusan: Jurusan): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KODE_JURUSAN, jurusan.kodeJurusan)
            put(COLUMN_NAMA_JURUSAN, jurusan.namaJurusan)
            put(COLUMN_FAKULTAS, jurusan.fakultas)
        }
        return db.insert(TABLE_JURUSAN, null, values)
    }

    fun getAllJurusan(): List<Jurusan> {
        val jurusanList = mutableListOf<Jurusan>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_JURUSAN ORDER BY $COLUMN_NAMA_JURUSAN", null)

        if (cursor.moveToFirst()) {
            do {
                val jurusan = Jurusan(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    kodeJurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_JURUSAN)),
                    namaJurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_JURUSAN)),
                    fakultas = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAKULTAS))
                )
                jurusanList.add(jurusan)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return jurusanList
    }

    fun updateJurusan(jurusan: Jurusan): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KODE_JURUSAN, jurusan.kodeJurusan)
            put(COLUMN_NAMA_JURUSAN, jurusan.namaJurusan)
            put(COLUMN_FAKULTAS, jurusan.fakultas)
        }
        return db.update(TABLE_JURUSAN, values, "$COLUMN_ID = ?", arrayOf(jurusan.id.toString()))
    }

    fun deleteJurusan(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_JURUSAN, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Dosen CRUD operations
    fun addDosen(dosen: Dosen): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIP, dosen.nip)
            put(COLUMN_NAMA_DOSEN, dosen.namaDosen)
            put(COLUMN_ALAMAT, dosen.alamat)
            put(COLUMN_TELEPON, dosen.telepon)
            put(COLUMN_EMAIL, dosen.email)
        }
        return db.insert(TABLE_DOSEN, null, values)
    }

    fun getAllDosen(): List<Dosen> {
        val dosenList = mutableListOf<Dosen>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_DOSEN ORDER BY $COLUMN_NAMA_DOSEN", null)

        if (cursor.moveToFirst()) {
            do {
                val dosen = Dosen(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nip = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIP)),
                    namaDosen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_DOSEN)),
                    alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)),
                    telepon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPON)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                )
                dosenList.add(dosen)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dosenList
    }

    fun updateDosen(dosen: Dosen): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIP, dosen.nip)
            put(COLUMN_NAMA_DOSEN, dosen.namaDosen)
            put(COLUMN_ALAMAT, dosen.alamat)
            put(COLUMN_TELEPON, dosen.telepon)
            put(COLUMN_EMAIL, dosen.email)
        }
        return db.update(TABLE_DOSEN, values, "$COLUMN_ID = ?", arrayOf(dosen.id.toString()))
    }

    fun deleteDosen(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_DOSEN, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Mahasiswa CRUD operations
    fun addMahasiswa(mahasiswa: Mahasiswa): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIM, mahasiswa.nim)
            put(COLUMN_NAMA, mahasiswa.nama)
            put(COLUMN_ALAMAT, mahasiswa.alamat)
            put(COLUMN_TELEPON, mahasiswa.telepon)
            put(COLUMN_EMAIL, mahasiswa.email)
            put(COLUMN_JURUSAN_ID, mahasiswa.jurusanId)
        }
        return db.insert(TABLE_MAHASISWA, null, values)
    }

    fun getAllMahasiswa(): List<Mahasiswa> {
        val mahasiswaList = mutableListOf<Mahasiswa>()
        val db = this.readableDatabase
        val query = """
            SELECT m.*, j.$COLUMN_NAMA_JURUSAN
            FROM $TABLE_MAHASISWA m
            LEFT JOIN $TABLE_JURUSAN j ON m.$COLUMN_JURUSAN_ID = j.$COLUMN_ID
            ORDER BY m.$COLUMN_NAMA
        """.trimIndent()
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val mahasiswa = Mahasiswa(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    nim = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIM)),
                    nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA)),
                    alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT)),
                    telepon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPON)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    jurusanId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JURUSAN_ID)),
                    jurusanNama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_JURUSAN)) ?: ""
                )
                mahasiswaList.add(mahasiswa)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return mahasiswaList
    }

    fun updateMahasiswa(mahasiswa: Mahasiswa): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIM, mahasiswa.nim)
            put(COLUMN_NAMA, mahasiswa.nama)
            put(COLUMN_ALAMAT, mahasiswa.alamat)
            put(COLUMN_TELEPON, mahasiswa.telepon)
            put(COLUMN_EMAIL, mahasiswa.email)
            put(COLUMN_JURUSAN_ID, mahasiswa.jurusanId)
        }
        return db.update(TABLE_MAHASISWA, values, "$COLUMN_ID = ?", arrayOf(mahasiswa.id.toString()))
    }

    fun deleteMahasiswa(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_MAHASISWA, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Mata Kuliah CRUD operations
    fun addMataKuliah(mataKuliah: MataKuliah): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KODE_MK, mataKuliah.kodeMk)
            put(COLUMN_NAMA_MK, mataKuliah.namaMk)
            put(COLUMN_SKS, mataKuliah.sks)
            put(COLUMN_SEMESTER, mataKuliah.semester)
            put(COLUMN_DOSEN_ID, mataKuliah.dosenId)
        }
        return db.insert(TABLE_MATA_KULIAH, null, values)
    }

    fun getAllMataKuliah(): List<MataKuliah> {
        val mataKuliahList = mutableListOf<MataKuliah>()
        val db = this.readableDatabase
        val query = """
            SELECT mk.*, d.$COLUMN_NAMA_DOSEN
            FROM $TABLE_MATA_KULIAH mk
            LEFT JOIN $TABLE_DOSEN d ON mk.$COLUMN_DOSEN_ID = d.$COLUMN_ID
            ORDER BY mk.$COLUMN_NAMA_MK
        """.trimIndent()
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val mataKuliah = MataKuliah(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    kodeMk = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_MK)),
                    namaMk = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_MK)),
                    sks = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SKS)),
                    semester = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEMESTER)),
                    dosenId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOSEN_ID)),
                    dosenNama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_DOSEN)) ?: ""
                )
                mataKuliahList.add(mataKuliah)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return mataKuliahList
    }

    fun updateMataKuliah(mataKuliah: MataKuliah): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KODE_MK, mataKuliah.kodeMk)
            put(COLUMN_NAMA_MK, mataKuliah.namaMk)
            put(COLUMN_SKS, mataKuliah.sks)
            put(COLUMN_SEMESTER, mataKuliah.semester)
            put(COLUMN_DOSEN_ID, mataKuliah.dosenId)
        }
        return db.update(TABLE_MATA_KULIAH, values, "$COLUMN_ID = ?", arrayOf(mataKuliah.id.toString()))
    }

    fun deleteMataKuliah(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_MATA_KULIAH, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Nilai CRUD operations
    fun addNilai(nilai: Nilai): Long {
        val db = this.writableDatabase
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val grade = Nilai.calculateGrade(nilai.nilai)
        
        val values = ContentValues().apply {
            put(COLUMN_MAHASISWA_ID, nilai.mahasiswaId)
            put(COLUMN_MATA_KULIAH_ID, nilai.mataKuliahId)
            put(COLUMN_NILAI, nilai.nilai)
            put(COLUMN_GRADE, grade)
            put(COLUMN_TANGGAL_INPUT, currentDate)
        }
        return db.insert(TABLE_NILAI, null, values)
    }

    fun getAllNilai(): List<Nilai> {
        val nilaiList = mutableListOf<Nilai>()
        val db = this.readableDatabase
        val query = """
            SELECT n.*, m.$COLUMN_NAMA as mahasiswa_nama, mk.$COLUMN_NAMA_MK as mata_kuliah_nama
            FROM $TABLE_NILAI n
            LEFT JOIN $TABLE_MAHASISWA m ON n.$COLUMN_MAHASISWA_ID = m.$COLUMN_ID
            LEFT JOIN $TABLE_MATA_KULIAH mk ON n.$COLUMN_MATA_KULIAH_ID = mk.$COLUMN_ID
            ORDER BY n.$COLUMN_TANGGAL_INPUT DESC
        """.trimIndent()
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val nilai = Nilai(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    mahasiswaId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAHASISWA_ID)),
                    mataKuliahId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MATA_KULIAH_ID)),
                    nilai = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NILAI)),
                    grade = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
                    tanggalInput = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL_INPUT)),
                    mahasiswaNama = cursor.getString(cursor.getColumnIndexOrThrow("mahasiswa_nama")) ?: "",
                    mataKuliahNama = cursor.getString(cursor.getColumnIndexOrThrow("mata_kuliah_nama")) ?: ""
                )
                nilaiList.add(nilai)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return nilaiList
    }

    fun updateNilai(nilai: Nilai): Int {
        val db = this.writableDatabase
        val grade = Nilai.calculateGrade(nilai.nilai)
        
        val values = ContentValues().apply {
            put(COLUMN_MAHASISWA_ID, nilai.mahasiswaId)
            put(COLUMN_MATA_KULIAH_ID, nilai.mataKuliahId)
            put(COLUMN_NILAI, nilai.nilai)
            put(COLUMN_GRADE, grade)
            // Keep the original tanggal_input, don't update it
        }
        return db.update(TABLE_NILAI, values, "$COLUMN_ID = ?", arrayOf(nilai.id.toString()))
    }

    fun deleteNilai(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NILAI, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Validation methods
    fun isKodeJurusanExists(kode: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val whereClause = if (excludeId != -1) {
            "$COLUMN_KODE_JURUSAN = ? AND $COLUMN_ID != ?"
        } else {
            "$COLUMN_KODE_JURUSAN = ?"
        }
        val whereArgs = if (excludeId != -1) {
            arrayOf(kode, excludeId.toString())
        } else {
            arrayOf(kode)
        }
        
        val cursor = db.query(TABLE_JURUSAN, null, whereClause, whereArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun isNipExists(nip: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val whereClause = if (excludeId != -1) {
            "$COLUMN_NIP = ? AND $COLUMN_ID != ?"
        } else {
            "$COLUMN_NIP = ?"
        }
        val whereArgs = if (excludeId != -1) {
            arrayOf(nip, excludeId.toString())
        } else {
            arrayOf(nip)
        }
        
        val cursor = db.query(TABLE_DOSEN, null, whereClause, whereArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun isNimExists(nim: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val whereClause = if (excludeId != -1) {
            "$COLUMN_NIM = ? AND $COLUMN_ID != ?"
        } else {
            "$COLUMN_NIM = ?"
        }
        val whereArgs = if (excludeId != -1) {
            arrayOf(nim, excludeId.toString())
        } else {
            arrayOf(nim)
        }
        
        val cursor = db.query(TABLE_MAHASISWA, null, whereClause, whereArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun isKodeMkExists(kode: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val whereClause = if (excludeId != -1) {
            "$COLUMN_KODE_MK = ? AND $COLUMN_ID != ?"
        } else {
            "$COLUMN_KODE_MK = ?"
        }
        val whereArgs = if (excludeId != -1) {
            arrayOf(kode, excludeId.toString())
        } else {
            arrayOf(kode)
        }
        
        val cursor = db.query(TABLE_MATA_KULIAH, null, whereClause, whereArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}