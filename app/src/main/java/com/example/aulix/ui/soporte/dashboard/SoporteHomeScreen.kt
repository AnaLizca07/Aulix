package com.example.aulix.ui.soporte.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import com.example.aulix.domain.model.Incidencia
import com.example.aulix.domain.model.PrioridadIncidencia
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.Arena
import com.example.aulix.ui.theme.Cielo
import com.example.aulix.ui.theme.Cobalto
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
fun SoporteHomeScreen(
    user: User,
    onVerDetalle: (String) -> Unit,
    onNuevaIncidencia: () -> Unit,
    onVerEquipos: () -> Unit,
    onVerMetricas: () -> Unit,
    onVerPerfil: () -> Unit,
    viewModel: SoporteHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val ultimaAbierta = state.incidencias.filter { it.estado == EstadoIncidencia.ABIERTA }
        .maxByOrNull { it.fecha + it.horaReporte }

    val abiertasEnLista = state.incidencias.count { it.estado == EstadoIncidencia.ABIERTA }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
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
                    selected = false,
                    onClick = onVerEquipos,
                    icon = { Icon(Icons.Default.Build, contentDescription = "Equipos") },
                    label = { Text("Equipos", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Tinta.copy(alpha = 0.4f),
                        unselectedTextColor = Tinta.copy(alpha = 0.4f),
                        indicatorColor = Cielo
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onVerMetricas,
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Métricas") },
                    label = { Text("Métricas", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Tinta.copy(alpha = 0.4f),
                        unselectedTextColor = Tinta.copy(alpha = 0.4f),
                        indicatorColor = Cielo
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onVerPerfil,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
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
            // ── Header ──────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Lienzo)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(initials = user.initials, role = UserRole.SOPORTE_TECNICO)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SOPORTE TÉCNICO",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoleSoporte,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Incidencias",
                        style = MaterialTheme.typography.titleLarge,
                        color = Tinta
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Tinta.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // ── Saludo ───────────────────────────────────────────────────────
                Text(
                    text = "Hola, ${user.fullName.split(" ").first()} —",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Tinta.copy(alpha = 0.55f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${state.abiertas + state.enAtencion} incidencias en tu bandeja.",
                    style = MaterialTheme.typography.displayLarge,
                    color = Tinta,
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Banner última incidencia ABIERTA ─────────────────────────────
                if (ultimaAbierta != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(StatusRedBg)
                            .clickable { onVerDetalle(ultimaAbierta.id) }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = StatusRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ultimaAbierta.equipo.nombre,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = StatusRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = ultimaAbierta.titulo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tinta.copy(alpha = 0.7f),
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "→",
                                style = MaterialTheme.typography.titleMedium,
                                color = StatusRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ── KPIs ──────────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.abiertas.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = StatusRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Abiertas",
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
                                text = state.enAtencion.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = StatusAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "En atención",
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
                                text = state.resueltasHoy.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = StatusGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Resueltas hoy",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Tabs ──────────────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabChip(
                        label = "Todas ${state.totalCount}",
                        seleccionado = state.tabSeleccionado == 0,
                        onClick = { viewModel.cambiarTab(0) }
                    )
                    TabChip(
                        label = "Mis asignadas ${state.misAsignadasCount}",
                        seleccionado = state.tabSeleccionado == 1,
                        onClick = { viewModel.cambiarTab(1) }
                    )
                    TabChip(
                        label = "Sin asignar ${state.sinAsignarCount}",
                        seleccionado = state.tabSeleccionado == 2,
                        onClick = { viewModel.cambiarTab(2) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Encabezado de lista ──────────────────────────────────────────
                Text(
                    text = "COLA DE TRABAJO · $abiertasEnLista ABIERTAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.45f),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Lista de incidencias ─────────────────────────────────────────
                state.incidencias.forEach { incidencia ->
                    IncidenciaItemCard(
                        incidencia = incidencia,
                        onClick = { onVerDetalle(incidencia.id) }
                    )
                    HorizontalDivider(
                        color = Tinta.copy(alpha = 0.07f),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TabChip(label: String, seleccionado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (seleccionado) Cobalto else Color.White)
            .border(
                width = 1.dp,
                color = if (seleccionado) Cobalto else Tinta.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (seleccionado) Color.White else Tinta.copy(alpha = 0.7f),
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun IncidenciaItemCard(incidencia: Incidencia, onClick: () -> Unit) {
    val (prioridadLabel, prioridadColor, prioridadBg) = when (incidencia.prioridad) {
        PrioridadIncidencia.ALTA -> Triple("ALTA", StatusRed, StatusRedBg)
        PrioridadIncidencia.MEDIA -> Triple("MEDIA", StatusAmber, StatusAmberBg)
        PrioridadIncidencia.BAJA -> Triple("BAJA", StatusGray, StatusGrayBg)
    }
    val (estadoLabel, estadoColor, estadoBg) = when (incidencia.estado) {
        EstadoIncidencia.ABIERTA -> Triple("ABIERTA", StatusRed, StatusRedBg)
        EstadoIncidencia.EN_ATENCION -> Triple("EN ATENCIÓN", StatusAmber, StatusAmberBg)
        EstadoIncidencia.RESUELTA -> Triple("RESUELTA", StatusGreen, StatusGreenBg)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.Top
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "#${incidencia.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.4f)
                )
                StatusChip(label = prioridadLabel, color = prioridadColor, backgroundColor = prioridadBg)
                StatusChip(label = estadoLabel, color = estadoColor, backgroundColor = estadoBg)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = tiempoDesdeReporte(incidencia.horaReporte, incidencia.fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.4f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
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
            Text(
                text = if (incidencia.asignadoA != null) "→ ${incidencia.asignadoA}" else "→ Sin asignar",
                style = MaterialTheme.typography.bodySmall,
                color = if (incidencia.asignadoA != null) Cobalto.copy(alpha = 0.8f) else StatusGray
            )
        }
    }
}

private fun tiempoDesdeReporte(horaReporte: String, fecha: String): String {
    return try {
        val cal = java.util.Calendar.getInstance()
        val hoy = "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
        if (fecha != hoy) {
            val partes = fecha.split("-")
            val fechaCal = java.util.Calendar.getInstance()
            fechaCal.set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt(), 0, 0, 0)
            val diffMs = cal.timeInMillis - fechaCal.timeInMillis
            val dias = (diffMs / (1000L * 60 * 60 * 24)).toInt()
            return if (dias == 1) "hace 1 día" else "hace $dias días"
        }
        val partesHora = horaReporte.split(":")
        val horaR = partesHora[0].toInt()
        val minR = partesHora[1].toInt()
        val horaA = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minA = cal.get(java.util.Calendar.MINUTE)
        val diffMin = (horaA * 60 + minA) - (horaR * 60 + minR)
        when {
            diffMin <= 0 -> "reciente"
            diffMin < 60 -> "hace $diffMin min"
            else -> "hace ${diffMin / 60} h"
        }
    } catch (e: Exception) {
        horaReporte
    }
}
