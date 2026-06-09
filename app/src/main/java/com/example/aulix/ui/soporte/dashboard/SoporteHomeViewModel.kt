package com.example.aulix.ui.soporte.dashboard

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakeIncidenciaDataSource
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.domain.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SoporteHomeViewModel : ViewModel() {

    data class UiState(
        val abiertas: Int = 0,
        val enAtencion: Int = 0,
        val resueltasHoy: Int = 0,
        val incidencias: List<Incidencia> = emptyList(),
        val tabSeleccionado: Int = 0,
        val totalCount: Int = 0,
        val misAsignadasCount: Int = 0,
        val sinAsignarCount: Int = 0
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { recargarDatos() }

    fun recargarDatos() {
        val todas = FakeIncidenciaDataSource.getIncidencias()
        val (abiertas, enAtencion, resueltasHoy) = FakeIncidenciaDataSource.getKPIs()
        val nombreUsuario = UserSession.currentUser?.fullName ?: ""
        val misAsignadas = todas.filter { it.asignadoA == nombreUsuario }
        val sinAsignar = todas.filter { it.asignadoA == null }
        val filtradas = filtrarPorTab(todas, _uiState.value.tabSeleccionado, nombreUsuario)
        _uiState.update {
            it.copy(
                abiertas = abiertas,
                enAtencion = enAtencion,
                resueltasHoy = resueltasHoy,
                incidencias = filtradas,
                totalCount = todas.size,
                misAsignadasCount = misAsignadas.size,
                sinAsignarCount = sinAsignar.size
            )
        }
    }

    fun cambiarTab(index: Int) {
        val todas = FakeIncidenciaDataSource.getIncidencias()
        val nombreUsuario = UserSession.currentUser?.fullName ?: ""
        val filtradas = filtrarPorTab(todas, index, nombreUsuario)
        _uiState.update { it.copy(tabSeleccionado = index, incidencias = filtradas) }
    }

    private fun filtrarPorTab(todas: List<Incidencia>, tab: Int, nombreUsuario: String): List<Incidencia> =
        when (tab) {
            1 -> todas.filter { it.asignadoA == nombreUsuario }
            2 -> todas.filter { it.asignadoA == null }
            else -> todas
        }
}
