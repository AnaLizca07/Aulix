package com.example.aulix.ui.soporte.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.IncidenciaRepository
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.domain.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoporteHomeViewModel @Inject constructor(
    private val incidenciaRepo: IncidenciaRepository,
) : ViewModel() {

    data class UiState(
        val abiertas: Int = 0,
        val enAtencion: Int = 0,
        val resueltasHoy: Int = 0,
        val incidencias: List<Incidencia> = emptyList(),
        val tabSeleccionado: Int = 0,
        val totalCount: Int = 0,
        val misAsignadasCount: Int = 0,
        val sinAsignarCount: Int = 0,
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var todasLasIncidencias: List<Incidencia> = emptyList()

    init { recargarDatos() }

    fun recargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            incidenciaRepo.getKPIs()
                .onSuccess { (abiertas, enAtencion, resueltasHoy) ->
                    _uiState.update {
                        it.copy(
                            abiertas = abiertas,
                            enAtencion = enAtencion,
                            resueltasHoy = resueltasHoy,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
            incidenciaRepo.getIncidencias()
                .onSuccess { todas ->
                    todasLasIncidencias = todas
                    val nombreUsuario = UserSession.currentUser?.fullName ?: ""
                    val filtradas = filtrarPorTab(todas, _uiState.value.tabSeleccionado, nombreUsuario)
                    _uiState.update {
                        it.copy(
                            incidencias = filtradas,
                            totalCount = todas.size,
                            misAsignadasCount = todas.count { it.asignadoA == nombreUsuario },
                            sinAsignarCount = todas.count { it.asignadoA == null },
                        )
                    }
                }
        }
    }

    fun cambiarTab(index: Int) {
        val nombreUsuario = UserSession.currentUser?.fullName ?: ""
        val filtradas = filtrarPorTab(todasLasIncidencias, index, nombreUsuario)
        _uiState.update { it.copy(tabSeleccionado = index, incidencias = filtradas) }
    }

    private fun filtrarPorTab(todas: List<Incidencia>, tab: Int, nombreUsuario: String): List<Incidencia> =
        when (tab) {
            1 -> todas.filter { it.asignadoA == nombreUsuario }
            2 -> todas.filter { it.asignadoA == null }
            else -> todas
        }
}
