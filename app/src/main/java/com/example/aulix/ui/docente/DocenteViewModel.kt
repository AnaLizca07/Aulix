package com.example.aulix.ui.docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.SesionRepository
import com.example.aulix.domain.model.Asistente
import com.example.aulix.domain.model.AsignaturaCatalog
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.Evidencia
import com.example.aulix.domain.model.LaboratorioCatalog
import com.example.aulix.domain.model.Sesion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocenteViewModel @Inject constructor(
    private val sesionRepo: SesionRepository,
) : ViewModel() {

    data class UiState(
        val sesion: Sesion? = null,
        val asistentes: List<Asistente> = emptyList(),
        val evidencias: List<Evidencia> = emptyList(),
        val agenda: List<EventoAgenda> = emptyList(),
        val asignaturas: List<AsignaturaCatalog> = emptyList(),
        val laboratorios: List<LaboratorioCatalog> = emptyList(),
        val qrTimerSegundos: Int = 300,
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init { cargarDatos() }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            sesionRepo.getSesionActiva()
                .onSuccess { activa ->
                    val sesion = activa ?: sesionRepo.getSesionProgramada().getOrNull()
                    _uiState.update { it.copy(sesion = sesion, isLoading = false) }
                    if (activa != null) {
                        cargarAsistentes(activa.id)
                        if (timerJob == null || timerJob?.isActive == false) iniciarTimerQr()
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            sesionRepo.getAgenda()
                .onSuccess { agenda -> _uiState.update { it.copy(agenda = agenda) } }
            sesionRepo.getAsignaturasCatalog()
                .onSuccess { asigs -> _uiState.update { it.copy(asignaturas = asigs) } }
            sesionRepo.getLaboratoriosCatalog()
                .onSuccess { labs -> _uiState.update { it.copy(laboratorios = labs) } }
        }
    }

    fun abrirSesion() {
        val sesionId = _uiState.value.sesion?.id ?: return
        val codigo = (100000..999999).random().toString()
        viewModelScope.launch {
            sesionRepo.abrirSesion(sesionId, codigo)
                .onSuccess { sesion ->
                    _uiState.update { it.copy(sesion = sesion) }
                    cargarAsistentes(sesion.id)
                    iniciarTimerQr()
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun renovarQr() {
        val sesionId = _uiState.value.sesion?.id ?: return
        val nuevoCodigo = (100000..999999).random().toString()
        viewModelScope.launch {
            sesionRepo.renovarCodigo(sesionId, nuevoCodigo)
                .onSuccess { sesion ->
                    _uiState.update { it.copy(sesion = sesion) }
                    iniciarTimerQr()
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    private fun iniciarTimerQr() {
        timerJob?.cancel()
        _uiState.update { it.copy(qrTimerSegundos = 300) }
        timerJob = viewModelScope.launch {
            repeat(300) {
                delay(1000L)
                _uiState.update { it.copy(qrTimerSegundos = maxOf(0, it.qrTimerSegundos - 1)) }
            }
        }
    }

    fun cerrarSesion() {
        val sesionId = _uiState.value.sesion?.id ?: return
        viewModelScope.launch {
            sesionRepo.cerrarSesion(sesionId)
                .onSuccess { cargarDatos() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun guardarEvento(evento: EventoAgenda) {
        if (evento.id.isBlank()) {
            viewModelScope.launch {
                sesionRepo.crearReserva(evento)
                    .onSuccess { guardado ->
                        val agenda = _uiState.value.agenda.toMutableList()
                        agenda.add(guardado)
                        _uiState.update { it.copy(agenda = agenda) }
                    }
                    .onFailure { e ->
                        android.util.Log.e("DocenteVM", "crearReserva falló", e)
                        _uiState.update { it.copy(error = e.message) }
                    }
            }
        } else {
            viewModelScope.launch {
                sesionRepo.actualizarReserva(evento)
                    .onSuccess {
                        val agenda = _uiState.value.agenda.toMutableList()
                        val index = agenda.indexOfFirst { it.id == evento.id }
                        if (index != -1) agenda[index] = evento
                        _uiState.update { it.copy(agenda = agenda) }
                    }
                    .onFailure { e ->
                        android.util.Log.e("DocenteVM", "actualizarReserva falló", e)
                        _uiState.update { it.copy(error = e.message) }
                    }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    private fun cargarAsistentes(sesionId: String) {
        viewModelScope.launch {
            sesionRepo.getAsistentes(sesionId)
                .onSuccess { asistentes -> _uiState.update { it.copy(asistentes = asistentes) } }
        }
    }
}
