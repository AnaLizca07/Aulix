package com.example.aulix.ui.auxiliar.inventario

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InventarioViewModel : ViewModel() {

    data class UiState(
        val equipos: List<Equipo> = emptyList(),
        val disponibles: Int = 0,
        val prestados: Int = 0,
        val enReparacion: Int = 0
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { recargar() }

    fun devolverEquipo(equipoId: String) {
        val prestamo = FakePrestamoDataSource.getPrestamosRecientes()
            .firstOrNull { it.equipo.id == equipoId && it.estado == EstadoPrestamo.ACTIVO }
        if (prestamo != null) {
            FakePrestamoDataSource.devolverEquipo(prestamo.id)
            recargar()
        }
    }

    private fun recargar() {
        val (disponibles, prestados, reparacion) = FakePrestamoDataSource.getKPIs()
        _uiState.update {
            it.copy(
                equipos = FakePrestamoDataSource.getEquipos(),
                disponibles = disponibles,
                prestados = prestados,
                enReparacion = reparacion
            )
        }
    }
}
