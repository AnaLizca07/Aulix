package com.example.aulix.ui.auxiliar.historial

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aulix.domain.model.EstadoPrestamo
import com.example.aulix.domain.model.Prestamo
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.theme.*

@Composable
fun HistorialScreen(
    onBack: () -> Unit,
    viewModel: HistorialViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(containerColor = Lienzo) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Lienzo)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
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
                        text = "HU 12 · LAB-B-204",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cobre,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Historial de préstamos",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Tinta
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = Tinta.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card analítica
                AulixCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Cobalto, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Cobalto)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "MAYO 2026 · A LA FECHA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = state.totalMes.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = state.comparativaMes,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "vs mes anterior",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                        Text(
                            text = "préstamos registrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        BarChartCanvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                }

                // TabRow
                TabRow(
                    selectedTabIndex = viewModel.tabs.indexOf(state.tabActivo),
                    containerColor = Color.White,
                    contentColor = Cobalto,
                    divider = {
                        HorizontalDivider(color = BorderLight, thickness = 1.dp)
                    }
                ) {
                    viewModel.tabs.forEach { tab ->
                        Tab(
                            selected = state.tabActivo == tab,
                            onClick = { viewModel.onTabChange(tab) },
                            text = {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selectedContentColor = Cobalto,
                            unselectedContentColor = Tinta.copy(alpha = 0.5f)
                        )
                    }
                }

                // Lista agrupada por fecha
                // Column en lugar de LazyColumn — estamos dentro de verticalScroll
                if (state.prestamosAgrupados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin préstamos en esta categoría",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tinta.copy(alpha = 0.4f)
                        )
                    }
                } else {
                    state.prestamosAgrupados.forEach { (fecha, prestamos) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(StatusAmber, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatearFecha(fecha),
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f),
                                letterSpacing = 0.5.sp
                            )
                        }
                        prestamos.forEach { prestamo ->
                            HistorialItemRow(prestamo = prestamo)
                            HorizontalDivider(
                                color = Tinta.copy(alpha = 0.07f),
                                thickness = 1.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BarChartCanvas(modifier: Modifier = Modifier) {
    val valores = listOf(3, 4, 2, 5, 6, 3, 5, 6, 4, 5, 6, 4, 5, 7, 5)
    val maxVal = valores.max().toFloat()

    Canvas(modifier = modifier) {
        val barWidth = 12.dp.toPx()
        val gap = 4.dp.toPx()
        val totalWidth = valores.size * (barWidth + gap) - gap
        val startX = (size.width - totalWidth) / 2f

        valores.forEachIndexed { index, valor ->
            val barHeight = (valor / maxVal) * size.height
            val x = startX + index * (barWidth + gap)
            val isLast = index == valores.size - 1
            drawRoundRect(
                color = Color.White.copy(alpha = if (isLast) 1f else 0.35f),
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
        }
    }
}

@Composable
private fun HistorialItemRow(prestamo: Prestamo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prestamo.horaInicio,
            style = MaterialTheme.typography.labelSmall,
            color = Tinta.copy(alpha = 0.5f),
            modifier = Modifier.width(40.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prestamo.equipo.nombre,
                style = MaterialTheme.typography.titleSmall,
                color = Tinta
            )
            Text(
                text = "${prestamo.equipo.codigo} · ${prestamo.destinatarioNombre} · ${prestamo.duracionHoras}h",
                style = MaterialTheme.typography.bodySmall,
                color = Tinta.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        val (label, color, bgColor) = when (prestamo.estado) {
            EstadoPrestamo.ACTIVO   -> Triple("ACTIVO", Cobalto, Cielo)
            EstadoPrestamo.DEVUELTO -> Triple("DEVUELTO", StatusGreen, StatusGreenBg)
            EstadoPrestamo.VENCIDO  -> Triple("VENCIDO", StatusRed, StatusRedBg)
        }
        StatusChip(label = label, color = color, backgroundColor = bgColor)
    }
}

private fun formatearFecha(fecha: String): String {
    return when (fecha) {
        "2026-05-22" -> "• HOY · JUE 22 MAY"
        "2026-05-21" -> "MIÉRCOLES 21 MAY"
        else -> {
            val parts = fecha.split("-")
            if (parts.size == 3) {
                val meses = listOf("ENE","FEB","MAR","ABR","MAY","JUN",
                                   "JUL","AGO","SEP","OCT","NOV","DIC")
                val mes = parts[1].toIntOrNull()?.minus(1)?.let { meses.getOrNull(it) } ?: ""
                "HOY · ${parts[2]} $mes ${parts[0]}"
            } else fecha
        }
    }
}
