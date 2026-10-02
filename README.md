# TechTest Bluetooth

Aplikasi Android untuk melihat perangkat Bluetooth Low Energy (BLE) di sekitar dan memantau kekuatan sinyalnya. Proyek ini dibuat dengan Kotlin dan Jetpack Compose.

## Yang bisa dilakukan

- Memindai perangkat BLE di sekitar.
- Menampilkan nama perangkat, alamat MAC, dan nilai RSSI.
- Mencari perangkat berdasarkan nama atau alamat MAC.
- Menyaring perangkat berdasarkan kekuatan sinyal.
- Membuka detail perangkat untuk melihat radar kedekatan dan perubahan sinyal.
- Menyimpan perangkat yang pernah terdeteksi ke riwayat lokal.
- Menghapus satu item atau seluruh riwayat bila diperlukan.

## Menjalankan proyek

1. Ekstrak proyek lalu buka foldernya di Android Studio.
2. Saat diminta, gunakan **JDK 17** dan instal **Android SDK 35**.
3. Tunggu Gradle selesai sinkronisasi.
4. Pilih ponsel Android atau emulator dari toolbar, lalu klik **Run**.
5. Untuk pemindaian BLE yang sebenarnya, gunakan ponsel fisik. Emulator tidak menyediakan sinyal Bluetooth nyata.
6. Izinkan akses **Nearby devices** di Android 12 ke atas. Pada Android 11 ke bawah, aplikasi akan meminta izin lokasi untuk pemindaian Bluetooth.

## Membuat APK

Dari Android Studio pilih:

**Build → Build Bundle(s) / APK(s) → Build APK(s)**

APK debug akan dibuat di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Atau dari terminal di folder proyek:

```bash
./gradlew assembleDebug
```

## Catatan teknis

- Minimum Android: API 26 (Android 8.0).
- Database Room dipakai untuk menyimpan riwayat di perangkat, jadi tidak ada data yang dikirim ke server.
- Nilai RSSI hanya menunjukkan perkiraan kedekatan. Nilainya dapat berubah karena jarak, penghalang, orientasi perangkat, dan kondisi sekitar.
- Radar menunjukkan kekuatan/kedekatan sinyal, bukan arah perangkat atau lokasi GPS.

## Struktur singkat

```text
AndroidBleScanner → DeviceRepository → ViewModel → Compose UI
```

- `data/ble`: proses pemindaian Bluetooth.
- `data/local`: penyimpanan riwayat dengan Room.
- `ui/screens`: halaman pemindai, pelacakan, dan riwayat.
- `domain`: model perangkat serta aturan zona sinyal.

## Menjalankan pemeriksaan

```bash
./gradlew testDebugUnitTest
```
