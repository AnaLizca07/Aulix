package com.example.aulix.ui.auxiliar.prestamo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavHostController
import com.example.aulix.domain.model.Destinatario
import com.example.aulix.domain.model.User
import com.example.aulix.domain.model.UserRole
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.components.UserAvatar
import com.example.aulix.ui.theme.*

@Composable
fun RegistrarPrestamoScreen(
    equipoId: String,
    user: User,
    navController: NavHostController,
    onBack: () -> Unit,
    onCambiarDestinatario: () -> Unit,
    onConfirmado: () -> Unit,
    viewModel: RegistrarPrestamoViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(equipoId) {
        viewModel.setEquipo(equipoId)
    }

    LaunchedEffect(state.confirmado) {
        if (state.confirmado) onConfirmado()
    }

    LaunchedEffect(Unit) {
        savedStateHandle?.getStateFlow<String?>("destinatario_nombre", null)
            ?.collect { nombre ->
                if (nombre != null) {
                    val id = savedStateHandle.get<String>("destinatario_id") ?: ""
                    val programa = savedStateHandle.get<String>("destinatario_programa") ?: ""
                    viewModel.cambiarDestinatario(Destinatario(nombre, id, programa))
                    savedStateHandle.remove<String>("destinatario_nombre")
                }
            }
    }

    Scaffold(containerColor = Lienzo) { innerPadding ->
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
                        text = "HU 12 · PASO 2",
                        style = MaterialTheme.typography.labelSmall,
                        color = Cobre,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Registrar préstamo",
                        style = MaterialTheme.typography.headlineMedium,
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
                state.equipo?.let { equipo ->
                    AulixCard {
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
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Tinta
                                )
                                Text(
                                    text = equipo.codigo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tinta.copy(alpha = 0.5f)
                                )
                            }
                            StatusChip(
                                label = "DISPONIBLE",
                                color = StatusGreen,
                                backgroundColor = StatusGreenBg
                            )
                        }
                    }
                }

                SectionLabel("DESTINATARIO")
                AulixCard(
                    modifier = Modifier.border(1.dp, Cobalto, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            initials = state.destinatarioNombre
                                .split(",", " ")
                                .filter { it.isNotBlank() }
                                .take(2)
                                .joinToString("") { it.first().uppercase() },
                            role = UserRole.ESTUDIANTE
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.destinatarioNombre,
                                style = MaterialTheme.typography.titleMedium,
                                color = Tinta
                            )
                            Text(
                                text = state.destinatarioPrograma.ifBlank { "Sin programa" },
                                style = MaterialTheme.typography.bodySmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                        TextButton(onClick = { onCambiarDestinatario() }) {
                            Text(
                                text = "Cambiar",
                                style = MaterialTheme.typography.labelMedium,
                                color = Cobalto
                            )
                        }
                    }
                }

                SectionLabel("FECHA · HORA / DEVOLUCIÓN")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = state.horaInicio,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Tinta
                            )
                            Text(
                                text = obtenerFechaFormateada(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                    AulixCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = state.horaDevolucion,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Tinta
                            )
                            Text(
                                text = "${state.duracionSeleccionada}h de préstamo",
                                style = MaterialTheme.typography.labelSmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                val duraciones = listOf(1 to "1h", 2 to "2h", 4 to "4h", 0 to "Cierre del día")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    duraciones.forEach { (horas, label) ->
                        val seleccionado = state.duracionSeleccionada == horas
                        FilterChip(
                            selected = seleccionado,
                            onClick = { viewModel.onDuracionChange(horas) },
                            label = {
                                Text(
                                    text = label,
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

                SectionLabel("RESPONSABLE DEL PRÉSTAMO")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Arena),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            initials = user.initials,
                            role = UserRole.AUXILIAR
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${user.fullName} (tú)",
                                style = MaterialTheme.typography.titleMedium,
                                color = Tinta
                            )
                            Text(
                                text = "Auxiliar · Lab-B-204",
                                style = MaterialTheme.typography.bodySmall,
                                color = Tinta.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                SectionLabel("ESTADO AL ENTREGAR")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.onEstadoChange(true) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (state.sinNovedad) Cielo else Color.White,
                            contentColor = if (state.sinNovedad) Cobalto else Tinta.copy(alpha = 0.6f)
                        ),
                        border = BorderStroke(
                            width = if (state.sinNovedad) 2.dp else 1.dp,
                            color = if (state.sinNovedad) Cobalto else BorderLight
                        )
                    ) {
                        Text(
                            text = "Sin novedad",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    OutlinedButton(
                        onClick = { viewModel.onEstadoChange(false) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (!state.sinNovedad) Cielo else Color.White,
                            contentColor = if (!state.sinNovedad) Cobalto else Tinta.copy(alpha = 0.6f)
                        ),
                        border = BorderStroke(
                            width = if (!state.sinNovedad) 2.dp else 1.dp,
                            color = if (!state.sinNovedad) Cobalto else BorderLight
                        )
                    ) {
                        Text(
                            text = "Con observaciones",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                if (!state.sinNovedad) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.observaciones,
                        onValueChange = { viewModel.onObservacionesChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción de la observación") },
                        placeholder = { Text("Ej: pantalla rayada, cable dañado…", style = MaterialTheme.typography.bodySmall) },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = BorderLight,
                            focusedBorderColor = Cobalto,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (state.error != null) {
                    Text(
                        text = state.error ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (state.destinatarioId.isBlank()) {
                    Text(
                        text = "Selecciona un destinatario antes de confirmar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Cobre,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                AulixButton(
                    text = if (state.isLoading) "Registrando..." else "✓ Confirmar préstamo",
                    onClick = { viewModel.confirmarPrestamo(user.fullName) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Tinta.copy(alpha = 0.45f),
        letterSpacing = 1.sp
    )
}

private fun obtenerFechaFormateada(): String {
    val cal = java.util.Calendar.getInstance()
    val meses = listOf("ENE","FEB","MAR","ABR","MAY","JUN",
                       "JUL","AGO","SEP","OCT","NOV","DIC")
    val dia = cal.get(java.util.Calendar.DAY_OF_MONTH)
    val mes = meses[cal.get(java.util.Calendar.MONTH)]
    val anio = cal.get(java.util.Calendar.YEAR)
    return "$dia $mes $anio"
}
