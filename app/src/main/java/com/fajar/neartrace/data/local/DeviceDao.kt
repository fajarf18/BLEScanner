package com.fajar.neartrace.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM detected_devices ORDER BY lastSeen DESC")
    fun observeAll(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM detected_devices WHERE address = :address LIMIT 1")
    suspend fun find(address: String): DeviceEntity?

    @Upsert
    suspend fun upsert(device: DeviceEntity)

    @Query("DELETE FROM detected_devices")
    suspend fun clear()
}
