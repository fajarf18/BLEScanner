package com.fajar.neartrace.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajar.neartrace.data.repository.DeviceRepository
import com.fajar.neartrace.domain.BleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerUiState(
    val devices: List<BleDevice> = emptyList(),
    val query: String = "",
    val minRssi: Int = -100,
    val scanning: Boolean = false,
    val error: String? = null
) {
    val filtered: List<BleDevice> get() = devices.filter {
        (query.isBlank() || it.displayName.contains(query, true) || it.address.contains(query, true)) && it.rssi >= minRssi
    }.sortedByDescending { it.rssi }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(private val repository: DeviceRepository) : ViewModel() {
    private val _state = MutableStateFlow(ScannerUiState())
    val state: StateFlow<ScannerUiState> = _state.asStateFlow()
    private var scanJob: Job? = null
    private var expiryJob: Job? = null

    fun start() {
        if (scanJob?.isActive == true) return
        _state.update { it.copy(scanning = true, devices = emptyList(), error = null) }
        expiryJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                val cutoff = System.currentTimeMillis() - 12_000
                _state.update { it.copy(devices = it.devices.filter { device -> device.lastSeen >= cutoff }) }
            }
        }
        scanJob = viewModelScope.launch {
            try {
                repository.scan().collect { fresh ->
                    _state.update { state ->
                        val previous = state.devices.firstOrNull { it.address == fresh.address }
                        // Advertising packets often omit the local name after it was seen once.
                        // Keep a usable name for the same BLE address instead of replacing it with null.
                        val updated = fresh.copy(name = fresh.name ?: previous?.name)
                        val next = state.devices.associateBy { it.address }.toMutableMap().apply { put(updated.address, updated) }
                        state.copy(devices = next.values.sortedByDescending { it.rssi })
                    }
                    repository.remember(fresh)
                }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { _state.update { it.copy(error = error.message ?: "Pemindaian gagal. Coba lagi.") } }
            finally { expiryJob?.cancel(); _state.update { it.copy(scanning = false) } }
        }
    }
    fun stop() { scanJob?.cancel(); scanJob = null; expiryJob?.cancel(); _state.update { it.copy(scanning = false) } }
    fun setQuery(value: String) = _state.update { it.copy(query = value) }
    fun setMinRssi(value: Int) = _state.update { it.copy(minRssi = value) }
    fun dismissError() = _state.update { it.copy(error = null) }
}
