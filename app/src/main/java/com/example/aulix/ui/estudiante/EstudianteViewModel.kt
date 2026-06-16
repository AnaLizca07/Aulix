package com.example.aulix.ui.estudiante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.AsistenciaRepository
import com.example.aulix.data.repository.SesionRepository
import com.example.aulix.domain.model.Asignatura
import com.example.aulix.domain.model.EstadoAsistencia
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.RegistroAsistencia
import com.example.aulix.domain.model.Sesion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EstudianteViewModel @Inject constructor(
    private val sesionRepo: SesionRepository,
    private val asistenciaRepo: AsistenciaRepository,
) : ViewModel() {

    data class Comprobante(
        val sesion: String,
        val asignatura: String,
        val laboratorio: String,
        val horaRegistro: String,
        val id: String,
        val confirmados: Int,
    )

    data class UiState(
        val sesionActiva: Sesion? = null,
        val yaRegistrado: Boolean = false,
        val asignaturas: List<Asignatura> = emptyList(),
        val agenda: List<EventoAgenda> = emptyList(),
        val historial: List<RegistroAsistencia> = emptyList(),
        val comprobanteReciente: Comprobante? = null,
        val isLoading: Boolean = false,
        val isRegistrando: Boolean = false,
        val error: String? = null,
    ) {
        val resumen: Triple<Int, Int, Int>
            get() {
                val asistidas = historial.count { it.estado != EstadoAsistencia.FALTA }
                val faltas = historial.count { it.estado == EstadoAsistencia.FALTA }
                val porcentaje = if (historial.isEmpty()) 0 else (asistidas * 100) / historial.size
                return Triple(porcentaje, asistidas, faltas)
            }

        // Eventos de agenda que caen en la semana actual (lunes–domingo)
        val sesionesSemana: Int
            get() {
                val hoy = java.time.LocalDate.now()
                val lunes = hoy.with(java.time.DayOfWeek.MONDAY)
                val domingo = hoy.with(java.time.DayOfWeek.SUNDAY)
                return agenda.count { evento ->
                    runCatching {
                        val fecha = java.time.LocalDate.parse(evento.dia)
                        !fecha.isBefore(lunes) && !fecha.isAfter(domingo)
                    }.getOrDefault(false)
                }
            }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { cargarDatos() }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            sesionRepo.getSesionActivaEstudiante()
                .onSuccess { sesion ->
                    _uiState.update { it.copy(sesionActiva = sesion, isLoading = false) }
                    if (sesion != null) {
                        val yaRegistrado = asistenciaRepo.getComprobanteReciente(sesion.id)
                            .getOrNull() != null
                        _uiState.update { it.copy(yaRegistrado = yaRegistrado) }
                    }
                }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
            sesionRepo.getAgenda()
                .onSuccess { agenda -> _uiState.update { it.copy(agenda = agenda) } }
            asistenciaRepo.getHistorial()
                .onSuccess { historial ->
                    val colors = listOf(0xFF2C5BA8L, 0xFF8E44ADL, 0xFF27AE60L, 0xFFE67E22L, 0xFF2980B9L)
                    val asignaturas = historial
                        .groupBy { it.asignatura }
                        .entries
                        .mapIndexed { index, (nombre, _) ->
                            Asignatura(
                                codigo = "",
                                nombre = nombre,
                                grupo = "",
                                proxima = "",
                                esHoy = false,
                                colorHex = colors[index % colors.size],
                            )
                        }
                    _uiState.update { it.copy(historial = historial, asignaturas = asignaturas) }
                }
        }
    }

    fun registrarAsistencia(sesionId: String, metodo: String) {
        _uiState.update { it.copy(isRegistrando = true, error = null, comprobanteReciente = null) }
        viewModelScope.launch {
            asistenciaRepo.registrar(sesionId, metodo)
                .onSuccess {
                    asistenciaRepo.getComprobanteReciente(sesionId)
                        .onSuccess { c ->
                            _uiState.update {
                                it.copy(
                                    isRegistrando = false,
                                    yaRegistrado = true,
                                    comprobanteReciente = c?.let { comp ->
                                        Comprobante(
                                            sesion = comp.sesionNombre,
                                            asignatura = comp.asignatura,
                                            laboratorio = comp.laboratorio,
                                            horaRegistro = comp.hora,
                                            id = comp.codigoSesion,
                                            confirmados = 0,
                                        )
                                    }
                                )
                            }
                        }
                        .onFailure { e -> _uiState.update { it.copy(isRegistrando = false, error = e.message) } }
                }
                .onFailure { e -> _uiState.update { it.copy(isRegistrando = false, error = e.message) } }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }
}
