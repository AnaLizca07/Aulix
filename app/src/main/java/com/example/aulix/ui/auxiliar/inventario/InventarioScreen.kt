package com.example.aulix.ui.auxiliar.inventario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoEquipo
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.theme.*

@Composable
fun InventarioScreen(
    onBack: () -> Unit,
    viewModel: InventarioViewModel = hiltViewModel()
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
                Column {
                    Text(
                        text = "INVENTARIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cobre,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Inventario",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Tinta
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AulixCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
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
                            .padding(vertical = 6.dp),
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
                            .padding(vertical = 6.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EQUIPOS · ${state.equipos.size} TOTAL",
                style = MaterialTheme.typography.labelSmall,
                color = Tinta.copy(alpha = 0.45f),
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.equipos, key = { it.id }) { equipo ->
                    EquipoInventarioRow(
                        equipo = equipo,
                        onDevolverEquipo = { viewModel.devolverEquipo(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipoInventarioRow(
    equipo: Equipo,
    onDevolverEquipo: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        text = equipo.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        color = Tinta
                    )
                    Text(
                        text = equipo.codigo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Tinta.copy(alpha = 0.5f)
                    )
                    if (equipo.infoAdicional.isNotBlank()) {
                        Text(
                            text = equipo.infoAdicional,
                            style = MaterialTheme.typography.bodySmall,
                            color = Tinta.copy(alpha = 0.4f)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                val (label, color, bgColor) = when (equipo.estado) {
                    EstadoEquipo.DISPONIBLE        -> Triple("DISPONIBLE",  StatusGreen, StatusGreenBg)
                    EstadoEquipo.PRESTADO          -> Triple("PRESTADO",    StatusAmber, StatusAmberBg)
                    EstadoEquipo.REPARACION        -> Triple("REPARACIÓN",  StatusRed,   StatusRedBg)
                    EstadoEquipo.FUERA_DE_SERVICIO -> Triple("FUERA SERV.", StatusRed,   StatusRedBg)
                }
                StatusChip(label = label, color = color, backgroundColor = bgColor)
            }
            if (equipo.estado == EstadoEquipo.PRESTADO) {
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 0.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDevolverEquipo(equipo.id) }) {
                        Text(
                            text = "Registrar devolución →",
                            style = MaterialTheme.typography.labelMedium,
                            color = StatusGreen
                        )
                    }
                }
            }
        }
    }
}
