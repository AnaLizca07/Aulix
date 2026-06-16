package com.example.aulix.ui.auxiliar.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.PrestamoRepository
import com.example.aulix.domain.model.EstadoPrestamo
import com.example.aulix.domain.model.Prestamo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val prestamoRepo: PrestamoRepository,
) : ViewModel() {

    data class UiState(
        val tabActivo: String = "Todos",
        val prestamosAgrupados: Map<String, List<Prestamo>> = emptyMap(),
        val totalMes: Int = 0,
        val comparativaMes: String = "",
        val prestamosPorDia: List<Int> = emptyList(),
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val tabs = listOf("Todos", "Activos", "Devueltos", "Vencidos")

    private var todosLosPrestamos: List<Prestamo> = emptyList()

    init { cargarHistorial() }

    fun onTabChange(tab: String) {
        val filtrado = filtrarYAgrupar(todosLosPrestamos, tab)
        _uiState.update { it.copy(tabActivo = tab, prestamosAgrupados = filtrado) }
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            prestamoRepo.getHistorial()
                .onSuccess { prestamos ->
                    todosLosPrestamos = prestamos
                    val prestamosPorDia = prestamos
                        .groupBy { it.fecha }
                        .entries
                        .sortedBy { it.key }
                        .takeLast(15)
                        .map { it.value.size }
                    _uiState.update {
                        it.copy(
                            prestamosAgrupados = filtrarYAgrupar(prestamos, "Todos"),
                            totalMes = prestamos.size,
                            comparativaMes = "+12% vs. mes anterior",
                            prestamosPorDia = prestamosPorDia,
                        )
                    }
                }
        }
    }

    private fun filtrarYAgrupar(prestamos: List<Prestamo>, tab: String): Map<String, List<Prestamo>> {
        val filtrado = when (tab) {
            "Activos"   -> prestamos.filter { it.estado == EstadoPrestamo.ACTIVO }
            "Devueltos" -> prestamos.filter { it.estado == EstadoPrestamo.DEVUELTO }
            "Vencidos"  -> prestamos.filter { it.estado == EstadoPrestamo.VENCIDO }
            else        -> prestamos
        }
        return filtrado.groupBy { it.fecha }
    }
}
