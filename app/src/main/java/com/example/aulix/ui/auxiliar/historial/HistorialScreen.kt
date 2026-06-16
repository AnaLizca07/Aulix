package com.example.aulix.ui.auxiliar.historial

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aulix.domain.model.EstadoPrestamo
import com.example.aulix.domain.model.Prestamo
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.theme.*

@Composable
fun HistorialScreen(
    onBack: () -> Unit,
    viewModel: HistorialViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(containerColor = Lienzo) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                        text = "HISTORIAL",
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Cobalto),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            val mesBanner = remember {
                                val d = java.time.LocalDate.now()
                                val mes = d.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("es", "CO")).uppercase()
                                "$mes ${d.year} · A LA FECHA"
                            }
                            Text(
                                text = mesBanner,
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
                                valores = state.prestamosPorDia,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                        }
                    }
                }

                item {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
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
                    }
                }

                state.prestamosAgrupados.forEach { (fecha, prestamos) ->
                    item(key = "header_$fecha") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(vertical = 4.dp)
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
                    }
                    items(
                        items = prestamos,
                        key = { "prestamo_${it.id}" }
                    ) { prestamo ->
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            HistorialItemRow(prestamo = prestamo)
                            HorizontalDivider(
                                color = Tinta.copy(alpha = 0.07f),
                                thickness = 1.dp
                            )
                        }
                    }
                }

                if (state.prestamosAgrupados.isEmpty()) {
                    item {
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
                    }
                }
            }
        }
    }
}

@Composable
private fun BarChartCanvas(valores: List<Int>, modifier: Modifier = Modifier) {
    if (valores.isEmpty()) return
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
            EstadoPrestamo.ACTIVO   -> Triple("ACTIVO",   Cobalto,     Cielo)
            EstadoPrestamo.DEVUELTO -> Triple("DEVUELTO", StatusGreen, StatusGreenBg)
            EstadoPrestamo.VENCIDO  -> Triple("VENCIDO",  StatusRed,   StatusRedBg)
        }
        StatusChip(label = label, color = color, backgroundColor = bgColor)
    }
}

private fun formatearFecha(fecha: String): String {
    val parts = fecha.split("-")
    if (parts.size != 3) return fecha
    val anio = parts[0].toIntOrNull() ?: return fecha
    val mes  = parts[1].toIntOrNull() ?: return fecha
    val dia  = parts[2].toIntOrNull() ?: return fecha

    val meses   = listOf("ENE","FEB","MAR","ABR","MAY","JUN","JUL","AGO","SEP","OCT","NOV","DIC")
    val diasSem = listOf("DOM","LUN","MAR","MIÉ","JUE","VIE","SÁB")
    val mesStr  = meses.getOrElse(mes - 1) { "" }

    val cal = java.util.Calendar.getInstance()
    cal.set(anio, mes - 1, dia)
    val diaSemana = diasSem.getOrElse(cal.get(java.util.Calendar.DAY_OF_WEEK) - 1) { "" }

    val hoy = java.util.Calendar.getInstance()
    val esHoy = anio == hoy.get(java.util.Calendar.YEAR) &&
                mes  == hoy.get(java.util.Calendar.MONTH) + 1 &&
                dia  == hoy.get(java.util.Calendar.DAY_OF_MONTH)

    return if (esHoy) "• HOY · $diaSemana $dia $mesStr" else "$diaSemana $dia $mesStr"
}
