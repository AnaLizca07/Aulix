package com.example.aulix.ui.soporte.incidencias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aulix.domain.model.PrioridadIncidencia
import com.example.aulix.domain.model.User
import com.example.aulix.ui.components.AulixButton
import com.example.aulix.ui.components.AulixDropdown
import com.example.aulix.ui.components.AulixTextField
import com.example.aulix.ui.theme.Lienzo
import com.example.aulix.ui.theme.RoleSoporte
import com.example.aulix.ui.theme.Tinta

@Composable
fun RegistrarIncidenciaScreen(
    user: User,
    onBack: () -> Unit,
    viewModel: RegistrarIncidenciaViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onBack()
    }

    Scaffold(containerColor = Lienzo) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ───────────────────────────────────────────────────────────
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
                        text = "Nueva incidencia",
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
                Spacer(modifier = Modifier.height(8.dp))

                AulixTextField(
                    value = state.titulo,
                    onValueChange = viewModel::onTituloChange,
                    label = "TÍTULO DE LA INCIDENCIA",
                    placeholder = "Ej. Pantalla no enciende"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AulixTextField(
                    value = state.descripcion,
                    onValueChange = viewModel::onDescripcionChange,
                    label = "DESCRIPCIÓN",
                    placeholder = "Describe el problema con detalle...",
                    singleLine = false,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                AulixDropdown(
                    value = state.prioridadSeleccionada.name,
                    onValueChange = { nombre ->
                        val prioridad = PrioridadIncidencia.entries.firstOrNull { it.name == nombre }
                            ?: PrioridadIncidencia.MEDIA
                        viewModel.onPrioridadChange(prioridad)
                    },
                    label = "PRIORIDAD",
                    options = PrioridadIncidencia.entries.map { it.name }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AulixDropdown(
                    value = state.equipoSeleccionado?.let { "${it.nombre} — ${it.codigo}" } ?: "",
                    onValueChange = { seleccion ->
                        val equipo = state.equiposDisponibles.firstOrNull {
                            "${it.nombre} — ${it.codigo}" == seleccion
                        }
                        if (equipo != null) viewModel.onEquipoChange(equipo)
                    },
                    label = "EQUIPO AFECTADO",
                    options = state.equiposDisponibles.map { "${it.nombre} — ${it.codigo}" }
                )

                Spacer(modifier = Modifier.height(28.dp))

                AulixButton(
                    text = "Registrar incidencia",
                    onClick = { viewModel.registrar(user.fullName) },
                    isLoading = state.isLoading,
                    enabled = state.titulo.isNotBlank() && state.descripcion.isNotBlank() && state.equipoSeleccionado != null
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
