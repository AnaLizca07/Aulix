package com.example.aulix.ui.auxiliar.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.PrestamoRepository
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoPrestamo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val equipoRepo: EquipoRepository,
    private val prestamoRepo: PrestamoRepository,
) : ViewModel() {

    data class UiState(
        val equipos: List<Equipo> = emptyList(),
        val disponibles: Int = 0,
        val prestados: Int = 0,
        val enReparacion: Int = 0,
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { recargar() }

    fun devolverEquipo(equipoId: String) {
        viewModelScope.launch {
            val prestamos = prestamoRepo.getPrestamosActivos().getOrNull() ?: return@launch
            val prestamo = prestamos.firstOrNull { it.equipo.id == equipoId } ?: return@launch
            prestamoRepo.devolver(prestamo.id)
                .onSuccess { recargar() }
        }
    }

    private fun recargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            prestamoRepo.getKPIs()
                .onSuccess { (disponibles, prestados, reparacion) ->
                    _uiState.update {
                        it.copy(
                            disponibles = disponibles,
                            prestados = prestados,
                            enReparacion = reparacion,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
            equipoRepo.getInventario()
                .onSuccess { equipos -> _uiState.update { it.copy(equipos = equipos) } }
        }
    }
}
