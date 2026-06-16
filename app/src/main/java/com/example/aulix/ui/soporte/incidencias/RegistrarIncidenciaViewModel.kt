package com.example.aulix.ui.soporte.incidencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.IncidenciaRepository
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.PrioridadIncidencia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrarIncidenciaViewModel @Inject constructor(
    private val equipoRepo: EquipoRepository,
    private val incidenciaRepo: IncidenciaRepository,
) : ViewModel() {

    data class UiState(
        val titulo: String = "",
        val descripcion: String = "",
        val prioridadSeleccionada: PrioridadIncidencia = PrioridadIncidencia.MEDIA,
        val equipoSeleccionado: Equipo? = null,
        val equiposDisponibles: List<Equipo> = emptyList(),
        val isLoading: Boolean = false,
        val guardadoExitoso: Boolean = false,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            equipoRepo.getEquipos()
                .onSuccess { equipos -> _uiState.update { it.copy(equiposDisponibles = equipos) } }
        }
    }

    fun onTituloChange(valor: String) = _uiState.update { it.copy(titulo = valor) }
    fun onDescripcionChange(valor: String) = _uiState.update { it.copy(descripcion = valor) }
    fun onPrioridadChange(prioridad: PrioridadIncidencia) = _uiState.update { it.copy(prioridadSeleccionada = prioridad) }
    fun onEquipoChange(equipo: Equipo) = _uiState.update { it.copy(equipoSeleccionado = equipo) }

    fun registrar(reportadoPor: String) {
        val state = _uiState.value
        if (state.titulo.isBlank() || state.descripcion.isBlank() || state.equipoSeleccionado == null) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            incidenciaRepo.registrar(
                titulo = state.titulo.trim(),
                descripcion = state.descripcion.trim(),
                severidad = state.prioridadSeleccionada,
                equipoId = state.equipoSeleccionado.id,
                sesionId = null,
            )
                .onSuccess { _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
