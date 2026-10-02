package com.fajar.neartrace.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.bluetooth.BluetoothAdapter
import androidx.core.content.ContextCompat
import com.fajar.neartrace.domain.BleDevice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                    trySend(BleDevice(device.address, device.name ?: result.scanRecord?.deviceName, result.rssi))
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
}
