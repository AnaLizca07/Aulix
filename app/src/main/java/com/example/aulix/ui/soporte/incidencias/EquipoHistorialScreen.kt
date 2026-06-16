package com.example.aulix.ui.soporte.incidencias

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulix.domain.model.EstadoIncidencia
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.theme.Arena
import com.example.aulix.ui.theme.Cobalto
import com.example.aulix.ui.theme.Lienzo
import com.example.aulix.ui.theme.RoleSoporte
import com.example.aulix.ui.theme.StatusAmber
import com.example.aulix.ui.theme.StatusAmberBg
import com.example.aulix.ui.theme.StatusGreen
import com.example.aulix.ui.theme.StatusGreenBg
import com.example.aulix.ui.theme.StatusRed
import com.example.aulix.ui.theme.StatusRedBg
import com.example.aulix.ui.theme.Tinta

@Composable
fun EquipoHistorialScreen(
    equipoId: String,
    onBack: () -> Unit,
    onProgramarMantenimiento: () -> Unit = {},
    viewModel: EquipoHistorialViewModel = hiltViewModel<EquipoHistorialViewModel, EquipoHistorialViewModel.Factory>(
        creationCallback = { factory -> factory.create(equipoId) }
    ),
) {
    val vmState by viewModel.uiState.collectAsState()
    val equipo = vmState.equipo ?: return
    val incidencias = vmState.incidencias

    val tieneFallasRepetidas = remember(incidencias) {
        val hace60 = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_YEAR, -60)
        }
        val recientes = incidencias.filter {
            val cal = parseFecha(it.fecha) ?: return@filter false
            !cal.before(hace60)
        }
        recientes.groupBy { it.titulo.lowercase().trim() }.any { it.value.size >= 3 }
    }

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
                            text = "${equipo.laboratorio} · ${equipo.codigo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RoleSoporte,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Historial por equipo",
                            style = MaterialTheme.typography.titleMedium,
                            color = Tinta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
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

                    // ── Card del equipo ──────────────────────────────────────────
                    AulixCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Arena),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = Tinta.copy(alpha = 0.45f),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = equipo.nombre,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Tinta,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = equipo.codigo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tinta.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "${equipo.laboratorio} · Compra 2022",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tinta.copy(alpha = 0.5f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = incidencias.size.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Cobalto,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "incidencias",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Tinta.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }

                    // ── Banner fallas repetidas (condicional) ────────────────────
                    if (tieneFallasRepetidas) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(StatusRedBg)
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = StatusRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "FALLAS REPETIDAS · ÚLTIMOS 60 DÍAS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = StatusRed,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Se detectaron fallas recurrentes en este equipo. Se recomienda programar mantenimiento preventivo.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Tinta.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Lista historial ──────────────────────────────────────────
                    Text(
                        text = "HISTORIAL · ${incidencias.size} REGISTROS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Tinta.copy(alpha = 0.45f),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (incidencias.isEmpty()) {
                        Text(
                            text = "Sin incidencias registradas para este equipo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tinta.copy(alpha = 0.4f),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        incidencias.forEachIndexed { index, incidencia ->
                            HistorialItemRow(
                                incidencia = incidencia,
                                esUltimo = index == incidencias.lastIndex
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ── Botones bottom ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Exportar", style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick = onProgramarMantenimiento,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cobalto)
                ) {
                    Text(
                        text = "Prog. mantenimiento",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun HistorialItemRow(incidencia: Incidencia, esUltimo: Boolean) {
    val (estadoLabel, estadoColor, estadoBg) = when (incidencia.estado) {
        EstadoIncidencia.ABIERTA -> Triple("ABIERTA", StatusRed, StatusRedBg)
        EstadoIncidencia.EN_ATENCION -> Triple("EN ATENCIÓN", StatusAmber, StatusAmberBg)
        EstadoIncidencia.RESUELTA -> Triple("RESUELTA", StatusGreen, StatusGreenBg)
    }
    val solucion = if (incidencia.estado == EstadoIncidencia.RESUELTA) {
        incidencia.lineaTiempo.lastOrNull { it.autor != "Sistema" }?.descripcion
    } else null

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(estadoColor)
            )
            if (!esUltimo) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(Tinta.copy(alpha = 0.1f))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (!esUltimo) 8.dp else 0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "#${incidencia.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.4f)
                )
                StatusChip(label = estadoLabel, color = estadoColor, backgroundColor = estadoBg)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = incidencia.titulo,
                style = MaterialTheme.typography.titleSmall,
                color = Tinta,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${incidencia.fecha} · ${if (incidencia.asignadoA != null) incidencia.asignadoA else "Sin asignar"}",
                style = MaterialTheme.typography.bodySmall,
                color = Tinta.copy(alpha = 0.5f)
            )
            if (solucion != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ $solucion",
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusGreen,
                    maxLines = 2
                )
            }
        }
    }
}

private fun parseFecha(fecha: String): java.util.Calendar? {
    return try {
        val p = fecha.split("-")
        java.util.Calendar.getInstance().apply {
            set(p[0].toInt(), p[1].toInt() - 1, p[2].toInt(), 0, 0, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
    } catch (e: Exception) {
        null
    }
}
