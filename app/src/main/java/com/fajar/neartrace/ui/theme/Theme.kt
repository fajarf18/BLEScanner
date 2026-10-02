package com.fajar.neartrace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Forest = Color(0xFF166C58)
val ForestDark = Color(0xFF0E4E40)
val Mint = Color(0xFFE8F3EF)
val Ink = Color(0xFF1B2522)
val Muted = Color(0xFF66736F)
val Canvas = Color(0xFFF8FAF9)
val Line = Color(0xFFDDE5E2)
val Amber = Color(0xFF95530A)
val Coral = Color(0xFFA43C38)

private val Light = lightColorScheme(
    primary = Forest, onPrimary = Color.White, primaryContainer = Mint, onPrimaryContainer = ForestDark,
    background = Canvas, onBackground = Ink, surface = Color.White, onSurface = Ink,
    surfaceVariant = Color(0xFFF0F4F2), onSurfaceVariant = Muted, outline = Line,
    error = Coral
)
private val Dark = darkColorScheme(
    primary = Color(0xFF72BC9F), onPrimary = Color(0xFF07271F), primaryContainer = Color(0xFF174B3E),
    background = Color(0xFF101613), surface = Color(0xFF18201D), onSurface = Color(0xFFF2F7F5),
    surfaceVariant = Color(0xFF25302C), onSurfaceVariant = Color(0xFFB5C2BD), outline = Color(0xFF3A4843)
)

@Composable fun NearTraceTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
