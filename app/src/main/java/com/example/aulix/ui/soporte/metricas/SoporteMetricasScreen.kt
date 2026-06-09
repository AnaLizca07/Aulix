package com.example.aulix.ui.soporte.metricas

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.aulix.data.local.FakeIncidenciaDataSource
import com.example.aulix.data.local.FakePrestamoDataSource
import com.example.aulix.domain.model.EstadoEquipo
import com.example.aulix.domain.model.EstadoIncidencia
import com.example.aulix.domain.model.PrioridadIncidencia
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.navigation.Route
import com.example.aulix.ui.theme.Cielo
import com.example.aulix.ui.theme.Cobalto
import com.example.aulix.ui.theme.Lienzo
import com.example.aulix.ui.theme.RoleSoporte
import com.example.aulix.ui.theme.StatusAmber
import com.example.aulix.ui.theme.StatusGray
import com.example.aulix.ui.theme.StatusGreen
import com.example.aulix.ui.theme.StatusRed
import com.example.aulix.ui.theme.Tinta

@Composable
fun SoporteMetricasScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val incidencias = remember { FakeIncidenciaDataSource.getIncidencias() }
    val equipos = remember { FakePrestamoDataSource.getEquipos() }

    val cal = remember { java.util.Calendar.getInstance() }
    val mesActual = remember {
        "%04d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1
        )
    }

    val totalMes = remember(incidencias) { incidencias.count { it.fecha.startsWith(mesActual) } }
    val resueltas = remember(incidencias) { incidencias.count { it.estado == EstadoIncidencia.RESUELTA } }
    val tasaResolucion = remember(resueltas, incidencias) {
        if (incidencias.isNotEmpty()) (resueltas * 100f / incidencias.size) else 0f
    }
    val fueraDeServicio = remember(equipos) { equipos.count { it.estado == EstadoEquipo.REPARACION } }

    val porLaboratorio = remember(incidencias) {
        incidencias.groupBy { it.equipo.laboratorio }
            .mapValues { it.value.size }
            .entries.sortedByDescending { it.value }
    }
    val maxLab = remember(porLaboratorio) { porLaboratorio.maxOfOrNull { it.value } ?: 1 }

    val alta = remember(incidencias) { incidencias.count { it.prioridad == PrioridadIncidencia.ALTA } }
    val media = remember(incidencias) { incidencias.count { it.prioridad == PrioridadIncidencia.MEDIA } }
    val baja = remember(incidencias) { incidencias.count { it.prioridad == PrioridadIncidencia.BAJA } }
    val totalSeveridad = alta + media + baja

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                val isBandeja = currentDestination?.hasRoute(Route.SoporteDashboard::class) == true
                val isMetricas = currentDestination?.hasRoute(Route.SoporteMetricas::class) == true
                val isPerfil = currentDestination?.hasRoute(Route.SoportePerfil::class) == true
                NavigationBarItem(
                    selected = isBandeja,
                    onClick = {
                        navController.navigate(Route.SoporteDashboard) {
                            popUpTo(Route.SoporteDashboard) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Bandeja") },
                    label = { Text("Bandeja", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Cobalto,
                        selectedTextColor = Cobalto,
                        unselectedIconColor = Tinta.copy(alpha = 0.4f),
                        unselectedTextColor = Tinta.copy(alpha = 0.4f),
                        indicatorColor = Cielo
                    )
                )
                NavigationBarItem(
                    selected = isMetricas,
                    onClick = {},
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Métricas") },
                    label = { Text("Métricas", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Cobalto,
                        selectedTextColor = Cobalto,
                        unselectedIconColor = Tinta.copy(alpha = 0.4f),
                        unselectedTextColor = Tinta.copy(alpha = 0.4f),
                        indicatorColor = Cielo
                    )
                )
                NavigationBarItem(
                    selected = isPerfil,
                    onClick = {
                        navController.navigate(Route.SoportePerfil) {
                            popUpTo(Route.SoporteDashboard) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Cobalto,
                        selectedTextColor = Cobalto,
                        unselectedIconColor = Tinta.copy(alpha = 0.4f),
                        unselectedTextColor = Tinta.copy(alpha = 0.4f),
                        indicatorColor = Cielo
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Lienzo)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SOPORTE TÉCNICO",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoleSoporte,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Métricas",
                        style = MaterialTheme.typography.titleLarge,
                        color = Tinta
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Tasa de resolución ───────────────────────────────────────
                AulixCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TASA DE RESOLUCIÓN",
                            style = MaterialTheme.typography.labelSmall,
                            color = Tinta.copy(alpha = 0.45f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(contentAlignment = Alignment.Center) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.size(120.dp)) {
                                val strokeWidth = 14.dp.toPx()
                                val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                drawArc(
                                    color = Lienzo,
                                    startAngle = -210f,
                                    sweepAngle = 240f,
                                    useCenter = false,
                                    style = stroke
                                )
                                drawArc(
                                    color = StatusGreen,
                                    startAngle = -210f,
                                    sweepAngle = 240f * (tasaResolucion / 100f),
                                    useCenter = false,
                                    style = stroke
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${tasaResolucion.toInt()}%",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Tinta,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$resueltas de ${incidencias.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Tinta.copy(alpha = 0.5f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // ── KPIs del mes ─────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = totalMes.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = Cobalto,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Incidencias",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "del mes",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = fueraDeServicio.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = StatusAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Equipos fuera",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "de servicio",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // ── Incidencias por laboratorio ───────────────────────────────
                AulixCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "POR LABORATORIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = Tinta.copy(alpha = 0.45f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        porLaboratorio.forEach { (lab, count) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lab,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tinta.copy(alpha = 0.7f),
                                    modifier = Modifier.width(96.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Lienzo)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(count.toFloat() / maxLab.toFloat())
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(Cobalto)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Cobalto,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                // ── Distribución por severidad ────────────────────────────────
                AulixCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "DISTRIBUCIÓN POR SEVERIDAD",
                            style = MaterialTheme.typography.labelSmall,
                            color = Tinta.copy(alpha = 0.45f),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (totalSeveridad > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(50.dp))
                            ) {
                                if (alta > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(alta.toFloat())
                                            .fillMaxSize()
                                            .background(StatusRed)
                                    )
                                }
                                if (media > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(media.toFloat())
                                            .fillMaxSize()
                                            .background(StatusAmber)
                                    )
                                }
                                if (baja > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(baja.toFloat())
                                            .fillMaxSize()
                                            .background(StatusGray)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                SeveridadLegend(label = "Alta", count = alta, color = StatusRed)
                                SeveridadLegend(label = "Media", count = media, color = StatusAmber)
                                SeveridadLegend(label = "Baja", count = baja, color = StatusGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SeveridadLegend(label: String, count: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$label ($count)",
            style = MaterialTheme.typography.labelSmall,
            color = Tinta.copy(alpha = 0.65f)
        )
    }
}
