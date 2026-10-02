# BLE Scanner

BLE Scannner adalah aplikasi Android untuk memindai perangkat **Bluetooth Low Energy (BLE)** di sekitar. Hasil pemindaian ditampilkan sebagai daftar perangkat dengan alamat Bluetooth, nama bila tersedia, RSSI, dan zona kedekatan. Pengguna dapat memilih perangkat untuk melihat radar sinyal, lalu melihat kembali perangkat tersebut pada halaman riwayat.

- **Package:** `com.fajar.neartrace`
- **Versi:** 1.0.0
- **Minimum Android:** Android 8.0 (API 26)
- **Release APK:** [GitHub Release v1.0.0](https://github.com/fajarf18/BLEScanner/releases/tag/v1.0.0)
- **Rekayasa Tampilan:** ![Rekayasa] (docs/ui-mobile.png)
## Fitur aplikasi

1. **Scanner BLE**
   - Memulai dan menghentikan pemindaian secara manual.
   - Menampilkan perangkat berdasarkan hasil advertising BLE yang diterima.
   - Mengurutkan daftar dari RSSI terkuat ke terlemah.
   - Menghapus perangkat dari daftar aktif jika tidak ada paket baru selama 12 detik.

2. **Pencarian dan filter**
   - Pencarian berdasarkan nama perangkat atau alamat Bluetooth.
   - Filter ambang RSSI agar daftar hanya menampilkan perangkat dengan sinyal minimum tertentu.

3. **Detail perangkat / radar**
   - Menampilkan status kedekatan berdasarkan RSSI.
   - Menampilkan estimasi zona: sangat dekat, dekat, cukup dekat, lemah, sangat lemah, atau sinyal hilang.
   - Menampilkan perubahan sinyal agar pengguna dapat melihat apakah perangkat mendekat atau menjauh.

4. **Riwayat lokal**
   - Menyimpan alamat, nama yang tersedia, RSSI terakhir, RSSI terkuat, dan waktu terakhir perangkat terlihat.
   - Riwayat tetap tersimpan setelah aplikasi ditutup karena memakai database Room.

5. **Penanganan nama perangkat**
   - Mencoba mengambil nama dari `BluetoothDevice.name`.
   - Membaca *Complete Local Name* atau *Shortened Local Name* dari data advertising BLE.
   - Menggunakan nama perangkat yang pernah dipair apabila alamat Bluetoothnya cocok.
   - Jika nama tidak tersedia, perangkat tetap ditampilkan dengan label **Perangkat BLE tanpa nama** dan alamat Bluetoothnya.

---

## Setup dan cara menjalankan

### Prasyarat

| Komponen | Kebutuhan |
| --- | --- |
| IDE | Android Studio Ladybug atau versi lebih baru |
| Java | JDK 17 |
| Android SDK | Platform API 35 dan Build Tools yang direkomendasikan Android Studio |
| Gradle | Gradle Wrapper sudah disertakan pada proyek |
| Perangkat uji | Ponsel Android fisik yang memiliki Bluetooth LE |

> Emulator dapat menjalankan tampilan aplikasi, tetapi tidak dapat dipakai sebagai pengganti pengujian sinyal BLE nyata.

### Menjalankan dari Android Studio

1. Clone repository atau ekstrak source code.
2. Buka Android Studio, pilih **File → Open**, lalu pilih folder `NearTrace`.
3. Saat Android Studio meminta konfigurasi SDK/JDK:
   - pilih **JDK 17**;
   - instal **Android SDK Platform 35** bila belum tersedia.
4. Tunggu proses **Gradle Sync** selesai tanpa error.
5. Hubungkan ponsel Android melalui USB dan aktifkan **USB debugging**, atau pilih perangkat target pada toolbar Android Studio.
6. Klik tombol **Run** ▶.
7. Saat aplikasi dibuka, setujui izin Bluetooth yang diminta.

### Izin Bluetooth

| Versi Android | Izin yang diperlukan | Catatan |
| --- | --- | --- |
| Android 12 / API 31 ke atas | `BLUETOOTH_SCAN` dan `BLUETOOTH_CONNECT` | Ditampilkan kepada pengguna sebagai izin **Nearby devices**. |
| Android 11 / API 30 ke bawah | Bluetooth dan izin lokasi | Android lama mensyaratkan izin lokasi untuk scan BLE. Lokasi sistem juga mungkin perlu aktif. |

### Build APK debug

Melalui Android Studio:

```text
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

Lokasi file APK debug:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Melalui terminal pada root proyek:

```bash
./gradlew assembleDebug
```

Jika terminal memakai JDK lain, arahkan `JAVA_HOME` ke JDK 17 terlebih dahulu:

```bash
export JAVA_HOME=/path/ke/jdk-17
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew assembleDebug
```

### Menjalankan test

```bash
./gradlew testDebugUnitTest
```

Test unit mencakup aturan batas zona RSSI, fallback nama perangkat, dan perhitungan estimasi kedekatan.

---

## Arsitektur dan alur data

Aplikasi memakai pola **MVVM (Model–View–ViewModel)**. Tujuannya agar kode UI tidak langsung mengakses Bluetooth atau database.

```text
Android BLE API
      ↓
AndroidBleScanner
      ↓
DeviceRepository
      ↓
ViewModel (StateFlow)
      ↓
Jetpack Compose UI
      ↓
Room database untuk riwayat
```

### Pembagian package

| Lokasi | Isi dan tanggung jawab |
| --- | --- |
| `data/ble` | `AndroidBleScanner` membungkus callback Android BLE menjadi `Flow<BleDevice>`. Kelas ini memulai/menghentikan scan, membaca RSSI, alamat, dan nama yang tersedia. |
| `data/local` | `DeviceEntity`, `DeviceDao`, dan `NearTraceDatabase` untuk menyimpan riwayat menggunakan Room. |
| `data/repository` | `DeviceRepository` menghubungkan scanner dengan database dan mengatur penyimpanan perangkat yang ditemukan. |
| `domain` | Model `BleDevice` dan `SignalZone`. Bagian ini berisi aturan zona RSSI serta fallback tampilan nama. |
| `ui/screens` | `DashboardScreen`, `TrackingScreen`, dan `HistoryScreen` beserta ViewModel masing-masing. |
| `ui/components` | Komponen Compose yang dipakai ulang, seperti kartu perangkat dan indikator kekuatan sinyal. |
| `ui/theme` | Warna dan tema Compose. |
| `di` | Konfigurasi Hilt untuk menyediakan scanner, repository, DAO, dan database. |

### Alur saat scan

1. Pengguna menekan **Mulai pemindaian** pada dashboard.
2. `DashboardViewModel` memanggil `DeviceRepository.scan()`.
3. Repository meneruskan hasil dari `AndroidBleScanner`.
4. Setiap `ScanResult` diubah menjadi `BleDevice`.
5. Daftar di ViewModel dikelompokkan menggunakan alamat Bluetooth. Jika alamat yang sama muncul lagi, RSSI diperbarui tanpa membuat baris baru.
6. Hasil terbaru disimpan ke Room sebagai riwayat.
7. Compose membaca `StateFlow` dan memperbarui tampilan daftar secara otomatis.

---

## Library dan alasan pemilihan

| Teknologi / library | Digunakan untuk | Alasan pemilihan |
| --- | --- | --- |
| **Kotlin 2.0.21** | Bahasa utama | Bahasa modern yang direkomendasikan untuk Android; null safety membantu saat nama perangkat atau hasil BLE tidak tersedia. |
| **Jetpack Compose** | Pembuatan UI | UI berbasis state lebih mudah diperbarui saat RSSI berubah terus-menerus. Mengurangi kebutuhan adapter dan XML layout. |
| **Material 3** | Komponen UI | Menyediakan komponen Android yang konsisten, aksesibel, dan mendukung light/dark theme. |
| **Android Bluetooth LE API** | Pemindaian BLE | API bawaan Android untuk menerima advertising packet dan RSSI. Tidak membutuhkan library pihak ketiga. |
| **ViewModel + StateFlow** | State halaman | State scan tetap terkelola saat perubahan konfigurasi dan UI bisa mengamati pembaruan secara reaktif. |
| **Kotlin Coroutines + Flow** | Proses asynchronous | Callback scan diubah menjadi `Flow`, sehingga proses dapat dibatalkan saat layar berhenti atau scan dihentikan. |
| **Hilt 2.52** | Dependency injection | Menghindari pembuatan scanner dan database secara manual pada banyak kelas; memudahkan pengujian dan pengelolaan lifecycle. |
| **Room 2.6.1** | Database lokal | Menyimpan riwayat secara terstruktur dan aman tanpa backend/server. |
| **KSP** | Code generation | Dipakai oleh Hilt dan Room untuk menghasilkan kode saat build dengan waktu build yang lebih baik dibanding pemrosesan annotation lama. |
| **Navigation Compose** | Perpindahan halaman | Menangani navigasi dashboard, detail/radar, dan riwayat dalam aplikasi Compose. |
| **JUnit 4** | Unit test | Digunakan untuk memverifikasi aturan domain yang tidak memerlukan perangkat Android fisik. |

---

## Known issues dan keterbatasan

1. **Nama perangkat tidak selalu tersedia**  
   Nama pada BLE bersifat opsional. Perangkat yang tidak mengirim nama pada advertising, belum pernah dipair, dan tidak memiliki nama cache Android akan muncul sebagai **Perangkat BLE tanpa nama**.

2. **Satu perangkat fisik dapat terlihat sebagai beberapa entri**  
   Beberapa perangkat menggunakan alamat MAC privat/acak untuk privasi atau memiliki lebih dari satu identitas BLE. Aplikasi mengelompokkan data berdasarkan alamat yang diterima; alamat yang berbeda dianggap entri berbeda.

3. **Smartwatch/headset dapat tidak muncul**  
   Perangkat hanya terlihat bila sedang mengirim BLE advertising. Beberapa smartwatch atau headset berhenti advertising setelah terhubung ke ponsel, saat hemat daya aktif, atau ketika tidak berada pada mode pairing.

4. **RSSI tidak sama dengan jarak pasti**  
   RSSI dipengaruhi penghalang, posisi antena, tubuh pengguna, baterai, dan interferensi radio. Radar dipakai sebagai indikator kedekatan, bukan pengukur jarak profesional.

5. **Tidak ada koneksi otomatis ke perangkat**  
   Aplikasi tidak menjalankan pairing atau koneksi GATT ke setiap perangkat yang ditemukan. Keputusan ini menghindari banyak dialog, pemakaian baterai berlebih, serta risiko mengganggu koneksi pengguna.

6. **Pengujian BLE tidak representatif pada emulator**  
   Fitur scan harus diuji di ponsel fisik. Emulator hanya berguna untuk menguji UI dan alur navigasi.

---

## Asumsi teknis dan kendala pengerjaan

### Asumsi

- Perangkat target mendukung BLE dan sedang mengirim advertising packet.
- Alamat Bluetooth hasil scan dipakai sebagai identitas perangkat selama alamat tersebut tidak berubah.
- Pengguna memberi izin Bluetooth yang diperlukan sebelum memulai scan.
- Penyimpanan riwayat bersifat lokal; tidak ada sinkronisasi cloud dan tidak ada data scan yang dikirim ke server.
- Zona kedekatan ditentukan dari rentang RSSI. Nilai tersebut digunakan untuk tampilan dan tidak diklaim sebagai jarak fisik yang presisi.

### Kendala

- Implementasi izin Bluetooth berbeda antara Android 11 ke bawah dan Android 12 ke atas, sehingga aplikasi perlu mendukung dua pola izin.
- Isi advertising packet dikendalikan oleh perangkat lain. Aplikasi tidak dapat memaksa perangkat mengirim nama atau identitas tetap.
- MAC address privat dapat membuat satu perangkat yang sama tampak baru pada sesi scan berikutnya.
- Data RSSI berubah cepat. UI perlu memperbarui daftar tanpa menduplikasi perangkat dengan alamat yang sama.
- Validasi akhir perilaku radio tetap bergantung pada perangkat fisik dan kondisi lingkungan saat pengujian.

---

## Struktur proyek

```text
NearTrace/
├── app/
│   └── src/
│       ├── main/java/com/fajar/neartrace/
│       │   ├── data/
│       │   ├── di/
│       │   ├── domain/
│       │   └── ui/
│       ├── main/res/
│       └── test/
├── README.md
└── build.gradle.kts
```
