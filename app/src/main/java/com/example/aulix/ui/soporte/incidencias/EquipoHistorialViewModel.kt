package com.example.aulix.ui.soporte.incidencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.IncidenciaRepository
import com.example.aulix.data.repository.MantenimientoRepository
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.domain.model.Mantenimiento
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EquipoHistorialViewModel.Factory::class)
class EquipoHistorialViewModel @AssistedInject constructor(
    @Assisted val equipoId: String,
    private val equipoRepo: EquipoRepository,
    private val incidenciaRepo: IncidenciaRepository,
    private val mantenimientoRepo: MantenimientoRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(equipoId: String): EquipoHistorialViewModel
    }

    data class UiState(
        val equipo: Equipo? = null,
        val incidencias: List<Incidencia> = emptyList(),
        val mantenimientos: List<Mantenimiento> = emptyList(),
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { cargarDatos() }

    private fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            equipoRepo.getEquipoById(equipoId)
                .onSuccess { equipo -> _uiState.update { it.copy(equipo = equipo, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
            incidenciaRepo.getIncidencias()
                .onSuccess { todas ->
                    val delEquipo = todas.filter { it.equipo.id == equipoId }
                    _uiState.update { it.copy(incidencias = delEquipo) }
                }
            mantenimientoRepo.getMantenimientosByEquipo(equipoId)
                .onSuccess { lista -> _uiState.update { it.copy(mantenimientos = lista) } }
        }
    }
}
