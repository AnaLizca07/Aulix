package com.example.aulix.ui.docente.incidencias

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.EquipoRepository
import com.example.aulix.data.repository.IncidenciaRepository
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.PrioridadIncidencia
import com.example.aulix.domain.model.TipoIncidencia
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReportarIncidenciaViewModel @Inject constructor(
    private val incidenciaRepo: IncidenciaRepository,
    private val equipoRepo: EquipoRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    data class UiState(
        val equipos: List<Equipo> = emptyList(),
        val equipoSeleccionado: Equipo? = null,
        val descripcion: String = "",
        val isLoading: Boolean = false,
        val enviado: Boolean = false,
        val error: String? = null,
        val imagenesUri: List<Uri> = emptyList(),
        val cameraUri: Uri? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            equipoRepo.getEquipos()
                .onSuccess { equipos -> _uiState.update { it.copy(equipos = equipos) } }
        }
    }

    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v) }
    fun onEquipoSeleccionado(equipo: Equipo) = _uiState.update { it.copy(equipoSeleccionado = equipo) }
    fun clearError() = _uiState.update { it.copy(error = null) }

    fun crearUriCamara(): Uri {
        val file = File(context.cacheDir, "cam_${UUID.randomUUID()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        _uiState.update { it.copy(cameraUri = uri) }
        return uri
    }

    fun onFotoTomada(exito: Boolean) {
        if (!exito) return
        val uri = _uiState.value.cameraUri ?: return
        _uiState.update { it.copy(imagenesUri = it.imagenesUri + uri, cameraUri = null) }
    }

    fun onImagenSeleccionada(uri: Uri?) {
        uri ?: return
        if (_uiState.value.imagenesUri.size >= 3) return
        _uiState.update { it.copy(imagenesUri = it.imagenesUri + uri) }
    }

    fun eliminarImagen(uri: Uri) {
        _uiState.update { it.copy(imagenesUri = it.imagenesUri - uri) }
    }

    fun enviar(tipo: TipoIncidencia, sesionId: String?) {
        val state = _uiState.value
        if (tipo == TipoIncidencia.EQUIPO && state.equipoSeleccionado == null) return
        if (state.descripcion.isBlank()) return
        val equipoId = state.equipoSeleccionado?.id

        val titulo = when (tipo) {
            TipoIncidencia.EQUIPO          -> "Falla de equipo"
            TipoIncidencia.SEGURIDAD       -> "Incidencia de seguridad"
            TipoIncidencia.INFRAESTRUCTURA -> "Falla de infraestructura"
            TipoIncidencia.OTRA            -> "Otra incidencia"
        }
        val severidad = when (tipo) {
            TipoIncidencia.SEGURIDAD                                    -> PrioridadIncidencia.ALTA
            TipoIncidencia.EQUIPO, TipoIncidencia.INFRAESTRUCTURA       -> PrioridadIncidencia.MEDIA
            TipoIncidencia.OTRA                                         -> PrioridadIncidencia.BAJA
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val urlsEvidencias = state.imagenesUri.mapNotNull { uri ->
                runCatching {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: return@mapNotNull null
                    val path = "incidencias/${UUID.randomUUID()}.jpg"
                    incidenciaRepo.subirEvidencia(path, bytes).getOrNull()
                }.getOrNull()
            }

            incidenciaRepo.registrar(
                titulo      = titulo,
                descripcion = state.descripcion.trim(),
                severidad   = severidad,
                equipoId    = equipoId,
                sesionId    = sesionId,
                evidencias  = urlsEvidencias,
            )
                .onSuccess { _uiState.update { it.copy(isLoading = false, enviado = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
