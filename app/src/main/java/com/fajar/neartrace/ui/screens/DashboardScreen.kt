package com.fajar.neartrace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.BluetoothSearching
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleStartEffect
import com.fajar.neartrace.ui.components.DeviceCard
import com.fajar.neartrace.ui.components.EmptyRadar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onDevice: (String) -> Unit,
    onStartRequested: ((() -> Unit) -> Unit),
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilter by remember { mutableStateOf(false) }
    LifecycleStartEffect(Unit) { onStopOrDispose { viewModel.stop() } }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Perangkat di sekitar", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(if (state.scanning) "Memindai secara real-time" else "Siap menemukan sinyal BLE", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = if (state.scanning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
                        Icon(if (state.scanning) Icons.Rounded.Radar else Icons.AutoMirrored.Rounded.BluetoothSearching, null, Modifier.padding(11.dp).size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            item {
                Button(
                    onClick = { if (state.scanning) viewModel.stop() else onStartRequested(viewModel::start) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Icon(if (state.scanning) Icons.Rounded.Stop else Icons.Rounded.PlayArrow, null)
                    Spacer(Modifier.width(8.dp)); Text(if (state.scanning) "Hentikan pemindaian" else "Mulai pemindaian", fontWeight = FontWeight.Bold)
                }
            }
            state.error?.let { error -> item {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer) {
                    Row(Modifier.fillMaxWidth().padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(error, Modifier.weight(1f), color = MaterialTheme.colorScheme.onErrorContainer)
                        IconButton(onClick = viewModel::dismissError) { Icon(Icons.Rounded.Close, "Tutup pesan") }
                    }
                }
            } }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.query, onValueChange = viewModel::setQuery,
                        modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(14.dp),
                        placeholder = { Text("Cari nama atau alamat…") }, leadingIcon = { Icon(Icons.Rounded.Search, null) },
                        trailingIcon = if (state.query.isNotEmpty()) { { IconButton(onClick = { viewModel.setQuery("") }) { Icon(Icons.Rounded.Close, null) } } } else null
                    )
                    FilledTonalIconButton(onClick = { showFilter = true }, Modifier.size(56.dp), shape = RoundedCornerShape(14.dp)) {
                        BadgedBox(badge = { if (state.minRssi > -100) Badge() }) { Icon(Icons.Rounded.Tune, "Filter RSSI") }
                    }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${state.filtered.size} perangkat", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Rounded.SwapVert, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(" Sinyal terkuat", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (state.filtered.isEmpty()) item {
                Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    EmptyRadar(Modifier.size(150.dp))
                    Text(if (state.devices.isNotEmpty()) "Tidak ada hasil yang cocok" else if (state.scanning) "Mencari perangkat…" else "Belum ada perangkat", fontWeight = FontWeight.SemiBold)
                    Text(if (state.devices.isNotEmpty()) "Ubah pencarian atau longgarkan filter sinyal" else if (state.scanning) "Perangkat akan muncul saat sinyal diterima" else "Mulai pemindaian untuk melihat perangkat BLE", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(state.filtered, key = { it.address }) { device -> DeviceCard(device, { onDevice(device.address) }) }
            item { Text(if (state.scanning) "Perangkat tanpa sinyal selama 12 detik dikeluarkan dari daftar." else "Pemindaian dijeda saat aplikasi tidak aktif.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }

    if (showFilter) ModalBottomSheet(onDismissRequest = { showFilter = false }) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("Ambang kekuatan sinyal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Tampilkan perangkat dengan sinyal minimal ${state.minRssi} dBm", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(18.dp))
            Slider(value = state.minRssi.toFloat(), onValueChange = { viewModel.setMinRssi(it.toInt()) }, valueRange = -100f..-30f, steps = 13)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("-100 dBm"); Text("-30 dBm") }
            Spacer(Modifier.height(12.dp))
            Button(onClick = { showFilter = false }, Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(14.dp)) { Text("Terapkan") }
        }
    }
}
