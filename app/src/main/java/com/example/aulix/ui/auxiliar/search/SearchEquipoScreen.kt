package com.example.aulix.ui.auxiliar.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.aulix.domain.model.Equipo
import com.example.aulix.domain.model.EstadoEquipo
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.theme.*

@Composable
fun SearchEquipoScreen(
    onBack: () -> Unit,
    onContinuar: (String) -> Unit,
    viewModel: SearchEquipoViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val disponiblesCount = state.resultados.count { it.estado == EstadoEquipo.DISPONIBLE }

    Scaffold(
        containerColor = Lienzo,
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    AulixButton(
                        text = "→ Continuar al préstamo",
                        onClick = { state.equipoSeleccionado?.let { onContinuar(it.id) } },
                        enabled = state.equipoSeleccionado != null
                    )
                }
            }
        }
    ) { innerPadding ->
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
                        text = "HU 12 · PASO 1",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cobre,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Buscar equipo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Tinta
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Buscar por nombre o código...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tinta.copy(alpha = 0.4f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Tinta.copy(alpha = 0.5f)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Escanear QR",
                                tint = Cobalto
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderLight,
                        focusedBorderColor = Cobalto,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.filtros) { filtro ->
                        val seleccionado = state.filtroActivo == filtro
                        FilterChip(
                            selected = seleccionado,
                            onClick = { viewModel.onFiltroChange(filtro) },
                            label = {
                                Text(
                                    text = filtro,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Cobalto,
                                selectedLabelColor = Color.White,
                                containerColor = Cielo,
                                labelColor = Tinta
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = seleccionado,
                                selectedBorderColor = Color.Transparent,
                                borderColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${state.resultados.size} RESULTADOS · $disponiblesCount DISPONIBLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Tinta.copy(alpha = 0.5f),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp)
            ) {
                items(
                    items = state.resultados,
                    key = { it.id }
                ) { equipo ->
                    EquipoItemRow(
                        equipo = equipo,
                        seleccionado = state.equipoSeleccionado?.id == equipo.id,
                        onClick = { viewModel.onEquipoSeleccionado(equipo) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipoItemRow(
    equipo: Equipo,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (seleccionado) Cobalto else BorderLight
    val borderWidth = if (seleccionado) 2.dp else 1.dp

    AulixCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(16.dp))
            .then(
                if (equipo.estado == EstadoEquipo.DISPONIBLE)
                    Modifier.clickable { onClick() }
                else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
            Column(horizontalAlignment = Alignment.End) {
                val (label, color, bgColor) = when (equipo.estado) {
                    EstadoEquipo.DISPONIBLE -> Triple("DISPONIBLE", StatusGreen, StatusGreenBg)
                    EstadoEquipo.PRESTADO   -> Triple("PRESTADO",   StatusAmber, StatusAmberBg)
                    EstadoEquipo.REPARACION -> Triple("REPARACIÓN", StatusRed,   StatusRedBg)
                }
                StatusChip(label = label, color = color, backgroundColor = bgColor)
                if (seleccionado) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Cobalto,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
