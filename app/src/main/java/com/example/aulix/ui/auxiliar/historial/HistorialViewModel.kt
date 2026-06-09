package com.example.aulix.ui.auxiliar.historial

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.EstadoPrestamo
import com.example.aulix.domain.model.Prestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HistorialViewModel : ViewModel() {

    data class UiState(
        val tabActivo: String = "Todos",
        val prestamosAgrupados: Map<String, List<Prestamo>> = emptyMap(),
        val totalMes: Int = 0,
        val comparativaMes: String = ""
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val tabs = listOf("Todos", "Activos", "Devueltos", "Vencidos")

    init {
        cargarHistorial("Todos")
    }

    fun onTabChange(tab: String) {
        cargarHistorial(tab)
    }

    private fun cargarHistorial(tab: String) {
        val todos = FakePrestamoDataSource.getPrestamosAgrupados()
        val filtrado = when (tab) {
            "Activos" -> todos.mapValues { (_, list) ->
                list.filter { it.estado == EstadoPrestamo.ACTIVO }
            }.filter { it.value.isNotEmpty() }
            "Devueltos" -> todos.mapValues { (_, list) ->
                list.filter { it.estado == EstadoPrestamo.DEVUELTO }
            }.filter { it.value.isNotEmpty() }
            "Vencidos" -> todos.mapValues { (_, list) ->
                list.filter { it.estado == EstadoPrestamo.VENCIDO }
            }.filter { it.value.isNotEmpty() }
            else -> todos
        }
        val totalPrestamos = FakePrestamoDataSource.getPrestamosRecientes().size
        _uiState.update {
            it.copy(
                tabActivo = tab,
                prestamosAgrupados = filtrado,
                totalMes = totalPrestamos,
                comparativaMes = "+12% vs. abril"
            )
        }
    }
}
