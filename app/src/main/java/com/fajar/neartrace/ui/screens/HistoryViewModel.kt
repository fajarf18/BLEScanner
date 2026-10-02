package com.fajar.neartrace.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fajar.neartrace.data.repository.DeviceRepository
import com.fajar.neartrace.domain.BleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: DeviceRepository) : ViewModel() {
    val devices: StateFlow<List<BleDevice>> = repository.history().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun clear() = viewModelScope.launch { repository.clearHistory() }
}
