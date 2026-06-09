package com.example.aulix.ui.soporte.incidencias

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakeIncidenciaDataSource
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.PrioridadIncidencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrarIncidenciaViewModel : ViewModel() {

    data class UiState(
        val titulo: String = "",
        val descripcion: String = "",
        val prioridadSeleccionada: PrioridadIncidencia = PrioridadIncidencia.MEDIA,
        val equipoSeleccionado: Equipo? = null,
        val equiposDisponibles: List<Equipo> = emptyList(),
        val isLoading: Boolean = false,
        val guardadoExitoso: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(equiposDisponibles = FakePrestamoDataSource.getEquipos()) }
    }

    fun onTituloChange(valor: String) = _uiState.update { it.copy(titulo = valor) }

    fun onDescripcionChange(valor: String) = _uiState.update { it.copy(descripcion = valor) }

    fun onPrioridadChange(prioridad: PrioridadIncidencia) = _uiState.update { it.copy(prioridadSeleccionada = prioridad) }

    fun onEquipoChange(equipo: Equipo) = _uiState.update { it.copy(equipoSeleccionado = equipo) }

    fun registrar(reportadoPor: String) {
        val state = _uiState.value
        if (state.titulo.isBlank() || state.descripcion.isBlank() || state.equipoSeleccionado == null) return
        _uiState.update { it.copy(isLoading = true) }
        FakeIncidenciaDataSource.registrarIncidencia(
            titulo = state.titulo.trim(),
            descripcion = state.descripcion.trim(),
            prioridad = state.prioridadSeleccionada,
            equipo = state.equipoSeleccionado,
            reportadoPor = reportadoPor,
            rolReportante = "Soporte Técnico"
        )
        _uiState.update { it.copy(isLoading = false, guardadoExitoso = true) }
    }
}
