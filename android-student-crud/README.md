# Student CRUD Android Application

Aplikasi Android berbasis Kotlin untuk pengelolaan data mahasiswa dengan operasi CRUD (Create, Read, Update, Delete) menggunakan 5 tabel database yang saling berelasi.

## Fitur Utama

### Database Structure (5 Tabel)
1. **Mahasiswa** - Data utama mahasiswa (NIM, nama, alamat, telepon, email, jurusan)
2. **Jurusan** - Data program studi/jurusan (kode, nama, fakultas)
3. **Mata Kuliah** - Data mata kuliah yang tersedia (kode, nama, SKS, semester, dosen)
4. **Nilai** - Data nilai mahasiswa per mata kuliah (mahasiswa, mata kuliah, nilai, grade, tanggal)
5. **Dosen** - Data dosen pengampu mata kuliah (NIP, nama, alamat, telepon, email)

### Operasi CRUD
- **Create**: Tambah data baru untuk setiap entitas
- **Read**: Tampilkan daftar data dengan RecyclerView
- **Update**: Edit data yang sudah ada
- **Delete**: Hapus data dengan konfirmasi

### Validasi Data
- NIM mahasiswa dan NIP dosen harus unik
- Email harus format valid (@)
- Nilai harus dalam range 0-100
- Grade otomatis dihitung berdasarkan nilai
- Semua field required tidak boleh kosong
- Relasi foreign key harus valid

### Teknologi yang Digunakan
- **Language**: Kotlin
- **Database**: SQLite dengan DatabaseHelper
- **UI Components**: 
  - RecyclerView untuk daftar data
  - Material Design EditText dan Button
  - Spinner untuk dropdown selection
  - CardView untuk item list
- **Architecture**: MVC pattern
- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34

## Struktur Project

```
android-student-crud/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/student/crud/
│       │   ├── MainActivity.kt
│       │   ├── JurusanActivity.kt
│       │   ├── DosenActivity.kt
│       │   ├── MahasiswaActivity.kt
│       │   ├── MataKuliahActivity.kt
│       │   ├── NilaiActivity.kt
│       │   ├── models/
│       │   │   ├── Mahasiswa.kt
│       │   │   ├── Jurusan.kt
│       │   │   ├── MataKuliah.kt
│       │   │   ├── Nilai.kt
│       │   │   └── Dosen.kt
│       │   ├── database/
│       │   │   └── DatabaseHelper.kt
│       │   └── adapters/
│       │       ├── MahasiswaAdapter.kt
│       │       ├── JurusanAdapter.kt
│       │       ├── MataKuliahAdapter.kt
│       │       ├── NilaiAdapter.kt
│       │       └── DosenAdapter.kt
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── activity_mahasiswa.xml
│           │   ├── activity_jurusan.xml
│           │   ├── activity_mata_kuliah.xml
│           │   ├── activity_nilai.xml
│           │   ├── activity_dosen.xml
│           │   └── item_list.xml
│           ├── values/
│           │   ├── strings.xml
│           │   ├── colors.xml
│           │   └── themes.xml
│           └── xml/
│               ├── backup_rules.xml
│               └── data_extraction_rules.xml
├── build.gradle
└── settings.gradle
```

## Cara Penggunaan

### 1. Setup Project
1. Buka Android Studio
2. Import project dari folder `android-student-crud`
3. Sync project dengan Gradle
4. Run aplikasi di emulator atau device

### 2. Navigasi Aplikasi
- **Dashboard Utama**: Pilih menu untuk mengelola data
- **Kelola Jurusan**: Tambah/edit/hapus data jurusan terlebih dahulu
- **Kelola Dosen**: Tambah/edit/hapus data dosen
- **Kelola Mata Kuliah**: Tambah mata kuliah dengan memilih dosen
- **Kelola Mahasiswa**: Tambah mahasiswa dengan memilih jurusan
- **Kelola Nilai**: Input nilai dengan memilih mahasiswa dan mata kuliah

### 3. Urutan Input Data
1. **Jurusan** (tidak ada dependency)
2. **Dosen** (tidak ada dependency)
3. **Mahasiswa** (memerlukan Jurusan)
4. **Mata Kuliah** (memerlukan Dosen)
5. **Nilai** (memerlukan Mahasiswa dan Mata Kuliah)

## Fitur Khusus

### Automatic Grade Calculation
Nilai grade otomatis dihitung berdasarkan nilai angka:
- A: 85-100
- B: 70-84
- C: 60-69
- D: 50-59
- E: 0-49

### Data Relationship
- Mahasiswa berelasi dengan Jurusan (foreign key)
- Mata Kuliah berelasi dengan Dosen (foreign key)
- Nilai berelasi dengan Mahasiswa dan Mata Kuliah (foreign key)

### User Interface
- Material Design components
- Responsive layout dengan ScrollView
- Toast notifications untuk feedback
- Dialog konfirmasi untuk delete
- Error handling dengan input validation

## Database Schema

### Tabel Jurusan
```sql
CREATE TABLE jurusan (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    kode_jurusan TEXT UNIQUE NOT NULL,
    nama_jurusan TEXT NOT NULL,
    fakultas TEXT NOT NULL
)
```

### Tabel Dosen
```sql
CREATE TABLE dosen (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nip TEXT UNIQUE NOT NULL,
    nama_dosen TEXT NOT NULL,
    alamat TEXT NOT NULL,
    telepon TEXT NOT NULL,
    email TEXT NOT NULL
)
```

### Tabel Mahasiswa
```sql
CREATE TABLE mahasiswa (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nim TEXT UNIQUE NOT NULL,
    nama TEXT NOT NULL,
    alamat TEXT NOT NULL,
    telepon TEXT NOT NULL,
    email TEXT NOT NULL,
    jurusan_id INTEGER NOT NULL,
    FOREIGN KEY(jurusan_id) REFERENCES jurusan(id)
)
```

### Tabel Mata Kuliah
```sql
CREATE TABLE mata_kuliah (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    kode_mk TEXT UNIQUE NOT NULL,
    nama_mk TEXT NOT NULL,
    sks INTEGER NOT NULL,
    semester INTEGER NOT NULL,
    dosen_id INTEGER NOT NULL,
    FOREIGN KEY(dosen_id) REFERENCES dosen(id)
)
```

### Tabel Nilai
```sql
CREATE TABLE nilai (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    mahasiswa_id INTEGER NOT NULL,
    mata_kuliah_id INTEGER NOT NULL,
    nilai REAL NOT NULL,
    grade TEXT NOT NULL,
    tanggal_input TEXT NOT NULL,
    FOREIGN KEY(mahasiswa_id) REFERENCES mahasiswa(id),
    FOREIGN KEY(mata_kuliah_id) REFERENCES mata_kuliah(id)
)
```

## Dependencies

```gradle
dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
}
```

## Kontributor
- Dikembangkan untuk memenuhi requirements sistem manajemen data mahasiswa
- Menggunakan best practices Android development dengan Kotlin
- Implementasi MVC architecture pattern
- Komprehensif CRUD operations dengan SQLite database