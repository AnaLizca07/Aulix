package com.example.aulix.ui.soporte.metricas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.IncidenciaRepository
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.Incidencia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoporteMetricasViewModel @Inject constructor(
    private val incidenciaRepo: IncidenciaRepository,
    private val equipoRepo: EquipoRepository,
) : ViewModel() {

    data class UiState(
        val incidencias: List<Incidencia> = emptyList(),
        val equipos: List<Equipo> = emptyList(),
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { cargarDatos() }

    private fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            incidenciaRepo.getIncidencias()
                .onSuccess { incidencias -> _uiState.update { it.copy(incidencias = incidencias, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
            equipoRepo.getEquipos()
                .onSuccess { equipos -> _uiState.update { it.copy(equipos = equipos) } }
        }
    }
}
