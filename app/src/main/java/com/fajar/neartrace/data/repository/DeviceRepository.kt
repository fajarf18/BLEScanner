package com.fajar.neartrace.data.repository

import com.fajar.neartrace.data.ble.AndroidBleScanner
import com.fajar.neartrace.data.local.DeviceDao
import com.fajar.neartrace.data.local.DeviceEntity
import com.fajar.neartrace.domain.BleDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepository @Inject constructor(
    private val scanner: AndroidBleScanner,
    private val dao: DeviceDao
) {
    fun scan(): Flow<BleDevice> = scanner.scan()
    fun history(): Flow<List<BleDevice>> = dao.observeAll().map { rows -> rows.map { it.toDomain() } }

    suspend fun remember(device: BleDevice) {
        val old = dao.find(device.address)
        dao.upsert(DeviceEntity(
            address = device.address,
            name = device.name ?: old?.name,
            lastRssi = device.rssi,
            strongestRssi = maxOf(device.rssi, old?.strongestRssi ?: -127),
            firstSeen = old?.firstSeen ?: device.lastSeen,
            lastSeen = device.lastSeen,
            seenCount = (old?.seenCount ?: 0) + 1
        ))
    }

    suspend fun find(address: String): BleDevice? = dao.find(address)?.toDomain()
    suspend fun clearHistory() = dao.clear()

    private fun DeviceEntity.toDomain() = BleDevice(address, name, lastRssi, lastSeen)
}
