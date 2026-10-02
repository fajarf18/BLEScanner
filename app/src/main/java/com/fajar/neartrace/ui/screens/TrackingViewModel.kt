package com.fajar.neartrace.ui.screens

import androidx.lifecycle.SavedStateHandle
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

data class TrackingUiState(
    val device: BleDevice? = null,
    val samples: List<Int> = emptyList(),
    val smoothedRssi: Int? = null,
    val lost: Boolean = true,
    val error: String? = null
) {
    val stability: Int? get() {
        if (samples.size < 5 || lost) return null
        val average = samples.average()
        val deviation = samples.map { kotlin.math.abs(it - average) }.average()
        return (100 - deviation * 6).toInt().coerceIn(0, 100)
    }
}

@HiltViewModel
class TrackingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DeviceRepository
) : ViewModel() {
    private val address: String = checkNotNull(savedStateHandle["address"])
    private val _state = MutableStateFlow(TrackingUiState())
    val state: StateFlow<TrackingUiState> = _state.asStateFlow()
    private var scanJob: Job? = null
    private var timerJob: Job? = null

    init { viewModelScope.launch { repository.find(address)?.let { saved ->
        _state.update { if (it.device == null) it.copy(device = saved) else it }
    } } }

    fun start() {
        if (scanJob?.isActive == true) return
        _state.update { it.copy(error = null, samples = emptyList(), smoothedRssi = null, lost = true) }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                _state.update { it.copy(lost = it.device == null || System.currentTimeMillis() - (it.device?.lastSeen ?: 0) > 10_000) }
            }
        }
        scanJob = viewModelScope.launch {
            try {
                repository.scan().collect { device ->
                    if (device.address == address) {
                        _state.update { old ->
                            val smoothed = old.smoothedRssi?.let { (0.35 * device.rssi + 0.65 * it).toInt() } ?: device.rssi
                            old.copy(device = device, smoothedRssi = smoothed, samples = (old.samples + device.rssi).takeLast(24), lost = false, error = null)
                        }
                        repository.remember(device)
                    }
                }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { _state.update { it.copy(error = error.message ?: "Pelacakan gagal.", lost = true) } }
            finally { timerJob?.cancel() }
        }
    }
    fun stop() { scanJob?.cancel(); scanJob = null; timerJob?.cancel(); _state.update { it.copy(lost = true) } }
}
