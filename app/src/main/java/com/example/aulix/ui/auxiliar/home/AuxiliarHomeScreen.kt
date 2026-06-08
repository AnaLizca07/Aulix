package com.example.aulix.ui.auxiliar.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aulix.domain.model.EstadoPrestamo
import com.example.aulix.domain.model.Prestamo
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.*

@Composable
fun AuxiliarHomeScreen(
    user: User,
    onNuevoPrestamo: () -> Unit,
    onVerHistorial: () -> Unit,
    onVerInventario: () -> Unit,
    onVerPerfil: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AuxiliarHomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", style = MaterialTheme.typography.labelSmall) },
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
                    onClick = onVerInventario,
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventario") },
                    label = { Text("Inventario", style = MaterialTheme.typography.labelSmall) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Tinta.copy(alpha = 0.4f),
                        unselectedTextColor = Tinta.copy(alpha = 0.4f),
                        indicatorColor = Cielo
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onVerHistorial,
                    icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
                    label = { Text("Historial", style = MaterialTheme.typography.labelSmall) },
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Lienzo)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(initials = user.initials, role = UserRole.AUXILIAR)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AUXILIAR · LAB-B-204",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoleAuxiliar,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Préstamos",
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
                Text(
                    text = "Hola, ${user.fullName.split(" ").first()} —",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Tinta.copy(alpha = 0.55f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${state.prestados} equipos prestados ahora.",
                    style = MaterialTheme.typography.displayLarge,
                    color = Tinta
                )

                Spacer(modifier = Modifier.height(20.dp))

                AulixButton(
                    text = "Nuevo préstamo",
                    onClick = onNuevoPrestamo,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onNuevoPrestamo() }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Tinta.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Buscar por nombre o código...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tinta.copy(alpha = 0.4f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.disponibles.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = StatusGreen
                            )
                            Text(
                                text = "Disponibles",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.prestados.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = StatusAmber
                            )
                            Text(
                                text = "Prestados",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.enReparacion.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = StatusRed
                            )
                            Text(
                                text = "Reparación",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HISTORIAL RECIENTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Tinta.copy(alpha = 0.45f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onVerHistorial) {
                        Text(
                            text = "Ver todo →",
                            style = MaterialTheme.typography.labelMedium,
                            color = Cobalto
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                state.prestamosRecientes.take(5).forEach { prestamo ->
                    PrestamoItemRow(prestamo = prestamo)
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
private fun PrestamoItemRow(prestamo: Prestamo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
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
                text = prestamo.equipo.nombre,
                style = MaterialTheme.typography.titleSmall,
                color = Tinta
            )
            Text(
                text = "${prestamo.equipo.codigo} · ${prestamo.destinatarioNombre}",
                style = MaterialTheme.typography.bodySmall,
                color = Tinta.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            val (label, color, bgColor) = when (prestamo.estado) {
                EstadoPrestamo.ACTIVO   -> Triple("ACTIVO",   Cobalto,     Cielo)
                EstadoPrestamo.DEVUELTO -> Triple("DEVUELTO", StatusGreen, StatusGreenBg)
                EstadoPrestamo.VENCIDO  -> Triple("VENCIDO",  StatusRed,   StatusRedBg)
            }
            StatusChip(label = label, color = color, backgroundColor = bgColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = prestamo.horaInicio,
                style = MaterialTheme.typography.labelSmall,
                color = Tinta.copy(alpha = 0.45f)
            )
        }
    }
}
