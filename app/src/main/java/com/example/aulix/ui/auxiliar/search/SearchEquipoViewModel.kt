package com.example.aulix.ui.auxiliar.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoEquipo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchEquipoViewModel @Inject constructor(
    private val equipoRepo: EquipoRepository,
) : ViewModel() {

    data class UiState(
        val query: String = "",
        val filtroActivo: String = "Más buscados",
        val resultados: List<Equipo> = emptyList(),
        val equipoSeleccionado: Equipo? = null,
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val filtros = listOf("Más buscados", "Disponibles", "Mi lab", "Hoy")

    init { cargarEquipos() }

    private fun cargarEquipos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            equipoRepo.getEquipos()
                .onSuccess { equipos ->
                    _uiState.update { it.copy(resultados = equipos, isLoading = false) }
                }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    fun onQueryChange(query: String) {
        viewModelScope.launch {
            equipoRepo.buscarEquipos(query)
                .onSuccess { resultados ->
                    _uiState.update {
                        it.copy(
                            query = query,
                            resultados = applyFiltro(resultados, it.filtroActivo),
                            equipoSeleccionado = null,
                        )
                    }
                }
        }
    }

    fun onFiltroChange(filtro: String) {
        viewModelScope.launch {
            equipoRepo.buscarEquipos(_uiState.value.query)
                .onSuccess { resultados ->
                    _uiState.update {
                        it.copy(
                            filtroActivo = filtro,
                            resultados = applyFiltro(resultados, filtro),
                            equipoSeleccionado = null,
                        )
                    }
                }
        }
    }

    fun onEquipoSeleccionado(equipo: Equipo) {
        if (equipo.estado != EstadoEquipo.DISPONIBLE) return
        _uiState.update { it.copy(equipoSeleccionado = equipo) }
    }

    private fun applyFiltro(equipos: List<Equipo>, filtro: String): List<Equipo> =
        when (filtro) {
            "Disponibles" -> equipos.filter { it.estado == EstadoEquipo.DISPONIBLE }
            else -> equipos
        }
}
