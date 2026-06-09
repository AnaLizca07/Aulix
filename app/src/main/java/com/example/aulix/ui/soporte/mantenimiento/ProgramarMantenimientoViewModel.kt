package com.example.aulix.ui.soporte.mantenimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aulix.data.local.FakeMantenimientoDataSource
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.Mantenimiento
import com.example.aulix.domain.model.TipoMantenimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProgramarMantenimientoViewModel(val equipoId: String) : ViewModel() {

    data class UiState(
        val tipoSeleccionado: TipoMantenimiento = TipoMantenimiento.PREVENTIVO,
        val fechaProgramada: String = "",
        val horaProgramada: String = "09:00",
        val tecnicoAsignado: String = "",
        val observaciones: String = "",
        val isLoading: Boolean = false,
        val guardadoExitoso: Boolean = false,
        val mantenimientoRegistrado: Mantenimiento? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val defaultFecha = "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
        _uiState.update { it.copy(fechaProgramada = defaultFecha) }
    }

    fun onTipoChange(tipo: TipoMantenimiento) = _uiState.update { it.copy(tipoSeleccionado = tipo) }
    fun onFechaChange(v: String) = _uiState.update { it.copy(fechaProgramada = v) }
    fun onHoraChange(v: String) = _uiState.update { it.copy(horaProgramada = v) }
    fun onTecnicoChange(v: String) = _uiState.update { it.copy(tecnicoAsignado = v) }
    fun onObservacionesChange(v: String) = _uiState.update { it.copy(observaciones = v) }

    fun registrar(registradoPor: String) {
        val state = _uiState.value
        if (state.fechaProgramada.isBlank() || state.horaProgramada.isBlank() || state.tecnicoAsignado.isBlank()) return
        val equipo = FakePrestamoDataSource.getEquipoById(equipoId) ?: return
        _uiState.update { it.copy(isLoading = true) }
        val nuevo = FakeMantenimientoDataSource.registrarMantenimiento(
            equipo = equipo,
            tipo = state.tipoSeleccionado,
            fechaProgramada = state.fechaProgramada,
            horaProgramada = state.horaProgramada,
            tecnicoAsignado = state.tecnicoAsignado.trim(),
            observaciones = state.observaciones.trim(),
            registradoPor = registradoPor
        )
        _uiState.update { it.copy(isLoading = false, guardadoExitoso = true, mantenimientoRegistrado = nuevo) }
    }

    companion object {
        fun factory(equipoId: String) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProgramarMantenimientoViewModel(equipoId) as T
            }
        }
    }
}
