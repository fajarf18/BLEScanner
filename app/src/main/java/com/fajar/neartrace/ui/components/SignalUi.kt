package com.fajar.neartrace.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fajar.neartrace.domain.SignalZone
import com.fajar.neartrace.ui.theme.Amber
import com.fajar.neartrace.ui.theme.Coral
import com.fajar.neartrace.ui.theme.Forest
import com.fajar.neartrace.ui.theme.Muted

@Composable
fun SignalZone.color(): Color {
    val dark = androidx.compose.foundation.isSystemInDarkTheme()
    return when (this) {
    SignalZone.VERY_CLOSE, SignalZone.CLOSE -> if (dark) Color(0xFF72BC9F) else Forest
    SignalZone.GOOD -> if (dark) Color(0xFF90CDB0) else Color(0xFF34775F)
    SignalZone.WEAK -> if (dark) Color(0xFFE7B578) else Color(0xFF95530A)
    SignalZone.VERY_WEAK, SignalZone.LOST -> if (dark) Color(0xFFF1AAA5) else Coral
    }
}

@Composable
fun SignalBars(rssi: Int, modifier: Modifier = Modifier) {
    val active = when { rssi >= -50 -> 4; rssi >= -65 -> 3; rssi >= -80 -> 2; rssi >= -90 -> 1; else -> 0 }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.Bottom) {
        (1..4).forEach { index ->
            Box(Modifier.width(3.dp).height((5 + index * 3).dp).background(
                if (index <= active) SignalZone.from(rssi).color() else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(2.dp)
            ))
        }
    }
}

@Composable
fun ZoneBadge(zone: SignalZone, modifier: Modifier = Modifier) {
    Row(modifier.background(zone.color().copy(alpha = .11f), RoundedCornerShape(8.dp)).padding(horizontal = 9.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(6.dp).background(zone.color(), RoundedCornerShape(50)))
        Spacer(Modifier.width(6.dp))
        Text(zone.label, color = zone.color(), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}

@Composable
fun EmptyRadar(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val center = this.center
        repeat(3) { i -> drawCircle(Muted.copy(alpha = .16f), radius = size.minDimension * (.16f + i * .12f), center = center, style = Stroke(2f)) }
        drawCircle(Forest.copy(alpha = .16f), radius = size.minDimension * .10f, center = center)
        drawCircle(Forest, radius = size.minDimension * .028f, center = center)
    }
}
