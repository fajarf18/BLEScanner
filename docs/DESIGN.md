# Desain UI/UX — NearTrace

## Acuan
Study Case Mobile Engineer.pdf: scanner, filter dan urutan RSSI, radar target, penyimpanan riwayat lokal. Nama NearTrace adalah nama kerja, bukan nama yang ditentukan PDF.

## Arah visual
Antarmuka utilitas yang tenang dan spesifik untuk membaca sinyal: canvas hampir putih, aksen hijau hutan, kartu berbatas tipis, ikon Material, ruang kosong yang terukur. Tanpa gradien dekoratif, glassmorphism, ilustrasi AI generik, atau animasi yang mengganggu.

- Aksi utama hanya satu pada scanner: mulai / berhenti.
- RSSI mentah selalu terpisah dari sinyal yang dihaluskan.
- Zona memakai label teks dan warna, bukan warna saja.
- Radar adalah indikator kedekatan, bukan kompas. Titik tidak mengukur arah perangkat.
- Estimasi utama memakai rentang zona sesuai brief; formula path-loss tersedia di model untuk eksperimen, bukan ukuran presisi yang ditampilkan.
- Grafik menampilkan 24 sampel RSSI terakhir. Stabilitas baru tampil setelah minimal lima sampel; ini indeks variasi RSSI, bukan confidence pengukuran.
- Target tidak menerima advertising selama 10 detik: tampilkan sinyal hilang dan sembunyikan angka live.
- Dashboard menghapus perangkat tanpa sinyal selama 12 detik dari daftar aktif.
- Pemindaian dijeda saat layar tidak aktif. Scanner dimulai kembali secara manual; layar tracking memulai scan saat kembali aktif.
- Riwayat selalu lokal, hapus seluruh riwayat membutuhkan konfirmasi.
- Kontrol sentuh minimal 48 dp; layar radar dapat digulir untuk layar pendek dan orientasi landscape.

## Preview
`ui-preview.html` dan `ui-preview.png` adalah ilustrasi visual dengan data contoh, bukan screenshot runtime Android. Perbedaan tipografi, insets sistem dan rendering Material 3 dapat terjadi. Aplikasi sebenarnya ditulis Kotlin/Jetpack Compose.

## Batas RSSI
Tabel PDF memiliki batas yang saling bertumpang tindih. Implementasi memakai zona lebih dekat untuk nilai tepat pada batas: -30, -50, -70, -80, dan -90. Unit test memeriksa batas ini.
