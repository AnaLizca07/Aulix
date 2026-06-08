package com.example.aulix.ui.estudiante

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakeEstudianteDataSource
import com.example.aulix.domain.model.Asignatura
import com.example.aulix.domain.model.EventoAgenda
import com.example.aulix.domain.model.RegistroAsistencia
import com.example.aulix.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel del flujo Estudiante: sesión activa, asignaturas, agenda e historial.
class EstudianteViewModel : ViewModel() {

    data class UiState(
        val sesionActiva: Sesion = FakeEstudianteDataSource.getSesionActiva(),
        val asignaturas: List<Asignatura> = FakeEstudianteDataSource.getAsignaturas(),
        val agenda: List<EventoAgenda> = FakeEstudianteDataSource.getAgenda(),
        val historial: List<RegistroAsistencia> = FakeEstudianteDataSource.getHistorial(),
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun registrarAsistencia(): FakeEstudianteDataSource.Comprobante =
        FakeEstudianteDataSource.registrarAsistencia()
}
