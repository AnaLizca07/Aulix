package com.example.aulix.ui.docente

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakeDocenteDataSource
import com.example.aulix.domain.model.Asistente
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.Evidencia
import com.example.aulix.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ViewModel compartido del flujo Docente: mantiene la sesión actual y sus datos.
class DocenteViewModel : ViewModel() {

    data class UiState(
        val sesion: Sesion = FakeDocenteDataSource.getSesionActual(),
        val asistentes: List<Asistente> = FakeDocenteDataSource.getAsistentesRecientes(),
        val evidencias: List<Evidencia> = FakeDocenteDataSource.getEvidencias(),
        val agenda: List<EventoAgenda> = FakeDocenteDataSource.getAgenda(),
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun abrirSesion(horaReal: String) {
        FakeDocenteDataSource.abrirSesion(horaReal)
        _uiState.update { it.copy(sesion = FakeDocenteDataSource.getSesionActual()) }
    }

    fun cerrarSesion() {
        FakeDocenteDataSource.cerrarSesion()
        _uiState.update { it.copy(sesion = FakeDocenteDataSource.getSesionActual()) }
    }

    fun guardarEvento(evento: EventoAgenda) {
        FakeDocenteDataSource.guardarEvento(evento)
        _uiState.update { it.copy(agenda = FakeDocenteDataSource.getAgenda()) }
    }
}
