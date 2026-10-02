package com.fajar.neartrace.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fajar.neartrace.domain.SignalZone
import com.fajar.neartrace.ui.components.ZoneBadge
import com.fajar.neartrace.ui.components.color
import com.fajar.neartrace.ui.theme.Forest
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TrackingScreen(onBack: () -> Unit, viewModel: TrackingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleStartEffect(Unit) { viewModel.start(); onStopOrDispose { viewModel.stop() } }
    val device = state.device
    val zone = if (state.lost) SignalZone.LOST else device?.zone ?: SignalZone.LOST
    Scaffold(topBar = {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali") }
            Column(Modifier.weight(1f)) { Text("Lacak perangkat", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("Pembaruan sinyal langsung", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Box(Modifier.size(9.dp).background(if (zone == SignalZone.LOST) MaterialTheme.colorScheme.error else Forest, CircleShape))
            Spacer(Modifier.width(16.dp))
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(8.dp))
            Text(device?.displayName ?: "Menunggu sinyal…", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(device?.address ?: "Target sedang dicari", color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = .5.sp)
            Spacer(Modifier.height(16.dp))
            RadarView(if (state.lost) -100 else state.smoothedRssi ?: -100, Modifier.fillMaxWidth().heightIn(max = 310.dp).aspectRatio(1f))
            Text("Indikator kedekatan · bukan arah lokasi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error); Spacer(Modifier.height(8.dp)) }
            if (device != null) {
                Text(zone.distanceLabel, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                ZoneBadge(zone)
                Spacer(Modifier.height(18.dp))
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                    Column(Modifier.padding(18.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Metric("RSSI mentah", if (state.lost) "—" else "${device.rssi} dBm")
                            Metric("RSSI halus", if (state.lost) "—" else "${state.smoothedRssi} dBm")
                            Metric("Stabilitas", state.stability?.let { "$it%" } ?: "—")
                        }
                        Spacer(Modifier.height(18.dp))
                        SignalChart(state.samples, Modifier.fillMaxWidth().height(66.dp))
                        Text("RSSI mentah · 24 sampel terakhir", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Shield, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Text("Jarak bukan ukuran presisi. Dinding dan interferensi memengaruhi RSSI. Stabilitas adalah indeks variasi sinyal, bukan akurasi jarak.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable private fun Metric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
}

@Composable private fun RadarView(rssi: Int, modifier: Modifier = Modifier) {
    val zone = SignalZone.from(rssi)
    val targetRadius = when (zone) { SignalZone.VERY_CLOSE -> .12f; SignalZone.CLOSE -> .23f; SignalZone.GOOD -> .36f; SignalZone.WEAK -> .46f; else -> .52f }
    val radius by animateFloatAsState(targetRadius, spring(dampingRatio = .7f), label = "radius")
    val color = zone.color()
    val surface = MaterialTheme.colorScheme.surface
    Canvas(modifier) {
        val max = size.minDimension / 2f
        repeat(4) { index ->
            drawCircle(color.copy(alpha = .10f + index * .025f), radius = max * (.22f + index * .22f), style = Stroke(width = 2f))
        }
        drawCircle(color.copy(alpha = .08f), radius = max * .22f)
        drawCircle(Forest, radius = 8.dp.toPx(), center = center)
        val angle = -Math.PI / 4
        val dot = Offset(center.x + cos(angle).toFloat() * max * radius * 1.7f, center.y + sin(angle).toFloat() * max * radius * 1.7f)
        drawLine(color.copy(alpha = .35f), center, dot, strokeWidth = 2.dp.toPx())
        drawCircle(color.copy(alpha = .18f), radius = 20.dp.toPx(), center = dot)
        drawCircle(color, radius = 8.dp.toPx(), center = dot)
        drawCircle(surface, radius = 3.dp.toPx(), center = dot)
    }
}

@Composable private fun SignalChart(samples: List<Int>, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outline
    Canvas(modifier) {
        repeat(3) { i -> drawLine(grid.copy(alpha = .45f), Offset(0f, size.height * i / 2), Offset(size.width, size.height * i / 2), 1f) }
        if (samples.size > 1) {
            val path = Path()
            samples.forEachIndexed { index, sample ->
                val x = size.width * index / (samples.size - 1)
                val y = size.height * (1f - ((sample + 100).coerceIn(0, 70) / 70f))
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, primary, style = Stroke(3.dp.toPx()))
        }
    }
}
