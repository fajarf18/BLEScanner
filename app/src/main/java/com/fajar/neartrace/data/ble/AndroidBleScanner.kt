package com.fajar.neartrace.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.fajar.neartrace.domain.BleDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidBleScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val adapter get() = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    @SuppressLint("MissingPermission")
    fun scan(): Flow<BleDevice> = callbackFlow {
        val scanner = adapter?.bluetoothLeScanner
        if (adapter?.isEnabled != true || scanner == null) {
            close(IllegalStateException("Bluetooth tidak aktif"))
            return@callbackFlow
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                    close(IllegalStateException("Bluetooth dinonaktifkan. Aktifkan lalu mulai kembali."))
                }
            }
        }
        ContextCompat.registerReceiver(context, receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED), ContextCompat.RECEIVER_EXPORTED)
        val callback = object : ScanCallback() {
            override fun onScanResult(type: Int, result: ScanResult) {
                try {
                    val device = result.device
                    val name = resolveName(device.name, result.scanRecord, device.address)
                    trySend(BleDevice(device.address, name, result.rssi))
                } catch (error: SecurityException) { close(error) }
            }
            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { onScanResult(0, it) }
            }
            override fun onScanFailed(errorCode: Int) {
                close(IllegalStateException("Pemindaian gagal (kode $errorCode)"))
            }
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(0)
            .build()
        try {
            scanner.startScan(null, settings, callback)
        } catch (error: SecurityException) {
            close(error)
        }
        awaitClose { runCatching { scanner.stopScan(callback) }; runCatching { context.unregisterReceiver(receiver) } }
    }

    /**
     * A BLE name is optional. Prefer Android's cached name, then the name fields
     * in the advertising packet, then a matching paired-device name. No pairing
     * or connection is started while scanning.
     */
    @SuppressLint("MissingPermission")
    private fun resolveName(cachedName: String?, record: ScanRecord?, address: String): String? {
        return cachedName.cleanName()
            ?: record?.deviceName.cleanName()
            ?: record?.advertisedLocalName().cleanName()
            ?: adapter?.bondedDevices?.firstOrNull { it.address == address }?.name.cleanName()
    }

    /** Reads AD types 0x09 (Complete Local Name) and 0x08 (Shortened Local Name). */
    private fun ScanRecord.advertisedLocalName(): String? {
        val bytes = bytes ?: return null
        var index = 0
        var shortenedName: String? = null
        while (index < bytes.size) {
            val length = bytes[index].toInt() and 0xFF
            if (length == 0) break
            val end = (index + length + 1).coerceAtMost(bytes.size)
            if (index + 1 < end) {
                val type = bytes[index + 1].toInt() and 0xFF
                if ((type == 0x08 || type == 0x09) && index + 2 < end) {
                    val name = String(bytes, index + 2, end - (index + 2), StandardCharsets.UTF_8).cleanName()
                    if (type == 0x09 && name != null) return name
                    if (shortenedName == null) shortenedName = name
                }
            }
            index += length + 1
        }
        return shortenedName
    }

    private fun String?.cleanName(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
}
