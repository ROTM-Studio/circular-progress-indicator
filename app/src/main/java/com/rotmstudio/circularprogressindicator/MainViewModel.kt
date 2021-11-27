package com.rotmstudio.circularprogressindicator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private var job: Job? = null
    private val _countdown = MutableStateFlow(0)
    val countdown = _countdown.asStateFlow()

    private val _currentDuration = MutableStateFlow(0)
    val currentDuration = _currentDuration.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    fun start(totalDurationInSecond: Int, isPlayingFromStart: Boolean = true) {
        if (isPlayingFromStart) {
            _countdown.value = totalDurationInSecond
            _currentDuration.value = 0
        }
        _isPlaying.value = true
        _isPaused.value = false
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (_countdown.value <= 0) {
                    _isPlaying.value = false
                    delay(1000)
                    job?.cancel()
                    _currentDuration.value = 0
                    return@launch
                }
                delay(1000)
                _countdown.value--
                _currentDuration.value++
            }
        }
    }

    fun pause() {
        job?.cancel()
        _isPaused.value = true
    }

    fun stop() {
        job?.cancel()
        _countdown.value = 0
        _currentDuration.value = 0
        _isPlaying.value = false
    }
}