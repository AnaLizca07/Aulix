package com.example.aulix.ui.soporte.incidencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.IncidenciaRepository
import com.example.aulix.domain.model.EstadoIncidencia
import com.example.aulix.domain.model.Incidencia
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = IncidenciaDetailViewModel.Factory::class)
class IncidenciaDetailViewModel @AssistedInject constructor(
    @Assisted private val incidenciaId: String,
    private val incidenciaRepo: IncidenciaRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(incidenciaId: String): IncidenciaDetailViewModel
    }

    data class UiState(
        val incidencia: Incidencia? = null,
        val isLoading: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { recargarDatos() }

    fun recargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            incidenciaRepo.getIncidenciaById(incidenciaId)
                .onSuccess { incidencia -> _uiState.update { it.copy(incidencia = incidencia, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    fun cambiarEstado(nuevoEstado: EstadoIncidencia) {
        viewModelScope.launch {
            incidenciaRepo.cambiarEstado(incidenciaId, nuevoEstado)
                .onSuccess { recargarDatos() }
        }
    }
}
