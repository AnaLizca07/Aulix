package com.example.aulix.ui.auxiliar.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.PrestamoRepository
import com.example.aulix.domain.model.Prestamo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuxiliarHomeViewModel @Inject constructor(
    private val prestamoRepo: PrestamoRepository,
) : ViewModel() {

    data class UiState(
        val disponibles: Int = 0,
        val prestados: Int = 0,
        val enReparacion: Int = 0,
        val prestamosRecientes: List<Prestamo> = emptyList(),
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { recargarDatos() }

    fun recargarDatos() {
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
            prestamoRepo.getPrestamosActivos()
                .onSuccess { prestamos -> _uiState.update { it.copy(prestamosRecientes = prestamos) } }
        }
    }
}
