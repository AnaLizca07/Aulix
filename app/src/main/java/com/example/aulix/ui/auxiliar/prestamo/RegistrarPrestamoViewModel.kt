package com.example.aulix.ui.auxiliar.prestamo

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.Destinatario
import com.example.aulix.domain.model.Equipo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrarPrestamoViewModel : ViewModel() {

    data class UiState(
        val equipo: Equipo? = null,
        val destinatarioNombre: String = "Pérez, Laura",
        val destinatarioId: String = "E-2003047",
        val destinatarioPrograma: String = "Programación de Redes",
        val duracionSeleccionada: Int = 2,
        val sinNovedad: Boolean = true,
        val confirmado: Boolean = false,
        val horaInicio: String = "",
        val horaDevolucion: String = ""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setEquipo(equipoId: String) {
        val equipo = FakePrestamoDataSource.getEquipoById(equipoId)
        val horaInicio = obtenerHoraActual()
        val horaDevolucion = calcularDevolucion(2)
        _uiState.update {
            it.copy(
                equipo = equipo,
                horaInicio = horaInicio,
                horaDevolucion = horaDevolucion
            )
        }
    }

    fun onDuracionChange(horas: Int) {
        _uiState.update {
            it.copy(
                duracionSeleccionada = horas,
                horaDevolucion = calcularDevolucion(horas)
            )
        }
    }

    fun cambiarDestinatario(destinatario: Destinatario) {
        _uiState.update {
            it.copy(
                destinatarioNombre = destinatario.nombre,
                destinatarioId = destinatario.id,
                destinatarioPrograma = destinatario.programa
            )
        }
    }

    fun onEstadoChange(sinNovedad: Boolean) {
        _uiState.update { it.copy(sinNovedad = sinNovedad) }
    }

    fun confirmarPrestamo(responsable: String) {
        val equipo = _uiState.value.equipo ?: return
        FakePrestamoDataSource.registrarPrestamo(
            equipo = equipo,
            destinatarioNombre = _uiState.value.destinatarioNombre,
            destinatarioId = _uiState.value.destinatarioId,
            destinatarioPrograma = _uiState.value.destinatarioPrograma,
            duracionHoras = _uiState.value.duracionSeleccionada,
            responsable = responsable
        )
        _uiState.update { it.copy(confirmado = true) }
    }

    private fun obtenerHoraActual(): String {
        val cal = java.util.Calendar.getInstance()
        return "%02d:%02d".format(
            cal.get(java.util.Calendar.HOUR_OF_DAY),
            cal.get(java.util.Calendar.MINUTE)
        )
    }

    private fun calcularDevolucion(horas: Int): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.HOUR_OF_DAY, horas)
        return "%02d:%02d".format(
            cal.get(java.util.Calendar.HOUR_OF_DAY),
            cal.get(java.util.Calendar.MINUTE)
        )
    }
}
