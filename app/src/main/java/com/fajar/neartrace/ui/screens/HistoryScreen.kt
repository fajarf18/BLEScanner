package com.fajar.neartrace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fajar.neartrace.ui.components.DeviceCard
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(onDevice: (String) -> Unit, viewModel: HistoryViewModel = hiltViewModel()) {
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    var confirm by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("Riwayat perangkat", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Tersimpan lokal di perangkat", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                if (devices.isNotEmpty()) IconButton(onClick = { confirm = true }) { Icon(Icons.Rounded.DeleteSweep, "Hapus semua") }
            }
        }
        if (devices.isEmpty()) item {
            Column(Modifier.fillMaxWidth().padding(top = 96.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.History, null, Modifier.size(52.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(14.dp)); Text("Riwayat masih kosong", fontWeight = FontWeight.SemiBold)
                Text("Perangkat yang terdeteksi akan tersimpan otomatis", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(devices, key = { it.address }) { device ->
            Column {
                Text("Terakhir terlihat ${relativeTime(device.lastSeen)}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp)); DeviceCard(device, { onDevice(device.address) })
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
    if (confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text("Hapus seluruh riwayat?") }, text = { Text("Tindakan ini tidak dapat dibatalkan.") }, confirmButton = { TextButton(onClick = { viewModel.clear(); confirm = false }) { Text("Hapus") } }, dismissButton = { TextButton(onClick = { confirm = false }) { Text("Batal") } })
}

private fun relativeTime(time: Long): String {
    val delta = System.currentTimeMillis() - time
    return when { delta < 60_000 -> "baru saja"; delta < 3_600_000 -> "${delta / 60_000} menit lalu"; delta < 86_400_000 -> "${delta / 3_600_000} jam lalu"; else -> SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(time)) }
}
