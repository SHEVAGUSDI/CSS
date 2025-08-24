package com.student.crud.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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
        const val TABLE_JURUSAN = "jurusan"
        const val TABLE_DOSEN = "dosen"
        const val TABLE_MAHASISWA = "mahasiswa"
        const val TABLE_MATA_KULIAH = "mata_kuliah"
        const val TABLE_NILAI = "nilai"

        // Common columns
        const val COLUMN_ID = "id"

        // Jurusan table columns
        const val COLUMN_KODE_JURUSAN = "kode_jurusan"
        const val COLUMN_NAMA_JURUSAN = "nama_jurusan"
        const val COLUMN_FAKULTAS = "fakultas"

        // Dosen table columns
        const val COLUMN_NIP = "nip"
        const val COLUMN_NAMA_DOSEN = "nama_dosen"
        const val COLUMN_ALAMAT_DOSEN = "alamat"
        const val COLUMN_TELEPON_DOSEN = "telepon"
        const val COLUMN_EMAIL_DOSEN = "email"

        // Mahasiswa table columns
        const val COLUMN_NIM = "nim"
        const val COLUMN_NAMA_MAHASISWA = "nama"
        const val COLUMN_ALAMAT_MAHASISWA = "alamat"
        const val COLUMN_TELEPON_MAHASISWA = "telepon"
        const val COLUMN_EMAIL_MAHASISWA = "email"
        const val COLUMN_JURUSAN_ID = "jurusan_id"

        // Mata Kuliah table columns
        const val COLUMN_KODE_MK = "kode_mk"
        const val COLUMN_NAMA_MK = "nama_mk"
        const val COLUMN_SKS = "sks"
        const val COLUMN_SEMESTER = "semester"
        const val COLUMN_DOSEN_ID = "dosen_id"

        // Nilai table columns
        const val COLUMN_MAHASISWA_ID = "mahasiswa_id"
        const val COLUMN_MATA_KULIAH_ID = "mata_kuliah_id"
        const val COLUMN_NILAI = "nilai"
        const val COLUMN_GRADE = "grade"
        const val COLUMN_TANGGAL_INPUT = "tanggal_input"
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
                $COLUMN_ALAMAT_DOSEN TEXT NOT NULL,
                $COLUMN_TELEPON_DOSEN TEXT NOT NULL,
                $COLUMN_EMAIL_DOSEN TEXT NOT NULL
            )
        """.trimIndent()

        // Create Mahasiswa table
        val createMahasiswaTable = """
            CREATE TABLE $TABLE_MAHASISWA (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NIM TEXT UNIQUE NOT NULL,
                $COLUMN_NAMA_MAHASISWA TEXT NOT NULL,
                $COLUMN_ALAMAT_MAHASISWA TEXT NOT NULL,
                $COLUMN_TELEPON_MAHASISWA TEXT NOT NULL,
                $COLUMN_EMAIL_MAHASISWA TEXT NOT NULL,
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

    // CRUD Operations for Jurusan
    fun insertJurusan(jurusan: Jurusan): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KODE_JURUSAN, jurusan.kodeJurusan)
            put(COLUMN_NAMA_JURUSAN, jurusan.namaJurusan)
            put(COLUMN_FAKULTAS, jurusan.fakultas)
        }
        return db.insert(TABLE_JURUSAN, null, values)
    }

    fun getAllJurusan(): List<Jurusan> {
        val list = mutableListOf<Jurusan>()
        val db = readableDatabase
        val cursor = db.query(TABLE_JURUSAN, null, null, null, null, null, COLUMN_NAMA_JURUSAN)
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToJurusan(it))
            }
        }
        return list
    }

    fun updateJurusan(jurusan: Jurusan): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KODE_JURUSAN, jurusan.kodeJurusan)
            put(COLUMN_NAMA_JURUSAN, jurusan.namaJurusan)
            put(COLUMN_FAKULTAS, jurusan.fakultas)
        }
        return db.update(TABLE_JURUSAN, values, "$COLUMN_ID = ?", arrayOf(jurusan.id.toString()))
    }

    fun deleteJurusan(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_JURUSAN, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun cursorToJurusan(cursor: Cursor): Jurusan {
        return Jurusan(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            kodeJurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_JURUSAN)),
            namaJurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_JURUSAN)),
            fakultas = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FAKULTAS))
        )
    }

    // CRUD Operations for Dosen
    fun insertDosen(dosen: Dosen): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIP, dosen.nip)
            put(COLUMN_NAMA_DOSEN, dosen.namaDosen)
            put(COLUMN_ALAMAT_DOSEN, dosen.alamat)
            put(COLUMN_TELEPON_DOSEN, dosen.telepon)
            put(COLUMN_EMAIL_DOSEN, dosen.email)
        }
        return db.insert(TABLE_DOSEN, null, values)
    }

    fun getAllDosen(): List<Dosen> {
        val list = mutableListOf<Dosen>()
        val db = readableDatabase
        val cursor = db.query(TABLE_DOSEN, null, null, null, null, null, COLUMN_NAMA_DOSEN)
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToDosen(it))
            }
        }
        return list
    }

    fun updateDosen(dosen: Dosen): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIP, dosen.nip)
            put(COLUMN_NAMA_DOSEN, dosen.namaDosen)
            put(COLUMN_ALAMAT_DOSEN, dosen.alamat)
            put(COLUMN_TELEPON_DOSEN, dosen.telepon)
            put(COLUMN_EMAIL_DOSEN, dosen.email)
        }
        return db.update(TABLE_DOSEN, values, "$COLUMN_ID = ?", arrayOf(dosen.id.toString()))
    }

    fun deleteDosen(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_DOSEN, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun cursorToDosen(cursor: Cursor): Dosen {
        return Dosen(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            nip = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIP)),
            namaDosen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_DOSEN)),
            alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT_DOSEN)),
            telepon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPON_DOSEN)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_DOSEN))
        )
    }

    // CRUD Operations for Mahasiswa
    fun insertMahasiswa(mahasiswa: Mahasiswa): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIM, mahasiswa.nim)
            put(COLUMN_NAMA_MAHASISWA, mahasiswa.nama)
            put(COLUMN_ALAMAT_MAHASISWA, mahasiswa.alamat)
            put(COLUMN_TELEPON_MAHASISWA, mahasiswa.telepon)
            put(COLUMN_EMAIL_MAHASISWA, mahasiswa.email)
            put(COLUMN_JURUSAN_ID, mahasiswa.jurusanId)
        }
        return db.insert(TABLE_MAHASISWA, null, values)
    }

    fun getAllMahasiswa(): List<Mahasiswa> {
        val list = mutableListOf<Mahasiswa>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MAHASISWA, null, null, null, null, null, COLUMN_NAMA_MAHASISWA)
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToMahasiswa(it))
            }
        }
        return list
    }

    fun updateMahasiswa(mahasiswa: Mahasiswa): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NIM, mahasiswa.nim)
            put(COLUMN_NAMA_MAHASISWA, mahasiswa.nama)
            put(COLUMN_ALAMAT_MAHASISWA, mahasiswa.alamat)
            put(COLUMN_TELEPON_MAHASISWA, mahasiswa.telepon)
            put(COLUMN_EMAIL_MAHASISWA, mahasiswa.email)
            put(COLUMN_JURUSAN_ID, mahasiswa.jurusanId)
        }
        return db.update(TABLE_MAHASISWA, values, "$COLUMN_ID = ?", arrayOf(mahasiswa.id.toString()))
    }

    fun deleteMahasiswa(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_MAHASISWA, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun cursorToMahasiswa(cursor: Cursor): Mahasiswa {
        return Mahasiswa(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            nim = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NIM)),
            nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_MAHASISWA)),
            alamat = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT_MAHASISWA)),
            telepon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEPON_MAHASISWA)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_MAHASISWA)),
            jurusanId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_JURUSAN_ID))
        )
    }

    // CRUD Operations for MataKuliah
    fun insertMataKuliah(mataKuliah: MataKuliah): Long {
        val db = writableDatabase
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
        val list = mutableListOf<MataKuliah>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MATA_KULIAH, null, null, null, null, null, COLUMN_NAMA_MK)
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToMataKuliah(it))
            }
        }
        return list
    }

    fun updateMataKuliah(mataKuliah: MataKuliah): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KODE_MK, mataKuliah.kodeMk)
            put(COLUMN_NAMA_MK, mataKuliah.namaMk)
            put(COLUMN_SKS, mataKuliah.sks)
            put(COLUMN_SEMESTER, mataKuliah.semester)
            put(COLUMN_DOSEN_ID, mataKuliah.dosenId)
        }
        return db.update(TABLE_MATA_KULIAH, values, "$COLUMN_ID = ?", arrayOf(mataKuliah.id.toString()))
    }

    fun deleteMataKuliah(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_MATA_KULIAH, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun cursorToMataKuliah(cursor: Cursor): MataKuliah {
        return MataKuliah(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            kodeMk = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_MK)),
            namaMk = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_MK)),
            sks = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SKS)),
            semester = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SEMESTER)),
            dosenId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DOSEN_ID))
        )
    }

    // CRUD Operations for Nilai
    fun insertNilai(nilai: Nilai): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MAHASISWA_ID, nilai.mahasiswaId)
            put(COLUMN_MATA_KULIAH_ID, nilai.mataKuliahId)
            put(COLUMN_NILAI, nilai.nilai)
            put(COLUMN_GRADE, nilai.grade)
            put(COLUMN_TANGGAL_INPUT, nilai.tanggalInput)
        }
        return db.insert(TABLE_NILAI, null, values)
    }

    fun getAllNilai(): List<Nilai> {
        val list = mutableListOf<Nilai>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NILAI, null, null, null, null, null, COLUMN_TANGGAL_INPUT)
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToNilai(it))
            }
        }
        return list
    }

    fun updateNilai(nilai: Nilai): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MAHASISWA_ID, nilai.mahasiswaId)
            put(COLUMN_MATA_KULIAH_ID, nilai.mataKuliahId)
            put(COLUMN_NILAI, nilai.nilai)
            put(COLUMN_GRADE, nilai.grade)
            put(COLUMN_TANGGAL_INPUT, nilai.tanggalInput)
        }
        return db.update(TABLE_NILAI, values, "$COLUMN_ID = ?", arrayOf(nilai.id.toString()))
    }

    fun deleteNilai(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NILAI, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun cursorToNilai(cursor: Cursor): Nilai {
        return Nilai(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            mahasiswaId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MAHASISWA_ID)),
            mataKuliahId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MATA_KULIAH_ID)),
            nilai = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NILAI)),
            grade = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GRADE)),
            tanggalInput = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL_INPUT))
        )
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}