package com.example.aulix.ui.docente.indicadores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.SesionRepository
import com.example.aulix.domain.model.EstadoSesion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocenteIndicadoresViewModel @Inject constructor(
    private val sesionRepo: SesionRepository,
) : ViewModel() {

    data class UiState(
        val sesionesTotales: Int = 0,
        val sesionesCompletadas: Int = 0,
        val sesionesPendientes: Int = 0,
        val sesionesCanceladas: Int = 0,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { cargar() }

    private fun cargar() {
        viewModelScope.launch {
            sesionRepo.getMisSesiones().onSuccess { sesiones ->
                _uiState.update {
                    it.copy(
                        sesionesTotales   = sesiones.size,
                        sesionesCompletadas = sesiones.count { s -> s.estado == EstadoSesion.CERRADA },
                        sesionesPendientes  = sesiones.count { s ->
                            s.estado == EstadoSesion.PROGRAMADA || s.estado == EstadoSesion.ACTIVA
                        },
                        sesionesCanceladas  = sesiones.count { s -> s.estado == EstadoSesion.CANCELADA },
                    )
                }
            }
        }
    }
}
