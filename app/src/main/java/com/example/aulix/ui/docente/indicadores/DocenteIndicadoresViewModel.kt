package com.example.aulix.ui.docente.indicadores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aulix.data.repository.AsistenciaRepository
import com.example.aulix.data.repository.SesionRepository
import com.example.aulix.domain.model.EstadoSesion
import com.example.aulix.domain.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocenteIndicadoresViewModel @Inject constructor(
    private val sesionRepo: SesionRepository,
    private val asistenciaRepo: AsistenciaRepository,
) : ViewModel() {

    data class AsistenciaAsignatura(
        val nombre: String,
        val totalAsistencias: Int,
        val totalSesiones: Int,
        val tasa: Int,
    )

    data class UiState(
        val sesionesTotales: Int = 0,
        val sesionesCompletadas: Int = 0,
        val sesionesPendientes: Int = 0,
        val sesionesCanceladas: Int = 0,
        val asistenciaPromedio: String = "—",
        val asistenciaPorAsignatura: List<AsistenciaAsignatura> = emptyList(),
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { cargar() }

    fun cargar() {
        val uid = UserSession.currentUser?.id ?: return
        viewModelScope.launch {
            val sesiones = sesionRepo.getMisSesiones().getOrElse { emptyList() }
            val resumen = asistenciaRepo.getResumenAsistenciaDocente(uid).getOrElse { emptyList() }

            val sesionesPorAsig = sesiones
                .filter { it.asignatura.isNotBlank() }
                .groupBy { it.asignatura }
                .mapValues { it.value.size }

            val asistenciasPorAsig = resumen
                .filter { it.asignaturaNombre.isNotBlank() }
                .groupBy { it.asignaturaNombre }
                .mapValues { it.value.size }

            val stats = asistenciasPorAsig
                .map { (nombre, total) ->
                    nombre to (total.toFloat() / (sesionesPorAsig[nombre] ?: 1))
                }
                .sortedByDescending { it.second }

            val maxAvg = stats.maxOfOrNull { it.second }?.takeIf { it > 0f } ?: 1f

            val asistenciaPorAsignatura = stats.map { (nombre, avg) ->
                AsistenciaAsignatura(
                    nombre = nombre,
                    totalAsistencias = asistenciasPorAsig[nombre] ?: 0,
                    totalSesiones = sesionesPorAsig[nombre] ?: 0,
                    tasa = ((avg / maxAvg) * 100).toInt().coerceIn(0, 100),
                )
            }

            val completadas = sesiones.count { it.estado == EstadoSesion.CERRADA }
            val promedioGlobal = if (completadas > 0)
                "%.0f".format(resumen.size.toFloat() / completadas)
            else "—"

            _uiState.update {
                it.copy(
                    sesionesTotales = sesiones.size,
                    sesionesCompletadas = completadas,
                    sesionesPendientes = sesiones.count { s ->
                        s.estado == EstadoSesion.PROGRAMADA || s.estado == EstadoSesion.ACTIVA
                    },
                    sesionesCanceladas = sesiones.count { s -> s.estado == EstadoSesion.CANCELADA },
                    asistenciaPromedio = promedioGlobal,
                    asistenciaPorAsignatura = asistenciaPorAsignatura,
                )
            }
        }
    }
}
