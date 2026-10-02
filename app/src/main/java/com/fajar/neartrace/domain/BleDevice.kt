package com.fajar.neartrace.domain

import kotlin.math.pow

/** Immutable UI/domain representation of one BLE advertiser. */
data class BleDevice(
    val address: String,
    val name: String?,
    val rssi: Int,
    val lastSeen: Long = System.currentTimeMillis()
) {
    val displayName: String get() = name?.takeIf { it.isNotBlank() } ?: "Perangkat BLE tanpa nama"
    val distanceMeters: Double get() = 10.0.pow((-59 - rssi) / 20.0).coerceIn(0.1, 99.0)
    val zone: SignalZone get() = SignalZone.from(rssi)
}

enum class SignalZone(val label: String, val distanceLabel: String) {
    VERY_CLOSE("Sangat dekat", "< 1 m"),
    CLOSE("Dekat", "1–3 m"),
    GOOD("Cukup dekat", "3–10 m"),
    WEAK("Lemah", "10–20 m"),
    VERY_WEAK("Sangat lemah", "> 20 m"),
    LOST("Sinyal hilang", "Di luar jangkauan");

    companion object {
        fun from(rssi: Int) = when {
            rssi >= -30 -> VERY_CLOSE
            rssi >= -50 -> CLOSE
            rssi >= -70 -> GOOD
            rssi >= -80 -> WEAK
            rssi >= -90 -> VERY_WEAK
            else -> LOST
        }
    }
}
