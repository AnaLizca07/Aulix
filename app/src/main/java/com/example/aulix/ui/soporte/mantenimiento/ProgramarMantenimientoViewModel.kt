package com.example.aulix.ui.soporte.mantenimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.MantenimientoRepository
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.Mantenimiento
import com.example.aulix.domain.model.TipoMantenimiento
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ProgramarMantenimientoViewModel.Factory::class)
class ProgramarMantenimientoViewModel @AssistedInject constructor(
    @Assisted val equipoId: String,
    private val equipoRepo: EquipoRepository,
    private val mantenimientoRepo: MantenimientoRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(equipoId: String): ProgramarMantenimientoViewModel
    }

    data class UiState(
        val equipo: Equipo? = null,
        val tipoSeleccionado: TipoMantenimiento = TipoMantenimiento.PREVENTIVO,
        val fechaProgramada: String = "",
        val horaProgramada: String = "09:00",
        val tecnicoAsignado: String = "",
        val observaciones: String = "",
        val isLoading: Boolean = false,
        val guardadoExitoso: Boolean = false,
        val mantenimientoRegistrado: Mantenimiento? = null,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            equipoRepo.getEquipoById(equipoId)
                .onSuccess { equipo -> _uiState.update { it.copy(equipo = equipo) } }
        }
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val defaultFecha = "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH),
        )
        _uiState.update { it.copy(fechaProgramada = defaultFecha) }
    }

    fun onTipoChange(tipo: TipoMantenimiento) = _uiState.update { it.copy(tipoSeleccionado = tipo) }
    fun onFechaChange(v: String) = _uiState.update { it.copy(fechaProgramada = v) }
    fun onHoraChange(v: String) = _uiState.update { it.copy(horaProgramada = v) }
    fun onTecnicoChange(v: String) = _uiState.update { it.copy(tecnicoAsignado = v) }
    fun onObservacionesChange(v: String) = _uiState.update { it.copy(observaciones = v) }

    fun registrar(registradoPor: String = "") {
        val state = _uiState.value
        if (state.fechaProgramada.isBlank() || state.horaProgramada.isBlank() || state.tecnicoAsignado.isBlank()) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            mantenimientoRepo.programar(
                equipoId = equipoId,
                tipo = state.tipoSeleccionado,
                fechaProgramada = state.fechaProgramada,
                horaProgramada = state.horaProgramada,
                tecnicoAsignado = state.tecnicoAsignado.trim(),
                observaciones = state.observaciones.trim(),
            )
                .onSuccess { mantenimiento ->
                    _uiState.update {
                        it.copy(isLoading = false, guardadoExitoso = true, mantenimientoRegistrado = mantenimiento)
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
