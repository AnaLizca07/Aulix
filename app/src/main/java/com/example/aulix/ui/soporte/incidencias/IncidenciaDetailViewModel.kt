package com.example.aulix.ui.soporte.incidencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aulix.data.local.FakeIncidenciaDataSource
import com.example.aulix.domain.model.EstadoIncidencia
import com.example.aulix.domain.model.Incidencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IncidenciaDetailViewModel(private val incidenciaId: String) : ViewModel() {

    data class UiState(
        val incidencia: Incidencia? = null,
        val isLoading: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { recargarDatos() }

    fun recargarDatos() {
        _uiState.update { it.copy(incidencia = FakeIncidenciaDataSource.getIncidenciaById(incidenciaId)) }
    }

    fun cambiarEstado(nuevoEstado: EstadoIncidencia) {
        FakeIncidenciaDataSource.cambiarEstado(incidenciaId, nuevoEstado)
        recargarDatos()
    }

    companion object {
        fun factory(incidenciaId: String) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return IncidenciaDetailViewModel(incidenciaId) as T
            }
        }
    }
}
