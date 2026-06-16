package com.example.aulix.ui.auxiliar.prestamo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.PrestamoRepository
import com.example.aulix.domain.model.Destinatario
import com.example.aulix.domain.model.Equipo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrarPrestamoViewModel @Inject constructor(
    private val equipoRepo: EquipoRepository,
    private val prestamoRepo: PrestamoRepository,
) : ViewModel() {

    data class UiState(
        val equipo: Equipo? = null,
        val destinatarioNombre: String = "",
        val destinatarioId: String = "",
        val destinatarioPrograma: String = "",
        val destinatarios: List<Destinatario> = emptyList(),
        val duracionSeleccionada: Int = 2,
        val sinNovedad: Boolean = true,
        val observaciones: String = "",
        val confirmado: Boolean = false,
        val horaInicio: String = "",
        val horaDevolucion: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { cargarDestinatarios() }

    private fun cargarDestinatarios() {
        viewModelScope.launch {
            equipoRepo.getDestinatarios()
                .onSuccess { destinatarios -> _uiState.update { it.copy(destinatarios = destinatarios) } }
        }
    }

    fun setEquipo(equipoId: String) {
        viewModelScope.launch {
            equipoRepo.getEquipoById(equipoId)
                .onSuccess { equipo ->
                    _uiState.update {
                        it.copy(
                            equipo = equipo,
                            horaInicio = obtenerHoraActual(),
                            horaDevolucion = calcularDevolucion(it.duracionSeleccionada),
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun onDuracionChange(horas: Int) {
        _uiState.update {
            it.copy(
                duracionSeleccionada = horas,
                horaDevolucion = calcularDevolucion(horas),
            )
        }
    }

    fun cambiarDestinatario(destinatario: Destinatario) {
        _uiState.update {
            it.copy(
                destinatarioNombre = destinatario.nombre,
                destinatarioId = destinatario.id,
                destinatarioPrograma = destinatario.programa,
            )
        }
    }

    fun onEstadoChange(sinNovedad: Boolean) {
        _uiState.update { it.copy(sinNovedad = sinNovedad, observaciones = "") }
    }

    fun onObservacionesChange(texto: String) {
        _uiState.update { it.copy(observaciones = texto) }
    }

    fun confirmarPrestamo(responsable: String) {
        val state = _uiState.value
        val equipo = state.equipo ?: return
        if (state.destinatarioId.isBlank()) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            prestamoRepo.registrar(
                equipoId      = equipo.id,
                solicitanteId = state.destinatarioId,
                duracionHoras = state.duracionSeleccionada,
                sinNovedad    = state.sinNovedad,
                sesionId      = null,
                observaciones = state.observaciones.trim().ifBlank { null },
            )
                .onSuccess { _uiState.update { it.copy(isLoading = false, confirmado = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
            Unit
        }
    }

    private fun obtenerHoraActual(): String {
        val cal = java.util.Calendar.getInstance()
        return "%02d:%02d".format(
            cal.get(java.util.Calendar.HOUR_OF_DAY),
            cal.get(java.util.Calendar.MINUTE),
        )
    }

    private fun calcularDevolucion(horas: Int): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.HOUR_OF_DAY, horas)
        return "%02d:%02d".format(
            cal.get(java.util.Calendar.HOUR_OF_DAY),
            cal.get(java.util.Calendar.MINUTE),
        )
    }
}
