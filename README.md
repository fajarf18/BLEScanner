# TechTest Bluetooth

TechTest Bluetooth adalah aplikasi Android untuk memindai perangkat Bluetooth Low Energy (BLE) di sekitar, melihat kekuatan sinyalnya, dan menyimpan riwayat perangkat yang pernah ditemukan. Aplikasi dibuat dengan Kotlin dan Jetpack Compose.

## Fitur

- Scan BLE secara real-time dengan tombol mulai dan berhenti.
- Menampilkan nama perangkat bila tersedia, alamat Bluetooth, dan nilai RSSI.
- Mengambil nama dari data advertising BLE, cache Android, atau perangkat yang sudah dipair bila alamatnya cocok.
- Pencarian berdasarkan nama atau alamat serta filter kekuatan sinyal.
- Halaman detail/radar untuk melihat kedekatan dan perubahan sinyal target.
- Riwayat lokal perangkat menggunakan Room.
- Dukungan izin Bluetooth untuk Android 12+ dan izin lokasi untuk Android lama.

## Kebutuhan

| Komponen | Versi |
| --- | --- |
| Android Studio | Ladybug atau lebih baru |
| JDK | 17 |
| Android SDK | 35 |
| Minimum Android | API 26 / Android 8.0 |
| Package aplikasi | `com.fajar.neartrace` |

Untuk menguji pemindaian Bluetooth, gunakan ponsel Android fisik. Emulator tidak dapat menggantikan sinyal BLE nyata.

## Kompilasi dengan Android Studio

1. Ekstrak source code, lalu buka folder `NearTrace` melalui **File → Open** di Android Studio.
2. Jika diminta, pilih **JDK 17** dan pasang **Android SDK Platform 35** melalui SDK Manager.
3. Tunggu proses **Gradle Sync** sampai selesai.
4. Hubungkan ponsel dengan USB debugging atau pilih emulator dari daftar device.
5. Klik tombol **Run** ▶ untuk memasang dan menjalankan aplikasi.
6. Saat aplikasi berjalan, setujui izin **Nearby devices**. Pada Android 11 ke bawah, setujui izin lokasi dan aktifkan lokasi sistem bila diperlukan.

## Membuat APK

### Dari Android Studio

Pilih menu:

```text
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

APK debug berada di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Dari terminal

Jalankan perintah berikut dari folder proyek:

```bash
./gradlew assembleDebug
```

Jika `JAVA_HOME` belum mengarah ke JDK 17, atur terlebih dahulu. Contoh Linux/macOS:

```bash
export JAVA_HOME=/path/ke/jdk-17
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew assembleDebug
```

## Menjalankan pemeriksaan

```bash
./gradlew testDebugUnitTest
```

## Arsitektur

Aplikasi menggunakan pola **MVVM**. Alur data utamanya:

```text
AndroidBleScanner → DeviceRepository → ViewModel → Compose UI
```

| Bagian | Tanggung jawab |
| --- | --- |
| `data/ble` | Menjalankan scan BLE dan membaca nama dari advertising atau informasi Android yang tersedia. |
| `data/local` | Menyimpan riwayat perangkat dengan Room (`DeviceEntity`, `DeviceDao`, dan `NearTraceDatabase`). |
| `data/repository` | Menjadi penghubung antara scanner BLE, riwayat lokal, dan ViewModel. |
| `domain` | Menyimpan model `BleDevice` serta aturan zona sinyal berdasarkan RSSI. |
| `ui/screens` | Menyediakan layar scanner, detail/radar, dan riwayat. |
| `ui/components` | Komponen Compose yang digunakan ulang, seperti kartu perangkat dan indikator sinyal. |
| `di` | Konfigurasi dependency injection Hilt. |

`DashboardViewModel` mengelola daftar perangkat aktif dan memperbarui RSSI berdasarkan alamat Bluetooth. `TrackingViewModel` menangani perangkat yang dipilih pada halaman radar. `HistoryViewModel` membaca dan menghapus riwayat lokal.

## Catatan penggunaan BLE

- Nama perangkat BLE tidak wajib ada di paket advertising. Jika tidak tersedia, aplikasi menampilkan **Perangkat BLE tanpa nama** dan alamat Bluetoothnya.
- Satu perangkat fisik dapat menggunakan alamat privat atau lebih dari satu identitas BLE. Karena itu, daftar dikelompokkan berdasarkan alamat Bluetooth yang diterima aplikasi.
- RSSI adalah indikator kekuatan sinyal, bukan pengukuran jarak yang presisi. Dinding, posisi perangkat, baterai, dan kondisi sekitar dapat memengaruhi nilainya.
- Radar menggambarkan kedekatan berdasarkan RSSI, bukan arah perangkat atau lokasi GPS.
