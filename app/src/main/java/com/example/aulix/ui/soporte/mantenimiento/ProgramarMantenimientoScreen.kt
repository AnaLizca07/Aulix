package com.example.aulix.ui.soporte.mantenimiento

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.aulix.domain.model.TipoMantenimiento
import com.example.aulix.domain.model.User
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.components.AulixCard
import com.example.aulix.ui.components.AulixDropdown
import com.example.aulix.ui.components.AulixTextField
import com.example.aulix.ui.components.InfoBox
import com.example.aulix.ui.components.StatusChip
import com.example.aulix.ui.theme.Arena
import com.example.aulix.ui.theme.BorderLight
import com.example.aulix.ui.theme.Cielo
import com.example.aulix.ui.theme.Cobalto
import com.example.aulix.ui.theme.Lienzo
import com.example.aulix.ui.theme.RoleSoporte
import com.example.aulix.ui.theme.StatusAmber
import com.example.aulix.ui.theme.StatusAmberBg
import com.example.aulix.ui.theme.StatusGreen
import com.example.aulix.ui.theme.StatusGreenBg
import com.example.aulix.ui.theme.Tinta

private val tipoOpciones = listOf("Preventivo", "Correctivo", "Calibración")

private fun TipoMantenimiento.label() = when (this) {
    TipoMantenimiento.PREVENTIVO -> "Preventivo"
    TipoMantenimiento.CORRECTIVO -> "Correctivo"
    TipoMantenimiento.CALIBRACION -> "Calibración"
}

private fun String.toTipoMantenimiento() = when (this) {
    "Correctivo" -> TipoMantenimiento.CORRECTIVO
    "Calibración" -> TipoMantenimiento.CALIBRACION
    else -> TipoMantenimiento.PREVENTIVO
}

@Composable
fun ProgramarMantenimientoScreen(
    equipoId: String,
    user: User,
    onBack: () -> Unit,
    viewModel: ProgramarMantenimientoViewModel = hiltViewModel<ProgramarMantenimientoViewModel, ProgramarMantenimientoViewModel.Factory>(
        creationCallback = { factory -> factory.create(equipoId) }
    )
) {
    val state by viewModel.uiState.collectAsState()
    val equipo = state.equipo

    val formValido = state.fechaProgramada.isNotBlank()
        && state.horaProgramada.isNotBlank()
        && state.tecnicoAsignado.isNotBlank()

    Scaffold(containerColor = Lienzo) { innerPadding ->
        AnimatedContent(
            targetState = state.guardadoExitoso,
            transitionSpec = {
                (fadeIn() + slideInVertically { it / 4 }) togetherWith fadeOut()
            },
            label = "mantenimiento_state"
        ) { exito ->
            if (exito && state.mantenimientoRegistrado != null) {
                // ── Pantalla de confirmación ─────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ícono de éxito
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(StatusGreenBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "¡Mantenimiento programado!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Tinta,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Se ha registrado correctamente en el sistema.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Tinta.copy(alpha = 0.55f)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Resumen
                    AulixCard {
                        val mnt = state.mantenimientoRegistrado!!
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            ResumenRow(label = "Equipo", value = mnt.equipo.nombre)
                            HorizontalDivider(color = BorderLight, thickness = 1.dp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tipo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Tinta.copy(alpha = 0.5f)
                                )
                                val (chipColor, chipBg) = when (mnt.tipo) {
                                    TipoMantenimiento.PREVENTIVO -> StatusGreen to StatusGreenBg
                                    TipoMantenimiento.CORRECTIVO -> StatusAmber to StatusAmberBg
                                    TipoMantenimiento.CALIBRACION -> Cobalto to Cielo
                                }
                                StatusChip(
                                    label = mnt.tipo.label().uppercase(),
                                    color = chipColor,
                                    backgroundColor = chipBg
                                )
                            }
                            HorizontalDivider(color = BorderLight, thickness = 1.dp)
                            ResumenRow(
                                label = "Fecha y hora",
                                value = "${mnt.fechaProgramada} · ${mnt.horaProgramada}"
                            )
                            HorizontalDivider(color = BorderLight, thickness = 1.dp)
                            ResumenRow(label = "Técnico", value = mnt.tecnicoAsignado)
                            if (!mnt.observaciones.isNullOrBlank()) {
                                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                                ResumenRow(label = "Notas", value = mnt.observaciones ?: "")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AulixButton(
                        text = "Volver al historial →",
                        onClick = onBack
                    )
                }
            } else {
                // ── Formulario ───────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
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
                        Column {
                            Text(
                                text = "SOPORTE TÉCNICO",
                                style = MaterialTheme.typography.labelSmall,
                                color = RoleSoporte,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Programar mantenimiento",
                                style = MaterialTheme.typography.titleMedium,
                                color = Tinta,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))

                        // Card del equipo
                        if (equipo != null) {
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
                                            text = equipo.nombre,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Tinta,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${equipo.codigo} · ${equipo.laboratorio}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Tinta.copy(alpha = 0.5f)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Build,
                                        contentDescription = null,
                                        tint = RoleSoporte.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tipo
                        AulixDropdown(
                            value = state.tipoSeleccionado.label(),
                            onValueChange = { viewModel.onTipoChange(it.toTipoMantenimiento()) },
                            label = "TIPO DE MANTENIMIENTO",
                            options = tipoOpciones
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fecha + Hora en fila
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AulixTextField(
                                value = state.fechaProgramada,
                                onValueChange = viewModel::onFechaChange,
                                label = "FECHA PROGRAMADA",
                                placeholder = "yyyy-MM-dd",
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Tinta.copy(alpha = 0.4f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier.weight(1.6f)
                            )
                            AulixTextField(
                                value = state.horaProgramada,
                                onValueChange = viewModel::onHoraChange,
                                label = "HORA",
                                placeholder = "HH:mm",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Técnico
                        AulixTextField(
                            value = state.tecnicoAsignado,
                            onValueChange = viewModel::onTecnicoChange,
                            label = "TÉCNICO ASIGNADO",
                            placeholder = "Nombre del técnico",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Tinta.copy(alpha = 0.4f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Observaciones
                        AulixTextField(
                            value = state.observaciones,
                            onValueChange = viewModel::onObservacionesChange,
                            label = "OBSERVACIONES",
                            placeholder = "Describe el trabajo a realizar, piezas necesarias...",
                            singleLine = false,
                            minLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoBox(
                            text = "El equipo quedará registrado con mantenimiento pendiente. El técnico asignado podrá ver la orden en su bandeja."
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        AulixButton(
                            text = "Confirmar mantenimiento",
                            onClick = { viewModel.registrar(user.fullName) },
                            isLoading = state.isLoading,
                            enabled = formValido
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Tinta.copy(alpha = 0.5f),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = Tinta,
            modifier = Modifier.weight(0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
