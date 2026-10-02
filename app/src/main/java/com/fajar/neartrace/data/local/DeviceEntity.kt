package com.fajar.neartrace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detected_devices")
data class DeviceEntity(
    @PrimaryKey val address: String,
    val name: String?,
    val lastRssi: Int,
    val strongestRssi: Int,
    val firstSeen: Long,
    val lastSeen: Long,
    val seenCount: Int
)
