package com.example.aulix.ui.estudiante.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.EstadoAsistencia
import com.example.aulix.domain.model.RegistroAsistencia
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.estudiante.components.EstudianteBottomBar
import com.example.aulix.ui.estudiante.components.EstudianteTab
import com.example.aulix.ui.theme.*

// ── Historial de asistencias del estudiante ────────────────────────────────────
@Composable
fun EstudianteHistorialScreen(
    historial: List<RegistroAsistencia>,
    resumen: Triple<Int, Int, Int>, // porcentaje, asistidas, faltas
    onHoy: () -> Unit,
    onAgenda: () -> Unit,
    onPerfil: () -> Unit,
) {
    val (porcentaje, asistidas, faltas) = resumen
    val porFecha = historial.groupBy { it.fecha }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            EstudianteBottomBar(
                selected = EstudianteTab.HISTORIAL,
                onHoy = onHoy, onAgenda = onAgenda, onHistorial = {}, onPerfil = onPerfil,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                val periodoLabel = remember {
                    val d = java.time.LocalDate.now()
                    val mes = d.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale("es", "CO")).uppercase()
                    "ESTUDIANTE · $mes ${d.year}"
                }
                Text(periodoLabel, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Text("Mi historial", style = MaterialTheme.typography.titleLarge, color = Tinta)
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Resumen
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ResumenCard("$porcentaje%", "Asistencia", StatusGreen, Modifier.weight(1f))
                    ResumenCard("$asistidas", "Asistencias", Tinta, Modifier.weight(1f))
                    ResumenCard("$faltas", "Faltas", StatusRed, Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))
                Text("REGISTRO DE SESIONES", style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.45f), letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))

                porFecha.forEach { (fecha, registros) ->
                    Text(
                        fecha,
                        style = MaterialTheme.typography.labelSmall,
                        color = Tinta.copy(alpha = 0.45f),
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    )
                    registros.forEach { r ->
                        RegistroRow(r)
                        Spacer(Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ResumenCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    AulixCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineLarge, color = color, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Tinta.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun RegistroRow(r: RegistroAsistencia) {
    val (icon, color, bg, chipLabel, chipColor, chipBg) = estadoVisual(r.estado)
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(CircleShape).background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(r.sesion, style = MaterialTheme.typography.titleSmall, color = Tinta)
            Text(
                buildString {
                    append(r.asignatura); append(" · "); append(r.laboratorio)
                    if (r.hora != "—") { append(" · "); append(r.hora) }
                    if (r.via != "—") { append(" · "); append(r.via) }
                },
                style = MaterialTheme.typography.bodySmall,
                color = Tinta.copy(alpha = 0.5f),
            )
        }
        Spacer(Modifier.width(8.dp))
        StatusChip(label = chipLabel, color = chipColor, backgroundColor = chipBg)
    }
}

private data class EstadoVisual(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val bg: Color,
    val chipLabel: String,
    val chipColor: Color,
    val chipBg: Color,
)

@Composable
private fun estadoVisual(estado: EstadoAsistencia): EstadoVisual = when (estado) {
    EstadoAsistencia.ASISTIO -> EstadoVisual(Icons.Default.Check, StatusGreen, StatusGreen.copy(alpha = 0.15f), "ASISTIÓ", StatusGreen, StatusGreenBg)
    EstadoAsistencia.TARDE   -> EstadoVisual(Icons.Default.Schedule, StatusAmber, StatusAmber.copy(alpha = 0.15f), "TARDE", StatusAmber, StatusAmberBg)
    EstadoAsistencia.FALTA   -> EstadoVisual(Icons.Default.Close, StatusRed, StatusRed.copy(alpha = 0.12f), "FALTA", StatusRed, StatusRedBg)
}
