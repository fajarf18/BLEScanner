# Validasi dan checklist pengujian

## Pengujian otomatis
`./gradlew assembleDebug testDebugUnitTest`
- Zona RSSI pada semua titik batas.
- Estimasi formula monoton terhadap sinyal.
- Fallback nama null / kosong.
- Nama perangkat yang tersedia tetap dipertahankan.

Status hasil build tercatat di `BUILD-RESULT.txt` saat paket final dibuat.

## Preview visual
Preview statis diperiksa pada 1440 px dan 390 px. Tidak ada horizontal overflow, glyph hilang atau konten terpotong. PNG/HTML adalah ilustrasi data contoh, bukan bukti pengujian runtime Android.

## Pengujian wajib pada perangkat fisik (belum dilakukan)
1. Android 11 dan Android 12+: izinkan / tolak izin, ulangi izin yang ditolak permanen.
2. Matikan Bluetooth sebelum scan dan saat scan; pastikan pesan sesuai dan tidak crash.
3. Start / Stop beberapa kali; tidak ada scan callback duplikat.
4. Cari nama dan MAC; ubah ambang ≥-80 dBm; periksa urutan menurun RSSI.
5. Pilih target yang advertising. Dekatkan / jauhkan target dan amati perubahan zona.
6. Matikan advertising target: radar menunjukkan Lost setelah 10 detik.
7. Pindah background / foreground serta rotasi: scan lama berhenti; tidak ada kebocoran callback.
8. Tutup / buka aplikasi: riwayat tetap tersimpan dengan waktu terakhir terlihat.
9. Hapus riwayat: batal tidak menghapus; konfirmasi menghapus.
10. Uji dark mode, font scale besar, layar kecil dan landscape.

## Coverage terhadap PDF
| Kebutuhan | Lokasi implementasi |
|---|---|
| BLE real-time, start/stop | AndroidBleScanner, DashboardViewModel |
| Nama, MAC, RSSI, rentang jarak | BleDevice, DeviceCard |
| Search, filter, strongest first | ScannerUiState, DashboardScreen |
| Radar dan stabilitas target | TrackingViewModel, TrackingScreen |
| Persistence dan last seen | DeviceDao, DeviceRepository, HistoryScreen |
| MVVM dan DI | ViewModels, AppModule (Hilt) |
| Error / permission / lifecycle | MainActivity, scanner, LifecycleStartEffect |
| Setup / known issues | README.md |
| Repository URL | Perlu publish ke akun pemilik |
| APK | Artefak debug disertakan; build berhasil |
