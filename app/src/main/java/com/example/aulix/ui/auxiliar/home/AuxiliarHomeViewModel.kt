package com.example.aulix.ui.auxiliar.home

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.Prestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuxiliarHomeViewModel : ViewModel() {

    data class UiState(
        val disponibles: Int = 0,
        val prestados: Int = 0,
        val enReparacion: Int = 0,
        val prestamosRecientes: List<Prestamo> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { recargarDatos() }

    fun recargarDatos() {
        val (disponibles, prestados, reparacion) = FakePrestamoDataSource.getKPIs()
        _uiState.update {
            it.copy(
                disponibles = disponibles,
                prestados = prestados,
                enReparacion = reparacion,
                prestamosRecientes = FakePrestamoDataSource.getPrestamosRecientes()
            )
        }
    }
}
