package com.example.aulix.ui.soporte.incidencias

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aulix.domain.model.EstadoIncidencia
import com.example.aulix.domain.model.EventoIncidencia
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.domain.model.PrioridadIncidencia
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.Arena
import com.example.aulix.ui.theme.BorderLight
import com.example.aulix.ui.theme.Cobalto
import com.example.aulix.ui.theme.Cobre
import com.example.aulix.ui.theme.Lienzo
import com.example.aulix.ui.theme.RoleSoporte
import com.example.aulix.ui.theme.StatusAmber
import com.example.aulix.ui.theme.StatusAmberBg
import com.example.aulix.ui.theme.StatusGray
import com.example.aulix.ui.theme.StatusGrayBg
import com.example.aulix.ui.theme.StatusGreen
import com.example.aulix.ui.theme.StatusGreenBg
import com.example.aulix.ui.theme.StatusRed
import com.example.aulix.ui.theme.StatusRedBg
import com.example.aulix.ui.theme.Tinta

@Composable
fun IncidenciaDetailScreen(
    incidenciaId: String,
    onBack: () -> Unit,
    onVerHistorialEquipo: (String) -> Unit,
    viewModel: IncidenciaDetailViewModel = viewModel(factory = IncidenciaDetailViewModel.factory(incidenciaId))
) {
    val state by viewModel.uiState.collectAsState()
    val incidencia = state.incidencia ?: return

    Scaffold(containerColor = Lienzo) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Header ───────────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Lienzo)
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Tinta
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${incidencia.equipo.laboratorio} · INCIDENCIA #${incidencia.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RoleSoporte,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = incidencia.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            color = Tinta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones",
                            tint = Tinta.copy(alpha = 0.6f)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    // ── Selector de estado ───────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EstadoChip(
                            label = "Abierta",
                            activo = incidencia.estado == EstadoIncidencia.ABIERTA,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.cambiarEstado(EstadoIncidencia.ABIERTA) }
                        )
                        EstadoChip(
                            label = "En atención",
                            activo = incidencia.estado == EstadoIncidencia.EN_ATENCION,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.cambiarEstado(EstadoIncidencia.EN_ATENCION) }
                        )
                        EstadoChip(
                            label = "Resuelta",
                            activo = incidencia.estado == EstadoIncidencia.RESUELTA,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.cambiarEstado(EstadoIncidencia.RESUELTA) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Card equipo ──────────────────────────────────────────────
                    AulixCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Arena),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = Tinta.copy(alpha = 0.45f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = incidencia.equipo.nombre,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Tinta,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${incidencia.equipo.codigo} · ${incidencia.equipo.laboratorio}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tinta.copy(alpha = 0.5f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Cobalto.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .clickable { onVerHistorialEquipo(incidencia.equipo.id) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Historial",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Cobalto
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Reporte original ─────────────────────────────────────────
                    AulixCard {
                        Column {
                            Text(
                                text = "REPORTE ORIGINAL",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.4f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val reportanteRole = when (incidencia.rolReportante.lowercase()) {
                                    "docente" -> UserRole.DOCENTE
                                    "estudiante" -> UserRole.ESTUDIANTE
                                    "auxiliar" -> UserRole.AUXILIAR
                                    else -> UserRole.SOPORTE_TECNICO
                                }
                                val iniciales = incidencia.reportadoPor
                                    .split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                    .joinToString("")
                                UserAvatar(initials = iniciales, role = reportanteRole, size = 36)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = incidencia.reportadoPor,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Tinta
                                    )
                                    Text(
                                        text = "${incidencia.fecha} · ${incidencia.horaReporte}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Tinta.copy(alpha = 0.45f)
                                    )
                                }
                                StatusChip(
                                    label = incidencia.rolReportante.uppercase(),
                                    color = Tinta.copy(alpha = 0.6f),
                                    backgroundColor = Arena
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = incidencia.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Tinta.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            // Placeholders de fotos
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FotoPlaceholder(label = "FOTO 1")
                                FotoPlaceholder(label = "FOTO 2")
                                FotoPlaceholder(label = "+1")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Línea de tiempo ──────────────────────────────────────────
                    AulixCard {
                        Column {
                            Text(
                                text = "LÍNEA DE TIEMPO",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.4f),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            incidencia.lineaTiempo.forEachIndexed { index, evento ->
                                EventoTimelineRow(
                                    evento = evento,
                                    esUltimo = index == incidencia.lineaTiempo.lastIndex
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ── CTA Button ───────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                when (incidencia.estado) {
                    EstadoIncidencia.ABIERTA -> AulixButton(
                        text = "Tomar y marcar en atención",
                        onClick = { viewModel.cambiarEstado(EstadoIncidencia.EN_ATENCION) }
                    )
                    EstadoIncidencia.EN_ATENCION -> AulixButton(
                        text = "Marcar como resuelta",
                        onClick = { viewModel.cambiarEstado(EstadoIncidencia.RESUELTA) }
                    )
                    EstadoIncidencia.RESUELTA -> AulixButton(
                        text = "Resuelta",
                        onClick = {},
                        enabled = false
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoChip(
    label: String,
    activo: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (activo) Cobalto else Arena)
            .border(
                width = 1.dp,
                color = if (activo) Cobalto else Tinta.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (activo) Color.White else Tinta.copy(alpha = 0.7f),
            fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun FotoPlaceholder(label: String) {
    Box(
        modifier = Modifier
            .size(width = 70.dp, height = 56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Arena)
            .border(1.dp, BorderLight, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Tinta.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun EventoTimelineRow(evento: EventoIncidencia, esUltimo: Boolean) {
    val esTecnico = evento.autor != "Sistema"
    val dotColor = if (esTecnico) Cobre else Cobalto
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            if (!esUltimo) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(Tinta.copy(alpha = 0.1f))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = evento.autor,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (esTecnico) Cobre else Cobalto,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = evento.hora,
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.4f)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = evento.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Tinta.copy(alpha = 0.7f)
            )
            if (!esUltimo) Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
