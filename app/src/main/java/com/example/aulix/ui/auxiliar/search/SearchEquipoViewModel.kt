package com.example.aulix.ui.auxiliar.search

import androidx.lifecycle.ViewModel
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoEquipo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchEquipoViewModel : ViewModel() {

    data class UiState(
        val query: String = "",
        val filtroActivo: String = "Más buscados",
        val resultados: List<Equipo> = emptyList(),
        val equipoSeleccionado: Equipo? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val filtros = listOf("Más buscados", "Disponibles", "Mi lab", "Hoy")

    init {
        _uiState.update {
            it.copy(resultados = FakePrestamoDataSource.getEquipos())
        }
    }

    fun onQueryChange(query: String) {
        val base = when (_uiState.value.filtroActivo) {
            "Disponibles" -> FakePrestamoDataSource.getEquipos()
                .filter { it.estado == EstadoEquipo.DISPONIBLE }
            else -> FakePrestamoDataSource.getEquipos()
        }
        val filtrados = if (query.isBlank()) base
        else base.filter {
            it.nombre.contains(query, ignoreCase = true) ||
            it.codigo.contains(query, ignoreCase = true) ||
            it.marca.contains(query, ignoreCase = true)
        }
        _uiState.update {
            it.copy(query = query, resultados = filtrados, equipoSeleccionado = null)
        }
    }

    fun onFiltroChange(filtro: String) {
        val base = FakePrestamoDataSource.getEquipos()
        val filtrados = when (filtro) {
            "Disponibles" -> base.filter { it.estado == EstadoEquipo.DISPONIBLE }
            else -> base
        }
        val query = _uiState.value.query
        val resultado = if (query.isBlank()) filtrados
        else filtrados.filter {
            it.nombre.contains(query, ignoreCase = true) ||
            it.codigo.contains(query, ignoreCase = true)
        }
        _uiState.update {
            it.copy(filtroActivo = filtro, resultados = resultado, equipoSeleccionado = null)
        }
    }

    fun onEquipoSeleccionado(equipo: Equipo) {
        if (equipo.estado != EstadoEquipo.DISPONIBLE) return
        _uiState.update { it.copy(equipoSeleccionado = equipo) }
    }
}
